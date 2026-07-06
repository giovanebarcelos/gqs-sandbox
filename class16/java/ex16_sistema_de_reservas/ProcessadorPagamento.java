package tdd20ex.ex16_sistema_de_reservas;

interface ProcessadorPagamento {
    boolean processarPagamento(String cartao, double valor);
}
