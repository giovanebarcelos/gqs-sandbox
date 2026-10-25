package br.edu.carona;

import static br.edu.carona.Fixtures.d;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.carona.Erros.ConflitoDeRegra;
import br.edu.carona.Erros.MotoristaSuspenso;
import br.edu.carona.Erros.NaoEncontrado;
import br.edu.carona.Erros.SemPermissao;
import br.edu.carona.Fixtures.RelogioFixo;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Integração: serviço + SQLite real (arquivo temporário), com relógio controlado. */
class ServicoSqliteTest {
    RelogioFixo relogio;
    RepositorioSqlite repo;
    ServicoCarona servico;
    Viagem viagem;

    @BeforeEach
    void preparar(@TempDir Path pasta) {
        relogio = new RelogioFixo();
        repo = Fixtures.repositorio(pasta);
        servico = new ServicoCarona(repo, relogio);
        viagem = Fixtures.viagemDaAna(servico, relogio);
    }

    // --- CT-01 · persistência
    @Test
    void ct01_viagemCriadaELidaDeVoltaComTodosOsCampos() {
        var saida = relogio.get().plusHours(2);
        Viagem criada = servico.criarViagem(1, "Campus", "Centro", saida, 4, d("20.00"));
        Viagem lida = servico.obterViagem(criada.id());
        assertEquals(List.of("Campus", "Centro", "4", "20.00", "1"),
            List.of(lida.origem(), lida.destino(), "" + lida.vagas(), lida.custoTotal().toPlainString(), "" + lida.motoristaId()));
        assertEquals(saida, lida.saida());
    }

    // --- CT-05, CT-06 · reserva e lotação, ponta a ponta
    @Test
    void ct05_ct06_viagemDe3VagasAceita3ReservasERecusaA4a() {
        for (long passageiro : new long[] {2, 3, 4}) {
            assertEquals(d("3.00"), servico.reservar(viagem.id(), passageiro).valor());
        }
        ConflitoDeRegra e = assertThrows(ConflitoDeRegra.class, () -> servico.reservar(viagem.id(), 5));
        assertTrue(e.getMessage().contains("lotada"));
    }

    @Test
    void ct06_vagaLiberadaPeloCancelamentoPodeSerReocupada() {
        Reserva r1 = servico.reservar(viagem.id(), 2);
        servico.reservar(viagem.id(), 3);
        servico.reservar(viagem.id(), 4);
        servico.cancelarReserva(r1.id(), 2);
        servico.reservar(viagem.id(), 5);
    }

    @Test
    void ct07_naoReservaEmViagemQueJaSaiu() {
        relogio.avancar(Duration.ofHours(3));
        assertTrue(assertThrows(ConflitoDeRegra.class, () -> servico.reservar(viagem.id(), 2)).getMessage().contains("já começou"));
    }

    // --- CT-08, CT-09 · taxa nos limites, com relógio controlado (reserva de R$ 3,00; saída em 3 h)
    @ParameterizedTest
    @CsvSource({"3599,0.00", "7200,0.00", "7201,1.50", "10740,1.50"})
    void ct08_ct09_taxaNoLimiteDe60Minutos(long segundosAvancados, String taxa) {
        Reserva r = servico.reservar(viagem.id(), 2);
        relogio.avancar(Duration.ofSeconds(segundosAvancados));
        Reserva cancelada = servico.cancelarReserva(r.id(), 2);
        assertEquals(d(taxa), cancelada.taxa());
        assertEquals(d(taxa), repo.obterReserva(r.id()).orElseThrow().taxa());          // gravou de verdade
    }

    @Test
    void ct10_aposASaidaNaoCancelaEAReservaContinuaAtiva() {
        Reserva r = servico.reservar(viagem.id(), 2);
        relogio.avancar(Duration.ofHours(3));
        assertThrows(ConflitoDeRegra.class, () -> servico.cancelarReserva(r.id(), 2));
        assertFalse(repo.obterReserva(r.id()).orElseThrow().cancelada());
    }

    // --- CT-11, CT-12, CT-13 · avaliação, suspensão e efeito
    private void viagensConcluidas(long[] passageiros, int[] notas) {
        for (int i = 0; i < notas.length; i++) {
            Viagem v = servico.criarViagem(1, "A", "B", relogio.get().plusHours(1), 2, d("6.00"));
            servico.reservar(v.id(), passageiros[i]);
            relogio.avancar(Duration.ofHours(2));
            servico.avaliar(v.id(), passageiros[i], notas[i]);
        }
    }

    @Test
    void ct11_naoAvaliaAntesDaSaidaNemSemReservaNemDuasVezes() {
        servico.reservar(viagem.id(), 2);
        assertThrows(ConflitoDeRegra.class, () -> servico.avaliar(viagem.id(), 2, 5));
        relogio.avancar(Duration.ofHours(3));
        assertThrows(SemPermissao.class, () -> servico.avaliar(viagem.id(), 3, 5));
        servico.avaliar(viagem.id(), 2, 5);
        assertThrows(ConflitoDeRegra.class, () -> servico.avaliar(viagem.id(), 2, 4));
    }

    @Test
    void ct11_reservaCanceladaNaoDaDireitoAAvaliar() {
        Reserva r = servico.reservar(viagem.id(), 2);
        servico.cancelarReserva(r.id(), 2);
        relogio.avancar(Duration.ofHours(3));
        assertThrows(SemPermissao.class, () -> servico.avaliar(viagem.id(), 2, 1));
    }

    @Test
    void ct12_ct13_quintaAvaliacaoBaixaSuspendeEBloqueiaNovaViagem() {
        viagensConcluidas(new long[] {2, 3, 4, 5, 6}, new int[] {2, 3, 3, 3, 3});           // média 2,8
        var rep = servico.reputacao(1);
        assertEquals(5, rep.get("avaliacoes"));
        assertEquals("2.80", rep.get("media"));
        assertEquals(true, rep.get("suspenso"));
        assertThrows(MotoristaSuspenso.class, () -> servico.criarViagem(1, "A", "B", relogio.get().plusHours(2), 2, d("6")));
    }

    @Test
    void ct12_ct13_mediaExatamente3NaoSuspende() {
        viagensConcluidas(new long[] {2, 3, 4, 5, 6}, new int[] {3, 3, 3, 3, 3});
        assertEquals(false, servico.reputacao(1).get("suspenso"));
        servico.criarViagem(1, "A", "B", relogio.get().plusHours(2), 2, d("6"));
    }

    @Test
    void ct12_quatroAvaliacoesPessimasAindaNaoSuspendem() {
        viagensConcluidas(new long[] {2, 3, 4, 5}, new int[] {1, 1, 1, 1});
        assertEquals(false, servico.reputacao(1).get("suspenso"));
    }

    // --- CT-14 · alterar e cancelar viagem
    @Test
    void ct14_alteracaoECancelamentoPersistem() {
        servico.atualizarViagem(viagem.id(), 1, null, "Aeroporto", 5, null, null);
        Viagem lida = servico.obterViagem(viagem.id());
        assertEquals(5, lida.vagas());
        assertEquals("Aeroporto", lida.destino());
        servico.excluirViagem(viagem.id(), 1);
        assertThrows(NaoEncontrado.class, () -> servico.obterViagem(viagem.id()));
        assertTrue(servico.listarViagens().stream().noneMatch(v -> v.id() == viagem.id()));
    }

    @Test
    void ct14_depoisQueAReservaECanceladaAViagemPodeSerCancelada() {
        Reserva r = servico.reservar(viagem.id(), 2);
        assertThrows(ConflitoDeRegra.class, () -> servico.excluirViagem(viagem.id(), 1));
        servico.cancelarReserva(r.id(), 2);
        servico.excluirViagem(viagem.id(), 1);
    }

    // --- CT-16 · relatório
    @Test
    void ct16_relatorioContaSoReservasAtivasDeViagensAbertas() {
        Viagem outra = servico.criarViagem(3, "X", "Y", relogio.get().plusHours(2), 2, d("6"));
        servico.reservar(viagem.id(), 2);
        Reserva r2 = servico.reservar(viagem.id(), 4);
        servico.cancelarReserva(r2.id(), 4);
        servico.reservar(outra.id(), 2);
        var rel = servico.relatorioOcupacao();
        assertEquals(2, rel.get("viagens"));
        assertEquals(5L, rel.get("vagas"));
        assertEquals(2L, rel.get("ocupadas"));
        assertEquals(40.0, rel.get("taxa_ocupacao"));
    }
}
