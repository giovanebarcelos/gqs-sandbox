#!/usr/bin/env python3
"""
GQS2501 - Simulação de Pipeline de CI/CD (build -> test -> analyse -> deploy).
Executa em sequência as etapas típicas de um pipeline (Slide 7 da Aula 25),
imprimindo o progresso de cada etapa e aplicando a regra de "fail fast": se uma
etapa falhar, as etapas seguintes NÃO são executadas e o pipeline é marcado
como reprovado -- sem depender de Jenkins, GitLab CI ou GitHub Actions reais.

Uso: python3 GQS2501-SimulacaoPipeline.py
"""
import random
import time


class Etapa:
    """Representa uma etapa (stage) do pipeline."""

    def __init__(self, nome: str, duracao_s: float, chance_sucesso: float):
        self.nome = nome
        self.duracao_s = duracao_s
        self.chance_sucesso = chance_sucesso
        self.sucesso: bool = False
        self.mensagem: str = ""

    def executar(self, rng: random.Random) -> bool:
        """Simula a execução da etapa (sem realmente esperar o tempo real,
        só uma pequena pausa para efeito didático)."""
        time.sleep(min(self.duracao_s, 0.05))  # pausa curta, só para dar sensação de progresso
        self.sucesso = rng.random() < self.chance_sucesso
        self.mensagem = "OK" if self.sucesso else "FALHOU"
        return self.sucesso


class Pipeline:
    """Executa uma lista de etapas em sequência, com fail fast."""

    def __init__(self, nome: str, etapas: list, seed: int = 7):
        self.nome = nome
        self.etapas = etapas
        self.rng = random.Random(seed)
        self.etapa_que_falhou: str | None = None

    def executar(self) -> bool:
        print("=" * 60)
        print(f"  PIPELINE: {self.nome}")
        print("=" * 60)
        for indice, etapa in enumerate(self.etapas, start=1):
            print(f"\n[{indice}/{len(self.etapas)}] Executando: {etapa.nome} ...", end=" ")
            ok = etapa.executar(self.rng)
            print(f"[{etapa.mensagem}] ({etapa.duracao_s:.1f}s)")
            if not ok:
                self.etapa_que_falhou = etapa.nome
                print(f"\n>> FAIL FAST: etapa '{etapa.nome}' falhou. "
                      f"Etapas seguintes NÃO serão executadas.")
                return False
        return True

    def imprimir_resultado_final(self) -> None:
        print("\n" + "=" * 60)
        if self.etapa_que_falhou is None:
            print("  RESULTADO FINAL: PIPELINE APROVADO -- deploy concluído com sucesso")
        else:
            print(f"  RESULTADO FINAL: PIPELINE REPROVADO na etapa '{self.etapa_que_falhou}'")
        print("=" * 60)


def criar_pipeline_sucesso() -> Pipeline:
    """Pipeline com alta chance de sucesso em todas as etapas (cenário feliz)."""
    etapas = [
        Etapa("Checkout", 0.2, chance_sucesso=1.0),
        Etapa("Build", 0.5, chance_sucesso=0.98),
        Etapa("Unit Tests", 0.8, chance_sucesso=0.95),
        Etapa("Static Analysis (Quality Gate)", 0.6, chance_sucesso=0.95),
        Etapa("Package", 0.3, chance_sucesso=0.99),
        Etapa("Deploy Staging", 0.4, chance_sucesso=0.97),
        Etapa("Deploy Prod", 0.4, chance_sucesso=0.97),
    ]
    return Pipeline("app-techstore (branch: main)", etapas, seed=7)


def criar_pipeline_com_falha() -> Pipeline:
    """Pipeline onde a etapa de testes unitários é forçada a falhar,
    demonstrando o comportamento de fail fast."""
    etapas = [
        Etapa("Checkout", 0.2, chance_sucesso=1.0),
        Etapa("Build", 0.5, chance_sucesso=1.0),
        Etapa("Unit Tests", 0.8, chance_sucesso=0.0),  # forçado a falhar
        Etapa("Static Analysis (Quality Gate)", 0.6, chance_sucesso=1.0),
        Etapa("Package", 0.3, chance_sucesso=1.0),
        Etapa("Deploy Staging", 0.4, chance_sucesso=1.0),
        Etapa("Deploy Prod", 0.4, chance_sucesso=1.0),
    ]
    return Pipeline("app-techstore (branch: feature/checkout-com-bug)", etapas, seed=1)


def main() -> None:
    pipeline1 = criar_pipeline_sucesso()
    pipeline1.executar()
    pipeline1.imprimir_resultado_final()

    print("\n")

    pipeline2 = criar_pipeline_com_falha()
    pipeline2.executar()
    pipeline2.imprimir_resultado_final()


if __name__ == "__main__":
    main()
