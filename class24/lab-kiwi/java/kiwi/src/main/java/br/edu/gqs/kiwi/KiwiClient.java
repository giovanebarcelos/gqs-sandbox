package br.edu.gqs.kiwi;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Cliente minimo da API JSON-RPC do Kiwi TCMS (endpoint /json-rpc/).
 *
 * <p>Toda chamada e' um POST com {"jsonrpc": "2.0", "method": "TestCase.filter",
 * "params": [...], "id": n}. O login (Auth.login) devolve um cookie de sessao,
 * guardado pelo CookieManager e reenviado nas chamadas seguintes.
 *
 * <p>Credenciais NUNCA vao no codigo. Duas formas aceitas, nesta ordem:
 * <ol>
 *   <li>variaveis de ambiente KIWI_URL, KIWI_USERNAME e KIWI_PASSWORD;</li>
 *   <li>arquivo ~/.tcms.conf (o mesmo usado pela biblioteca Python tcms-api).</li>
 * </ol>
 * A URL pode terminar em /xml-rpc/ ou /json-rpc/ - o cliente sempre usa JSON-RPC.
 */
public class KiwiClient {

    static {
        // APENAS EM LABORATORIO: o Traefik responde em https://kiwi.localhost
        // com certificado autoassinado. Em producao remova esta linha e o
        // SSLContext "confia em tudo" abaixo, e use um certificado valido.
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
    }

    private static final ObjectMapper JSON = new ObjectMapper();

    private final URI endpoint;
    private final HttpClient http;
    private final AtomicInteger proximoId = new AtomicInteger(1);

    public KiwiClient(String url, String usuario, String senha) {
        this.endpoint = URI.create(url.replace("xml-rpc", "json-rpc"));
        this.http = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .sslContext(sslQueConfiaEmTudo())
                .build();
        call("Auth.login", usuario, senha);
    }

    /** Conecta usando KIWI_URL/KIWI_USERNAME/KIWI_PASSWORD ou ~/.tcms.conf. */
    public static KiwiClient conectar() {
        String url = System.getenv("KIWI_URL");
        String usuario = System.getenv("KIWI_USERNAME");
        String senha = System.getenv("KIWI_PASSWORD");
        if (url == null || usuario == null || senha == null) {
            Map<String, String> conf = lerTcmsConf();
            url = conf.get("url");
            usuario = conf.get("username");
            senha = conf.get("password");
        }
        if (url == null || usuario == null || senha == null) {
            throw new IllegalStateException(
                    "Configure ~/.tcms.conf ou exporte KIWI_URL/KIWI_USERNAME/KIWI_PASSWORD.");
        }
        return new KiwiClient(url, usuario, senha);
    }

    /** Executa um metodo da API e devolve o campo "result" da resposta. */
    public JsonNode call(String metodo, Object... parametros) {
        Map<String, Object> corpo = Map.of(
                "jsonrpc", "2.0",
                "method", metodo,
                "params", List.of(parametros),
                "id", proximoId.getAndIncrement());
        try {
            HttpRequest requisicao = HttpRequest.newBuilder(endpoint)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(corpo)))
                    .build();
            HttpResponse<String> resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString());
            JsonNode json = JSON.readTree(resposta.body());
            if (json.hasNonNull("error")) {
                throw new IllegalStateException(metodo + ": " + json.get("error").path("message").asText());
            }
            return json.get("result");
        } catch (IOException erro) {
            throw new IllegalStateException("Falha ao chamar " + metodo + " em " + endpoint + ": " + erro, erro);
        } catch (InterruptedException erro) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(erro);
        }
    }

    /** Atalho para Entidade.filter(filtro). */
    public JsonNode filter(String entidade, Map<String, ?> filtro) {
        return call(entidade + ".filter", filtro);
    }

    /** Estrategia "get or create": devolve o primeiro que casa com o filtro ou cria. */
    public JsonNode obterOuCriar(String entidade, Map<String, ?> filtro, Map<String, ?> dados) {
        JsonNode existentes = filter(entidade, filtro);
        if (existentes.size() > 0) {
            return existentes.get(0);
        }
        return call(entidade + ".create", dados);
    }

    /** Id de um TestExecutionStatus pelo nome (PASSED, FAILED, ...). */
    public int idDoStatus(String nome) {
        return filter("TestExecutionStatus", Map.of("name", nome)).get(0).get("id").asInt();
    }

    private static Map<String, String> lerTcmsConf() {
        Map<String, String> valores = new HashMap<>();
        Path arquivo = Path.of(System.getProperty("user.home"), ".tcms.conf");
        if (!Files.exists(arquivo)) {
            return valores;
        }
        try {
            for (String linha : Files.readAllLines(arquivo)) {
                int igual = linha.indexOf('=');
                if (igual > 0 && !linha.strip().startsWith("#")) {
                    valores.put(linha.substring(0, igual).strip(), linha.substring(igual + 1).strip());
                }
            }
        } catch (IOException erro) {
            throw new IllegalStateException("Nao consegui ler " + arquivo, erro);
        }
        return valores;
    }

    private static SSLContext sslQueConfiaEmTudo() {
        TrustManager[] confiaEmTudo = {new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] cadeia, String tipo) {
                // laboratorio: aceita qualquer certificado
            }

            @Override
            public void checkServerTrusted(X509Certificate[] cadeia, String tipo) {
                // laboratorio: aceita qualquer certificado
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        try {
            SSLContext contexto = SSLContext.getInstance("TLS");
            contexto.init(null, confiaEmTudo, new SecureRandom());
            return contexto;
        } catch (Exception erro) {
            throw new IllegalStateException(erro);
        }
    }
}
