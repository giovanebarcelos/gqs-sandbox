"""Exercício 03 — Conversor de Temperatura.

Equivalente em Python do exercício `ex05_conversor_de_temperatura` da
Aula 13 (JUnit/Java), reaproveitado aqui para comparar
`@pytest.mark.parametrize` com `@ParameterizedTest` + `@CsvSource`.
"""

ZERO_ABSOLUTO_KELVIN = 0.0


class ConversorTemperatura:
    """Conversões entre Celsius, Fahrenheit e Kelvin."""

    def celsius_para_fahrenheit(self, celsius: float) -> float:
        return (celsius * 9.0 / 5.0) + 32

    def fahrenheit_para_celsius(self, fahrenheit: float) -> float:
        return (fahrenheit - 32) * 5.0 / 9.0

    def celsius_para_kelvin(self, celsius: float) -> float:
        return celsius + 273.15

    def kelvin_para_celsius(self, kelvin: float) -> float:
        if kelvin < ZERO_ABSOLUTO_KELVIN:
            raise ValueError("Kelvin não pode ser negativo (abaixo do zero absoluto)")
        return kelvin - 273.15
