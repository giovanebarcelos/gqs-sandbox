/**
 * GQS0801 - Demonstração de Partição de Equivalência e Análise de Valor Limite (BVA).
 * Reimplementação em Java do exemplo de classificação do campo Nota (0 a 10).
 *
 * Compilação e execução:
 *   javac GQS0801-PartEquivalencia.java
 *   java -cp . GQS0801_PartEquivalencia
 */
import java.util.*;

/** Representa uma classe de equivalência a ser demonstrada na tabela. */
class ClasseEquivalencia {
    String classe;
    String intervalo;
    String tipo;
    String valorTexto;
    double valorNumerico;
    boolean numerico;

    ClasseEquivalencia(String classe, String intervalo, String tipo, double valor) {
        this.classe = classe;
        this.intervalo = intervalo;
        this.tipo = tipo;
        this.valorNumerico = valor;
        this.valorTexto = formatarValor(valor);
        this.numerico = true;
    }

    ClasseEquivalencia(String classe, String intervalo, String tipo, String valorNaoNumerico) {
        this.classe = classe;
        this.intervalo = intervalo;
        this.tipo = tipo;
        this.valorTexto = valorNaoNumerico;
        this.numerico = false;
    }

    private static String formatarValor(double valor) {
        if (valor == Math.floor(valor)) {
            return String.valueOf((int) valor);
        }
        return String.valueOf(valor);
    }
}

class GQS0801_PartEquivalencia {

    /** Exemplo de campo Nota (0 a 10). */
    static String classificarNota(String valorTexto, boolean numerico, double valor) {
        if (!numerico) {
            return "Inválido: não numérico";
        }
        if (valor < 0 || valor > 10) {
            return "Inválido: fora da faixa";
        }
        if (valor >= 6) {
            return "Aprovado";
        }
        return "Reprovado";
    }

    /** Demonstra partição de equivalência para campo Nota. */
    static void particaoEquivalenciaNota() {
        System.out.println("Partição de Equivalência - Campo Nota (0 a 10):");
        List<ClasseEquivalencia> classes = List.of(
            new ClasseEquivalencia("CE1", "0 ≤ nota ≤ 10", "Válida", 7),
            new ClasseEquivalencia("CE2", "nota < 0", "Inválida", -1),
            new ClasseEquivalencia("CE3", "nota > 10", "Inválida", 11),
            new ClasseEquivalencia("CE4", "não numérico", "Inválida", "abc")
        );
        for (ClasseEquivalencia c : classes) {
            String resultado = classificarNota(c.valorTexto, c.numerico, c.valorNumerico);
            System.out.printf("  %s: %-20s %-10s → valor %-5s = %s%n",
                c.classe, c.intervalo, c.tipo, c.valorTexto, resultado);
        }
    }

    /** Demonstra Boundary Value Analysis para campo Nota. */
    static void bvaNota() {
        System.out.println("\nBVA (3-valor) - Campo Nota (0 a 10):");
        int[] pontos = {-1, 0, 1, 9, 10, 11};
        String[] rotulos = {"Abaixo mínimo", "Mínimo exato", "Mínimo + 1", "Máximo - 1", "Máximo exato", "Acima máximo"};
        for (int i = 0; i < pontos.length; i++) {
            String resultado = classificarNota(String.valueOf(pontos[i]), true, pontos[i]);
            System.out.printf("  %-20s = %-5d → %s%n", rotulos[i], pontos[i], resultado);
        }
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS0801 - PARTIÇÃO DE EQUIVALÊNCIA E BVA");
        System.out.println("=".repeat(60));
        particaoEquivalenciaNota();
        bvaNota();
    }
}
