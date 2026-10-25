"""Fixtures comuns: relógio controlável, serviço sobre SQLite temporário e cliente HTTP."""
from datetime import datetime, timedelta
from decimal import Decimal

import pytest

from carona.api import RelogioAjustavel, criar_app, hash_senha
from carona.repositorio import RepositorioSqlite
from carona.servico import ServicoCarona

AGORA = datetime(2026, 11, 10, 12, 0, 0)


class RelogioFixo:
    """Relógio de teste: o tempo só anda quando o teste manda."""

    def __init__(self, agora: datetime = AGORA):
        self.agora = agora

    def __call__(self) -> datetime:
        return self.agora

    def avancar(self, **kw) -> None:
        self.agora += timedelta(**kw)


@pytest.fixture
def relogio():
    return RelogioFixo()


@pytest.fixture
def repo(tmp_path):
    r = RepositorioSqlite(str(tmp_path / "carona.db"))
    for mat, nome, senha in [("1", "Ana", "a"), ("2", "Bruno", "b"), ("3", "Carla", "c"),
                             ("4", "Davi", "d"), ("5", "Eva", "e"), ("6", "Fabio", "f"),
                             ("7", "Gil", "g")]:
        r.criar_usuario(mat, nome, hash_senha(senha))
    return r


@pytest.fixture
def servico(repo, relogio):
    return ServicoCarona(repo, relogio)


@pytest.fixture
def viagem(servico, relogio):
    """Viagem da Ana (id 1) daqui a 3 h, 3 vagas, custo R$ 12,00 -> R$ 3,00 por vaga."""
    return servico.criar_viagem(1, "Campus", "Centro", relogio() + timedelta(hours=3), 3, Decimal("12.00"))


@pytest.fixture
def cliente(repo, relogio):
    """Cliente HTTP do Flask sobre o mesmo repositório; relógio do servidor = relogio de teste."""
    ajustavel = RelogioAjustavel(base=relogio)
    app = criar_app(ServicoCarona(repo, ajustavel), ajustavel, modo_teste=True)
    return app.test_client()


def login(cliente, matricula: str, senha: str) -> dict:
    r = cliente.post("/auth/login", json={"matricula": matricula, "senha": senha})
    assert r.status_code == 200, r.get_data(as_text=True)
    return {"Authorization": f"Bearer {r.get_json()['token']}"}
