package br.edu.carona;

import static br.edu.carona.Fixtures.d;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.carona.Erros.ConflitoDeRegra;
import br.edu.carona.Fixtures.RelogioFixo;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Integração: o adaptador SQLite sozinho (restrições do banco e atomicidade da reserva). */
class RepositorioSqliteTest {
    RelogioFixo relogio = new RelogioFixo();
    RepositorioSqlite repo;
    Path pasta;

    @BeforeEach
    void preparar(@TempDir Path p) {
        pasta = p;
        repo = Fixtures.repositorio(p);
    }

    private Viagem viagemNova(int vagas) {
        return new Viagem(0, 1, "A", "B", relogio.get().plusHours(2), vagas, d("6"), false);
    }

    @Test
    void ct01_dadosSobrevivemAUmNovoRepositorioNoMesmoArquivo() {
        Viagem v = repo.inserirViagem(new Viagem(0, 1, "A", "B", relogio.get().plusHours(2), 2, d("9.99"), false));
        RepositorioSqlite outro = new RepositorioSqlite(pasta.resolve("carona.db").toString());
        assertEquals(d("9.99"), outro.obterViagem(v.id()).orElseThrow().custoTotal());
    }

    @Test
    void ct01_checkDoBancoBarraVagasInvalidasMesmoSemOServico() {
        assertThrows(IllegalStateException.class, () -> repo.inserirViagem(viagemNova(9)));
    }

    @Test
    void ct01_chaveEstrangeiraBarraViagemDeMotoristaInexistente() {
        Viagem v = new Viagem(0, 999, "A", "B", relogio.get().plusHours(2), 2, d("5"), false);
        assertThrows(IllegalStateException.class, () -> repo.inserirViagem(v));
    }

    @Test
    void ct06_repositorioRecusaAReservaAlemDasVagasSemAjudaDoServico() {
        Viagem v = repo.inserirViagem(viagemNova(1));
        assertTrue(repo.reservarSeHaVaga(Reserva.nova(v.id(), 2, d("3.00")), v.vagas()).isPresent());
        assertTrue(repo.reservarSeHaVaga(Reserva.nova(v.id(), 3, d("3.00")), v.vagas()).isEmpty());
        assertEquals(1, repo.reservasAtivas(v.id()).size());
    }

    @Test
    void ct07_indiceUnicoBarraReservaDuplicadaMesmoSemAChecagemDoServico() {
        Viagem v = repo.inserirViagem(viagemNova(3));
        repo.reservarSeHaVaga(Reserva.nova(v.id(), 2, d("3.00")), v.vagas());
        ConflitoDeRegra e = assertThrows(ConflitoDeRegra.class,
            () -> repo.reservarSeHaVaga(Reserva.nova(v.id(), 2, d("3.00")), v.vagas()));
        assertTrue(e.getMessage().contains("já tem reserva"));
        assertEquals(1, repo.reservasAtivas(v.id()).size());
    }

    @Test
    void ct07_reservaCanceladaNaoImpedeNovaReservaDoMesmoPassageiro() {
        ServicoCarona servico = new ServicoCarona(repo, relogio);
        Viagem v = Fixtures.viagemDaAna(servico, relogio);
        Reserva r = servico.reservar(v.id(), 2);
        servico.cancelarReserva(r.id(), 2);
        assertTrue(servico.reservar(v.id(), 2).id() != r.id());
    }
}
