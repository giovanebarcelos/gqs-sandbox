"""API HTTP (Flask) do CaronaCampus. Só traduz HTTP <-> serviço; sem regra de negócio."""
from __future__ import annotations

import hashlib
import hmac
import secrets
from datetime import datetime, timedelta, timezone
from decimal import Decimal, InvalidOperation

from flask import Flask, jsonify, request

from .dominio import (
    ConflitoDeRegra, DadoInvalido, ErroDeDominio, MotoristaSuspenso, NaoEncontrado, SemPermissao,
)
from .servico import ServicoCarona

STATUS = {
    DadoInvalido: 400, NaoEncontrado: 404, SemPermissao: 403,
    ConflitoDeRegra: 409, MotoristaSuspenso: 403,
}


def hash_senha(senha: str, sal: str = "carona-campus") -> str:
    """PBKDF2 com sal fixo: suficiente para a demo (ver riscos no relatório)."""
    return hashlib.pbkdf2_hmac("sha256", senha.encode(), sal.encode(), 50_000).hex()


def _viagem_json(v) -> dict:
    return {"id": v.id, "motorista_id": v.motorista_id, "origem": v.origem, "destino": v.destino,
            "saida": v.saida.isoformat(), "vagas": v.vagas, "custo_total": str(v.custo_total),
            "valor_por_vaga": str(v.valor_por_vaga)}


def _reserva_json(r) -> dict:
    return {"id": r.id, "viagem_id": r.viagem_id, "passageiro_id": r.passageiro_id,
            "valor": str(r.valor), "cancelada": r.cancelada, "taxa": str(r.taxa)}


def _dinheiro(valor) -> Decimal:
    try:
        return Decimal(str(valor))
    except InvalidOperation:
        raise DadoInvalido("valor monetário inválido") from None


def _data(texto) -> datetime:
    try:
        return datetime.fromisoformat(str(texto))
    except ValueError:
        raise DadoInvalido("data inválida; use AAAA-MM-DDTHH:MM:SS") from None


class RelogioAjustavel:
    """Relógio do servidor; em modo de teste aceita avanço manual (POST /teste/avancar-relogio)."""

    def __init__(self, base=lambda: datetime.now(timezone.utc).replace(tzinfo=None)):
        self.base = base
        self.deslocamento = timedelta(0)

    def __call__(self) -> datetime:
        return self.base().replace(microsecond=0) + self.deslocamento


def criar_app(servico: ServicoCarona, relogio: RelogioAjustavel | None = None,
              modo_teste: bool = False) -> Flask:
    app = Flask(__name__)
    tokens: dict[str, int] = {}

    @app.errorhandler(ErroDeDominio)
    def _erro(e: ErroDeDominio):
        return jsonify({"erro": e.codigo, "mensagem": e.mensagem}), STATUS.get(type(e), 400)

    def autenticado() -> int:
        cab = request.headers.get("Authorization", "")
        if not cab.startswith("Bearer ") or cab[7:] not in tokens:
            raise _NaoAutenticado()
        return tokens[cab[7:]]

    class _NaoAutenticado(Exception):
        pass

    @app.errorhandler(_NaoAutenticado)
    def _401(_):
        return jsonify({"erro": "nao_autenticado", "mensagem": "token ausente ou inválido"}), 401

    def corpo() -> dict:
        dados = request.get_json(silent=True)
        if not isinstance(dados, dict):
            raise DadoInvalido("corpo JSON obrigatório")
        return dados

    @app.get("/saude")
    def saude():
        return jsonify({"status": "ok"})

    @app.post("/auth/login")
    def login():
        d = corpo()
        achado = servico.repo.usuario_por_matricula(str(d.get("matricula", "")))
        if not achado or not hmac.compare_digest(achado[1], hash_senha(str(d.get("senha", "")))):
            return jsonify({"erro": "nao_autenticado", "mensagem": "matrícula ou senha inválida"}), 401
        token = secrets.token_hex(16)
        tokens[token] = achado[0].id
        return jsonify({"token": token, "usuario_id": achado[0].id, "nome": achado[0].nome})

    @app.post("/viagens")
    def criar_viagem():
        uid, d = autenticado(), corpo()
        v = servico.criar_viagem(uid, d.get("origem"), d.get("destino"), _data(d.get("saida")),
                                 d.get("vagas"), _dinheiro(d.get("custo_total")))
        return jsonify(_viagem_json(v)), 201

    @app.get("/viagens")
    def listar_viagens():
        autenticado()
        return jsonify([_viagem_json(v) for v in servico.listar_viagens()])

    @app.get("/viagens/<int:viagem_id>")
    def obter_viagem(viagem_id: int):
        autenticado()
        return jsonify(_viagem_json(servico.obter_viagem(viagem_id)))

    @app.put("/viagens/<int:viagem_id>")
    def atualizar_viagem(viagem_id: int):
        uid, d = autenticado(), corpo()
        campos = {k: d[k] for k in ("origem", "destino", "vagas") if k in d}
        if "custo_total" in d:
            campos["custo_total"] = _dinheiro(d["custo_total"])
        if "saida" in d:
            campos["saida"] = _data(d["saida"])
        return jsonify(_viagem_json(servico.atualizar_viagem(viagem_id, uid, **campos)))

    @app.delete("/viagens/<int:viagem_id>")
    def excluir_viagem(viagem_id: int):
        servico.excluir_viagem(viagem_id, autenticado())
        return "", 204

    @app.post("/viagens/<int:viagem_id>/reservas")
    def reservar(viagem_id: int):
        return jsonify(_reserva_json(servico.reservar(viagem_id, autenticado()))), 201

    @app.delete("/reservas/<int:reserva_id>")
    def cancelar_reserva(reserva_id: int):
        return jsonify(_reserva_json(servico.cancelar_reserva(reserva_id, autenticado())))

    @app.post("/viagens/<int:viagem_id>/avaliacoes")
    def avaliar(viagem_id: int):
        uid, d = autenticado(), corpo()
        servico.avaliar(viagem_id, uid, d.get("nota"))
        return "", 201

    @app.get("/motoristas/<int:motorista_id>/reputacao")
    def reputacao(motorista_id: int):
        autenticado()
        return jsonify(servico.reputacao(motorista_id))

    @app.get("/relatorios/ocupacao")
    def relatorio():
        autenticado()
        return jsonify(servico.relatorio_ocupacao())

    if modo_teste and relogio is not None:
        @app.post("/teste/avancar-relogio")
        def avancar():
            minutos = corpo().get("minutos", 0)
            relogio.deslocamento += timedelta(minutes=int(minutos))
            return jsonify({"agora": relogio().isoformat()})

    return app
