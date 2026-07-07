/**
 * GQS1001 - Calculadora de Exposição a Risco (Probabilidade x Impacto).
 * Reimplementação em Java da matriz de risco usada para priorização de testes.
 *
 * Compilação e execução:
 *   javac GQS1001-MatrizRisco.java
 *   java -cp . GQS1001_MatrizRisco
 */
import java.util.*;

/** Representa uma funcionalidade avaliada na matriz de risco. */
class Funcionalidade {
    String nome;
    int probabilidade;
    int impacto;

    Funcionalidade(String nome, int probabilidade, int impacto) {
        this.nome = nome;
        this.probabilidade = probabilidade;
        this.impacto = impacto;
    }
}

class GQS1001_MatrizRisco {

    /** Risco = Probabilidade x Impacto. */
    static int calcularRisco(int probabilidade, int impacto) {
        return probabilidade * impacto;
    }

    static String classificarRisco(int risco) {
        if (risco >= 15) {
            return "🔴 Crítico";
        } else if (risco >= 10) {
            return "🟠 Alto";
        } else if (risco >= 5) {
            return "🟡 Médio";
        } else {
            return "🟢 Baixo";
        }
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS1001 - MATRIZ DE RISCO (Probabilidade x Impacto)");
        System.out.println("=".repeat(60));

        System.out.println("\nEscala de Probabilidade e Impacto (1 a 5):");
        System.out.println("  1 = Muito Baixo | 2 = Baixo | 3 = Médio | 4 = Alto | 5 = Muito Alto");

        List<Funcionalidade> funcionalidades = List.of(
            new Funcionalidade("Checkout/Pagamento", 3, 5),
            new Funcionalidade("Carrinho de Compras", 4, 4),
            new Funcionalidade("Catálogo de Produtos", 3, 2),
            new Funcionalidade("Cadastro de Usuário", 2, 3),
            new Funcionalidade("Busca/Filtros", 4, 2)
        );

        System.out.printf("%n%-25s %-8s %-8s %-8s %-15s%n", "Funcionalidade", "Prob.", "Imp.", "Risco", "Classificação");
        System.out.println("-".repeat(65));
        for (Funcionalidade f : funcionalidades) {
            int risco = calcularRisco(f.probabilidade, f.impacto);
            String classificacao = classificarRisco(risco);
            System.out.printf("%-25s %-8d %-8d %-8d %-15s%n", f.nome, f.probabilidade, f.impacto, risco, classificacao);
        }

        System.out.println("\n--- Priorização de Testes ---");
        System.out.println("  Crítico: Teste exaustivo, automação prioritária");
        System.out.println("  Alto: Teste extensivo");
        System.out.println("  Médio: Teste aprofundado");
        System.out.println("  Baixo: Teste padrão");
    }
}
