package tdd20ex.ex19_sistema_de_workflow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SistemaWorkflowTest {

    @Mock
    private ValidadorEtapa validador;

    @Mock
    private ExecutorTarefa executor;

    @Mock
    private RepositorioWorkflow repositorio;

    @InjectMocks
    private SistemaWorkflow sistema;

    @Test
    void deveExecutarProximaEtapaComSucesso() {
        // Arrange
        Etapa etapa = new Etapa("1", "Primeira Etapa", Map.of());
        Workflow workflow = new Workflow("WF001", Arrays.asList(etapa));

        when(validador.validarEtapa(etapa)).thenReturn(true);
        when(executor.executarEtapa(etapa)).thenReturn(true);

        // Act
        boolean resultado = sistema.executarProximaEtapa(workflow);

        // Assert
        assertTrue(resultado);
        assertEquals(1, workflow.getEtapaAtual());

        InOrder inOrder = inOrder(validador, executor, repositorio);
        inOrder.verify(validador).validarEtapa(etapa);
        inOrder.verify(executor).executarEtapa(etapa);
        inOrder.verify(repositorio).salvarProgresso(workflow);
    }

    @Test
    void deveRetornarTrueParaWorkflowJaCompleto() {
        // Arrange
        Workflow workflow = new Workflow("WF002", Arrays.asList());

        // Act
        boolean resultado = sistema.executarProximaEtapa(workflow);

        // Assert
        assertTrue(resultado);
        verify(validador, never()).validarEtapa(any());
        verify(executor, never()).executarEtapa(any());
        verify(repositorio, never()).salvarProgresso(any());
    }

    @Test
    void deveFalharQuandoValidacaoFalha() {
        // Arrange
        Etapa etapa = new Etapa("1", "Etapa Inválida", Map.of());
        Workflow workflow = new Workflow("WF003", Arrays.asList(etapa));

        when(validador.validarEtapa(etapa)).thenReturn(false);

        // Act
        boolean resultado = sistema.executarProximaEtapa(workflow);

        // Assert
        assertFalse(resultado);
        assertEquals(0, workflow.getEtapaAtual()); // Não deve avançar
        verify(executor, never()).executarEtapa(any());
        verify(repositorio, never()).salvarProgresso(any());
    }

    @Test
    void deveExecutarWorkflowCompletoComSucesso() {
        // Arrange
        Etapa etapa1 = new Etapa("1", "Etapa 1", Map.of());
        Etapa etapa2 = new Etapa("2", "Etapa 2", Map.of());
        Workflow workflow = new Workflow("WF004", Arrays.asList(etapa1, etapa2));

        when(validador.validarEtapa(any())).thenReturn(true);
        when(executor.executarEtapa(any())).thenReturn(true);

        // Act
        boolean resultado = sistema.executarWorkflowCompleto(workflow);

        // Assert
        assertTrue(resultado);
        assertTrue(workflow.isCompleto());
        verify(validador, times(2)).validarEtapa(any());
        verify(executor, times(2)).executarEtapa(any());
        verify(repositorio, times(2)).salvarProgresso(workflow);
    }

    @Test
    void deveInterromperWorkflowQuandoEtapaFalha() {
        // Arrange
        Etapa etapa1 = new Etapa("1", "Etapa 1", Map.of());
        Etapa etapa2 = new Etapa("2", "Etapa 2", Map.of());
        Workflow workflow = new Workflow("WF005", Arrays.asList(etapa1, etapa2));

        when(validador.validarEtapa(etapa1)).thenReturn(true);
        when(executor.executarEtapa(etapa1)).thenReturn(false);

        // Act
        boolean resultado = sistema.executarWorkflowCompleto(workflow);

        // Assert
        assertFalse(resultado);
        assertEquals(0, workflow.getEtapaAtual());
        verify(validador, times(1)).validarEtapa(etapa1);
        verify(executor, times(1)).executarEtapa(etapa1);
        verify(repositorio, never()).salvarProgresso(workflow);
    }
}
