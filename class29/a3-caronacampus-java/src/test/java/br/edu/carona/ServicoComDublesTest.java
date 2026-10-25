package br.edu.carona;

import static br.edu.carona.Fixtures.AGORA;
import static br.edu.carona.Fixtures.d;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.carona.Erros.ConflitoDeRegra;
import br.edu.carona.Erros.DadoInvalido;
import br.edu.carona.Erros.MotoristaSuspenso;
import br.edu.carona.Erros.NaoEncontrado;
import br.edu.carona.Erros.SemPermissao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Serviço isolado: o repositório é um Mock. Verifica também o que o serviço NÃO chama quando uma regra recusa. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServicoComDublesTest {
    @Mock RepositorioCarona repo;
    ServicoCarona servico;

    static Viagem viagem(LocalDateTime saida, int vagas, boolean cancelada) {
        return new Viagem(10, 1, "Campus", "Centro", saida, vagas, d("9.00"), cancelada);
    }

    @BeforeEach
    void preparar() {
        servico = new ServicoCarona(repo, () -> AGORA);
        when(repo.notasDoMotorista(1)).thenReturn(List.of());
        when(repo.reservasAtivas(10)).thenReturn(List.of());
        when(repo.obterViagem(10)).thenReturn(Optional.of(viagem(AGORA.plusHours(3), 2, false)));
        when(repo.inserirViagem(any())).thenAnswer(i -> i.getArgument(0));
        when(repo.reservarSeHaVaga(any(), anyInt())).thenAnswer(i -> Optional.of(i.getArgument(0)));
    }

    // --- CT-01 · criação
    @Test
    void ct01_criarViagemValidaPersisteUmaVez() {
        Viagem v = servico.criarViagem(1, " Campus ", "Centro", AGORA.plusHours(2), 3, d("12.00"));
        verify(repo).inserirViagem(any());
        assertEquals("Campus", v.origem());
        assertEquals(3, v.vagas());
    }

    @ParameterizedTest
    @CsvSource(value = {"'',Centro", "Campus,'  '", "Centro,centro"})
    void ct01_origemEDestinoObrigatoriosEDiferentes(String origem, String destino) {
        assertThrows(DadoInvalido.class, () -> servico.criarViagem(1, origem, destino, AGORA.plusHours(2), 3, d("12")));
        verify(repo, never()).inserirViagem(any());
    }

    @Test
    void ct01_origemNulaERecusada() {
        assertThrows(DadoInvalido.class, () -> servico.criarViagem(1, null, "Centro", AGORA.plusHours(2), 3, d("12")));
    }

    @Test
    void ct01_custoNaoPositivoERecusado() {
        assertThrows(DadoInvalido.class, () -> servico.criarViagem(1, "A", "B", AGORA.plusHours(2), 3, d("0")));
        verify(repo, never()).inserirViagem(any());
    }

    // --- CT-13 · motorista suspenso
    @Test
    void ct13_motoristaSuspensoNaoCriaViagem() {
        when(repo.notasDoMotorista(1)).thenReturn(List.of(1, 1, 2, 2, 1));
        assertThrows(MotoristaSuspenso.class, () -> servico.criarViagem(1, "A", "B", AGORA.plusHours(2), 3, d("12")));
        verify(repo, never()).inserirViagem(any());
    }

    @Test
    void ct13_motoristaComMediaExata3AindaCriaViagem() {
        when(repo.notasDoMotorista(1)).thenReturn(List.of(3, 3, 3, 3, 3));
        servico.criarViagem(1, "A", "B", AGORA.plusHours(2), 3, d("12"));
        verify(repo).inserirViagem(any());
    }

    // --- CT-05, CT-06, CT-07 · reservas
    @Test
    void ct05_reservarGravaOValorCalculadoPeloDominio() {
        Reserva r = servico.reservar(10, 2);
        assertEquals(d("3.00"), r.valor());   // 9,00 / (2 + 1)
        assertEquals(2, r.passageiroId());
        verify(repo).reservarSeHaVaga(any(), anyInt());
    }

    @Test
    void ct06_ultimaVagaEAceitaEASeguinteERecusada() {
        when(repo.reservasAtivas(10)).thenReturn(List.of(Reserva.nova(10, 3, d("3.00"))));            // 1 de 2
        servico.reservar(10, 2);
        when(repo.reservasAtivas(10)).thenReturn(List.of(Reserva.nova(10, 3, d("3.00")), Reserva.nova(10, 4, d("3.00")))); // 2 de 2
        org.mockito.Mockito.clearInvocations(repo);
        ConflitoDeRegra e = assertThrows(ConflitoDeRegra.class, () -> servico.reservar(10, 2));
        assertTrue(e.getMessage().contains("lotada"));
        verify(repo, never()).reservarSeHaVaga(any(), anyInt());
    }

    @Test
    void ct06_seORepositorioDizQueLotouNaHoraOServicoRecusa() {
        doReturn(Optional.empty()).when(repo).reservarSeHaVaga(any(), anyInt());   // outra requisição levou a vaga
        ConflitoDeRegra e = assertThrows(ConflitoDeRegra.class, () -> servico.reservar(10, 2));
        assertTrue(e.getMessage().contains("lotada"));
    }

    @Test
    void ct07_motoristaNaoReservaAPropriaViagem() {
        ConflitoDeRegra e = assertThrows(ConflitoDeRegra.class, () -> servico.reservar(10, 1));
        assertTrue(e.getMessage().contains("própria viagem"));
        verify(repo, never()).reservarSeHaVaga(any(), anyInt());
    }

    @Test
    void ct07_passageiroComReservaAtivaNaoReservaDeNovo() {
        when(repo.reservasAtivas(10)).thenReturn(List.of(Reserva.nova(10, 2, d("3.00"))));
        ConflitoDeRegra e = assertThrows(ConflitoDeRegra.class, () -> servico.reservar(10, 2));
        assertTrue(e.getMessage().contains("já tem reserva"));
        verify(repo, never()).reservarSeHaVaga(any(), anyInt());
    }

    @ParameterizedTest
    @CsvSource({"0", "-60"})
    void ct07_naoReservaEmViagemQueJaComecou(long segundosDepoisDaSaida) {
        when(repo.obterViagem(10)).thenReturn(Optional.of(viagem(AGORA.plusSeconds(segundosDepoisDaSaida), 2, false)));
        ConflitoDeRegra e = assertThrows(ConflitoDeRegra.class, () -> servico.reservar(10, 2));
        assertTrue(e.getMessage().contains("já começou"));
    }

    @Test
    void ct07_viagemInexistenteOuCanceladaNaoEEncontrada() {
        when(repo.obterViagem(99)).thenReturn(Optional.empty());
        assertThrows(NaoEncontrado.class, () -> servico.reservar(99, 2));
        when(repo.obterViagem(10)).thenReturn(Optional.of(viagem(AGORA.plusHours(3), 2, true)));
        assertThrows(NaoEncontrado.class, () -> servico.reservar(10, 2));
    }

    // --- CT-08, CT-09, CT-10 · cancelamento de reserva
    private Reserva reservaDe(long passageiro, boolean cancelada) {
        return new Reserva(5, 10, passageiro, d("6.00"), cancelada, d("0.00"));
    }

    @Test
    void ct08_cancelarComAntecedenciaNaoCobraTaxa() {
        when(repo.obterReserva(5)).thenReturn(Optional.of(reservaDe(2, false)));
        Reserva r = servico.cancelarReserva(5, 2);
        assertTrue(r.cancelada());
        assertEquals(d("0.00"), r.taxa());
        verify(repo).atualizarReserva(r);
    }

    @Test
    void ct09_cancelarEmCimaDaHoraCobraMetade() {
        when(repo.obterViagem(10)).thenReturn(Optional.of(viagem(AGORA.plusSeconds(3599), 2, false)));
        when(repo.obterReserva(5)).thenReturn(Optional.of(reservaDe(2, false)));
        assertEquals(d("3.00"), servico.cancelarReserva(5, 2).taxa());
    }

    @Test
    void ct10_cancelarDepoisDaSaidaERecusadoSemGravar() {
        when(repo.obterViagem(10)).thenReturn(Optional.of(viagem(AGORA, 2, false)));
        when(repo.obterReserva(5)).thenReturn(Optional.of(reservaDe(2, false)));
        assertThrows(ConflitoDeRegra.class, () -> servico.cancelarReserva(5, 2));
        verify(repo, never()).atualizarReserva(any());
    }

    @Test
    void ct10_soODonoCancelaENaoCancelaDuasVezes() {
        when(repo.obterReserva(5)).thenReturn(Optional.of(reservaDe(2, false)));
        assertThrows(SemPermissao.class, () -> servico.cancelarReserva(5, 3));
        when(repo.obterReserva(5)).thenReturn(Optional.of(reservaDe(2, true)));
        assertTrue(assertThrows(ConflitoDeRegra.class, () -> servico.cancelarReserva(5, 2)).getMessage().contains("já cancelada"));
        when(repo.obterReserva(5)).thenReturn(Optional.empty());
        assertThrows(NaoEncontrado.class, () -> servico.cancelarReserva(5, 2));
    }

    // --- CT-11 · avaliação
    private void viagemQueJaSaiu() {
        when(repo.obterViagem(10)).thenReturn(Optional.of(viagem(AGORA.minusHours(1), 2, false)));
        when(repo.reservasAtivas(10)).thenReturn(List.of(Reserva.nova(10, 2, d("3.00"))));
        when(repo.avaliacaoExiste(10, 2)).thenReturn(false);
    }

    @Test
    void ct11_passageiroQueViajouAvalia() {
        viagemQueJaSaiu();
        servico.avaliar(10, 2, 5);
        verify(repo).inserirAvaliacao(10, 2, 5);
    }

    @Test
    void ct11_avaliacoesRecusadas() {
        viagemQueJaSaiu();
        when(repo.obterViagem(10)).thenReturn(Optional.of(viagem(AGORA.plusHours(1), 2, false)));   // ainda não saiu
        assertThrows(ConflitoDeRegra.class, () -> servico.avaliar(10, 2, 4));
        viagemQueJaSaiu();
        assertThrows(SemPermissao.class, () -> servico.avaliar(10, 3, 4));                            // não tinha reserva
        when(repo.avaliacaoExiste(10, 2)).thenReturn(true);
        assertThrows(ConflitoDeRegra.class, () -> servico.avaliar(10, 2, 4));                         // já avaliou
        verify(repo, never()).inserirAvaliacao(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(), anyInt());
    }

    @Test
    void ct11_notaInvalidaNemConsultaORepositorio() {
        org.mockito.Mockito.clearInvocations(repo);
        assertThrows(DadoInvalido.class, () -> servico.avaliar(10, 2, 6));
        verify(repo, never()).obterViagem(org.mockito.ArgumentMatchers.anyLong());
    }

    // --- CT-12 · reputação
    @Test
    void ct12_reputacaoInformaMediaESuspensao() {
        when(repo.obterUsuario(1)).thenReturn(Optional.of(new Usuario(1, "1", "Ana")));
        when(repo.notasDoMotorista(1)).thenReturn(List.of(3, 3, 3, 3, 2));
        var r = servico.reputacao(1);
        assertEquals(5, r.get("avaliacoes"));
        assertEquals("2.80", r.get("media"));
        assertEquals(true, r.get("suspenso"));
        when(repo.notasDoMotorista(1)).thenReturn(List.of());
        assertEquals(null, servico.reputacao(1).get("media"));
    }

    @Test
    void ct12_reputacaoDeUsuarioInexistente() {
        when(repo.obterUsuario(99)).thenReturn(Optional.empty());
        assertThrows(NaoEncontrado.class, () -> servico.reputacao(99));
    }

    // --- CT-14 · alterar e cancelar viagem
    @Test
    void ct14_motoristaAlteraViagemSemReservas() {
        Viagem v = servico.atualizarViagem(10, 1, null, "Rodoviária", 4, d("20.00"), null);
        assertEquals(4, v.vagas());
        assertEquals(d("20.00"), v.custoTotal());
        assertEquals("Rodoviária", v.destino());
        verify(repo).atualizarViagem(v);
    }

    @Test
    void ct14_alteracaoInvalidaNaoGrava() {
        assertThrows(DadoInvalido.class, () -> servico.atualizarViagem(10, 1, null, null, 7, null, null));
        assertThrows(DadoInvalido.class, () -> servico.atualizarViagem(10, 1, null, null, null, d("0"), null));
        assertThrows(DadoInvalido.class, () -> servico.atualizarViagem(10, 1, null, "campus", null, null, null));
        assertThrows(DadoInvalido.class, () -> servico.atualizarViagem(10, 1, null, null, null, null, AGORA.plusMinutes(29)));
        assertThrows(DadoInvalido.class, () -> servico.atualizarViagem(10, 1, "  ", null, null, null, null));
        verify(repo, never()).atualizarViagem(any());
    }

    @Test
    void ct14_outroUsuarioNaoAlteraNemCancela() {
        assertThrows(SemPermissao.class, () -> servico.atualizarViagem(10, 2, null, null, 3, null, null));
        assertThrows(SemPermissao.class, () -> servico.excluirViagem(10, 2));
        verify(repo, never()).atualizarViagem(any());
    }

    @Test
    void ct14_viagemComReservasNaoMudaNemECancelada() {
        when(repo.reservasAtivas(10)).thenReturn(List.of(Reserva.nova(10, 2, d("3.00"))));
        assertThrows(ConflitoDeRegra.class, () -> servico.atualizarViagem(10, 1, null, null, 3, null, null));
        assertThrows(ConflitoDeRegra.class, () -> servico.excluirViagem(10, 1));
        verify(repo, never()).atualizarViagem(any());
    }

    @Test
    void ct14_excluirViagemMarcaComoCancelada() {
        servico.excluirViagem(10, 1);
        org.mockito.ArgumentCaptor<Viagem> c = org.mockito.ArgumentCaptor.forClass(Viagem.class);
        verify(repo).atualizarViagem(c.capture());
        assertTrue(c.getValue().cancelada());
    }

    // --- CT-16 · relatório
    @Test
    void ct16_relatorioDeOcupacao() {
        when(repo.ocupacaoPorViagem()).thenReturn(List.of(new long[] {1, 4, 3}, new long[] {2, 2, 0}, new long[] {3, 3, 3}));
        var r = servico.relatorioOcupacao();
        assertEquals(3, r.get("viagens"));
        assertEquals(9L, r.get("vagas"));
        assertEquals(6L, r.get("ocupadas"));
        assertEquals(66.7, r.get("taxa_ocupacao"));
    }

    @Test
    void ct16_relatorioSemViagensNaoDivideAcimaDeZero() {
        when(repo.ocupacaoPorViagem()).thenReturn(List.of());
        assertEquals(0.0, servico.relatorioOcupacao().get("taxa_ocupacao"));
        assertFalse(((List<?>) servico.relatorioOcupacao().get("por_viagem")).iterator().hasNext());
    }
}
