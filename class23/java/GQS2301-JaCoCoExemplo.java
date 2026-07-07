/**
 * GQS2301 - Exemplo Java para a Aula 23 (Cobertura de Código com JaCoCo).
 * Contém 3 métodos pequenos e independentes, com decisões (if/else, switch)
 * propositalmente desiguais em complexidade, para servir de alvo de um
 * relatório JaCoCo real (linha, ramo/branch) — ver `pom.xml` desta pasta.
 *
 * Compilação e execução (standalone, sem Maven):
 *   javac GQS2301-JaCoCoExemplo.java
 *   java -cp . GQS2301_JaCoCoExemplo
 */
class GQS2301_JaCoCoExemplo {

    /** Valida se uma idade é maior de idade (>= 18). Lança exceção para valores fora da faixa humana. */
    static boolean validarMaioridade(int idade) {
        if (idade < 0 || idade > 130) {
            throw new IllegalArgumentException("Idade inválida: " + idade);
        }
        return idade >= 18;
    }

    /** Calcula o valor final aplicando desconto conforme o tipo de cliente. */
    static double calcularDesconto(double valor, String tipoCliente) {
        if (valor < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo");
        }
        double percentual;
        switch (tipoCliente) {
            case "regular":
                percentual = 0.0;
                break;
            case "silver":
                percentual = 0.05;
                break;
            case "gold":
                percentual = 0.10;
                break;
            case "platinum":
                percentual = 0.15;
                break;
            default:
                throw new IllegalArgumentException("Tipo de cliente desconhecido: " + tipoCliente);
        }
        return Math.round(valor * (1 - percentual) * 100.0) / 100.0;
    }

    /**
     * Classifica o IMC (Índice de Massa Corporal) em 4 faixas.
     * NOTA PROPOSITAL: no laboratório desta aula, o ramo "Obesidade" fica
     * sem teste dedicado, para servir de exemplo real de linha/ramo "Missed"
     * num relatório JaCoCo (ver Slide "JaCoCo HTML — Interpretando Cores").
     */
    static String classificarIMC(double peso, double altura) {
        if (altura <= 0) {
            throw new IllegalArgumentException("Altura deve ser positiva");
        }
        double imc = peso / (altura * altura);
        if (imc < 18.5) {
            return "Abaixo do peso";
        } else if (imc < 25) {
            return "Peso normal";
        } else if (imc < 30) {
            return "Sobrepeso";
        } else {
            return "Obesidade";
        }
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS2301 - EXEMPLO JAVA PARA JACOCO");
        System.out.println("=".repeat(60));

        System.out.println("\n--- validarMaioridade ---");
        for (int idade : new int[]{17, 18, 65}) {
            System.out.printf("  idade=%-3d -> maior de idade? %s%n", idade, validarMaioridade(idade));
        }

        System.out.println("\n--- calcularDesconto ---");
        for (String tipo : new String[]{"regular", "silver", "gold", "platinum"}) {
            System.out.printf("  R$ 100.00 (%s) -> R$ %.2f%n", tipo, calcularDesconto(100.0, tipo));
        }

        System.out.println("\n--- classificarIMC ---");
        double[][] casos = {{50.0, 1.70}, {65.0, 1.70}};
        for (double[] c : casos) {
            System.out.printf("  peso=%.1fkg altura=%.2fm -> %s%n", c[0], c[1], classificarIMC(c[0], c[1]));
        }
    }
}
