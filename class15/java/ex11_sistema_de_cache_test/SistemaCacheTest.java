package tdd20ex.ex11_sistema_de_cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SistemaCacheTest {

    @Mock
    private Cache cache;

    @Mock
    private RepositorioDados repositorio;

    @InjectMocks
    private SistemaCache sistema;

    @Test
    void deveRetornarDadosDoCache() {
        // Arrange
        String id = "123";
        String dadosCache = "dados do cache";
        when(cache.containsKey("dados_123")).thenReturn(true);
        when(cache.get("dados_123")).thenReturn(dadosCache);

        // Act
        String resultado = sistema.obterDados(id);

        // Assert
        assertEquals(dadosCache, resultado);
        verify(cache).containsKey("dados_123");
        verify(cache).get("dados_123");
        verify(repositorio, never()).buscarDados(anyString());
    }

    @Test
    void deveBuscarNoRepositorioQuandoNaoEstiverNoCache() {
        // Arrange
        String id = "456";
        String dadosRepositorio = "dados do repositorio";
        when(cache.containsKey("dados_456")).thenReturn(false);
        when(repositorio.buscarDados(id)).thenReturn(dadosRepositorio);

        // Act
        String resultado = sistema.obterDados(id);

        // Assert
        assertEquals(dadosRepositorio, resultado);

        InOrder inOrder = inOrder(cache, repositorio);
        inOrder.verify(cache).containsKey("dados_456");
        inOrder.verify(repositorio).buscarDados(id);
        inOrder.verify(cache).put("dados_456", dadosRepositorio);
    }

    @Test
    void naoDeveAdicionarNoCacheQuandoDadosNulos() {
        // Arrange
        when(cache.containsKey(anyString())).thenReturn(false);
        when(repositorio.buscarDados("789")).thenReturn(null);

        // Act
        String resultado = sistema.obterDados("789");

        // Assert
        assertNull(resultado);
        verify(cache, never()).put(anyString(), any());
    }
}
