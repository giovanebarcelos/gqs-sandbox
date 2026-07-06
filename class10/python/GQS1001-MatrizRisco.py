#!/usr/bin/env python3
"""
GQS1001 - Calculadora de Exposição a Risco (Probabilidade × Impacto).
Uso: python3 GQS1001-MatrizRisco.py
"""


def calcular_risco(probabilidade: int, impacto: int) -> int:
    """Risco = Probabilidade × Impacto."""
    return probabilidade * impacto


def classificar_risco(risco: int) -> str:
    if risco >= 15:
        return "🔴 Crítico"
    elif risco >= 10:
        return "🟠 Alto"
    elif risco >= 5:
        return "🟡 Médio"
    else:
        return "🟢 Baixo"


def main():
    print("=" * 60)
    print("  GQS1001 - MATRIZ DE RISCO (Probabilidade × Impacto)")
    print("=" * 60)

    print("\nEscala de Probabilidade e Impacto (1 a 5):")
    print("  1 = Muito Baixo | 2 = Baixo | 3 = Médio | 4 = Alto | 5 = Muito Alto")

    funcionalidades = [
        ("Checkout/Pagamento", 3, 5),
        ("Carrinho de Compras", 4, 4),
        ("Catálogo de Produtos", 3, 2),
        ("Cadastro de Usuário", 2, 3),
        ("Busca/Filtros", 4, 2),
    ]

    print(f"\n{'Funcionalidade':<25} {'Prob.':<8} {'Imp.':<8} {'Risco':<8} {'Classificação':<15}")
    print("-" * 65)
    for nome, prob, imp in funcionalidades:
        risco = calcular_risco(prob, imp)
        classificacao = classificar_risco(risco)
        print(f"{nome:<25} {prob:<8} {imp:<8} {risco:<8} {classificacao:<15}")

    print("\n--- Priorização de Testes ---")
    print("  Crítico: Teste exaustivo, automação prioritária")
    print("  Alto: Teste extensivo")
    print("  Médio: Teste aprofundado")
    print("  Baixo: Teste padrão")


if __name__ == "__main__":
    main()
