"""
Testes da camada HTTP com o cliente de teste do Flask - gabarito da Etapa 10.

Copie para python/tests/test_app.py. Exercita cada rota sem subir servidor.
"""

import pytest

from biblioteca.app import criar_app
from biblioteca.regras import Biblioteca

TOKEN = {"X-Admin-Token": "token-de-teste"}


@pytest.fixture
def cliente():
    return criar_app(Biblioteca(token_admin="token-de-teste")).test_client()


def test_health_e_livros(cliente):
    assert cliente.get("/api/health").get_json()["status"] == "ok"
    assert len(cliente.get("/api/livros").get_json()["disponiveis"]) == 4


def test_empresta_e_devolve(cliente):
    resposta = cliente.post("/api/emprestimos", json={"titulo": "Clean Code", "matricula": "2026001"})
    assert resposta.status_code == 201
    assert resposta.get_json()["codigo"].startswith("EMP-")
    resposta = cliente.post("/api/devolucoes", json={"titulo": "Clean Code"})
    assert resposta.get_json()["mensagem"] == "Devolucao registrada: Clean Code"


def test_erros_viram_404_e_400(cliente):
    assert cliente.post("/api/emprestimos", json={"titulo": "Nao Existe", "matricula": "1"}).status_code == 404
    assert cliente.post("/api/emprestimos", json={"titulo": "Clean Code", "matricula": ""}).status_code == 400
    assert cliente.get("/api/multa?dias=dez&tipo=aluno").status_code == 400


def test_multa(cliente):
    assert cliente.get("/api/multa?dias=20&tipo=aluno&raro=true").get_json()["multa"] == pytest.approx(90.0)


def test_relatorio_exige_token(cliente):
    assert cliente.get("/api/relatorio").status_code == 401
    assert "RELATORIO DA BIBLIOTECA" in cliente.get("/api/relatorio", headers=TOKEN).get_data(as_text=True)
    assert "indicador;valor" in cliente.get("/api/relatorio?formato=csv", headers=TOKEN).get_data(as_text=True)
