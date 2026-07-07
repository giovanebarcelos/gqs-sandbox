/**
 * GQS1901 - Testes de API REST com REST Assured.
 * Exercita o CRUD de usuários da API pública JSONPlaceholder,
 * validando status code e estrutura do JSON de resposta.
 *
 * Dependência Maven necessária (pom.xml):
 *   <dependency>
 *     <groupId>io.rest-assured</groupId>
 *     <artifactId>rest-assured</artifactId>
 *     <version>5.4.0</version>
 *     <scope>test</scope>
 *   </dependency>
 *
 * Compilação e execução (com as dependências de rest-assured no classpath):
 *   javac -cp .:rest-assured-5.4.0.jar:... GQS1901-RestAssured.java
 *   java -cp .:rest-assured-5.4.0.jar:... GQS1901_RestAssured
 *
 * Em um projeto Maven/Gradle real, prefira reescrever os métodos como
 * testes JUnit (@Test) em vez do main() usado aqui apenas para
 * permitir a execução standalone deste arquivo.
 */
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.response.Response;

class GQS1901_RestAssured {

    static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    static void testarGetListaUsuarios() {
        given()
            .baseUri(BASE_URL)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].email", notNullValue());

        System.out.println("[OK] GET /users -> 200 (lista não vazia)");
    }

    static void testarGetUsuarioPorId() {
        given()
            .baseUri(BASE_URL)
        .when()
            .get("/users/{id}", 1)
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("name", notNullValue());

        System.out.println("[OK] GET /users/1 -> 200");
    }

    static void testarPostCriarUsuario() {
        String corpo = "{ \"name\": \"João Silva\", \"email\": \"joao@email.com\" }";

        Response resposta =
            given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(corpo)
            .when()
                .post("/users")
            .then()
                .statusCode(201)
                .body("name", equalTo("João Silva"))
                .extract().response();

        System.out.printf("[OK] POST /users -> 201 (id gerado: %s)%n", resposta.jsonPath().get("id").toString());
    }

    static void testarPutAtualizarUsuario() {
        String corpo = "{ \"id\": 1, \"name\": \"João Silva Atualizado\" }";

        given()
            .baseUri(BASE_URL)
            .contentType("application/json")
            .body(corpo)
        .when()
            .put("/users/1")
        .then()
            .statusCode(200)
            .body("name", equalTo("João Silva Atualizado"));

        System.out.println("[OK] PUT /users/1 -> 200");
    }

    static void testarDeleteUsuario() {
        given()
            .baseUri(BASE_URL)
        .when()
            .delete("/users/1")
        .then()
            .statusCode(200);

        System.out.println("[OK] DELETE /users/1 -> 200");
    }

    static void testarGetComBearerToken() {
        // Demonstra o envio do cabeçalho Authorization: Bearer <token>,
        // equivalente à aba Authorization > Bearer Token do Postman.
        given()
            .baseUri(BASE_URL)
            .header("Authorization", "Bearer token-de-exemplo-123")
        .when()
            .get("/users/1")
        .then()
            .statusCode(200);

        System.out.println("[OK] GET /users/1 com Bearer token -> 200");
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS1901 - TESTES DE API REST COM REST ASSURED");
        System.out.println("=".repeat(60));

        Runnable[] testes = {
            GQS1901_RestAssured::testarGetListaUsuarios,
            GQS1901_RestAssured::testarGetUsuarioPorId,
            GQS1901_RestAssured::testarPostCriarUsuario,
            GQS1901_RestAssured::testarPutAtualizarUsuario,
            GQS1901_RestAssured::testarDeleteUsuario,
            GQS1901_RestAssured::testarGetComBearerToken,
        };

        int falhas = 0;
        for (Runnable teste : testes) {
            try {
                teste.run();
            } catch (AssertionError | RuntimeException erro) {
                falhas++;
                System.out.println("[FALHA] " + erro.getMessage());
            }
        }

        System.out.println("=".repeat(60));
        System.out.printf("  Resultado: %d/%d testes OK%n", testes.length - falhas, testes.length);
        System.out.println("=".repeat(60));
    }
}
