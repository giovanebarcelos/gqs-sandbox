/**
 * GQS2501 - Simulação de Pipeline de CI/CD (build -> test -> analyse -> deploy).
 * Executa em sequência as etapas típicas de um pipeline (Slide 7 da Aula 25),
 * imprimindo o progresso de cada etapa e aplicando a regra de "fail fast": se uma
 * etapa falhar, as etapas seguintes NÃO são executadas e o pipeline é marcado
 * como reprovado -- sem depender de Jenkins, GitLab CI ou GitHub Actions reais.
 *
 * Compilação e execução:
 *   javac GQS2501-SimulacaoPipeline.java
 *   java -cp . GQS2501_SimulacaoPipeline
 */
import java.util.*;

class Etapa {
    String nome;
    double duracaoS;
    double chanceSucesso;
    boolean sucesso;
    String mensagem = "";

    Etapa(String nome, double duracaoS, double chanceSucesso) {
        this.nome = nome;
        this.duracaoS = duracaoS;
        this.chanceSucesso = chanceSucesso;
    }

    boolean executar(Random rng) {
        try {
            Thread.sleep((long) (Math.min(duracaoS, 0.05) * 1000));
        } catch (InterruptedException ignored) {
        }
        sucesso = rng.nextDouble() < chanceSucesso;
        mensagem = sucesso ? "OK" : "FALHOU";
        return sucesso;
    }
}

class Pipeline {
    String nome;
    List<Etapa> etapas;
    Random rng;
    String etapaQueFalhou = null;

    Pipeline(String nome, List<Etapa> etapas, long seed) {
        this.nome = nome;
        this.etapas = etapas;
        this.rng = new Random(seed);
    }

    boolean executar() {
        System.out.println("=".repeat(60));
        System.out.printf("  PIPELINE: %s%n", nome);
        System.out.println("=".repeat(60));

        for (int i = 0; i < etapas.size(); i++) {
            Etapa etapa = etapas.get(i);
            System.out.printf("%n[%d/%d] Executando: %s ... ", i + 1, etapas.size(), etapa.nome);
            boolean ok = etapa.executar(rng);
            System.out.printf("[%s] (%.1fs)%n", etapa.mensagem, etapa.duracaoS);
            if (!ok) {
                etapaQueFalhou = etapa.nome;
                System.out.printf("%n>> FAIL FAST: etapa '%s' falhou. Etapas seguintes NÃO serão executadas.%n",
                        etapa.nome);
                return false;
            }
        }
        return true;
    }

    void imprimirResultadoFinal() {
        System.out.println("\n" + "=".repeat(60));
        if (etapaQueFalhou == null) {
            System.out.println("  RESULTADO FINAL: PIPELINE APROVADO -- deploy concluído com sucesso");
        } else {
            System.out.printf("  RESULTADO FINAL: PIPELINE REPROVADO na etapa '%s'%n", etapaQueFalhou);
        }
        System.out.println("=".repeat(60));
    }
}

class GQS2501_SimulacaoPipeline {

    static Pipeline criarPipelineSucesso() {
        List<Etapa> etapas = List.of(
            new Etapa("Checkout", 0.2, 1.0),
            new Etapa("Build", 0.5, 0.98),
            new Etapa("Unit Tests", 0.8, 0.95),
            new Etapa("Static Analysis (Quality Gate)", 0.6, 0.95),
            new Etapa("Package", 0.3, 0.99),
            new Etapa("Deploy Staging", 0.4, 0.97),
            new Etapa("Deploy Prod", 0.4, 0.97)
        );
        return new Pipeline("app-techstore (branch: main)", etapas, 7);
    }

    static Pipeline criarPipelineComFalha() {
        List<Etapa> etapas = List.of(
            new Etapa("Checkout", 0.2, 1.0),
            new Etapa("Build", 0.5, 1.0),
            new Etapa("Unit Tests", 0.8, 0.0),
            new Etapa("Static Analysis (Quality Gate)", 0.6, 1.0),
            new Etapa("Package", 0.3, 1.0),
            new Etapa("Deploy Staging", 0.4, 1.0),
            new Etapa("Deploy Prod", 0.4, 1.0)
        );
        return new Pipeline("app-techstore (branch: feature/checkout-com-bug)", etapas, 1);
    }

    public static void main(String[] args) {
        Pipeline pipeline1 = criarPipelineSucesso();
        pipeline1.executar();
        pipeline1.imprimirResultadoFinal();

        System.out.println();

        Pipeline pipeline2 = criarPipelineComFalha();
        pipeline2.executar();
        pipeline2.imprimirResultadoFinal();
    }
}
