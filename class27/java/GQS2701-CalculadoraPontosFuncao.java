import java.util.HashMap;
import java.util.Map;

/**
 * GQS2701 - Calculadora de Pontos de Função e Estimativa COCOMO.
 * Calcula PF Não Ajustado, VAF, PF Ajustado, KLOC e esforço/prazo (COCOMO Básico).
 *
 * Compilação e execução:
 *   javac GQS2701-CalculadoraPontosFuncao.java
 *   java -cp . GQS2701_CalculadoraPontosFuncao
 */
class GQS2701_CalculadoraPontosFuncao {

    static Map<String, Map<String, Integer>> pesosDados = new HashMap<>();
    static Map<String, Map<String, Integer>> pesosTransacao = new HashMap<>();
    static Map<String, Integer> locPorPf = new HashMap<>();

    static {
        pesosDados.put("ILF", Map.of("baixa", 7, "media", 10, "alta", 15));
        pesosDados.put("EIF", Map.of("baixa", 5, "media", 7, "alta", 10));
        pesosTransacao.put("EI", Map.of("baixa", 3, "media", 4, "alta", 6));
        pesosTransacao.put("EO", Map.of("baixa", 4, "media", 5, "alta", 7));
        pesosTransacao.put("EQ", Map.of("baixa", 3, "media", 4, "alta", 6));
        locPorPf.put("java", 53);
        locPorPf.put("python", 42);
        locPorPf.put("c", 128);
        locPorPf.put("assembly", 320);
    }

    static int pesoFuncao(String tipo, String complexidade) {
        Map<String, Integer> tabela = pesosDados.containsKey(tipo) ? pesosDados.get(tipo) : pesosTransacao.get(tipo);
        if (tabela == null) throw new IllegalArgumentException("tipo de função desconhecido: " + tipo);
        return tabela.get(complexidade);
    }

    static int calcularPfna(String[][] funcoes) {
        int total = 0;
        for (String[] f : funcoes) total += pesoFuncao(f[0], f[1]);
        return total;
    }

    static double calcularVaf(int tdi) {
        if (tdi < 0 || tdi > 70) throw new IllegalArgumentException("TDI deve estar entre 0 e 70");
        return round(0.65 + (tdi * 0.01), 4);
    }

    static double calcularPfAjustado(int pfna, double vaf) {
        return round(pfna * vaf, 2);
    }

    static double pfParaKloc(double pfAjustado, String linguagem) {
        int locPf = locPorPf.get(linguagem.toLowerCase());
        return round((pfAjustado * locPf) / 1000.0, 4);
    }

    static double cocomoEsforco(double kloc) {
        return round(2.4 * Math.pow(kloc, 1.05), 2);
    }

    static double cocomoPrazo(double esforcoPessoasMes) {
        return round(2.5 * Math.pow(esforcoPessoasMes, 0.38), 2);
    }

    static double aderenciaPercentual(double real, double estimado) {
        if (estimado == 0) return 0.0;
        return round((real - estimado) / estimado * 100, 2);
    }

    static double round(double valor, int casas) {
        double fator = Math.pow(10, casas);
        return Math.round(valor * fator) / fator;
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(64));
        System.out.println("  GQS2701 - CALCULADORA DE PONTOS DE FUNÇÃO E COCOMO");
        System.out.println("=".repeat(64));

        System.out.println("\n--- Exemplo: Sistema de Cadastro de Clientes (Aula 27) ---");
        String[][] funcoes = {
            {"ILF", "baixa"},   // Tabela Clientes
            {"EIF", "baixa"},   // Tabela de CEP (externa)
            {"EI", "baixa"},    // Cadastrar Cliente
            {"EI", "baixa"},    // Atualizar Cliente
            {"EO", "media"},    // Relatório de Clientes Inadimplentes
            {"EQ", "baixa"},    // Consultar Cliente por CPF
        };
        int pfna = calcularPfna(funcoes);
        System.out.println("  PF Não Ajustado (PFNA) = " + pfna + " PF");

        int tdi = 30;
        double vaf = calcularVaf(tdi);
        double pfAjustado = calcularPfAjustado(pfna, vaf);
        System.out.println("  TDI = " + tdi + " (exemplo)");
        System.out.println("  VAF = " + vaf);
        System.out.println("  PF Ajustado = " + pfAjustado + " PF");

        String linguagem = "java";
        double kloc = pfParaKloc(pfAjustado, linguagem);
        System.out.println("\n  KLOC estimado (" + linguagem + ") = " + kloc + " KLOC");

        double esforco = cocomoEsforco(kloc);
        double prazo = cocomoPrazo(esforco);
        double equipe = prazo != 0 ? round(esforco / prazo, 2) : 0;
        System.out.println("  Esforço (COCOMO Básico) = " + esforco + " pessoas-mês");
        System.out.println("  Prazo (COCOMO Básico)   = " + prazo + " meses");
        System.out.println("  Equipe média            = " + equipe + " pessoas");

        System.out.println("\n--- Aderência de um projeto real (exemplo) ---");
        double esforcoReal = 3.7;
        double aderencia = aderenciaPercentual(esforcoReal, esforco);
        String sinal = aderencia > 0 ? "acima" : "abaixo";
        System.out.println("  Esforço real = " + esforcoReal + " pessoas-mês");
        System.out.println("  Aderência a esforço = " + aderencia + "% (" + sinal + " do estimado)");
    }
}
