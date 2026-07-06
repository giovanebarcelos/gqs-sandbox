package tdd20ex.ex09_processador_de_pagamento;

public interface GatewayPagamento {
    boolean processarPagamento(String cartao, double valor);
}
