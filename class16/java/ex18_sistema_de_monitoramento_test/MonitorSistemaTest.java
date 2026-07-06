package tdd20ex.ex18_sistema_de_monitoramento;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonitorSistemaTest {

    @Mock
    private ColetorMetricas coletor;

    @Mock
    private VerificadorLimites verificador;

    @Mock
    private AlertaService alertaService;

    @InjectMocks
    private MonitorSistema monitor;

    @Test
    void deveExecutarMonitoramentoSemAlertas() {
        // Arrange
        Metricas metricas = new Metricas(50.0, 60.0, 70.0);
        when(coletor.coletarMetricas()).thenReturn(metricas);
        when(verificador.verificarLimiteCPU(50.0)).thenReturn(true);
        when(verificador.verificarLimiteMemoria(60.0)).thenReturn(true);
        when(verificador.verificarLimiteDisco(70.0)).thenReturn(true);

        // Act
        List<Alerta> alertas = monitor.executarMonitoramento();

        // Assert
        assertTrue(alertas.isEmpty());
        verify(alertaService, never()).enviarAlerta(any(Alerta.class));
    }

    @Test
    void deveGerarAlertaDeCPU() {
        // Arrange
        Metricas metricas = new Metricas(95.0, 50.0, 60.0);
        when(coletor.coletarMetricas()).thenReturn(metricas);
        when(verificador.verificarLimiteCPU(95.0)).thenReturn(false);
        when(verificador.verificarLimiteMemoria(50.0)).thenReturn(true);
        when(verificador.verificarLimiteDisco(60.0)).thenReturn(true);

        // Act
        List<Alerta> alertas = monitor.executarMonitoramento();

        // Assert
        assertEquals(1, alertas.size());
        assertEquals("CPU", alertas.get(0).getTipo());
        verify(alertaService, times(1)).enviarAlerta(any(Alerta.class));
    }

    @Test
    void deveGerarMultiplosAlertas() {
        // Arrange
        Metricas metricas = new Metricas(95.0, 90.0, 85.0);
        when(coletor.coletarMetricas()).thenReturn(metricas);
        when(verificador.verificarLimiteCPU(95.0)).thenReturn(false);
        when(verificador.verificarLimiteMemoria(90.0)).thenReturn(false);
        when(verificador.verificarLimiteDisco(85.0)).thenReturn(false);

        // Act
        List<Alerta> alertas = monitor.executarMonitoramento();

        // Assert
        assertEquals(3, alertas.size());
        verify(alertaService, times(3)).enviarAlerta(any(Alerta.class));

        // Verificar tipos dos alertas
        Set<String> tiposAlertas = alertas.stream()
                .map(Alerta::getTipo)
                .collect(Collectors.toSet());
        assertTrue(tiposAlertas.contains("CPU"));
        assertTrue(tiposAlertas.contains("MEMORIA"));
        assertTrue(tiposAlertas.contains("DISCO"));
    }
}
