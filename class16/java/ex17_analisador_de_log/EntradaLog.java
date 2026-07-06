package tdd20ex.ex17_analisador_de_log;

class EntradaLog {
    private String timestamp;
    private String nivel;
    private String mensagem;

    public EntradaLog(String timestamp, String nivel, String mensagem) {
        this.timestamp = timestamp;
        this.nivel = nivel;
        this.mensagem = mensagem;
    }

    public String getNivel() { return nivel; }
    public String getMensagem() { return mensagem; }
}
