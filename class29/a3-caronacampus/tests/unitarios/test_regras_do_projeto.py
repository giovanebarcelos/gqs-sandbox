"""Guardas das regras que o `AGENTS.md` promete (não são casos de teste do sistema, e sim do repositório)."""
import ast
from pathlib import Path

SRC = Path(__file__).resolve().parents[2] / "src" / "carona"


def _modulos_importados(arq: Path) -> set[str]:
    nomes = set()
    for no in ast.walk(ast.parse(arq.read_text(encoding="utf-8"))):
        if isinstance(no, ast.Import):
            nomes |= {a.name for a in no.names}
        elif isinstance(no, ast.ImportFrom):
            nomes.add(("." * no.level) + (no.module or ""))
    return nomes


def _chama_relogio_real(arq: Path) -> bool:
    for no in ast.walk(ast.parse(arq.read_text(encoding="utf-8"))):
        if (isinstance(no, ast.Call) and isinstance(no.func, ast.Attribute)
                and no.func.attr in {"now", "utcnow", "today"}
                and isinstance(no.func.value, ast.Name) and no.func.value.id == "datetime"):
            return True
    return False


def test_arq_dominio_nao_depende_de_flask_nem_de_sqlite_nem_das_outras_camadas():
    proibidos = {"flask", "sqlite3", ".api", ".repositorio", ".servico", ".app"}
    assert not _modulos_importados(SRC / "dominio.py") & proibidos


def test_arq_relogio_real_so_aparece_na_api():
    for arq in SRC.glob("*.py"):
        assert _chama_relogio_real(arq) == (arq.name == "api.py"), arq.name
