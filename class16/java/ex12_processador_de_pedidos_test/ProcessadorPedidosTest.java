package tdd20ex.ex12_processador_de_pedidos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessadorPedidosTest {

    @Mock
    private ValidadorPedido validador;

    @Mock
    private CalculadoraFrete calculadora;

    @Mock
    private ServicoEntrega servicoEntrega;

    @InjectMocks
    private ProcessadorPedidos processador;

    @Test
    void deveProcessarPedidoComSucesso() {
        // Arrange
        Pedido pedido = new Pedido("PED001", "São Paulo",
                100.0, Arrays.asList("item1", "item2"));
        when(validador.validar(pedido)).thenReturn(true);
        when(calculadora.calcularFrete("São Paulo", 2)).
                thenReturn(15.0);
        when(servicoEntrega.agendarEntrega(pedido, 115.0)).
                thenReturn("TRACK123");

        // Act
        ResultadoPedido resultado = processador.processarPedido(pedido);

        // Assert
        assertTrue(resultado.isSucesso());
        assertEquals("TRACK123", resultado.getCodigoRastreamento());
        assertEquals(115.0, resultado.getValorTotal(), 0.001);

        // Verificar ordem das chamadas
        InOrder inOrder = inOrder(validador, calculadora, servicoEntrega);
        inOrder.verify(validador).validar(pedido);
        inOrder.verify(calculadora).calcularFrete("São Paulo", 2);
        inOrder.verify(servicoEntrega).agendarEntrega(pedido, 115.0);
    }

    @Test
    void deveRejeitarPedidoInvalido() {
        // Arrange
        Pedido pedido = new Pedido("PED002", "", -10.0, Arrays.asList());
        when(validador.validar(pedido)).thenReturn(false);

        // Act
        ResultadoPedido resultado = processador.processarPedido(pedido);

        // Assert
        assertFalse(resultado.isSucesso());
        assertNull(resultado.getCodigoRastreamento());
        assertEquals(0, resultado.getValorTotal());

        verify(validador).validar(pedido);
        verify(calculadora, never()).calcularFrete(anyString(), anyDouble());
        verify(servicoEntrega, never()).agendarEntrega(any(Pedido.class), anyDouble());
    }
}
