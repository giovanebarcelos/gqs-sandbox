"""Porta de persistência e adaptador SQLite.

O serviço depende só do `RepositorioCarona` (um `Protocol`): nos testes
unitários ele é trocado por um dublê; nos de integração, pelo SQLite real.
"""
from __future__ import annotations

import sqlite3
from collections.abc import Iterator
from contextlib import contextmanager
from datetime import datetime
from decimal import Decimal
from typing import Protocol

from .dominio import ConflitoDeRegra, Reserva, Usuario, Viagem

ESQUEMA = """
CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    matricula TEXT NOT NULL UNIQUE,
    nome TEXT NOT NULL,
    senha_hash TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS viagens (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    motorista_id INTEGER NOT NULL REFERENCES usuarios(id),
    origem TEXT NOT NULL,
    destino TEXT NOT NULL,
    saida TEXT NOT NULL,
    vagas INTEGER NOT NULL CHECK (vagas BETWEEN 1 AND 6),
    custo_total TEXT NOT NULL,
    cancelada INTEGER NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS reservas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    viagem_id INTEGER NOT NULL REFERENCES viagens(id),
    passageiro_id INTEGER NOT NULL REFERENCES usuarios(id),
    valor TEXT NOT NULL,
    cancelada INTEGER NOT NULL DEFAULT 0,
    taxa TEXT NOT NULL DEFAULT '0.00'
);
CREATE TABLE IF NOT EXISTS avaliacoes (
    viagem_id INTEGER NOT NULL REFERENCES viagens(id),
    passageiro_id INTEGER NOT NULL REFERENCES usuarios(id),
    nota INTEGER NOT NULL CHECK (nota BETWEEN 1 AND 5),
    PRIMARY KEY (viagem_id, passageiro_id)
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_reserva_ativa
    ON reservas (viagem_id, passageiro_id) WHERE cancelada = 0;
"""


class RepositorioCarona(Protocol):
    def criar_usuario(self, matricula: str, nome: str, senha_hash: str) -> Usuario: ...
    def usuario_por_matricula(self, matricula: str) -> tuple[Usuario, str] | None: ...
    def obter_usuario(self, usuario_id: int) -> Usuario | None: ...

    def inserir_viagem(self, viagem: Viagem) -> Viagem: ...
    def obter_viagem(self, viagem_id: int) -> Viagem | None: ...
    def listar_viagens(self) -> list[Viagem]: ...
    def atualizar_viagem(self, viagem: Viagem) -> None: ...

    def reservar_se_ha_vaga(self, reserva: Reserva, vagas: int) -> Reserva | None: ...
    def obter_reserva(self, reserva_id: int) -> Reserva | None: ...
    def reservas_ativas(self, viagem_id: int) -> list[Reserva]: ...
    def atualizar_reserva(self, reserva: Reserva) -> None: ...

    def inserir_avaliacao(self, viagem_id: int, passageiro_id: int, nota: int) -> None: ...
    def avaliacao_existe(self, viagem_id: int, passageiro_id: int) -> bool: ...
    def notas_do_motorista(self, motorista_id: int) -> list[int]: ...
    def ocupacao_por_viagem(self) -> list[tuple[int, int, int]]: ...


class RepositorioSqlite:
    """Implementação em SQLite; abre uma conexão por operação (segura entre threads)."""

    def __init__(self, caminho: str):
        self.caminho = caminho
        with self._conexao() as con:
            con.executescript(ESQUEMA)

    @contextmanager
    def _conexao(self) -> Iterator[sqlite3.Connection]:
        """Abre, confirma (ou desfaz) e FECHA. `with sqlite3.connect()` sozinho não fecha (DEF-002)."""
        con = sqlite3.connect(self.caminho, timeout=10)
        con.row_factory = sqlite3.Row
        con.execute("PRAGMA foreign_keys = ON")
        try:
            with con:
                yield con
        finally:
            con.close()

    # -- usuários -------------------------------------------------------
    def criar_usuario(self, matricula: str, nome: str, senha_hash: str) -> Usuario:
        with self._conexao() as con:
            cur = con.execute(
                "INSERT INTO usuarios (matricula, nome, senha_hash) VALUES (?, ?, ?)",
                (matricula, nome, senha_hash))
            return Usuario(cur.lastrowid, matricula, nome)

    def usuario_por_matricula(self, matricula: str) -> tuple[Usuario, str] | None:
        with self._conexao() as con:
            r = con.execute("SELECT * FROM usuarios WHERE matricula = ?", (matricula,)).fetchone()
        return (Usuario(r["id"], r["matricula"], r["nome"]), r["senha_hash"]) if r else None

    def obter_usuario(self, usuario_id: int) -> Usuario | None:
        with self._conexao() as con:
            r = con.execute("SELECT * FROM usuarios WHERE id = ?", (usuario_id,)).fetchone()
        return Usuario(r["id"], r["matricula"], r["nome"]) if r else None

    # -- viagens --------------------------------------------------------
    @staticmethod
    def _viagem(r: sqlite3.Row) -> Viagem:
        return Viagem(r["id"], r["motorista_id"], r["origem"], r["destino"],
                      datetime.fromisoformat(r["saida"]), r["vagas"],
                      Decimal(r["custo_total"]), bool(r["cancelada"]))

    def inserir_viagem(self, v: Viagem) -> Viagem:
        with self._conexao() as con:
            cur = con.execute(
                "INSERT INTO viagens (motorista_id, origem, destino, saida, vagas, custo_total)"
                " VALUES (?, ?, ?, ?, ?, ?)",
                (v.motorista_id, v.origem, v.destino, v.saida.isoformat(), v.vagas, str(v.custo_total)))
            v.id = cur.lastrowid
        return v

    def obter_viagem(self, viagem_id: int) -> Viagem | None:
        with self._conexao() as con:
            r = con.execute("SELECT * FROM viagens WHERE id = ?", (viagem_id,)).fetchone()
        return self._viagem(r) if r else None

    def listar_viagens(self) -> list[Viagem]:
        with self._conexao() as con:
            rs = con.execute("SELECT * FROM viagens WHERE cancelada = 0 ORDER BY saida, id").fetchall()
        return [self._viagem(r) for r in rs]

    def atualizar_viagem(self, v: Viagem) -> None:
        with self._conexao() as con:
            con.execute(
                "UPDATE viagens SET origem=?, destino=?, saida=?, vagas=?, custo_total=?, cancelada=?"
                " WHERE id=?",
                (v.origem, v.destino, v.saida.isoformat(), v.vagas, str(v.custo_total),
                 int(v.cancelada), v.id))

    # -- reservas -------------------------------------------------------
    @staticmethod
    def _reserva(r: sqlite3.Row) -> Reserva:
        return Reserva(r["id"], r["viagem_id"], r["passageiro_id"], Decimal(r["valor"]),
                       bool(r["cancelada"]), Decimal(r["taxa"]))

    def reservar_se_ha_vaga(self, rv: Reserva, vagas: int) -> Reserva | None:
        """Conta as reservas ativas e insere NA MESMA transação (`BEGIN IMMEDIATE`).

        Devolve None se a viagem já está lotada. Sem isso, duas requisições
        simultâneas leem "há 1 vaga" e as duas gravam (DEF-001).
        """
        con = sqlite3.connect(self.caminho, timeout=10, isolation_level=None)
        try:
            con.execute("BEGIN IMMEDIATE")
            ativas = con.execute("SELECT COUNT(*) FROM reservas WHERE viagem_id = ? AND cancelada = 0",
                                 (rv.viagem_id,)).fetchone()[0]
            if ativas >= vagas:
                con.execute("ROLLBACK")
                return None
            cur = con.execute(
                "INSERT INTO reservas (viagem_id, passageiro_id, valor) VALUES (?, ?, ?)",
                (rv.viagem_id, rv.passageiro_id, str(rv.valor)))
            con.execute("COMMIT")
            rv.id = cur.lastrowid
            return rv
        except sqlite3.IntegrityError:
            con.execute("ROLLBACK")
            raise ConflitoDeRegra("passageiro já tem reserva nesta viagem") from None
        finally:
            con.close()

    def obter_reserva(self, reserva_id: int) -> Reserva | None:
        with self._conexao() as con:
            r = con.execute("SELECT * FROM reservas WHERE id = ?", (reserva_id,)).fetchone()
        return self._reserva(r) if r else None

    def reservas_ativas(self, viagem_id: int) -> list[Reserva]:
        with self._conexao() as con:
            rs = con.execute("SELECT * FROM reservas WHERE viagem_id = ? AND cancelada = 0 ORDER BY id",
                             (viagem_id,)).fetchall()
        return [self._reserva(r) for r in rs]

    def atualizar_reserva(self, rv: Reserva) -> None:
        with self._conexao() as con:
            con.execute("UPDATE reservas SET cancelada=?, taxa=? WHERE id=?",
                        (int(rv.cancelada), str(rv.taxa), rv.id))

    # -- avaliações e relatório ----------------------------------------
    def inserir_avaliacao(self, viagem_id: int, passageiro_id: int, nota: int) -> None:
        with self._conexao() as con:
            con.execute("INSERT INTO avaliacoes (viagem_id, passageiro_id, nota) VALUES (?, ?, ?)",
                        (viagem_id, passageiro_id, nota))

    def avaliacao_existe(self, viagem_id: int, passageiro_id: int) -> bool:
        with self._conexao() as con:
            r = con.execute("SELECT 1 FROM avaliacoes WHERE viagem_id=? AND passageiro_id=?",
                            (viagem_id, passageiro_id)).fetchone()
        return r is not None

    def notas_do_motorista(self, motorista_id: int) -> list[int]:
        with self._conexao() as con:
            rs = con.execute(
                "SELECT a.nota FROM avaliacoes a JOIN viagens v ON v.id = a.viagem_id"
                " WHERE v.motorista_id = ? ORDER BY a.rowid", (motorista_id,)).fetchall()
        return [r["nota"] for r in rs]

    def ocupacao_por_viagem(self) -> list[tuple[int, int, int]]:
        """(viagem_id, vagas, reservas ativas) das viagens não canceladas."""
        with self._conexao() as con:
            rs = con.execute(
                "SELECT v.id, v.vagas, COUNT(r.id) AS ocupadas FROM viagens v"
                " LEFT JOIN reservas r ON r.viagem_id = v.id AND r.cancelada = 0"
                " WHERE v.cancelada = 0 GROUP BY v.id ORDER BY v.id").fetchall()
        return [(r["id"], r["vagas"], r["ocupadas"]) for r in rs]
