#!/usr/bin/env python3
"""Matriz de rastreabilidade: caso de teste -> testes que o automatizam.

Lê `casos-de-teste.md` (IDs CT-xx e requisitos), os testes JUnit (método `ctNN_...`)
e a coleção Postman (requests com título `CT-NN · ...`). Falha (código 1) se:
  - um caso não tem nenhum teste automatizado;
  - um teste ou request cita um CT que não existe no documento.
"""
import json
import re
import sys
from collections import defaultdict
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent


def casos() -> dict[str, str]:
    """{CT-01: 'RF-01, RF-02', ...} a partir da tabela do documento."""
    achados = {}
    for linha in (RAIZ / "casos-de-teste.md").read_text(encoding="utf-8").splitlines():
        m = re.match(r"\|\s*\*\*(CT-\d\d)\*\*\s*\|\s*([^|]+)\|", linha)
        if m:
            achados[m.group(1)] = m.group(2).strip()
    return achados


def testes_junit() -> dict[str, list[str]]:
    por_caso = defaultdict(list)
    for arq in sorted((RAIZ / "src" / "test" / "java").rglob("*Test.java")):
        for nome in re.findall(r"void (ct\d\d\w*)\(", arq.read_text(encoding="utf-8")):
            por_caso[f"CT-{nome[2:4]}"].append(f"{arq.stem}::{nome}")
    return por_caso


def requests_postman() -> dict[str, list[str]]:
    colecao = json.loads((RAIZ / "postman" / "carona.postman_collection.json").read_text(encoding="utf-8"))
    por_caso = defaultdict(list)

    def percorrer(itens):
        for it in itens:
            if "item" in it:
                percorrer(it["item"])
            else:
                for ct in set(re.findall(r"CT-\d\d", it["name"])):
                    por_caso[ct].append(it["name"])
    percorrer(colecao["item"])
    return por_caso


def main() -> int:
    doc, py, pm = casos(), testes_junit(), requests_postman()
    print("| Caso | Requisitos | Testes JUnit | Requests Postman |\n|---|---|---:|---:|")
    erros = []
    for ct, req in doc.items():
        print(f"| {ct} | {req} | {len(py.get(ct, []))} | {len(pm.get(ct, []))} |")
        if not py.get(ct) and not pm.get(ct):
            erros.append(f"{ct} sem nenhum teste automatizado")
    for origem, mapa in (("JUnit", py), ("Postman", pm)):
        for ct in mapa:
            if ct not in doc:
                erros.append(f"{origem} cita {ct}, que não existe em casos-de-teste.md")
    total_py = sum(len(v) for v in py.values())
    print(f"\nCasos: {len(doc)} · métodos JUnit ligados a casos: {total_py} · requests Postman ligados: "
          f"{sum(len(v) for v in pm.values())}")
    for e in erros:
        print("ERRO:", e, file=sys.stderr)
    return 1 if erros else 0


if __name__ == "__main__":
    sys.exit(main())
