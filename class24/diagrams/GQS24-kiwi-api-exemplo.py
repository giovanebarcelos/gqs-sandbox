#!/usr/bin/env python3
r"""
GQS24-kiwi-api-exemplo.py
Garantia da Qualidade de Software - Aula 24
Exemplo de automacao do Kiwi TCMS pela API JSON-RPC/XML-RPC (biblioteca tcms-api).

O script monta, do zero, toda a hierarquia usada no laboratorio da aula:

    Classification -> Product -> Version -> Build
                             -> Category
    TestPlan -> TestCase (N)
    TestRun  -> TestExecution (N)  ->  status PASSED / FAILED / BLOCKED

e no final registra o resultado de uma rodada de testes.

Todas as funcoes usam a estrategia "get or create": rodar o script duas vezes
nao duplica nada, apenas reaproveita o que ja existe. Isso permite repetir o
laboratorio sem recriar o banco.

-------------------------------------------------------------------------------
PRE-REQUISITOS
-------------------------------------------------------------------------------
1. Kiwi TCMS no ar (docker compose -f GQS24-kiwi-docker-compose.yml up -d).

2. Instalar a biblioteca cliente:

       python3 -m venv venv
       source venv/bin/activate        # Windows: venv\Scripts\activate
       pip install tcms-api

3. Credenciais. NUNCA escreva usuario e senha dentro do script - este arquivo
   vai para o repositorio. Use uma das duas formas abaixo:

   (a) arquivo ~/.tcms.conf (forma documentada pelo Kiwi TCMS), modo 600:

           [tcms]
           url = https://localhost:8443/xml-rpc/
           username = admin
           password = sua-senha

       chmod 600 ~/.tcms.conf

   (b) variaveis de ambiente (usadas por este script quando presentes):

           export KIWI_URL=https://localhost:8443/xml-rpc/
           export KIWI_USERNAME=admin
           export KIWI_PASSWORD=sua-senha

   Repare que esta e' exatamente a regra que o SonarQube cobra na Aula 23B
   com a regra S6437 / S2068 (senha embutida no codigo-fonte).

-------------------------------------------------------------------------------
CERTIFICADO AUTOASSINADO
-------------------------------------------------------------------------------
A imagem oficial do Kiwi TCMS sobe com HTTPS e certificado autoassinado. Em
laboratorio local isso faz o Python recusar a conexao. A linha marcada com
"APENAS EM LABORATORIO" desliga a verificacao do certificado. Em producao ela
deve ser removida e o servidor deve usar um certificado valido.
-------------------------------------------------------------------------------
"""

import os
import ssl
import sys

from tcms_api import TCMS

# --------------------------------------------------------------------------- #
# APENAS EM LABORATORIO: aceita o certificado autoassinado do container.
# Em producao, remova estas duas linhas e use um certificado valido.
# --------------------------------------------------------------------------- #
ssl._create_default_https_context = ssl._create_unverified_context


def conectar():
    """Abre a conexao com o Kiwi TCMS.

    Usa as variaveis de ambiente KIWI_URL / KIWI_USERNAME / KIWI_PASSWORD
    quando existirem; caso contrario cai no ~/.tcms.conf, que e' a forma
    documentada pelo projeto.
    """
    url = os.environ.get("KIWI_URL")
    usuario = os.environ.get("KIWI_USERNAME")
    senha = os.environ.get("KIWI_PASSWORD")

    if url and usuario and senha:
        return TCMS(url=url, username=usuario, password=senha).exec

    try:
        return TCMS().exec
    except RuntimeError as erro:
        print(f"ERRO: {erro}")
        print("Configure ~/.tcms.conf ou exporte KIWI_URL/KIWI_USERNAME/KIWI_PASSWORD.")
        sys.exit(1)


# --------------------------------------------------------------------------- #
# Helpers "get or create"
# --------------------------------------------------------------------------- #
def obter_ou_criar(rpc, entidade, filtro, dados):
    """Retorna o primeiro objeto que casa com o filtro; cria se nao existir.

    entidade e' o nome da API do Kiwi ("Product", "TestPlan", ...).
    """
    api = getattr(rpc, entidade)
    existentes = api.filter(filtro)
    if existentes:
        return existentes[0]
    return api.create(dados)


def main():
    rpc = conectar()

    # ----------------------------------------------------------------- #
    # 1. Classification -> Product
    #    Classification agrupa produtos ("Aplicacoes Web", "Mobile", ...).
    #    A instalacao nova do Kiwi vem sem nenhuma; e' preciso criar.
    # ----------------------------------------------------------------- #
    classificacao = obter_ou_criar(
        rpc,
        "Classification",
        {"name": "Aplicacoes Web"},
        {"name": "Aplicacoes Web"},
    )
    print(f"Classification : {classificacao['id']:>3}  {classificacao['name']}")

    produto = obter_ou_criar(
        rpc,
        "Product",
        {"name": "TechStore"},
        {
            "name": "TechStore",
            "classification": classificacao["id"],
            "description": "Loja virtual usada como estudo de caso da disciplina",
        },
    )
    print(f"Product        : {produto['id']:>3}  {produto['name']}")

    # ----------------------------------------------------------------- #
    # 2. Version e Build
    #    Version = versao do produto sob teste. Build = pacote concreto
    #    daquela versao (o artefato que a rodada de testes exercita).
    # ----------------------------------------------------------------- #
    versao = obter_ou_criar(
        rpc,
        "Version",
        {"product": produto["id"], "value": "1.5.0"},
        {"product": produto["id"], "value": "1.5.0"},
    )
    print(f"Version        : {versao['id']:>3}  {versao['value']}")

    build = obter_ou_criar(
        rpc,
        "Build",
        {"version": versao["id"], "name": "v1.5.0-RC1"},
        {"version": versao["id"], "name": "v1.5.0-RC1"},
    )
    print(f"Build          : {build['id']:>3}  {build['name']}")

    # ----------------------------------------------------------------- #
    # 3. Category
    #    Classifica o caso de teste por area funcional. Toda instalacao ja
    #    cria a categoria "--default--"; aqui criamos uma propria.
    # ----------------------------------------------------------------- #
    categoria = obter_ou_criar(
        rpc,
        "Category",
        {"product": produto["id"], "name": "Autenticacao"},
        {"product": produto["id"], "name": "Autenticacao"},
    )
    print(f"Category       : {categoria['id']:>3}  {categoria['name']}")

    # ----------------------------------------------------------------- #
    # 4. TestPlan
    #    O campo "type" aceita o id de um TestPlanType. Os tipos padrao sao
    #    Unit, Integration, Function, System, Acceptance, Installation,
    #    Performance, Product, Interoperability, Smoke e Regression.
    # ----------------------------------------------------------------- #
    tipo_system = rpc.PlanType.filter({"name": "System"})[0]

    plano = obter_ou_criar(
        rpc,
        "TestPlan",
        {"name": "Sprint 5 - Modulo Autenticacao"},
        {
            "name": "Sprint 5 - Modulo Autenticacao",
            "product": produto["id"],
            "product_version": versao["id"],
            "type": tipo_system["id"],
            "text": "Plano de testes de sistema do modulo de login da TechStore.",
        },
    )
    print(f"TestPlan       : {plano['id']:>3}  {plano['name']}")

    # ----------------------------------------------------------------- #
    # 5. TestCase
    #    case_status: 1=PROPOSED 2=CONFIRMED 3=DISABLED 4=NEED_UPDATE
    #    priority   : 1=P1 (mais alta) ... 5=P5
    #    Somente casos CONFIRMED podem ser adicionados a um TestRun.
    # ----------------------------------------------------------------- #
    status_confirmed = rpc.TestCaseStatus.filter({"name": "CONFIRMED"})[0]
    prioridade_p1 = rpc.Priority.filter({"value": "P1"})[0]
    prioridade_p2 = rpc.Priority.filter({"value": "P2"})[0]

    especificacao = [
        (
            "TS-001 Login com credenciais validas",
            prioridade_p1["id"],
            "Dado um usuario cadastrado e ativo\n"
            "Quando informar e-mail e senha corretos\n"
            "Entao o sistema deve exibir a pagina inicial autenticada",
        ),
        (
            "TS-002 Login com senha incorreta",
            prioridade_p1["id"],
            "Dado um usuario cadastrado e ativo\n"
            "Quando informar a senha errada\n"
            "Entao o sistema deve exibir 'Credenciais invalidas' "
            "e nao revelar se o e-mail existe",
        ),
        (
            "TS-003 Bloqueio apos 3 tentativas",
            prioridade_p2["id"],
            "Dado um usuario que errou a senha 2 vezes\n"
            "Quando errar a senha pela terceira vez\n"
            "Entao a conta deve ser bloqueada por 15 minutos",
        ),
    ]

    casos = []
    for resumo, prioridade, roteiro in especificacao:
        caso = obter_ou_criar(
            rpc,
            "TestCase",
            {"summary": resumo},
            {
                "summary": resumo,
                "product": produto["id"],
                "category": categoria["id"],
                "priority": prioridade,
                "case_status": status_confirmed["id"],
                "text": roteiro,
                "is_automated": False,
            },
        )
        casos.append(caso)
        print(f"TestCase       : {caso['id']:>3}  {caso['summary']}")

        # add_case e' idempotente: repetir nao duplica o vinculo.
        rpc.TestPlan.add_case(plano["id"], caso["id"])

    # ----------------------------------------------------------------- #
    # 6. TestRun
    #    A rodada amarra um plano a um build. manager e default_tester
    #    recebem o id de um usuario; usamos o proprio usuario da conexao.
    # ----------------------------------------------------------------- #
    eu = rpc.User.filter({})[0]

    rodada = obter_ou_criar(
        rpc,
        "TestRun",
        {"summary": "Execucao v1.5.0-RC1"},
        {
            "plan": plano["id"],
            "build": build["id"],
            "summary": "Execucao v1.5.0-RC1",
            "manager": eu["id"],
            "default_tester": eu["id"],
        },
    )
    print(f"TestRun        : {rodada['id']:>3}  {rodada['summary']}")

    for caso in casos:
        rpc.TestRun.add_case(rodada["id"], caso["id"])

    # ----------------------------------------------------------------- #
    # 7. TestExecution
    #    add_case cria automaticamente uma TestExecution por caso, sempre
    #    no status IDLE. Registrar o resultado = atualizar esse status.
    #    Status disponiveis: IDLE, RUNNING, PAUSED, PASSED, FAILED,
    #    BLOCKED, ERROR, WAIVED.
    # ----------------------------------------------------------------- #
    def id_do_status(nome):
        return rpc.TestExecutionStatus.filter({"name": nome})[0]["id"]

    resultado_da_rodada = {
        "TS-001 Login com credenciais validas": ("PASSED", None),
        "TS-002 Login com senha incorreta": (
            "FAILED",
            "A mensagem exibida foi 'Usuario nao encontrado', "
            "revelando a existencia do e-mail. Defeito TS-002-D1.",
        ),
        # TS-003 fica IDLE de proposito: rodada incompleta, e' isso que
        # a metrica de progresso do Kiwi deve mostrar no dashboard.
    }

    for execucao in rpc.TestExecution.filter({"run_id": rodada["id"]}):
        resumo = execucao["case__summary"]
        if resumo not in resultado_da_rodada:
            continue
        status, comentario = resultado_da_rodada[resumo]
        rpc.TestExecution.update(execucao["id"], {"status": id_do_status(status)})
        if comentario:
            rpc.TestExecution.add_comment(execucao["id"], comentario)

    # ----------------------------------------------------------------- #
    # 8. Leitura do resultado - e' daqui que sai a metrica da aula
    # ----------------------------------------------------------------- #
    print("\n--- resultado da rodada ---")
    execucoes = rpc.TestExecution.filter({"run_id": rodada["id"]})
    contagem = {}
    for execucao in execucoes:
        status = execucao["status__name"]
        contagem[status] = contagem.get(status, 0) + 1
        print(f"  execucao {execucao['id']}: {status:<8} {execucao['case__summary']}")

    total = len(execucoes)
    executados = total - contagem.get("IDLE", 0)
    aprovados = contagem.get("PASSED", 0)

    print(f"\n  total de casos      : {total}")
    print(f"  executados          : {executados}")
    print(f"  progresso           : {executados / total:.0%}")
    if executados:
        print(f"  taxa de aprovacao   : {aprovados / executados:.0%}")


if __name__ == "__main__":
    main()
