package tdd20ex.ex12_processador_de_pedidos;

class ResultadoPedido {
    private boolean sucesso;
    private String codigoRastreamento;
    private double valorTotal;

    public ResultadoPedido(boolean sucesso, String codigoRastreamento,
                           double valorTotal) {
        this.sucesso = sucesso;
        this.codigoRastreamento = codigoRastreamento;
        this.valorTotal = valorTotal;
    }

    // getters...
    public boolean isSucesso() { return sucesso; }
    public String getCodigoRastreamento() { return codigoRastreamento; }
    public double getValorTotal() { return valorTotal; }
}
