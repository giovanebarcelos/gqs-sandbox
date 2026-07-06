package tdd20ex.ex18_sistema_de_monitoramento;

import java.util.ArrayList;
import java.util.List;

public class MonitorSistema {
    private ColetorMetricas coletor;
    private VerificadorLimites verificador;
    private AlertaService alertaService;

    public MonitorSistema(ColetorMetricas coletor, VerificadorLimites verificador, AlertaService alertaService) {
        this.coletor = coletor;
        this.verificador = verificador;
        this.alertaService = alertaService;
    }

    public List<Alerta> executarMonitoramento() {
        Metricas metricas = coletor.coletarMetricas();
        List<Alerta> alertas = new ArrayList<>();

        if (!verificador.verificarLimiteCPU(metricas.getCpu())) {
            Alerta alerta = new Alerta("CPU", "CPU acima do limite", metricas.getCpu());
            alertas.add(alerta);
            alertaService.enviarAlerta(alerta);
        }

        if (!verificador.verificarLimiteMemoria(metricas.getMemoria())) {
            Alerta alerta = new Alerta("MEMORIA", "Memória acima do limite", metricas.getMemoria());
            alertas.add(alerta);
            alertaService.enviarAlerta(alerta);
        }

        if (!verificador.verificarLimiteDisco(metricas.getDisco())) {
            Alerta alerta = new Alerta("DISCO", "Disco acima do limite", metricas.getDisco());
            alertas.add(alerta);
            alertaService.enviarAlerta(alerta);
        }

        return alertas;
    }
}
