/**
 * GQS0701 - Simulação do Ciclo de Vida de um Defeito.
 * Demonstra os estados de um bug até o fechamento.
 *
 * Compilação e execução:
 *   javac GQS0701-CicloDefecto.java
 *   java -cp . GQS0701_CicloDefecto
 */
class Defeito {
    String titulo;
    String estado;
    String severidade;
    String prioridade;

    Defeito(String titulo) {
        this.titulo = titulo;
        this.estado = "Novo";
        this.severidade = "";
        this.prioridade = "";
    }

    void transitar(String novoEstado) {
        System.out.printf("  %s → %s%n", estado, novoEstado);
        estado = novoEstado;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    void exibir() {
        System.out.printf("%n  BUG: %s%n", titulo);
        System.out.printf("  Estado: %s%n", estado);
        if (!severidade.isEmpty()) {
            System.out.printf("  Severidade: %s | Prioridade: %s%n", severidade, prioridade);
        }
    }
}

class GQS0701_CicloDefecto {

    static void simularCiclo() {
        System.out.println("=".repeat(60));
        System.out.println("  GQS0701 - CICLO DE VIDA DE UM DEFEITO");
        System.out.println("=".repeat(60));

        Defeito bug = new Defeito("App fecha ao selecionar endereço");

        System.out.println("\n--- Simulação do Ciclo ---");
        bug.exibir();
        bug.transitar("Atribuído");
        bug.transitar("Em Análise");
        bug.severidade = "Crítica";
        bug.prioridade = "Imediata";
        bug.exibir();
        bug.transitar("Corrigindo");
        bug.transitar("Resolvido");
        bug.transitar("Em Teste");
        System.out.println("\n  QA valida a correção...");
        bug.transitar("Fechado");
        bug.exibir();
        System.out.println("\n  Ciclo concluído com sucesso!");
    }

    public static void main(String[] args) {
        simularCiclo();
    }
}
