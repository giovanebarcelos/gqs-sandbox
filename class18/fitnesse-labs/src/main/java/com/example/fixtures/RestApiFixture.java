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
