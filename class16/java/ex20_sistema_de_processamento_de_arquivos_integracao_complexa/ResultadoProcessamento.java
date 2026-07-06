package tdd20ex.ex20_sistema_de_processamento_de_arquivos_integracao_complexa;

class ResultadoProcessamento {
    private boolean sucesso;
    private String urlFinal;
    private long tamanhoOriginal;
    private long tamanhoFinal;
    private String mensagem;

    public ResultadoProcessamento(boolean sucesso, String urlFinal, long tamanhoOriginal, long tamanhoFinal, String mensagem) {
        this.sucesso = sucesso;
        this.urlFinal = urlFinal;
        this.tamanhoOriginal = tamanhoOriginal;
        this.tamanhoFinal = tamanhoFinal;
        this.mensagem = mensagem;
    }

    // getters...
    public boolean isSucesso() { return sucesso; }
    public String getUrlFinal() { return urlFinal; }
    public long getTamanhoOriginal() { return tamanhoOriginal; }
    public long getTamanhoFinal() { return tamanhoFinal; }
    public String getMensagem() { return mensagem; }
}
