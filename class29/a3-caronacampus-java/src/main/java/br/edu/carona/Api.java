package br.edu.carona;

import br.edu.carona.Erros.DadoInvalido;
import br.edu.carona.Erros.ErroDeDominio;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** API HTTP (JDK HttpServer). Só traduz HTTP <-> serviço; sem regra de negócio. */
public class Api {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private static final Pattern VIAGEM = Pattern.compile("^/viagens/(\\d+)$");
    private static final Pattern VIAGEM_RESERVAS = Pattern.compile("^/viagens/(\\d+)/reservas$");
    private static final Pattern VIAGEM_AVALIACOES = Pattern.compile("^/viagens/(\\d+)/avaliacoes$");
    private static final Pattern RESERVA = Pattern.compile("^/reservas/(\\d+)$");
    private static final Pattern REPUTACAO = Pattern.compile("^/motoristas/(\\d+)/reputacao$");

    private final ServicoCarona servico;
    private final RelogioAjustavel relogio;
    private final boolean modoTeste;
    private final Map<String, Long> tokens = new ConcurrentHashMap<>();
    private HttpServer servidor;

    public Api(ServicoCarona servico, RelogioAjustavel relogio, boolean modoTeste) {
        this.servico = servico;
        this.relogio = relogio;
        this.modoTeste = modoTeste;
    }

    /** Sobe o servidor; porta 0 escolhe uma porta livre (usado nos testes). Devolve a porta. */
    public int iniciar(int porta) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(porta), 0);
        servidor.createContext("/", this::tratar);
        servidor.setExecutor(Executors.newFixedThreadPool(16));
        servidor.start();
        return servidor.getAddress().getPort();
    }

    public void parar() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    // ------------------------------------------------------------------ roteamento
    private void tratar(HttpExchange ex) throws IOException {
        try {
            String metodo = ex.getRequestMethod();
            String caminho = ex.getRequestURI().getPath();
            Matcher m;
            if (caminho.equals("/saude") && metodo.equals("GET")) {
                responder(ex, 200, Map.of("status", "ok"));
            } else if (caminho.equals("/auth/login") && metodo.equals("POST")) {
                login(ex);
            } else if (caminho.equals("/viagens") && metodo.equals("POST")) {
                criarViagem(ex, autenticado(ex));
            } else if (caminho.equals("/viagens") && metodo.equals("GET")) {
                autenticado(ex);
                responder(ex, 200, servico.listarViagens().stream().map(Api::viagemJson).toList());
            } else if ((m = VIAGEM.matcher(caminho)).matches()) {
                viagem(ex, metodo, Long.parseLong(m.group(1)));
            } else if ((m = VIAGEM_RESERVAS.matcher(caminho)).matches() && metodo.equals("POST")) {
                responder(ex, 201, reservaJson(servico.reservar(Long.parseLong(m.group(1)), autenticado(ex))));
            } else if ((m = RESERVA.matcher(caminho)).matches() && metodo.equals("DELETE")) {
                responder(ex, 200, reservaJson(servico.cancelarReserva(Long.parseLong(m.group(1)), autenticado(ex))));
            } else if ((m = VIAGEM_AVALIACOES.matcher(caminho)).matches() && metodo.equals("POST")) {
                avaliar(ex, Long.parseLong(m.group(1)));
            } else if ((m = REPUTACAO.matcher(caminho)).matches() && metodo.equals("GET")) {
                autenticado(ex);
                responder(ex, 200, servico.reputacao(Long.parseLong(m.group(1))));
            } else if (caminho.equals("/relatorios/ocupacao") && metodo.equals("GET")) {
                autenticado(ex);
                responder(ex, 200, servico.relatorioOcupacao());
            } else if (modoTeste && caminho.equals("/teste/avancar-relogio") && metodo.equals("POST")) {
                JsonNode corpo = corpo(ex);
                relogio.avancar(corpo.path("minutos").asLong(0));
                responder(ex, 200, Map.of("agora", relogio.get().format(RepositorioSqlite.FORMATO)));
            } else {
                responder(ex, 404, erro("nao_encontrado", "rota não encontrada"));
            }
        } catch (NaoAutenticado e) {
            responder(ex, 401, erro("nao_autenticado", "token ausente ou inválido"));
        } catch (ErroDeDominio e) {
            responder(ex, e.status(), erro(e.codigo(), e.getMessage()));
        } catch (RuntimeException e) {
            responder(ex, 500, erro("erro_interno", "erro inesperado"));
        } finally {
            ex.close();
        }
    }

    private void viagem(HttpExchange ex, String metodo, long id) throws IOException {
        switch (metodo) {
            case "GET" -> {
                autenticado(ex);
                responder(ex, 200, viagemJson(servico.obterViagem(id)));
            }
            case "PUT" -> {
                long uid = autenticado(ex);
                JsonNode d = corpo(ex);
                Viagem v = servico.atualizarViagem(id, uid,
                    d.has("origem") ? texto(d, "origem") : null,
                    d.has("destino") ? texto(d, "destino") : null,
                    d.has("vagas") ? inteiro(d, "vagas") : null,
                    d.has("custo_total") ? dinheiro(d.get("custo_total")) : null,
                    d.has("saida") ? data(d.get("saida")) : null);
                responder(ex, 200, viagemJson(v));
            }
            case "DELETE" -> {
                servico.excluirViagem(id, autenticado(ex));
                responder(ex, 204, null);
            }
            default -> responder(ex, 405, erro("metodo_nao_permitido", "método não permitido"));
        }
    }

    private void login(HttpExchange ex) throws IOException {
        JsonNode d = corpo(ex);
        var achado = servico.repositorio().usuarioPorMatricula(d.path("matricula").asText(""));
        if (achado.isEmpty() || !Senhas.confere(d.path("senha").asText(""), achado.get().senhaHash())) {
            responder(ex, 401, erro("nao_autenticado", "matrícula ou senha inválida"));
            return;
        }
        byte[] bytes = new byte[16];
        ALEATORIO.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);
        tokens.put(token, achado.get().usuario().id());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("token", token);
        r.put("usuario_id", achado.get().usuario().id());
        r.put("nome", achado.get().usuario().nome());
        responder(ex, 200, r);
    }

    private void criarViagem(HttpExchange ex, long uid) throws IOException {
        JsonNode d = corpo(ex);
        Viagem v = servico.criarViagem(uid, textoOuNulo(d, "origem"), textoOuNulo(d, "destino"),
            data(d.get("saida")), inteiro(d, "vagas"), dinheiro(d.get("custo_total")));
        responder(ex, 201, viagemJson(v));
    }

    private void avaliar(HttpExchange ex, long viagemId) throws IOException {
        long uid = autenticado(ex);
        JsonNode d = corpo(ex);
        servico.avaliar(viagemId, uid, inteiro(d, "nota"));
        responder(ex, 201, null);
    }

    // ------------------------------------------------------------------ entrada
    private static final class NaoAutenticado extends RuntimeException {}

    private long autenticado(HttpExchange ex) {
        String cab = ex.getRequestHeaders().getFirst("Authorization");
        if (cab == null || !cab.startsWith("Bearer ") || !tokens.containsKey(cab.substring(7))) {
            throw new NaoAutenticado();
        }
        return tokens.get(cab.substring(7));
    }

    private static JsonNode corpo(HttpExchange ex) throws IOException {
        try {
            JsonNode no = JSON.readTree(ex.getRequestBody());
            if (no == null || !no.isObject()) {
                throw new DadoInvalido("corpo JSON obrigatório");
            }
            return no;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new DadoInvalido("corpo JSON obrigatório");
        }
    }

    private static String texto(JsonNode d, String campo) {
        JsonNode n = d.get(campo);
        if (n == null || !n.isTextual()) {
            throw new DadoInvalido(campo + " deve ser texto");
        }
        return n.asText();
    }

    private static String textoOuNulo(JsonNode d, String campo) {
        JsonNode n = d.get(campo);
        return n == null || n.isNull() ? null : (n.isTextual() ? n.asText() : "");
    }

    /** Inteiro de verdade: recusa 2.5, "3", true e ausente (o Python também recusa). */
    private static int inteiro(JsonNode d, String campo) {
        JsonNode n = d.get(campo);
        if (n == null || !n.isIntegralNumber() || !n.canConvertToInt()) {
            throw new DadoInvalido(campo + " deve ser um inteiro");
        }
        return n.asInt();
    }

    private static BigDecimal dinheiro(JsonNode n) {
        if (n == null || n.isNull() || n.isBoolean() || !(n.isNumber() || n.isTextual())) {
            throw new DadoInvalido("valor monetário inválido");
        }
        try {
            return new BigDecimal(n.asText());
        } catch (NumberFormatException e) {
            throw new DadoInvalido("valor monetário inválido");
        }
    }

    private static LocalDateTime data(JsonNode n) {
        try {
            return LocalDateTime.parse(n == null ? "None" : n.asText());
        } catch (DateTimeParseException e) {
            throw new DadoInvalido("data inválida; use AAAA-MM-DDTHH:MM:SS");
        }
    }

    // ------------------------------------------------------------------ saída
    private static Map<String, Object> viagemJson(Viagem v) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", v.id());
        m.put("motorista_id", v.motoristaId());
        m.put("origem", v.origem());
        m.put("destino", v.destino());
        m.put("saida", v.saida().format(RepositorioSqlite.FORMATO));
        m.put("vagas", v.vagas());
        m.put("custo_total", v.custoTotal().toPlainString());
        m.put("valor_por_vaga", v.valorPorVaga().toPlainString());
        return m;
    }

    private static Map<String, Object> reservaJson(Reserva r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.id());
        m.put("viagem_id", r.viagemId());
        m.put("passageiro_id", r.passageiroId());
        m.put("valor", r.valor().toPlainString());
        m.put("cancelada", r.cancelada());
        m.put("taxa", r.taxa().toPlainString());
        return m;
    }

    private static Map<String, Object> erro(String codigo, String mensagem) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("erro", codigo);
        m.put("mensagem", mensagem);
        return m;
    }

    private static void responder(HttpExchange ex, int status, Object corpo) throws IOException {
        if (corpo == null) {
            ex.sendResponseHeaders(status, -1);
            return;
        }
        byte[] bytes = JSON.writeValueAsString(corpo).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
