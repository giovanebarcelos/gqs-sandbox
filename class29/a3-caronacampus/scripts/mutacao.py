#!/usr/bin/env python3
"""Teste de mutação manual: aplica UM mutante por vez numa cópia do projeto e roda a suíte.

Uso: python scripts/mutacao.py            (precisa de pytest instalado)
Saída: tabela em Markdown; código de saída 1 se algum mutante NÃO equivalente sobreviver.
"""
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent

# (id, arquivo, trecho original, trecho mutado, o que a mutação simula)
MUTANTES = [
    ("M01", "dominio.py", "saida - agora >= LIMITE_SEM_TAXA", "saida - agora > LIMITE_SEM_TAXA", "limite de 60 min: >= vira >"),
    ("M02", "dominio.py", "if agora >= saida:", "if agora > saida:", "cancela na hora exata da saída"),
    ("M03", "dominio.py", "rounding=ROUND_CEILING", "rounding=ROUND_FLOOR", "preço arredonda para baixo"),
    ("M04", "dominio.py", "custo_total / (vagas + 1)", "custo_total / vagas", "esquece de contar o motorista"),
    ("M05", "dominio.py", "VAGAS_MIN, VAGAS_MAX = 1, 6", "VAGAS_MIN, VAGAS_MAX = 1, 7", "máximo de vagas 6 -> 7"),
    ("M06", "dominio.py", "saida < agora + ANTECEDENCIA_MIN", "saida <= agora + ANTECEDENCIA_MIN", "antecedência de 30 min exclusiva"),
    ("M07", "dominio.py", "saida > agora + JANELA_MAX", "saida >= agora + JANELA_MAX", "janela de 14 dias exclusiva"),
    ("M08", "dominio.py", "sum(notas) < MEDIA_MINIMA * len(notas)", "sum(notas) <= MEDIA_MINIMA * len(notas)", "média exata 3,0 suspende"),
    ("M09", "dominio.py", "len(notas) < AVALIACOES_PARA_SUSPENDER", "len(notas) <= AVALIACOES_PARA_SUSPENDER", "só suspende com 6 avaliações"),
    ("M10", "dominio.py", 'TAXA_TARDIA = Decimal("0.50")', 'TAXA_TARDIA = Decimal("0.40")', "taxa de 50% -> 40%"),
    ("M11", "dominio.py", 'PRECO_MAX = Decimal("2.00"), Decimal("25.00")', 'PRECO_MAX = Decimal("2.00"), Decimal("26.00")', "teto de R$ 25 -> 26"),
    ("M12", "dominio.py", "not 1 <= nota <= 5", "not 1 < nota <= 5", "nota 1 vira inválida"),
    ("M13", "servico.py", "if len(ativas) >= v.vagas:", "if len(ativas) > v.vagas:", "lotação na checagem do serviço"),
    ("M14", "servico.py", "if v.motorista_id == passageiro_id:", "if v.motorista_id != passageiro_id:", "motorista reserva a própria viagem"),
    ("M15", "servico.py", "if not v.iniciada(self.relogio()):", "if False:", "avalia antes da saída"),
    ("M16", "servico.py", "if r.passageiro_id != usuario_id:", "if False:", "qualquer um cancela reserva alheia"),
    ("M17", "repositorio.py", "if ativas >= vagas:", "if ativas > vagas:", "lotação dentro da transação"),
    ("M18", "repositorio.py", "yield con\n        finally:\n            con.close()", "yield con\n        finally:\n            pass", "esquece de fechar a conexão"),
    ("M19", "api.py", "ConflitoDeRegra: 409", "ConflitoDeRegra: 400", "conflito devolve 400"),
    ("M20", "api.py", 'not cab.startswith("Bearer ") or cab[7:] not in tokens', "False", "autenticação desligada"),
    ("M21", "servico.py", "if not v.origem or not v.destino:", "if False:", "alteração aceita origem/destino vazios"),
]


def rodar(raiz: Path) -> tuple[bool, str]:
    """(sobreviveu?, primeiro teste que falhou). Sobreviver = a suíte passou mesmo com o defeito."""
    r = subprocess.run([sys.executable, "-m", "pytest", "-x", "-q", "-p", "no:cacheprovider"],
                       cwd=raiz, capture_output=True, text=True, timeout=300)
    m = re.search(r"^(?:FAILED|ERROR) \S*?([\w.]+\.py::\w+)", r.stdout, flags=re.M)
    return r.returncode == 0, (m.group(1) if m else "—")


def conferir_trechos() -> bool:
    """Antes de rodar, cada trecho original deve existir exatamente uma vez."""
    ok = True
    for mid, arq, orig, _, _ in MUTANTES:
        n = (RAIZ / "src" / "carona" / arq).read_text(encoding="utf-8").count(orig)
        if n != 1:
            print(f"{mid}: trecho aparece {n}x em {arq}", file=sys.stderr)
            ok = False
    return ok


def main() -> int:
    if not conferir_trechos():
        return 2
    linhas, sobreviventes = [], []
    for mid, arq, orig, mut, desc in MUTANTES:
        with tempfile.TemporaryDirectory() as tmp:
            copia = Path(tmp) / "proj"
            shutil.copytree(RAIZ, copia, ignore=shutil.ignore_patterns(".git", "__pycache__", ".venv", "*.db"))
            alvo = copia / "src" / "carona" / arq
            texto = alvo.read_text(encoding="utf-8")
            if texto.count(orig) != 1:
                print(f"{mid}: trecho original não encontrado exatamente uma vez em {arq}", file=sys.stderr)
                return 2
            alvo.write_text(texto.replace(orig, mut), encoding="utf-8")
            sobreviveu, quem = rodar(copia)
        linhas.append(f"| {mid} | `{arq}` | {desc} | {'**SOBREVIVEU**' if sobreviveu else 'detectado'} | "
                      f"{'—' if sobreviveu else '`' + quem + '`'} |")
        if sobreviveu:
            sobreviventes.append(mid)
    print("| # | Arquivo | Mutação | Resultado | Primeiro teste que falhou |\n|---|---|---|---|---|")
    print("\n".join(linhas))
    print(f"\nMutantes: {len(MUTANTES)} · detectados: {len(MUTANTES) - len(sobreviventes)} · "
          f"sobreviventes: {len(sobreviventes)} {sobreviventes}")
    return 1 if sobreviventes else 0


if __name__ == "__main__":
    sys.exit(main())
