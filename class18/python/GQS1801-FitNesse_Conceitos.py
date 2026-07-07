#!/usr/bin/env python3
"""
GQS1801 - Conceitos de FitNesse (Decision Table e fixture Slim) em Python.
Demonstra didaticamente, sem depender do FitNesse real, como uma Decision
Table é processada linha a linha por uma fixture e como o resultado
obtido é comparado ao esperado - a mesma lógica usada pelo servidor Slim.
Uso: python3 GQS1801-FitNesse_Conceitos.py
"""


class CalculadoraFixture:
    """Equivalente Python da CalculadoraFixture.java (Slide 10)."""

    def __init__(self, a: float, b: float):
        self.a = a
        self.b = b

    def somar(self) -> float:
        return self.a + self.b

    def subtrair(self) -> float:
        return self.a - self.b

    def multiplicar(self) -> float:
        return self.a * self.b

    def dividir(self) -> float:
        return self.a / self.b


def processar_decision_table(linhas):
    """
    Simula o que o servidor Slim faz: para cada linha da tabela,
    instancia a fixture com as colunas de entrada e compara o resultado
    dos métodos com as colunas de saída esperadas.
    """
    total = 0
    aprovados = 0
    for linha in linhas:
        total += 1
        fixture = CalculadoraFixture(linha["a"], linha["b"])
        resultados = {
            "somar()": fixture.somar(),
            "subtrair()": fixture.subtrair(),
            "multiplicar()": fixture.multiplicar(),
            "dividir()": round(fixture.dividir(), 3),
        }
        ok = all(resultados[campo] == linha[campo] for campo in resultados)
        status = "PASS" if ok else "FAIL"
        if ok:
            aprovados += 1
        print(f"  [{status}] a={linha['a']} b={linha['b']} -> {resultados}")
    return aprovados, total


def main():
    print("=" * 60)
    print("  GQS1801 - CONCEITOS DE DECISION TABLE (FitNesse/Slim)")
    print("=" * 60)

    tabela = [
        {"a": 10, "b": 5, "somar()": 15, "subtrair()": 5, "multiplicar()": 50, "dividir()": 2.0},
        {"a": 7, "b": 3, "somar()": 10, "subtrair()": 4, "multiplicar()": 21, "dividir()": 2.333},
    ]

    print("\nProcessando Decision Table (CalculadoraFixture):")
    aprovados, total = processar_decision_table(tabela)

    print(f"\nResultado: {aprovados}/{total} linhas aprovadas")
    print("=" * 60)


if __name__ == "__main__":
    main()
