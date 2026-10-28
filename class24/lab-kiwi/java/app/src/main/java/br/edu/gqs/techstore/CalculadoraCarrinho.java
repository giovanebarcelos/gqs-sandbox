package br.edu.gqs.techstore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Regras de negocio do carrinho da TechStore (Aula 24 - laboratorio Kiwi TCMS).
 *
 * <p>Esta classe NAO depende do Spring: e' Java puro, para poder ser testada
 * por testes unitarios (JUnit 5) sem subir servidor nenhum. O
 * {@link CarrinhoController} apenas traduz HTTP para chamadas destes metodos.
 *
 * <p>Regras de negocio (sao elas que viram casos de teste no Kiwi TCMS):
 * <pre>
 *   RN01  subtotal = soma de (preco unitario x quantidade) de cada item
 *   RN02  o cupom "GQS10" da 10% de desconto sobre o subtotal;
 *         qualquer outro cupom e' recusado
 *   RN03  frete fixo de R$ 15,00; GRATIS quando o valor com desconto
 *         for MAIOR OU IGUAL a R$ 200,00
 *   RN04  a quantidade de cada item deve estar entre 1 e 10 (inclusive)
 *   RN05  produto inexistente e' recusado
 * </pre>
 *
 * <p>ATENCAO, ALUNO: esta versao (1.0.0) contem DOIS defeitos plantados de
 * proposito. Nao corrija antes de executar a Etapa 7 do tutorial - a graca
 * e' ver o Kiwi TCMS registrar as falhas e o defeito.
 */
public class CalculadoraCarrinho {

    public static final BigDecimal VALOR_FRETE = new BigDecimal("15.00");
    public static final BigDecimal LIMITE_FRETE_GRATIS = new BigDecimal("200.00");
    public static final int QUANTIDADE_MINIMA = 1;
    public static final int QUANTIDADE_MAXIMA = 10;

    private static final Map<Integer, Produto> PRODUTOS = new LinkedHashMap<>();
    private static final Map<String, BigDecimal> CUPONS = Map.of("GQS10", new BigDecimal("0.10"));

    static {
        PRODUTOS.put(1, new Produto(1, "Mouse Gamer", new BigDecimal("120.00")));
        PRODUTOS.put(2, new Produto(2, "Teclado Mecanico", new BigDecimal("250.00")));
        PRODUTOS.put(3, new Produto(3, "Headset", new BigDecimal("80.00")));
        PRODUTOS.put(4, new Produto(4, "Cabo HDMI", new BigDecimal("25.00")));
    }

    public List<Produto> listarProdutos() {
        return List.copyOf(PRODUTOS.values());
    }

    /** RN05 - devolve o produto ou lanca ProdutoNaoEncontradoException. */
    public Produto buscarProduto(int produtoId) {
        Produto produto = PRODUTOS.get(produtoId);
        if (produto == null) {
            throw new ProdutoNaoEncontradoException(produtoId);
        }
        return produto;
    }

    /** RN04 - quantidade inteira entre 1 e 10. */
    public void validarQuantidade(Integer quantidade) {
        if (quantidade == null) {
            throw new CarrinhoInvalidoException("Quantidade deve ser um numero inteiro");
        }
        // DEFEITO PLANTADO #2 (BUG-2): o limite inferior deveria ser
        // QUANTIDADE_MINIMA (1). Com "< 0", a quantidade 0 passa na validacao.
        if (quantidade < 0 || quantidade > QUANTIDADE_MAXIMA) {
            throw new CarrinhoInvalidoException(
                    "Quantidade deve estar entre " + QUANTIDADE_MINIMA + " e " + QUANTIDADE_MAXIMA);
        }
    }

    /** RN01 - soma de preco x quantidade. */
    public BigDecimal calcularSubtotal(List<ItemCarrinho> itens) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemCarrinho item : itens) {
            validarQuantidade(item.quantidade());
            if (item.produtoId() == null) {
                throw new CarrinhoInvalidoException("Informe o produto_id de cada item");
            }
            Produto produto = buscarProduto(item.produtoId());
            subtotal = subtotal.add(produto.preco().multiply(BigDecimal.valueOf(item.quantidade())));
        }
        return centavos(subtotal);
    }

    /** RN02 - desconto do cupom; sem cupom, desconto zero. */
    public BigDecimal calcularDesconto(BigDecimal subtotal, String cupom) {
        if (cupom == null || cupom.isBlank()) {
            return centavos(BigDecimal.ZERO);
        }
        BigDecimal percentual = CUPONS.get(cupom.strip().toUpperCase());
        if (percentual == null) {
            throw new CarrinhoInvalidoException("Cupom '" + cupom + "' invalido");
        }
        return centavos(subtotal.multiply(percentual));
    }

    /** RN03 - frete gratis a partir de R$ 200,00 (inclusive). */
    public BigDecimal calcularFrete(BigDecimal valorComDesconto) {
        // DEFEITO PLANTADO #1 (BUG-1): a regra diz "maior OU IGUAL".
        // Com "> 0", um carrinho de exatamente R$ 200,00 paga frete.
        if (valorComDesconto.compareTo(LIMITE_FRETE_GRATIS) > 0) {
            return centavos(BigDecimal.ZERO);
        }
        return VALOR_FRETE;
    }

    /** Junta RN01..RN05 e devolve o resumo do pedido. */
    public ResumoCarrinho calcularCarrinho(List<ItemCarrinho> itens, String cupom) {
        if (itens == null || itens.isEmpty()) {
            throw new CarrinhoInvalidoException("O carrinho deve ter ao menos um item");
        }
        BigDecimal subtotal = calcularSubtotal(itens);
        BigDecimal desconto = calcularDesconto(subtotal, cupom);
        BigDecimal comDesconto = subtotal.subtract(desconto);
        BigDecimal frete = calcularFrete(comDesconto);
        return new ResumoCarrinho(subtotal, desconto, frete, centavos(comDesconto.add(frete)));
    }

    private static BigDecimal centavos(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
