package tdd20ex.ex15_sistema_de_autenticacao_multi_factor;

class CredenciaisUsuario {
    private String usuario;
    private String senha;

    public CredenciaisUsuario(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    public String getUsuario() { return usuario; }
    public String getSenha() { return senha; }
}
