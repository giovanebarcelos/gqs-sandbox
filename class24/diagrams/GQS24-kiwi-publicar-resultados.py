#!/usr/bin/env python3
r"""
GQS24-kiwi-publicar-resultados.py
Garantia da Qualidade de Software - Aula 24
Publica no Kiwi TCMS o resultado de uma suite automatizada (pytest, JUnit, ...).

O trabalho pesado e' feito pelo plugin OFICIAL do projeto,
"kiwitcms-junit.xml-plugin": ele le um arquivo junit.xml, cria/reaproveita os
TestCase correspondentes, cria um TestPlan e um TestRun e grava uma
TestExecution por teste com o status traduzido:

    teste passou   -> PASSED
    assert falhou  -> FAILED
    excecao/erro   -> ERROR
    skip           -> WAIVED

Este arquivo e' apenas um invólucro de UMA linha util: desligar a verificacao
do certificado TLS, porque o container do Kiwi TCMS usa um certificado
autoassinado cujo CN e' "buildkitsandbox" - ele nao casa com "localhost" nem
com qualquer nome que voce use no laboratorio. Em um servidor de verdade,
com certificado valido, o comando oficial funciona direto:

    tcms-junit.xml-plugin resultados.xml

-------------------------------------------------------------------------------
USO
-------------------------------------------------------------------------------
    pip install pytest tcms-api "kiwitcms-junit.xml-plugin"

    # 1. rode a suite gerando o relatorio no formato JUnit
    pytest -q --junitxml=resultados.xml

    # 2. publique
    export TCMS_PRODUCT="TechStore"
    export TCMS_PRODUCT_VERSION="1.5.0"
    export TCMS_BUILD="v1.5.0-RC1"
    python3 GQS24-kiwi-publicar-resultados.py resultados.xml

-------------------------------------------------------------------------------
VARIAVEIS DE AMBIENTE LIDAS PELO PLUGIN OFICIAL
-------------------------------------------------------------------------------
    TCMS_PRODUCT           nome do Product no Kiwi (criado se nao existir)
    TCMS_PRODUCT_VERSION   valor da Version
    TCMS_BUILD             nome do Build
    TCMS_PLAN_ID           publica dentro de um TestPlan ja existente (opcional)
    TCMS_RUN_ID            publica dentro de um TestRun ja existente (opcional)
    TCMS_PREFIX            prefixo do TestPlan/TestRun criados

Em um pipeline de CI o plugin tambem aceita, como alternativa, as variaveis
que o proprio CI ja exporta: JOB_NAME/GIT_COMMIT/BUILD_NUMBER (Jenkins) e
TRAVIS_REPO_SLUG/TRAVIS_PULL_REQUEST_SHA/TRAVIS_BUILD_NUMBER (Travis).

Credenciais NUNCA vao em variavel de ambiente de script versionado nem no
codigo: o plugin le ~/.tcms.conf (modo 600):

    [tcms]
    url = https://localhost:8443/xml-rpc/
    username = admin
    password = sua-senha
-------------------------------------------------------------------------------
"""

import ssl
import sys
from datetime import datetime

# --------------------------------------------------------------------------- #
# APENAS EM LABORATORIO: aceita o certificado autoassinado do container.
# Precisa vir ANTES do import do plugin, que abre a conexao ao ser executado.
# --------------------------------------------------------------------------- #
ssl._create_default_https_context = ssl._create_unverified_context

from tcms_junit_plugin import Plugin  # noqa: E402  (import apos o patch de ssl)


class PluginISO8601(Plugin):
    """Plugin oficial + suporte ao carimbo de tempo que o pytest gera hoje.

    O plugin 15.0 so' entende os formatos "%Y-%m-%dT%H:%M:%S.%f",
    "%Y-%m-%dT%H:%M:%S" e "%Y-%m-%d %H:%M:%S". O pytest 8.x escreve o atributo
    timestamp do <testsuite> em ISO-8601 COM fuso ("2026-09-04T08:37:48-03:00"),
    e o plugin aborta com "Unknown timestamp format".

    A propria documentacao do plugin diz que estes metodos podem ser
    sobrescritos; e' o que fazemos aqui, delegando ao parser ISO-8601 da
    biblioteca padrao e caindo no comportamento original se ele nao der conta.
    """

    def parse_timestamp(self, value):
        try:
            return datetime.fromisoformat(value)
        except ValueError:
            return super().parse_timestamp(value)


def main():
    if len(sys.argv) < 2:
        print(f"uso: {sys.argv[0]} resultados.xml [outro.xml ...]")
        sys.exit(1)

    # summary_template define como o nome do TestCase e' montado a partir do
    # junit.xml. O padrao e' "${classname}.${name}"; aqui usamos so o nome do
    # teste, que fica legivel na interface do Kiwi.
    plugin = PluginISO8601(verbose=True, summary_template="${name}")
    plugin.parse(sys.argv[1:])


if __name__ == "__main__":
    main()
