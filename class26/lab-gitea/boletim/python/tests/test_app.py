"""Testes da camada HTTP com o cliente de teste do Flask."""

import pytest

from boletim.app import app


@pytest.fixture
def cliente(monkeypatch):
    monkeypatch.setenv("TOKEN_ADMIN", "segredo-de-teste")
    app.config["TESTING"] = True
    return app.test_client()


def test_health_responde_ok(cliente):
    resposta = cliente.get("/api/health")
    assert resposta.status_code == 200
    assert resposta.get_json()["status"] == "ok"


def test_calcula_boletim(cliente):
    resposta = cliente.post("/api/boletim", json={"aluno": "Ana", "notas": [5, 6], "frequencia": 80})
    assert resposta.status_code == 200
    assert resposta.get_json()["media"] == 5.5
    assert resposta.get_json()["situacao"] == "EXAME"


def test_nota_invalida_responde_400(cliente):
    resposta = cliente.post("/api/boletim", json={"aluno": "Ana", "notas": [11], "frequencia": 80})
    assert resposta.status_code == 400
    assert resposta.get_json()["erro"] == "Nota fora do intervalo 0 a 10: 11"


def test_json_quebrado_responde_400(cliente):
    resposta = cliente.post("/api/boletim", data="{", content_type="application/json")
    assert resposta.status_code == 400


def test_estatisticas_sem_token_responde_401(cliente):
    assert cliente.get("/api/admin/estatisticas").status_code == 401


def test_estatisticas_com_token_responde_200(cliente):
    resposta = cliente.get("/api/admin/estatisticas", headers={"X-Admin-Token": "segredo-de-teste"})
    assert resposta.status_code == 200
    assert isinstance(resposta.get_json()["boletinsCalculados"], int)
