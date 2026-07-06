package tdd20ex.ex06_sistema_de_notificacao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SistemaNotificacaoTest {

    @Mock
    private ServicoEmail servicoEmail;

    @InjectMocks
    private SistemaNotificacao sistema;

    @Test
    void deveEnviarNotificacaoComSucesso() {
        // Arrange
        String usuario = "user@test.com";
        String mensagem = "Teste de notificação";
        when(servicoEmail.enviarEmail(anyString(), anyString(), anyString())).thenReturn(true);

        // Act
        boolean resultado = sistema.enviarNotificacao(usuario, mensagem);

        // Assert
        assertTrue(resultado);
        verify(servicoEmail).enviarEmail(usuario, "Notificação Importante", mensagem);
    }

    @Test
    void naoDeveEnviarNotificacaoComParametrosNulos() {
        // Act & Assert
        assertFalse(sistema.enviarNotificacao(null, "mensagem"));
        assertFalse(sistema.enviarNotificacao("user@test.com", null));

        // Verificar que email não foi enviado
        verify(servicoEmail, never()).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveRetornarFalsoQuandoEmailFalha() {
        // Arrange
        when(servicoEmail.enviarEmail(anyString(), anyString(), anyString())).thenReturn(false);

        // Act
        boolean resultado = sistema.enviarNotificacao("user@test.com", "mensagem");

        // Assert
        assertFalse(resultado);
    }
}
