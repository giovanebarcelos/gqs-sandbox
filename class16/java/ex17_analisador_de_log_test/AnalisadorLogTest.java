package tdd20ex.ex17_analisador_de_log;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalisadorLogTest {

    @Mock
    private LeitorArquivo leitor;

    @Mock
    private ProcessadorEstatisticas processador;

    @InjectMocks
    private AnalisadorLog analisador;

    @Test
    void deveAnalisarArquivoComSucesso() {
        // Arrange
        String caminho = "/logs/app.log";
        List<String> linhas = Arrays.asList(
                "2024-01-01 INFO Aplicacao iniciada",
                "2024-01-01 ERROR Erro de conexao",
                "2024-01-01 WARN Cache invalidado"
        );

        EstatisticasLog estatisticas = new EstatisticasLog(3,
                Map.of("INFO", 1, "ERROR", 1, "WARN", 1),
                Arrays.asList("Erro de conexao"));

        when(leitor.lerLinhas(caminho)).thenReturn(linhas);
        when(processador.calcularEstatisticas(any())).thenReturn(estatisticas);

        // Act
        EstatisticasLog resultado = analisador.analisarArquivo(caminho);

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.getTotalEntradas());
        verify(leitor).lerLinhas(caminho);
        verify(processador).calcularEstatisticas(any());
    }

    @Test
    void deveLancarExcecaoParaArquivoVazio() {
        // Arrange
        when(leitor.lerLinhas(anyString())).thenReturn(Arrays.asList());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> analisador.analisarArquivo("/logs/vazio.log")
        );

        assertEquals("Arquivo vazio ou não encontrado", exception.getMessage());
        verify(processador, never()).calcularEstatisticas(any());
    }

    @Test
    void deveFiltrarLinhasInvalidas() {
        // Arrange
        List<String> linhas = Arrays.asList(
                "2024-01-01 INFO Linha valida",
                "linha_invalida",
                "2024-01-01 ERROR Outra linha valida"
        );

        when(leitor.lerLinhas(anyString())).thenReturn(linhas);
        when(processador.calcularEstatisticas(any())).thenReturn(
                new EstatisticasLog(2, Map.of(), Arrays.asList()));

        // Act
        analisador.analisarArquivo("/test.log");

        // Assert
        ArgumentCaptor<List<EntradaLog>> captor = ArgumentCaptor.forClass(List.class);
        verify(processador).calcularEstatisticas(captor.capture());

        List<EntradaLog> logsProcessados = captor.getValue();
        assertEquals(2, logsProcessados.size()); // Apenas linhas válidas
    }
}
