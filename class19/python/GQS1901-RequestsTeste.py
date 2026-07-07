#!/usr/bin/env python3
"""
GQS1901 - Testes de API REST com a biblioteca requests.
Exercita o CRUD de usuários da API pública JSONPlaceholder,
validando status code e estrutura do JSON de resposta.

Requisitos: pip install requests
Uso: python3 GQS1901-RequestsTeste.py
"""
import requests

BASE_URL = "https://jsonplaceholder.typicode.com"


def testar_get_lista_usuarios() -> None:
    """GET /users deve retornar 200 e uma lista não vazia de usuários."""
    resposta = requests.get(f"{BASE_URL}/users", timeout=10)
    assert resposta.status_code == 200, f"Esperado 200, recebido {resposta.status_code}"

    usuarios = resposta.json()
    assert isinstance(usuarios, list), "Resposta deveria ser uma lista"
    assert len(usuarios) > 0, "Lista de usuários não deveria estar vazia"
    assert "email" in usuarios[0], "Usuário deveria ter o campo 'email'"

    print(f"[OK] GET /users -> {resposta.status_code} ({len(usuarios)} usuários)")


def testar_get_usuario_por_id(user_id: int = 1) -> None:
    """GET /users/{id} deve retornar o usuário correspondente."""
    resposta = requests.get(f"{BASE_URL}/users/{user_id}", timeout=10)
    assert resposta.status_code == 200

    usuario = resposta.json()
    assert usuario["id"] == user_id
    assert "name" in usuario and "username" in usuario

    print(f"[OK] GET /users/{user_id} -> {resposta.status_code} ({usuario['name']})")


def testar_post_criar_usuario() -> None:
    """POST /users deve criar um recurso e retornar 201 com o id gerado."""
    novo_usuario = {"name": "João Silva", "email": "joao@email.com"}
    resposta = requests.post(f"{BASE_URL}/users", json=novo_usuario, timeout=10)
    assert resposta.status_code == 201, f"Esperado 201, recebido {resposta.status_code}"

    criado = resposta.json()
    assert criado["name"] == novo_usuario["name"]
    assert "id" in criado

    print(f"[OK] POST /users -> {resposta.status_code} (id gerado: {criado['id']})")


def testar_put_atualizar_usuario(user_id: int = 1) -> None:
    """PUT /users/{id} deve atualizar o recurso e retornar 200."""
    atualizacao = {"id": user_id, "name": "João Silva Atualizado"}
    resposta = requests.put(f"{BASE_URL}/users/{user_id}", json=atualizacao, timeout=10)
    assert resposta.status_code == 200

    atualizado = resposta.json()
    assert atualizado["name"] == atualizacao["name"]

    print(f"[OK] PUT /users/{user_id} -> {resposta.status_code} ({atualizado['name']})")


def testar_delete_usuario(user_id: int = 1) -> None:
    """DELETE /users/{id} deve remover o recurso e retornar 200."""
    resposta = requests.delete(f"{BASE_URL}/users/{user_id}", timeout=10)
    assert resposta.status_code == 200

    print(f"[OK] DELETE /users/{user_id} -> {resposta.status_code}")


def testar_get_com_bearer_token_invalido() -> None:
    """
    Demonstra o envio de um cabeçalho Authorization: Bearer <token>.
    JSONPlaceholder não valida o token (é um mock), mas o teste mostra
    como montar a chamada autenticada — equivalente à aba
    "Authorization > Bearer Token" do Postman.
    """
    headers = {"Authorization": "Bearer token-de-exemplo-123"}
    resposta = requests.get(f"{BASE_URL}/users/1", headers=headers, timeout=10)
    assert resposta.status_code == 200

    print(f"[OK] GET /users/1 com Bearer token -> {resposta.status_code}")


def main() -> None:
    print("=" * 60)
    print("  GQS1901 - TESTES DE API REST COM REQUESTS")
    print("=" * 60)

    testes = [
        testar_get_lista_usuarios,
        testar_get_usuario_por_id,
        testar_post_criar_usuario,
        testar_put_atualizar_usuario,
        testar_delete_usuario,
        testar_get_com_bearer_token_invalido,
    ]

    falhas = 0
    for teste in testes:
        try:
            teste()
        except AssertionError as erro:
            falhas += 1
            print(f"[FALHA] {teste.__name__}: {erro}")
        except requests.exceptions.RequestException as erro:
            falhas += 1
            print(f"[ERRO DE CONEXÃO] {teste.__name__}: {erro}")

    print("=" * 60)
    total = len(testes)
    print(f"  Resultado: {total - falhas}/{total} testes OK")
    print("=" * 60)


if __name__ == "__main__":
    main()
