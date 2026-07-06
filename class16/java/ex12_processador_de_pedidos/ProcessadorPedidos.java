package tdd20ex.ex12_processador_de_pedidos;

public class ProcessadorPedidos {
    private ValidadorPedido validador;
    private CalculadoraFrete calculadora;
    private ServicoEntrega servicoEntrega;

    public ProcessadorPedidos(ValidadorPedido validador, CalculadoraFrete calculadora, ServicoEntrega servicoEntrega) {
        this.validador = validador;
        this.calculadora = calculadora;
        this.servicoEntrega = servicoEntrega;
    }

    public ResultadoPedido processarPedido(Pedido pedido) {
        if (!validador.validar(pedido)) {
            return new ResultadoPedido(false, null, 0);
        }

        double frete = calculadora.calcularFrete(pedido.getDestino(), pedido.getItens().size());
        double valorTotal = pedido.getValor() + frete;

        String codigoRastreamento = servicoEntrega.agendarEntrega(pedido, valorTotal);

        return new ResultadoPedido(true, codigoRastreamento, valorTotal);
    }
}
