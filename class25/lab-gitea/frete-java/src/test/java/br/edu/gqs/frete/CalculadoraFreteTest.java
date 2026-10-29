package br.edu.gqs.frete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/** Testes unitarios das regras R1 a R4 (etapa "testes" do pipeline). */
class CalculadoraFreteTest {

    private final CalculadoraFrete calculadora = new CalculadoraFrete();

    private static BigDecimal reais(String valor) {
        return new BigDecimal(valor);
    }

    @Test
    void freteSulComDoisKgSomaUmKgExtra() {
        CotacaoFrete cotacao = calculadora.calcular("SUL", 2.0, reais("100.00"));
        assertEquals(reais("17.50"), cotacao.frete());
        assertFalse(cotacao.gratis());
        assertEquals(3, cotacao.prazoDias());
    }

    @Test
    void ateUmKgCobraSoOFreteBase() {
        assertEquals(reais("35.00"), calculadora.calcular("NORTE", 1.0, reais("50.00")).frete());
    }

    @Test
    void fracaoDeKgContaComoKgInteiro() {
        // 2,3 kg = 1 kg da base + 2 kg extras (1,3 arredonda para 2)
        assertEquals(reais("23.00"), calculadora.calcular("SUDESTE", 2.3, reais("80.00")).frete());
    }

    @Test
    void pedidoDeExatamente200TemFreteGratis() {
        CotacaoFrete cotacao = calculadora.calcular("NORDESTE", 5.0, reais("200.00"));
        assertTrue(cotacao.gratis(), "R2: a partir de R$ 200,00 o frete e' gratis");
        assertEquals(reais("0.00"), cotacao.frete());
    }

    @Test
    void pedidoAbaixoDe200PagaFrete() {
        CotacaoFrete cotacao = calculadora.calcular("NORDESTE", 5.0, reais("199.99"));
        assertFalse(cotacao.gratis());
        assertEquals(reais("40.00"), cotacao.frete());
    }

    @Test
    void pesoAcimaDe30KgERejeitado() {
        assertThrows(FreteInvalidoException.class, () -> calculadora.calcular("SUL", 30.5, reais("10.00")));
    }

    @Test
    void pesoZeroERejeitado() {
        assertThrows(FreteInvalidoException.class, () -> calculadora.calcular("SUL", 0, reais("10.00")));
    }

    @Test
    void valorNegativoERejeitado() {
        assertThrows(FreteInvalidoException.class, () -> calculadora.calcular("SUL", 1, reais("-1")));
    }

    @Test
    void regiaoDesconhecidaERejeitada() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular("LESTE", 1, reais("10.00")));
    }

    @Test
    void regiaoAceitaMinusculasEHifen() {
        CotacaoFrete cotacao = calculadora.calcular("centro-oeste", 1, reais("10.00"));
        assertEquals(Regiao.CENTRO_OESTE, cotacao.regiao());
        assertEquals(5, cotacao.prazoDias());
    }
}
