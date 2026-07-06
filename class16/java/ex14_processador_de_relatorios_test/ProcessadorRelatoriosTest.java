package tdd20ex.ex14_processador_de_relatorios;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessadorRelatoriosTest {

    @Mock
    private ColetorDados coletor;

    @Mock
    private FormatadorRelatorio formatador;

    @Mock
    private ServicoEmail servicoEmail;

    @Spy
    @InjectMocks
    private ProcessadorRelatorios processador;

    @Test
    void deveGerarRelatorioHTMLComSucesso() {
        // Arrange
        String periodo = "2024-01";
        String destinatario = "admin@empresa.com";
        Map<String, Object> dados = Map.of("vendas", 1000, "clientes", 50);
        String relatorioHTML = "<html>Relatório</html>";

        when(coletor.coletarDados(periodo)).thenReturn(dados);
        when(formatador.formatarHTML(dados)).thenReturn(relatorioHTML);
        when(servicoEmail.enviarRelatorio(destinatario, relatorioHTML, "HTML")).thenReturn(true);

        // Act
        boolean resultado = processador.gerarEEnviarRelatorio(periodo, destinatario, "HTML");

        // Assert
        assertTrue(resultado);
        verify(coletor).coletarDados(periodo);
        verify(formatador).formatarHTML(dados);
        verify(servicoEmail).enviarRelatorio(destinatario, relatorioHTML, "HTML");
    }

    @Test
    void deveGerarRelatorioPDF() {
        // Arrange
        Map<String, Object> dados = Map.of("vendas", 1000);
        String relatorioPDF = "PDF_CONTENT";

        when(coletor.coletarDados("2024-01")).thenReturn(dados);
        when(formatador.formatarPDF(dados)).thenReturn(relatorioPDF);
        when(servicoEmail.enviarRelatorio(anyString(), eq(relatorioPDF), eq("PDF"))).thenReturn(true);

        // Act
        boolean resultado = processador.gerarEEnviarRelatorio("2024-01", "user@test.com", "PDF");

        // Assert
        assertTrue(resultado);
        verify(formatador).formatarPDF(dados);
        verify(formatador, never()).formatarHTML(any());
    }

    @Test
    void deveRetornarFalsoQuandoDadosVazios() {
        // Arrange
        when(coletor.coletarDados(anyString())).thenReturn(Map.of());

        // Act
        boolean resultado = processador.gerarEEnviarRelatorio("2024-01", "user@test.com", "HTML");

        // Assert
        assertFalse(resultado);
        verify(formatador, never()).formatarHTML(any());
        verify(servicoEmail, never()).enviarRelatorio(anyString(), anyString(), anyString());
    }

    @Test
    void deveTratarExcecaoNaColeta() {
        // Arrange
        when(coletor.coletarDados(anyString())).thenThrow(new RuntimeException("Erro na coleta"));

        // Act
        boolean resultado = processador.gerarEEnviarRelatorio("2024-01", "user@test.com", "HTML");

        // Assert
        assertFalse(resultado);
    }

    @Test
    void deveUsarSpyParaTestarFormatacao() {
        // Arrange
        Map<String, Object> dados = Map.of("test", "data");

        // Act
        String resultado = processador.formatarRelatorio(dados, "PDF");

        // Assert - usando spy para verificar chamada do método protegido
        verify(processador).formatarRelatorio(dados, "PDF");
        verify(formatador).formatarPDF(dados);
    }
}
