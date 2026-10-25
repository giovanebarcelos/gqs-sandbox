package br.edu.carona;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;

/** Peças comuns dos testes: relógio controlável, repositório sobre SQLite temporário e serviço pronto. */
final class Fixtures {
    private Fixtures() {}

    static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 10, 12, 0, 0);
    /** Hashes calculados uma vez só (PBKDF2 é lento de propósito). Usuários 1 a 3 conseguem fazer login. */
    private static final String HASH_A = Senhas.hash("a");
    private static final String HASH_B = Senhas.hash("b");
    private static final String HASH_C = Senhas.hash("c");

    /** Relógio de teste: o tempo só anda quando o teste manda. */
    static final class RelogioFixo implements Supplier<LocalDateTime> {
        private volatile LocalDateTime agora = AGORA;

        @Override
        public LocalDateTime get() { return agora; }

        void avancar(Duration d) { agora = agora.plus(d); }
    }

    static RepositorioSqlite repositorio(Path pasta) {
        RepositorioSqlite r = new RepositorioSqlite(pasta.resolve("carona.db").toString());
        String[][] usuarios = {{"1", "Ana", HASH_A}, {"2", "Bruno", HASH_B}, {"3", "Carla", HASH_C},
            {"4", "Davi", "x"}, {"5", "Eva", "x"}, {"6", "Fabio", "x"}, {"7", "Gil", "x"}};
        for (String[] u : usuarios) {
            r.criarUsuario(u[0], u[1], u[2]);
        }
        return r;
    }

    static BigDecimal d(String v) { return new BigDecimal(v); }

    /** Viagem da Ana (id 1) daqui a 3 h, 3 vagas, custo R$ 12,00 -> R$ 3,00 por vaga. */
    static Viagem viagemDaAna(ServicoCarona s, RelogioFixo relogio) {
        return s.criarViagem(1, "Campus", "Centro", relogio.get().plusHours(3), 3, d("12.00"));
    }
}
