#!/usr/bin/env python3
"""
GQS0901 - Demonstração de Cobertura de Instrução e Decisão.
Analisa um código exemplo e calcula as métricas de cobertura.
Uso: python3 GQS0901-CoberturaDecisao.py
"""


def funcao_exemplo(a: int, b: int) -> str:
    """Função exemplo para análise de cobertura."""
    if a > 0 and b > 0:
        return "Ambos positivos"
    elif a > 0:
        return "A positivo"
    elif b > 0:
        return "B positivo"
    else:
        return "Nenhum positivo"


def calcular_complexidade_ciclomatica(decisions: int) -> int:
    """M = número de decisões + 1 (McCabe)."""
    return decisions + 1


def main():
    print("=" * 60)
    print("  GQS0901 - COBERTURA DE INSTRUÇÃO E DECISÃO")
    print("=" * 60)

    print("\nFunção analisada: funcao_exemplo(a, b)")
    print("  if a > 0 and b > 0 → Ambos positivos")
    print("  elif a > 0 → A positivo")
    print("  elif b > 0 → B positivo")
    print("  else → Nenhum positivo")
    print(f"\n  Complexidade Ciclomática (M) = {calcular_complexidade_ciclomatica(3)}")
    print("  (3 decisões + 1 = 4 caminhos independentes)")

    print("\n--- Cobertura de Decisão ---")
    casos = [
        (5, 5, "Ambos positivos"),
        (5, -1, "A positivo"),
        (-1, 5, "B positivo"),
        (-1, -1, "Nenhum positivo"),
    ]
    print(f"{'Teste (a, b)':<15} {'Resultado':<20} {'Cobertura':<15}")
    print("-" * 50)
    for i, (a, b, esperado) in enumerate(casos, 1):
        resultado = funcao_exemplo(a, b)
        coberto = "✅" if resultado == esperado else "❌"
        print(f"  CT-{i:02d} ({a:<2},{b:<3}) {resultado:<20} {coberto}")

    print("\n  Para 100% de cobertura de decisão: todos os 4 casos")
    print("  Para 100% de cobertura de instrução: casos 1, 2, 3, 4")


if __name__ == "__main__":
    main()
