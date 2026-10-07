# Laboratórios FitNesse (Slim) — Guia Completo e Testado

Todos os laboratórios abaixo existem **prontos e executando** em [`repository/class18/fitnesse-labs/`](https://github.com/giovanebarcelos/gqs-sandbox/tree/main/class18/fitnesse-labs/) (6 testes, todos verdes via navegador, `run-suite.sh` e `mvn verify`). Se algo não funcionar na sua máquina, compare com essa pasta.

Cada laboratório tem: objetivo, pré-requisitos, passos, código completo, como rodar e troubleshooting.

> **Antes de começar — o que toda página de teste precisa (e que causa 90% dos "nada funciona"):**
>
> 1. `!define TEST_SYSTEM {slim}` — usa o Slim (fixtures POJO).
> 2. `!path ...` — classpath das fixtures (pasta de classes ou `.jar`; aceita `*.jar`).
> 3. A página precisa ter a propriedade **Test** (ou **Suite** na página-pai): *Properties* → marcar.
> 4. Páginas se organizam em pastas: `Labs.Lab2.Calculadora` = `FitNesseRoot/Labs/Lab2/Calculadora/content.txt`. Nomes são **WikiWords** (`MyTest` sim; `01_Login` não).
> 5. As definições `!define` e `!path` da página-pai são **herdadas** pelas filhas — por isso ficam na raiz da suíte (`Labs`).
>
> Regras de tabela Slim: entrada = setter (`a` → `setA`); saída = `metodo?`; no `script`, um argumento por célula e nome do método terminado em `;`; o Slim compara **texto** (`double` volta como `0.5`/`2.0`); exceção esperada = `EXCEPTION: =~/trecho/`.

---

# Laboratório 1 — Instalação e primeiro teste

**Objetivo:** subir o FitNesse e executar a primeira tabela.

**Pré-requisitos:** Java 17+, Maven 3.8+, `curl`.

### Passos

1. Obtenha o projeto (ou crie a pasta e copie os arquivos deste guia):

```bash
cd repository/class18/fitnesse-labs
```

2. Compile as fixtures (gera `target/classes` e `target/lib/*.jar`) e suba o servidor:

```bash
mvn -q package -DskipTests
java -jar fitnesse.jar -p 8080 -d .      # fitnesse.jar: github.com/unclebob/fitnesse/releases
# atalho que faz tudo isso:  bash start.sh
```

> O jar também pode ser baixado com: `curl -L -o fitnesse.jar https://github.com/unclebob/fitnesse/releases/latest/download/fitnesse-standalone.jar`

3. Abra `http://localhost:8080` e entre em **Labs**. A página raiz da suíte contém a configuração herdada por todas as filhas:

```
!1 Labs FitNesse - raiz da suíte

!define TEST_SYSTEM {slim}
!define COLLAPSE_SETUP {true}
!define COLLAPSE_TEARDOWN {true}

!path target/classes
!path target/lib/*.jar

!contents -R2 -g
```

4. Abra `Labs.Lab1.MyTest` e clique em **Test**:

```
!1 Lab 1 - Decision Table mínima

!|CalculadoraFixture|
|a|b|soma?|
|1|2|3|
```

> `SuiteSetUp` (página `Labs.SuiteSetUp`) roda antes de cada teste da suíte; ela contém o `import` do pacote das fixtures:
>
> ```
> !|import|
> |com.example.fixtures|
> ```

### Estrutura de páginas

```
FitNesseRoot/
 ├─ FrontPage
 └─ Labs                  (Suite: TEST_SYSTEM + !path)
     ├─ SuiteSetUp        (import)
     ├─ Lab1/MyTest
     ├─ Lab2/Calculadora
     ├─ Lab3/{SetUp,TearDown,MyDBTest}
     ├─ Lab4/{SetUp,TearDown,ApiTest}
     └─ Lab5/{SetUp,TearDown,FluxoCompleto}
```

### Verificação

- `MyTest` fica **verde** (1 célula certa). Mude `3` para `4` e veja ficar **vermelho** (`[3] expected [4]`).

### Dicas

- Porta 8080 ocupada: `-p 9090`. Parar: `Ctrl+C`.
- Sempre inicie o servidor **dentro** da pasta do projeto: `!path target/classes` é relativo a ela.

---

# Laboratório 2 — Fixtures Java (Maven) e Decision Tables

**Objetivo:** conectar o FitNesse a uma fixture Java empacotada com Maven.

**Pré-requisitos:** Java 17+, Maven, Lab 1.

### Estrutura do projeto

```
fitnesse-labs/
 ├─ pom.xml
 ├─ src/main/java/com/example/fixtures/
 │    ├─ CalculadoraFixture.java   (Lab 2)
 │    ├─ DatabaseFixture.java      (Lab 3)
 │    ├─ UsuariosDb.java           (Lab 3)
 │    └─ RestApiFixture.java       (Labs 4 e 5)
 └─ FitNesseRoot/                  (páginas wiki)
```

### `pom.xml` (completo)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>fitnesse-fixtures</artifactId>
  <version>1.0-SNAPSHOT</version>

  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.fitnesse</groupId>
      <artifactId>fitnesse</artifactId>
      <version>20250223</version>
    </dependency>
    <!-- Lab 3: banco em memória (sem Docker). Para PostgreSQL troque por org.postgresql:postgresql -->
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <version>2.2.224</version>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <!-- copia as dependências para target/lib: a página wiki usa "!path target/lib/*.jar" -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>3.6.1</version>
        <executions>
          <execution>
            <id>copy-libs</id>
            <phase>package</phase>
            <goals><goal>copy-dependencies</goal></goals>
            <configuration><outputDirectory>${project.build.directory}/lib</outputDirectory></configuration>
          </execution>
        </executions>
      </plugin>
      <!-- Lab 6: roda a suíte no build (integration-test). Falha o build se algum teste falhar -->
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <version>3.5.0</version>
        <executions>
          <execution>
            <id>fitnesse-suite</id>
            <phase>integration-test</phase>
            <goals><goal>exec</goal></goals>
            <configuration>
              <executable>java</executable>
              <arguments>
                <argument>-cp</argument><classpath/>
                <argument>fitnesseMain.FitNesseMain</argument>
                <argument>-d</argument><argument>.</argument>
                <argument>-o</argument>
                <argument>-c</argument><argument>Labs?suite&amp;format=text</argument>
              </arguments>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

O `maven-dependency-plugin` copia as dependências para `target/lib` (é o que `!path target/lib/*.jar` usa). O `exec-maven-plugin` roda a suíte na fase `integration-test` (Lab 6).

### Fixture — `CalculadoraFixture.java`

```java
package com.example.fixtures;

public class CalculadoraFixture {
    private int a;
    private int b;

    public void setA(int a) { this.a = a; }
    public void setB(int b) { this.b = b; }

    public int soma()         { return a + b; }
    public int subtrai()      { return a - b; }
    public int multiplica()   { return a * b; }
    public double divide()    { return (double) a / b; }
}
```

### Construir e rodar

```bash
mvn package -DskipTests      # classes em target/classes
bash start.sh                # ou: java -jar fitnesse.jar -p 8080 -d .
```

### Página `Labs.Lab2.Calculadora`

```
!1 Lab 2 - Calculadora

Valores `int` voltam sem casa decimal; `divide()` devolve `double` (usa `.0` ou `~=`).

!|CalculadoraFixture|
|a|b|soma?|subtrai?|multiplica?|divide?|
|1|2|3|-1|2|0.5|
|5|3|8|2|15|~=1.67|
|10|5|15|5|50|2.0|
```

- `|import|com.example.fixtures|` (já feito na `SuiteSetUp`) permite escrever `CalculadoraFixture` sem o pacote. Fora da suíte, coloque o bloco `!|import|` na própria página.
- `int` volta como `3`; `double` como `0.5` / `2.0`; `~=1.67` compara aproximado.

### Troubleshooting

| Sintoma | Causa / solução |
|---|---|
| `Could not find class CalculadoraFixture` | Falta `import`, `!path target/classes` errado, ou `mvn package` não foi executado |
| `[3.0] expected [3]` | Fixture devolve `double`; escreva `3.0` ou devolva `int` |
| Página sem botão **Test** | Marcar a propriedade **Test** |

---

# Laboratório 3 — Banco de dados com SetUp/TearDown

**Objetivo:** preparar e limpar dados com `SetUp`/`TearDown` e validar o banco com uma **Query Table**.

**Pré-requisitos:** Labs 1–2. Usamos **H2 em memória** (sem Docker, já está no `pom.xml`). PostgreSQL funciona igual — veja o final do lab.

### Fixture JDBC — `DatabaseFixture.java`

```java
package com.example.fixtures;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab 3 - Script Table (connect / executeUpdate / close) + metodo estatico de consulta
 * usado pela Query Table (UsuariosDb). A conexao e estatica porque cada tabela do
 * Slim cria uma instancia nova da fixture; ja o SetUp e as paginas de teste rodam na mesma JVM.
 */
public class DatabaseFixture {
    private static Connection conn;

    public void connect(String url, String user, String pass) throws Exception {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(url, user, pass);
        }
    }

    public void executeUpdate(String sql) throws Exception {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    public int countRows(String table) throws Exception {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public void close() throws Exception {
        if (conn != null) conn.close();
        conn = null;
    }

    /** Linhas no formato de Query Table do Slim: lista de linhas; cada linha = lista de pares [coluna, valor]. */
    static List<Object> select(String sql) throws Exception {
        List<Object> rows = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    List<String> pair = new ArrayList<>();
                    pair.add(md.getColumnLabel(i).toLowerCase());
                    pair.add(rs.getString(i));
                    row.add(pair);
                }
                rows.add(row);
            }
        }
        return rows;
    }
}
```

### Fixture de consulta — `UsuariosDb.java`

```java
package com.example.fixtures;

import java.util.List;

/** Query Table: |Query:UsuariosDb| - compara as linhas da tabela USUARIOS com a wiki. */
public class UsuariosDb {
    public List<Object> query() throws Exception {
        return DatabaseFixture.select("SELECT id, nome, email FROM usuarios ORDER BY id");
    }
}
```

> Uma Query Table instancia a classe `UsuariosDb` e chama `query()`. Ela devolve **linhas de pares `[coluna, valor]`**. A conexão é `static` porque o `SetUp` e a tabela de teste usam instâncias diferentes.

### Páginas FitNesse

`Labs.Lab3.SetUp` (roda antes de cada teste da pasta `Lab3`):

```
!|script|DatabaseFixture|
|connect;|jdbc:h2:mem:fitdb;DB_CLOSE_DELAY=-1|sa||
|execute update;|CREATE TABLE usuarios (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100), email VARCHAR(100))|
|execute update;|INSERT INTO usuarios (nome, email) VALUES ('Alice','alice@example.com')|
|execute update;|INSERT INTO usuarios (nome, email) VALUES ('Bob','bob@example.com')|
```

`Labs.Lab3.TearDown`:

```
!|script|DatabaseFixture|
|execute update;|DROP TABLE usuarios|
|close|
```

`Labs.Lab3.MyDBTest`:

```
!1 Lab 3 - Query Table sobre o banco

!|Query:UsuariosDb|
|id|nome|email|
|1|Alice|alice@example.com|
|2|Bob|bob@example.com|

Contagem via Script Table:

!|script|DatabaseFixture|
|check|count rows;|usuarios|2|
|execute update;|INSERT INTO usuarios (nome, email) VALUES ('Carol','carol@example.com')|
|check|count rows;|usuarios|3|
```

### Como rodar

Abra `Labs.Lab3.MyDBTest` → **Test** (o `SetUp` cria e popula a tabela; o `TearDown` apaga e fecha a conexão, então o teste pode rodar várias vezes).

### Usando PostgreSQL (opcional)

```yaml
# docker-compose.yml
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_USER: fituser
      POSTGRES_PASSWORD: fitpass
      POSTGRES_DB: fitdb
    ports: ["5432:5432"]
```

1. `docker compose up -d`
2. No `pom.xml` troque a dependência `h2` por `org.postgresql:postgresql:42.7.3`.
3. No `SetUp`: `|connect;|jdbc:postgresql://localhost:5432/fitdb|fituser|fitpass|` e use `SERIAL PRIMARY KEY` no lugar de `INT AUTO_INCREMENT`.

---

# Laboratório 4 — Testes de API REST

**Objetivo:** validar status e corpo de respostas HTTP a partir de uma Script Table.

**Pré-requisitos:** Labs 1–2. A fixture traz um **servidor de exemplo embutido** (porta 18080), então o lab roda sem internet. Para testar uma API real, troque as URLs.

### Fixture — `RestApiFixture.java`

```java
package com.example.fixtures;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Labs 4 e 5 - fixture de API REST com um servidor de exemplo embutido (sem internet e sem Docker).
 * Endpoints: POST /api/login, POST /api/customers (201), GET /api/customers/{id}, DELETE /api/customers/{id}.
 */
public class RestApiFixture {
    private static HttpServer server;
    private static final Map<Integer, String> customers = new ConcurrentHashMap<>();
    private static final AtomicInteger seq = new AtomicInteger(0);

    private final HttpClient client = HttpClient.newHttpClient();
    private HttpResponse<String> last;
    private String token = "";

    // ---- servidor de exemplo ----
    public String startServer(int port) throws Exception {
        if (server != null) return "ja iniciado";
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/api/login", ex -> {
            String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            boolean ok = body.contains("\"user\":\"admin\"") && body.contains("\"pass\":\"admin\"");
            reply(ex, ok ? 200 : 401, ok ? "{\"token\":\"abc123\"}" : "{\"error\":\"invalid\"}");
        });
        server.createContext("/api/customers", ex -> {
            String path = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod();
            if (method.equals("POST")) {
                String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                int id = seq.incrementAndGet();
                customers.put(id, body);
                reply(ex, 201, "{\"id\":" + id + "}");
                return;
            }
            String[] parts = path.split("/");
            Integer id = parts.length > 3 ? Integer.valueOf(parts[3]) : null;
            if (id == null || !customers.containsKey(id)) { reply(ex, 404, "{}"); return; }
            if (method.equals("GET")) reply(ex, 200, customers.get(id));
            else if (method.equals("DELETE")) { customers.remove(id); reply(ex, 204, ""); }
            else reply(ex, 405, "{}");
        });
        server.start();
        return "iniciado";
    }

    public void stopServer() {
        if (server != null) { server.stop(0); server = null; }
        customers.clear();
        seq.set(0);
    }

    private static void reply(com.sun.net.httpserver.HttpExchange ex, int code, String body) throws java.io.IOException {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(code, code == 204 ? -1 : b.length);
        if (code != 204) ex.getResponseBody().write(b);
        ex.close();
    }

    // ---- cliente ----
    public void callGet(String url) throws Exception {
        last = send(HttpRequest.newBuilder(URI.create(url)).GET());
    }

    public void callPost(String url, String jsonBody) throws Exception {
        last = send(HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)));
    }

    public void callDelete(String url) throws Exception {
        last = send(HttpRequest.newBuilder(URI.create(url)).DELETE());
    }

    private HttpResponse<String> send(HttpRequest.Builder b) throws Exception {
        return client.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    public int status() { return last.statusCode(); }

    public String responseBody() { return last.body(); }

    public boolean responseContains(String trecho) { return last.body().contains(trecho); }

    /** Extrai um campo simples ("id", "token") do JSON da ultima resposta; ideal para guardar em $simbolo. */
    public String jsonField(String campo) {
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\"" + campo + "\":\\s*\"?([^\",}]*)\"?").matcher(last.body());
        return m.find() ? m.group(1) : "";
    }
}
```

Usa apenas o JDK (`java.net.http.HttpClient`), sem dependências extras.

### Páginas FitNesse

`Labs.Lab4.SetUp` / `TearDown` (iniciam e param o servidor de exemplo):

```
!|script|RestApiFixture|
|start server;|18080|
```

```
!|script|RestApiFixture|
|stop server|
```

`Labs.Lab4.ApiTest`:

```
!1 Lab 4 - Chamadas REST

!|script|RestApiFixture|
|call post;|http://127.0.0.1:18080/api/customers|{"name":"Joao","email":"joao@ex.com"}|
|check|status|201|
|call get;|http://127.0.0.1:18080/api/customers/1|
|check|status|200|
|ensure|response contains;|joao@ex.com|
|call get;|http://127.0.0.1:18080/api/customers/99|
|check|status|404|
```

Observações:
- Método `callPost` → `|call post;|url|corpo|` (nome em palavras separadas, terminado em `;`).
- Corpo JSON numa célula só; as chaves `{}` não precisam de escape.
- `check|status|201` compara o texto `201`; `ensure|response contains;|trecho|` exige `true`.

---

# Laboratório 5 — Fluxo completo com variáveis

**Objetivo:** encadear login → criar → consultar → remover, reutilizando valores devolvidos pela API.

### Estrutura

```
Labs/Lab5/
 ├─ SetUp          (sobe o servidor de exemplo)
 ├─ TearDown       (para o servidor)
 └─ FluxoCompleto  (Test)
```

> Cada Test Page é independente: símbolos (`$ID`) **não** passam de uma página para outra. Por isso o fluxo encadeado fica **numa única página**. Para compartilhar passos entre páginas, use **Scenario Tables** numa `ScenarioLibrary`.

### `Labs.Lab5.FluxoCompleto`

```
!1 Lab 5 - Fluxo encadeado com variáveis

`!define` guarda um valor FIXO ('''${BASE}'''). Valores obtidos em tempo de execução (token, id) vão em '''símbolos''' com `$nome=` e são reutilizados como `$nome` na mesma página.

!define BASE {http://127.0.0.1:18080/api}

!|script|RestApiFixture|
|call post;|${BASE}/login|{"user":"admin","pass":"admin"}|
|check|status|200|
|$TOKEN=|json field;|token|
|check|json field;|token|abc123|
|call post;|${BASE}/customers|{"name":"Joao","email":"joao@ex.com"}|
|check|status|201|
|$ID=|json field;|id|
|call get;|${BASE}/customers/$ID|
|check|status|200|
|ensure|response contains;|Joao|
|call delete;|${BASE}/customers/$ID|
|check|status|204|
|call get;|${BASE}/customers/$ID|
|check|status|404|

Login inválido:

!|script|RestApiFixture|
|call post;|${BASE}/login|{"user":"admin","pass":"errada"}|
|check|status|401|
```

### Variáveis: `!define` × símbolos

| Mecanismo | Quando usar | Exemplo |
|---|---|---|
| `!define BASE {...}` e `${BASE}` | Valor **fixo**, conhecido ao escrever a página | URL base |
| `|$ID=|json field;|id|` e `$ID` | Valor **calculado em tempo de execução** | id retornado pelo POST |

(Um `!define` não consegue guardar o retorno de uma chamada — a versão antiga deste laboratório, `!define TOKEN {${responseBody}}`, não funcionava.)

### Como rodar a suíte inteira

`Labs.Lab5` → botão **Suite**. Boas práticas: cada teste idempotente; `SetUp` prepara, `TearDown` limpa; `SuiteSetUp`/`SuiteTearDown` rodam uma vez por suíte.

---

# Laboratório 6 — Integração Contínua (Maven, Jenkins, GitHub Actions)

**Objetivo:** rodar a suíte sem navegador e quebrar o build quando algum teste falhar.

### Executando por CLI (sem servidor)

```bash
mvn -q package -DskipTests
java -jar fitnesse.jar -d . -o -c "Labs?suite&format=text"      # resumo
java -jar fitnesse.jar -d . -o -c "Labs?suite&format=junit" > suite-result.xml
echo $?     # 0 = tudo verde; N = N testes com falha
```

- `-c` executa o comando wiki e **encerra** (não precisa subir servidor antes).
- Formatos: `text`, `xml`, `junit` (aceito pelo Jenkins/GitHub). A saída começa com poucas linhas de log; para um XML puro: `sed -n '/<?xml/,$p'`.
- Atalho: `bash run-suite.sh [Pagina] [formato]`.

### Via Maven

```bash
mvn verify        # empacota, copia libs e roda a suíte (exec-maven-plugin); falha o build se houver teste vermelho
```

### Jenkinsfile

```groovy
pipeline {
  agent any
  stages {
    stage('Checkout') { steps { checkout scm } }
    stage('Build fixtures') {
      steps { sh 'mvn -q -f repository/class18/fitnesse-labs/pom.xml package -DskipTests' }
    }
    stage('Run suite') {
      steps {
        dir('repository/class18/fitnesse-labs') {
          sh 'java -jar fitnesse.jar -d . -o -c "Labs?suite&format=junit" | sed -n "/<?xml/,\\$p" > suite-result.xml'
        }
      }
    }
  }
  post {
    always { junit 'repository/class18/fitnesse-labs/suite-result.xml' }
  }
}
```

(O `junit` fica em `post.always` para publicar o relatório mesmo quando a suíte falha.)

### GitHub Actions — `.github/workflows/fitnesse.yml`

```yaml
name: FitNesse CI
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: repository/class18/fitnesse-labs
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
      - name: Build fixtures
        run: mvn -q -B package -DskipTests
      - name: Baixar FitNesse
        run: curl -L -o fitnesse.jar https://github.com/unclebob/fitnesse/releases/latest/download/fitnesse-standalone.jar
      - name: Run suite
        run: java -jar fitnesse.jar -d . -o -c "Labs?suite&format=junit" | sed -n '/<?xml/,$p' > suite-result.xml
      - name: Upload results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: fitnesse-results
          path: repository/class18/fitnesse-labs/suite-result.xml
```

> Se algum teste falhar, o comando `java` retorna código ≠ 0 — porém, num *pipe*, o código que vale é o do último comando (`sed`). Para o build quebrar, use `set -o pipefail` (o `run:` do Actions usa `bash -e -o pipefail` por padrão; no Jenkins, use `sh '''set -o pipefail; ...'''` com `bash`).
>
> O serviço PostgreSQL só é necessário se você trocar o Lab 3 para Postgres.

---

# Extras

### Selenium + FitNesse (UI)

Adicione `selenium-java`, crie uma fixture que abre o `WebDriver` num método (`open page;`) e métodos de ação/consulta (`type;`, `click;`, `title`). Em CI use Chrome *headless*. Regras de negócio ficam no FitNesse; fluxos de tela no Selenium (Aula 17).

### FitLibrary

`fitlibrary` oferece fixtures orientadas a fluxo (DoFixture) para a arquitetura **Fit**; em Slim a Script Table cumpre esse papel — prefira Slim.

### Spring Boot

Exponha serviços REST e consuma-os com a `RestApiFixture`, ou injete beans na fixture (`ApplicationContext` estático). TestContainers pode subir banco e aplicação durante os testes.

---

# Arquivos para copiar (tudo em `repository/class18/fitnesse-labs/`)

- `pom.xml`, `start.sh`, `run-suite.sh`
- `src/main/java/com/example/fixtures/` — `CalculadoraFixture`, `DatabaseFixture`, `UsuariosDb`, `RestApiFixture`
- `FitNesseRoot/Labs/...` — páginas `content.txt` + `properties.xml`
