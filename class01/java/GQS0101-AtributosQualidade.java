/**
 * GQS0101 - Demonstração dos Atributos de Qualidade ISO 25010.
 * Exibe as 8 características e calcula métricas de confiabilidade.
 *
 * Compilação e execução:
 *   javac GQS0101-AtributosQualidade.java
 *   java -cp . GQS0101_AtributosQualidade
 */
import java.util.*;

class AtributoQualidade {
    String nome;
    String descricao;
    List<String> subcaracteristicas;

    AtributoQualidade(String nome, String descricao, List<String> subcaracteristicas) {
        this.nome = nome;
        this.descricao = descricao;
        this.subcaracteristicas = subcaracteristicas;
    }

    void exibir() {
        System.out.printf("%n  %s%n", nome);
        System.out.printf("    %s%n", descricao);
        for (String s : subcaracteristicas) {
            System.out.printf("    - %s%n", s);
        }
    }
}

class GQS0101_AtributosQualidade {

    static List<AtributoQualidade> criarCaracteristicas() {
        return List.of(
            new AtributoQualidade("Adequação Funcional",
                "O software faz o que deveria fazer?",
                List.of("Completude funcional", "Correção funcional", "Pertinência funcional")),
            new AtributoQualidade("Eficiência de Desempenho",
                "O software responde com velocidade adequada?",
                List.of("Comportamento temporal", "Utilização de recursos", "Capacidade")),
            new AtributoQualidade("Compatibilidade",
                "O software interage bem com outros sistemas?",
                List.of("Coexistência", "Interoperabilidade")),
            new AtributoQualidade("Usabilidade",
                "O software é fácil de usar?",
                List.of("Reconhecibilidade", "Apreensibilidade", "Operabilidade", "Proteção ao erro", "Acessibilidade")),
            new AtributoQualidade("Confiabilidade",
                "O software é confiável e disponível?",
                List.of("Maturidade", "Disponibilidade", "Tolerância a falhas", "Recuperabilidade")),
            new AtributoQualidade("Segurança",
                "O software protege dados e acessos?",
                List.of("Confidencialidade", "Integridade", "Não-repúdio", "Autenticidade", "Responsabilização")),
            new AtributoQualidade("Manutenibilidade",
                "O software é fácil de modificar?",
                List.of("Modularidade", "Reutilizabilidade", "Analisabilidade", "Modificabilidade", "Testabilidade")),
            new AtributoQualidade("Portabilidade",
                "O software funciona em diferentes ambientes?",
                List.of("Adaptabilidade", "Instalabilidade", "Substituibilidade"))
        );
    }

    static double calcularMTBF(double tempoTotal, int numFalhas) {
        if (numFalhas == 0) return Double.POSITIVE_INFINITY;
        return Math.round(tempoTotal / numFalhas * 100.0) / 100.0;
    }

    static double calcularDisponibilidade(double mtbf, double mttr) {
        if (mtbf + mttr == 0) return 0.0;
        return Math.round(mtbf / (mtbf + mttr) * 10000.0) / 100.0;
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS0101 - ATRIBUTOS DE QUALIDADE ISO 25010");
        System.out.println("=".repeat(60));

        var caracteristicas = criarCaracteristicas();
        for (int i = 0; i < caracteristicas.size(); i++) {
            System.out.printf("%n[%d] %s%n", i + 1, caracteristicas.get(i).nome);
        }
        System.out.printf("%n[9] Sair%n");

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nEscolha uma característica (1-9): ");
                String entrada = sc.nextLine();
                if (entrada.equals("9")) {
                    System.out.println("Encerrando. Até logo!");
                    break;
                }
                try {
                    int idx = Integer.parseInt(entrada) - 1;
                    if (idx >= 0 && idx < caracteristicas.size()) {
                        caracteristicas.get(idx).exibir();
                    } else {
                        System.out.println("Opção inválida.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Digite um número válido.");
                }
            }
        }

        System.out.println("\n--- Exemplo de Métricas ---");
        double mtbf = calcularMTBF(720, 3);
        double mttr = 2.0;
        double disp = calcularDisponibilidade(mtbf, mttr);
        System.out.printf("MTBF = %.2fh | MTTR = %.2fh | Disponibilidade = %.2f%%%n", mtbf, mttr, disp);
    }
}
