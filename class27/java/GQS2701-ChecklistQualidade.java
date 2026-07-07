/**
 * GQS2701 - Checklist de Qualidade para Projeto Final.
 * Verifica se todos os itens do projeto A3 estão completos.
 *
 * Compilação e execução:
 *   javac GQS2701-ChecklistQualidade.java
 *   java -cp . GQS2701_ChecklistQualidade
 */
import java.util.*;

class GQS2701_ChecklistQualidade {

    /** Checklist do projeto A3, organizado por categoria, na ordem de exibição. */
    static LinkedHashMap<String, List<String>> criarChecklist() {
        LinkedHashMap<String, List<String>> checklist = new LinkedHashMap<>();
        checklist.put("Plano de Teste", List.of(
            "Escopo positivo e negativo definidos",
            "Critérios de entrada e saída claros",
            "Riscos identificados e mitigados",
            "Estratégia de teste definida",
            "Recursos e cronograma estimados"
        ));
        checklist.put("Casos de Teste", List.of(
            "Mínimo de 15 casos de teste",
            "Pré-condição para cada caso",
            "Dados de entrada especificados",
            "Passos numerados e claros",
            "Resultado esperado definido",
            "Matriz de rastreabilidade (req x CT)"
        ));
        checklist.put("Testes Automatizados", List.of(
            "Mínimo de 10 testes automatizados",
            "Cobertura ≥ 70%",
            "Padrão AAA (Arrange-Act-Assert)",
            "Testes independentes e determinísticos",
            "Testes executando e passando"
        ));
        checklist.put("Postman", List.of(
            "Collection com CRUD completo",
            "Scripts de teste (pm.test)",
            "Variáveis de ambiente configuradas",
            "Execução via Newman funcionando"
        ));
        checklist.put("Pipeline CI/CD", List.of(
            "Workflow GitHub Actions configurado",
            "Build + Test + Coverage no pipeline",
            "Quality Gate definido",
            "Pipeline executando com sucesso"
        ));
        return checklist;
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS2701 - CHECKLIST DE QUALIDADE - PROJETO A3");
        System.out.println("=".repeat(60));
        System.out.println("  Marque os itens concluídos (s/n):\n");

        LinkedHashMap<String, List<String>> checklist = criarChecklist();
        int total = 0;
        int concluidos = 0;
        List<String> pendentes = new ArrayList<>();

        try (Scanner sc = new Scanner(System.in)) {
            for (Map.Entry<String, List<String>> entrada : checklist.entrySet()) {
                String categoria = entrada.getKey();
                System.out.printf("--- %s ---%n", categoria);
                for (String item : entrada.getValue()) {
                    total++;
                    while (true) {
                        System.out.printf("  %s? (s/n): ", item);
                        String resp = sc.nextLine().strip().toLowerCase();
                        if (resp.equals("s") || resp.equals("n")) {
                            if (resp.equals("s")) {
                                concluidos++;
                            } else {
                                pendentes.add(categoria + ": " + item);
                            }
                            break;
                        }
                        System.out.println("    Responda 's' ou 'n'.");
                    }
                }
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  RESUMO");
        System.out.println("=".repeat(60));
        System.out.printf("  Itens totais: %d%n", total);
        System.out.printf("  Concluídos: %d/%d (%.0f%%)%n", concluidos, total, concluidos * 100.0 / total);
        if (!pendentes.isEmpty()) {
            System.out.printf("  Pendentes (%d):%n", pendentes.size());
            for (String p : pendentes) {
                System.out.printf("    ❌ %s%n", p);
            }
        }
        if (concluidos == total) {
            System.out.println("  ✅ Projeto pronto para entrega!");
        }
        System.out.println("=".repeat(60));
    }
}
