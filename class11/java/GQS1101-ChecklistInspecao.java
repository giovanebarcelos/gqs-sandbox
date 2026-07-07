/**
 * GQS1101 - Checklist de Inspeção de Código.
 * Ferramenta interativa para revisão de código, organizada por categorias.
 *
 * Compilação e execução:
 *   javac GQS1101-ChecklistInspecao.java
 *   java -cp . GQS1101_ChecklistInspecao
 */
import java.util.*;

class GQS1101_ChecklistInspecao {

    /** Checklist de inspeção organizado por categoria, na ordem de exibição. */
    static LinkedHashMap<String, List<String>> criarChecklist() {
        LinkedHashMap<String, List<String>> checklist = new LinkedHashMap<>();
        checklist.put("Lógica", List.of(
            "Condições de borda tratadas?",
            "Laços têm condição de parada?",
            "Recursão tem caso base?"
        ));
        checklist.put("Dados", List.of(
            "Variáveis foram inicializadas?",
            "Parâmetros de métodos são validados?",
            "Null pointers são evitados?"
        ));
        checklist.put("Tratamento de Erros", List.of(
            "Exceções não foram silenciadas?",
            "Recursos são fechados adequadamente?",
            "Mensagens de erro são informativas?"
        ));
        checklist.put("Legibilidade", List.of(
            "Nomes são significativos?",
            "Métodos são coesos (uma responsabilidade)?",
            "Comentários são necessários e claros?"
        ));
        checklist.put("Padrões", List.of(
            "Convenções de codificação seguidas?",
            "Formatação consistente?",
            "Código morto foi removido?"
        ));
        return checklist;
    }

    static void executarChecklist(Scanner sc) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS1101 - CHECKLIST DE INSPEÇÃO DE CÓDIGO");
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

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  RESULTADO DA INSPEÇÃO");
        System.out.println("=".repeat(60));
        System.out.printf("  Itens verificados: %d%n", total);
        System.out.printf("  Aprovados: %d/%d (%.0f%%)%n", aprovados, total, aprovados * 100.0 / total);
        if (!falhas.isEmpty()) {
            System.out.printf("  Falhas (%d):%n", falhas.size());
            for (String f : falhas) {
                System.out.printf("    ❌ %s%n", f);
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
