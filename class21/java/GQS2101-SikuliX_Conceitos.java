/**
 * GQS2101 - Conceitos de automacao visual (estilo SikuliX), em Java puro.
 *
 * O SikuliX real (org.sikuli.script.*) exige um ambiente grafico e a
 * biblioteca nativa de reconhecimento de imagem (OpenCV). Este arquivo NAO
 * depende do jar do SikuliX: ele MODELA a logica central da ferramenta
 * (busca de imagem na tela por similaridade, click, type, wait, exists)
 * usando uma "tela simulada" (mock), para fins didaticos.
 *
 * Compilacao e execucao:
 *   javac GQS2101-SikuliX_Conceitos.java
 *   java -cp . GQS2101_SikuliX_Conceitos
 */
import java.util.HashMap;
import java.util.Map;

class Regiao {
    int x, y, largura, altura;
    double similaridade;

    Regiao(int x, int y, int largura, int altura, double similaridade) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.similaridade = similaridade;
    }

    int centroX() {
        return x + largura / 2;
    }

    int centroY() {
        return y + altura / 2;
    }
}

/** Equivalente conceitual de org.sikuli.script.Settings. */
class Settings {
    static double MinSimilarity = 0.7;
}

class ImageMatcher {

    private double minSimilarity;
    private final Map<String, Regiao> tela = new HashMap<>();

    ImageMatcher() {
        this(Settings.MinSimilarity);
    }

    ImageMatcher(double minSimilarity) {
        this.minSimilarity = minSimilarity;
    }

    void setMinSimilarity(double valor) {
        this.minSimilarity = valor;
    }

    double getMinSimilarity() {
        return minSimilarity;
    }

    void registrarElemento(String imagem, int x, int y, int largura, int altura, double similaridade) {
        tela.put(imagem, new Regiao(x, y, largura, altura, similaridade));
    }

    Regiao find(String imagem) {
        Regiao regiao = tela.get(imagem);
        if (regiao == null || regiao.similaridade < minSimilarity) {
            return null;
        }
        return regiao;
    }

    boolean exists(String imagem) {
        return find(imagem) != null;
    }

    boolean click(String imagem) {
        Regiao regiao = find(imagem);
        if (regiao == null) {
            System.out.printf("  [click] FALHOU: '%s' nao encontrada (similaridade minima exigida: %.2f)%n",
                imagem, minSimilarity);
            return false;
        }
        System.out.printf("  [click] '%s' encontrada em (%d, %d) com similaridade %.2f -> clique simulado%n",
            imagem, regiao.centroX(), regiao.centroY(), regiao.similaridade);
        return true;
    }

    boolean type(String imagem, String texto) {
        if (!click(imagem)) {
            return false;
        }
        System.out.printf("  [type] digitando '%s' no campo '%s'%n", texto, imagem);
        return true;
    }

    Regiao wait(String imagem, double timeoutSegundos, double intervaloSegundos) {
        double decorrido = 0.0;
        while (decorrido < timeoutSegundos) {
            Regiao regiao = find(imagem);
            if (regiao != null) {
                System.out.printf("  [wait] '%s' apareceu apos %.1fs%n", imagem, decorrido);
                return regiao;
            }
            try {
                Thread.sleep((long) (intervaloSegundos * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            decorrido += intervaloSegundos;
        }
        System.out.printf("  [wait] TIMEOUT: '%s' nao apareceu em %.1fs%n", imagem, timeoutSegundos);
        return null;
    }
}

class GQS2101_SikuliX_Conceitos {

    static void demoLoginCalculadora() {
        ImageMatcher matcher = new ImageMatcher(0.8);

        System.out.println("=".repeat(60));
        System.out.println("  CENARIO 1: Login (imagens com boa similaridade)");
        System.out.println("=".repeat(60));
        matcher.registrarElemento("campo-usuario.png", 100, 200, 150, 30, 0.95);
        matcher.registrarElemento("campo-senha.png", 100, 250, 150, 30, 0.93);
        matcher.registrarElemento("btn-login.png", 100, 300, 80, 30, 0.90);
        matcher.registrarElemento("msg-boas-vindas.png", 100, 350, 200, 20, 0.85);

        matcher.type("campo-usuario.png", "admin");
        matcher.type("campo-senha.png", "senha123");
        matcher.click("btn-login.png");
        System.out.println(matcher.exists("msg-boas-vindas.png") ? "  Login OK!" : "  Falha no login");

        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  CENARIO 2: Calculadora 12 + 34 (botao '=' com baixa similaridade)");
        System.out.println("=".repeat(60));
        ImageMatcher matcher2 = new ImageMatcher(Settings.MinSimilarity);
        matcher2.registrarElemento("btn-1.png", 10, 10, 40, 40, 0.92);
        matcher2.registrarElemento("btn-2.png", 60, 10, 40, 40, 0.91);
        matcher2.registrarElemento("btn-3.png", 110, 10, 40, 40, 0.90);
        matcher2.registrarElemento("btn-4.png", 10, 60, 40, 40, 0.88);
        matcher2.registrarElemento("btn-plus.png", 160, 10, 40, 40, 0.85);
        // Botao "=" capturado em outra resolucao/DPI: similaridade abaixo do padrao
        matcher2.registrarElemento("btn-igual.png", 160, 110, 40, 40, 0.55);

        matcher2.click("btn-1.png");
        matcher2.click("btn-2.png");
        matcher2.click("btn-plus.png");
        matcher2.click("btn-3.png");
        matcher2.click("btn-4.png");
        boolean sucesso = matcher2.click("btn-igual.png");

        if (!sucesso) {
            System.out.println();
            System.out.println("  Mitigacao: reduzir Settings.MinSimilarity ou recapturar a imagem");
            System.out.println("  'btn-igual.png' na resolucao/DPI atual do ambiente de teste.");
            matcher2.setMinSimilarity(0.5);
            System.out.printf("  Novo MinSimilarity = %.2f%n", matcher2.getMinSimilarity());
            matcher2.click("btn-igual.png");
        }

        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  CENARIO 3: wait() por elemento que nunca aparece");
        System.out.println("=".repeat(60));
        ImageMatcher matcher3 = new ImageMatcher();
        matcher3.wait("dialogo-carregamento-fim.png", 1.0, 0.3);
    }

    public static void main(String[] args) {
        System.out.println("GQS2101 - Conceitos de Automacao Visual (modelo do SikuliX)");
        demoLoginCalculadora();
        System.out.println("\nExecucao concluida sem erros.");
    }
}
