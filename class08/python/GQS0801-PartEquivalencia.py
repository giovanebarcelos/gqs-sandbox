#!/usr/bin/env python3
"""
GQS0801 - Demonstração de Partição de Equivalência e Análise de Valor Limite (BVA).
Uso: python3 GQS0801-PartEquivalencia.py
"""


def classificar_nota(nota: float) -> str:
    """Exemplo de campo Nota (0 a 10)."""
    if not isinstance(nota, (int, float)):
        return "Inválido: não numérico"
    if nota < 0 or nota > 10:
        return "Inválido: fora da faixa"
    if nota >= 6:
        return "Aprovado"
    return "Reprovado"


def particao_equivalencia_nota():
    """Demonstra partição de equivalência para campo Nota."""
    print("Partição de Equivalência - Campo Nota (0 a 10):")
    classes = [
        ("CE1", "0 ≤ nota ≤ 10", "Válida", 7),
        ("CE2", "nota < 0", "Inválida", -1),
        ("CE3", "nota > 10", "Inválida", 11),
        ("CE4", "não numérico", "Inválida", "abc"),
    ]
    for classe, intervalo, tipo, valor in classes:
        resultado = classificar_nota(valor)
        print(f"  {classe}: {intervalo:<20} {tipo:<10} → valor {str(valor):<5} = {resultado}")


def bva_nota():
    """Demonstra Boundary Value Analysis para campo Nota."""
    print("\nBVA (3-valor) - Campo Nota (0 a 10):")
    pontos = [-1, 0, 1, 9, 10, 11]
    rotulos = ["Abaixo mínimo", "Mínimo exato", "Mínimo + 1", "Máximo - 1", "Máximo exato", "Acima máximo"]
    for valor, rotulo in zip(pontos, rotulos):
        print(f"  {rotulo:<20} = {str(valor):<5} → {classificar_nota(valor)}")


def main():
    print("=" * 60)
    print("  GQS0801 - PARTIÇÃO DE EQUIVALÊNCIA E BVA")
    print("=" * 60)
    particao_equivalencia_nota()
    bva_nota()


if __name__ == "__main__":
    main()
