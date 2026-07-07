/**
 * GQS2601-GitHubActions_Workflow.java
 * ======================================
 * Gerador e validador conceitual de workflow do GitHub Actions.
 * Aula 26 — DevOps e GitHub Actions | Garantia da Qualidade de Software.
 *
 * Compilar:  javac GQS2601-GitHubActions_Workflow.java
 * Executar:  java GQS2601-GitHubActions_Workflow
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GQS2601_GitHubActions_Workflow {

    // ──────────────────────────────────────────────
    // 1. Validação de workflow YAML (simulada)
    // ──────────────────────────────────────────────

    /**
     * Valida conceitualmente um workflow YAML do GitHub Actions.
     * @param yamlContent String contendo o YAML do workflow.
     * @return Lista de problemas encontrados (vazia se tudo ok).
     */
    public static List<String> validateWorkflow(String yamlContent) {
        List<String> issues = new ArrayList<>();

        // Verifica 'name'
        if (!yamlContent.contains("name:")) {
            issues.add("[AVISO] 'name' não encontrado. Recomenda-se fornecer um nome.");
        }

        // Verifica 'on'
        if (!yamlContent.contains("on:")) {
            issues.add("[ERRO] 'on' não encontrado. O gatilho é obrigatório.");
        } else {
            Pattern eventPattern = Pattern.compile("push|pull_request|schedule|workflow_dispatch");
            Matcher matcher = eventPattern.matcher(yamlContent);
            if (!matcher.find()) {
                issues.add("[AVISO] Nenhum evento conhecido encontrado em 'on'.");
            }
        }

        // Verifica 'jobs'
        if (!yamlContent.contains("jobs:")) {
            issues.add("[ERRO] 'jobs' não encontrado. Pelo menos um job deve ser definido.");
        } else {
            // Verifica 'runs-on'
            if (!yamlContent.contains("runs-on:")) {
                issues.add("[ERRO] Nenhum job possui 'runs-on'.");
            }
            // Verifica steps
            if (!yamlContent.contains("steps:")) {
                issues.add("[AVISO] Nenhum step encontrado. Use 'steps:' com ações.");
            }
        }

        return issues;
    }

    // ──────────────────────────────────────────────
    // 2. Gerador de template de workflow
    // ──────────────────────────────────────────────

    public static String generateWorkflowTemplate(
            String workflowName,
            String javaVersion,
            String osRunner,
            boolean useMatrix) {

        StringBuilder sb = new StringBuilder();

        sb.append("name: ").append(workflowName).append("\n");
        sb.append("\n");
        sb.append("on:\n");
        sb.append("  push:\n");
        sb.append("    branches: [ main ]\n");
        sb.append("  pull_request:\n");
        sb.append("    branches: [ main ]\n");
        sb.append("\n");
        sb.append("jobs:\n");
        sb.append("  test:\n");
        sb.append("    runs-on: ").append(osRunner).append("\n");

        if (useMatrix) {
            sb.append("\n");
            sb.append("    strategy:\n");
            sb.append("      matrix:\n");
            sb.append("        java: [17, 21]\n");
            sb.append("        os: [ubuntu-latest, windows-latest]\n");
            sb.append("\n");
            sb.append("    steps:\n");
            sb.append("      - name: Checkout code\n");
            sb.append("        uses: actions/checkout@v4\n");
            sb.append("\n");
            sb.append("      - name: Set up JDK\n");
            sb.append("        uses: actions/setup-java@v4\n");
            sb.append("        with:\n");
            sb.append("          java-version: ${{ matrix.java }}\n");
            sb.append("          distribution: 'temurin'\n");
            sb.append("\n");
            sb.append("      - name: Compile and test with Maven\n");
            sb.append("        run: mvn clean test\n");
        } else {
            sb.append("\n");
            sb.append("    steps:\n");
            sb.append("      - name: Checkout code\n");
            sb.append("        uses: actions/checkout@v4\n");
            sb.append("\n");
            sb.append("      - name: Set up JDK\n");
            sb.append("        uses: actions/setup-java@v4\n");
            sb.append("        with:\n");
            sb.append("          java-version: '").append(javaVersion).append("'\n");
            sb.append("          distribution: 'temurin'\n");
            sb.append("\n");
            sb.append("      - name: Cache Maven dependencies\n");
            sb.append("        uses: actions/cache@v4\n");
            sb.append("        with:\n");
            sb.append("          path: ~/.m2/repository\n");
            sb.append("          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}\n");
            sb.append("          restore-keys: |\n");
            sb.append("            ${{ runner.os }}-maven-\n");
            sb.append("\n");
            sb.append("      - name: Run tests with coverage\n");
            sb.append("        run: mvn clean test jacoco:report\n");
            sb.append("\n");
            sb.append("      - name: Upload coverage report\n");
            sb.append("        uses: actions/upload-artifact@v4\n");
            sb.append("        with:\n");
            sb.append("          name: coverage-report\n");
            sb.append("          path: target/site/jacoco/\n");
        }

        return sb.toString();
    }

    // ──────────────────────────────────────────────
    // 3. Exemplos pré-definidos
    // ──────────────────────────────────────────────

    private static final String WORKFLOW_VALID =
            "name: CI - Java 17\n" +
            "on:\n" +
            "  push:\n" +
            "    branches: [ main ]\n" +
            "  pull_request:\n" +
            "    branches: [ main ]\n" +
            "\n" +
            "jobs:\n" +
            "  test:\n" +
            "    runs-on: ubuntu-latest\n" +
            "    steps:\n" +
            "      - name: Checkout\n" +
            "        uses: actions/checkout@v4\n" +
            "      - name: Setup Java\n" +
            "        uses: actions/setup-java@v4\n" +
            "        with:\n" +
            "          java-version: '17'\n" +
            "          distribution: 'temurin'\n" +
            "      - name: Run tests\n" +
            "        run: mvn test\n";

    private static final String WORKFLOW_INVALID =
            "name:\n" +
            "on:\n" +
            "  push\n" +
            "\n" +
            "  test:\n" +
            "    steps:\n" +
            "      - run: echo hello\n";

    // ──────────────────────────────────────────────
    // 4. Modo interativo
    // ──────────────────────────────────────────────

    public static void interactiveMode(Scanner scanner) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS2601 - Gerador de Workflow GitHub Actions");
        System.out.println("  Garantia da Qualidade de Software — Aula 26");
        System.out.println("=".repeat(60));

        while (true) {
            System.out.println("\nOpções:");
            System.out.println("  1 - Validar workflow YAML existente");
            System.out.println("  2 - Gerar template de workflow");
            System.out.println("  3 - Validar exemplos pré-definidos");
            System.out.println("  0 - Sair");

            System.out.print("\nEscolha: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\nCole o YAML do workflow (digite 'EOF' em linha vazia para finalizar):");
                    StringBuilder yamlInput = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.trim().equalsIgnoreCase("EOF")) break;
                        yamlInput.append(line).append("\n");
                    }
                    List<String> issues = validateWorkflow(yamlInput.toString());
                    if (issues.isEmpty()) {
                        System.out.println("\n✅ Workflow válido! Nenhum problema encontrado.");
                    } else {
                        System.out.println("\n❌ Problemas encontrados:");
                        for (String issue : issues) {
                            System.out.println("   • " + issue);
                        }
                    }
                    break;

                case "2":
                    System.out.println("\n--- Gerar Template de Workflow ---");
                    System.out.print("Nome do workflow [CI - Build and Test]: ");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) name = "CI - Build and Test";

                    System.out.print("Versão Java [17]: ");
                    String javaVer = scanner.nextLine().trim();
                    if (javaVer.isEmpty()) javaVer = "17";

                    System.out.print("Runner OS [ubuntu-latest]: ");
                    String os = scanner.nextLine().trim();
                    if (os.isEmpty()) os = "ubuntu-latest";

                    System.out.print("Usar matrix (s/N)? ");
                    String matrixChoice = scanner.nextLine().trim().toLowerCase();
                    boolean useMatrix = matrixChoice.equals("s") || matrixChoice.equals("sim");

                    System.out.println("\n--- Workflow Gerado ---\n");
                    System.out.println(generateWorkflowTemplate(name, javaVer, os, useMatrix));
                    break;

                case "3":
                    System.out.println("\n--- Validando workflow VÁLIDO ---");
                    List<String> validIssues = validateWorkflow(WORKFLOW_VALID);
                    if (validIssues.isEmpty()) {
                        System.out.println("   ✅ Válido!");
                    } else {
                        validIssues.forEach(i -> System.out.println("   ❌ " + i));
                    }

                    System.out.println("\n--- Validando workflow INVÁLIDO ---");
                    List<String> invalidIssues = validateWorkflow(WORKFLOW_INVALID);
                    if (invalidIssues.isEmpty()) {
                        System.out.println("   ✅ Válido!");
                    } else {
                        invalidIssues.forEach(i -> System.out.println("   ❌ " + i));
                    }

                    System.out.println("\n--- Template gerado (com matrix) ---");
                    System.out.println(generateWorkflowTemplate("CI - Matrix Build", "17", "ubuntu-latest", true));
                    break;

                case "0":
                    System.out.println("\nEncerrando. Até a próxima aula!");
                    return;

                default:
                    System.out.println("\nOpção inválida. Tente novamente.");
            }
        }
    }

    // ──────────────────────────────────────────────
    // 5. Ponto de entrada
    // ──────────────────────────────────────────────

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--validate")) {
            System.out.println("=".repeat(60));
            System.out.println("  GQS2601 - Validação de Workflows");
            System.out.println("=".repeat(60));

            System.out.println("\n1. Workflow válido:");
            List<String> issuesValid = validateWorkflow(WORKFLOW_VALID);
            if (issuesValid.isEmpty()) {
                System.out.println("   ✅ Nenhum problema.");
            } else {
                issuesValid.forEach(i -> System.out.println("   ❌ " + i));
            }

            System.out.println("\n2. Workflow inválido:");
            List<String> issuesInvalid = validateWorkflow(WORKFLOW_INVALID);
            if (issuesInvalid.isEmpty()) {
                System.out.println("   ✅ Nenhum problema.");
            } else {
                issuesInvalid.forEach(i -> System.out.println("   ❌ " + i));
            }

            System.out.println("\n3. Template gerado (com matrix):");
            System.out.println(generateWorkflowTemplate("CI - Matrix Build", "17", "ubuntu-latest", true));

            System.exit(0);
        }

        try (Scanner scanner = new Scanner(System.in)) {
            interactiveMode(scanner);
        }
    }
}
