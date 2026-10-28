#!/usr/bin/env python3
"""
preparar_kiwi.py - Aula 24 - cria no Kiwi TCMS tudo o que o laboratorio usa.

    Classification "Aplicacoes Web"
      Product  "TechStore Carrinho"
        Version  "1.0"
          Build  "1.0.0"
        Category "Regras de Negocio"   (casos cobertos por teste unitario)
        Category "API REST"            (casos cobertos pelo Bruno)
    TestPlan "TechStore Carrinho v1.0 - Regressao"
      TestCase CT01 ... CT08, CT10 ... CT16  (CONFIRMED, marcados como automatizados)
    Severidades de defeito: Baixa, Media, Alta, Critica

Estrategia "get or create": o que voce ja criou pela interface (Etapas 3 e 4
do tutorial) e' REAPROVEITADO. Casos de teste sao localizados pelo prefixo do
Summary ("CT03 ", "CT14 "...), entao nao importa se voce digitou o resto do
titulo um pouco diferente. Rodar duas vezes nao duplica nada.

Uso:
    pip install -r requirements.txt
    python3 preparar_kiwi.py
"""

from kiwi_conexao import conectar

PRODUTO = "TechStore Carrinho"
VERSAO = "1.0"
BUILD = "1.0.0"
PLANO = "TechStore Carrinho v1.0 - Regressao"

UNIT = "Regras de Negocio"
API = "API REST"

# (resumo, categoria, prioridade, script automatizado, roteiro Dado/Quando/Entao)
CASOS = [
    ("CT01 - Calcular subtotal de multiplos itens", UNIT, "P2",
     "app/tests/test_carrinho.py::test_CT01_subtotal_de_multiplos_itens",
     "Dado um carrinho com 2 Mouse Gamer (R$ 120,00) e 1 Cabo HDMI (R$ 25,00)\n"
     "Quando o subtotal for calculado\n"
     "Entao o subtotal deve ser R$ 265,00"),
    ("CT02 - Aplicar cupom GQS10 com 10% de desconto", UNIT, "P1",
     "app/tests/test_carrinho.py::test_CT02_cupom_GQS10_aplica_10_por_cento",
     "Dado um carrinho com 1 Teclado Mecanico (R$ 250,00)\n"
     "Quando o cupom GQS10 for aplicado\n"
     "Entao o desconto deve ser R$ 25,00, o frete gratis e o total R$ 225,00"),
    ("CT03 - Frete gratis no limite exato de R$ 200,00", UNIT, "P1",
     "app/tests/test_carrinho.py::test_CT03_frete_gratis_no_limite_exato_de_200",
     "Dado um valor de compra de exatamente R$ 200,00\n"
     "Quando o frete for calculado\n"
     "Entao o frete deve ser R$ 0,00 (RN03: a partir de 200,00 inclusive)"),
    ("CT04 - Cobrar frete abaixo de R$ 200,00", UNIT, "P2",
     "app/tests/test_carrinho.py::test_CT04_frete_cobrado_abaixo_de_200",
     "Dado um valor de compra de R$ 199,99\n"
     "Quando o frete for calculado\n"
     "Entao o frete deve ser R$ 15,00"),
    ("CT05 - Rejeitar quantidade menor que 1", UNIT, "P1",
     "app/tests/test_carrinho.py::test_CT05_rejeita_quantidade_menor_que_1",
     "Dado um item com quantidade 0 (e depois -1)\n"
     "Quando a quantidade for validada\n"
     "Entao o sistema deve recusar o item (RN04: minimo 1)"),
    ("CT06 - Validar limite maximo de 10 unidades", UNIT, "P2",
     "app/tests/test_carrinho.py::test_CT06_rejeita_quantidade_maior_que_10",
     "Dado um item com quantidade 1, 10, 11 e 50\n"
     "Quando a quantidade for validada\n"
     "Entao 1 e 10 sao aceitos e 11 e 50 sao recusados"),
    ("CT07 - Rejeitar cupom invalido", UNIT, "P3",
     "app/tests/test_carrinho.py::test_CT07_rejeita_cupom_invalido",
     "Dado um carrinho valido\n"
     "Quando o cupom NATAL50 (inexistente) for aplicado\n"
     "Entao o sistema deve recusar o cupom"),
    ("CT08 - Rejeitar produto inexistente", UNIT, "P3",
     "app/tests/test_carrinho.py::test_CT08_rejeita_produto_inexistente",
     "Dado um item com produto_id 999\n"
     "Quando o subtotal for calculado\n"
     "Entao o sistema deve informar que o produto nao existe"),
    ("CT10 - API: health check responde 200", API, "P1",
     "bruno/TechStore-Carrinho/CT10-health.bru",
     "Quando GET /api/health\nEntao status 200 e corpo {\"status\": \"ok\"}"),
    ("CT11 - API: listar produtos", API, "P2",
     "bruno/TechStore-Carrinho/CT11-listar-produtos.bru",
     "Quando GET /api/produtos\nEntao status 200 e uma lista com 4 produtos (id, nome, preco)"),
    ("CT12 - API: produto inexistente retorna 404", API, "P3",
     "bruno/TechStore-Carrinho/CT12-produto-inexistente.bru",
     "Quando GET /api/produtos/999\nEntao status 404 e corpo com a chave \"erro\""),
    ("CT13 - API: calcular carrinho com cupom GQS10", API, "P1",
     "bruno/TechStore-Carrinho/CT13-carrinho-com-cupom.bru",
     "Quando POST /api/carrinho/calcular com 1 Teclado e cupom GQS10\n"
     "Entao status 200, subtotal 250, desconto 25, frete 0 e total 225"),
    ("CT14 - API: frete gratis no limite de R$ 200,00", API, "P1",
     "bruno/TechStore-Carrinho/CT14-frete-gratis-limite.bru",
     "Quando POST /api/carrinho/calcular com 1 Mouse Gamer + 1 Headset (R$ 200,00)\n"
     "Entao status 200, frete 0 e total 200"),
    ("CT15 - API: quantidade zero retorna 400", API, "P1",
     "bruno/TechStore-Carrinho/CT15-quantidade-zero.bru",
     "Quando POST /api/carrinho/calcular com quantidade 0\n"
     "Entao status 400 e corpo com a chave \"erro\""),
    ("CT16 - API: cupom invalido retorna 400", API, "P3",
     "bruno/TechStore-Carrinho/CT16-cupom-invalido.bru",
     "Quando POST /api/carrinho/calcular com o cupom NATAL50\n"
     "Entao status 400 e a mensagem de erro cita o cupom"),
]

SEVERIDADES = [
    ("Baixa", 1, "fa fa-info-circle", "#0088ce"),
    ("Media", 2, "fa fa-exclamation-triangle", "#f0ab00"),
    ("Alta", 3, "fa fa-exclamation-circle", "#ec7a08"),
    ("Critica", 4, "fa fa-fire", "#cc0000"),
]


def obter_ou_criar(api, filtro, dados):
    existentes = api.filter(filtro)
    return existentes[0] if existentes else api.create(dados)


def main():
    rpc = conectar()

    classificacao = obter_ou_criar(
        rpc.Classification, {"name": "Aplicacoes Web"}, {"name": "Aplicacoes Web"}
    )
    produto = obter_ou_criar(
        rpc.Product,
        {"name": PRODUTO},
        {
            "name": PRODUTO,
            "classification": classificacao["id"],
            "description": "API de carrinho da TechStore (laboratorio da Aula 24)",
        },
    )
    versao = obter_ou_criar(
        rpc.Version,
        {"product": produto["id"], "value": VERSAO},
        {"product": produto["id"], "value": VERSAO},
    )
    build = obter_ou_criar(
        rpc.Build,
        {"version": versao["id"], "name": BUILD},
        {"version": versao["id"], "name": BUILD},
    )
    categorias = {
        nome: obter_ou_criar(
            rpc.Category,
            {"product": produto["id"], "name": nome},
            {"product": produto["id"], "name": nome},
        )
        for nome in (UNIT, API)
    }
    print(f"Product  {produto['id']:>3}  {PRODUTO}")
    print(f"Version  {versao['id']:>3}  {VERSAO}")
    print(f"Build    {build['id']:>3}  {BUILD}")

    tipo = rpc.PlanType.filter({"name": "Regression"})[0]
    plano = obter_ou_criar(
        rpc.TestPlan,
        {"name": PLANO, "product": produto["id"]},
        {
            "name": PLANO,
            "product": produto["id"],
            "product_version": versao["id"],
            "type": tipo["id"],
            "text": "Regressao da API de carrinho: regras RN01 a RN05 "
                    "(testes unitarios com pytest e testes de API com Bruno).",
        },
    )
    print(f"TestPlan {plano['id']:>3}  {PLANO}   <-- use este id em TCMS_PLAN_ID")

    confirmado = rpc.TestCaseStatus.filter({"name": "CONFIRMED"})[0]["id"]
    prioridades = {p["value"]: p["id"] for p in rpc.Priority.filter({})}

    for resumo, categoria, prioridade, script, roteiro in CASOS:
        prefixo = resumo.split(" ")[0] + " "
        existentes = rpc.TestCase.filter(
            {"summary__startswith": prefixo, "category__product": produto["id"]}
        )
        if existentes:
            caso = existentes[0]
            acao = "reuse"
        else:
            caso = rpc.TestCase.create(
                {
                    "summary": resumo,
                    "product": produto["id"],
                    "category": categorias[categoria]["id"],
                    "priority": prioridades[prioridade],
                    "case_status": confirmado,
                    "text": roteiro,
                }
            )
            acao = "added"
        # Marca como automatizado e diz QUAL codigo executa o caso.
        rpc.TestCase.update(caso["id"], {"is_automated": True, "script": script})
        rpc.TestPlan.add_case(plano["id"], caso["id"])
        print(f"  {acao}: TC-{caso['id']:<3} {caso['summary']}")

    for nome, peso, icone, cor in SEVERIDADES:
        obter_ou_criar(
            rpc.Severity,
            {"name": nome},
            {"name": nome, "weight": peso, "icon": icone, "color": cor},
        )
    print("Severidades: " + ", ".join(s[0] for s in SEVERIDADES))


if __name__ == "__main__":
    main()
