  
  
Esses laboratórios cobrem desde a **automação básica de navegador** até **testes de sistemas reais com page objects, waits, screenshots e execução paralela**.  
  
---  
  
# 🧪 **Laboratório 1 – Instalação e Primeiro Teste no Selenium**  
  
### 🎯 Objetivo  
  
Configurar o ambiente Selenium e abrir o primeiro site automaticamente.  
  
### 🧰 Pré-requisitos  
  
- Java 17+  
  
- Maven  
  
- Navegador Chrome  
  
- ChromeDriver compatível com a versão do seu Chrome  
  (ou use WebDriverManager para baixar automaticamente)  
  
---  
  
### 1️⃣ Criar projeto Maven  
  
```bash  
mkdir selenium-lab1 && cd selenium-lab1  
mvn archetype:generate -DgroupId=com.example.selenium -DartifactId=selenium-lab1 -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false  
cd selenium-lab1  
```  
  
---  
  
### 2️⃣ `pom.xml` – adicionar dependências  
  
```xml  
<dependencies>  
  <dependency>  
    <groupId>org.seleniumhq.selenium</groupId>  
    <artifactId>selenium-java</artifactId>  
    <version>4.22.0</version>  
  </dependency>  
  <dependency>  
    <groupId>io.github.bonigarcia</groupId>  
    <artifactId>webdrivermanager</artifactId>  
    <version>5.9.0</version>  
  </dependency>  
  <dependency>  
    <groupId>org.junit.jupiter</groupId>  
    <artifactId>junit-jupiter-api</artifactId>  
    <version>5.10.0</version>  
    <scope>test</scope>  
  </dependency>  
  <dependency>  
    <groupId>org.junit.jupiter</groupId>  
    <artifactId>junit-jupiter-engine</artifactId>  
    <version>5.10.0</version>  
    <scope>test</scope>  
  </dependency>  
</dependencies>  
```  
  
---  
  
### 3️⃣ Criar o teste `GoogleTest.java`  
  
`src/test/java/com/example/selenium/GoogleTest.java`:  
  
```java  
package com.example.selenium;  
  
import org.junit.jupiter.api.*;  
import org.openqa.selenium.*;  
import org.openqa.selenium.chrome.ChromeDriver;  
import io.github.bonigarcia.wdm.WebDriverManager;  
  
public class GoogleTest {  
    private WebDriver driver;  
  
    @BeforeEach  
    void setup() {  
        WebDriverManager.chromedriver().setup();  
        driver = new ChromeDriver();  
    }  
  
    @Test  
    void abrirGoogle() {  
        driver.get("https://www.google.com");  
        Assertions.assertEquals("Google", driver.getTitle());  
    }  
  
    @AfterEach  
    void teardown() {  
        driver.quit();  
    }  
}  
```  
  
---  
  
### 4️⃣ Rodar o teste  
  
```bash  
mvn test  
```  
  
> 🟢 Resultado esperado: o Chrome abre, carrega o Google e fecha automaticamente.  
  
---  
  
# 🧪 **Laboratório 2 – Localizando Elementos e Fazendo Interações**  
  
### 🎯 Objetivo  
  
Aprender a localizar elementos (por `id`, `name`, `cssSelector`, `xpath`) e interagir (digitar, clicar).  
  
---  
  
### 1️⃣ Novo teste: `LoginFormTest.java`  
  
Crie arquivo `src/test/java/com/example/selenium/LoginFormTest.java`:  
  
```java  
package com.example.selenium;  
  
import org.junit.jupiter.api.*;  
import org.openqa.selenium.*;  
import org.openqa.selenium.chrome.ChromeDriver;  
import io.github.bonigarcia.wdm.WebDriverManager;  
  
public class LoginFormTest {  
    WebDriver driver;  
  
    @BeforeEach  
    void setUp() {  
        WebDriverManager.chromedriver().setup();  
        driver = new ChromeDriver();  
    }  
  
    @Test  
    void preencherFormulario() {  
        driver.get("https://www.saucedemo.com/");  
  
        driver.findElement(By.id("user-name")).sendKeys("standard_user");  
        driver.findElement(By.id("password")).sendKeys("secret_sauce");  
        driver.findElement(By.id("login-button")).click();  
  
        Assertions.assertTrue(driver.getCurrentUrl().contains("inventory"));  
    }  
  
    @AfterEach  
    void tearDown() {  
        driver.quit();  
    }  
}  
```  
  
---  
  
### 2️⃣ Rodar  
  
```bash  
mvn test  
```  
  
🟢 **Resultado esperado:** o navegador faz login e muda para a tela de produtos.  
  
---  
  
# 🧪 **Laboratório 3 – Esperas, Alerts e Dropdowns**  
  
### 🎯 Objetivo  
  
Aprender a lidar com **esperas explícitas**, **pop-ups/alerts** e **menus suspensos (select)**.  
  
---  
  
### 1️⃣ Exemplo: `WaitsAndAlertsTest.java`  
  
```java  
package com.example.selenium;  
  
import org.junit.jupiter.api.*;  
import org.openqa.selenium.*;  
import org.openqa.selenium.support.ui.*;  
import org.openqa.selenium.chrome.ChromeDriver;  
import io.github.bonigarcia.wdm.WebDriverManager;  
  
import java.time.Duration;  
  
public class WaitsAndAlertsTest {  
    WebDriver driver;  
    WebDriverWait wait;  
  
    @BeforeEach  
    void setup() {  
        WebDriverManager.chromedriver().setup();  
        driver = new ChromeDriver();  
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));  
    }  
  
    @Test  
    void usarEsperaEAlert() {  
        driver.get("https://the-internet.herokuapp.com/javascript_alerts");  
  
        driver.findElement(By.xpath("//button[text()='Click for JS Alert']")).click();  
  
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());  
        Assertions.assertEquals("I am a JS Alert", alert.getText());  
        alert.accept();  
    }  
  
    @Test  
    void selecionarDropDown() {  
        driver.get("https://the-internet.herokuapp.com/dropdown");  
  
        Select dropdown = new Select(driver.findElement(By.id("dropdown")));  
        dropdown.selectByVisibleText("Option 2");  
  
        Assertions.assertEquals("Option 2", dropdown.getFirstSelectedOption().getText());  
    }  
  
    @AfterEach  
    void tearDown() {  
        driver.quit();  
    }  
}  
```  
  
🟢 **Resultado esperado:** o teste aceita um alerta e escolhe um item em um dropdown.  
  
---  
  
# 🧪 **Laboratório 4 – Page Object Model (POM)**  
  
### 🎯 Objetivo  
  
Organizar o código de automação separando **páginas** de **testes** (boas práticas profissionais).  
  
---  
  
### 1️⃣ Estrutura  
  
```  
src/  
 └── test/java/com/example/selenium/  
      ├── pages/  
      │    ├── LoginPage.java  
      │    └── InventoryPage.java  
      └── tests/  
           └── SauceDemoPOMTest.java  
```  
  
---  
  
### 2️⃣ `LoginPage.java`  
  
```java  
package com.example.selenium.pages;  
  
import org.openqa.selenium.*;  
  
public class LoginPage {  
    private WebDriver driver;  
  
    public LoginPage(WebDriver driver) { this.driver = driver; }  
  
    private By username = By.id("user-name");  
    private By password = By.id("password");  
    private By loginBtn = By.id("login-button");  
  
    public void open() { driver.get("https://www.saucedemo.com/"); }  
  
    public void login(String user, String pass) {  
        driver.findElement(username).sendKeys(user);  
        driver.findElement(password).sendKeys(pass);  
        driver.findElement(loginBtn).click();  
    }  
}  
```  
  
### 3️⃣ `InventoryPage.java`  
  
```java  
package com.example.selenium.pages;  
  
import org.openqa.selenium.*;  
  
public class InventoryPage {  
    private WebDriver driver;  
    private By title = By.className("title");  
  
    public InventoryPage(WebDriver driver) { this.driver = driver; }  
  
    public boolean isDisplayed() {  
        return driver.findElement(title).isDisplayed();  
    }  
}  
```  
  
### 4️⃣ `SauceDemoPOMTest.java`  
  
```java  
package com.example.selenium.tests;  
  
import org.junit.jupiter.api.*;  
import org.openqa.selenium.*;  
import org.openqa.selenium.chrome.ChromeDriver;  
import io.github.bonigarcia.wdm.WebDriverManager;  
import com.example.selenium.pages.*;  
  
public class SauceDemoPOMTest {  
    WebDriver driver;  
    LoginPage loginPage;  
    InventoryPage inventoryPage;  
  
    @BeforeEach  
    void setup() {  
        WebDriverManager.chromedriver().setup();  
        driver = new ChromeDriver();  
        loginPage = new LoginPage(driver);  
        inventoryPage = new InventoryPage(driver);  
    }  
  
    @Test  
    void loginComSucesso() {  
        loginPage.open();  
        loginPage.login("standard_user", "secret_sauce");  
        Assertions.assertTrue(inventoryPage.isDisplayed());  
    }  
  
    @AfterEach  
    void teardown() {  
        driver.quit();  
    }  
}  
```  
  
🟢 **Resultado esperado:** o código está mais limpo e reutilizável — aplicável em frameworks maiores.  
  
---  
  
# 🧪 **Laboratório 5 – Screenshots, Logs e Captura de Erros**  
  
### 🎯 Objetivo  
  
Salvar evidências de testes (screenshots e logs) automaticamente em caso de falha.  
  
---  
  
### 1️⃣ `ScreenshotTest.java`  
  
```java  
package com.example.selenium;  
  
import org.junit.jupiter.api.*;  
import org.openqa.selenium.*;  
import org.openqa.selenium.chrome.ChromeDriver;  
import io.github.bonigarcia.wdm.WebDriverManager;  
  
import java.io.File;  
import java.nio.file.Files;  
  
public class ScreenshotTest {  
    WebDriver driver;  
  
    @BeforeEach  
    void setup() {  
        WebDriverManager.chromedriver().setup();  
        driver = new ChromeDriver();  
    }  
  
    @Test  
    void tirarScreenshotAoFalhar() throws Exception {  
        driver.get("https://www.saucedemo.com/");  
        try {  
            driver.findElement(By.id("user-name")).sendKeys("erro");  
            Assertions.assertTrue(driver.getTitle().contains("Inventário")); // vai falhar  
        } catch (AssertionError e) {  
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);  
            Files.copy(screenshot.toPath(), new File("screenshot-falha.png").toPath());  
            throw e;  
        }  
    }  
  
    @AfterEach  
    void teardown() {  
        driver.quit();  
    }  
}  
```  
  
🟢 **Resultado esperado:** se o teste falhar, é criada uma imagem `screenshot-falha.png` no diretório.  
  
---  
  
# 🧪 **Laboratório 6 – Execução Paralela, Headless e CI/CD**  
  
### 🎯 Objetivo  
  
Rodar testes **em paralelo**, em **modo headless** e **integrar com CI (Jenkins/GitHub Actions)**.  
  
---  
  
### 1️⃣ Executar headless (sem abrir janela)  
  
Modifique o `setup()` dos testes:  
  
```java  
ChromeOptions options = new ChromeOptions();  
options.addArguments("--headless=new"); // executa sem interface  
options.addArguments("--no-sandbox", "--disable-dev-shm-usage");  
driver = new ChromeDriver(options);  
```  
  
---  
  
### 2️⃣ Execução paralela via Maven Surefire  
  
No `pom.xml`:  
  
```xml  
<build>  
  <plugins>  
    <plugin>  
      <groupId>org.apache.maven.plugins</groupId>  
      <artifactId>maven-surefire-plugin</artifactId>  
      <version>3.2.2</version>  
      <configuration>  
        <parallel>methods</parallel>  
        <threadCount>4</threadCount>  
      </configuration>  
    </plugin>  
  </plugins>  
</build>  
```  
  
---  
  
### 3️⃣ Integração com Jenkins  
  
```groovy  
pipeline {  
  agent any  
  stages {  
    stage('Checkout') { steps { checkout scm } }  
    stage('Build & Test') {  
      steps {  
        sh 'mvn clean test -Dheadless=true'  
      }  
    }  
    stage('Archive Evidence') {  
      steps {  
        archiveArtifacts artifacts: '**/*.png', fingerprint: true  
      }  
    }  
  }  
}  
```  
  
---  
  
### 4️⃣ Integração com GitHub Actions  
  
`.github/workflows/selenium.yml`  
  
```yaml  
name: Selenium Tests  
on: [push, pull_request]  
jobs:  
  test:  
    runs-on: ubuntu-latest  
    steps:  
      - uses: actions/checkout@v4  
      - name: Set up JDK  
        uses: actions/setup-java@v4  
        with:  
          distribution: 'temurin'  
          java-version: '17'  
      - name: Run Selenium Tests (Headless)  
        run: mvn clean test  
```  
  
🟢 **Resultado esperado:**  
  
- Testes executam em paralelo e headless.  
  
- Relatórios e screenshots disponíveis no pipeline.  
  
---  
  
# ✅ **Resumo dos 6 Laboratórios**  
  
| Nº  | Tema                        | Principais Conceitos                                     |  
| --- | --------------------------- | -------------------------------------------------------- |  
| 1   | Instalação e primeiro teste | ChromeDriver, WebDriverManager, `getTitle()`             |  
| 2   | Interação com elementos     | `findElement`, `sendKeys`, `click`                       |  
| 3   | Esperas e alerts            | `WebDriverWait`, `ExpectedConditions`, `Select`, `Alert` |  
| 4   | Page Object Model           | Reutilização, design profissional                        |  
| 5   | Evidências e erros          | Screenshots, logs                                        |  
| 6   | Paralelismo e CI/CD         | Headless, Maven Surefire, Jenkins/GitHub Actions         |  
  
---  
  
  
