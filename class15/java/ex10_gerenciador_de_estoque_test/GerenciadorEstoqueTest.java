package tdd20ex.ex10_gerenciador_de_estoque;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GerenciadorEstoqueTest {

    @Mock
    private RepositorioProduto repositorio;

    @InjectMocks
    private GerenciadorEstoque gerenciador;

    @Test
    void deveVerificarDisponibilidadeComSucesso() {
        // Arrange
        String codigo = "PROD001";
        Produto produto = new Produto(codigo, "Produto Teste", 10);
        when(repositorio.buscarPorCodigo(codigo)).thenReturn(Optional.of(produto));

        // Act & Assert
        assertTrue(gerenciador.verificarDisponibilidade(codigo, 5));
        assertFalse(gerenciador.verificarDisponibilidade(codigo, 15));
    }

    @Test
    void deveRetornarFalsoParaProdutoInexistente() {
        // Arrange
        when(repositorio.buscarPorCodigo("INEXISTENTE")).thenReturn(Optional.empty());

        // Act & Assert
        assertFalse(gerenciador.verificarDisponibilidade("INEXISTENTE", 1));
    }

    @Test
    void deveReservarItensComSucesso() {
        // Arrange
        String codigo = "PROD001";
        Produto produto = new Produto(codigo, "Produto", 10);
        when(repositorio.buscarPorCodigo(codigo)).thenReturn(Optional.of(produto));

        // Act
        boolean resultado = gerenciador.reservarItens(codigo, 3);

        // Assert
        assertTrue(resultado);
        assertEquals(7, produto.getQuantidade());
        verify(repositorio).atualizar(produto);
    }

    @Test
    void naoDeveReservarMaisItensQueDisponivel() {
        // Arrange
        Produto produto = new Produto("PROD001", "Produto", 5);
        when(repositorio.buscarPorCodigo("PROD001")).thenReturn(Optional.of(produto));

        // Act
        boolean resultado = gerenciador.reservarItens("PROD001", 10);

        // Assert
        assertFalse(resultado);
        assertEquals(5, produto.getQuantidade()); // Não deve alterar
        verify(repositorio, never()).atualizar(any(Produto.class));
    }
}
