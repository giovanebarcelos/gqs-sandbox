package br.edu.gqs.techstore;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST da TechStore. Traduz HTTP para as regras de CalculadoraCarrinho.
 *
 * <pre>
 *   GET  /api/health                 -> {"status": "ok", "versao": "..."}
 *   GET  /api/produtos               -> lista do catalogo
 *   GET  /api/produtos/{id}          -> um produto (404 se nao existir)
 *   POST /api/carrinho/calcular      -> subtotal, desconto, frete e total
 * </pre>
 *
 * Codigos de erro: 400 entrada invalida (quantidade fora de 1..10, cupom
 * invalido, JSON ruim); 404 produto inexistente.
 */
@RestController
public class CarrinhoController {

    /** Resposta do health check: {"status": "ok", "versao": "1.0.0"}. */
    public record Health(String status, String versao) {
    }

    /** Corpo do POST: {"itens": [...], "cupom": "GQS10"}. */
    public record PedidoCarrinho(List<ItemCarrinho> itens, String cupom) {
    }

    private final CalculadoraCarrinho calculadora;
    private final String versao;

    public CarrinhoController(CalculadoraCarrinho calculadora, @Value("${app.versao}") String versao) {
        this.calculadora = calculadora;
        this.versao = versao;
    }

    @GetMapping("/")
    public Map<String, Object> raiz() {
        return Map.of(
                "aplicacao", "TechStore - API de Carrinho (Java)",
                "versao", versao,
                "endpoints", List.of("GET /api/health", "GET /api/produtos",
                        "GET /api/produtos/{id}", "POST /api/carrinho/calcular"));
    }

    @GetMapping("/api/health")
    public Health health() {
        return new Health("ok", versao);
    }

    @GetMapping("/api/produtos")
    public List<Produto> listarProdutos() {
        return calculadora.listarProdutos();
    }

    @GetMapping("/api/produtos/{id}")
    public Produto obterProduto(@PathVariable int id) {
        return calculadora.buscarProduto(id);
    }

    @PostMapping("/api/carrinho/calcular")
    public ResumoCarrinho calcular(@RequestBody PedidoCarrinho pedido) {
        return calculadora.calcularCarrinho(pedido.itens(), pedido.cupom());
    }

    @ExceptionHandler(CarrinhoInvalidoException.class)
    public ResponseEntity<Map<String, String>> carrinhoInvalido(CarrinhoInvalidoException erro) {
        return erro(HttpStatus.BAD_REQUEST, erro.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException erro) {
        return erro(HttpStatus.BAD_REQUEST, "Envie um JSON com a lista 'itens'");
    }

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> produtoNaoEncontrado(ProdutoNaoEncontradoException erro) {
        return erro(HttpStatus.NOT_FOUND, erro.getMessage());
    }

    private static ResponseEntity<Map<String, String>> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}
