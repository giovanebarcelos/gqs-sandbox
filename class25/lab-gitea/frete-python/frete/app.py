"""
API REST de frete da LojaOnline (Flask).

    GET  /api/health    -> {"status": "ok", "versao": "..."}
    GET  /api/regioes   -> regioes atendidas, com frete base e prazo
    POST /api/frete     -> {"regiao": "SUL", "pesoKg": 2.0, "valorPedido": 100.00}

Entrada invalida (regiao, peso, valor ou JSON) responde 400 com {"erro": "..."}.
"""

import os
from decimal import Decimal, InvalidOperation

from flask import Flask, jsonify, request

from frete.calculadora import REGIOES, calcular_frete

# O pipeline publica cada build com APP_VERSION = 7 primeiros caracteres do commit
VERSAO = os.environ.get("APP_VERSION", "dev")

app = Flask(__name__)
app.json.sort_keys = False


@app.get("/api/health")
def health():
    return jsonify({"status": "ok", "versao": VERSAO})


@app.get("/api/regioes")
def regioes():
    return jsonify(
        [
            {"regiao": nome, "freteBase": float(base), "prazoDias": prazo}
            for nome, (base, prazo) in REGIOES.items()
        ]
    )


@app.post("/api/frete")
def frete():
    corpo = request.get_json(silent=True)
    if not isinstance(corpo, dict):
        return jsonify({"erro": "Envie um JSON com regiao, pesoKg e valorPedido"}), 400
    try:
        peso = float(corpo["pesoKg"]) if corpo.get("pesoKg") is not None else None
        valor = Decimal(str(corpo["valorPedido"])) if corpo.get("valorPedido") is not None else None
        cotacao = calcular_frete(corpo.get("regiao"), peso, valor)
    except (ValueError, InvalidOperation) as erro:
        return jsonify({"erro": str(erro)}), 400
    return jsonify(
        {
            **cotacao,
            "valorPedido": float(cotacao["valorPedido"]),
            "frete": float(cotacao["frete"]),
        }
    )


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
