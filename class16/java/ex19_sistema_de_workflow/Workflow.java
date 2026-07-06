package tdd20ex.ex19_sistema_de_workflow;

import java.util.List;

class Workflow {
    private String id;
    private List<Etapa> etapas;
    private int etapaAtual;

    public Workflow(String id, List<Etapa> etapas) {
        this.id = id;
        this.etapas = etapas;
        this.etapaAtual = 0;
    }

    public String getId() { return id; }
    public List<Etapa> getEtapas() { return etapas; }
    public int getEtapaAtual() { return etapaAtual; }
    public void setEtapaAtual(int etapaAtual) { this.etapaAtual = etapaAtual; }

    public boolean isCompleto() {
        return etapaAtual >= etapas.size();
    }
}
