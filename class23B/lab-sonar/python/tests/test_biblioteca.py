"""
Testes unitarios da Biblioteca (pytest) - Aula 23B.

Suite propositalmente INCOMPLETA, espelhando a versao Java: cobre emprestimo,
devolucao e um caminho da multa. O SonarQube mostra o resto como nao coberto.

    pytest --cov --cov-report=xml      # gera coverage.xml para o Sonar (veja .coveragerc)

Na versao 1.0.0, o teste de emprestimo FALHA por causa do `is`.
"""

import re

import pytest

from biblioteca.regras import Biblioteca, LivroNaoEncontrado


def titulo_vindo_da_requisicao(*palavras):
    """Na API o titulo chega no JSON e vira um objeto str NOVO."""
    return " ".join(palavras)


def test_empresta_livro_disponivel():
    bib = Biblioteca()
    codigo = bib.emprestar(titulo_vindo_da_requisicao("Clean", "Code"), "2026001")
    assert re.fullmatch(r"EMP-\d{6}", codigo)
    assert "Clean Code" in bib.emprestados


def test_recusa_livro_inexistente():
    with pytest.raises(LivroNaoEncontrado):
        Biblioteca().emprestar("Livro Que Nao Existe", "2026001")


def test_recusa_devolucao_de_livro_nao_emprestado():
    with pytest.raises(LivroNaoEncontrado):
        Biblioteca().devolver("Clean Code")


def test_multa_aluno_atraso_curto():
    assert Biblioteca().calcular_multa(10, "aluno", False) == pytest.approx(15.0)
