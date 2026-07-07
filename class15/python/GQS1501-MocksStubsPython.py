#!/usr/bin/env python3
"""
GQS1501-MocksStubsPython.py
============================
Exemplos de Mocks, Stubs e Test Doubles com unittest.mock (Python).
Aula 15 — Mocks, Stubs, Doubles (Mockito) | Garantia da Qualidade de Software.

Uso:
    python3 -m pytest GQS1501-MocksStubsPython.py -v
    python3 GQS1501-MocksStubsPython.py --validate
"""

import sys
import unittest
from unittest.mock import Mock, MagicMock, patch, PropertyMock
from typing import List, Optional


# ──────────────────────────────────────────────
# 1. SISTEMA DE NOTIFICAÇÃO
# ──────────────────────────────────────────────

class ServicoEmail:
    """Serviço real de email (não deve ser chamado em testes unitários)."""

    def enviar(self, destinatario: str, mensagem: str) -> bool:
        # Simulação de envio real
        print(f"ENVIANDO EMAIL PARA {destinatario}: {mensagem}")
        return True


class SistemaNotificacao:
    """Sistema que usa ServicoEmail — queremos testar sem enviar email."""

    def __init__(self, servico_email: ServicoEmail):
        self._servico = servico_email

    def notificar_promocao(self, email: str, produto: str, desconto: float) -> bool:
        mensagem = f"Promoção! {produto} com {desconto:.0f}% de desconto!"
        return self._servico.enviar(email, mensagem)


# ──────────────────────────────────────────────
# 2. CARRINHO DE COMPRAS
# ──────────────────────────────────────────────

class CalculadoraDesconto:
    """Calcula descontos — será substituída por stub nos testes."""

    def calcular(self, valor_total: float, cupom: str) -> float:
        raise NotImplementedError("Chamada real — use stub nos testes")


class Item:
    def __init__(self, nome: str, preco: float, quantidade: int = 1):
        self.nome = nome
        self.preco = preco
        self.quantidade = quantidade


class CarrinhoCompras:
    def __init__(self, calculadora: CalculadoraDesconto):
        self._itens: List[Item] = []
        self._calculadora = calculadora

    def adicionar(self, item: Item) -> None:
        self._itens.append(item)

    def total_sem_desconto(self) -> float:
        return sum(item.preco * item.quantidade for item in self._itens)

    def total_com_desconto(self, cupom: str) -> float:
        total = self.total_sem_desconto()
        desconto = self._calculadora.calcular(total, cupom)
        return total - desconto


# ──────────────────────────────────────────────
# 3. AUTENTICADOR DE USUÁRIO
# ──────────────────────────────────────────────

class RepositorioUsuario:
    """Acesso a banco de dados — substituído por mock nos testes."""

    def buscar_por_email(self, email: str) -> Optional[dict]:
        raise NotImplementedError("Chamada real ao banco")


class CriptografadorSenha:
    """Serviço de hash de senha — stub nos testes."""

    def verificar(self, senha_plana: str, hash_armazenado: str) -> bool:
        raise NotImplementedError("Chamada real de hash")


class AutenticadorUsuario:
    def __init__(self, repo: RepositorioUsuario, cripto: CriptografadorSenha):
        self._repo = repo
        self._cripto = cripto

    def autenticar(self, email: str, senha: str) -> bool:
        usuario = self._repo.buscar_por_email(email)
        if not usuario:
            return False
        return self._cripto.verificar(senha, usuario["senha_hash"])


# ══════════════════════════════════════════════
# TESTES COM MOCK / STUB
# ══════════════════════════════════════════════

class TestSistemaNotificacao(unittest.TestCase):
    """Demonstra Mock — verifica interação sem enviar email real."""

    def test_notificar_promocao_envia_email(self):
        # Arrange: criar mock do ServicoEmail
        mock_email = Mock(spec=ServicoEmail)
        mock_email.enviar.return_value = True

        sistema = SistemaNotificacao(mock_email)

        # Act
        resultado = sistema.notificar_promocao("cliente@test.com", "Curso Python", 25)

        # Assert: verifica se o método foi chamado com os parâmetros corretos
        self.assertTrue(resultado)
        mock_email.enviar.assert_called_once_with(
            "cliente@test.com",
            "Promoção! Curso Python com 25% de desconto!"
        )

    def test_notificar_promocao_falha_no_envio(self):
        mock_email = Mock(spec=ServicoEmail)
        mock_email.enviar.return_value = False  # simula falha

        sistema = SistemaNotificacao(mock_email)
        resultado = sistema.notificar_promocao("x@y.com", "Livro", 10)

        self.assertFalse(resultado)


class TestCarrinhoCompras(unittest.TestCase):
    """Demonstra Stub — objeto com comportamento controlado."""

    def test_total_sem_desconto(self):
        # Arrange: stub da calculadora (retorna 0 para qualquer chamada)
        stub_calculadora = Mock(spec=CalculadoraDesconto)
        stub_calculadora.calcular.return_value = 0

        carrinho = CarrinhoCompras(stub_calculadora)
        carrinho.adicionar(Item("Teclado", 150.0))
        carrinho.adicionar(Item("Mouse", 80.0, 2))

        # Act + Assert
        self.assertEqual(carrinho.total_sem_desconto(), 150.0 + 160.0)

    def test_total_com_desconto_fixo(self):
        # Stub: desconto fixo de R$ 50
        stub_calculadora = Mock(spec=CalculadoraDesconto)
        stub_calculadora.calcular.return_value = 50.0

        carrinho = CarrinhoCompras(stub_calculadora)
        carrinho.adicionar(Item("Monitor", 1000.0))

        self.assertEqual(carrinho.total_com_desconto("CUPOM50"), 950.0)

    def test_total_com_desconto_percentual(self):
        # Stub: 10% de desconto
        stub_calculadora = Mock(spec=CalculadoraDesconto)
        stub_calculadora.calcular.side_effect = lambda total, cupom: total * 0.10

        carrinho = CarrinhoCompras(stub_calculadora)
        carrinho.adicionar(Item("Notebook", 3000.0))

        self.assertEqual(carrinho.total_com_desconto("PROMO10"), 2700.0)


class TestAutenticadorUsuario(unittest.TestCase):
    """Demonstra Spy — registra interações e verifica chamadas."""

    def test_autenticar_com_sucesso(self):
        # Mock do repositório
        mock_repo = Mock(spec=RepositorioUsuario)
        mock_repo.buscar_por_email.return_value = {
            "email": "user@test.com",
            "senha_hash": "hash123"
        }

        # Mock do criptografador
        mock_cripto = Mock(spec=CriptografadorSenha)
        mock_cripto.verificar.return_value = True

        autenticador = AutenticadorUsuario(mock_repo, mock_cripto)
        resultado = autenticador.autenticar("user@test.com", "senha123")

        self.assertTrue(resultado)
        mock_repo.buscar_por_email.assert_called_once_with("user@test.com")
        mock_cripto.verificar.assert_called_once_with("senha123", "hash123")

    def test_autenticar_usuario_inexistente(self):
        mock_repo = Mock(spec=RepositorioUsuario)
        mock_repo.buscar_por_email.return_value = None

        mock_cripto = Mock(spec=CriptografadorSenha)

        autenticador = AutenticadorUsuario(mock_repo, mock_cripto)
        resultado = autenticador.autenticar("unknown@test.com", "senha")

        self.assertFalse(resultado)
        mock_cripto.verificar.assert_not_called()  # não deve tentar verificar senha

    def test_autenticar_com_falha_na_senha(self):
        mock_repo = Mock(spec=RepositorioUsuario)
        mock_repo.buscar_por_email.return_value = {
            "email": "user@test.com",
            "senha_hash": "hash123"
        }

        mock_cripto = Mock(spec=CriptografadorSenha)
        mock_cripto.verificar.return_value = False

        autenticador = AutenticadorUsuario(mock_repo, mock_cripto)
        resultado = autenticador.autenticar("user@test.com", "senha_errada")

        self.assertFalse(resultado)


# ══════════════════════════════════════════════
# DEMONSTRAÇÃO: DIFERENTES TIPOS DE TEST DOUBLE
# ══════════════════════════════════════════════

class TestTiposTestDouble(unittest.TestCase):
    """Demonstra os 5 tipos de Test Double com exemplos."""

    def test_dummy(self):
        """Dummy: objeto passado mas nunca usado."""
        dummy = object()  # apenas para preencher parâmetro
        # Se o método não usar o dummy, é um Dummy
        self.assertIsNotNone(dummy)

    def test_fake(self):
        """Fake: implementação simplificada que funciona."""
        class FakeRepositorio:
            def __init__(self):
                self._dados = {}

            def salvar(self, chave, valor):
                self._dados[chave] = valor

            def buscar(self, chave):
                return self._dados.get(chave)

        repo = FakeRepositorio()
        repo.salvar("user1", {"nome": "João"})
        self.assertEqual(repo.buscar("user1")["nome"], "João")

    def test_stub(self):
        """Stub: retorna valores pré-definidos."""
        stub = Mock()
        stub.get_data.return_value = "dados mockados"
        stub.get_status.return_value = 200
        self.assertEqual(stub.get_data(), "dados mockados")
        self.assertEqual(stub.get_status(), 200)

    def test_spy(self):
        """Spy: registra interações (wrapper)."""
        spy = Mock(wraps={"chave": "valor"})
        _ = spy.get("chave")
        spy.get.assert_called_once_with("chave")

    def test_mock(self):
        """Mock: stub + spy combinados."""
        mock = Mock()
        mock.procesar(1, 2, 3)
        mock.procesar.assert_called_once_with(1, 2, 3)


# ══════════════════════════════════════════════
# PONTO DE ENTRADA
# ══════════════════════════════════════════════

if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "--validate":
        suite = unittest.TestLoader().loadTestsFromModule(sys.modules[__name__])
        runner = unittest.TextTestRunner(verbosity=2)
        result = runner.run(suite)
        print(f"\nTotal: {result.testsRun} | Falhas: {len(result.failures)} | Erros: {len(result.errors)}")
        sys.exit(0 if result.wasSuccessful() else 1)
    else:
        unittest.main(verbosity=2)
