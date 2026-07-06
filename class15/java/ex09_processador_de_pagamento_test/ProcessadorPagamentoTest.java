package tdd20ex.ex09_processador_de_pagamento;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessadorPagamentoTest {

    @Mock
    private GatewayPagamento gateway;

    @Mock
    private ServicoLog servicoLog;

    @InjectMocks
    private ProcessadorPagamento processador;

    @Test
    void deveProcessarPagamentoComSucesso() {
        // Arrange
        String cartao = "1234-5678-9012-3456";
        double valor = 100.0;
        when(gateway.processarPagamento(cartao, valor)).thenReturn(true);

        // Act
        boolean resultado = processador.processarPagamento(cartao, valor);

        // Assert
        assertTrue(resultado);
        verify(gateway).processarPagamento(cartao, valor);
        verify(servicoLog).registrarSucesso("Pagamento de R$100.0 processado");
        verify(servicoLog, never()).registrarErro(anyString());
    }

    @Test
    void deveRegistrarErroQuandoPagamentoFalha() {
        // Arrange
        String cartao = "1234-5678-9012-3456";
        double valor = 100.0;
        when(gateway.processarPagamento(cartao, valor)).thenReturn(false);

        // Act
        boolean resultado = processador.processarPagamento(cartao, valor);

        // Assert
        assertFalse(resultado);
        verify(servicoLog).registrarErro("Falha no pagamento de R$100.0");
        verify(servicoLog, never()).registrarSucesso(anyString());
    }

    @Test
    void deveRejeitarValorInvalido() {
        // Act
        boolean resultado = processador.processarPagamento("1234", -50.0);

        // Assert
        assertFalse(resultado);
        verify(servicoLog).registrarErro("Valor inválido: -50.0");
        verify(gateway, never()).processarPagamento(anyString(), anyDouble());
    }

    @Test
    void deveTratarExcecaoDoGateway() {
        // Arrange
        when(gateway.processarPagamento(anyString(), anyDouble()))
                .thenThrow(new RuntimeException("Erro de conexão"));

        // Act
        boolean resultado = processador.processarPagamento("1234", 100.0);

        // Assert
        assertFalse(resultado);
        verify(servicoLog).registrarErro("Erro no gateway: Erro de conexão");
    }
}
