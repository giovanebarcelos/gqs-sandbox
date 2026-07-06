#!/usr/bin/env python3
"""
GQS2201 - Calculadora de Métricas de Software.
Calcula MTBF, MTTR, Disponibilidade e Densidade de Defeitos.
Uso: python3 GQS2201-CalculadoraMetricas.py
"""


def calcular_mtbf(tempo_total_h: float, num_falhas: int) -> float:
    if num_falhas == 0:
        return float("inf")
    return round(tempo_total_h / num_falhas, 2)


def calcular_mttr(tempo_reparo_h: float, num_falhas: int) -> float:
    if num_falhas == 0:
        return 0.0
    return round(tempo_reparo_h / num_falhas, 2)


def calcular_disponibilidade(mtbf: float, mttr: float) -> float:
    if mtbf + mttr == 0:
        return 0.0
    return round(mtbf / (mtbf + mttr) * 100, 4)


def calcular_densidade_defeitos(defeitos: int, loc: int) -> float:
    if loc == 0:
        return 0.0
    return round(defeitos / (loc / 1000), 2)


def nivel_disponibilidade(disponibilidade: float) -> str:
    if disponibilidade >= 99.999:
        return "Five Nines (Critical Systems)"
    elif disponibilidade >= 99.99:
        return "Four Nines (High Availability)"
    elif disponibilidade >= 99.9:
        return "Three Nines (Standard)"
    elif disponibilidade >= 99:
        return "Two Nines (Basic)"
    else:
        return "Below Standard"


def main():
    print("=" * 60)
    print("  GQS2201 - CALCULADORA DE MÉTRICAS DE SOFTWARE")
    print("=" * 60)

    print("\n--- Métricas de Confiabilidade ---")
    tempo_total = float(input("Tempo total de operação (horas): ") or 720)
    num_falhas = int(input("Número de falhas: ") or 3)
    tempo_reparo = float(input("Tempo total de reparo (horas): ") or 6)

    mtbf = calcular_mtbf(tempo_total, num_falhas)
    mttr = calcular_mttr(tempo_reparo, num_falhas)
    disp = calcular_disponibilidade(mtbf, mttr)

    print(f"\n  MTBF = {mtbf}h")
    print(f"  MTTR = {mttr}h")
    print(f"  Disponibilidade = {disp}%")
    print(f"  Nível: {nivel_disponibilidade(disp)}")

    print("\n--- Densidade de Defeitos ---")
    defeitos = int(input("Número de defeitos encontrados: ") or 25)
    loc = int(input("Tamanho do sistema (LOC): ") or 50000)
    densidade = calcular_densidade_defeitos(defeitos, loc)
    print(f"\n  Densidade de Defeitos = {densidade} defeitos/KLOC")
    if densidade < 1:
        print("  Classificação: Excelente (sistemas críticos)")
    elif densidade < 5:
        print("  Classificação: Bom (sistemas corporativos)")
    else:
        print("  Classificação: Precisa melhorar")


if __name__ == "__main__":
    main()
