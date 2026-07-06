package tdd20ex.ex17_analisador_de_log;

import java.util.List;
import java.util.Map;

class EstatisticasLog {
    private int totalEntradas;
    private Map<String, Integer> contagemPorNivel;
    private List<String> errosMaisFrequentes;

    public EstatisticasLog(int totalEntradas, Map<String, Integer> contagemPorNivel, List<String> errosMaisFrequentes) {
        this.totalEntradas = totalEntradas;
        this.contagemPorNivel = contagemPorNivel;
        this.errosMaisFrequentes = errosMaisFrequentes;
    }

    public int getTotalEntradas() { return totalEntradas; }
    public Map<String, Integer> getContagemPorNivel() { return contagemPorNivel; }
}
