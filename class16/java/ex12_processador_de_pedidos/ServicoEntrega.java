package tdd20ex.ex12_processador_de_pedidos;

interface ServicoEntrega {
    String agendarEntrega(Pedido pedido, double valorTotal);
}
