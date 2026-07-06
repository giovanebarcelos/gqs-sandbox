package tdd20ex.ex09_processador_de_pagamento;

public class ProcessadorPagamento {
    private GatewayPagamento gateway;
    private ServicoLog servicoLog;

    public ProcessadorPagamento(GatewayPagamento gateway, ServicoLog servicoLog) {
        this.gateway = gateway;
        this.servicoLog = servicoLog;
    }

    public boolean processarPagamento(String cartao, double valor) {
        if (valor <= 0) {
            servicoLog.registrarErro("Valor inválido: " + valor);
            return false;
        }

        try {
            boolean sucesso = gateway.processarPagamento(cartao, valor);

            if (sucesso) {
                servicoLog.registrarSucesso("Pagamento de R$" + valor + " processado");
                return true;
            } else {
                servicoLog.registrarErro("Falha no pagamento de R$" + valor);
                return false;
            }
        } catch (Exception e) {
            servicoLog.registrarErro("Erro no gateway: " + e.getMessage());
            return false;
        }
    }
}
