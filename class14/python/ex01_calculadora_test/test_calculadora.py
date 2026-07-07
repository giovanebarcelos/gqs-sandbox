"""Testes pytest para o Exercício 01 — Calculadora.

Demonstra os fundamentos cobrados nos Slides 3, 5 e 6 da Aula 14:
- descoberta automática de testes por convenção (test_*.py, funções test_*)
- `assert` nativo do Python (sem métodos assertEquals/assertTrue)
- fixture simples com `@pytest.fixture`
- `pytest.raises` para verificar exceções
"""
import pytest

from calculadora import Calculadora


@pytest.fixture
def calculadora():
    """Fixture de escopo `function` (padrão): cria uma Calculadora nova a
    cada teste, garantindo isolamento entre os casos."""
    return Calculadora()


def test_somar_deve_retornar_a_soma(calculadora):
    assert calculadora.somar(2, 3) == 5


def test_somar_com_zero(calculadora):
    assert calculadora.somar(0, 5) == 5


def test_subtrair_deve_retornar_a_diferenca(calculadora):
    assert calculadora.subtrair(5, 3) == 2


def test_multiplicar_deve_retornar_o_produto(calculadora):
    assert calculadora.multiplicar(2, 3) == 6


def test_dividir_deve_retornar_o_quociente(calculadora):
    assert calculadora.dividir(5, 2) == pytest.approx(2.5)


def test_dividir_por_zero_deve_lancar_excecao(calculadora):
    with pytest.raises(ZeroDivisionError) as exc_info:
        calculadora.dividir(5, 0)
    assert "Divisão por zero" in str(exc_info.value)


@pytest.mark.parametrize("a, b, esperado", [
    (1, 1, 2),
    (5, 3, 8),
    (0, 0, 0),
    (-1, -1, -2),
    (100, 200, 300),
])
def test_somar_parametrizado(calculadora, a, b, esperado):
    assert calculadora.somar(a, b) == esperado
