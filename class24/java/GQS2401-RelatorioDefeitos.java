/**
 * GQS2401 - Relatório Estruturado de Defeitos (estilo TestLink/Jira).
 * Modela uma lista de defeitos com severidade, prioridade e status, e gera um
 * relatório de texto com contagens por dimensão e o indicador mais importante
 * para decidir se um release pode avançar: o percentual de bugs CRÍTICOS que
 * ainda estão ABERTOS.
 *
 * Compilação e execução:
 *   javac GQS2401-RelatorioDefeitos.java
 *   java -cp . GQS2401_RelatorioDefeitos
 */
import java.util.*;

enum Severidade {
    CRITICA("Crítica"), ALTA("Alta"), MEDIA("Média"), BAIXA("Baixa");

    final String rotulo;

    Severidade(String rotulo) {
        this.rotulo = rotulo;
    }
}

enum Prioridade {
    URGENTE("Urgente"), ALTA("Alta"), MEDIA("Média"), BAIXA("Baixa");

    final String rotulo;

    Prioridade(String rotulo) {
        this.rotulo = rotulo;
    }
}

enum Status {
    NOVO("Novo"), EM_ANDAMENTO("Em Andamento"), RESOLVIDO("Resolvido"),
    FECHADO("Fechado"), REABERTO("Reaberto");

    final String rotulo;

    Status(String rotulo) {
        this.rotulo = rotulo;
    }

    boolean estaAberto() {
        return this != FECHADO;
    }
}

class Defeito {
    String id;
    String titulo;
    Severidade severidade;
    Prioridade prioridade;
    Status status;
    String modulo;

    Defeito(String id, String titulo, Severidade severidade, Prioridade prioridade,
            Status status, String modulo) {
        this.id = id;
        this.titulo = titulo;
        this.severidade = severidade;
        this.prioridade = prioridade;
        this.status = status;
        this.modulo = modulo;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Módulo: %s | Severidade: %s | Prioridade: %s | Status: %s",
                id, titulo, modulo, severidade.rotulo, prioridade.rotulo, status.rotulo);
    }
}

class GQS2401_RelatorioDefeitos {

    static List<Defeito> criarDefeitosExemplo() {
        return List.of(
            new Defeito("BUG-001", "Mensagem de erro genérica no login com senha incorreta",
                Severidade.BAIXA, Prioridade.ALTA, Status.RESOLVIDO, "Autenticação"),
            new Defeito("BUG-002", "Sistema trava ao finalizar checkout com cupom expirado",
                Severidade.CRITICA, Prioridade.URGENTE, Status.NOVO, "Checkout"),
            new Defeito("BUG-003", "Subtotal do carrinho não atualiza ao remover item",
                Severidade.ALTA, Prioridade.ALTA, Status.EM_ANDAMENTO, "Carrinho"),
            new Defeito("BUG-004", "Ícone do carrinho desalinhado em telas pequenas",
                Severidade.BAIXA, Prioridade.BAIXA, Status.NOVO, "Interface"),
            new Defeito("BUG-005", "Frete calculado incorretamente para CEPs do Norte",
                Severidade.ALTA, Prioridade.MEDIA, Status.REABERTO, "Checkout"),
            new Defeito("BUG-006", "Perda de sessão ao pagar com cartão de crédito",
                Severidade.CRITICA, Prioridade.URGENTE, Status.EM_ANDAMENTO, "Pagamento"),
            new Defeito("BUG-007", "Busca não encontra produtos com acento no nome",
                Severidade.MEDIA, Prioridade.MEDIA, Status.NOVO, "Catálogo"),
            new Defeito("BUG-008", "Filtro de preço não reseta ao trocar de categoria",
                Severidade.MEDIA, Prioridade.BAIXA, Status.FECHADO, "Catálogo")
        );
    }

    static LinkedHashMap<String, Integer> contarPorSeveridade(List<Defeito> defeitos) {
        LinkedHashMap<String, Integer> contagem = new LinkedHashMap<>();
        for (Defeito d : defeitos) {
            contagem.merge(d.severidade.rotulo, 1, Integer::sum);
        }
        return contagem;
    }

    static LinkedHashMap<String, Integer> contarPorPrioridade(List<Defeito> defeitos) {
        LinkedHashMap<String, Integer> contagem = new LinkedHashMap<>();
        for (Defeito d : defeitos) {
            contagem.merge(d.prioridade.rotulo, 1, Integer::sum);
        }
        return contagem;
    }

    static LinkedHashMap<String, Integer> contarPorStatus(List<Defeito> defeitos) {
        LinkedHashMap<String, Integer> contagem = new LinkedHashMap<>();
        for (Defeito d : defeitos) {
            contagem.merge(d.status.rotulo, 1, Integer::sum);
        }
        return contagem;
    }

    static double percentualCriticosAbertos(List<Defeito> defeitos) {
        List<Defeito> criticos = defeitos.stream()
                .filter(d -> d.severidade == Severidade.CRITICA)
                .toList();
        if (criticos.isEmpty()) return 0.0;
        long criticosAbertos = criticos.stream().filter(d -> d.status.estaAberto()).count();
        return Math.round((double) criticosAbertos / criticos.size() * 10000.0) / 100.0;
    }

    static void imprimirTabelaContagem(String titulo, Map<String, Integer> contagem) {
        System.out.printf("%n%s%n", titulo);
        System.out.println("-".repeat(40));
        for (Map.Entry<String, Integer> entry : contagem.entrySet()) {
            System.out.printf("  %-15s %d%n", entry.getKey(), entry.getValue());
        }
    }

    static void gerarRelatorio(List<Defeito> defeitos) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS2401 - RELATÓRIO DE DEFEITOS");
        System.out.println("=".repeat(60));

        System.out.printf("%nTotal de defeitos registrados: %d%n%n", defeitos.size());
        System.out.println("Lista completa:");
        for (Defeito d : defeitos) {
            System.out.println("  " + d);
        }

        imprimirTabelaContagem("Contagem por Severidade", contarPorSeveridade(defeitos));
        imprimirTabelaContagem("Contagem por Prioridade", contarPorPrioridade(defeitos));
        imprimirTabelaContagem("Contagem por Status", contarPorStatus(defeitos));

        double pctCriticosAbertos = percentualCriticosAbertos(defeitos);
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("  INDICADOR DE RELEASE: %.2f%% dos bugs CRÍTICOS estão ABERTOS%n", pctCriticosAbertos);
        if (pctCriticosAbertos > 0) {
            System.out.println("  >> Recomendação: NÃO liberar o release enquanto houver crítico aberto.");
        } else {
            System.out.println("  >> Recomendação: nenhum crítico aberto; release pode avançar (avaliar demais riscos).");
        }
        System.out.println("=".repeat(60));
    }

    public static void main(String[] args) {
        List<Defeito> defeitos = criarDefeitosExemplo();
        gerarRelatorio(defeitos);
    }
}
