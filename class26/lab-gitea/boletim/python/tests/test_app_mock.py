"""
TESTES COM MOCK (unittest.mock): a rota HTTP e' testada SOZINHA, com a funcao
de regra de negocio substituida por um dublê. Nao importa se calcular_boletim
esta certa ou errada - aqui so' se verifica a conversa entre as camadas: o que
a rota entrega a funcao e o que faz com a resposta ou com o erro.
"""

from decimal import Decimal
from unittest.mock import patch

import pytest

from boletim.app import app


@pytest.fixture
def cliente():
    app.config["TESTING"] = True
    return app.test_client()


PEDIDO = {"aluno": "Ana", "notas": [7, 9], "frequencia": 90}


def test_entrega_os_dados_do_json_a_funcao_e_devolve_o_que_ela_retornou(cliente):
    resultado_falso = {
        "aluno": "Ana", "media": Decimal("1.0"), "situacao": "REPROVADO",
        "maiorNota": 9.0, "menorNota": 7.0, "resumo": "resposta do mock",
    }
    # STUB: a funcao "responde" um valor combinado, sem calcular nada.
    with patch("boletim.app.calcular_boletim", return_value=resultado_falso) as mock:
        resposta = cliente.post("/api/boletim", json=PEDIDO)

    assert resposta.status_code == 200
    assert resposta.get_json()["resumo"] == "resposta do mock"
    assert resposta.get_json()["media"] == 1.0
    # VERIFY: a rota chamou a funcao uma vez, com estes argumentos.
    mock.assert_called_once_with("Ana", [7, 9], 90)


def test_erro_da_funcao_vira_http_400_com_a_mensagem_dela(cliente):
    with patch("boletim.app.calcular_boletim", side_effect=ValueError("Informe de 1 a 4 notas")) as mock:
        resposta = cliente.post("/api/boletim", json=PEDIDO)

    assert resposta.status_code == 400
    assert resposta.get_json() == {"erro": "Informe de 1 a 4 notas"}
    mock.assert_called_once()


def test_json_invalido_nem_chega_na_funcao(cliente):
    with patch("boletim.app.calcular_boletim") as mock:
        resposta = cliente.post("/api/boletim", data="{", content_type="application/json")

    assert resposta.status_code == 400
    mock.assert_not_called()


def test_health_nao_usa_a_funcao(cliente):
    with patch("boletim.app.calcular_boletim") as mock:
        assert cliente.get("/api/health").status_code == 200

    mock.assert_not_called()


def test_token_admin_e_lido_do_ambiente_com_monkeypatch(cliente, monkeypatch):
    # monkeypatch: outro tipo de dublê - troca uma variavel de ambiente so' neste teste.
    monkeypatch.setenv("TOKEN_ADMIN", "outro-segredo")
    assert cliente.get("/api/admin/estatisticas", headers={"X-Admin-Token": "outro-segredo"}).status_code == 200
    assert cliente.get("/api/admin/estatisticas", headers={"X-Admin-Token": "segredo-de-teste"}).status_code == 401
