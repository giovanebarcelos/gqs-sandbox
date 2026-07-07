"""Testes pytest para o Exercício 03 — Conversor de Temperatura.

Foco em `@pytest.mark.parametrize`, incluindo múltiplos parâmetros
combinados (Slide 8), e `pytest.approx` para comparação de ponto
flutuante (Slide 5).
"""
import pytest

from conversor import ConversorTemperatura


@pytest.fixture
def conversor():
    return ConversorTemperatura()


@pytest.mark.parametrize("celsius, fahrenheit_esperado", [
    (0, 32),
    (100, 212),
    (-40, -40),
    (37, 98.6),
    (20, 68),
])
def test_celsius_para_fahrenheit(conversor, celsius, fahrenheit_esperado):
    assert conversor.celsius_para_fahrenheit(celsius) == pytest.approx(fahrenheit_esperado, abs=0.01)


@pytest.mark.parametrize("fahrenheit, celsius_esperado", [
    (32, 0),
    (212, 100),
    (-40, -40),
    (98.6, 37),
])
def test_fahrenheit_para_celsius(conversor, fahrenheit, celsius_esperado):
    assert conversor.fahrenheit_para_celsius(fahrenheit) == pytest.approx(celsius_esperado, abs=0.01)


@pytest.mark.parametrize("celsius, kelvin_esperado", [
    (0, 273.15),
    (-273.15, 0),
    (100, 373.15),
])
def test_celsius_para_kelvin(conversor, celsius, kelvin_esperado):
    assert conversor.celsius_para_kelvin(celsius) == pytest.approx(kelvin_esperado, abs=0.01)


def test_kelvin_negativo_deve_lancar_excecao(conversor):
    with pytest.raises(ValueError):
        conversor.kelvin_para_celsius(-1)


@pytest.mark.parametrize("celsius", [-50, 0, 25])
@pytest.mark.parametrize("ida_e_volta", ["fahrenheit", "kelvin"])
def test_conversao_ida_e_volta_preserva_valor_original(conversor, ida_e_volta, celsius):
    """Demonstra parametrização combinada: cada `celsius` roda para cada
    `ida_e_volta`, gerando 3 x 2 = 6 casos de teste."""
    if ida_e_volta == "fahrenheit":
        convertido = conversor.celsius_para_fahrenheit(celsius)
        de_volta = conversor.fahrenheit_para_celsius(convertido)
    else:
        convertido = conversor.celsius_para_kelvin(celsius)
        de_volta = conversor.kelvin_para_celsius(convertido)

    assert de_volta == pytest.approx(celsius, abs=0.01)
