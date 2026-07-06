package tdd20ex.ex15_sistema_de_autenticacao_multi_factor;

class ResultadoAutenticacao {
    private boolean sucesso;
    private String tokenMFA;
    private String mensagem;

    public ResultadoAutenticacao(boolean sucesso, String tokenMFA, String mensagem) {
        this.sucesso = sucesso;
        this.tokenMFA = tokenMFA;
        this.mensagem = mensagem;
    }

    public boolean isSucesso() { return sucesso; }
    public String getTokenMFA() { return tokenMFA; }
    public String getMensagem() { return mensagem; }
}
