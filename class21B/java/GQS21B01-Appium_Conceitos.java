/**
 * GQS21B01 - Conceitos de automacao mobile (estilo Appium), em Java puro.
 *
 * O Appium real (io.appium.java_client.*) exige um Appium Server rodando
 * (Node.js) e um driver de plataforma (UiAutomator2/XCUITest) conectado a
 * um emulador ou dispositivo real. Este arquivo NAO depende do jar do
 * Appium: ele MODELA a logica central de uma sessao Appium (localizar
 * elemento por resource-id, clicar, ler texto, aguardar) usando uma
 * "tela simulada" (mock), para fins didaticos.
 *
 * Compilacao e execucao:
 *   javac GQS21B01-Appium_Conceitos.java
 *   java -cp . GQS21B01_Appium_Conceitos
 */
import java.util.HashMap;
import java.util.Map;

class Elemento {
    String resourceId;
    String texto;
    boolean visivel;

    Elemento(String resourceId, String texto, boolean visivel) {
        this.resourceId = resourceId;
        this.texto = texto;
        this.visivel = visivel;
    }

    String getText() {
        return texto;
    }
}

class AndroidDriverSimulado {

    private final String appPackage;
    private final String appActivity;
    private boolean emExecucao;
    private final Map<String, Elemento> tela = new HashMap<>();

    AndroidDriverSimulado(String appPackage, String appActivity) {
        this.appPackage = appPackage;
        this.appActivity = appActivity;
    }

    void registrarElemento(String resourceId, String texto, boolean visivel) {
        tela.put(resourceId, new Elemento(resourceId, texto, visivel));
    }

    void launchApp() {
        emExecucao = true;
        System.out.printf("  [launchApp] %s/%s iniciado%n", appPackage, appActivity);
    }

    Elemento findElementById(String resourceId) {
        Elemento elemento = tela.get(resourceId);
        if (elemento == null || !elemento.visivel) {
            return null;
        }
        return elemento;
    }

    boolean click(String resourceId) {
        Elemento elemento = findElementById(resourceId);
        if (elemento == null) {
            System.out.printf("  [click] FALHOU: elemento '%s' nao encontrado ou nao visivel%n", resourceId);
            return false;
        }
        System.out.printf("  [click] '%s' tocado (texto atual: '%s')%n", resourceId, elemento.texto);
        return true;
    }

    String getText(String resourceId) {
        Elemento elemento = findElementById(resourceId);
        return elemento == null ? null : elemento.getText();
    }

    boolean waitForVisible(String resourceId, double timeoutSegundos, double intervaloSegundos) {
        double decorrido = 0.0;
        while (decorrido < timeoutSegundos) {
            Elemento elemento = findElementById(resourceId);
            if (elemento != null) {
                System.out.printf("  [wait] '%s' visivel apos %.1fs%n", resourceId, decorrido);
                return true;
            }
            try {
                Thread.sleep((long) (intervaloSegundos * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            decorrido += intervaloSegundos;
        }
        System.out.printf("  [wait] TIMEOUT: '%s' nao ficou visivel em %.1fs%n", resourceId, timeoutSegundos);
        return false;
    }

    void quit() {
        emExecucao = false;
        System.out.println("  [quit] sessao encerrada");
    }
}

class GQS21B01_Appium_Conceitos {

    static void demoCalculadora12Mais34() {
        final String PKG = "com.google.android.calculator";

        AndroidDriverSimulado driver = new AndroidDriverSimulado(PKG, ".Calculator");
        driver.launchApp();

        driver.registrarElemento(PKG + ":id/digit_1", "1", true);
        driver.registrarElemento(PKG + ":id/digit_2", "2", true);
        driver.registrarElemento(PKG + ":id/op_add", "+", true);
        driver.registrarElemento(PKG + ":id/digit_3", "3", true);
        driver.registrarElemento(PKG + ":id/digit_4", "4", true);
        driver.registrarElemento(PKG + ":id/eq", "=", true);
        driver.registrarElemento(PKG + ":id/result_final", "46", true);

        System.out.println("=".repeat(60));
        System.out.println("  CENARIO 1: Calculadora 12 + 34 (todos os elementos presentes)");
        System.out.println("=".repeat(60));
        for (String id : new String[]{"digit_1", "digit_2", "op_add", "digit_3", "digit_4", "eq"}) {
            driver.click(PKG + ":id/" + id);
        }

        String resultado = driver.getText(PKG + ":id/result_final");
        if ("46".equals(resultado)) {
            System.out.printf("  PASSOU: resultado = %s%n", resultado);
        } else {
            System.out.printf("  FALHOU: esperado 46, obtido %s%n", resultado);
        }

        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  CENARIO 2: resource-id incorreto (simula quebra por versao do app)");
        System.out.println("=".repeat(60));
        AndroidDriverSimulado driver2 = new AndroidDriverSimulado(PKG, ".Calculator");
        driver2.launchApp();
        driver2.registrarElemento(PKG + ":id/digit_1", "1", true);
        // "digit_2" nao foi registrado -> simula id que mudou de nome em uma nova versao
        boolean sucesso = driver2.click(PKG + ":id/digit_2");
        if (!sucesso) {
            System.out.println("  Mitigacao: reconfirmar o resource-id no Appium Inspector");
            System.out.println("  apos atualizacao do app (ver Aula 21B, Slide 9).");
        }

        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  CENARIO 3: waitForVisible por elemento que nunca aparece");
        System.out.println("=".repeat(60));
        AndroidDriverSimulado driver3 = new AndroidDriverSimulado(PKG, ".Calculator");
        driver3.launchApp();
        driver3.waitForVisible(PKG + ":id/dialogo_erro", 1.0, 0.3);

        driver.quit();
        driver2.quit();
        driver3.quit();
    }

    public static void main(String[] args) {
        System.out.println("GQS21B01 - Conceitos de Automacao Mobile (modelo do Appium)");
        demoCalculadora12Mais34();
        System.out.println("\nExecucao concluida sem erros.");
    }
}
