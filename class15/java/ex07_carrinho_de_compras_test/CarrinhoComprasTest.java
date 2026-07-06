package tdd20ex.ex07_carrinho_de_compras;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarrinhoComprasTest {

    @Mock
    private CalculadoraDesconto calculadora;

    @InjectMocks
    private CarrinhoCompras carrinho;

    @Test
    void deveCalcularTotalSemDesconto() {
        carrinho.adicionarItem(new Item("Produto A", 100.0));
        carrinho.adicionarItem(new Item("Produto B", 50.0));

        assertEquals(150.0, carrinho.calcularTotal(), 0.001);
    }

    @Test
    void deveCalcularTotalComDesconto() {
        // Arrange
        carrinho.adicionarItem(new Item("Produto A", 100.0));
        when(calculadora.calcularDesconto(100.0, "VIP")).thenReturn(10.0);

        // Act
        double resultado = carrinho.calcularTotalComDesconto("VIP");

        // Assert
        assertEquals(90.0, resultado, 0.001);
        verify(calculadora).calcularDesconto(100.0, "VIP");
    }

    @Test
    void deveContarItensCorretamente() {
        assertEquals(0, carrinho.getQuantidadeItens());

        carrinho.adicionarItem(new Item("Item 1", 10.0));
        assertEquals(1, carrinho.getQuantidadeItens());

        carrinho.adicionarItem(new Item("Item 2", 20.0));
        assertEquals(2, carrinho.getQuantidadeItens());
    }
}
