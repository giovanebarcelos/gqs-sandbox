#!/usr/bin/env python3
"""
GQS0501 - Demonstração da Pirâmide de Testes.
Calcula a distribuição ideal de testes por nível.
Uso: python3 GQS0501-PiramideTestes.py
"""


class NivelTeste:
    def __init__(self, nome: str, quantidade: int, velocidade: str, custo: str):
        self.nome = nome
        self.quantidade = quantidade
        self.velocidade = velocidade
        self.custo = custo
        self.percentual = 0.0


def calcular_distribuicao(total_testes: int = 1000) -> list:
    """Calcula a distribuição ideal (70-20-10)."""
    return [
        NivelTeste("Testes Unitários", int(total_testes * 0.7), "ms", "Baixo"),
        NivelTeste("Testes de Integração", int(total_testes * 0.2), "s", "Médio"),
        NivelTeste("Testes E2E / UI", int(total_testes * 0.1), "min", "Alto"),
    ]


def main():
    print("=" * 60)
    print("  GQS0501 - PIRÂMIDE DE TESTES")
    print("=" * 60)

    total = int(input("\nNúmero total de testes desejado (ex: 1000): ") or 1000)
    niveis = calcular_distribuicao(total)

    print(f"\n{'Nível':<25} {'Quantidade':<12} {'Velocidade':<12} {'Custo':<10}")
    print("-" * 60)
    for n in niveis:
        print(f"{n.nome:<25} {n.quantidade:<12} {n.velocidade:<12} {n.custo:<10}")

    print(f"\nDistribuição: 70% Unitários + 20% Integração + 10% E2E")
    print(f"Total: {sum(n.quantidade for n in niveis)} testes")

    print("\n--- Custo Relativo de Execução ---")
    tempo_unitario = total * 0.7 * 0.01  # 10ms cada
    tempo_integracao = total * 0.2 * 1.0  # 1s cada
    tempo_e2e = total * 0.1 * 60.0  # 60s cada
    print(f"  Unitários: {tempo_unitario:.1f}s")
    print(f"  Integração: {tempo_integracao:.1f}s")
    print(f"  E2E: {tempo_e2e:.1f}s")
    print(f"  Total: {tempo_unitario + tempo_integracao + tempo_e2e:.1f}s")


if __name__ == "__main__":
    main()
