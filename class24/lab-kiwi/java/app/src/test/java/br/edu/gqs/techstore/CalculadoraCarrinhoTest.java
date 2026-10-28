package br.edu.gqs.techstore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testes unitarios das regras de carrinho (JUnit 5) - Aula 24.
 *
 * <p>Convencao que liga o codigo ao Kiwi TCMS: o nome de cada metodo comeca
 * com o identificador do caso de teste cadastrado no Kiwi (ct01, ct02, ...).
 * O publicador (java/kiwi) le o junit.xml gerado pelo Surefire, extrai esse
 * identificador e grava o resultado na TestExecution do caso de teste cujo
 * Summary comeca com o mesmo prefixo ("CT03 - ..."). Ou seja: o caso escrito
 * a mao no Kiwi passa a ser executado por este codigo.
 *
 * <pre>
 *   mvn test       # relatorios em target/surefire-reports/TEST-*.xml
 * </pre>
 *
 * Na versao 1.0.0 da aplicacao, CT03 e CT05 FALHAM (defeitos plantados).
 */
class CalculadoraCarrinhoTest {

    private final CalculadoraCarrinho calculadora = new CalculadoraCarrinho();

    @Test
    @DisplayName("CT01 - RN01: 2 x Mouse (120,00) + 1 x Cabo HDMI (25,00) = 265,00")
    void ct01_subtotalDeMultiplosItens() {
        List<ItemCarrinho> itens = List.of(new ItemCarrinho(1, 2), new ItemCarrinho(4, 1));
        assertEquals(new BigDecimal("265.00"), calculadora.calcularSubtotal(itens));
    }

    @Test
    @DisplayName("CT02 - RN02: Teclado (250,00) com GQS10 -> desconto 25,00, frete gratis, total 225,00")
    void ct02_cupomGqs10Aplica10PorCento() {
        ResumoCarrinho resumo = calculadora.calcularCarrinho(List.of(new ItemCarrinho(2, 1)), "GQS10");
        assertEquals(new BigDecimal("25.00"), resumo.desconto());
        assertEquals(new BigDecimal("0.00"), resumo.frete());
        assertEquals(new BigDecimal("225.00"), resumo.total());
    }

    @Test
    @DisplayName("CT03 - RN03: valor limite - exatamente R$ 200,00 ja tem frete gratis")
    void ct03_freteGratisNoLimiteExatoDe200() {
        assertEquals(new BigDecimal("0.00"), calculadora.calcularFrete(new BigDecimal("200.00")));
    }

    @Test
    @DisplayName("CT04 - RN03: valor limite - R$ 199,99 ainda paga R$ 15,00 de frete")
    void ct04_freteCobradoAbaixoDe200() {
        assertEquals(new BigDecimal("15.00"), calculadora.calcularFrete(new BigDecimal("199.99")));
    }

    @ParameterizedTest(name = "quantidade {0}")
    @ValueSource(ints = {0, -1})
    @DisplayName("CT05 - RN04: limite inferior - 0 e -1 devem ser recusados")
    void ct05_rejeitaQuantidadeMenorQue1(int quantidade) {
        assertThrows(CarrinhoInvalidoException.class, () -> calculadora.validarQuantidade(quantidade));
    }

    @ParameterizedTest(name = "quantidade {0}")
    @ValueSource(ints = {11, 50})
    @DisplayName("CT06 - RN04: limite superior - 11 em diante deve ser recusado")
    void ct06_rejeitaQuantidadeMaiorQue10(int quantidade) {
        assertThrows(CarrinhoInvalidoException.class, () -> calculadora.validarQuantidade(quantidade));
    }

    @ParameterizedTest(name = "quantidade {0}")
    @ValueSource(ints = {1, 10})
    @DisplayName("CT06 - RN04: 1 e 10 sao validos")
    void ct06_aceitaQuantidadesNosLimites(int quantidade) {
        assertDoesNotThrow(() -> calculadora.validarQuantidade(quantidade));
    }

    @Test
    @DisplayName("CT07 - RN02: cupom inexistente e' recusado")
    void ct07_rejeitaCupomInvalido() {
        List<ItemCarrinho> itens = List.of(new ItemCarrinho(1, 1));
        assertThrows(CarrinhoInvalidoException.class, () -> calculadora.calcularCarrinho(itens, "NATAL50"));
    }

    @Test
    @DisplayName("CT08 - RN05: produto 999 nao existe")
    void ct08_rejeitaProdutoInexistente() {
        List<ItemCarrinho> itens = List.of(new ItemCarrinho(999, 1));
        assertThrows(ProdutoNaoEncontradoException.class, () -> calculadora.calcularSubtotal(itens));
    }
}
