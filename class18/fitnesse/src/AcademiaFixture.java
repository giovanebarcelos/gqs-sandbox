import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/** Script Table Slim: matricula -> mensalidade -> liberacao de treino. */
public class AcademiaFixture {

    private final Map<String, LocalDate> matriculaDesde = new HashMap<>();
    private final Map<String, LocalDate> mensalidadePagaAte = new HashMap<>();

    public AcademiaFixture() {
        matriculaDesde.put("joao", LocalDate.of(2026, 1, 1));
        mensalidadePagaAte.put("joao", LocalDate.of(2026, 7, 31));
    }

    public boolean matriculaAtiva(String nome, String data) {
        LocalDate desde = matriculaDesde.get(nome);
        return desde != null && !LocalDate.parse(data).isBefore(desde);
    }

    public boolean mensalidadeEmDia(String nome, String data) {
        LocalDate pagaAte = mensalidadePagaAte.get(nome);
        return pagaAte != null && !LocalDate.parse(data).isAfter(pagaAte);
    }

    public boolean podeTreinar(String nome, String data) {
        return matriculaAtiva(nome, data) && mensalidadeEmDia(nome, data);
    }
}
