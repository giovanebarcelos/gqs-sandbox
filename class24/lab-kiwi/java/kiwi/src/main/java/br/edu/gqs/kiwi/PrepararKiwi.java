package br.edu.gqs.kiwi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Cria no Kiwi TCMS tudo o que o laboratorio usa.
 *
 * <pre>
 *   Classification "Aplicacoes Web"
 *     Product  "TechStore Carrinho"
 *       Version  "1.0"
 *         Build  "1.0.0"
 *       Category "Regras de Negocio"   (casos cobertos por teste unitario)
 *       Category "API REST"            (casos cobertos pelo Bruno)
 *   TestPlan "TechStore Carrinho v1.0 - Regressao"
 *     TestCase CT01 ... CT08, CT10 ... CT16  (CONFIRMED, marcados como automatizados)
 *   Severidades de defeito: Baixa, Media, Alta, Critica
 * </pre>
 *
 * Estrategia "get or create": o que voce ja criou pela interface e'
 * REAPROVEITADO. Casos de teste sao localizados pelo prefixo do Summary
 * ("CT03 ", "CT14 "...). Rodar duas vezes nao duplica nada.
 *
 * <pre>
 *   java -jar target/kiwi-tools.jar preparar
 * </pre>
 */
public class PrepararKiwi {

    static final String PRODUTO = "TechStore Carrinho";
    static final String VERSAO = "1.0";
    static final String BUILD = "1.0.0";
    static final String PLANO = "TechStore Carrinho v1.0 - Regressao";

    private static final String UNIT = "Regras de Negocio";
    private static final String API = "API REST";
    private static final String TESTE_JAVA = "java/app/src/test/java/br/edu/gqs/techstore/CalculadoraCarrinhoTest.java#";
    private static final String BRUNO = "bruno/TechStore-Carrinho/";

    record Caso(String resumo, String categoria, String prioridade, String script, String roteiro) {
    }

    static final List<Caso> CASOS = List.of(
            new Caso("CT01 - Calcular subtotal de multiplos itens", UNIT, "P2",
                    TESTE_JAVA + "ct01_subtotalDeMultiplosItens",
                    "Dado um carrinho com 2 Mouse Gamer (R$ 120,00) e 1 Cabo HDMI (R$ 25,00)\n"
                            + "Quando o subtotal for calculado\nEntao o subtotal deve ser R$ 265,00"),
            new Caso("CT02 - Aplicar cupom GQS10 com 10% de desconto", UNIT, "P1",
                    TESTE_JAVA + "ct02_cupomGqs10Aplica10PorCento",
                    "Dado um carrinho com 1 Teclado Mecanico (R$ 250,00)\nQuando o cupom GQS10 for aplicado\n"
                            + "Entao o desconto deve ser R$ 25,00, o frete gratis e o total R$ 225,00"),
            new Caso("CT03 - Frete gratis no limite exato de R$ 200,00", UNIT, "P1",
                    TESTE_JAVA + "ct03_freteGratisNoLimiteExatoDe200",
                    "Dado um valor de compra de exatamente R$ 200,00\nQuando o frete for calculado\n"
                            + "Entao o frete deve ser R$ 0,00 (RN03: a partir de 200,00 inclusive)"),
            new Caso("CT04 - Cobrar frete abaixo de R$ 200,00", UNIT, "P2",
                    TESTE_JAVA + "ct04_freteCobradoAbaixoDe200",
                    "Dado um valor de compra de R$ 199,99\nQuando o frete for calculado\n"
                            + "Entao o frete deve ser R$ 15,00"),
            new Caso("CT05 - Rejeitar quantidade menor que 1", UNIT, "P1",
                    TESTE_JAVA + "ct05_rejeitaQuantidadeMenorQue1",
                    "Dado um item com quantidade 0 (e depois -1)\nQuando a quantidade for validada\n"
                            + "Entao o sistema deve recusar o item (RN04: minimo 1)"),
            new Caso("CT06 - Validar limite maximo de 10 unidades", UNIT, "P2",
                    TESTE_JAVA + "ct06_rejeitaQuantidadeMaiorQue10",
                    "Dado um item com quantidade 1, 10, 11 e 50\nQuando a quantidade for validada\n"
                            + "Entao 1 e 10 sao aceitos e 11 e 50 sao recusados"),
            new Caso("CT07 - Rejeitar cupom invalido", UNIT, "P3",
                    TESTE_JAVA + "ct07_rejeitaCupomInvalido",
                    "Dado um carrinho valido\nQuando o cupom NATAL50 (inexistente) for aplicado\n"
                            + "Entao o sistema deve recusar o cupom"),
            new Caso("CT08 - Rejeitar produto inexistente", UNIT, "P3",
                    TESTE_JAVA + "ct08_rejeitaProdutoInexistente",
                    "Dado um item com produto_id 999\nQuando o subtotal for calculado\n"
                            + "Entao o sistema deve informar que o produto nao existe"),
            new Caso("CT10 - API: health check responde 200", API, "P1",
                    BRUNO + "CT10-health.bru",
                    "Quando GET /api/health\nEntao status 200 e corpo {\"status\": \"ok\"}"),
            new Caso("CT11 - API: listar produtos", API, "P2",
                    BRUNO + "CT11-listar-produtos.bru",
                    "Quando GET /api/produtos\nEntao status 200 e uma lista com 4 produtos (id, nome, preco)"),
            new Caso("CT12 - API: produto inexistente retorna 404", API, "P3",
                    BRUNO + "CT12-produto-inexistente.bru",
                    "Quando GET /api/produtos/999\nEntao status 404 e corpo com a chave \"erro\""),
            new Caso("CT13 - API: calcular carrinho com cupom GQS10", API, "P1",
                    BRUNO + "CT13-carrinho-com-cupom.bru",
                    "Quando POST /api/carrinho/calcular com 1 Teclado e cupom GQS10\n"
                            + "Entao status 200, subtotal 250, desconto 25, frete 0 e total 225"),
            new Caso("CT14 - API: frete gratis no limite de R$ 200,00", API, "P1",
                    BRUNO + "CT14-frete-gratis-limite.bru",
                    "Quando POST /api/carrinho/calcular com 1 Mouse Gamer + 1 Headset (R$ 200,00)\n"
                            + "Entao status 200, frete 0 e total 200"),
            new Caso("CT15 - API: quantidade zero retorna 400", API, "P1",
                    BRUNO + "CT15-quantidade-zero.bru",
                    "Quando POST /api/carrinho/calcular com quantidade 0\n"
                            + "Entao status 400 e corpo com a chave \"erro\""),
            new Caso("CT16 - API: cupom invalido retorna 400", API, "P3",
                    BRUNO + "CT16-cupom-invalido.bru",
                    "Quando POST /api/carrinho/calcular com o cupom NATAL50\n"
                            + "Entao status 400 e a mensagem de erro cita o cupom"));

    record Severidade(String nome, int peso, String icone, String cor) {
    }

    static final List<Severidade> SEVERIDADES = List.of(
            new Severidade("Baixa", 1, "fa fa-info-circle", "#0088ce"),
            new Severidade("Media", 2, "fa fa-exclamation-triangle", "#f0ab00"),
            new Severidade("Alta", 3, "fa fa-exclamation-circle", "#ec7a08"),
            new Severidade("Critica", 4, "fa fa-fire", "#cc0000"));

    public static void main(String[] args) {
        KiwiClient kiwi = KiwiClient.conectar();

        JsonNode classificacao = kiwi.obterOuCriar("Classification",
                Map.of("name", "Aplicacoes Web"), Map.of("name", "Aplicacoes Web"));
        int produto = kiwi.obterOuCriar("Product", Map.of("name", PRODUTO), Map.of(
                "name", PRODUTO,
                "classification", classificacao.get("id").asInt(),
                "description", "API de carrinho da TechStore (laboratorio da Aula 24)")).get("id").asInt();
        int versao = kiwi.obterOuCriar("Version",
                Map.of("product", produto, "value", VERSAO),
                Map.of("product", produto, "value", VERSAO)).get("id").asInt();
        int build = kiwi.obterOuCriar("Build",
                Map.of("version", versao, "name", BUILD),
                Map.of("version", versao, "name", BUILD)).get("id").asInt();
        Map<String, Integer> categorias = new HashMap<>();
        for (String nome : List.of(UNIT, API)) {
            categorias.put(nome, kiwi.obterOuCriar("Category",
                    Map.of("product", produto, "name", nome),
                    Map.of("product", produto, "name", nome)).get("id").asInt());
        }
        System.out.printf("Product  %3d  %s%n", produto, PRODUTO);
        System.out.printf("Version  %3d  %s%n", versao, VERSAO);
        System.out.printf("Build    %3d  %s%n", build, BUILD);

        int tipoRegressao = kiwi.filter("PlanType", Map.of("name", "Regression")).get(0).get("id").asInt();
        int plano = kiwi.obterOuCriar("TestPlan", Map.of("name", PLANO, "product", produto), Map.of(
                "name", PLANO,
                "product", produto,
                "product_version", versao,
                "type", tipoRegressao,
                "text", "Regressao da API de carrinho: regras RN01 a RN05 "
                        + "(testes unitarios com JUnit/pytest e testes de API com Bruno).")).get("id").asInt();
        System.out.printf("TestPlan %3d  %s   <-- use este id em TCMS_PLAN_ID%n", plano, PLANO);

        int confirmado = kiwi.filter("TestCaseStatus", Map.of("name", "CONFIRMED")).get(0).get("id").asInt();
        Map<String, Integer> prioridades = new HashMap<>();
        kiwi.filter("Priority", Map.of()).forEach(p -> prioridades.put(p.get("value").asText(), p.get("id").asInt()));

        for (Caso caso : CASOS) {
            String prefixo = caso.resumo().split(" ")[0] + " ";
            JsonNode existentes = kiwi.filter("TestCase",
                    Map.of("summary__startswith", prefixo, "category__product", produto));
            JsonNode testCase;
            String acao;
            if (existentes.size() > 0) {
                testCase = existentes.get(0);
                acao = "reuse";
            } else {
                testCase = kiwi.call("TestCase.create", Map.of(
                        "summary", caso.resumo(),
                        "product", produto,
                        "category", categorias.get(caso.categoria()),
                        "priority", prioridades.get(caso.prioridade()),
                        "case_status", confirmado,
                        "text", caso.roteiro()));
                acao = "added";
            }
            int id = testCase.get("id").asInt();
            // Marca como automatizado e diz QUAL codigo executa o caso.
            kiwi.call("TestCase.update", id, Map.of("is_automated", true, "script", caso.script()));
            kiwi.call("TestPlan.add_case", plano, id);
            System.out.printf("  %s: TC-%-3d %s%n", acao, id, testCase.get("summary").asText());
        }

        for (Severidade s : SEVERIDADES) {
            kiwi.obterOuCriar("Severity", Map.of("name", s.nome()),
                    Map.of("name", s.nome(), "weight", s.peso(), "icon", s.icone(), "color", s.cor()));
        }
        System.out.println("Severidades: Baixa, Media, Alta, Critica");
    }
}
