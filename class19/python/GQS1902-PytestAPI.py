#!/usr/bin/env python3
"""
GQS1902 - Testes de API REST estruturados com pytest.
Mesmos cenários de GQS1901-RequestsTeste.py, reescritos como
funções de teste pytest (fixtures + asserts nativos).

Requisitos: pip install requests pytest
Uso: pytest GQS1902-PytestAPI.py -v
"""
import pytest
import requests

BASE_URL = "https://jsonplaceholder.typicode.com"


@pytest.fixture(scope="module")
def sessao() -> requests.Session:
    """Reutiliza uma única sessão HTTP entre os testes do módulo."""
    with requests.Session() as sessao_http:
        yield sessao_http


def test_get_lista_usuarios(sessao: requests.Session) -> None:
    resposta = sessao.get(f"{BASE_URL}/users", timeout=10)
    assert resposta.status_code == 200

    usuarios = resposta.json()
    assert isinstance(usuarios, list)
    assert len(usuarios) > 0
    assert "email" in usuarios[0]


def test_get_usuario_por_id(sessao: requests.Session) -> None:
    resposta = sessao.get(f"{BASE_URL}/users/1", timeout=10)
    assert resposta.status_code == 200

    usuario = resposta.json()
    assert usuario["id"] == 1
    assert "name" in usuario
    assert "username" in usuario


@pytest.mark.parametrize("user_id", [1, 2, 5])
def test_get_usuario_existe(sessao: requests.Session, user_id: int) -> None:
    """Testa múltiplos IDs de usuário via parametrize."""
    resposta = sessao.get(f"{BASE_URL}/users/{user_id}", timeout=10)
    assert resposta.status_code == 200
    assert resposta.json()["id"] == user_id


def test_post_criar_usuario(sessao: requests.Session) -> None:
    novo_usuario = {"name": "João Silva", "email": "joao@email.com"}
    resposta = sessao.post(f"{BASE_URL}/users", json=novo_usuario, timeout=10)
    assert resposta.status_code == 201

    criado = resposta.json()
    assert criado["name"] == novo_usuario["name"]
    assert "id" in criado


def test_put_atualizar_usuario(sessao: requests.Session) -> None:
    atualizacao = {"id": 1, "name": "João Silva Atualizado"}
    resposta = sessao.put(f"{BASE_URL}/users/1", json=atualizacao, timeout=10)
    assert resposta.status_code == 200
    assert resposta.json()["name"] == atualizacao["name"]


def test_delete_usuario(sessao: requests.Session) -> None:
    resposta = sessao.delete(f"{BASE_URL}/users/1", timeout=10)
    assert resposta.status_code == 200


def test_get_com_bearer_token(sessao: requests.Session) -> None:
    """
    Envia o cabeçalho Authorization: Bearer <token>, equivalente
    à aba Authorization > Bearer Token do Postman.
    """
    headers = {"Authorization": "Bearer token-de-exemplo-123"}
    resposta = sessao.get(f"{BASE_URL}/users/1", headers=headers, timeout=10)
    assert resposta.status_code == 200


def test_get_usuario_inexistente_retorna_404(sessao: requests.Session) -> None:
    resposta = sessao.get(f"{BASE_URL}/users/99999", timeout=10)
    assert resposta.status_code == 404


if __name__ == "__main__":
    raise SystemExit(pytest.main([__file__, "-v"]))
