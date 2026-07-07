/**
 * GQS2001 - Simulação de Métricas de Teste de Carga (estilo JMeter Aggregate Report).
 * Gera amostras sintéticas de tempo de resposta para cenários de requisições e calcula
 * throughput, latência (p50/p95/p99), tempo médio e taxa de erro — sem depender de uma
 * instância real do JMeter ou de acesso à rede.
 *
 * Compilação e execução:
 *   javac GQS2001-SimulacaoCarga.java
 *   java -cp . GQS2001_SimulacaoCarga
 */
import java.util.*;

class AmostraRequisicao {
    double tempoRespostaMs;
    boolean sucesso;

    AmostraRequisicao(double tempoRespostaMs, boolean sucesso) {
        this.tempoRespostaMs = tempoRespostaMs;
        this.sucesso = sucesso;
    }
}

class CenarioCarga {
    String nome;
    int threads;
    int numAmostras;
    double mediaMs;
    double desvioMs;
    double taxaErro;
    long seed;
    List<AmostraRequisicao> amostras = new ArrayList<>();

    CenarioCarga(String nome, int threads, int numAmostras, double mediaMs,
                 double desvioMs, double taxaErro, long seed) {
        this.nome = nome;
        this.threads = threads;
        this.numAmostras = numAmostras;
        this.mediaMs = mediaMs;
        this.desvioMs = desvioMs;
        this.taxaErro = taxaErro;
        this.seed = seed;
    }

    /** Gera as amostras sintéticas (equivalente a "rodar" o Thread Group). */
    void executar() {
        Random rng = new Random(seed);
        for (int i = 0; i < numAmostras; i++) {
            double tempo = Math.max(1.0, mediaMs + rng.nextGaussian() * desvioMs);
            boolean sucesso = rng.nextDouble() >= taxaErro;
            amostras.add(new AmostraRequisicao(tempo, sucesso));
        }
    }
}

class EstatisticasCenario {
    String cenario;
    int amostras;
    double tempoMedioMs;
    double tempoMinMs;
    double tempoMaxMs;
    double p50Ms;
    double p95Ms;
    double p99Ms;
    double erroPct;
    double throughputRps;
}

class GQS2001_SimulacaoCarga {

    static double calcularPercentil(List<Double> valores, double percentil) {
        if (valores.isEmpty()) return 0.0;
        List<Double> ordenados = new ArrayList<>(valores);
        Collections.sort(ordenados);
        int idx = (int) Math.round((percentil / 100.0) * (ordenados.size() - 1));
        idx = Math.min(Math.max(idx, 0), ordenados.size() - 1);
        return arredondar(ordenados.get(idx));
    }

    static double arredondar(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    static EstatisticasCenario calcularEstatisticas(CenarioCarga cenario) {
        List<Double> tempos = new ArrayList<>();
        int numErros = 0;
        double soma = 0.0;
        for (AmostraRequisicao a : cenario.amostras) {
            tempos.add(a.tempoRespostaMs);
            soma += a.tempoRespostaMs;
            if (!a.sucesso) numErros++;
        }

        // Duração total aproximada do cenário: soma dos tempos dividida pelos
        // usuários virtuais simultâneos (modelo simplificado para fins didáticos).
        double duracaoTotalS = (soma / 1000.0) / Math.max(cenario.threads, 1);
        double throughput = duracaoTotalS > 0 ? arredondar(cenario.numAmostras / duracaoTotalS) : 0.0;

        EstatisticasCenario stats = new EstatisticasCenario();
        stats.cenario = cenario.nome;
        stats.amostras = cenario.numAmostras;
        stats.tempoMedioMs = arredondar(soma / cenario.numAmostras);
        stats.tempoMinMs = arredondar(Collections.min(tempos));
        stats.tempoMaxMs = arredondar(Collections.max(tempos));
        stats.p50Ms = calcularPercentil(tempos, 50);
        stats.p95Ms = calcularPercentil(tempos, 95);
        stats.p99Ms = calcularPercentil(tempos, 99);
        stats.erroPct = arredondar(100.0 * numErros / cenario.numAmostras);
        stats.throughputRps = throughput;
        return stats;
    }

    static String classificarResultado(EstatisticasCenario s) {
        if (s.erroPct > 5 || s.p95Ms > 5000) return "CRÍTICO";
        if (s.erroPct > 1 || s.p95Ms > 2000) return "ATENÇÃO";
        return "OK";
    }

    static void imprimirRelatorio(List<EstatisticasCenario> lista) {
        String cabecalho = String.format("%-20s%10s%12s%10s%10s%10s%8s%8s%10s",
                "Cenário", "Amostras", "Média(ms)", "P50(ms)", "P95(ms)", "P99(ms)",
                "Erro%", "RPS", "Status");
        System.out.println(cabecalho);
        System.out.println("-".repeat(cabecalho.length()));
        for (EstatisticasCenario s : lista) {
            String status = classificarResultado(s);
            System.out.printf("%-20s%10d%12.2f%10.2f%10.2f%10.2f%8.2f%8.2f%10s%n",
                    s.cenario, s.amostras, s.tempoMedioMs, s.p50Ms, s.p95Ms, s.p99Ms,
                    s.erroPct, s.throughputRps, status);
        }
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(78));
        System.out.println("  GQS2001 - SIMULAÇÃO DE MÉTRICAS DE TESTE DE CARGA (estilo JMeter)");
        System.out.println("=".repeat(78));

        List<CenarioCarga> cenarios = List.of(
                new CenarioCarga("Consulta CEP", 50, 500, 250, 60, 0.01, 1),
                new CenarioCarga("Login", 100, 800, 400, 150, 0.03, 2),
                new CenarioCarga("Checkout (stress)", 300, 1200, 1800, 900, 0.08, 3)
        );

        List<EstatisticasCenario> resultados = new ArrayList<>();
        for (CenarioCarga cenario : cenarios) {
            cenario.executar();
            resultados.add(calcularEstatisticas(cenario));
        }

        imprimirRelatorio(resultados);

        System.out.println("\n--- Interpretação ---");
        for (EstatisticasCenario s : resultados) {
            String status = classificarResultado(s);
            System.out.printf("[%s] %s: P95=%.2fms, erro=%.2f%%, throughput=%.2f req/s%n",
                    status, s.cenario, s.p95Ms, s.erroPct, s.throughputRps);
        }
    }
}
