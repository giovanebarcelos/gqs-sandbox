/**
 * GQS0601 - Gerador de Caso de Teste (template IEEE 829).
 * Cria um template completo de caso de teste.
 *
 * Compilação e execução:
 *   javac GQS0601-GeradorCasoTeste.java
 *   java -cp . GQS0601_GeradorCasoTeste
 */
import java.util.*;

class GQS0601_GeradorCasoTeste {

    static String perguntar(Scanner sc, String rotulo, String padrao) {
        System.out.printf("%s: ", rotulo);
        String resposta = sc.nextLine().strip();
        return resposta.isEmpty() ? padrao : resposta;
    }

    static void gerarCaso() {
        System.out.println("=".repeat(60));
        System.out.println("  GQS0601 - GERADOR DE CASO DE TESTE");
        System.out.println("=".repeat(60));

        Map<String, String> caso = new LinkedHashMap<>();
        try (Scanner sc = new Scanner(System.in)) {
            caso.put("ID", perguntar(sc, "ID do caso (ex: CT-001)", "CT-001"));
            caso.put("Título", perguntar(sc, "Título", "Validar login com credenciais corretas"));
            caso.put("Módulo", perguntar(sc, "Módulo", "Autenticação"));
            caso.put("Pré-condição", perguntar(sc, "Pré-condição", "Usuário cadastrado como 'joao'"));
            caso.put("Dados de entrada", perguntar(sc, "Dados de entrada", "usuário: \"joao\", senha: \"123\""));
            caso.put("Passos", perguntar(sc, "Passos (separados por ;)", "1. Abrir /login; 2. Preencher campos; 3. Clicar Entrar"));
            caso.put("Resultado esperado", perguntar(sc, "Resultado esperado", "Redirecionar para /dashboard"));
            caso.put("Pós-condição", perguntar(sc, "Pós-condição", "Sessão criada, último_login atualizado"));
            caso.put("Prioridade", perguntar(sc, "Prioridade (Crítica/Alta/Média/Baixa)", "Alta"));
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  CASO DE TESTE GERADO");
        System.out.println("=".repeat(60));
        for (Map.Entry<String, String> entrada : caso.entrySet()) {
            System.out.printf("  %s: %s%n", entrada.getKey(), entrada.getValue());
        }
        System.out.println("=".repeat(60));
    }

    public static void main(String[] args) {
        gerarCaso();
    }
}
