Cada laboratório terá:  
  
1. Objetivo curto.  
  
2. Pré-requisitos.  
  
3. Passos passo-a-passo (com comandos).  
  
4. Código e arquivos prontos (copie/cole em arquivos).  
  
5. Como rodar e verificar o resultado.  
  
6. Dicas de troubleshooting.  
  
---  
  
# Laboratório 1 — Instalação do FitNesse e estrutura básica  
  
**Objetivo:** Instalar FitNesse, criar páginas simples, rodar testes básicos.  
  
**Pré-requisitos:** Java 17+, `wget` ou `curl`.  
  
### Passos  
  
1. Crie uma pasta do projeto:  
  
```bash  
mkdir fitnesse-lab1 && cd fitnesse-lab1  
```  
  
2. Baixe o `fitnesse-standalone.jar` (exemplo com nome genérico — use a versão mais recente) e rode:  
  
```bash  
# download (ajuste se tiver URL/version específica)  
wget -O fitnesse-standalone.jar https://github.com/unclebob/fitnesse/releases/download/v20240410/fitnesse-standalone.jar  
# iniciar  
java -jar fitnesse-standalone.jar -p 8080  
```  
  
> Resultado: FitNesse rodando em `http://localhost:8080`  
  
3. Abra `http://localhost:8080` no navegador. Crie as páginas: `FrontPage`, `MyTest`, `MySuite` (a UI tem botão `Add`).  
  
4. Crie um teste simples (no editor de página `MyTest`) como Decision Table:  
  
```  
!|fitnesse.slim.test.TestFixture|  
|a|b|sum?|  
|1|2|3|  
```  
  
> Note: `fitnesse.slim.test.TestFixture` é um exemplo; vamos usar fixtures reais no Lab 2.  
  
5. Estrutura de páginas recomendada:  
  
```  
FrontPage  
  |- Lab1  
      |- MyTest  
      |- MySuite (marcar como Suite)  
```  
  
### Verificação  
  
- Execute `MyTest` e verifique se aparece a página de resultados com PASS/FAIL.  
  
### Dicas  
  
- Se porta 8080 ocupada: `-p 9090`.  
  
- Para parar: `CTRL+C` no terminal onde executou o jar.  
  
---  
  
# Laboratório 2 — Fixtures Java simples (Maven) e Decision Tables  
  
**Objetivo:** Conectar FitNesse a fixtures Java, rodar DecisionTables e tabelas de decisão.  
  
**Pré-requisitos:** Java 17+, Maven, FitNesse (rodando).  
  
### Estrutura do projeto  
  
```  
fitnesse-java-fixtures/  
 ├─ pom.xml  
 └─ src/main/java/com/example/fixtures/CalculadoraFixture.java  
```  
  
### `pom.xml` (arquivo completo)  
  
```xml  
<project xmlns="http://maven.apache.org/POM/4.0.0" ...>  
  <modelVersion>4.0.0</modelVersion>  
  <groupId>com.example</groupId>  
  <artifactId>fitnesse-fixtures</artifactId>  
  <version>1.0-SNAPSHOT</version>  
  <properties>  
    <maven.compiler.source>17</maven.compiler.source>  
    <maven.compiler.target>17</maven.compiler.target>  
  </properties>  
  <dependencies>  
    <!-- Slim (normalmente não precisa como dependency específica; incluí apenas para referência) -->  
    <dependency>  
      <groupId>org.fitnesse</groupId>  
      <artifactId>fitnesse</artifactId>  
      <version>20240410</version>  
      <scope>test</scope>  
    </dependency>  
  </dependencies>  
</project>  
```  
  
### Fixture Java — `CalculadoraFixture.java`  
  
```java  
package com.example.fixtures;  
  
public class CalculadoraFixture {  
    private int a;  
    private int b;  
  
    public void setA(int a) { this.a = a; }  
    public void setB(int b) { this.b = b; }  
  
    public int soma() { return this.a + this.b; }  
    public int subtrai() { return this.a - this.b; }  
    public int multiplica() { return this.a * this.b; }  
    public double divide() { return (double) this.a / this.b; }  
}  
```  
  
### Construir o JAR  
  
```bash  
mvn package  
# o JAR ficará em target/fitnesse-fixtures-1.0-SNAPSHOT.jar  
```  
  
### Configurar FitNesse para usar o JAR  
  
1. Copie o JAR para o diretório do FitNesse (ou coloque no classpath ao iniciar FitNesse):  
  
```bash  
cp target/fitnesse-fixtures-1.0-SNAPSHOT.jar /caminho/para/fitnesse/  
# ou inicie fitnesse com classpath:  
java -cp "fitnesse-standalone.jar:target/fitnesse-fixtures-1.0-SNAPSHOT.jar" fitnesse.Main -p 8080  
```  
  
2. No FitNesse crie página `Lab2.Calculadora` com conteúdo:  
  
```  
!|import|  
|com.example.fixtures|  
  
!|CalculadoraFixture|  
|a|b|soma?|subtrai?|multiplica?|  
|1|2|3| -1 |2|  
|5|3|8|2|15|  
```  
  
3. Execute a página. Resultado: PASS se os métodos retornarem os valores esperados.  
  
### Troubleshooting  
  
- `ClassNotFoundException`: verifique se JAR está no classpath do FitNesse.  
  
- Use `!define TEST_RUNNER {...}` só se for custom.  
  
---  
  
# Laboratório 3 — Testes com Banco de Dados (PostgreSQL/H2) e Fixtures JDBC  
  
**Objetivo:** Fazer fixtures que conectam ao banco, preparar dados com `SetUp`/`TearDown`.  
  
**Pré-requisitos:** Docker (para PostgreSQL), Maven.  
  
### Docker Compose (Postgres)  
  
Crie `docker-compose.yml`:  
  
```yaml  
version: '3.8'  
services:  
  db:  
    image: postgres:15  
    environment:  
      POSTGRES_USER: fituser  
      POSTGRES_PASSWORD: fitpass  
      POSTGRES_DB: fitdb  
    ports:  
      - "5432:5432"  
    volumes:  
      - pgdata:/var/lib/postgresql/data  
volumes:  
  pgdata:  
```  
  
Rodar:  
  
```bash  
docker-compose up -d  
```  
  
### Fixture JDBC — `DatabaseFixture.java`  
  
```java  
package com.example.fixtures;  
  
import java.sql.*;  
import java.util.ArrayList;  
import java.util.List;  
  
public class DatabaseFixture {  
    private Connection conn;  
  
    public void connect(String url, String user, String pass) throws Exception {  
        conn = DriverManager.getConnection(url, user, pass);  
    }  
  
    public void executeUpdate(String sql) throws Exception {  
        try (Statement st = conn.createStatement()) {  
            st.executeUpdate(sql);  
        }  
    }  
  
    public List<List<String>> query(String sql) throws Exception {  
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {  
            ResultSetMetaData md = rs.getMetaData();  
            int cols = md.getColumnCount();  
            List<List<String>> rows = new ArrayList<>();  
            while (rs.next()) {  
                List<String> r = new ArrayList<>();  
                for (int i = 1; i <= cols; i++) {  
                    r.add(rs.getString(i));  
                }  
                rows.add(r);  
            }  
            return rows;  
        }  
    }  
  
    public void close() throws Exception {  
        if (conn != null) conn.close();  
    }  
}  
```  
  
### SQL Init (crie `init.sql`)  
  
```sql  
CREATE TABLE usuarios (  
  id SERIAL PRIMARY KEY,  
  nome VARCHAR(100),  
  email VARCHAR(100)  
);  
  
INSERT INTO usuarios (nome, email) VALUES ('Alice','alice@example.com');  
```  
  
Rodar init:  
  
```bash  
# esperar o postgres iniciar (pode demorar)  
docker exec -i $(docker ps -q -f ancestor=postgres:15) psql -U fituser -d fitdb -c "CREATE TABLE ... "  
# ou usar psql no host se tiver  
```  
  
### FitNesse pages  
  
`Lab3.SetUp`:  
  
```  
!|import|  
|com.example.fixtures|  
  
!|DatabaseFixture|  
|connect|jdbc:postgresql://localhost:5432/fitdb|fituser|fitpass|  
|executeUpdate|INSERT INTO usuarios (nome,email) VALUES ('Bob','bob@example.com')|  
```  
  
`Lab3.MyDBTest`:  
  
```  
!|DatabaseFixture|  
|query|SELECT nome,email FROM usuarios ORDER BY id|  
|nome|email|  
|Alice|alice@example.com|  
|Bob|bob@example.com|  
```  
  
### Como rodar  
  
- Inicie o Postgres via Docker Compose.  
  
- Inicie FitNesse com o classpath contendo o JAR das fixtures.  
  
- Abra `Lab3.MyDBTest`.  
  
### Dica  
  
- Para testes isolados use banco em memória H2 para velocidade (URL `jdbc:h2:mem:test;DB_CLOSE_DELAY=-1`).  
  
---  
  
# Laboratório 4 — Testes de API REST com OkHttp (ou HttpClient)  
  
**Objetivo:** Criar fixtures que chamam endpoints REST e validam status/JSON.  
  
**Pré-requisitos:** Dependência `okhttp` no `pom.xml`.  
  
### Dependência Maven (adicionar)  
  
```xml  
<dependency>  
  <groupId>com.squareup.okhttp3</groupId>  
  <artifactId>okhttp</artifactId>  
  <version>4.11.0</version>  
</dependency>  
<dependency>  
  <groupId>com.fasterxml.jackson.core</groupId>  
  <artifactId>jackson-databind</artifactId>  
  <version>2.15.2</version>  
</dependency>  
```  
  
### `RestApiFixture.java`  
  
```java  
package com.example.fixtures;  
  
import okhttp3.*;  
import com.fasterxml.jackson.databind.JsonNode;  
import com.fasterxml.jackson.databind.ObjectMapper;  
  
public class RestApiFixture {  
    private final OkHttpClient client = new OkHttpClient();  
    private final ObjectMapper mapper = new ObjectMapper();  
    private Response lastResponse;  
  
    public void callGet(String url) throws Exception {  
        Request r = new Request.Builder().url(url).get().build();  
        lastResponse = client.newCall(r).execute();  
    }  
  
    public void callPost(String url, String jsonBody) throws Exception {  
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));  
        Request r = new Request.Builder().url(url).post(body).build();  
        lastResponse = client.newCall(r).execute();  
    }  
  
    public int status() { return lastResponse.code(); }  
  
    public boolean responseContains(String trecho) throws Exception {  
        String body = lastResponse.body().string();  
        return body.contains(trecho);  
    }  
  
    public String responseBody() throws Exception {  
        return lastResponse.body().string();  
    }  
  
    public boolean jsonPathEquals(String path, String expected) throws Exception {  
        String body = lastResponse.body().string();  
        JsonNode node = mapper.readTree(body);  
        // path simples: use node.at("/field")  
        JsonNode found = node.at(path);  
        return found != null && found.asText().equals(expected);  
    }  
}  
```  
  
> Nota: `jsonPathEquals` usa `JsonNode.at` com caminho JSON Pointer (ex: `/user/name`).  
  
### FitNesse test  
  
```  
!|import|  
|com.example.fixtures|  
  
!|RestApiFixture|  
|callGet|http://jsonplaceholder.typicode.com/posts/1|  
|status|200?|  
|responseContains|userId|  
```  
  
### Como testar  
  
- Se tiver uma API local: use `http://localhost:8080/api/...`.  
  
- Para APIs públicas de exemplo use `jsonplaceholder.typicode.com`.  
  
---  
  
# Laboratório 5 — Suíte de Integração (fluxo completo) com variáveis e Setup/TearDown  
  
**Objetivo:** Montar uma suíte que encadeia múltiplas etapas (login → criar recurso → validar → deletar).  
  
### Estrutura FitNesse  
  
```  
FrontPage  
  |- Lab5  
      |- SuitePage (tipo Suite)  
      |- SetUp (prepara dados)  
      |- TearDown (limpa)  
      |- 01_Login  
      |- 02_CreateCustomer  
      |- 03_GetCustomer  
      |- 04_DeleteCustomer  
```  
  
### Exemplo de `01_Login`  
  
```  
!|import|  
|com.example.fixtures|  
  
!|RestApiFixture|  
|callPost|http://localhost:8080/api/login|{"user":"admin","pass":"admin"}|  
|status|200?|  
!define TOKEN {${responseBody}}   # simplificação: use extractions reais em fixtures  
```  
  
### `02_CreateCustomer`  
  
```  
!|RestApiFixture|  
|callPost|http://localhost:8080/api/customers|{"name":"Joao","email":"joao@ex.com"}|  
|status|201?|  
!define CUSTOMER_ID {123}  # extraido real via fixture ideally  
```  
  
### Shared variables  
  
- Use `!define VAR {valor}` e `${VAR}` para reutilizar.  
  
- Também é possível usar páginas `SuiteSetUp` / `SuiteTearDown`.  
  
### Como rodar a suíte inteira  
  
- Acesse a página `Lab5.SuitePage` e clique em `Run`.  
  
### Boas práticas  
  
- Cada etapa deve ser idempotente (ou o SetUp limpa/garante estado).  
  
- Use `SetUp` para inicializar e `TearDown` para limpar (ex: deletar usuários criados).  
  
---  
  
# Laboratório 6 — Integração Contínua (Jenkins + GitHub Actions) e execução headless via CLI  
  
**Objetivo:** Rodar FitNesse em CI, gerar relatórios JUnit/XML e integrar com pipeline.  
  
### Executando FitNesse por CLI (modo non-GUI)  
  
Com FitNesse standalone você pode executar uma suite e gerar saída:  
  
```bash  
# Rodar suíte e imprimir resultado em texto  
java -jar fitnesse-standalone.jar -p 8080 &  
# executar comando que dispara suite e sai  
java -jar fitnesse-standalone.jar -c "Lab5.SuitePage?suite&format=xml" > suite-result.xml  
```  
  
> Ajuste `format=xml` para obter XML legível/convertível.  
  
### Jenkins pipeline (Jenkinsfile)  
  
```groovy  
pipeline {  
  agent any  
  stages {  
    stage('Checkout') {  
      steps { checkout scm }  
    }  
    stage('Build Fixtures') {  
      steps { sh 'mvn -f fitnesse-fixtures/pom.xml package -DskipTests' }  
    }  
    stage('Start Services') {  
      steps {  
        sh 'docker-compose -f docker-compose.yml up -d'  
      }  
    }  
    stage('Start FitNesse') {  
      steps {  
        sh 'nohup java -cp "fitnesse-standalone.jar:fitnesse-fixtures/target/fitnesse-fixtures-1.0-SNAPSHOT.jar" fitnesse.Main -p 8080 &'  
        sleep 5  
      }  
    }  
    stage('Run Suite') {  
      steps {  
        sh 'java -jar fitnesse-standalone.jar -c "Lab5.SuitePage?suite&format=xml" > results/suite-result.xml'  
        junit 'results/suite-result.xml' // se XML compatível  
      }  
    }  
  }  
  post {  
    always {  
      sh 'docker-compose -f docker-compose.yml down'  
    }  
  }  
}  
```  
  
### GitHub Actions workflow (exemplo)  
  
`.github/workflows/fitnesse.yml`:  
  
```yaml  
name: FitNesse CI  
on: [push, pull_request]  
jobs:  
  test:  
    runs-on: ubuntu-latest  
    services:  
      postgres:  
        image: postgres:15  
        env:  
          POSTGRES_USER: fituser  
          POSTGRES_PASSWORD: fitpass  
          POSTGRES_DB: fitdb  
        ports: ['5432:5432']  
    steps:  
      - uses: actions/checkout@v4  
      - name: Set up JDK 17  
        uses: actions/setup-java@v4  
        with: distribution: 'temurin', java-version: '17'  
      - name: Build fixtures  
        run: mvn -f fitnesse-fixtures/pom.xml package -DskipTests  
      - name: Start FitNesse  
        run: nohup java -cp "fitnesse-standalone.jar:fitnesse-fixtures/target/fitnesse-fixtures-1.0-SNAPSHOT.jar" fitnesse.Main -p 8080 &  
      - name: Run suite  
        run: java -jar fitnesse-standalone.jar -c "Lab5.SuitePage?suite&format=xml" > suite-result.xml  
      - name: Upload results  
        uses: actions/upload-artifact@v4  
        with:  
          name: fitnesse-results  
          path: suite-result.xml  
```  
  
### Gerar JUnit XML compatível  
  
- FitNesse tem opções para exportar em formatos que podem ser convertidos para JUnit. Se necessário, use scripts que transformam o XML do FitNesse para JUnit ou use `fitnesse.FitNesseRunner` libs que geram JUnit.  
  
---  
  
# Extras úteis (Selenium, FitLibrary, Spring Boot)  
  
### Selenium + FitNesse (UI tests)  
  
- Adicione dependência `selenium-java`.  
  
- Crie fixture que inicia `WebDriver` (ChromeDriver/Gecko).  
  
- Exemplo simples:  
  
```java  
WebDriver driver = new ChromeDriver();  
driver.get("http://localhost:8080");  
driver.findElement(By.id("login")).sendKeys("user");  
```  
  
- Para rodar headless em CI: use Chrome headless flags.  
  
### FitLibrary (flow fixtures)  
  
- Inclua `fitlibrary` se quiser fixtures orientadas a fluxo/DSL.  
  
- FitLibrary facilita testes de aceitação com passos legíveis.  
  
### Integração com Spring Boot  
  
- Injete `ApplicationContext` nas fixtures ou exponha serviços REST que os fixtures consomem.  
  
- Outra abordagem: usar TestContainers para rodar toda a aplicação durante os testes.  
  
---  
  
# Arquivos que você pode copiar e colar (resumo rápido)  
  
- `pom.xml` (módulo fixtures)  
  
- `CalculadoraFixture.java`  
  
- `DatabaseFixture.java`  
  
- `RestApiFixture.java`  
  
- `docker-compose.yml` (Postgres)  
  
- `Jenkinsfile` e `.github/workflows/fitnesse.yml`  
  
- FitNesse pages (`SetUp`, `Suite`, `Test`): já exemplificados acima.  
  
  
