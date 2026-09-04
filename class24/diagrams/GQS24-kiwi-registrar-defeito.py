#!/usr/bin/env python3
r"""
GQS24-kiwi-registrar-defeito.py
Garantia da Qualidade de Software - Aula 24
Bug tracker INTEGRADO do Kiwi TCMS + leitura das metricas de telemetria.

Diferenca importante em relacao ao TestLink: o Kiwi TCMS nao depende de um
Jira, Mantis ou Bugzilla para registrar defeitos - ele tem um bug tracker
proprio (menu TESTING > New Bug). A integracao com trackers externos existe e
continua disponivel (Bugzilla e JIRA ja vem pre-cadastrados em
BugTracker.filter), mas e' opcional.

O script faz tres coisas:

  1. cria a escala de severidade - o Kiwi sobe SEM nenhuma severidade
     cadastrada (Severity.filter({}) devolve lista vazia);
  2. registra um defeito a partir da execucao que falhou e amarra os dois
     com Bug.add_execution - e' esse vinculo que da rastreabilidade
     "caso de teste -> execucao -> defeito";
  3. le as metricas prontas da API de telemetria, as mesmas que alimentam o
     menu TELEMETRY da interface.

PRE-REQUISITO: rodar antes o GQS24-kiwi-api-exemplo.py, que monta o produto,
o plano, os casos e a rodada de execucao.

USO
    export KIWI_URL=https://localhost:8443/xml-rpc/
    export KIWI_USERNAME=admin
    export KIWI_PASSWORD=sua-senha
    python3 GQS24-kiwi-registrar-defeito.py

(ou configure o ~/.tcms.conf, como explicado em GQS24-kiwi-api-exemplo.py)
"""

import json
import os
import ssl
import sys

from tcms_api import TCMS

# APENAS EM LABORATORIO: certificado autoassinado do container.
ssl._create_default_https_context = ssl._create_unverified_context


def conectar():
    url = os.environ.get("KIWI_URL")
    usuario = os.environ.get("KIWI_USERNAME")
    senha = os.environ.get("KIWI_PASSWORD")
    if url and usuario and senha:
        return TCMS(url=url, username=usuario, password=senha).exec
    try:
        return TCMS().exec
    except RuntimeError as erro:
        print(f"ERRO: {erro}")
        sys.exit(1)


def main():
    rpc = conectar()

    # ----------------------------------------------------------------- #
    # 1. Escala de severidade
    #    O peso (weight) e' quem define a ordem na interface: quanto maior,
    #    mais grave. icon e color sao OBRIGATORIOS - o Kiwi usa os icones do
    #    Font Awesome que ja vem embutidos na interface.
    # ----------------------------------------------------------------- #
    escala = [
        ("Critica", 4, "fa fa-fire", "#cc0000"),
        ("Alta", 3, "fa fa-exclamation-circle", "#ec7a08"),
        ("Media", 2, "fa fa-exclamation-triangle", "#f0ab00"),
        ("Baixa", 1, "fa fa-info-circle", "#0088ce"),
    ]
    if not rpc.Severity.filter({}):
        for nome, peso, icone, cor in escala:
            rpc.Severity.create(
                {"name": nome, "weight": peso, "icon": icone, "color": cor}
            )
    severidades = {s["name"]: s["id"] for s in rpc.Severity.filter({})}
    print("severidades:", ", ".join(severidades))

    # ----------------------------------------------------------------- #
    # 2. Defeito a partir da execucao que falhou
    #    product, version e build sao OBRIGATORIOS em Bug.create - e' o que
    #    responde "em qual versao o defeito foi encontrado".
    # ----------------------------------------------------------------- #
    falhas = [
        e
        for e in rpc.TestExecution.filter({"run_id": 1})
        if e["status__name"] == "FAILED"
    ]
    if not falhas:
        print("Nenhuma execucao FAILED na rodada 1. Rode antes o GQS24-kiwi-api-exemplo.py.")
        sys.exit(1)
    execucao = falhas[0]
    print(f"execucao com falha: TE-{execucao['id']} {execucao['case__summary']}")

    resumo = "Mensagem de login revela se o e-mail existe na base"

    # ATENCAO a uma inconsistencia real da API 16.3: Bug.create devolve a
    # chave "id", mas Bug.filter devolve "pk" para o mesmo campo.
    ja_existe = rpc.Bug.filter({"summary": resumo})
    if ja_existe:
        defeito = {"id": ja_existe[0]["pk"], "summary": ja_existe[0]["summary"]}
    else:
        eu = rpc.User.filter({"username": "admin"})[0]
        defeito = rpc.Bug.create(
            {
                "summary": resumo,
                "status": True,  # True = aberto, False = fechado
                "product": rpc.Product.filter({"name": "TechStore"})[0]["id"],
                "version": rpc.Version.filter({"value": "1.5.0"})[0]["id"],
                "build": rpc.Build.filter({"name": "v1.5.0-RC1"})[0]["id"],
                "reporter": eu["id"],
                "assignee": eu["id"],
                "severity": severidades["Alta"],
            }
        )
    print(f"defeito: BUG-{defeito['id']} {defeito['summary']}")

    # O vinculo entre defeito e execucao e' o que fecha a rastreabilidade.
    rpc.Bug.add_execution(defeito["id"], execucao["id"])

    rpc.Bug.add_comment(
        defeito["id"],
        "Passos: informar um e-mail cadastrado com a senha errada.\n"
        "Esperado: 'Credenciais invalidas'.\n"
        "Obtido: 'Senha incorreta' - permite enumerar contas validas.\n"
        "Severidade Alta (falha de seguranca); prioridade e' decisao do PO.",
    )

    # ----------------------------------------------------------------- #
    # 3. Telemetria - as mesmas metricas do menu TELEMETRY
    # ----------------------------------------------------------------- #
    print("\n--- TELEMETRY > Testing > Breakdown ---")
    print(json.dumps(rpc.Testing.breakdown({}), indent=2))

    print("\n--- TELEMETRY > Testing > TestCase health ---")
    for item in rpc.Testing.test_case_health({}):
        c = item["count"]
        print(
            f"  TC-{item['case_id']} {item['case_summary']}: "
            f"{c['fail']} falha(s) em {c['all']} execucao(oes)"
        )

    print("\n--- TELEMETRY > Testing > Execution > Trends ---")
    tendencia = rpc.Testing.execution_trends({})
    print("  rodadas:", tendencia["categories"])
    for status, valores in tendencia["data_set"].items():
        if any(valores):
            print(f"  {status:<8} {valores}")
    print("  contagem:", tendencia["status_count"])

    # ----------------------------------------------------------------- #
    # ATENCAO - duas limitacoes reais do Kiwi TCMS 16.3, medidas em sala:
    #
    #   Testing.status_matrix  -> falha por XML-RPC ("dictionary key must be
    #       string"), porque a resposta usa inteiros como chave. Funciona pelo
    #       endpoint /json-rpc/, que e' o que a propria interface web usa.
    #
    #   Bug.details            -> devolve "Internal error: 'int' object has no
    #       attribute 'decode'" nos dois protocolos. Use Bug.filter no lugar.
    #
    # Nao e' erro de configuracao: e' o estado da API nesta versao. Verificar
    # o que a ferramenta realmente faz - e nao o que a documentacao promete -
    # e' parte do trabalho de QA.
    # ----------------------------------------------------------------- #


if __name__ == "__main__":
    main()
