"""Exercício 01 — Calculadora básica.

Equivalente em Python do exercício `ex01_calculadora_basica` da Aula 13
(JUnit/Java), usado para comparar a mesma bateria de operações testada
com pytest em vez de JUnit 5.
"""


class Calculadora:
    """Operações aritméticas básicas."""

    def somar(self, a, b):
        return a + b

    def subtrair(self, a, b):
        return a - b

    def multiplicar(self, a, b):
        return a * b

    def dividir(self, a, b):
        if b == 0:
            raise ZeroDivisionError("Divisão por zero não é permitida")
        return a / b
