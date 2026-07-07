"""Testes pytest para o Exercício 04 — Repositório de Produtos.

Foco em fixtures com `yield` para setup/teardown (Slide 7) e nos
diferentes escopos de fixture (`function` vs `module`) descritos no
Slide 6 e detalhados nos novos slides de escopo de fixtures.
"""
import pytest

from repositorio_produtos import RepositorioProdutos

# Lista de módulo usada para provar que o teardown de uma fixture
# `function` roda entre um teste e outro (cada teste recebe um
# repositório novo e vazio, populado igual, e fechado ao final).
_historico_teardown = []


@pytest.fixture
def repositorio():
    """Fixture de escopo `function` (padrão) com setup e teardown via
    `yield`. O código antes do `yield` roda antes do teste; o código
    depois do `yield` roda depois do teste, mesmo se ele falhar."""
    # --- Setup ---
    repo = RepositorioProdutos()
    repo.adicionar("Caneta", 2.50)
    repo.adicionar("Caderno", 15.00)

    yield repo  # --- o teste roda aqui ---

    # --- Teardown ---
    repo.fechar()
    _historico_teardown.append(True)


def test_repositorio_inicia_com_produtos_do_setup(repositorio):
    assert repositorio.total_produtos() == 2
    assert repositorio.buscar("Caneta") == 2.50


def test_adicionar_novo_produto(repositorio):
    repositorio.adicionar("Lápis", 1.20)
    assert repositorio.total_produtos() == 3
    assert repositorio.buscar("Lápis") == 1.20


def test_remover_produto(repositorio):
    repositorio.remover("Caneta")
    assert repositorio.total_produtos() == 1
    assert repositorio.buscar("Caneta") is None


def test_listar_produtos_em_ordem_alfabetica(repositorio):
    assert repositorio.listar() == ["Caderno", "Caneta"]


def test_buscar_produto_inexistente_retorna_none(repositorio):
    assert repositorio.buscar("Monitor") is None


def test_teardown_foi_executado_apos_teste_anterior():
    """Este teste roda depois de vários testes que usam a fixture
    `repositorio`; cada um deles já deve ter disparado o teardown
    (fechar) ao final — prova de que o `yield` sempre executa a parte
    de limpeza, mesmo com múltiplos testes usando a mesma fixture."""
    assert len(_historico_teardown) >= 1


def test_usar_repositorio_apos_fechar_lanca_excecao():
    repo = RepositorioProdutos()
    repo.adicionar("Teste", 1.0)
    repo.fechar()

    with pytest.raises(RuntimeError):
        repo.buscar("Teste")
