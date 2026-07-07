/**
 * GQS2801 - Checklist de Code Review.
 * Ferramenta interativa para revisão de código antes do merge,
 * cobrindo ética, boas práticas e qualidade do Pull Request.
 *
 * Compilação e execução:
 *   javac GQS2801-CodeReviewChecklist.java
 *   java -cp . GQS2801_CodeReviewChecklist
 */
import java.util.*;

class GQS2801_CodeReviewChecklist {

    /** Checklist de code review organizado por categoria, na ordem de exibição. */
    static LinkedHashMap<String, List<String>> criarChecklist() {
        LinkedHashMap<String, List<String>> checklist = new LinkedHashMap<>();
        checklist.put("Código Limpo", List.of(
            "Nomes de variáveis e funções são significativos?",
            "Funções são pequenas e coesas (uma responsabilidade)?",
            "Não há código duplicado?"
        ));
        checklist.put("Testes", List.of(
            "Existem testes automatizados para a mudança?",
            "Os testes cobrem casos de borda?",
            "A suíte de testes passou no CI?"
        ));
        checklist.put("Segurança e Dados", List.of(
            "Dados sensíveis (senhas, tokens) não estão hardcoded?",
            "Entradas do usuário são validadas?",
            "A mudança respeita a LGPD (dados pessoais anonimizados em testes)?"
        ));
        checklist.put("Ética e Boas Práticas", List.of(
            "O código não introduz viés ou discriminação?",
            "Comentários e mensagens de commit são profissionais?",
            "A mudança foi comunicada de forma transparente ao time?"
        ));
        checklist.put("Pull Request", List.of(
            "Descrição do PR explica o 'porquê' da mudança?",
            "Escopo do PR é pequeno e revisável?",
            "Pipeline de CI/CD está verde?"
        ));
        return checklist;
    }

    static void executarChecklist(Scanner sc) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS2801 - CHECKLIST DE CODE REVIEW");
        System.out.println("=".repeat(60));

        LinkedHashMap<String, List<String>> checklist = criarChecklist();
        int total = 0;
        int aprovados = 0;
        List<String> falhas = new ArrayList<>();

        for (Map.Entry<String, List<String>> entrada : checklist.entrySet()) {
            String categoria = entrada.getKey();
            System.out.printf("%n--- %s ---%n", categoria);
            for (String item : entrada.getValue()) {
                total++;
                while (true) {
                    System.out.printf("  %s (s/n): ", item);
                    String resp = sc.nextLine().strip().toLowerCase();
                    if (resp.equals("s") || resp.equals("n")) {
                        if (resp.equals("s")) {
                            aprovados++;
                        } else {
                            falhas.add(categoria + ": " + item);
                        }
                        break;
                    }
                    System.out.println("    Responda 's' ou 'n'.");
                }
            }
        }

        double percentual = total > 0 ? aprovados * 100.0 / total : 0.0;

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  RESULTADO DO CODE REVIEW");
        System.out.println("=".repeat(60));
        System.out.printf("  Itens verificados: %d%n", total);
        System.out.printf("  Aprovados: %d/%d (%.0f%%)%n", aprovados, total, percentual);

        String veredito;
        if (percentual >= 90) {
            veredito = "APROVADO — pronto para merge";
        } else if (percentual >= 70) {
            veredito = "APROVADO COM RESSALVAS — ajustar pontos pendentes";
        } else {
            veredito = "REPROVADO — revisar antes de novo review";
        }
        System.out.printf("  Veredito: %s%n", veredito);

        if (!falhas.isEmpty()) {
            System.out.printf("%n  Pontos de atenção (%d):%n", falhas.size());
            for (String f : falhas) {
                System.out.printf("    - %s%n", f);
            }
        }
        System.out.println("=".repeat(60));
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            executarChecklist(sc);
        }
    }
}
