package tdd20ex.ex13_sistema_de_backup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SistemaBackupTest {

    @Mock
    private ArmazenamentoLocal local;

    @Mock
    private ArmazenamentoNuvem nuvem;

    @InjectMocks
    private SistemaBackup sistema;

    @Test
    void deveUsarBackupLocal() {
        // Arrange
        String arquivo = "dados.txt";
        byte[] dados = "conteudo".getBytes();
        when(local.estaDisponivel()).thenReturn(true);
        when(local.salvar(arquivo, dados)).thenReturn(true);

        // Act
        String resultado = sistema.realizarBackup(arquivo, dados);

        // Assert
        assertEquals("BACKUP_LOCAL_dados.txt", resultado);
        verify(local).estaDisponivel();
        verify(local).salvar(arquivo, dados);
        verify(nuvem, never()).upload(anyString(), any(byte[].class));
    }

    @Test
    void deveUsarBackupNuvemQuandoLocalIndisponivel() {
        // Arrange
        String arquivo = "dados.txt";
        byte[] dados = "conteudo".getBytes();
        when(local.estaDisponivel()).thenReturn(false);
        when(nuvem.upload(arquivo, dados)).thenReturn(true);

        // Act
        String resultado = sistema.realizarBackup(arquivo, dados);

        // Assert
        assertEquals("BACKUP_NUVEM_dados.txt", resultado);
        verify(local).estaDisponivel();
        verify(local, never()).salvar(anyString(), any(byte[].class));
        verify(nuvem).upload(arquivo, dados);
    }

    @Test
    void deveUsarBackupNuvemQuandoLocalFalha() {
        // Arrange
        String arquivo = "dados.txt";
        byte[] dados = "conteudo".getBytes();
        when(local.estaDisponivel()).thenReturn(true);
        when(local.salvar(arquivo, dados)).thenReturn(false);
        when(nuvem.upload(arquivo, dados)).thenReturn(true);

        // Act
        String resultado = sistema.realizarBackup(arquivo, dados);

        // Assert
        assertEquals("BACKUP_NUVEM_dados.txt", resultado);
        verify(local).salvar(arquivo, dados);
        verify(nuvem).upload(arquivo, dados);
    }

    @Test
    void deveLancarExcecaoQuandoTodosBackupsFalham() {
        // Arrange
        when(local.estaDisponivel()).thenReturn(true);
        when(local.salvar(anyString(), any(byte[].class))).thenReturn(false);
        when(nuvem.upload(anyString(), any(byte[].class))).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> sistema.realizarBackup("arquivo.txt", "dados".getBytes())
        );

        assertEquals("Falha em todos os backups", exception.getMessage());
    }
}
