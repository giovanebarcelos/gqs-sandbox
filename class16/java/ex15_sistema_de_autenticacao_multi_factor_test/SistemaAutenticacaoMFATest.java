package tdd20ex.ex15_sistema_de_autenticacao_multi_factor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SistemaAutenticacaoMFATest {

    @Mock
    private ValidadorCredenciais validador;

    @Mock
    private GeradorTokenMFA geradorToken;

    @Mock
    private ServicoSMS servicoSMS;

    @InjectMocks
    private SistemaAutenticacaoMFA sistema;

    @Test
    void deveAutenticarComSucesso() {
        // Arrange
        CredenciaisUsuario creds = new CredenciaisUsuario("user123", "senha123");
        when(validador.validarCredenciais(creds)).thenReturn(true);
        when(validador.obterTelefone("user123")).thenReturn("11999999999");
        when(geradorToken.gerarToken()).thenReturn("123456");
        when(servicoSMS.enviarSMS("11999999999", "Seu código MFA: 123456")).thenReturn(true);

        // Act
        ResultadoAutenticacao resultado = sistema.autenticar(creds);

        // Assert
        assertTrue(resultado.isSucesso());
        assertEquals("123456", resultado.getTokenMFA());
        assertEquals("Token enviado", resultado.getMensagem());

        InOrder inOrder = inOrder(validador, geradorToken, servicoSMS);
        inOrder.verify(validador).validarCredenciais(creds);
        inOrder.verify(validador).obterTelefone("user123");
        inOrder.verify(geradorToken).gerarToken();
        inOrder.verify(servicoSMS).enviarSMS("11999999999", "Seu código MFA: 123456");
    }

    @Test
    void deveRejeitarCredenciaisInvalidas() {
        // Arrange
        CredenciaisUsuario creds = new CredenciaisUsuario("userInvalido", "senhaErrada");
        when(validador.validarCredenciais(creds)).thenReturn(false);

        // Act
        ResultadoAutenticacao resultado = sistema.autenticar(creds);

        // Assert
        assertFalse(resultado.isSucesso());
        assertNull(resultado.getTokenMFA());
        assertEquals("Credenciais inválidas", resultado.getMensagem());

        verify(validador).validarCredenciais(creds);
        verify(validador, never()).obterTelefone(anyString());
        verify(geradorToken, never()).gerarToken();
        verify(servicoSMS, never()).enviarSMS(anyString(), anyString());
    }

    @Test
    void deveRejeitarUsuarioSemTelefone() {
        // Arrange
        CredenciaisUsuario creds = new CredenciaisUsuario("userSemTel", "senha123");
        when(validador.validarCredenciais(creds)).thenReturn(true);
        when(validador.obterTelefone("userSemTel")).thenReturn(null);

        // Act
        ResultadoAutenticacao resultado = sistema.autenticar(creds);

        // Assert
        assertFalse(resultado.isSucesso());
        assertEquals("Telefone não cadastrado", resultado.getMensagem());
        verify(geradorToken, never()).gerarToken();
    }

    @Test
    void deveTratarFalhaNoEnvioSMS() {
        // Arrange
        CredenciaisUsuario creds = new CredenciaisUsuario("user123", "senha123");
        when(validador.validarCredenciais(creds)).thenReturn(true);
        when(validador.obterTelefone("user123")).thenReturn("11999999999");
        when(geradorToken.gerarToken()).thenReturn("123456");
        when(servicoSMS.enviarSMS(anyString(), anyString())).thenReturn(false);

        // Act
        ResultadoAutenticacao resultado = sistema.autenticar(creds);

        // Assert
        assertFalse(resultado.isSucesso());
        assertEquals("Falha ao enviar SMS", resultado.getMensagem());
    }
}
