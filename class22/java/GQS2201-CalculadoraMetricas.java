/**
 * GQS2201 - Calculadora de Métricas de Software.
 * Calcula MTBF, MTTR, Disponibilidade e Densidade de Defeitos.
 *
 * Compilação e execução:
 *   javac GQS2201-CalculadoraMetricas.java
 *   java -cp . GQS2201_CalculadoraMetricas
 */
import java.util.*;

class GQS2201_CalculadoraMetricas {

    static double calcularMTBF(double tempoTotalH, int numFalhas) {
        if (numFalhas == 0) return Double.POSITIVE_INFINITY;
        return Math.round(tempoTotalH / numFalhas * 100.0) / 100.0;
    }

    static double calcularMTTR(double tempoReparoH, int numFalhas) {
        if (numFalhas == 0) return 0.0;
        return Math.round(tempoReparoH / numFalhas * 100.0) / 100.0;
    }

    static double calcularDisponibilidade(double mtbf, double mttr) {
        if (mtbf + mttr == 0) return 0.0;
        return Math.round(mtbf / (mtbf + mttr) * 100 * 10000.0) / 10000.0;
    }

    static double calcularDensidadeDefeitos(int defeitos, int loc) {
        if (loc == 0) return 0.0;
        return Math.round(defeitos / (loc / 1000.0) * 100.0) / 100.0;
    }

    static String nivelDisponibilidade(double disponibilidade) {
        if (disponibilidade >= 99.999) {
            return "Five Nines (Critical Systems)";
        } else if (disponibilidade >= 99.99) {
            return "Four Nines (High Availability)";
        } else if (disponibilidade >= 99.9) {
            return "Three Nines (Standard)";
        } else if (disponibilidade >= 99) {
            return "Two Nines (Basic)";
        } else {
            return "Below Standard";
        }
    }

    /** Lê uma linha do Scanner; retorna o valor padrão se a entrada for vazia. */
    static double lerDoubleOuPadrao(Scanner sc, String prompt, double padrao) {
        System.out.print(prompt);
        String linha = sc.nextLine().strip();
        return linha.isEmpty() ? padrao : Double.parseDouble(linha);
    }

    static int lerIntOuPadrao(Scanner sc, String prompt, int padrao) {
        System.out.print(prompt);
        String linha = sc.nextLine().strip();
        return linha.isEmpty() ? padrao : Integer.parseInt(linha);
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS2201 - CALCULADORA DE MÉTRICAS DE SOFTWARE");
        System.out.println("=".repeat(60));

        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("\n--- Métricas de Confiabilidade ---");
            double tempoTotal = lerDoubleOuPadrao(sc, "Tempo total de operação (horas): ", 720);
            int numFalhas = lerIntOuPadrao(sc, "Número de falhas: ", 3);
            double tempoReparo = lerDoubleOuPadrao(sc, "Tempo total de reparo (horas): ", 6);

            double mtbf = calcularMTBF(tempoTotal, numFalhas);
            double mttr = calcularMTTR(tempoReparo, numFalhas);
            double disp = calcularDisponibilidade(mtbf, mttr);

            System.out.printf("%n  MTBF = %sh%n", mtbf);
            System.out.printf("  MTTR = %sh%n", mttr);
            System.out.printf("  Disponibilidade = %s%%%n", disp);
            System.out.printf("  Nível: %s%n", nivelDisponibilidade(disp));

            System.out.println("\n--- Densidade de Defeitos ---");
            int defeitos = lerIntOuPadrao(sc, "Número de defeitos encontrados: ", 25);
            int loc = lerIntOuPadrao(sc, "Tamanho do sistema (LOC): ", 50000);
            double densidade = calcularDensidadeDefeitos(defeitos, loc);
            System.out.printf("%n  Densidade de Defeitos = %s defeitos/KLOC%n", densidade);
            if (densidade < 1) {
                System.out.println("  Classificação: Excelente (sistemas críticos)");
            } else if (densidade < 5) {
                System.out.println("  Classificação: Bom (sistemas corporativos)");
            } else {
                System.out.println("  Classificação: Precisa melhorar");
            }
        }
    }
}
