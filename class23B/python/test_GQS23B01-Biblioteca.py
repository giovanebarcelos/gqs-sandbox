"""Testes pytest PROPOSITALMENTE INCOMPLETOS da Aula 23B (lado Python).

Espelham a suite Java: todos passam, mas deixam `buscar_por_titulo`,
`relatorio_csv`, `registrar_historico` e a maior parte de `calcular_multa`
sem cobertura - o suficiente para o Quality Gate do curso reprovar.

Observacao sobre o import: o modulo usa hifen no nome
(`GQS23B01-Biblioteca.py`), seguindo a convencao do repositorio da
disciplina. Como hifen nao e valido em `import`, ele e carregado via
`importlib` - mesmo padrao do `test_GQS2301-CoberturaCode.py` da Aula 23.
"""
import importlib.util
import pathlib

import pytest

_MODULE_PATH = pathlib.Path(__file__).parent / "GQS23B01-Biblioteca.py"
_spec = importlib.util.spec_from_file_location("GQS23B01_Biblioteca", _MODULE_PATH)
_modulo = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(_modulo)

Biblioteca = _modulo.Biblioteca


@pytest.fixture
def biblioteca():
    return Biblioteca()


def test_acervo_inicial_tem_tres_livros(biblioteca):
    assert biblioteca.total_disponivel() == 3


def test_emprestar_remove_do_acervo(biblioteca):
    assert biblioteca.emprestar("Clean Code", "2026001") == "Emprestimo registrado: Clean Code"
    assert biblioteca.total_disponivel() == 2


def test_emprestar_titulo_inexistente(biblioteca):
    assert biblioteca.emprestar("Livro Fantasma", "2026001") == "Livro nao encontrado"


def test_emprestar_sem_matricula_levanta_erro(biblioteca):
    with pytest.raises(ValueError):
        biblioteca.emprestar("Clean Code", None)


def test_devolver_recoloca_no_acervo(biblioteca):
    biblioteca.emprestar("Clean Code", "2026001")
    assert biblioteca.devolver("Clean Code") == "Devolucao registrada: Clean Code"
    assert biblioteca.total_disponivel() == 3


def test_devolver_livro_nao_emprestado(biblioteca):
    assert biblioteca.devolver("Clean Code") == "Livro nao encontrado"


def test_multa_aluno_dez_dias(biblioteca):
    assert biblioteca.calcular_multa(10, "aluno", False) == 15.0


def test_sem_atraso_nao_ha_multa(biblioteca):
    assert biblioteca.calcular_multa(0, "aluno", False) == 0.0


def test_relatorio_texto_lista_titulos(biblioteca):
    relatorio = biblioteca.relatorio_texto()
    assert "RELATORIO DO ACERVO" in relatorio
    assert "Clean Code" in relatorio
