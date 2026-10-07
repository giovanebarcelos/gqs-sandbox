import java.util.*;

/**
 * Fixture Slim do Lab Academia (RF1..RF10). Os nomes dos metodos espelham o texto
 * das linhas da Script Table: "cadastrar usuario" -> cadastrarUsuario(...).
 * Usuarios sao identificados pelo indice (0, 1, ...) na ordem de cadastro.
 */
public class SistemaAcademia {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final Map<Integer, List<String>> atividades = new HashMap<>();
    private final List<String> notificacoes = new ArrayList<>();

    // RF1
    public void cadastrarUsuario(String nome, int idade, String sexo, double altura, double peso) {
        usuarios.add(new Usuario(nome, idade, sexo, altura, peso));
    }

    // RF2
    public void atualizarPesoDoUsuario(int indice, double novoPeso) {
        usuario(indice).setPeso(novoPeso);
        verificarMeta(indice);
    }

    // RF3
    public int totalDeUsuarios() {
        return usuarios.size();
    }

    public String listarUsuarios() {
        StringBuilder sb = new StringBuilder();
        for (Usuario u : usuarios) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append(u);
        }
        return sb.toString();
    }

    public String descricaoDoUsuario(int indice) {
        return usuario(indice).toString();
    }

    public String nomeDoUsuario(int indice) {
        return usuario(indice).getNome();
    }

    // RF4
    public void registrarAtividade(int indice, String atividade, int minutos) {
        usuario(indice).addMinutos(minutos);
        atividades.computeIfAbsent(indice, k -> new ArrayList<>()).add(atividade + ":" + minutos);
    }

    // RF5
    public double calcularImcParaUsuario(int indice) {
        return Math.round(usuario(indice).calcularIMC() * 10.0) / 10.0;
    }

    // RF6
    public String recomendarAtividadeParaUsuario(int indice) {
        double imc = usuario(indice).calcularIMC();
        if (imc < 18.5) return "Ganhar peso com atividades leves";
        if (imc < 25.0) return "Manter rotina saudavel";
        return "Focar em atividades de perda de peso";
    }

    // RF7
    public String gerarRelatorioMensalDoUsuario(int indice) {
        Usuario u = usuario(indice);
        return u.getNome() + ": " + u.getMinutosAtividades() + " min em "
             + atividades.getOrDefault(indice, Collections.emptyList()).size() + " atividades";
    }

    // RF8
    public void definirMetaDePesoParaUsuario(int indice, double meta) {
        usuario(indice).setMetaPeso(meta);
    }

    public double progressoDaMetaDoUsuario(int indice) {
        Usuario u = usuario(indice);
        return Math.round((u.getPeso() - u.getMetaPeso()) * 10.0) / 10.0;
    }

    public boolean metaAtingidaParaUsuario(int indice) {
        Usuario u = usuario(indice);
        return u.getMetaPeso() > 0 && u.getPeso() <= u.getMetaPeso();
    }

    // RF9
    public String ultimaNotificacao() {
        return notificacoes.isEmpty() ? "" : notificacoes.get(notificacoes.size() - 1);
    }

    // RF10
    public void excluirUsuario(int indice) {
        usuarios.remove(indice);
    }

    private void verificarMeta(int indice) {
        if (metaAtingidaParaUsuario(indice)) {
            notificacoes.add("Parabens " + usuario(indice).getNome() + ", meta atingida!");
        }
    }

    private Usuario usuario(int indice) {
        if (indice < 0 || indice >= usuarios.size()) {
            throw new IllegalArgumentException("Usuario inexistente: " + indice);
        }
        return usuarios.get(indice);
    }
}
