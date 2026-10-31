"""
TESTE DE INTEGRACAO: sobe a aplicacao INTEIRA (Flask servido pelo Werkzeug numa
porta livre, rota + regras reais, sem nenhum mock) e conversa com ela por HTTP
de verdade, usando so' a biblioteca padrao. O marcador "integracao" permite
rodar so' estes (-m integracao) ou so' os outros (-m "not integracao").
"""

import json
import threading
import urllib.error
import urllib.request

import pytest
from werkzeug.serving import make_server

from boletim.app import app

pytestmark = pytest.mark.integracao


@pytest.fixture(scope="module")
def url_base():
    servidor = make_server("127.0.0.1", 0, app)  # porta 0 = o sistema escolhe uma livre
    thread = threading.Thread(target=servidor.serve_forever, daemon=True)
    thread.start()
    yield f"http://127.0.0.1:{servidor.server_port}"
    servidor.shutdown()
    thread.join()


@pytest.fixture(autouse=True)
def token_admin(monkeypatch):
    monkeypatch.setenv("TOKEN_ADMIN", "segredo-it")


def requisicao(url, corpo=None, cabecalhos=None):
    """Devolve (status, json) sem lancar excecao para 4xx."""
    dados = json.dumps(corpo).encode() if corpo is not None else None
    pedido = urllib.request.Request(url, data=dados, headers={"Content-Type": "application/json", **(cabecalhos or {})})
    try:
        with urllib.request.urlopen(pedido, timeout=5) as resposta:
            return resposta.status, json.load(resposta)
    except urllib.error.HTTPError as erro:
        return erro.code, json.load(erro)


def test_health_pela_rede(url_base):
    status, corpo = requisicao(f"{url_base}/api/health")
    assert status == 200
    assert corpo["status"] == "ok"


def test_boletim_percorre_rota_e_regras_de_verdade(url_base):
    status, corpo = requisicao(f"{url_base}/api/boletim", {"aluno": "Ana", "notas": [7, 9, 8], "frequencia": 90})
    assert status == 200
    assert corpo["situacao"] == "APROVADO"
    assert corpo["media"] == 8.0


def test_entrada_invalida_volta_como_400_com_a_mensagem_da_regra(url_base):
    status, corpo = requisicao(f"{url_base}/api/boletim", {"aluno": "Davi", "notas": [11], "frequencia": 80})
    assert status == 400
    assert corpo["erro"] == "Nota fora do intervalo 0 a 10: 11"


def test_estatisticas_contam_so_os_boletins_calculados_com_sucesso(url_base):
    cabecalho = {"X-Admin-Token": "segredo-it"}
    antes = requisicao(f"{url_base}/api/admin/estatisticas", cabecalhos=cabecalho)[1]["boletinsCalculados"]

    requisicao(f"{url_base}/api/boletim", {"aluno": "Bia", "notas": [6], "frequencia": 100})  # conta
    requisicao(f"{url_base}/api/boletim", {"aluno": "Bia", "notas": [60], "frequencia": 100})  # invalido: nao conta

    depois = requisicao(f"{url_base}/api/admin/estatisticas", cabecalhos=cabecalho)[1]["boletinsCalculados"]
    assert depois == antes + 1


def test_area_administrativa_recusa_token_errado_ou_ausente(url_base):
    assert requisicao(f"{url_base}/api/admin/estatisticas")[0] == 401
    assert requisicao(f"{url_base}/api/admin/estatisticas", cabecalhos={"X-Admin-Token": "errado"})[0] == 401
