#!/usr/bin/env python3
"""
GQS0101 - Demonstração dos Atributos de Qualidade ISO 25010.
Exibe as 8 características e suas subcaracterísticas de forma interativa.
Uso: python3 GQS0101-AtributosQualidade.py
"""


class AtributoQualidade:
    """Representa uma característica de qualidade ISO 25010."""

    def __init__(self, nome: str, descricao: str, subcaracteristicas: list):
        self.nome = nome
        self.descricao = descricao
        self.subcaracteristicas = subcaracteristicas

    def exibir(self) -> None:
        print(f"\n  {self.nome}")
        print(f"    {self.descricao}")
        for sub in self.subcaracteristicas:
            print(f"    • {sub}")


def criar_caracteristicas() -> list:
    """Retorna a lista das 8 características ISO 25010."""
    return [
        AtributoQualidade(
            "Adequação Funcional",
            "O software faz o que deveria fazer?",
            ["Completude funcional", "Correção funcional", "Pertinência funcional"],
        ),
        AtributoQualidade(
            "Eficiência de Desempenho",
            "O software responde com velocidade adequada?",
            ["Comportamento temporal", "Utilização de recursos", "Capacidade"],
        ),
        AtributoQualidade(
            "Compatibilidade",
            "O software interage bem com outros sistemas?",
            ["Coexistência", "Interoperabilidade"],
        ),
        AtributoQualidade(
            "Usabilidade",
            "O software é fácil de usar?",
            [
                "Reconhecibilidade",
                "Apreensibilidade",
                "Operabilidade",
                "Proteção ao erro",
                "Acessibilidade",
            ],
        ),
        AtributoQualidade(
            "Confiabilidade",
            "O software é confiável e disponível?",
            ["Maturidade", "Disponibilidade", "Tolerância a falhas", "Recuperabilidade"],
        ),
        AtributoQualidade(
            "Segurança",
            "O software protege dados e acessos?",
            ["Confidencialidade", "Integridade", "Não-repúdio", "Autenticidade", "Responsabilização"],
        ),
        AtributoQualidade(
            "Manutenibilidade",
            "O software é fácil de modificar?",
            ["Modularidade", "Reutilizabilidade", "Analisabilidade", "Modificabilidade", "Testabilidade"],
        ),
        AtributoQualidade(
            "Portabilidade",
            "O software funciona em diferentes ambientes?",
            ["Adaptabilidade", "Instalabilidade", "Substituibilidade"],
        ),
    ]


def calcular_mtbf(tempo_total: float, num_falhas: int) -> float:
    """Calcula o Mean Time Between Failures."""
    if num_falhas == 0:
        return float("inf")
    return round(tempo_total / num_falhas, 2)


def calcular_disponibilidade(mtbf: float, mttr: float) -> float:
    """Calcula a disponibilidade com base em MTBF e MTTR."""
    if mtbf + mttr == 0:
        return 0.0
    return round(mtbf / (mtbf + mttr) * 100, 2)


def main():
    print("=" * 60)
    print("  GQS0101 - ATRIBUTOS DE QUALIDADE ISO 25010")
    print("=" * 60)

    caracteristicas = criar_caracteristicas()
    for i, c in enumerate(caracteristicas, 1):
        print(f"\n[{i}] {c.nome}")
    print(f"\n[9] Sair")

    while True:
        try:
            opcao = input("\nEscolha uma característica (1-9): ")
            if opcao == "9":
                print("Encerrando. Até logo!")
                break
            idx = int(opcao) - 1
            if 0 <= idx < len(caracteristicas):
                caracteristicas[idx].exibir()
            else:
                print("Opção inválida.")
        except ValueError:
            print("Digite um número válido.")

    print("\n--- Exemplo de Métricas ---")
    mtbf = calcular_mtbf(720, 3)  # 30 dias, 3 falhas
    mttr = 2.0
    disp = calcular_disponibilidade(mtbf, mttr)
    print(f"MTBF = {mtbf}h | MTTR = {mttr}h | Disponibilidade = {disp}%")


if __name__ == "__main__":
    main()
