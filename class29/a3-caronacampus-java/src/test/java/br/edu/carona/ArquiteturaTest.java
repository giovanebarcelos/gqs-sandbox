package br.edu.carona;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Guardas das regras que o AGENTS.md promete (não são casos de teste do sistema, e sim do repositório). */
class ArquiteturaTest {
    static final Path SRC = Path.of("src/main/java/br/edu/carona");

    static String ler(String arquivo) throws IOException {
        return Files.readString(SRC.resolve(arquivo));
    }

    @Test
    void arq_regrasEEntidadesNaoDependemDeJdbcNemDeHttp() throws IOException {
        for (String arq : List.of("Regras.java", "Viagem.java", "Reserva.java", "Usuario.java", "Erros.java")) {
            String fonte = ler(arq);
            assertFalse(fonte.contains("import java.sql"), arq);
            assertFalse(fonte.contains("com.sun.net"), arq);
            assertFalse(fonte.contains("org.sqlite"), arq);
        }
    }

    @Test
    void arq_servicoDependeSoDaPortaENaoDoSqlite() throws IOException {
        String fonte = ler("ServicoCarona.java");
        assertFalse(fonte.contains("RepositorioSqlite"));
        assertTrue(fonte.contains("RepositorioCarona"));
    }

    @Test
    void arq_relogioRealSoApareceEmRelogioAjustavel() throws IOException {
        Pattern relogioReal = Pattern.compile("(LocalDateTime|Instant|LocalDate)\\.now\\(");
        try (Stream<Path> arquivos = Files.list(SRC)) {
            for (Path p : arquivos.filter(f -> f.toString().endsWith(".java")).toList()) {
                String semComentarios = Files.readString(p).replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//[^\\n]*", "");
                boolean usa = relogioReal.matcher(semComentarios).find();
                assertEquals(p.getFileName().toString().equals("RelogioAjustavel.java"), usa, p.getFileName().toString());
            }
        }
    }
}
