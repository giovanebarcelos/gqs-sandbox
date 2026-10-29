package br.edu.gqs.boletim;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST do boletim.
 *
 * <pre>
 *   GET  /api/health                 -> {"status": "ok", "versao": "...", "instituicao": "..."}
 *   POST /api/boletim                -> {"aluno": "Ana", "notas": [7.5, 8.0], "frequencia": 80}
 *   GET  /api/admin/estatisticas     -> exige o cabecalho X-Admin-Token (secret TOKEN_ADMIN)
 * </pre>
 */
@RestController
public class BoletimController {

    public record Health(String status, String versao, String instituicao) {
    }

    public record PedidoBoletim(String aluno, List<Double> notas, Double frequencia) {
    }

    private final CalculadoraBoletim calculadora;
    private final String versao;
    private final String instituicao;
    private final String tokenAdmin;
    private final AtomicLong calculados = new AtomicLong();

    public BoletimController(CalculadoraBoletim calculadora,
                             @Value("${app.versao}") String versao,
                             @Value("${app.instituicao}") String instituicao,
                             @Value("${app.token-admin}") String tokenAdmin) {
        this.calculadora = calculadora;
        this.versao = versao;
        this.instituicao = instituicao;
        this.tokenAdmin = tokenAdmin;
    }

    @GetMapping("/api/health")
    public Health health() {
        return new Health("ok", versao, instituicao);
    }

    @PostMapping("/api/boletim")
    public Boletim calcular(@RequestBody PedidoBoletim pedido) {
        double frequencia = pedido.frequencia() == null ? -1 : pedido.frequencia();
        Boletim boletim = calculadora.calcular(pedido.aluno(), pedido.notas(), frequencia);
        calculados.incrementAndGet();
        return boletim;
    }

    /** Sem TOKEN_ADMIN configurado, ninguem entra: token vazio nunca e' valido. */
    @GetMapping("/api/admin/estatisticas")
    public ResponseEntity<Map<String, Object>> estatisticas(
            @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        if (tokenAdmin.isBlank() || !tokenAdmin.equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Token invalido"));
        }
        return ResponseEntity.ok(Map.of("boletinsCalculados", calculados.get(), "versao", versao));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> entradaInvalida(IllegalArgumentException erro) {
        return ResponseEntity.badRequest().body(Map.of("erro", erro.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException erro) {
        return ResponseEntity.badRequest().body(Map.of("erro", "Envie um JSON com aluno, notas e frequencia"));
    }
}
