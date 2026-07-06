#!/usr/bin/env python3
"""
GQS0701 - Simulação do Ciclo de Vida de um Defeito.
Demonstra os estados de um bug até o fechamento.
Uso: python3 GQS0701-CicloDefecto.py
"""

import time


class Defeito:
    def __init__(self, titulo: str):
        self.titulo = titulo
        self.estado = "Novo"
        self.severidade = ""
        self.prioridade = ""

    def transitar(self, novo_estado: str) -> None:
        print(f"  {self.estado} → {novo_estado}")
        self.estado = novo_estado
        time.sleep(0.5)

    def exibir(self) -> None:
        print(f"\n  BUG: {self.titulo}")
        print(f"  Estado: {self.estado}")
        if self.severidade:
            print(f"  Severidade: {self.severidade} | Prioridade: {self.prioridade}")


def simular_ciclo():
    print("=" * 60)
    print("  GQS0701 - CICLO DE VIDA DE UM DEFEITO")
    print("=" * 60)

    bug = Defeito("App fecha ao selecionar endereço")

    print("\n--- Simulação do Ciclo ---")
    bug.exibir()
    bug.transitar("Atribuído")
    bug.transitar("Em Análise")
    bug.severidade = "Crítica"
    bug.prioridade = "Imediata"
    bug.exibir()
    bug.transitar("Corrigindo")
    bug.transitar("Resolvido")
    bug.transitar("Em Teste")
    print("\n  QA valida a correção...")
    bug.transitar("Fechado")
    bug.exibir()
    print("\n  Ciclo concluído com sucesso!")


if __name__ == "__main__":
    simular_ciclo()
