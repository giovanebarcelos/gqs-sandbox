package br.edu.gqs.boletim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Testes unitarios das regras R1 a R3. */
class CalculadoraBoletimTest {

    private final CalculadoraBoletim calculadora = new CalculadoraBoletim();

    @Test
    void calculaMediaMaiorEMenorNota() {
        Boletim boletim = calculadora.calcular("Ana", List.of(7.0, 9.0, 8.0), 90);
        assertEquals(new BigDecimal("8.0"), boletim.media());
        assertEquals(Situacao.APROVADO, boletim.situacao());
        assertEquals(9.0, boletim.maiorNota());
        assertEquals(7.0, boletim.menorNota());
        assertEquals("Ana: APROVADO com media 8.0", boletim.resumo());
    }

    @Test
    void mediaArredondaComUmaCasa() {
        // 7 + 6,5 + 6,5 = 20 / 3 = 6,666... -> 6,7
        assertEquals(new BigDecimal("6.7"), calculadora.calcular("Bia", List.of(7.0, 6.5, 6.5), 100).media());
    }

    @ParameterizedTest(name = "media {0} com frequencia {1}% -> {2}")
    @CsvSource({
            "7.0, 100, APROVADO",
            "6.9, 100, EXAME",
            "4.0, 100, EXAME",
            "3.9, 100, REPROVADO",
            "10,  74,  REPROVADO_POR_FALTA",
            "10,  75,  APROVADO",
            "5.0, 75,  EXAME",
    })
    void situacaoPelaMediaEFrequencia(double nota, double frequencia, Situacao esperada) {
        assertEquals(esperada, calculadora.calcular("Caio", List.of(nota), frequencia).situacao());
    }

    @Test
    void notaAcimaDe10ERejeitada() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular("Davi", List.of(11.0), 80));
    }

    @Test
    void semNotasERejeitado() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular("Eva", List.of(), 80));
    }

    @Test
    void maisDeQuatroNotasERejeitado() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular("Eva", List.of(1.0, 2.0, 3.0, 4.0, 5.0), 80));
    }

    @Test
    void frequenciaAcimaDe100ERejeitada() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular("Fabi", List.of(7.0), 101));
    }

    @Test
    void alunoSemNomeERejeitado() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(" ", List.of(7.0), 80));
    }
}
