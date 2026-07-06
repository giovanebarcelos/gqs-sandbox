package tdd20ex.ex16_sistema_de_reservas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SistemaReservasTest {

    @Mock
    private RepositorioQuarto repositorioQuarto;

    @Mock
    private ProcessadorPagamento processadorPagamento;

    @Mock
    private ServicoNotificacao servicoNotificacao;

    @InjectMocks
    private SistemaReservas sistema;

    @Test
    void deveCriarReservaComSucesso() {
        // Arrange
        String numeroQuarto = "101";
        String cliente = "João Silva";
        int dias = 3;
        String cartao = "1234-5678";

        Quarto quarto = new Quarto(numeroQuarto, true, 100.0);
        when(repositorioQuarto.buscarPorNumero(numeroQuarto)).thenReturn(Optional.of(quarto));
        when(processadorPagamento.processarPagamento(cartao, 300.0)).thenReturn(true);

        // Act
        Reserva reserva = sistema.criarReserva(numeroQuarto, cliente, dias, cartao);

        // Assert
        assertNotNull(reserva);
        assertEquals(numeroQuarto, reserva.getQuartoNumero());
        assertEquals(cliente, reserva.getCliente());
        assertEquals(300.0, reserva.getValorTotal());
        assertFalse(quarto.isDisponivel());

        InOrder inOrder = inOrder(repositorioQuarto, processadorPagamento, servicoNotificacao);
        inOrder.verify(repositorioQuarto).buscarPorNumero(numeroQuarto);
        inOrder.verify(processadorPagamento).processarPagamento(cartao, 300.0);
        inOrder.verify(repositorioQuarto).atualizar(quarto);
        inOrder.verify(servicoNotificacao).enviarConfirmacao(cliente, reserva);
    }

    @Test
    void deveLancarExcecaoParaQuartoInexistente() {
        // Arrange
        when(repositorioQuarto.buscarPorNumero("999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> sistema.criarReserva("999", "Cliente", 2, "1234")
        );

        assertEquals("Quarto não encontrado", exception.getMessage());
        verify(processadorPagamento, never()).processarPagamento(anyString(), anyDouble());
    }

    @Test
    void deveLancarExcecaoParaQuartoIndisponivel() {
        // Arrange
        Quarto quarto = new Quarto("101", false, 100.0);
        when(repositorioQuarto.buscarPorNumero("101")).thenReturn(Optional.of(quarto));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> sistema.criarReserva("101", "Cliente", 2, "1234")
        );

        assertEquals("Quarto não disponível", exception.getMessage());
        verify(processadorPagamento, never()).processarPagamento(anyString(), anyDouble());
    }

    @Test
    void deveLancarExcecaoQuandoPagamentoFalha() {
        // Arrange
        Quarto quarto = new Quarto("101", true, 100.0);
        when(repositorioQuarto.buscarPorNumero("101")).thenReturn(Optional.of(quarto));
        when(processadorPagamento.processarPagamento("1234", 200.0)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> sistema.criarReserva("101", "Cliente", 2, "1234")
        );

        assertEquals("Falha no pagamento", exception.getMessage());
        assertTrue(quarto.isDisponivel()); // Quarto deve permanecer disponível
        verify(repositorioQuarto, never()).atualizar(any(Quarto.class));
        verify(servicoNotificacao, never()).enviarConfirmacao(anyString(), any(Reserva.class));
    }
}
