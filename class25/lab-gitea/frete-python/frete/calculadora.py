"""
Regras de frete da LojaOnline.

    R1  frete = base da regiao + R$ 2,50 por kg (ou fracao) acima de 1 kg
    R2  pedidos a partir de R$ 200,00 tem frete gratis
    R3  peso aceito: maior que 0 e no maximo 30 kg
    R4  regiao desconhecida e' rejeitada
"""

import math
from decimal import Decimal

# regiao: (frete base, prazo em dias)
REGIOES = {
    "SUL": (Decimal("15.00"), 3),
    "SUDESTE": (Decimal("18.00"), 2),
    "CENTRO_OESTE": (Decimal("25.00"), 5),
    "NORDESTE": (Decimal("30.00"), 7),
    "NORTE": (Decimal("35.00"), 9),
}

LIMITE_FRETE_GRATIS = Decimal("200.00")
ADICIONAL_POR_KG = Decimal("2.50")
PESO_MAXIMO_KG = 30


class FreteInvalido(Exception):
    """Peso ou valor do pedido fora das regras (R3)."""


def normalizar_regiao(texto):
    """Aceita "sul", "Sul", "centro-oeste"... Texto desconhecido: ValueError."""
    if not texto or not str(texto).strip():
        raise ValueError("Informe a regiao")
    nome = str(texto).strip().upper().replace("-", "_")
    if nome not in REGIOES:
        raise ValueError(f"Regiao invalida: {texto}")
    return nome


def _adicional_por_peso(peso_kg):
    """R$ 2,50 por kg ou fracao acima do primeiro: 2,3 kg cobra 2 kg extras."""
    kg_extras = math.ceil(peso_kg - 1)
    return ADICIONAL_POR_KG * kg_extras if kg_extras > 0 else Decimal("0")


def calcular_frete(regiao_texto, peso_kg, valor_pedido):
    regiao = normalizar_regiao(regiao_texto)
    if peso_kg is None or peso_kg <= 0 or peso_kg > PESO_MAXIMO_KG:
        raise FreteInvalido("Peso deve ser maior que 0 e no maximo 30 kg")
    if valor_pedido is None or valor_pedido < 0:
        raise FreteInvalido("Valor do pedido invalido")

    base, prazo = REGIOES[regiao]
    gratis = valor_pedido > LIMITE_FRETE_GRATIS
    frete = Decimal("0") if gratis else base + _adicional_por_peso(peso_kg)

    return {
        "regiao": regiao,
        "pesoKg": peso_kg,
        "valorPedido": valor_pedido,
        "frete": frete.quantize(Decimal("0.01")),
        "gratis": gratis,
        "prazoDias": prazo,
    }
