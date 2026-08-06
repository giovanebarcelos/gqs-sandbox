#!/usr/bin/env python3
"""
GQS2701 - Calculadora de Pontos de Função e Estimativa COCOMO.
Calcula PF Não Ajustado, VAF, PF Ajustado, KLOC e esforço/prazo (COCOMO Básico).
Uso: python3 GQS2701-CalculadoraPontosFuncao.py
"""

PESOS_DADOS = {
    "ILF": {"baixa": 7, "media": 10, "alta": 15},
    "EIF": {"baixa": 5, "media": 7, "alta": 10},
}

PESOS_TRANSACAO = {
    "EI": {"baixa": 3, "media": 4, "alta": 6},
    "EO": {"baixa": 4, "media": 5, "alta": 7},
    "EQ": {"baixa": 3, "media": 4, "alta": 6},
}

LOC_POR_PF = {"assembly": 320, "c": 128, "java": 53, "python": 42}


def peso_funcao(tipo: str, complexidade: str) -> int:
    tabela = PESOS_DADOS.get(tipo.upper()) or PESOS_TRANSACAO.get(tipo.upper())
    if tabela is None:
        raise ValueError(f"tipo de função desconhecido: {tipo}")
    return tabela[complexidade.lower()]


def calcular_pfna(funcoes: list[tuple[str, str]]) -> int:
    """funcoes: lista de (tipo, complexidade), ex.: [("ILF", "baixa"), ...]"""
    return sum(peso_funcao(tipo, complexidade) for tipo, complexidade in funcoes)


def calcular_vaf(tdi: int) -> float:
    if not 0 <= tdi <= 70:
        raise ValueError("TDI deve estar entre 0 e 70")
    return round(0.65 + (tdi * 0.01), 4)


def calcular_pf_ajustado(pfna: int, vaf: float) -> float:
    return round(pfna * vaf, 2)


def pf_para_kloc(pf_ajustado: float, linguagem: str) -> float:
    loc_pf = LOC_POR_PF[linguagem.lower()]
    return round((pf_ajustado * loc_pf) / 1000, 4)


def cocomo_esforco(kloc: float, a: float = 2.4, b: float = 1.05) -> float:
    return round(a * (kloc ** b), 2)


def cocomo_prazo(esforco_pessoas_mes: float, c: float = 2.5, d: float = 0.38) -> float:
    return round(c * (esforco_pessoas_mes ** d), 2)


def aderencia_percentual(real: float, estimado: float) -> float:
    if estimado == 0:
        return 0.0
    return round((real - estimado) / estimado * 100, 2)


def main():
    print("=" * 64)
    print("  GQS2701 - CALCULADORA DE PONTOS DE FUNÇÃO E COCOMO")
    print("=" * 64)

    print("\n--- Exemplo: Sistema de Cadastro de Clientes (Aula 27) ---")
    funcoes = [
        ("ILF", "baixa"),   # Tabela Clientes
        ("EIF", "baixa"),   # Tabela de CEP (externa)
        ("EI", "baixa"),    # Cadastrar Cliente
        ("EI", "baixa"),    # Atualizar Cliente
        ("EO", "media"),    # Relatório de Clientes Inadimplentes
        ("EQ", "baixa"),    # Consultar Cliente por CPF
    ]
    pfna = calcular_pfna(funcoes)
    print(f"  PF Não Ajustado (PFNA) = {pfna} PF")

    tdi = int(input("\nTDI do projeto (0-70, padrão 30): ") or 30)
    vaf = calcular_vaf(tdi)
    pf_ajustado = calcular_pf_ajustado(pfna, vaf)
    print(f"  VAF = {vaf}")
    print(f"  PF Ajustado = {pf_ajustado} PF")

    linguagem = (input("Linguagem alvo (java/python, padrão java): ") or "java").lower()
    kloc = pf_para_kloc(pf_ajustado, linguagem)
    print(f"\n  KLOC estimado ({linguagem}) = {kloc} KLOC")

    esforco = cocomo_esforco(kloc)
    prazo = cocomo_prazo(esforco)
    equipe = round(esforco / prazo, 2) if prazo else 0
    print(f"  Esforço (COCOMO Básico) = {esforco} pessoas-mês")
    print(f"  Prazo (COCOMO Básico)   = {prazo} meses")
    print(f"  Equipe média            = {equipe} pessoas")

    print("\n--- Aderência do projeto real (opcional) ---")
    esforco_real = input("Esforço real, em pessoas-mês (Enter para pular): ")
    if esforco_real:
        aderencia = aderencia_percentual(float(esforco_real), esforco)
        sinal = "acima" if aderencia > 0 else "abaixo"
        print(f"  Aderência a esforço = {aderencia}% ({sinal} do estimado)")


if __name__ == "__main__":
    main()
