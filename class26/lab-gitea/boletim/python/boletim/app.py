"""
API REST do boletim (Flask).

    GET  /api/health                 -> {"status": "ok", "versao": "...", "instituicao": "..."}
    POST /api/boletim                -> {"aluno": "Ana", "notas": [7.5, 8.0], "frequencia": 80}
    GET  /api/admin/estatisticas     -> exige o cabecalho X-Admin-Token (secret TOKEN_ADMIN)
"""

import hmac
import os

from flask import Flask, jsonify, request

from boletim.calculadora import calcular_boletim

# Preenchidos no deploy: versao = tag do release; instituicao = variavel do
# Environment; TOKEN_ADMIN = secret do Environment (nunca no repositorio).
VERSAO = os.environ.get("APP_VERSION", "dev")
INSTITUICAO = os.environ.get("INSTITUICAO", "nao configurada")

app = Flask(__name__)
app.json.sort_keys = False
app.config["CALCULADOS"] = 0


@app.get("/api/health")
def health():
    return jsonify({"status": "ok", "versao": VERSAO, "instituicao": INSTITUICAO})


@app.post("/api/boletim")
def boletim():
    corpo = request.get_json(silent=True)
    if not isinstance(corpo, dict):
        return jsonify({"erro": "Envie um JSON com aluno, notas e frequencia"}), 400
    try:
        resultado = calcular_boletim(corpo.get("aluno"), corpo.get("notas"), corpo.get("frequencia"))
    except ValueError as erro:
        return jsonify({"erro": str(erro)}), 400
    app.config["CALCULADOS"] += 1
    return jsonify({**resultado, "media": float(resultado["media"])})


@app.get("/api/admin/estatisticas")
def estatisticas():
    # Sem TOKEN_ADMIN configurado, ninguem entra: token vazio nunca e' valido.
    esperado = os.environ.get("TOKEN_ADMIN", "")
    recebido = request.headers.get("X-Admin-Token", "")
    if not esperado or not hmac.compare_digest(esperado, recebido):
        return jsonify({"erro": "Token invalido"}), 401
    return jsonify({"boletinsCalculados": app.config["CALCULADOS"], "versao": VERSAO})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
