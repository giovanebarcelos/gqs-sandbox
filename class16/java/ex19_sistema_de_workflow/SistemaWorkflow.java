package tdd20ex.ex19_sistema_de_workflow;

public class SistemaWorkflow {
    private ValidadorEtapa validador;
    private ExecutorTarefa executor;
    private RepositorioWorkflow repositorio;

    public SistemaWorkflow(ValidadorEtapa validador, ExecutorTarefa executor, RepositorioWorkflow repositorio) {
        this.validador = validador;
        this.executor = executor;
        this.repositorio = repositorio;
    }

    public boolean executarProximaEtapa(Workflow workflow) {
        if (workflow.isCompleto()) {
            return true;
        }

        Etapa etapaAtual = workflow.getEtapas().get(workflow.getEtapaAtual());

        if (!validador.validarEtapa(etapaAtual)) {
            return false;
        }

        if (!executor.executarEtapa(etapaAtual)) {
            return false;
        }

        workflow.setEtapaAtual(workflow.getEtapaAtual() + 1);
        repositorio.salvarProgresso(workflow);

        return true;
    }

    public boolean executarWorkflowCompleto(Workflow workflow) {
        while (!workflow.isCompleto()) {
            if (!executarProximaEtapa(workflow)) {
                return false;
            }
        }
        return true;
    }
}
