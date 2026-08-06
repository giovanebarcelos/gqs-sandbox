#!/usr/bin/env python3
"""
GQS2801 - Simulador de Histórico de Commits, Baseline e Versionamento Semântico.
Simula um repositório simplificado: commits, criação de baseline e cálculo
da próxima versão SemVer a partir do tipo de mudança.
Uso: python3 GQS2801-SimuladorVersionamento.py
"""

from dataclasses import dataclass, field


@dataclass
class Commit:
    hash: str
    mensagem: str
    tipo: str  # "major", "minor" ou "patch"


@dataclass
class Repositorio:
    versao: tuple[int, int, int] = (0, 1, 0)
    historico: list[Commit] = field(default_factory=list)
    baseline: tuple[int, int, int] | None = None

    def commitar(self, hash_: str, mensagem: str, tipo: str) -> None:
        if tipo not in ("major", "minor", "patch"):
            raise ValueError("tipo deve ser major, minor ou patch")
        self.historico.append(Commit(hash_, mensagem, tipo))

    def proxima_versao(self, tipo: str) -> tuple[int, int, int]:
        major, minor, patch = self.versao
        if tipo == "major":
            return (major + 1, 0, 0)
        if tipo == "minor":
            return (major, minor + 1, 0)
        return (major, minor, patch + 1)

    def aplicar_release(self) -> tuple[int, int, int]:
        """Aplica o maior impacto entre os commits desde a última release."""
        prioridade = {"major": 3, "minor": 2, "patch": 1}
        if not self.historico:
            return self.versao
        tipo_dominante = max(self.historico, key=lambda c: prioridade[c.tipo]).tipo
        self.versao = self.proxima_versao(tipo_dominante)
        self.historico = []
        return self.versao

    def congelar_baseline(self) -> tuple[int, int, int]:
        self.baseline = self.versao
        return self.baseline

    def houve_mudanca_desde_baseline(self) -> bool:
        return self.baseline is not None and self.baseline != self.versao


def formatar_versao(v: tuple[int, int, int]) -> str:
    return f"{v[0]}.{v[1]}.{v[2]}"


def main():
    print("=" * 64)
    print("  GQS2801 - SIMULADOR DE VERSIONAMENTO E BASELINE")
    print("=" * 64)

    repo = Repositorio()
    print(f"\n  Versão inicial: {formatar_versao(repo.versao)}")

    repo.congelar_baseline()
    print(f"  Baseline congelada em: {formatar_versao(repo.baseline)}")

    print("\n--- Ciclo de mudanças (exemplo da Aula 28) ---")
    commits = [
        ("a1b2c3d", "Corrige cálculo de disponibilidade (MTBF/MTTR)", "patch"),
        ("e4f5g6h", "Adiciona endpoint GET /clientes/{id}/historico", "minor"),
    ]
    for hash_, msg, tipo in commits:
        repo.commitar(hash_, msg, tipo)
        print(f"  commit {hash_}: \"{msg}\" [{tipo}]")

    nova_versao = repo.aplicar_release()
    print(f"\n  Release aplicada (maior impacto vence): {formatar_versao(nova_versao)}")
    print(f"  Houve mudança desde a baseline? {repo.houve_mudanca_desde_baseline()}")

    print("\n--- Mudança que quebra compatibilidade ---")
    repo.commitar("i7j8k9l", "Remove campo 'endereco' do JSON de resposta", "major")
    nova_versao = repo.aplicar_release()
    print(f"  Nova versão (breaking change): {formatar_versao(nova_versao)}")

    repo.congelar_baseline()
    print(f"  Nova baseline: {formatar_versao(repo.baseline)}")


if __name__ == "__main__":
    main()
