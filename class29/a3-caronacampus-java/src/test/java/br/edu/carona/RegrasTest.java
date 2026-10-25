package br.edu.carona;

import static br.edu.carona.Fixtures.AGORA;
import static br.edu.carona.Fixtures.d;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.carona.Erros.ConflitoDeRegra;
import br.edu.carona.Erros.DadoInvalido;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Testes unitários das regras puras (RN-01 a RN-05). Sem banco, sem HTTP, sem relógio real. */
class RegrasTest {
    // --- CT-02 · RN-01 · valor-limite das vagas
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 6})
    void ct02_vagasDentroDoIntervaloSaoAceitas(int vagas) {
        assertEquals(vagas, Regras.validarVagas(vagas));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 7, -1, 100})
    void ct02_vagasForaDoIntervaloSaoRecusadas(int vagas) {
        assertThrows(DadoInvalido.class, () -> Regras.validarVagas(vagas));
    }

    // --- CT-03 · RN-02 · valor-limite da saída (em segundos)
    @ParameterizedTest
    @ValueSource(longs = {1800, 1801, 14 * 86400, 86400})
    void ct03_saidaNosLimitesInclusivosEAceita(long segundos) {
        LocalDateTime saida = AGORA.plusSeconds(segundos);
        assertEquals(saida, Regras.validarSaida(saida, AGORA));
    }

    @ParameterizedTest
    @ValueSource(longs = {1799, 0, 14 * 86400 + 1, -86400})
    void ct03_saidaForaDosLimitesERecusada(long segundos) {
        assertThrows(DadoInvalido.class, () -> Regras.validarSaida(AGORA.plusSeconds(segundos), AGORA));
    }

    // --- CT-04 · RN-03 · preço por vaga
    @ParameterizedTest
    @CsvSource({"12.00,3,3.00", "10.00,2,3.34", "10.01,4,2.01", "2.00,6,2.00", "100.00,1,25.00",
        "50.00,1,25.00", "50.02,1,25.00", "4.00,1,2.00", "3.98,1,2.00"})
    void ct04_precoPorVaga(String custo, int vagas, String esperado) {
        assertEquals(d(esperado), Regras.precoPorVaga(d(custo), vagas));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-5.00"})
    void ct04_custoNaoPositivoERecusado(String custo) {
        assertThrows(DadoInvalido.class, () -> Regras.precoPorVaga(d(custo), 3));
    }

    @Test
    void ct04_precoTemDuasCasas() {
        assertEquals(2, Regras.precoPorVaga(d("10.00"), 2).scale());
    }

    // --- CT-08, CT-09, CT-10 · RN-04 · taxa de cancelamento
    private static final LocalDateTime SAIDA = AGORA.plusHours(5);

    @ParameterizedTest
    @ValueSource(longs = {18000, 3660, 3600})
    void ct08_ate60MinAntesNaoHaTaxa(long segundosAntes) {
        assertEquals(d("0.00"), Regras.taxaDeCancelamento(d("6.00"), SAIDA, SAIDA.minusSeconds(segundosAntes)));
    }

    @ParameterizedTest
    @ValueSource(longs = {3599, 1800, 1})
    void ct09_comMenosDe60MinATaxaEMetadeDoValor(long segundosAntes) {
        assertEquals(d("3.00"), Regras.taxaDeCancelamento(d("6.00"), SAIDA, SAIDA.minusSeconds(segundosAntes)));
    }

    @Test
    void ct09_taxaArredondaMeioCentavoParaCima() {
        assertEquals(d("1.51"), Regras.taxaDeCancelamento(d("3.01"), SAIDA, SAIDA.minusMinutes(10)));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 7200})
    void ct10_naHoraDaSaidaOuDepoisNaoCancela(long segundosDepois) {
        assertThrows(ConflitoDeRegra.class,
            () -> Regras.taxaDeCancelamento(d("6.00"), SAIDA, SAIDA.plusSeconds(segundosDepois)));
    }

    // --- CT-11 · validação da nota
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void ct11_notasDe1a5SaoAceitas(int nota) {
        assertEquals(nota, Regras.validarNota(nota));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6, -1})
    void ct11_notasForaDoDominioSaoRecusadas(int nota) {
        assertThrows(DadoInvalido.class, () -> Regras.validarNota(nota));
    }

    // --- CT-12 · RN-05 · suspensão por reputação
    @Test
    void ct12_suspensaoPorMediaBaixa() {
        assertFalse(Regras.estaSuspenso(List.of()));
        assertFalse(Regras.estaSuspenso(List.of(1, 1, 1, 1)));            // 4 avaliações: ainda não
        assertTrue(Regras.estaSuspenso(List.of(1, 1, 1, 1, 1)));          // 5ª avaliação, média 1,0
        assertFalse(Regras.estaSuspenso(List.of(3, 3, 3, 3, 3)));         // média exata 3,0: NÃO suspende
        assertTrue(Regras.estaSuspenso(List.of(3, 3, 3, 3, 2)));          // média 2,8
        assertFalse(Regras.estaSuspenso(List.of(5, 5, 5, 1, 1)));         // média 3,4
        assertTrue(Regras.estaSuspenso(List.of(1, 5, 5, 1, 2)));          // 2,8 em outra ordem
        assertTrue(Regras.estaSuspenso(List.of(4, 4, 4, 1, 1, 1)));       // 6 avaliações, média 2,5
    }

    @Test
    void ct12_mediaEArredondadaEmDuasCasas() {
        assertEquals(d("4.33"), Regras.media(List.of(5, 4, 4)));
        assertNull(Regras.media(List.of()));
    }
}
