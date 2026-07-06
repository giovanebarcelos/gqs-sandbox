package tdd20ex.ex08_autenticador_de_usuario;

class Usuario {
    private String email;
    private String senhaHash;

    public Usuario(String email, String senhaHash) {
        this.email = email;
        this.senhaHash = senhaHash;
    }

    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
}
