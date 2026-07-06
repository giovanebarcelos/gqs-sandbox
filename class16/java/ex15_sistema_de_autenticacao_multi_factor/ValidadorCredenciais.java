package tdd20ex.ex15_sistema_de_autenticacao_multi_factor;

interface ValidadorCredenciais {
    boolean validarCredenciais(CredenciaisUsuario credenciais);
    String obterTelefone(String usuario);
}
