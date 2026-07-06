package tdd20ex.ex20_sistema_de_processamento_de_arquivos_integracao_complexa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessadorArquivosTest {

    @Mock
    private ValidadorArquivo validador;

    @Mock
    private ConversorFormato conversor;

    @Mock
    private CompactadorArquivo compactador;

    @Mock
    private ServicoUpload servicoUpload;

    @InjectMocks
    private ProcessadorArquivos processador;

    @Test
    void deveProcessarArquivoCompletoComSucesso() {
        // Arrange
        byte[] conteudo = "conteudo do arquivo".getBytes();
        Arquivo arquivo = new Arquivo("documento.txt", "TXT", conteudo);
        Arquivo arquivoConvertido = new Arquivo("documento.pdf", "PDF", "pdf_content".getBytes());
        Arquivo arquivoCompactado = new Arquivo("documento.pdf", "PDF", "compressed".getBytes());

        when(validador.validarTamanho(arquivo, 1000000)).thenReturn(true);
        when(validador.validarTipo(eq(arquivo), any())).thenReturn(true);
        when(conversor.converterPara(arquivo, "PDF")).thenReturn(arquivoConvertido);
        when(compactador.compactar(arquivoConvertido)).thenReturn(arquivoCompactado);
        when(servicoUpload.fazerUpload(arquivoCompactado)).thenReturn("https://storage.com/file123");

        // Act
        ResultadoProcessamento resultado = processador.processarArquivo(arquivo, "PDF", true, 1000000);

        // Assert
        assertTrue(resultado.isSucesso());
        assertEquals("https://storage.com/file123", resultado.getUrlFinal());
        assertEquals(conteudo.length, resultado.getTamanhoOriginal());
        assertEquals(arquivoCompactado.getTamanho(), resultado.getTamanhoFinal());

        InOrder inOrder = inOrder(validador, conversor, compactador, servicoUpload);
        inOrder.verify(validador).validarTamanho(arquivo, 1000000);
        inOrder.verify(validador).validarTipo(eq(arquivo), any());
        inOrder.verify(conversor).converterPara(arquivo, "PDF");
        inOrder.verify(compactador).compactar(arquivoConvertido);
        inOrder.verify(servicoUpload).fazerUpload(arquivoCompactado);
    }

    @Test
    void deveProcessarSemConversaoNemCompactacao() {
        // Arrange
        Arquivo arquivo = new Arquivo("documento.pdf", "PDF", "content".getBytes());
        when(validador.validarTamanho(arquivo, 1000000)).thenReturn(true);
        when(validador.validarTipo(eq(arquivo), any())).thenReturn(true);
        when(servicoUpload.fazerUpload(arquivo)).thenReturn("https://storage.com/file456");

        // Act
        ResultadoProcessamento resultado = processador.processarArquivo(arquivo, "PDF", false, 1000000);

        // Assert
        assertTrue(resultado.isSucesso());
        assertEquals("https://storage.com/file456", resultado.getUrlFinal());

        verify(conversor, never()).converterPara(any(), anyString());
        verify(compactador, never()).compactar(any());
        verify(servicoUpload).fazerUpload(arquivo);
    }

    @Test
    void deveRejeitarArquivoMuitoGrande() {
        // Arrange
        Arquivo arquivo = new Arquivo("arquivo_grande.txt", "TXT", new byte[1000]);
        when(validador.validarTamanho(arquivo, 500)).thenReturn(false);

        // Act
        ResultadoProcessamento resultado = processador.processarArquivo(arquivo, "PDF", true, 500);

        // Assert
        assertFalse(resultado.isSucesso());
        assertEquals("Arquivo muito grande", resultado.getMensagem());
        verify(validador).validarTamanho(arquivo, 500);
        verify(validador, never()).validarTipo(any(), any());
        verify(conversor, never()).converterPara(any(), anyString());
    }

    @Test
    void deveRejeitarTipoNaoPermitido() {
        // Arrange
        Arquivo arquivo = new Arquivo("virus.exe", "EXE", "malware".getBytes());
        when(validador.validarTamanho(arquivo, 1000000)).thenReturn(true);
        when(validador.validarTipo(eq(arquivo), any())).thenReturn(false);

        // Act
        ResultadoProcessamento resultado = processador.processarArquivo(arquivo, "EXE", false, 1000000);

        // Assert
        assertFalse(resultado.isSucesso());
        assertEquals("Tipo de arquivo não permitido", resultado.getMensagem());
        verify(conversor, never()).converterPara(any(), anyString());
        verify(servicoUpload, never()).fazerUpload(any());
    }

    @Test
    void deveTratarExcecaoNoProcessamento() {
        // Arrange
        Arquivo arquivo = new Arquivo("documento.txt", "TXT", "content".getBytes());
        when(validador.validarTamanho(arquivo, 1000000)).thenReturn(true);
        when(validador.validarTipo(eq(arquivo), any())).thenReturn(true);
        when(conversor.converterPara(arquivo, "PDF")).thenThrow(new RuntimeException("Erro na conversão"));

        // Act
        ResultadoProcessamento resultado = processador.processarArquivo(arquivo, "PDF", false, 1000000);

        // Assert
        assertFalse(resultado.isSucesso());
        assertTrue(resultado.getMensagem().contains("Erro no processamento"));
        verify(compactador, never()).compactar(any());
        verify(servicoUpload, never()).fazerUpload(any());
    }

    @Test
    void deveUsarArgumentCaptorParaVerificarParametros() {
        // Arrange
        Arquivo arquivo = new Arquivo("test.txt", "TXT", "test".getBytes());
        when(validador.validarTamanho(any(), anyLong())).thenReturn(true);
        when(validador.validarTipo(any(), any())).thenReturn(true);
        when(servicoUpload.fazerUpload(arquivo)).thenReturn("url");

        // Act
        processador.processarArquivo(arquivo, "TXT", false, 1000000);

        // Assert
        ArgumentCaptor<List<String>> tiposCaptor = ArgumentCaptor.forClass(List.class);
        verify(validador).validarTipo(eq(arquivo), tiposCaptor.capture());

        List<String> tiposPermitidos = tiposCaptor.getValue();
        assertTrue(tiposPermitidos.contains("PDF"));
        assertTrue(tiposPermitidos.contains("TXT"));
        assertTrue(tiposPermitidos.contains("JPG"));
    }
}
