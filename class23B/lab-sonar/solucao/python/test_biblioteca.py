"""
Testes unitarios da Biblioteca - VERSAO COMPLETA (gabarito da Etapa 10).

Copie para python/tests/test_biblioteca.py. A tabela de multas cobre todos os
ramos de calcular_multa: e' ela que garante que a refatoracao nao mudou nada.
"""

import re

import pytest

from biblioteca.regras import Biblioteca, LivroNaoEncontrado


def titulo_vindo_da_requisicao(*palavras):
    """Na API o titulo chega no JSON e vira um objeto str NOVO."""
    return " ".join(palavras)


@pytest.fixture
def bib():
    return Biblioteca(token_admin="token-de-teste")


def test_empresta_livro_disponivel(bib):
    codigo = bib.emprestar(titulo_vindo_da_requisicao("Clean", "Code"), "2026001")
    assert re.fullmatch(r"EMP-\d{6}", codigo)
    assert "Clean Code" in bib.emprestados
    assert "Clean Code" not in bib.acervo


def test_recusa_livro_inexistente(bib):
    with pytest.raises(LivroNaoEncontrado):
        bib.emprestar("Livro Que Nao Existe", "2026001")


@pytest.mark.parametrize("titulo, matricula", [("Clean Code", " "), (None, "2026001")])
def test_recusa_emprestimo_incompleto(bib, titulo, matricula):
    with pytest.raises(ValueError):
        bib.emprestar(titulo, matricula)


def test_devolve_livro_emprestado(bib):
    bib.emprestar("Refactoring", "2026001")
    bib.devolver(titulo_vindo_da_requisicao("Refactoring"))
    assert "Refactoring" in bib.acervo
    assert bib.emprestados == []


def test_recusa_devolucao_de_livro_nao_emprestado(bib):
    with pytest.raises(LivroNaoEncontrado):
        bib.devolver("Clean Code")


@pytest.mark.parametrize(
    "dias, tipo, raro, esperado",
    [
        (0, "aluno", False, 0.0),
        (10, "aluno", False, 15.0),
        (20, "aluno", False, 45.0),
        (20, "aluno", True, 90.0),
        (31, "aluno", False, 93.0),
        (31, "aluno", True, 100.0),
        (10, "professor", False, 7.5),
        (31, "professor", False, 55.8),
        (31, "professor", True, 100.0),
        (5, "visitante", False, 15.0),
        (10, "visitante", False, 45.0),
        (10, "outro", False, 15.0),
        (90, "outro", False, 100.0),
    ],
)
def test_tabela_de_multas(bib, dias, tipo, raro, esperado):
    assert bib.calcular_multa(dias, tipo, raro) == pytest.approx(esperado)


def test_ler_dias_converte_numero(bib):
    assert bib.ler_dias("12") == 12


def test_ler_dias_rejeita_texto(bib):
    with pytest.raises(ValueError):
        bib.ler_dias("doze")


def test_token(bib):
    assert bib.token_valido("token-de-teste")
    assert not bib.token_valido("outro")
    assert not Biblioteca().token_valido("")


def test_relatorios(bib):
    bib.emprestar("Clean Code", "2026001")
    texto = bib.relatorio_texto()
    csv = bib.relatorio_csv()
    assert texto.startswith("RELATORIO DA BIBLIOTECA")
    assert "Emprestados: 1" in texto
    assert "Taxa de ocupacao: 25.0%" in texto
    assert "emprestado - Clean Code" in texto
    assert csv.startswith("indicador;valor")
    assert "emprestado;Clean Code" in csv
