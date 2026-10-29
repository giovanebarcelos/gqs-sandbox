"""
API REST da Biblioteca (Flask) - VERSAO CORRIGIDA (gabarito da Etapa 10).

Copie para python/biblioteca/app.py. Mudancas: o token do relatorio vem da
variavel de ambiente TOKEN_ADMIN e o literal "text/plain" virou constante.

Mesmas rotas da versao Java.

    GET  /api/health                           -> {"status": "ok", "versao": "..."}
    GET  /api/livros                           -> {"disponiveis": [...], "emprestados": [...]}
    POST /api/emprestimos  {titulo, matricula} -> 201 {"codigo", "titulo"} | 404
    POST /api/devolucoes   {titulo}            -> 200 | 404
    GET  /api/multa?dias=&tipo=&raro=          -> {"multa": 0.0}
    GET  /api/relatorio?formato=texto|csv      -> texto; exige o cabecalho X-Admin-Token
"""

import os

from flask import Flask, Response, jsonify, request

from biblioteca.regras import Biblioteca, LivroNaoEncontrado

VERSAO = os.environ.get("APP_VERSION", "1.0.0")
TEXTO = "text/plain"


def criar_app(biblioteca=None):
    app = Flask(__name__)
    app.json.sort_keys = False
    bib = biblioteca or Biblioteca(os.environ.get("TOKEN_ADMIN", ""))

    @app.get("/api/health")
    def health():
        return jsonify({"status": "ok", "versao": VERSAO})

    @app.get("/api/livros")
    def livros():
        return jsonify({"disponiveis": bib.acervo, "emprestados": bib.emprestados})

    @app.post("/api/emprestimos")
    def emprestar():
        corpo = request.get_json(silent=True) or {}
        codigo = bib.emprestar(corpo.get("titulo"), corpo.get("matricula"))
        return jsonify({"codigo": codigo, "titulo": corpo.get("titulo")}), 201

    @app.post("/api/devolucoes")
    def devolver():
        corpo = request.get_json(silent=True) or {}
        bib.devolver(corpo.get("titulo"))
        return jsonify({"mensagem": f"Devolucao registrada: {corpo.get('titulo')}"})

    @app.get("/api/multa")
    def multa():
        dias = bib.ler_dias(request.args.get("dias", ""))
        raro = request.args.get("raro", "false").lower() == "true"
        return jsonify({"multa": bib.calcular_multa(dias, request.args.get("tipo", ""), raro)})

    @app.get("/api/relatorio")
    def relatorio():
        if not bib.token_valido(request.headers.get("X-Admin-Token")):
            return Response("Token invalido", status=401, mimetype=TEXTO)
        if request.args.get("formato") == "csv":
            return Response(bib.relatorio_csv(), mimetype=TEXTO)
        return Response(bib.relatorio_texto(), mimetype=TEXTO)

    @app.errorhandler(LivroNaoEncontrado)
    def nao_encontrado(erro):
        return jsonify({"erro": str(erro)}), 404

    @app.errorhandler(ValueError)
    def invalido(erro):
        return jsonify({"erro": str(erro)}), 400

    return app


app = criar_app()

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
