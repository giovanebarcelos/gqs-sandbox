#!/usr/bin/env python3
"""
GQS0201 - Calculadora de Nível de Maturidade (CMMI).
Analisa características da organização e sugere o nível CMMI.
Uso: python3 GQS0201-NivelMaturidade.py
"""


class NivelMaturidade:
    def __init__(self, nivel: int, nome: str, descricao: str):
        self.nivel = nivel
        self.nome = nome
        self.descricao = descricao


NIVEIS = [
    NivelMaturidade(1, "Inicial", "Processos imprevisíveis, sucesso depende de heróis"),
    NivelMaturidade(2, "Gerenciado", "Processos planejados e controlados por projeto"),
    NivelMaturidade(3, "Definido", "Processos padronizados na organização"),
    NivelMaturidade(4, "Gerenciado Quantitativamente", "Processos controlados com métricas"),
    NivelMaturidade(5, "Otimizando", "Melhoria contínua baseada em dados"),
]


def avaliar_maturidade(respostas: dict) -> int:
    """Calcula o nível de maturidade com base nas respostas."""
    pontos = 0
    criterios = {
        "documenta_requisitos": 1,
        "planeja_projetos": 1,
        "faz_revisoes": 1,
        "testa_sistematicamente": 1,
        "mede_qualidade": 2,
        "melhoria_continua": 2,
    }
    for chave, valor in respostas.items():
        if valor and chave in criterios:
            pontos += criterios[chave]

    if pontos >= 8:
        return 5
    elif pontos >= 6:
        return 4
    elif pontos >= 4:
        return 3
    elif pontos >= 2:
        return 2
    return 1


def main():
    print("=" * 60)
    print("  GQS0201 - AVALIADOR DE NÍVEL DE MATURIDADE CMMI")
    print("=" * 60)
    print("\nResponda com 's' (sim) ou 'n' (não):")

    perguntas = {
        "documenta_requisitos": "Sua organização documenta requisitos dos projetos?",
        "planeja_projetos": "Os projetos têm planejamento com cronograma?",
        "faz_revisoes": "São feitas revisões de código/design?",
        "testa_sistematicamente": "Os testes são sistemáticos e documentados?",
        "mede_qualidade": "Métricas de qualidade são coletadas regularmente?",
        "melhoria_continua": "Existe programa de melhoria contínua?",
    }

    respostas = {}
    for chave, pergunta in perguntas.items():
        while True:
            resposta = input(f"\n{pergunta} (s/n): ").strip().lower()
            if resposta in ("s", "n"):
                respostas[chave] = resposta == "s"
                break
            print("Responda 's' ou 'n'.")

    nivel = avaliar_maturidade(respostas)
    nivel_info = NIVEIS[nivel - 1]

    print(f"\n{'=' * 60}")
    print(f"  NÍVEL DE MATURIDADE: {nivel} - {nivel_info.nome}")
    print(f"  {nivel_info.descricao}")
    print(f"{'=' * 60}")


if __name__ == "__main__":
    main()
