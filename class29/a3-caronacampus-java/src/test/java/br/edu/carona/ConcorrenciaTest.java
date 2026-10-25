package br.edu.carona;

import static br.edu.carona.Fixtures.d;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.carona.Erros.ConflitoDeRegra;
import br.edu.carona.Fixtures.RelogioFixo;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Integração: reservas simultâneas disputando a última vaga (RN-01, RNF-05). */
class ConcorrenciaTest {
    @Test
    void ct06_seisReservasSimultaneasParaAUltimaVagaSoUmaVence(@TempDir Path pasta) throws Exception {
        RelogioFixo relogio = new RelogioFixo();
        RepositorioSqlite repo = Fixtures.repositorio(pasta);
        ServicoCarona servico = new ServicoCarona(repo, relogio);
        Viagem v = servico.criarViagem(1, "A", "B", relogio.get().plusHours(2), 1, d("6"));

        CountDownLatch largada = new CountDownLatch(1);
        List<String> resultados = Collections.synchronizedList(new ArrayList<>());
        List<Thread> fios = new ArrayList<>();
        for (long passageiro = 2; passageiro <= 7; passageiro++) {
            long p = passageiro;
            Thread t = new Thread(() -> {
                try {
                    largada.await();
                    servico.reservar(v.id(), p);
                    resultados.add("ok");
                } catch (ConflitoDeRegra e) {
                    resultados.add("lotada");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            fios.add(t);
            t.start();
        }
        largada.countDown();
        for (Thread t : fios) {
            t.join();
        }
        assertEquals(5, resultados.stream().filter("lotada"::equals).count());
        assertEquals(1, resultados.stream().filter("ok"::equals).count());
        assertEquals(1, repo.reservasAtivas(v.id()).size());
    }
}
