"""
Regras de negocio do carrinho da TechStore (Aula 24 - laboratorio Kiwi TCMS).

Este modulo NAO depende do Flask: e' Python puro, para poder ser testado
por testes unitarios (pytest) sem subir servidor nenhum. A API em app.py
apenas traduz HTTP <-> chamadas destas funcoes.

Regras de negocio (sao elas que viram casos de teste no Kiwi TCMS):

    RN01  subtotal = soma de (preco unitario x quantidade) de cada item
    RN02  o cupom "GQS10" da 10% de desconto sobre o subtotal;
          qualquer outro cupom e' recusado
    RN03  frete fixo de R$ 15,00; GRATIS quando o valor com desconto
          for MAIOR OU IGUAL a R$ 200,00
    RN04  a quantidade de cada item deve estar entre 1 e 10 (inclusive)
    RN05  produto inexistente e' recusado

ATENCAO, ALUNO: esta versao (1.0.0) contem DOIS defeitos plantados de
proposito. Nao corrija antes de executar a Etapa 7 do tutorial - a graca
e' ver o Kiwi TCMS registrar as falhas e o defeito.
"""

from decimal import ROUND_HALF_UP, Decimal

PRODUTOS = {
    1: {"id": 1, "nome": "Mouse Gamer", "preco": Decimal("120.00")},
    2: {"id": 2, "nome": "Teclado Mecanico", "preco": Decimal("250.00")},
    3: {"id": 3, "nome": "Headset", "preco": Decimal("80.00")},
    4: {"id": 4, "nome": "Cabo HDMI", "preco": Decimal("25.00")},
}

CUPONS = {"GQS10": Decimal("0.10")}

VALOR_FRETE = Decimal("15.00")
LIMITE_FRETE_GRATIS = Decimal("200.00")
QUANTIDADE_MINIMA = 1
QUANTIDADE_MAXIMA = 10


class ProdutoNaoEncontrado(Exception):
    """RN05 - o produto informado nao existe no catalogo."""


class CarrinhoInvalido(ValueError):
    """Entrada que viola RN02 ou RN04."""


def _centavos(valor):
    return valor.quantize(Decimal("0.01"), rounding=ROUND_HALF_UP)


def buscar_produto(produto_id):
    """RN05 - devolve o produto ou levanta ProdutoNaoEncontrado."""
    produto = PRODUTOS.get(produto_id)
    if produto is None:
        raise ProdutoNaoEncontrado(f"Produto {produto_id} nao encontrado")
    return produto


def validar_quantidade(quantidade):
    """RN04 - quantidade inteira entre 1 e 10."""
    if not isinstance(quantidade, int) or isinstance(quantidade, bool):
        raise CarrinhoInvalido("Quantidade deve ser um numero inteiro")
    # DEFEITO PLANTADO #2 (BUG-2): o limite inferior deveria ser
    # QUANTIDADE_MINIMA (1). Com "< 0", a quantidade 0 passa na validacao.
    if quantidade < 0 or quantidade > QUANTIDADE_MAXIMA:
        raise CarrinhoInvalido(
            f"Quantidade deve estar entre {QUANTIDADE_MINIMA} e {QUANTIDADE_MAXIMA}"
        )


def calcular_subtotal(itens):
    """RN01 - itens e' uma lista de {"produto_id": int, "quantidade": int}."""
    subtotal = Decimal("0")
    for item in itens:
        validar_quantidade(item["quantidade"])
        produto = buscar_produto(item["produto_id"])
        subtotal += produto["preco"] * item["quantidade"]
    return _centavos(subtotal)


def calcular_desconto(subtotal, cupom=None):
    """RN02 - desconto do cupom; sem cupom, desconto zero."""
    if not cupom:
        return Decimal("0.00")
    percentual = CUPONS.get(cupom.strip().upper())
    if percentual is None:
        raise CarrinhoInvalido(f"Cupom '{cupom}' invalido")
    return _centavos(subtotal * percentual)


def calcular_frete(valor_com_desconto):
    """RN03 - frete gratis a partir de R$ 200,00 (inclusive)."""
    # DEFEITO PLANTADO #1 (BUG-1): a regra diz "maior OU IGUAL".
    # Com ">", um carrinho de exatamente R$ 200,00 paga frete.
    if valor_com_desconto > LIMITE_FRETE_GRATIS:
        return Decimal("0.00")
    return VALOR_FRETE


def calcular_carrinho(itens, cupom=None):
    """Junta RN01..RN05 e devolve o resumo do pedido."""
    if not itens:
        raise CarrinhoInvalido("O carrinho deve ter ao menos um item")
    subtotal = calcular_subtotal(itens)
    desconto = calcular_desconto(subtotal, cupom)
    com_desconto = subtotal - desconto
    frete = calcular_frete(com_desconto)
    return {
        "subtotal": subtotal,
        "desconto": desconto,
        "frete": frete,
        "total": _centavos(com_desconto + frete),
    }
