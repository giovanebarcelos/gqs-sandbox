"""
Testes unitarios das regras de carrinho (pytest) - Aula 24.

Convencao que liga o codigo ao Kiwi TCMS: o nome de cada teste comeca com o
identificador do caso de teste cadastrado no Kiwi (CT01, CT02, ...). O script
kiwi/publicar_resultados.py le o junit.xml, extrai esse identificador e grava
o resultado na TestExecution do caso de teste que tem o mesmo prefixo no
Summary. Ou seja: o caso escrito a mao no Kiwi passa a ser executado por
este codigo.

Rodar:
    pytest -v                                   # so no terminal
    pytest -v --junitxml=resultados-unit.xml    # gera o relatorio para o Kiwi

Na versao 1.0.0 da aplicacao, CT03 e CT05 FALHAM (defeitos plantados).
"""

from decimal import Decimal

import pytest

from techstore.carrinho import (
    CarrinhoInvalido,
    ProdutoNaoEncontrado,
    calcular_carrinho,
    calcular_frete,
    calcular_subtotal,
    validar_quantidade,
)


def test_CT01_subtotal_de_multiplos_itens():
    """RN01 - 2 x Mouse (120,00) + 1 x Cabo HDMI (25,00) = 265,00."""
    itens = [
        {"produto_id": 1, "quantidade": 2},
        {"produto_id": 4, "quantidade": 1},
    ]
    assert calcular_subtotal(itens) == Decimal("265.00")


def test_CT02_cupom_GQS10_aplica_10_por_cento():
    """RN02 - Teclado (250,00) com GQS10: desconto 25,00, frete gratis, total 225,00."""
    resumo = calcular_carrinho([{"produto_id": 2, "quantidade": 1}], cupom="GQS10")
    assert resumo["desconto"] == Decimal("25.00")
    assert resumo["frete"] == Decimal("0.00")
    assert resumo["total"] == Decimal("225.00")


def test_CT03_frete_gratis_no_limite_exato_de_200():
    """RN03 - valor limite: exatamente R$ 200,00 ja tem frete gratis."""
    assert calcular_frete(Decimal("200.00")) == Decimal("0.00")


def test_CT04_frete_cobrado_abaixo_de_200():
    """RN03 - valor limite: R$ 199,99 ainda paga R$ 15,00 de frete."""
    assert calcular_frete(Decimal("199.99")) == Decimal("15.00")


@pytest.mark.parametrize("quantidade", [0, -1])
def test_CT05_rejeita_quantidade_menor_que_1(quantidade):
    """RN04 - limite inferior: 0 e -1 devem ser recusados."""
    with pytest.raises(CarrinhoInvalido):
        validar_quantidade(quantidade)


@pytest.mark.parametrize("quantidade", [11, 50])
def test_CT06_rejeita_quantidade_maior_que_10(quantidade):
    """RN04 - limite superior: 11 em diante deve ser recusado."""
    with pytest.raises(CarrinhoInvalido):
        validar_quantidade(quantidade)


@pytest.mark.parametrize("quantidade", [1, 10])
def test_CT06_aceita_quantidades_nos_limites(quantidade):
    """RN04 - 1 e 10 sao validos (nao deve levantar excecao)."""
    validar_quantidade(quantidade)


def test_CT07_rejeita_cupom_invalido():
    """RN02 - cupom inexistente e' recusado."""
    with pytest.raises(CarrinhoInvalido):
        calcular_carrinho([{"produto_id": 1, "quantidade": 1}], cupom="NATAL50")


def test_CT08_rejeita_produto_inexistente():
    """RN05 - produto 999 nao existe."""
    with pytest.raises(ProdutoNaoEncontrado):
        calcular_subtotal([{"produto_id": 999, "quantidade": 1}])
