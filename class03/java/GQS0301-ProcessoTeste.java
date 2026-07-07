/**
 * GQS0301 - Simulação do Ciclo de Vida de Teste (STLC).
 * Demonstra as 5 fases do processo de teste de software.
 *
 * Compilação e execução:
 *   javac GQS0301-ProcessoTeste.java
 *   java -cp . GQS0301_ProcessoTeste
 */
import java.util.*;

class FaseSTLC {
    String nome;
    String descricao;
    String duracao;

    FaseSTLC(String nome, String descricao, String duracao) {
        this.nome = nome;
        this.descricao = descricao;
        this.duracao = duracao;
    }
}

class GQS0301_ProcessoTeste {

    static List<FaseSTLC> criarFases() {
        return List.of(
            new FaseSTLC("Planejamento de Testes", "Define escopo, estratégia, recursos e cronograma", "15-20%"),
            new FaseSTLC("Design de Casos de Teste", "Especifica cenários, dados e resultados esperados", "20-25%"),
            new FaseSTLC("Configuração do Ambiente", "Prepara servidores, bancos e ferramentas", "10-15%"),
            new FaseSTLC("Execução dos Testes", "Roda os casos, registra resultados e bugs", "35-40%"),
            new FaseSTLC("Encerramento e Relatório", "Consolida métricas e gera relatório final", "5-10%")
        );
    }

    /** Retorna o custo relativo do defeito baseado na regra 1:10:100. */
    static int calcularCustoDefeito(int fase) {
        int[] fatores = {1, 5, 10, 20, 100};
        int idx = Math.min(fase, fatores.length - 1);
        return fatores[idx];
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS0301 - CICLO DE VIDA DE TESTE (STLC)");
        System.out.println("=".repeat(60));

        System.out.println("\nAs 5 fases do STLC:");
        List<FaseSTLC> fases = criarFases();
        for (int i = 0; i < fases.size(); i++) {
            FaseSTLC fase = fases.get(i);
            System.out.printf("%n  [%d] %s%n", i + 1, fase.nome);
            System.out.printf("      %s%n", fase.descricao);
            System.out.printf("      Duração: %s%n", fase.duracao);
        }

        System.out.println("\n--- Regra 1:10:100 (Custo do Defeito) ---");
        String[] fasesNome = {"Requisitos", "Design", "Codificação", "Teste", "Produção"};
        for (int i = 0; i < fasesNome.length; i++) {
            int custo = calcularCustoDefeito(i);
            System.out.printf("  Defeito descoberto em %s: %dx%n", fasesNome[i], custo);
        }

        System.out.println("\n--- Simulação de Execução ---");
        String[] casos = {
            "CT-001: Login válido", "CT-002: Login inválido",
            "CT-003: Cadastro", "CT-004: Recuperar senha"
        };
        String[] statusPossiveis = {"✅ Passou", "❌ Falhou", "⏳ Bloqueado"};
        Random random = new Random(42);
        for (String caso : casos) {
            String status = statusPossiveis[random.nextInt(statusPossiveis.length)];
            System.out.printf("  %s: %s%n", caso, status);
        }
    }
}
