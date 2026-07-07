/**
 * GQS0401 - Gerador de Estrutura de Plano de Teste (IEEE 829).
 * Gera um template completo de plano de teste.
 *
 * Compilação e execução:
 *   javac GQS0401-GeradorPlanTeste.java
 *   java -cp . GQS0401_GeradorPlanTeste
 */
import java.time.LocalDate;
import java.util.Scanner;

class GQS0401_GeradorPlanTeste {

    static final String TEMPLATE = """
        # Plano de Teste - %s

        **Versão:** 1.0 | **Data:** %s | **Responsável:** %s

        ## 1. Identificador
        PT-%s-v1.0

        ## 2. Itens de Teste
        - Módulo de autenticação
        - Módulo de cadastro

        ## 3. Funcionalidades a Testar
        - Login
        - Cadastro de usuário
        - Recuperação de senha

        ## 4. Funcionalidades a NÃO Testar
        - Relatórios (terceirizado)
        - Performance (fase separada)

        ## 5. Abordagem
        - Níveis: Unitário, Integração, Sistema
        - Técnicas: Caixa-preta (partição de equivalência, BVA)
        - Ferramentas: JUnit 5, pytest, Selenium

        ## 6. Critérios de Aprovação/Reprovação
        - 95%% dos casos de teste passando
        - Nenhum bug crítico em aberto

        ## 7. Critérios de Suspensão e Retomada
        - Se ambiente de teste ficar indisponível por mais de 4h

        ## 8. Entregáveis
        - Plano de Teste
        - Casos de Teste
        - Relatório de Execução

        ## 9. Recursos
        - 1 Analista de Teste
        - 1 Engenheiro de Automação
        - Servidor Staging

        ## 10. Cronograma
        - Semana 1: Planejamento
        - Semana 2: Design
        - Semana 3-4: Execução

        ## 11. Riscos
        - Risco: indisponibilidade do ambiente
        - Mitigação: ambiente reserva em cloud
        """;

    /** Lê os dados do projeto e imprime o plano de teste preenchido. */
    static void gerarPlano(Scanner sc) {
        System.out.print("Nome do projeto: ");
        String projeto = sc.nextLine().strip();
        if (projeto.isEmpty()) projeto = "MeuProjeto";

        System.out.print("Responsável: ");
        String responsavel = sc.nextLine().strip();
        if (responsavel.isEmpty()) responsavel = "Eng. QA";

        String data = LocalDate.now().toString();

        String plano = String.format(TEMPLATE, projeto, data, responsavel, projeto);

        System.out.println("\n" + "=".repeat(60));
        System.out.println(plano);
        System.out.println("=".repeat(60));
    }

    public static void main(String[] args) {
        System.out.println("GQS0401 - Gerador de Plano de Teste (IEEE 829)");
        try (Scanner sc = new Scanner(System.in)) {
            gerarPlano(sc);
        }
    }
}
