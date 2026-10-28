package br.edu.gqs.kiwi;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Envia um ou mais junit.xml ao Kiwi TCMS.
 *
 * <p>E' a "ponte" que faz um caso de teste escrito no Kiwi ser EXECUTADO por
 * codigo: o JUnit (via Surefire) e o Bruno geram junit.xml; esta classe le
 * os arquivos, acha o caso de teste certo no Kiwi e grava PASSED/FAILED na
 * execucao.
 *
 * <p>COMO O CASO E' ENCONTRADO - o nome do teste comeca com o id do caso:
 * <pre>
 *   JUnit : ct03_freteGratisNoLimiteExatoDe200
 *   Bruno : test("CT14 - Carrinho de exatamente 200,00 tem frete gratis")
 * </pre>
 * e o publicador procura no produto o TestCase cujo Summary comeca com
 * "CT03 ". Varios testes com o mesmo id (ex.: @ParameterizedTest) viram UMA
 * execucao no Kiwi, que so' fica PASSED se todos passarem. Teste sem id no
 * nome vira um TestCase novo, criado com o proprio nome.
 *
 * <p>VARIAVEIS DE AMBIENTE (mesmos nomes do plugin oficial do Kiwi):
 * <pre>
 *   TCMS_PLAN_ID          id do TestPlan (saida do "preparar")
 *   TCMS_PRODUCT_VERSION  default "1.0"
 *   TCMS_BUILD            default "1.0.0"  -> use 1.0.1 depois da correcao
 *   TCMS_PREFIX           prefixo do nome da rodada, ex. "[junit]" ou "[bruno]"
 *   TCMS_RUN_ID           grava numa rodada JA existente em vez de criar outra
 * </pre>
 *
 * <pre>
 *   java -jar target/kiwi-tools.jar publicar ../app/target/surefire-reports
 *   java -jar target/kiwi-tools.jar publicar ../../resultados-api.xml
 * </pre>
 * Aceita arquivos .xml ou pastas (le os TEST-*.xml de dentro).
 */
public class PublicarResultados {

    /** "CT03" no inicio ou depois de "_", "-", espaco... */
    private static final Pattern ID_DO_CASO =
            Pattern.compile("(?<![A-Za-z0-9])(CT\\d+)", Pattern.CASE_INSENSITIVE);

    /** Quanto maior, mais "grave": a execucao agregada fica com o pior status. */
    private static final List<String> GRAVIDADE = List.of("WAIVED", "PASSED", "FAILED", "ERROR");

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Resultado agregado de todos os testes de um mesmo caso. */
    static final class Resultado {
        String status = "WAIVED";
        final List<String> linhas = new ArrayList<>();

        void registrar(String novoStatus, String linha) {
            if (GRAVIDADE.indexOf(novoStatus) > GRAVIDADE.indexOf(status)) {
                status = novoStatus;
            }
            linhas.add(linha);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("uso: publicar resultados.xml|pasta-surefire [outro ...]");
            System.exit(1);
        }
        String planoEnv = System.getenv("TCMS_PLAN_ID");
        String rodadaEnv = System.getenv("TCMS_RUN_ID");
        if (planoEnv == null && rodadaEnv == null) {
            System.out.println("ERRO: exporte TCMS_PLAN_ID com o id do plano (saida do \"preparar\").");
            System.exit(1);
        }

        Map<String, Resultado> resultados = lerResultados(args);
        KiwiClient kiwi = KiwiClient.conectar();

        int rodada = rodadaEnv != null ? Integer.parseInt(rodadaEnv) : criarRodada(kiwi, Integer.parseInt(planoEnv));
        JsonNode dadosRodada = kiwi.filter("TestRun", Map.of("pk", rodada)).get(0);
        int plano = dadosRodada.get("plan").asInt();
        int produto = kiwi.filter("TestPlan", Map.of("pk", plano)).get(0).get("product").asInt();
        System.out.printf("TestRun TR-%d (plano TP-%d)%n", rodada, plano);

        Map<String, Integer> contagem = new TreeMap<>();
        for (Map.Entry<String, Resultado> entrada : resultados.entrySet()) {
            Resultado resultado = entrada.getValue();
            JsonNode caso = localizarCaso(kiwi, produto, entrada.getKey());
            int idCaso = caso.get("id").asInt();

            if (kiwi.filter("TestCase", Map.of("pk", idCaso, "plan", plano)).size() == 0) {
                kiwi.call("TestPlan.add_case", plano, idCaso);
            }
            int status = kiwi.idDoStatus(resultado.status);
            String comentario = "Resultado publicado a partir do junit.xml:\n" + String.join("\n", resultado.linhas);

            JsonNode execucoes = kiwi.call("TestRun.add_case", rodada, idCaso);
            for (JsonNode execucao : execucoes.isArray() ? execucoes : List.of(execucoes)) {
                int idExecucao = execucao.get("id").asInt();
                kiwi.call("TestExecution.update", idExecucao, Map.of("status", status));
                kiwi.call("TestExecution.add_comment", idExecucao, comentario);
            }
            contagem.merge(resultado.status, 1, Integer::sum);
            System.out.printf("  %-7s TC-%-3d %s%n", resultado.status, idCaso, caso.get("summary").asText());
        }

        kiwi.call("TestRun.update", rodada, Map.of("stop_date", LocalDateTime.now().format(DATA)));
        System.out.printf("Rodada TR-%d concluida -> %s%n", rodada, contagem);
    }

    /** Cria Version/Build se preciso e abre uma TestRun nova no plano. */
    private static int criarRodada(KiwiClient kiwi, int plano) {
        JsonNode dadosPlano = kiwi.filter("TestPlan", Map.of("pk", plano)).get(0);
        int produto = dadosPlano.get("product").asInt();
        String nomeProduto = dadosPlano.get("product__name").asText();
        String valorVersao = Optional.ofNullable(System.getenv("TCMS_PRODUCT_VERSION")).orElse(PrepararKiwi.VERSAO);
        String nomeBuild = Optional.ofNullable(System.getenv("TCMS_BUILD")).orElse(PrepararKiwi.BUILD);
        String prefixo = Optional.ofNullable(System.getenv("TCMS_PREFIX")).orElse("[automatizado]");

        int versao = kiwi.obterOuCriar("Version",
                Map.of("product", produto, "value", valorVersao),
                Map.of("product", produto, "value", valorVersao)).get("id").asInt();
        int build = kiwi.obterOuCriar("Build",
                Map.of("version", versao, "name", nomeBuild),
                Map.of("version", versao, "name", nomeBuild)).get("id").asInt();

        int gerente = dadosPlano.get("author").asInt();
        JsonNode rodada = kiwi.call("TestRun.create", Map.of(
                "summary", prefixo + " Results for " + nomeProduto + ", " + valorVersao + ", " + nomeBuild,
                "plan", plano,
                "build", build,
                "manager", gerente,
                "default_tester", gerente,
                "start_date", LocalDateTime.now().format(DATA)));
        return rodada.get("id").asInt();
    }

    /** Le os junit.xml e agrupa os testes por id do caso (CT03, CT14...). */
    static Map<String, Resultado> lerResultados(String[] caminhos) throws Exception {
        List<File> arquivos = new ArrayList<>();
        for (String caminho : caminhos) {
            File arquivo = new File(caminho);
            if (arquivo.isDirectory()) {
                File[] relatorios = arquivo.listFiles((pasta, nome) -> nome.startsWith("TEST-") && nome.endsWith(".xml"));
                if (relatorios != null) {
                    arquivos.addAll(List.of(relatorios));
                }
            } else {
                arquivos.add(arquivo);
            }
        }

        Map<String, Resultado> agrupado = new TreeMap<>();
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        for (File arquivo : arquivos) {
            NodeList casos = fabrica.newDocumentBuilder().parse(arquivo).getElementsByTagName("testcase");
            for (int i = 0; i < casos.getLength(); i++) {
                Element caso = (Element) casos.item(i);
                String nome = caso.getAttribute("name");
                Matcher id = ID_DO_CASO.matcher(nome);
                String chave = id.find() ? id.group(1).toUpperCase() : nome;

                String status = "PASSED";
                String mensagem = null;
                for (String[] tipo : new String[][] {{"failure", "FAILED"}, {"error", "ERROR"}, {"skipped", "WAIVED"}}) {
                    NodeList filhos = caso.getElementsByTagName(tipo[0]);
                    if (filhos.getLength() > 0) {
                        status = tipo[1];
                        mensagem = ((Element) filhos.item(0)).getAttribute("message");
                        break;
                    }
                }
                String linha = status + ": " + caso.getAttribute("classname") + "::" + nome
                        + (mensagem == null || mensagem.isEmpty() ? "" : "\n    " + mensagem);
                agrupado.computeIfAbsent(chave, k -> new Resultado()).registrar(status, linha);
            }
        }
        return agrupado;
    }

    private static JsonNode localizarCaso(KiwiClient kiwi, int produto, String chave) {
        if (ID_DO_CASO.matcher(chave).matches()) {
            JsonNode casos = kiwi.filter("TestCase",
                    Map.of("summary__startswith", chave + " ", "category__product", produto));
            if (casos.size() > 0) {
                return casos.get(0);
            }
            System.out.printf("  aviso: nenhum caso '%s ...' no Kiwi; criando um novo%n", chave);
        }
        JsonNode existentes = kiwi.filter("TestCase", Map.of("summary", chave, "category__product", produto));
        if (existentes.size() > 0) {
            return existentes.get(0);
        }
        return kiwi.call("TestCase.create", Map.of(
                "summary", chave.length() > 255 ? chave.substring(0, 255) : chave,
                "product", produto,
                "category", kiwi.filter("Category", Map.of("product", produto)).get(0).get("id").asInt(),
                "priority", kiwi.filter("Priority", Map.of()).get(0).get("id").asInt(),
                "case_status", kiwi.filter("TestCaseStatus", Map.of("name", "CONFIRMED")).get(0).get("id").asInt(),
                "is_automated", true));
    }
}
