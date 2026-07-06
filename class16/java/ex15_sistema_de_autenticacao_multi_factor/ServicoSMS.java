package tdd20ex.ex15_sistema_de_autenticacao_multi_factor;

interface ServicoSMS {
    boolean enviarSMS(String telefone, String mensagem);
}
