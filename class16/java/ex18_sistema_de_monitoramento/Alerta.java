package tdd20ex.ex18_sistema_de_monitoramento;

class Alerta {
    private String tipo;
    private String mensagem;
    private double valor;

    public Alerta(String tipo, String mensagem, double valor) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.valor = valor;
    }

    public String getTipo() { return tipo; }
    public String getMensagem() { return mensagem; }
}
