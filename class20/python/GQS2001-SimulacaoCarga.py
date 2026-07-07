#!/usr/bin/env python3
"""
GQS2001 - Simulação de Métricas de Teste de Carga (estilo JMeter Aggregate Report).
Gera amostras sintéticas de tempo de resposta para cenários de requisições e calcula
throughput, latência (p50/p95/p99), tempo médio e taxa de erro — sem depender de uma
instância real do JMeter ou de acesso à rede. Útil para estudar a interpretação das
métricas antes/depois de rodar o plano de teste real (GQS20-ConsultaCEP.jmx).

Uso: python3 GQS2001-SimulacaoCarga.py
"""
import random
import statistics


class AmostraRequisicao:
    """Representa uma amostra (sample) de requisição, análoga a uma linha do
    'View Results Tree' do JMeter."""

    def __init__(self, tempo_resposta_ms: float, sucesso: bool):
        self.tempo_resposta_ms = tempo_resposta_ms
        self.sucesso = sucesso


class CenarioCarga:
    """Representa uma Thread Group + Sampler simulados."""

    def __init__(self, nome: str, threads: int, num_amostras: int,
                 media_ms: float, desvio_ms: float, taxa_erro: float, seed: int = 42):
        self.nome = nome
        self.threads = threads
        self.num_amostras = num_amostras
        self.media_ms = media_ms
        self.desvio_ms = desvio_ms
        self.taxa_erro = taxa_erro
        self.seed = seed
        self.amostras: list = []

    def executar(self) -> None:
        """Gera as amostras sintéticas (equivalente a 'rodar' o Thread Group)."""
        rng = random.Random(self.seed)
        for _ in range(self.num_amostras):
            tempo = max(1.0, rng.gauss(self.media_ms, self.desvio_ms))
            sucesso = rng.random() >= self.taxa_erro
            self.amostras.append(AmostraRequisicao(tempo, sucesso))


def calcular_percentil(valores: list, percentil: float) -> float:
    """Calcula o percentil (0-100) de uma lista de valores, como o JMeter faz
    para P90/P95/P99 no Aggregate Report."""
    if not valores:
        return 0.0
    ordenados = sorted(valores)
    idx = int(round((percentil / 100) * (len(ordenados) - 1)))
    idx = min(max(idx, 0), len(ordenados) - 1)
    return round(ordenados[idx], 2)


def calcular_estatisticas(cenario: CenarioCarga) -> dict:
    """Calcula as métricas equivalentes a uma linha do Aggregate Report/Summary Report."""
    tempos = [a.tempo_resposta_ms for a in cenario.amostras]
    erros = [a for a in cenario.amostras if not a.sucesso]

    # Duração total aproximada do cenário: soma dos tempos dividida pelos
    # usuários virtuais simultâneos (modelo simplificado para fins didáticos).
    duracao_total_s = (sum(tempos) / 1000) / max(cenario.threads, 1)
    throughput = round(cenario.num_amostras / duracao_total_s, 2) if duracao_total_s > 0 else 0.0

    return {
        "cenario": cenario.nome,
        "amostras": cenario.num_amostras,
        "tempo_medio_ms": round(statistics.mean(tempos), 2),
        "tempo_min_ms": round(min(tempos), 2),
        "tempo_max_ms": round(max(tempos), 2),
        "p50_ms": calcular_percentil(tempos, 50),
        "p95_ms": calcular_percentil(tempos, 95),
        "p99_ms": calcular_percentil(tempos, 99),
        "erro_pct": round(100 * len(erros) / cenario.num_amostras, 2),
        "throughput_rps": throughput,
    }


def classificar_resultado(stats: dict) -> str:
    """Aplica limiares simples (mesmos do Slide 'Interpretando Resultados') para
    indicar se o cenário está dentro do esperado."""
    if stats["erro_pct"] > 5 or stats["p95_ms"] > 5000:
        return "CRÍTICO"
    if stats["erro_pct"] > 1 or stats["p95_ms"] > 2000:
        return "ATENÇÃO"
    return "OK"


def imprimir_relatorio(lista_stats: list) -> None:
    cabecalho = f"{'Cenário':<20}{'Amostras':>10}{'Média(ms)':>12}{'P50(ms)':>10}" \
                f"{'P95(ms)':>10}{'P99(ms)':>10}{'Erro%':>8}{'RPS':>8}{'Status':>10}"
    print(cabecalho)
    print("-" * len(cabecalho))
    for s in lista_stats:
        status = classificar_resultado(s)
        print(f"{s['cenario']:<20}{s['amostras']:>10}{s['tempo_medio_ms']:>12}"
              f"{s['p50_ms']:>10}{s['p95_ms']:>10}{s['p99_ms']:>10}"
              f"{s['erro_pct']:>8}{s['throughput_rps']:>8}{status:>10}")


def main():
    print("=" * 78)
    print("  GQS2001 - SIMULAÇÃO DE MÉTRICAS DE TESTE DE CARGA (estilo JMeter)")
    print("=" * 78)

    cenarios = [
        CenarioCarga("Consulta CEP", threads=50, num_amostras=500,
                     media_ms=250, desvio_ms=60, taxa_erro=0.01, seed=1),
        CenarioCarga("Login", threads=100, num_amostras=800,
                     media_ms=400, desvio_ms=150, taxa_erro=0.03, seed=2),
        CenarioCarga("Checkout (stress)", threads=300, num_amostras=1200,
                     media_ms=1800, desvio_ms=900, taxa_erro=0.08, seed=3),
    ]

    resultados = []
    for cenario in cenarios:
        cenario.executar()
        resultados.append(calcular_estatisticas(cenario))

    imprimir_relatorio(resultados)

    print("\n--- Interpretação ---")
    for s in resultados:
        status = classificar_resultado(s)
        print(f"[{status}] {s['cenario']}: P95={s['p95_ms']}ms, "
              f"erro={s['erro_pct']}%, throughput={s['throughput_rps']} req/s")


if __name__ == "__main__":
    main()
