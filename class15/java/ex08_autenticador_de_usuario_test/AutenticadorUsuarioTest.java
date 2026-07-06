package tdd20ex.ex08_autenticador_de_usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticadorUsuarioTest {

    @Mock
    private RepositorioUsuario repositorio;

    @Mock
    private CriptografadorSenha criptografador;

    @InjectMocks
    private AutenticadorUsuario autenticador;

    @Test
    void deveAutenticarUsuarioValido() {
        // Arrange
        String email = "user@test.com";
        String senha = "123456";
        String senhaHash = "hash123";
        Usuario usuario = new Usuario(email, senhaHash);

        when(repositorio.buscarPorEmail(email)).thenReturn(Optional.of(usuario));
        when(criptografador.verificar(senha, senhaHash)).thenReturn(true);

        // Act
        String token = autenticador.autenticar(email, senha);

        // Assert
        assertNotNull(token);
        assertTrue(token.startsWith("TOKEN_" + email));
        verify(repositorio).buscarPorEmail(email);
        verify(criptografador).verificar(senha, senhaHash);
    }

    @Test
    void deveLancarExcecaoParaUsuarioInexistente() {
        // Arrange
        when(repositorio.buscarPorEmail("inexistente@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> autenticador.autenticar("inexistente@test.com", "senha")
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(criptografador, never()).verificar(anyString(), anyString());
    }

    @Test
    void deveLancarExcecaoParaSenhaIncorreta() {
        // Arrange
        String email = "user@test.com";
        Usuario usuario = new Usuario(email, "hash123");

        when(repositorio.buscarPorEmail(email)).thenReturn(Optional.of(usuario));
        when(criptografador.verificar("senhaErrada", "hash123")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> autenticador.autenticar(email, "senhaErrada")
        );

        assertEquals("Senha incorreta", exception.getMessage());
    }
}
