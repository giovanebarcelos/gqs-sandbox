#!/usr/bin/env python3
"""
GQS1301-TestesUnitariosPython.py
=================================
Exemplos de testes unitários com unittest (equivalente Python ao JUnit).
Aula 13 — JUnit e Testes Unitários em Java | Garantia da Qualidade de Software.

Uso:
    python3 -m pytest GQS1301-TestesUnitariosPython.py -v
    python3 GQS1301-TestesUnitariosPython.py              # modo direto
"""

import sys
import unittest
from typing import List, Optional


# ──────────────────────────────────────────────
# 1. Classe Calculadora (exemplo básico)
# ──────────────────────────────────────────────

class Calculadora:
    """Calculadora simples para demonstração de testes unitários."""

    def somar(self, a: float, b: float) -> float:
        return a + b

    def subtrair(self, a: float, b: float) -> float:
        return a - b

    def multiplicar(self, a: float, b: float) -> float:
        return a * b

    def dividir(self, a: float, b: float) -> float:
        if b == 0:
            raise ValueError("Divisão por zero não permitida")
        return a / b


# ──────────────────────────────────────────────
# 2. Validador de String
# ──────────────────────────────────────────────

class ValidadorString:
    """Validador de strings com múltiplas regras."""

    @staticmethod
    def is_email_valido(email: str) -> bool:
        if not email or "@" not in email:
            return False
        partes = email.split("@")
        if len(partes) != 2:
            return False
        nome, dominio = partes
        return len(nome) >= 1 and "." in dominio

    @staticmethod
    def is_cpf_valido(cpf: str) -> bool:
        """Valida CPF (apenas formato, sem dígitos verificadores)."""
        import re
        cpf = re.sub(r"\D", "", cpf)
        return len(cpf) == 11 and len(set(cpf)) > 1


# ──────────────────────────────────────────────
# 3. Contador com Estado
# ──────────────────────────────────────────────

class Contador:
    """Contador que mantém estado interno."""

    def __init__(self, valor_inicial: int = 0):
        self._valor = valor_inicial

    def incrementar(self, passo: int = 1) -> int:
        self._valor += passo
        return self._valor

    def decrementar(self, passo: int = 1) -> int:
        self._valor -= passo
        return self._valor

    def zerar(self) -> None:
        self._valor = 0

    @property
    def valor(self) -> int:
        return self._valor


# ──────────────────────────────────────────────
# 4. Processador de Lista
# ──────────────────────────────────────────────

class ProcessadorLista:
    """Processa listas com filtros e transformações."""

    @staticmethod
    def filtrar_pares(numeros: List[int]) -> List[int]:
        return [n for n in numeros if n % 2 == 0]

    @staticmethod
    def ordenar_decrescente(itens: List[int]) -> List[int]:
        return sorted(itens, reverse=True)

    @staticmethod
    def media(numeros: List[float]) -> float:
        if not numeros:
            raise ValueError("Lista vazia")
        return sum(numeros) / len(numeros)


# ──────────────────────────────────────────────
# 5. Conversor de Temperatura
# ──────────────────────────────────────────────

class ConversorTemperatura:
    """Conversor entre escalas de temperatura."""

    @staticmethod
    def celsius_para_fahrenheit(c: float) -> float:
        return (c * 9 / 5) + 32

    @staticmethod
    def fahrenheit_para_celsius(f: float) -> float:
        return (f - 32) * 5 / 9

    @staticmethod
    def celsius_para_kelvin(c: float) -> float:
        if c < -273.15:
            raise ValueError("Temperatura abaixo do zero absoluto")
        return c + 273.15


# ══════════════════════════════════════════════
# TESTES UNITÁRIOS
# ══════════════════════════════════════════════

class TestCalculadora(unittest.TestCase):
    """Testes para a classe Calculadora."""

    def setUp(self):
        self.calc = Calculadora()

    def test_somar_positivos(self):
        self.assertEqual(self.calc.somar(2, 3), 5)

    def test_somar_negativos(self):
        self.assertEqual(self.calc.somar(-1, -2), -3)

    def test_somar_zero(self):
        self.assertEqual(self.calc.somar(5, 0), 5)

    def test_subtrair(self):
        self.assertEqual(self.calc.subtrair(10, 4), 6)

    def test_multiplicar(self):
        self.assertEqual(self.calc.multiplicar(3, 4), 12)

    def test_dividir_exata(self):
        self.assertEqual(self.calc.dividir(10, 2), 5)

    def test_dividir_por_zero(self):
        with self.assertRaises(ValueError):
            self.calc.dividir(5, 0)


class TestValidadorString(unittest.TestCase):
    """Testes para o validador de strings."""

    def test_email_valido(self):
        self.assertTrue(ValidadorString.is_email_valido("user@example.com"))

    def test_email_sem_arroba(self):
        self.assertFalse(ValidadorString.is_email_valido("userexample.com"))

    def test_email_vazio(self):
        self.assertFalse(ValidadorString.is_email_valido(""))

    def test_cpf_valido(self):
        self.assertTrue(ValidadorString.is_cpf_valido("12345678901"))


class TestContador(unittest.TestCase):
    """Testes para o contador com estado."""

    def setUp(self):
        self.contador = Contador()

    def test_valor_inicial(self):
        self.assertEqual(self.contador.valor, 0)

    def test_incrementar(self):
        self.assertEqual(self.contador.incrementar(), 1)

    def test_incrementar_5(self):
        self.contador.incrementar(5)
        self.assertEqual(self.contador.valor, 5)

    def test_decrementar(self):
        self.contador.incrementar(10)
        self.contador.decrementar(3)
        self.assertEqual(self.contador.valor, 7)

    def test_zerar(self):
        self.contador.incrementar(100)
        self.contador.zerar()
        self.assertEqual(self.contador.valor, 0)

    def test_multiplas_operacoes(self):
        self.contador.incrementar(5)
        self.contador.incrementar(3)
        self.contador.decrementar(2)
        self.assertEqual(self.contador.valor, 6)


class TestProcessadorLista(unittest.TestCase):
    """Testes para o processador de lista."""

    def setUp(self):
        self.processador = ProcessadorLista()

    def test_filtrar_pares(self):
        self.assertEqual(self.processador.filtrar_pares([1, 2, 3, 4, 5, 6]), [2, 4, 6])

    def test_filtrar_pares_vazio(self):
        self.assertEqual(self.processador.filtrar_pares([1, 3, 5]), [])

    def test_ordenar_decrescente(self):
        self.assertEqual(self.processador.ordenar_decrescente([3, 1, 4, 1, 5]), [5, 4, 3, 1, 1])

    def test_media(self):
        self.assertAlmostEqual(self.processador.media([10, 20, 30]), 20.0)

    def test_media_lista_vazia(self):
        with self.assertRaises(ValueError):
            self.processador.media([])


class TestConversorTemperatura(unittest.TestCase):
    """Testes para o conversor de temperatura."""

    def test_celsius_fahrenheit(self):
        self.assertAlmostEqual(ConversorTemperatura.celsius_para_fahrenheit(0), 32.0)

    def test_fahrenheit_celsius(self):
        self.assertAlmostEqual(ConversorTemperatura.fahrenheit_para_celsius(32), 0.0)

    def test_celsius_kelvin(self):
        self.assertAlmostEqual(ConversorTemperatura.celsius_para_kelvin(0), 273.15)

    def test_zero_absoluto(self):
        with self.assertRaises(ValueError):
            ConversorTemperatura.celsius_para_kelvin(-300)

    def test_ponto_ebulicao(self):
        self.assertAlmostEqual(ConversorTemperatura.celsius_para_fahrenheit(100), 212.0)


# ══════════════════════════════════════════════
# PONTO DE ENTRADA
# ══════════════════════════════════════════════

if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "--validate":
        print("=" * 60)
        print("  GQS1301 - Testes Unitários em Python")
        print("  Garantia da Qualidade de Software — Aula 13")
        print("=" * 60)

        # Suite de testes
        suite = unittest.TestLoader().loadTestsFromModule(sys.modules[__name__])
        runner = unittest.TextTestRunner(verbosity=2)
        result = runner.run(suite)

        print(f"\nResultados: {result.testsRun} testes, "
              f"{len(result.failures)} falhas, "
              f"{len(result.errors)} erros")
        sys.exit(0 if result.wasSuccessful() else 1)
    else:
        unittest.main(verbosity=2)
