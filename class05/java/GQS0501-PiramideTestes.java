/**
 * GQS0501 - Demonstração da Pirâmide de Testes.
 * Calcula a distribuição ideal de testes por nível.
 *
 * Compilação e execução:
 *   javac GQS0501-PiramideTestes.java
 *   java -cp . GQS0501_PiramideTestes
 */
import java.util.*;

class NivelTeste {
    String nome;
    int quantidade;
    String velocidade;
    String custo;

    NivelTeste(String nome, int quantidade, String velocidade, String custo) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.velocidade = velocidade;
        this.custo = custo;
    }
}

class GQS0501_PiramideTestes {

    /** Calcula a distribuição ideal (70-20-10). */
    static List<NivelTeste> calcularDistribuicao(int totalTestes) {
        return List.of(
            new NivelTeste("Testes Unitários", (int) (totalTestes * 0.7), "ms", "Baixo"),
            new NivelTeste("Testes de Integração", (int) (totalTestes * 0.2), "s", "Médio"),
            new NivelTeste("Testes E2E / UI", (int) (totalTestes * 0.1), "min", "Alto")
        );
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS0501 - PIRÂMIDE DE TESTES");
        System.out.println("=".repeat(60));

        int total = 1000;
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("\nNúmero total de testes desejado (ex: 1000): ");
            String entrada = sc.nextLine().strip();
            if (!entrada.isEmpty()) {
                try {
                    total = Integer.parseInt(entrada);
                } catch (NumberFormatException e) {
                    total = 1000;
                }
            }
        }

        List<NivelTeste> niveis = calcularDistribuicao(total);

        System.out.printf("%n%-25s %-12s %-12s %-10s%n", "Nível", "Quantidade", "Velocidade", "Custo");
        System.out.println("-".repeat(60));
        int somaQuantidade = 0;
        for (NivelTeste n : niveis) {
            System.out.printf("%-25s %-12d %-12s %-10s%n", n.nome, n.quantidade, n.velocidade, n.custo);
            somaQuantidade += n.quantidade;
        }

        System.out.println("\nDistribuição: 70% Unitários + 20% Integração + 10% E2E");
        System.out.printf("Total: %d testes%n", somaQuantidade);

        System.out.println("\n--- Custo Relativo de Execução ---");
        double tempoUnitario = total * 0.7 * 0.01;
        double tempoIntegracao = total * 0.2 * 1.0;
        double tempoE2e = total * 0.1 * 60.0;
        System.out.printf("  Unitários: %.1fs%n", tempoUnitario);
        System.out.printf("  Integração: %.1fs%n", tempoIntegracao);
        System.out.printf("  E2E: %.1fs%n", tempoE2e);
        System.out.printf("  Total: %.1fs%n", tempoUnitario + tempoIntegracao + tempoE2e);
    }
}
