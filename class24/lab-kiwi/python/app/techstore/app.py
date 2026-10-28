"""
API REST da TechStore (Flask). Traduz HTTP para as regras de carrinho.py.

Endpoints
    GET  /api/health                 -> {"status": "ok", "versao": "..."}
    GET  /api/produtos               -> lista do catalogo
    GET  /api/produtos/<id>          -> um produto (404 se nao existir)
    POST /api/carrinho/calcular      -> subtotal, desconto, frete e total

Codigos de erro
    400  entrada invalida (quantidade fora de 1..10, cupom invalido, JSON ruim)
    404  produto inexistente
"""

import os

from flask import Flask, jsonify, request

from techstore.carrinho import (
    PRODUTOS,
    CarrinhoInvalido,
    ProdutoNaoEncontrado,
    buscar_produto,
    calcular_carrinho,
)

VERSAO = os.environ.get("APP_VERSION", "1.0.0")

app = Flask(__name__)
app.json.sort_keys = False


def _produto_json(produto):
    return {**produto, "preco": float(produto["preco"])}


@app.get("/")
def raiz():
    return jsonify(
        {
            "aplicacao": "TechStore - API de Carrinho",
            "versao": VERSAO,
            "endpoints": [
                "GET /api/health",
                "GET /api/produtos",
                "GET /api/produtos/<id>",
                "POST /api/carrinho/calcular",
            ],
        }
    )


@app.get("/api/health")
def health():
    return jsonify({"status": "ok", "versao": VERSAO})


@app.get("/api/produtos")
def listar_produtos():
    return jsonify([_produto_json(p) for p in PRODUTOS.values()])


@app.get("/api/produtos/<int:produto_id>")
def obter_produto(produto_id):
    try:
        return jsonify(_produto_json(buscar_produto(produto_id)))
    except ProdutoNaoEncontrado as erro:
        return jsonify({"erro": str(erro)}), 404


@app.post("/api/carrinho/calcular")
def calcular():
    corpo = request.get_json(silent=True)
    if not isinstance(corpo, dict) or not isinstance(corpo.get("itens"), list):
        return jsonify({"erro": "Envie um JSON com a lista 'itens'"}), 400
    try:
        itens = [
            {"produto_id": int(i["produto_id"]), "quantidade": i["quantidade"]}
            for i in corpo["itens"]
        ]
        resumo = calcular_carrinho(itens, corpo.get("cupom"))
    except (KeyError, TypeError, ValueError) as erro:
        # CarrinhoInvalido herda de ValueError
        return jsonify({"erro": str(erro)}), 400
    except ProdutoNaoEncontrado as erro:
        return jsonify({"erro": str(erro)}), 404
    return jsonify({chave: float(valor) for chave, valor in resumo.items()})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
