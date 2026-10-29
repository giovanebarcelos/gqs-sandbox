"""Testes da camada HTTP com o cliente de teste do Flask - sem servidor."""

import pytest

from frete.app import app


@pytest.fixture
def cliente():
    app.config["TESTING"] = True
    return app.test_client()


def test_health_responde_ok(cliente):
    resposta = cliente.get("/api/health")
    assert resposta.status_code == 200
    assert resposta.get_json()["status"] == "ok"


def test_lista_as_cinco_regioes(cliente):
    assert len(cliente.get("/api/regioes").get_json()) == 5


def test_calcula_frete(cliente):
    resposta = cliente.post("/api/frete", json={"regiao": "SUL", "pesoKg": 2.0, "valorPedido": 100.00})
    assert resposta.status_code == 200
    assert resposta.get_json()["frete"] == 17.5
    assert resposta.get_json()["prazoDias"] == 3


def test_regiao_invalida_responde_400(cliente):
    resposta = cliente.post("/api/frete", json={"regiao": "LESTE", "pesoKg": 1, "valorPedido": 10})
    assert resposta.status_code == 400
    assert resposta.get_json()["erro"] == "Regiao invalida: LESTE"


def test_json_quebrado_responde_400(cliente):
    resposta = cliente.post("/api/frete", data="{", content_type="application/json")
    assert resposta.status_code == 400
