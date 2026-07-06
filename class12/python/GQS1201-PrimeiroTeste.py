#!/usr/bin/env python3
"""
GQS1201 - Exemplo "Hello Test" de Automação de Testes.
Demonstra o padrão AAA (Arrange-Act-Assert).
Uso: python3 GQS1201-PrimeiroTeste.py

Para executar os testes:
  pytest GQS1201-PrimeiroTeste.py -v
"""


def somar(a: int, b: int) -> int:
    """Função simples a ser testada."""
    return a + b


def test_soma_deve_retornar_cinco():
    """Exemplo de teste com padrão AAA."""
    # Arrange
    a, b = 2, 3
    # Act
    resultado = somar(a, b)
    # Assert
    assert resultado == 5, f"Esperado 5, obtido {resultado}"


def test_soma_com_zero():
    assert somar(0, 5) == 5


def test_soma_com_negativos():
    assert somar(-1, 1) == 0


def test_soma_valores_grandes():
    assert somar(1000000, 2000000) == 3000000


def test_soma_duas_vezes():
    """Demonstra independência de testes."""
    assert somar(3, 4) == 7
    assert somar(10, 20) == 30


if __name__ == "__main__":
    print("GQS1201 - Executando testes manualmente:")
    print(f"  somar(2, 3) = {somar(2, 3)}")
    print(f"  somar(0, 5) = {somar(0, 5)}")
    print(f"  somar(-1, 1) = {somar(-1, 1)}")
    print("\nExecute com 'pytest GQS1201-PrimeiroTeste.py -v'")
