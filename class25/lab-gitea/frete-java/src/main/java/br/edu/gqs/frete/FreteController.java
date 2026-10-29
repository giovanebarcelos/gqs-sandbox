package br.edu.gqs.frete;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de frete da LojaOnline.
 *
 * <pre>
 *   GET  /api/health    -> {"status": "ok", "versao": "..."}
 *   GET  /api/regioes   -> regioes atendidas, com frete base e prazo
 *   POST /api/frete     -> {"regiao": "SUL", "pesoKg": 2.0, "valorPedido": 100.00}
 * </pre>
 *
 * Entrada invalida (regiao, peso, valor ou JSON) responde 400 com {"erro": "..."}.
 */
@RestController
public class FreteController {

    public record Health(String status, String versao) {
    }

    public record PedidoFrete(String regiao, Double pesoKg, BigDecimal valorPedido) {
    }

    private final CalculadoraFrete calculadora;
    private final String versao;

    public FreteController(CalculadoraFrete calculadora, @Value("${app.versao}") String versao) {
        this.calculadora = calculadora;
        this.versao = versao;
    }

    @GetMapping("/api/health")
    public Health health() {
        return new Health("ok", versao);
    }

    @GetMapping("/api/regioes")
    public List<Map<String, Object>> regioes() {
        return Arrays.stream(Regiao.values())
                .map(r -> Map.<String, Object>of(
                        "regiao", r.name(), "freteBase", r.getFreteBase(), "prazoDias", r.getPrazoDias()))
                .toList();
    }

    @PostMapping("/api/frete")
    public CotacaoFrete calcular(@RequestBody PedidoFrete pedido) {
        double peso = pedido.pesoKg() == null ? 0 : pedido.pesoKg();
        return calculadora.calcular(pedido.regiao(), peso, pedido.valorPedido());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> entradaInvalida(IllegalArgumentException erro) {
        return erro(erro.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException erro) {
        return erro("Envie um JSON com regiao, pesoKg e valorPedido");
    }

    private static ResponseEntity<Map<String, String>> erro(String mensagem) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", mensagem));
    }
}
