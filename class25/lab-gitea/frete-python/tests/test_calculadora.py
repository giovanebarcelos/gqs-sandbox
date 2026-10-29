"""Testes unitarios das regras R1 a R4 (etapa "testes" do pipeline)."""

from decimal import Decimal

import pytest

from frete.calculadora import FreteInvalido, calcular_frete


def test_frete_sul_com_dois_kg_soma_um_kg_extra():
    cotacao = calcular_frete("SUL", 2.0, Decimal("100.00"))
    assert cotacao["frete"] == Decimal("17.50")
    assert cotacao["gratis"] is False
    assert cotacao["prazoDias"] == 3


def test_ate_um_kg_cobra_so_o_frete_base():
    assert calcular_frete("NORTE", 1.0, Decimal("50.00"))["frete"] == Decimal("35.00")


def test_fracao_de_kg_conta_como_kg_inteiro():
    # 2,3 kg = 1 kg da base + 2 kg extras (1,3 arredonda para 2)
    assert calcular_frete("SUDESTE", 2.3, Decimal("80.00"))["frete"] == Decimal("23.00")


def test_pedido_de_exatamente_200_tem_frete_gratis():
    cotacao = calcular_frete("NORDESTE", 5.0, Decimal("200.00"))
    assert cotacao["gratis"] is True, "R2: a partir de R$ 200,00 o frete e' gratis"
    assert cotacao["frete"] == Decimal("0.00")


def test_pedido_abaixo_de_200_paga_frete():
    cotacao = calcular_frete("NORDESTE", 5.0, Decimal("199.99"))
    assert cotacao["gratis"] is False
    assert cotacao["frete"] == Decimal("40.00")


@pytest.mark.parametrize("peso", [30.5, 0, -1])
def test_peso_fora_do_limite_e_rejeitado(peso):
    with pytest.raises(FreteInvalido):
        calcular_frete("SUL", peso, Decimal("10.00"))


def test_valor_negativo_e_rejeitado():
    with pytest.raises(FreteInvalido):
        calcular_frete("SUL", 1, Decimal("-1"))


def test_regiao_desconhecida_e_rejeitada():
    with pytest.raises(ValueError, match="Regiao invalida: LESTE"):
        calcular_frete("LESTE", 1, Decimal("10.00"))


def test_regiao_aceita_minusculas_e_hifen():
    cotacao = calcular_frete("centro-oeste", 1, Decimal("10.00"))
    assert cotacao["regiao"] == "CENTRO_OESTE"
    assert cotacao["prazoDias"] == 5
