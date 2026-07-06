package tdd20ex.ex12_processador_de_pedidos;

import java.util.List;

class Pedido {
    private String id;
    private String destino;
    private double valor;
    private List<String> itens;

    public Pedido(String id, String destino, double valor,
                  List<String> itens) {
        this.id = id;
        this.destino = destino;
        this.valor = valor;
        this.itens = itens;
    }

    // getters...
    public String getId() { return id; }
    public String getDestino() { return destino; }
    public double getValor() { return valor; }
    public List<String> getItens() { return itens; }
}
