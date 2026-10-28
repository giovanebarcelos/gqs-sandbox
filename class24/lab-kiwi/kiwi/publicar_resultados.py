#!/usr/bin/env python3
"""
publicar_resultados.py - Aula 24 - envia um ou mais junit.xml ao Kiwi TCMS.

E' a "ponte" que faz um caso de teste escrito no Kiwi ser EXECUTADO por
codigo: o pytest e o Bruno geram junit.xml; este script le o arquivo, acha o
caso de teste certo no Kiwi e grava PASSED/FAILED na execucao.

COMO O CASO E' ENCONTRADO
    O nome do teste comeca com o id do caso (CT03, CT14...):
        pytest : test_CT03_frete_gratis_no_limite_exato_de_200
        Bruno  : test("CT14 - Carrinho de exatamente 200,00 tem frete gratis")
    O script procura no produto o TestCase cujo Summary comeca com "CT03 ".
    Varios testes com o mesmo id (ex.: parametrize) viram UMA execucao no
    Kiwi, que so' fica PASSED se todos passarem.
    Teste sem id no nome vira um TestCase novo, criado com o proprio nome
    (comportamento padrao do plugin oficial).

QUEM FAZ O TRABALHO PESADO
    A classe Backend do tcms-api (a mesma usada pelo plugin oficial
    kiwitcms-junit.xml-plugin) cria/reaproveita Version, Build e o TestRun.

VARIAVEIS DE AMBIENTE
    TCMS_PLAN_ID          id do TestPlan (veja a saida do preparar_kiwi.py)
    TCMS_PRODUCT          default "TechStore Carrinho"
    TCMS_PRODUCT_VERSION  default "1.0"
    TCMS_BUILD            default "1.0.0"  -> use 1.0.1 depois da correcao
    TCMS_PREFIX           prefixo do nome da rodada, ex. "[pytest]" ou "[bruno]"
    TCMS_RUN_ID           grava numa rodada JA existente em vez de criar outra
    + credenciais: KIWI_URL/KIWI_USERNAME/KIWI_PASSWORD ou ~/.tcms.conf

USO
    python3 publicar_resultados.py ../resultados-unit.xml
    TCMS_PREFIX="[bruno]" python3 publicar_resultados.py ../resultados-api.xml
"""

import os
import re
import sys

from junitparser import Error, Failure, JUnitXml, Skipped
from tcms_api import TCMS, plugin_helpers

from kiwi_conexao import credenciais_do_ambiente

os.environ.setdefault("TCMS_PRODUCT", "TechStore Carrinho")
os.environ.setdefault("TCMS_PRODUCT_VERSION", "1.0")
os.environ.setdefault("TCMS_BUILD", "1.0.0")

# "CT03" no inicio ou depois de "_", "-", espaco... (em test_CT03 o \b nao serve)
ID_DO_CASO = re.compile(r"(?<![A-Za-z0-9])(CT\d+)", re.IGNORECASE)

# Quanto maior, mais "grave": a execucao agregada fica com o pior status.
PESO = {"WAIVED": 0, "PASSED": 1, "FAILED": 2, "ERROR": 3}


class Backend(plugin_helpers.Backend):
    name = "gqs24-publicar-resultados"
    version = "1.0"

    def configure(self):
        url, usuario, senha = credenciais_do_ambiente()
        self.rpc = TCMS(url=url, username=usuario, password=senha).exec
        # o restante e' identico ao Backend.configure() original
        self.run_id = self.get_run_id()
        self.plan_id = self.get_plan_id(self.run_id)
        self.product_id, _ = self.get_product_id(self.plan_id)
        self.category_id = self.rpc.Category.filter({"product": self.product_id})[0]["id"]
        self.priority_id = self.rpc.Priority.filter({})[0]["id"]
        self.confirmed_id = self.rpc.TestCaseStatus.filter({"name": "CONFIRMED"})[0]["id"]


def status_do_teste(caso_xml):
    for resultado in caso_xml.result:
        if isinstance(resultado, Failure):
            return "FAILED", resultado.message
        if isinstance(resultado, Error):
            return "ERROR", resultado.message
        if isinstance(resultado, Skipped):
            return "WAIVED", resultado.message
    return "PASSED", None


def ler_resultados(arquivos):
    """Agrupa os testes do junit.xml por id do caso: {chave: {...}}."""
    agrupado = {}
    for arquivo in arquivos:
        xml = JUnitXml.fromfile(arquivo)
        suites = xml if xml._tag == "testsuites" else [xml]
        for suite in suites:
            for caso_xml in suite:
                achado = ID_DO_CASO.search(caso_xml.name)
                chave = achado.group(1).upper() if achado else caso_xml.name
                status, mensagem = status_do_teste(caso_xml)
                item = agrupado.setdefault(chave, {"status": "WAIVED", "linhas": []})
                if PESO[status] > PESO[item["status"]]:
                    item["status"] = status
                linha = f"{status}: {caso_xml.classname}::{caso_xml.name}"
                if mensagem:
                    linha += f"\n    {mensagem}"
                item["linhas"].append(linha)
    return agrupado


def localizar_caso(backend, chave):
    if ID_DO_CASO.fullmatch(chave):
        casos = backend.rpc.TestCase.filter(
            {"summary__startswith": f"{chave} ", "category__product": backend.product_id}
        )
        if casos:
            return casos[0]
        print(f"  aviso: nenhum caso '{chave} ...' no Kiwi; criando um novo")
    caso, _ = backend.test_case_get_or_create(chave)
    return caso


def main(arquivos):
    if not arquivos:
        print(f"uso: {sys.argv[0]} resultados.xml [outro.xml ...]")
        sys.exit(1)

    if not os.environ.get("TCMS_PLAN_ID") and not os.environ.get("TCMS_RUN_ID"):
        print("ERRO: exporte TCMS_PLAN_ID com o id do plano (saida do preparar_kiwi.py).")
        sys.exit(1)

    backend = Backend(prefix="[automatizado]", verbose=True)
    backend.configure()
    print(f"TestRun TR-{backend.run_id} (plano TP-{backend.plan_id})")

    contagem = {}
    for chave, item in sorted(ler_resultados(arquivos).items()):
        caso = localizar_caso(backend, chave)
        backend.add_test_case_to_plan(caso["id"], backend.plan_id)
        status_id = backend.get_status_id(item["status"])
        comentario = "Resultado publicado a partir do junit.xml:\n" + "\n".join(item["linhas"])
        for execucao in backend.add_test_case_to_run(caso["id"], backend.run_id):
            backend.update_test_execution(execucao["id"], status_id, comment=comentario)
        contagem[item["status"]] = contagem.get(item["status"], 0) + 1
        print(f"  {item['status']:<7} TC-{caso['id']:<3} {caso['summary']}")

    backend.finish_test_run()
    resumo = ", ".join(f"{k}: {v}" for k, v in sorted(contagem.items()))
    print(f"Rodada TR-{backend.run_id} concluida -> {resumo}")


if __name__ == "__main__":
    main(sys.argv[1:])
