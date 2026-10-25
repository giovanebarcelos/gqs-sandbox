#!/usr/bin/env python3
"""Mede o RNF-01: GET /viagens com 1.000 viagens deve responder em <= 300 ms (p95).

Roda em processo (cliente de teste do Flask) sobre um SQLite temporário: mede serviço +
banco + serialização, sem a rede. Uso: python scripts/medir_desempenho.py
"""
import statistics
import sys
import tempfile
import time
from datetime import datetime, timedelta
from decimal import Decimal
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent / "src"))

from carona.api import RelogioAjustavel, criar_app, hash_senha  # noqa: E402
from carona.repositorio import RepositorioSqlite  # noqa: E402
from carona.servico import ServicoCarona  # noqa: E402

LIMITE_MS, VIAGENS, REPETICOES = 300, 1000, 200


def p95(amostras: list[float]) -> float:
    return statistics.quantiles(amostras, n=100)[94]


def main() -> int:
    agora = datetime(2026, 11, 10, 12, 0, 0)
    with tempfile.TemporaryDirectory() as tmp:
        repo = RepositorioSqlite(str(Path(tmp) / "perf.db"))
        repo.criar_usuario("1", "Ana", hash_senha("a"))
        relogio = RelogioAjustavel(base=lambda: agora)
        servico = ServicoCarona(repo, relogio)
        for i in range(VIAGENS):
            servico.criar_viagem(1, "Campus", f"Destino {i}", agora + timedelta(hours=1 + i % 300), 3, Decimal("12.00"))
        cliente = criar_app(servico, relogio).test_client()
        token = cliente.post("/auth/login", json={"matricula": "1", "senha": "a"}).get_json()["token"]
        cab = {"Authorization": f"Bearer {token}"}
        amostras = []
        for _ in range(REPETICOES):
            t0 = time.perf_counter()
            r = cliente.get("/viagens", headers=cab)
            amostras.append((time.perf_counter() - t0) * 1000)
            assert r.status_code == 200 and len(r.get_json()) == VIAGENS
    print(f"GET /viagens com {VIAGENS} viagens, {REPETICOES} repetições")
    print(f"  mediana {statistics.median(amostras):.1f} ms · p95 {p95(amostras):.1f} ms · máximo {max(amostras):.1f} ms")
    ok = p95(amostras) <= LIMITE_MS
    print(f"  RNF-01 (p95 <= {LIMITE_MS} ms): {'ATENDIDO' if ok else 'NÃO ATENDIDO'}")
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
