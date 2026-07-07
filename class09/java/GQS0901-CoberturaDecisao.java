/**
 * GQS0901 - Demonstração de Cobertura de Instrução e Decisão.
 * Analisa uma função exemplo e calcula as métricas de cobertura e a
 * complexidade ciclomática (McCabe).
 *
 * Compilação e execução:
 *   javac GQS0901-CoberturaDecisao.java
 *   java -cp . GQS0901_CoberturaDecisao
 */
import java.util.*;

/** Representa um caso de teste com entradas e o resultado esperado. */
class CasoTeste {
    int a;
    int b;
    String esperado;

    CasoTeste(int a, int b, String esperado) {
        this.a = a;
        this.b = b;
        this.esperado = esperado;
    }
}

class GQS0901_CoberturaDecisao {

    /** Função exemplo para análise de cobertura. */
    static String funcaoExemplo(int a, int b) {
        if (a > 0 && b > 0) {
            return "Ambos positivos";
        } else if (a > 0) {
            return "A positivo";
        } else if (b > 0) {
            return "B positivo";
        } else {
            return "Nenhum positivo";
        }
    }

    /** M = número de decisões + 1 (McCabe). */
    static int calcularComplexidadeCiclomatica(int decisoes) {
        return decisoes + 1;
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS0901 - COBERTURA DE INSTRUÇÃO E DECISÃO");
        System.out.println("=".repeat(60));

        System.out.println("\nFunção analisada: funcaoExemplo(a, b)");
        System.out.println("  if a > 0 e b > 0 → Ambos positivos");
        System.out.println("  else if a > 0 → A positivo");
        System.out.println("  else if b > 0 → B positivo");
        System.out.println("  else → Nenhum positivo");
        System.out.printf("%n  Complexidade Ciclomática (M) = %d%n", calcularComplexidadeCiclomatica(3));
        System.out.println("  (3 decisões + 1 = 4 caminhos independentes)");

        System.out.println("\n--- Cobertura de Decisão ---");
        List<CasoTeste> casos = List.of(
            new CasoTeste(5, 5, "Ambos positivos"),
            new CasoTeste(5, -1, "A positivo"),
            new CasoTeste(-1, 5, "B positivo"),
            new CasoTeste(-1, -1, "Nenhum positivo")
        );
        System.out.printf("%-15s %-20s %-15s%n", "Teste (a, b)", "Resultado", "Cobertura");
        System.out.println("-".repeat(50));
        for (int i = 0; i < casos.size(); i++) {
            CasoTeste ct = casos.get(i);
            String resultado = funcaoExemplo(ct.a, ct.b);
            String coberto = resultado.equals(ct.esperado) ? "✅" : "❌";
            System.out.printf("  CT-%02d (%-2d,%-3d) %-20s %s%n", i + 1, ct.a, ct.b, resultado, coberto);
        }

        System.out.println("\n  Para 100% de cobertura de decisão: todos os 4 casos");
        System.out.println("  Para 100% de cobertura de instrução: casos 1, 2, 3, 4");
    }
}
