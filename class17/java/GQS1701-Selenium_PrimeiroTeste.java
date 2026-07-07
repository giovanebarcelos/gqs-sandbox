/**
 * GQS1701 - Primeiro teste automatizado com Selenium WebDriver (Java).
 * Abre o Chrome, navega ate uma pagina publica e estavel de login
 * (https://the-internet.herokuapp.com/login), preenche o formulario,
 * autentica e valida a mensagem de sucesso na area segura.
 *
 * Dependencia Maven (pom.xml):
 *   <dependency>
 *     <groupId>org.seleniumhq.selenium</groupId>
 *     <artifactId>selenium-java</artifactId>
 *     <version>4.18.0</version>
 *   </dependency>
 *
 * Compilacao e execucao (com selenium-java + dependencias no classpath):
 *   javac -cp selenium-java-4.18.0.jar GQS1701-Selenium_PrimeiroTeste.java
 *   java -cp .:selenium-java-4.18.0.jar GQS1701_Selenium_PrimeiroTeste
 *
 * Requer Google Chrome instalado. O Selenium 4 localiza o ChromeDriver
 * automaticamente via Selenium Manager (nao precisa de webdriver-manager
 * externo como no Java 3.x).
 *
 * Credenciais de teste do site (publicas, uso didatico):
 *   usuario: tomsmith
 *   senha:   SuperSecretPassword!
 */
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class GQS1701_Selenium_PrimeiroTeste {

    static final String URL_LOGIN = "https://the-internet.herokuapp.com/login";
    static final String USUARIO = "tomsmith";
    static final String SENHA = "SuperSecretPassword!";

    static WebDriver criarDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return new ChromeDriver(options);
    }

    static boolean executarTeste(boolean headless) {
        WebDriver driver;
        try {
            driver = criarDriver(headless);
        } catch (WebDriverException erro) {
            System.out.println("ERRO: nao foi possivel iniciar o Chrome/ChromeDriver neste ambiente.");
            System.out.println("Detalhe: " + erro.getMessage());
            System.out.println("Verifique se o Google Chrome esta instalado e acessivel no PATH.");
            return false;
        }

        try {
            driver.get(URL_LOGIN);
            if (!driver.getTitle().contains("The Internet")) {
                throw new AssertionError("Titulo inesperado: " + driver.getTitle());
            }

            driver.findElement(By.id("username")).sendKeys(USUARIO);
            driver.findElement(By.id("password")).sendKeys(SENHA);
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement flash = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("flash"))
            );

            if (!flash.getText().contains("You logged into a secure area")) {
                throw new AssertionError("Mensagem de sucesso nao encontrada: " + flash.getText());
            }
            if (!driver.getCurrentUrl().contains("/secure")) {
                throw new AssertionError("URL inesperada: " + driver.getCurrentUrl());
            }

            System.out.println("Teste de login concluido com sucesso!");
            System.out.println("URL final: " + driver.getCurrentUrl());
            System.out.println("Mensagem: " + flash.getText().trim());
            return true;
        } catch (Exception erro) {
            System.out.println("FALHA no teste: " + erro.getMessage());
            return false;
        } finally {
            driver.quit();
        }
    }

    public static void main(String[] args) {
        boolean headless = false;
        for (String arg : args) {
            if (arg.equals("--headless")) {
                headless = true;
            }
        }
        boolean sucesso = executarTeste(headless);
        System.exit(sucesso ? 0 : 1);
    }
}
