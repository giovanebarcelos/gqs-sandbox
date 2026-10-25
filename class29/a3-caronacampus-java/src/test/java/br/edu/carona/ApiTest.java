package br.edu.carona;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Integração: a API HTTP real (JDK HttpServer, porta livre) + serviço + SQLite. */
class ApiTest {
    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newHttpClient();

    record Resp(int status, JsonNode corpo) {}

    Fixtures.RelogioFixo relogio;
    RelogioAjustavel ajustavel;
    Api api;
    String base;
    String ana;
    String bruno;

    @BeforeEach
    void subir(@TempDir Path pasta) throws Exception {
        relogio = new Fixtures.RelogioFixo();
        ajustavel = new RelogioAjustavel(relogio);
        api = App.montar(pasta.resolve("api.db").toString(), true, ajustavel);
        base = "http://localhost:" + api.iniciar(0);
        ana = login("2026001", "ana123");
        bruno = login("2026002", "bruno123");
    }

    @AfterEach
    void parar() { api.parar(); }

    Resp http(String metodo, String caminho, String token, Object corpo) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(base + caminho));
        if (token != null) {
            b.header("Authorization", "Bearer " + token);
        }
        b.header("Content-Type", "application/json");
        HttpRequest.BodyPublisher p = corpo == null ? HttpRequest.BodyPublishers.noBody()
            : HttpRequest.BodyPublishers.ofString(corpo instanceof String s ? s : JSON.writeValueAsString(corpo));
        HttpResponse<String> r = HTTP.send(b.method(metodo, p).build(), HttpResponse.BodyHandlers.ofString());
        return new Resp(r.statusCode(), r.body().isEmpty() ? null : JSON.readTree(r.body()));
    }

    String login(String matricula, String senha) throws Exception {
        Resp r = http("POST", "/auth/login", null, Map.of("matricula", matricula, "senha", senha));
        assertEquals(200, r.status());
        return r.corpo().get("token").asText();
    }

    Map<String, Object> corpoViagem(Object... sobrescritas) {
        Map<String, Object> m = new java.util.LinkedHashMap<>(Map.of("origem", "Campus", "destino", "Centro", "vagas", 3,
            "custo_total", "12.00", "saida", relogio.get().plusHours(3).format(RepositorioSqlite.FORMATO)));
        for (int i = 0; i < sobrescritas.length; i += 2) {
            m.put((String) sobrescritas[i], sobrescritas[i + 1]);
        }
        return m;
    }

    long criarViagem(String token, Object... sobrescritas) throws Exception {
        Resp r = http("POST", "/viagens", token, corpoViagem(sobrescritas));
        assertEquals(201, r.status());
        return r.corpo().get("id").asLong();
    }

    // --- CT-15 · autenticação
    @Test
    void ct15_loginValidoDevolveTokenEUsuario() throws Exception {
        Resp r = http("POST", "/auth/login", null, Map.of("matricula", "2026001", "senha", "ana123"));
        assertEquals(200, r.status());
        assertEquals(1, r.corpo().get("usuario_id").asInt());
        assertEquals(32, r.corpo().get("token").asText().length());
    }

    @ParameterizedTest
    @CsvSource(value = {"{\"matricula\":\"2026001\",\"senha\":\"errada\"}", "{\"matricula\":\"999\",\"senha\":\"a\"}",
        "{\"matricula\":\"2026001\"}", "{}"}, delimiter = '|')
    void ct15_loginInvalidoDevolve401(String corpo) throws Exception {
        assertEquals(401, http("POST", "/auth/login", null, corpo).status());
    }

    @ParameterizedTest
    @CsvSource({"GET,/viagens", "POST,/viagens", "GET,/viagens/1", "PUT,/viagens/1", "DELETE,/viagens/1",
        "POST,/viagens/1/reservas", "DELETE,/reservas/1", "POST,/viagens/1/avaliacoes",
        "GET,/motoristas/1/reputacao", "GET,/relatorios/ocupacao"})
    void ct15_todoEndpointProtegidoExigeToken(String metodo, String caminho) throws Exception {
        Resp r = http(metodo, caminho, null, null);
        assertEquals(401, r.status());
        assertEquals("nao_autenticado", r.corpo().get("erro").asText());
    }

    @Test
    void ct15_tokenInventadoERecusadoESaudeNaoExigeToken() throws Exception {
        assertEquals(401, http("GET", "/viagens", "xxx", null).status());
        HttpResponse<String> basic = HTTP.send(HttpRequest.newBuilder(URI.create(base + "/viagens"))
            .header("Authorization", "Basic abc").GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(401, basic.statusCode());                                         // esquema errado: Basic no lugar de Bearer
        Resp s = http("GET", "/saude", null, null);
        assertEquals(200, s.status());
        assertEquals("ok", s.corpo().get("status").asText());
    }

    // --- CT-01, CT-14 · CRUD de viagens
    @Test
    void ct01_criarEConsultarViagem() throws Exception {
        Resp r = http("POST", "/viagens", ana, corpoViagem());
        assertEquals(201, r.status());
        assertEquals("3.00", r.corpo().get("valor_por_vaga").asText());
        assertEquals(1, r.corpo().get("motorista_id").asInt());
        long id = r.corpo().get("id").asLong();
        assertEquals(r.corpo(), http("GET", "/viagens/" + id, ana, null).corpo());
        assertEquals(id, http("GET", "/viagens", ana, null).corpo().get(0).get("id").asLong());
    }

    @ParameterizedTest
    @CsvSource(value = {"vagas|0", "vagas|7", "vagas|2.5", "vagas|\"3\"", "vagas|true", "custo_total|-1", "custo_total|\"abc\"",
        "origem|\"\"", "saida|\"ontem\"", "destino|\"campus\""}, delimiter = '|')
    void ct01_dadosInvalidosDevolvem400ComErroPadronizado(String campo, String valorJson) throws Exception {
        Map<String, Object> corpo = corpoViagem();
        corpo.put(campo, JSON.readTree(valorJson));
        Resp r = http("POST", "/viagens", ana, corpo);
        assertEquals(400, r.status());
        assertEquals("dado_invalido", r.corpo().get("erro").asText());
    }

    @Test
    void ct01_corpoAusenteOuNaoJsonDevolve400() throws Exception {
        assertEquals(400, http("POST", "/viagens", ana, null).status());
        assertEquals(400, http("POST", "/viagens", ana, "texto").status());
    }

    @Test
    void ct14_alterarECancelarViagemPelaApi() throws Exception {
        long id = criarViagem(ana);
        Resp r = http("PUT", "/viagens/" + id, ana, Map.of("vagas", 5, "custo_total", "30.00", "origem", "Biblioteca",
            "destino", "Rodoviária", "saida", relogio.get().plusHours(5).format(RepositorioSqlite.FORMATO)));
        assertEquals(200, r.status());
        assertEquals(5, r.corpo().get("vagas").asInt());
        assertEquals("5.00", r.corpo().get("valor_por_vaga").asText());
        assertEquals(204, http("DELETE", "/viagens/" + id, ana, null).status());
        assertEquals(404, http("GET", "/viagens/" + id, ana, null).status());
    }

    @Test
    void ct14_alteracaoComOrigemVaziaERecusada() throws Exception {
        long id = criarViagem(ana);
        assertEquals(400, http("PUT", "/viagens/" + id, ana, Map.of("origem", "  ")).status());
        assertEquals(400, http("PUT", "/viagens/" + id, ana, Map.of("destino", "")).status());
    }

    @Test
    void ct14_soOMotoristaAlteraEViagemComReservaEImutavel() throws Exception {
        long id = criarViagem(ana);
        assertEquals(403, http("PUT", "/viagens/" + id, bruno, Map.of("vagas", 2)).status());
        assertEquals(403, http("DELETE", "/viagens/" + id, bruno, null).status());
        http("POST", "/viagens/" + id + "/reservas", bruno, null);
        assertEquals(409, http("PUT", "/viagens/" + id, ana, Map.of("vagas", 2)).status());
        assertEquals(409, http("DELETE", "/viagens/" + id, ana, null).status());
    }

    @Test
    void ct14_viagemInexistenteDevolve404() throws Exception {
        assertEquals(404, http("GET", "/viagens/999", ana, null).status());
        assertEquals(404, http("PUT", "/viagens/999", ana, Map.of()).status());
        assertEquals(404, http("GET", "/rota/que/nao/existe", ana, null).status());
    }

    // --- CT-05..CT-10 · reservas e cancelamento
    @Test
    void ct05_reservarDevolve201ComValor() throws Exception {
        long id = criarViagem(ana);
        Resp r = http("POST", "/viagens/" + id + "/reservas", bruno, null);
        assertEquals(201, r.status());
        assertEquals("3.00", r.corpo().get("valor").asText());
        assertEquals(false, r.corpo().get("cancelada").asBoolean());
    }

    @Test
    void ct07_reservasInvalidasDevolvem409() throws Exception {
        long id = criarViagem(ana);
        assertEquals(409, http("POST", "/viagens/" + id + "/reservas", ana, null).status());    // própria viagem
        assertEquals(201, http("POST", "/viagens/" + id + "/reservas", bruno, null).status());
        Resp dup = http("POST", "/viagens/" + id + "/reservas", bruno, null);                    // duplicada
        assertEquals(409, dup.status());
        assertEquals("conflito_de_regra", dup.corpo().get("erro").asText());
        assertEquals(404, http("POST", "/viagens/999/reservas", bruno, null).status());
    }

    @Test
    void ct06_viagemLotadaDevolve409() throws Exception {
        long id = criarViagem(ana, "vagas", 1);
        assertEquals(201, http("POST", "/viagens/" + id + "/reservas", bruno, null).status());
        Resp r = http("POST", "/viagens/" + id + "/reservas", login("2026003", "carla123"), null);
        assertEquals(409, r.status());
        assertTrue(r.corpo().get("mensagem").asText().contains("lotada"));
    }

    @Test
    void ct08_ct09_cancelarReservaComESemTaxaPelaApi() throws Exception {
        long id = criarViagem(ana);
        long rid = http("POST", "/viagens/" + id + "/reservas", bruno, null).corpo().get("id").asLong();
        Resp r = http("DELETE", "/reservas/" + rid, bruno, null);
        assertEquals(200, r.status());
        assertEquals("0.00", r.corpo().get("taxa").asText());
        assertEquals(true, r.corpo().get("cancelada").asBoolean());
        long rid2 = http("POST", "/viagens/" + id + "/reservas", bruno, null).corpo().get("id").asLong();
        assertEquals(200, http("POST", "/teste/avancar-relogio", null, Map.of("minutos", 121)).status());   // faltam 59 min
        assertEquals("1.50", http("DELETE", "/reservas/" + rid2, bruno, null).corpo().get("taxa").asText());
    }

    @Test
    void ct10_cancelarReservaDeOutroOuInexistente() throws Exception {
        long id = criarViagem(ana);
        long rid = http("POST", "/viagens/" + id + "/reservas", bruno, null).corpo().get("id").asLong();
        assertEquals(403, http("DELETE", "/reservas/" + rid, ana, null).status());
        assertEquals(404, http("DELETE", "/reservas/999", bruno, null).status());
        http("DELETE", "/reservas/" + rid, bruno, null);
        assertEquals(409, http("DELETE", "/reservas/" + rid, bruno, null).status());
    }

    // --- CT-11, CT-12, CT-13, CT-16 · avaliação, reputação, relatório
    @Test
    void ct11_avaliarExigeTerViajado() throws Exception {
        long id = criarViagem(ana);
        http("POST", "/viagens/" + id + "/reservas", bruno, null);
        assertEquals(409, http("POST", "/viagens/" + id + "/avaliacoes", bruno, Map.of("nota", 5)).status());
        relogio.avancar(Duration.ofMinutes(181));
        assertEquals(400, http("POST", "/viagens/" + id + "/avaliacoes", bruno, Map.of("nota", 6)).status());
        assertEquals(400, http("POST", "/viagens/" + id + "/avaliacoes", bruno, Map.of()).status());
        assertEquals(201, http("POST", "/viagens/" + id + "/avaliacoes", bruno, Map.of("nota", 5)).status());
        assertEquals(409, http("POST", "/viagens/" + id + "/avaliacoes", bruno, Map.of("nota", 4)).status());
        assertEquals(403, http("POST", "/viagens/" + id + "/avaliacoes", ana, Map.of("nota", 4)).status());
    }

    @Test
    void ct12_reputacaoEct13_suspensaoPontaAPonta() throws Exception {
        Resp zero = http("GET", "/motoristas/1/reputacao", ana, null);
        assertEquals(0, zero.corpo().get("avaliacoes").asInt());
        assertTrue(zero.corpo().get("media").isNull());
        assertEquals(false, zero.corpo().get("suspenso").asBoolean());
        for (int i = 0; i < 5; i++) {
            long id = criarViagem(ana, "saida", relogio.get().plusHours(1).format(RepositorioSqlite.FORMATO));
            http("POST", "/viagens/" + id + "/reservas", bruno, null);
            relogio.avancar(Duration.ofMinutes(61));
            http("POST", "/viagens/" + id + "/avaliacoes", bruno, Map.of("nota", 1));
        }
        Resp rep = http("GET", "/motoristas/1/reputacao", ana, null);
        assertEquals(true, rep.corpo().get("suspenso").asBoolean());
        assertEquals("1.00", rep.corpo().get("media").asText());
        Resp nova = http("POST", "/viagens", ana, corpoViagem());
        assertEquals(403, nova.status());
        assertEquals("motorista_suspenso", nova.corpo().get("erro").asText());
        assertEquals(404, http("GET", "/motoristas/999/reputacao", ana, null).status());
    }

    @Test
    void ct16_relatorioDeOcupacao() throws Exception {
        long id = criarViagem(ana, "vagas", 4);
        http("POST", "/viagens/" + id + "/reservas", bruno, null);
        JsonNode rel = http("GET", "/relatorios/ocupacao", ana, null).corpo();
        assertEquals(1, rel.get("viagens").asInt());
        assertEquals(4, rel.get("vagas").asInt());
        assertEquals(1, rel.get("ocupadas").asInt());
        assertEquals(25.0, rel.get("taxa_ocupacao").asDouble());
    }

    // --- montagem e modo de teste
    @Test
    void ct15_montarDuasVezesNoMesmoBancoNaoDuplicaUsuarios(@TempDir Path pasta) throws Exception {
        String db = pasta.resolve("dois.db").toString();
        App.montar(db, false, new RelogioAjustavel());
        Api outra = App.montar(db, false, new RelogioAjustavel());                   // reinício do servidor
        String outraBase = "http://localhost:" + outra.iniciar(0);
        HttpResponse<String> r = HTTP.send(HttpRequest.newBuilder(URI.create(outraBase + "/auth/login"))
            .POST(HttpRequest.BodyPublishers.ofString("{\"matricula\":\"2026001\",\"senha\":\"ana123\"}")).build(),
            HttpResponse.BodyHandlers.ofString());
        outra.parar();
        assertEquals(200, r.statusCode());
    }

    @Test
    void modoDeTesteDesligadoNaoExpoeORelogio(@TempDir Path pasta) throws Exception {
        Api normal = App.montar(pasta.resolve("n.db").toString(), false, new RelogioAjustavel());
        String b = "http://localhost:" + normal.iniciar(0);
        HttpResponse<String> r = HTTP.send(HttpRequest.newBuilder(URI.create(b + "/teste/avancar-relogio"))
            .POST(HttpRequest.BodyPublishers.ofString("{\"minutos\":5}")).build(), HttpResponse.BodyHandlers.ofString());
        normal.parar();
        assertEquals(404, r.statusCode());
    }
}
