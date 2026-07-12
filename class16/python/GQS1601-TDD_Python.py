#!/usr/bin/env python3
"""
GQS1601-TDD_Python.py
=======================
Exemplo de TDD (Test-Driven Development) em Python com pytest.
Aula 16 — TDD | Garantia da Qualidade de Software.

Uso:
    python3 -m pytest GQS1601-TDD_Python.py -v
    python3 GQS1601-TDD_Python.py --validate
"""

import sys
import unittest
from typing import List


# ══════════════════════════════════════════════
# CICLO TDD: RED → GREEN → REFACTOR
# ══════════════════════════════════════════════

# ──────────────────────────────────────────────
# FASE 1: RED — Escrever o teste que falha
# ──────────────────────────────────────────────

class TestCalculadoraTDD(unittest.TestCase):
    """
    Testes escritos ANTES da implementação (Red).
    """

    # ---- Teste 1: soma ----
    def test_somar_dois_positivos(self):
        self.assertEqual(CalculadoraTDD.somar(2, 3), 5)

    def test_somar_positivo_com_zero(self):
        self.assertEqual(CalculadoraTDD.somar(5, 0), 5)

    def test_somar_negativos(self):
        self.assertEqual(CalculadoraTDD.somar(-1, -1), -2)

    # ---- Teste 2: subtração ----
    def test_subtrair(self):
        self.assertEqual(CalculadoraTDD.subtrair(10, 4), 6)

    def test_subtrair_resultado_negativo(self):
        self.assertEqual(CalculadoraTDD.subtrair(3, 10), -7)

    # ---- Teste 3: multiplicação ----
    def test_multiplicar(self):
        self.assertEqual(CalculadoraTDD.multiplicar(3, 4), 12)

    def test_multiplicar_por_zero(self):
        self.assertEqual(CalculadoraTDD.multiplicar(5, 0), 0)

    # ---- Teste 4: divisão ----
    def test_dividir(self):
        self.assertAlmostEqual(CalculadoraTDD.dividir(10, 3), 3.333, places=2)

    def test_dividir_por_zero(self):
        with self.assertRaises(ValueError):
            CalculadoraTDD.dividir(5, 0)


# ──────────────────────────────────────────────
# FASE 2: GREEN — Implementação mínima para passar
# ──────────────────────────────────────────────

class CalculadoraTDD:
    """
    Implementação mínima que faz os testes passarem (Green).
    Depois pode ser refatorada (Refactor) mantendo os testes verdes.
    """

    @staticmethod
    def somar(a: float, b: float) -> float:
        return a + b

    @staticmethod
    def subtrair(a: float, b: float) -> float:
        return a - b

    @staticmethod
    def multiplicar(a: float, b: float) -> float:
        return a * b

    @staticmethod
    def dividir(a: float, b: float) -> float:
        if b == 0:
            raise ValueError("Divisão por zero")
        return a / b


# ──────────────────────────────────────────────
# SEGUNDO CICLO TDD: Validador de Senha
# ──────────────────────────────────────────────

class TestValidadorSenhaTDD(unittest.TestCase):
    """Testes TDD para um validador de senha."""

    # Requisito 1: mínimo 8 caracteres (senha atende também aos requisitos 2 e 3)
    def test_senha_deve_ter_8_caracteres(self):
        self.assertTrue(ValidadorSenha.eh_valida("Abc12345"))

    def test_senha_curta_rejeitada(self):
        self.assertFalse(ValidadorSenha.eh_valida("abc1234"))

    # Requisito 2: pelo menos 1 número
    def test_senha_sem_numero_rejeitada(self):
        self.assertFalse(ValidadorSenha.eh_valida("abcdefgh"))

    # Requisito 3: pelo menos 1 letra maiúscula
    def test_senha_sem_maiuscula_rejeitada(self):
        self.assertFalse(ValidadorSenha.eh_valida("abcdef12"))

    # Requisito 4: senha válida completa
    def test_senha_valida_completa(self):
        self.assertTrue(ValidadorSenha.eh_valida("Abcdef12"))


class ValidadorSenha:
    """
    Implementado seguindo TDD: cada requisito foi adicionado
    um de cada vez, com teste primeiro (Red), depois código (Green).
    """

    @staticmethod
    def eh_valida(senha: str) -> bool:
        if len(senha) < 8:
            return False
        if not any(c.isdigit() for c in senha):
            return False
        if not any(c.isupper() for c in senha):
            return False
        return True


# ──────────────────────────────────────────────
# TERCEIRO CICLO TDD: Gerenciador de Tarefas
# ──────────────────────────────────────────────

class TestGerenciadorTarefas(unittest.TestCase):
    """TDD para um gerenciador de tarefas simples."""

    def test_criar_tarefa(self):
        gerenciador = GerenciadorTarefas()
        tarefa = gerenciador.criar("Estudar TDD", "Alta")
        self.assertEqual(tarefa.titulo, "Estudar TDD")
        self.assertEqual(tarefa.prioridade, "Alta")
        self.assertFalse(tarefa.concluida)

    def test_listar_tarefas(self):
        gerenciador = GerenciadorTarefas()
        gerenciador.criar("Tarefa 1", "Baixa")
        gerenciador.criar("Tarefa 2", "Alta")
        self.assertEqual(len(gerenciador.listar()), 2)

    def test_concluir_tarefa(self):
        gerenciador = GerenciadorTarefas()
        tarefa = gerenciador.criar("Ler livro", "Média")
        gerenciador.concluir(tarefa.id)
        self.assertTrue(tarefa.concluida)

    def test_listar_pendentes(self):
        gerenciador = GerenciadorTarefas()
        gerenciador.criar("Fazer compras", "Baixa")
        t2 = gerenciador.criar("Estudar", "Alta")
        gerenciador.concluir(t2.id)

        pendentes = gerenciador.listar_pendentes()
        self.assertEqual(len(pendentes), 1)
        self.assertEqual(pendentes[0].titulo, "Fazer compras")


class Tarefa:
    def __init__(self, id: int, titulo: str, prioridade: str):
        self.id = id
        self.titulo = titulo
        self.prioridade = prioridade
        self.concluida = False


class GerenciadorTarefas:
    """Implementação TDD do gerenciador de tarefas."""

    def __init__(self):
        self._tarefas: List[Tarefa] = []
        self._proximo_id = 1

    def criar(self, titulo: str, prioridade: str = "Média") -> Tarefa:
        tarefa = Tarefa(self._proximo_id, titulo, prioridade)
        self._proximo_id += 1
        self._tarefas.append(tarefa)
        return tarefa

    def listar(self) -> List[Tarefa]:
        return list(self._tarefas)

    def concluir(self, tarefa_id: int) -> None:
        for t in self._tarefas:
            if t.id == tarefa_id:
                t.concluida = True
                return
        raise ValueError(f"Tarefa {tarefa_id} não encontrada")

    def listar_pendentes(self) -> List[Tarefa]:
        return [t for t in self._tarefas if not t.concluida]


# ══════════════════════════════════════════════
# PONTO DE ENTRADA
# ══════════════════════════════════════════════

if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "--validate":
        print("=" * 60)
        print("  GQS1601 - TDD em Python")
        print("  Garantia da Qualidade de Software — Aula 16")
        print("=" * 60)

        suite = unittest.TestLoader().loadTestsFromModule(sys.modules[__name__])
        runner = unittest.TextTestRunner(verbosity=2)
        result = runner.run(suite)

        print(f"\nResultados: {result.testsRun} testes, "
              f"{len(result.failures)} falhas, "
              f"{len(result.errors)} erros")
        print("\nCiclo TDD demonstrado: RED (testes falham) → GREEN (código passa) → REFACTOR (código limpo)")
        sys.exit(0 if result.wasSuccessful() else 1)
    else:
        unittest.main(verbosity=2)
