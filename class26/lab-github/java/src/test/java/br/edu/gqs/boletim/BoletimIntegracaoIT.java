package br.edu.gqs.boletim;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * TESTE DE INTEGRACAO: sobe a aplicacao INTEIRA (Spring + Tomcat numa porta
 * aleatoria, controller + calculadora reais, sem nenhum mock) e conversa com
 * ela por HTTP de verdade. O sufixo "IT" faz o maven-failsafe-plugin executar
 * esta classe na fase integration-test, depois dos testes unitarios.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"app.token-admin=segredo-it", "app.versao=it", "app.instituicao=Teste de integracao"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BoletimIntegracaoIT {

    @Autowired
    private TestRestTemplate http;

    private ResponseEntity<Map> estatisticas(String token) {
        HttpHeaders cabecalhos = new HttpHeaders();
        if (token != null) {
            cabecalhos.set("X-Admin-Token", token);
        }
        return http.exchange("/api/admin/estatisticas", HttpMethod.GET, new HttpEntity<>(cabecalhos), Map.class);
    }

    @Test
    @Order(1)
    void healthMostraAConfiguracaoReal() {
        ResponseEntity<Map> resposta = http.getForEntity("/api/health", Map.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).containsEntry("versao", "it").containsEntry("instituicao", "Teste de integracao");
    }

    @Test
    @Order(2)
    void boletimPercorreControllerECalculadoraDeVerdade() {
        Map<String, Object> pedido = Map.of("aluno", "Ana", "notas", List.of(7.0, 9.0, 8.0), "frequencia", 90);

        ResponseEntity<Map> resposta = http.postForEntity("/api/boletim", pedido, Map.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).containsEntry("situacao", "APROVADO").containsEntry("aluno", "Ana");
        assertThat(((Number) resposta.getBody().get("media")).doubleValue()).isEqualTo(8.0);
    }

    @Test
    @Order(3)
    void entradaInvalidaVoltaComoHttp400ComAMensagemDaRegra() {
        Map<String, Object> pedido = Map.of("aluno", "Davi", "notas", List.of(11.0), "frequencia", 80);

        ResponseEntity<Map> resposta = http.postForEntity("/api/boletim", pedido, Map.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).containsEntry("erro", "Nota fora do intervalo 0 a 10: 11.0");
    }

    @Test
    @Order(4)
    void estatisticasContamSoOsBoletinsCalculadosComSucesso() {
        // Estado compartilhado entre requisicoes: o teste 2 calculou 1 boletim com sucesso;
        // o teste 3 falhou na validacao e NAO conta.
        ResponseEntity<Map> resposta = estatisticas("segredo-it");

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).containsEntry("boletinsCalculados", 1).containsEntry("versao", "it");
    }

    @Test
    @Order(5)
    void areaAdministrativaRecusaTokenErradoOuAusente() {
        assertThat(estatisticas(null).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(estatisticas("token-errado").getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
