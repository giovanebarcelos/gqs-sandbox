// Fixture Slim (POJO). Salvar como UsuariosAtivos.java (copia pronta em fitnesse/src/).
import java.util.*;

/** Query Table Slim (Atividade 06): lista de usuarios ativos. Cada linha = pares [coluna, valor]. */
public class UsuariosAtivos {
    private static final String[][] DADOS = {
        {"1", "Maria Silva", "maria@teste.com", "Ativo"},
        {"2", "Joao Souza",  "joao@teste.com",  "Ativo"},
        {"3", "Ana Costa",   "ana@teste.com",   "Ativo"},
    };

    public List<Object> query() {
        List<Object> linhas = new ArrayList<>();
        for (String[] d : DADOS) {
            linhas.add(Arrays.asList(
                Arrays.asList("id", d[0]),
                Arrays.asList("nome", d[1]),
                Arrays.asList("email", d[2]),
                Arrays.asList("status", d[3])));
        }
        return linhas;
    }
}
