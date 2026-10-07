// Fixture Slim (POJO). Salvar como ClientesAtivos.java em fitnesse/src/ (copia pronta em repository/class18/fitnesse/src/).
import java.util.*;

/** Query Table Slim: query() devolve lista de linhas; cada linha = lista de pares [coluna, valor]. */
public class ClientesAtivos {
    public List<Object> query() {
        List<Object> linhas = new ArrayList<>();
        linhas.add(linha("1", "Joao", "joao@email.com"));
        linhas.add(linha("2", "Maria", "maria@email.com"));
        return linhas;
    }

    private List<Object> linha(String id, String nome, String email) {
        return Arrays.asList(
            Arrays.asList("id", id),
            Arrays.asList("nome", nome),
            Arrays.asList("email", email));
    }
}
