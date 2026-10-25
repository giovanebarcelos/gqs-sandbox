"""Ponto de entrada: monta as peças, popula usuários de demonstração e sobe o servidor."""
from __future__ import annotations

import os

from .api import RelogioAjustavel, criar_app, hash_senha
from .repositorio import RepositorioSqlite
from .servico import ServicoCarona

# Usuários fictícios da demonstração (nenhum dado real).
USUARIOS_DEMO = [("2026001", "Ana Motorista", "ana123"), ("2026002", "Bruno Passageiro", "bruno123"),
                 ("2026003", "Carla Passageira", "carla123")]


def montar(caminho_db: str, modo_teste: bool = False):
    repo = RepositorioSqlite(caminho_db)
    for matricula, nome, senha in USUARIOS_DEMO:
        if repo.usuario_por_matricula(matricula) is None:
            repo.criar_usuario(matricula, nome, hash_senha(senha))
    relogio = RelogioAjustavel()
    return criar_app(ServicoCarona(repo, relogio), relogio, modo_teste)


def main() -> None:  # pragma: no cover  (sobe o servidor; coberto pela execução no compose)
    app = montar(os.environ.get("CARONA_DB", "carona.db"),
                 os.environ.get("CARONA_MODO_TESTE") == "1")
    app.run(host="0.0.0.0", port=int(os.environ.get("PORT", "5000")))


if __name__ == "__main__":
    main()
