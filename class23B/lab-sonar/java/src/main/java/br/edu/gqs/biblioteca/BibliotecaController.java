package br.edu.gqs.biblioteca;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST da Biblioteca.
 *
 * <pre>
 *   GET  /api/health                         -> {"status": "ok", "versao": "..."}
 *   GET  /api/livros                         -> {"disponiveis": [...], "emprestados": [...]}
 *   POST /api/emprestimos  {titulo, matricula} -> 201 {"codigo": "EMP-...", "titulo": "..."} | 404
 *   POST /api/devolucoes   {titulo}           -> 200 | 404
 *   GET  /api/multa?dias=&tipo=&raro=        -> {"multa": 0.0}
 *   GET  /api/relatorio?formato=texto|csv    -> texto puro; exige o cabecalho X-Admin-Token
 * </pre>
 */
@RestController
public class BibliotecaController {

    public record Health(String status, String versao) {
    }

    public record PedidoEmprestimo(String titulo, String matricula) {
    }

    public record Emprestimo(String codigo, String titulo) {
    }

    public record PedidoDevolucao(String titulo) {
    }

    public record Multa(double multa) {
    }

    private final Biblioteca biblioteca;
    private final String versao;

    public BibliotecaController(Biblioteca biblioteca, @Value("${app.versao}") String versao) {
        this.biblioteca = biblioteca;
        this.versao = versao;
    }

    @GetMapping("/api/health")
    public Health health() {
        return new Health("ok", versao);
    }

    @GetMapping("/api/livros")
    public Map<String, List<String>> livros() {
        return Map.of("disponiveis", biblioteca.listarDisponiveis(),
                "emprestados", biblioteca.listarEmprestados());
    }

    @PostMapping("/api/emprestimos")
    @ResponseStatus(HttpStatus.CREATED)
    public Emprestimo emprestar(@RequestBody PedidoEmprestimo pedido) {
        String codigo = biblioteca.emprestar(pedido.titulo(), pedido.matricula());
        return new Emprestimo(codigo, pedido.titulo());
    }

    @PostMapping("/api/devolucoes")
    public Map<String, String> devolver(@RequestBody PedidoDevolucao pedido) {
        biblioteca.devolver(pedido.titulo());
        return Map.of("mensagem", "Devolucao registrada: " + pedido.titulo());
    }

    @GetMapping("/api/multa")
    public Multa multa(@RequestParam String dias, @RequestParam String tipo,
                       @RequestParam(defaultValue = "false") boolean raro) {
        return new Multa(biblioteca.calcularMulta(biblioteca.lerDias(dias), tipo, raro));
    }

    @GetMapping("/api/relatorio")
    public ResponseEntity<String> relatorio(@RequestParam(defaultValue = "texto") String formato,
                                            @RequestHeader(name = "X-Admin-Token", required = false) String token) {
        if (!biblioteca.tokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalido");
        }
        String corpo = "csv".equals(formato) ? biblioteca.relatorioCsv() : biblioteca.relatorioTexto();
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(corpo);
    }

    @ExceptionHandler(LivroNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(LivroNaoEncontradoException erro) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", erro.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> invalido(IllegalArgumentException erro) {
        return ResponseEntity.badRequest().body(Map.of("erro", erro.getMessage()));
    }
}
