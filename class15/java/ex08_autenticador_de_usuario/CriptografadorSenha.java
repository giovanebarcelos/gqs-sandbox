package tdd20ex.ex08_autenticador_de_usuario;

interface CriptografadorSenha {
    String criptografar(String senha);
    boolean verificar(String senha, String hash);
}