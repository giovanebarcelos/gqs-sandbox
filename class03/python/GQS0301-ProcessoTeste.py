#!/usr/bin/env python3
"""
GQS0301 - Simulação do Ciclo de Vida de Teste (STLC).
Demonstra as 5 fases do processo de teste de software.
Uso: python3 GQS0301-ProcessoTeste.py
"""


class FaseSTLC:
    def __init__(self, nome: str, descricao: str, duracao: str):
        self.nome = nome
        self.descricao = descricao
        self.duracao = duracao


FASES = [
    FaseSTLC("Planejamento de Testes", "Define escopo, estratégia, recursos e cronograma", "15-20%"),
    FaseSTLC("Design de Casos de Teste", "Especifica cenários, dados e resultados esperados", "20-25%"),
    FaseSTLC("Configuração do Ambiente", "Prepara servidores, bancos e ferramentas", "10-15%"),
    FaseSTLC("Execução dos Testes", "Roda os casos, registra resultados e bugs", "35-40%"),
    FaseSTLC("Encerramento e Relatório", "Consolida métricas e gera relatório final", "5-10%"),
]


def calcular_custo_defeito(fase: int) -> int:
    """Retorna o custo relativo do defeito baseado na regra 1:10:100."""
    fatores = [1, 5, 10, 20, 100]
    idx = min(fase, len(fatores) - 1)
    return fatores[idx]


def main():
    print("=" * 60)
    print("  GQS0301 - CICLO DE VIDA DE TESTE (STLC)")
    print("=" * 60)

    print("\nAs 5 fases do STLC:")
    for i, fase in enumerate(FASES, 1):
        print(f"\n  [{i}] {fase.nome}")
        print(f"      {fase.descricao}")
        print(f"      Duração: {fase.duracao}")

    print("\n--- Regra 1:10:100 (Custo do Defeito) ---")
    fases_nome = ["Requisitos", "Design", "Codificação", "Teste", "Produção"]
    for i, nome in enumerate(fases_nome):
        custo = calcular_custo_defeito(i)
        print(f"  Defeito descoberto em {nome}: {custo}x")

    print("\n--- Simulação de Execução ---")
    casos = ["CT-001: Login válido", "CT-002: Login inválido", "CT-003: Cadastro", "CT-004: Recuperar senha"]
    import random
    random.seed(42)
    for caso in casos:
        status = random.choice(["✅ Passou", "❌ Falhou", "⏳ Bloqueado"])
        print(f"  {caso}: {status}")


if __name__ == "__main__":
    main()
