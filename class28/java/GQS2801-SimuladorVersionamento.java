import java.util.ArrayList;
import java.util.List;

/**
 * GQS2801 - Simulador de Histórico de Commits, Baseline e Versionamento Semântico.
 * Simula um repositório simplificado: commits, criação de baseline e cálculo
 * da próxima versão SemVer a partir do tipo de mudança.
 *
 * Compilação e execução:
 *   javac GQS2801-SimuladorVersionamento.java
 *   java -cp . GQS2801_SimuladorVersionamento
 */
class GQS2801_SimuladorVersionamento {

    static class Commit {
        String hash, mensagem, tipo;
        Commit(String hash, String mensagem, String tipo) {
            this.hash = hash; this.mensagem = mensagem; this.tipo = tipo;
        }
    }

    static class Versao {
        int major, minor, patch;
        Versao(int major, int minor, int patch) { this.major = major; this.minor = minor; this.patch = patch; }
        String formatar() { return major + "." + minor + "." + patch; }
        boolean igual(Versao outra) {
            return outra != null && major == outra.major && minor == outra.minor && patch == outra.patch;
        }
    }

    static class Repositorio {
        Versao versao = new Versao(0, 1, 0);
        List<Commit> historico = new ArrayList<>();
        Versao baseline = null;

        void commitar(String hash, String mensagem, String tipo) {
            if (!tipo.equals("major") && !tipo.equals("minor") && !tipo.equals("patch"))
                throw new IllegalArgumentException("tipo deve ser major, minor ou patch");
            historico.add(new Commit(hash, mensagem, tipo));
        }

        Versao proximaVersao(String tipo) {
            if (tipo.equals("major")) return new Versao(versao.major + 1, 0, 0);
            if (tipo.equals("minor")) return new Versao(versao.major, versao.minor + 1, 0);
            return new Versao(versao.major, versao.minor, versao.patch + 1);
        }

        Versao aplicarRelease() {
            if (historico.isEmpty()) return versao;
            String tipoDominante = "patch";
            int melhorPrioridade = 0;
            for (Commit c : historico) {
                int prioridade = c.tipo.equals("major") ? 3 : c.tipo.equals("minor") ? 2 : 1;
                if (prioridade > melhorPrioridade) { melhorPrioridade = prioridade; tipoDominante = c.tipo; }
            }
            versao = proximaVersao(tipoDominante);
            historico.clear();
            return versao;
        }

        Versao congelarBaseline() {
            baseline = versao;
            return baseline;
        }

        boolean houveMudancaDesdeBaseline() {
            return baseline != null && !baseline.igual(versao);
        }
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(64));
        System.out.println("  GQS2801 - SIMULADOR DE VERSIONAMENTO E BASELINE");
        System.out.println("=".repeat(64));

        Repositorio repo = new Repositorio();
        System.out.println("\n  Versão inicial: " + repo.versao.formatar());

        repo.congelarBaseline();
        System.out.println("  Baseline congelada em: " + repo.baseline.formatar());

        System.out.println("\n--- Ciclo de mudanças (exemplo da Aula 28) ---");
        repo.commitar("a1b2c3d", "Corrige cálculo de disponibilidade (MTBF/MTTR)", "patch");
        System.out.println("  commit a1b2c3d: \"Corrige cálculo de disponibilidade (MTBF/MTTR)\" [patch]");
        repo.commitar("e4f5g6h", "Adiciona endpoint GET /clientes/{id}/historico", "minor");
        System.out.println("  commit e4f5g6h: \"Adiciona endpoint GET /clientes/{id}/historico\" [minor]");

        Versao novaVersao = repo.aplicarRelease();
        System.out.println("\n  Release aplicada (maior impacto vence): " + novaVersao.formatar());
        System.out.println("  Houve mudança desde a baseline? " + repo.houveMudancaDesdeBaseline());

        System.out.println("\n--- Mudança que quebra compatibilidade ---");
        repo.commitar("i7j8k9l", "Remove campo 'endereco' do JSON de resposta", "major");
        novaVersao = repo.aplicarRelease();
        System.out.println("  Nova versão (breaking change): " + novaVersao.formatar());

        repo.congelarBaseline();
        System.out.println("  Nova baseline: " + repo.baseline.formatar());
    }
}
