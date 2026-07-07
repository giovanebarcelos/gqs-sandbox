#!/usr/bin/env python3
"""
GQS2401 - Relatório Estruturado de Defeitos (estilo TestLink/Jira).
Modela uma lista de defeitos com severidade, prioridade e status, e gera um
relatório de texto com contagens por dimensão e o indicador mais importante
para decidir se um release pode avançar: o percentual de bugs CRÍTICOS que
ainda estão ABERTOS.

Uso: python3 GQS2401-RelatorioDefeitos.py
"""
from enum import Enum


class Severidade(Enum):
    """Gravidade técnica do impacto do defeito (Slide 11 da Aula 24)."""
    CRITICA = "Crítica"
    ALTA = "Alta"
    MEDIA = "Média"
    BAIXA = "Baixa"


class Prioridade(Enum):
    """Urgência de negócio para corrigir o defeito (Slide 11 da Aula 24)."""
    URGENTE = "Urgente"
    ALTA = "Alta"
    MEDIA = "Média"
    BAIXA = "Baixa"


class Status(Enum):
    """Estado do defeito no fluxo de bug tracking (Slide 4 da Aula 24)."""
    NOVO = "Novo"
    EM_ANDAMENTO = "Em Andamento"
    RESOLVIDO = "Resolvido"
    FECHADO = "Fechado"
    REABERTO = "Reaberto"

    @property
    def esta_aberto(self) -> bool:
        """Um defeito é considerado 'aberto' enquanto não foi Fechado."""
        return self != Status.FECHADO


class Defeito:
    """Representa um bug/defeito registrado numa ferramenta como TestLink ou Jira."""

    def __init__(self, id_defeito: str, titulo: str, severidade: Severidade,
                 prioridade: Prioridade, status: Status, modulo: str):
        self.id_defeito = id_defeito
        self.titulo = titulo
        self.severidade = severidade
        self.prioridade = prioridade
        self.status = status
        self.modulo = modulo

    def __str__(self) -> str:
        return (f"[{self.id_defeito}] {self.titulo} | Módulo: {self.modulo} | "
                f"Severidade: {self.severidade.value} | Prioridade: {self.prioridade.value} | "
                f"Status: {self.status.value}")


def criar_defeitos_exemplo() -> list:
    """Retorna uma lista de defeitos de exemplo, simulando uma rodada de testes
    do projeto TechStore (ver GQS24-Tutorial-TestLink-TechStore.md)."""
    return [
        Defeito("BUG-001", "Mensagem de erro genérica no login com senha incorreta",
                Severidade.BAIXA, Prioridade.ALTA, Status.RESOLVIDO, "Autenticação"),
        Defeito("BUG-002", "Sistema trava ao finalizar checkout com cupom expirado",
                Severidade.CRITICA, Prioridade.URGENTE, Status.NOVO, "Checkout"),
        Defeito("BUG-003", "Subtotal do carrinho não atualiza ao remover item",
                Severidade.ALTA, Prioridade.ALTA, Status.EM_ANDAMENTO, "Carrinho"),
        Defeito("BUG-004", "Ícone do carrinho desalinhado em telas pequenas",
                Severidade.BAIXA, Prioridade.BAIXA, Status.NOVO, "Interface"),
        Defeito("BUG-005", "Frete calculado incorretamente para CEPs do Norte",
                Severidade.ALTA, Prioridade.MEDIA, Status.REABERTO, "Checkout"),
        Defeito("BUG-006", "Perda de sessão ao pagar com cartão de crédito",
                Severidade.CRITICA, Prioridade.URGENTE, Status.EM_ANDAMENTO, "Pagamento"),
        Defeito("BUG-007", "Busca não encontra produtos com acento no nome",
                Severidade.MEDIA, Prioridade.MEDIA, Status.NOVO, "Catálogo"),
        Defeito("BUG-008", "Filtro de preço não reseta ao trocar de categoria",
                Severidade.MEDIA, Prioridade.BAIXA, Status.FECHADO, "Catálogo"),
    ]


def contar_por_dimensao(defeitos: list, atributo: str) -> dict:
    """Conta quantos defeitos existem para cada valor de uma dimensão (severidade,
    prioridade ou status), preservando a ordem de primeira ocorrência."""
    contagem: dict = {}
    for defeito in defeitos:
        valor = getattr(defeito, atributo).value
        contagem[valor] = contagem.get(valor, 0) + 1
    return contagem


def percentual_criticos_abertos(defeitos: list) -> float:
    """Calcula o indicador mais importante para uma decisão de release:
    % de defeitos com severidade CRÍTICA que ainda estão abertos (não Fechados)."""
    criticos = [d for d in defeitos if d.severidade == Severidade.CRITICA]
    if not criticos:
        return 0.0
    criticos_abertos = [d for d in criticos if d.status.esta_aberto]
    return round(len(criticos_abertos) / len(criticos) * 100, 2)


def imprimir_tabela_contagem(titulo: str, contagem: dict) -> None:
    print(f"\n{titulo}")
    print("-" * 40)
    for valor, qtd in contagem.items():
        print(f"  {valor:<15} {qtd}")


def gerar_relatorio(defeitos: list) -> None:
    print("=" * 60)
    print("  GQS2401 - RELATÓRIO DE DEFEITOS")
    print("=" * 60)

    print(f"\nTotal de defeitos registrados: {len(defeitos)}\n")
    print("Lista completa:")
    for defeito in defeitos:
        print(f"  {defeito}")

    imprimir_tabela_contagem("Contagem por Severidade", contar_por_dimensao(defeitos, "severidade"))
    imprimir_tabela_contagem("Contagem por Prioridade", contar_por_dimensao(defeitos, "prioridade"))
    imprimir_tabela_contagem("Contagem por Status", contar_por_dimensao(defeitos, "status"))

    pct_criticos_abertos = percentual_criticos_abertos(defeitos)
    print("\n" + "=" * 60)
    print(f"  INDICADOR DE RELEASE: {pct_criticos_abertos}% dos bugs CRÍTICOS estão ABERTOS")
    if pct_criticos_abertos > 0:
        print("  >> Recomendação: NÃO liberar o release enquanto houver crítico aberto.")
    else:
        print("  >> Recomendação: nenhum crítico aberto; release pode avançar (avaliar demais riscos).")
    print("=" * 60)


def main() -> None:
    defeitos = criar_defeitos_exemplo()
    gerar_relatorio(defeitos)


if __name__ == "__main__":
    main()
