import java.util.ArrayList;
import java.util.List;

class Usuario {
    private String nome;
    private int idade;
    private String sexo;
    private double altura;
    private double peso;
    private double metaPeso;

    public Usuario(String nome, int idade, String sexo, double altura, double peso) {
        this.nome = nome;
        this.idade = idade;
        this.sexo = sexo;
        this.altura = altura;
        this.peso = peso;
    }

    // Getters e setters
    public double calcularIMC() {
        return peso / (altura * altura);
    }

    public double getPeso() {
        return peso;
    }

    public double getMetaPeso() {
        return metaPeso;
    }

    public void setMetaPeso(double metaPeso) {
        this.metaPeso = metaPeso;
    }
}

class SistemaAcademia {
    private List<Usuario> usuarios = new ArrayList<>();

    public void cadastrarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public void editarUsuario(int index, Usuario novoUsuario) {
        usuarios.set(index, novoUsuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    public String recomendarAtividade(double imc) {
        if (imc < 18.5) return "Ganhar peso com atividades leves";
        else if (imc < 25) return "Manter rotina saudável";
        else return "Focar em atividades de perda de peso";
    }

    public String gerarRelatorioMensal() {
        // Implementação básica para ilustrar
        return "Relatório mensal gerado.";
    }

    public void definirMetaPeso(int index, double meta) {
        usuarios.get(index).setMetaPeso(meta);
    }

    public boolean verificarMetaAtingida(int index) {
        Usuario usuario = usuarios.get(index);
        return usuario.getPeso() <= usuario.getMetaPeso();
    }

    public void excluirUsuario(int index) {
        usuarios.remove(index);
    }
}
