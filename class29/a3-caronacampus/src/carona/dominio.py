"""Regras de negócio do CaronaCampus — funções puras e entidades, sem I/O.

Todas as datas são `datetime` ingênuos em UTC. O "agora" é sempre recebido
como parâmetro (relógio injetado): nenhuma função daqui chama `datetime.now()`.
"""
from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime, timedelta
from decimal import ROUND_CEILING, ROUND_HALF_UP, Decimal

# --- Constantes das regras (RN-01 a RN-05) -----------------------------------
VAGAS_MIN, VAGAS_MAX = 1, 6                      # RN-01
ANTECEDENCIA_MIN = timedelta(minutes=30)         # RN-02 (mínima)
JANELA_MAX = timedelta(days=14)                  # RN-02 (máxima)
PRECO_MIN, PRECO_MAX = Decimal("2.00"), Decimal("25.00")   # RN-03
LIMITE_SEM_TAXA = timedelta(minutes=60)          # RN-04
TAXA_TARDIA = Decimal("0.50")                    # RN-04
AVALIACOES_PARA_SUSPENDER = 5                    # RN-05
MEDIA_MINIMA = 3                                 # RN-05
CENTAVO = Decimal("0.01")


class ErroDeDominio(Exception):
    """Base dos erros de regra de negócio; carrega um código estável."""

    codigo = "erro_de_dominio"

    def __init__(self, mensagem: str):
        super().__init__(mensagem)
        self.mensagem = mensagem


class DadoInvalido(ErroDeDominio):
    codigo = "dado_invalido"


class NaoEncontrado(ErroDeDominio):
    codigo = "nao_encontrado"


class SemPermissao(ErroDeDominio):
    codigo = "sem_permissao"


class ConflitoDeRegra(ErroDeDominio):
    codigo = "conflito_de_regra"


class MotoristaSuspenso(ErroDeDominio):
    codigo = "motorista_suspenso"


# --- Regras como funções puras -----------------------------------------------
def validar_vagas(vagas: int) -> int:
    """RN-01: uma viagem oferece de 1 a 6 vagas."""
    if isinstance(vagas, bool) or not isinstance(vagas, int):
        raise DadoInvalido("vagas deve ser um inteiro")
    if not VAGAS_MIN <= vagas <= VAGAS_MAX:
        raise DadoInvalido(f"vagas deve estar entre {VAGAS_MIN} e {VAGAS_MAX}")
    return vagas


def validar_saida(saida: datetime, agora: datetime) -> datetime:
    """RN-02: a saída fica entre 30 min e 14 dias à frente (limites inclusivos)."""
    if saida < agora + ANTECEDENCIA_MIN:
        raise DadoInvalido("a saída deve ser pelo menos 30 minutos no futuro")
    if saida > agora + JANELA_MAX:
        raise DadoInvalido("a saída deve ser em no máximo 14 dias")
    return saida


def preco_por_vaga(custo_total: Decimal, vagas: int) -> Decimal:
    """RN-03: custo dividido por (vagas + 1), arredondado para CIMA ao centavo.

    O "+ 1" é o motorista, que também paga a sua parte. O resultado fica
    limitado ao intervalo [R$ 2,00; R$ 25,00].
    """
    validar_vagas(vagas)
    if custo_total <= 0:
        raise DadoInvalido("custo_total deve ser positivo")
    bruto = (custo_total / (vagas + 1)).quantize(CENTAVO, rounding=ROUND_CEILING)
    return max(PRECO_MIN, min(PRECO_MAX, bruto))


def taxa_de_cancelamento(valor: Decimal, saida: datetime, agora: datetime) -> Decimal:
    """RN-04: sem taxa até 60 min antes da saída (inclusive); depois, 50% do valor.

    Depois da saída não se cancela.
    """
    if agora >= saida:
        raise ConflitoDeRegra("a viagem já começou: não é possível cancelar")
    if saida - agora >= LIMITE_SEM_TAXA:
        return Decimal("0.00")
    return (valor * TAXA_TARDIA).quantize(CENTAVO, rounding=ROUND_HALF_UP)


def esta_suspenso(notas: list[int]) -> bool:
    """RN-05: com 5 ou mais avaliações, média abaixo de 3,0 suspende o motorista.

    A comparação usa inteiros (soma < 3 × quantidade): sem erro de ponto flutuante.
    """
    if len(notas) < AVALIACOES_PARA_SUSPENDER:
        return False
    return sum(notas) < MEDIA_MINIMA * len(notas)


def media(notas: list[int]) -> Decimal | None:
    """Média das notas com 2 casas, ou None se não houver avaliações."""
    if not notas:
        return None
    return (Decimal(sum(notas)) / len(notas)).quantize(CENTAVO, rounding=ROUND_HALF_UP)


def validar_nota(nota: int) -> int:
    if isinstance(nota, bool) or not isinstance(nota, int) or not 1 <= nota <= 5:
        raise DadoInvalido("a nota deve ser um inteiro de 1 a 5")
    return nota


# --- Entidades ---------------------------------------------------------------
@dataclass
class Usuario:
    id: int
    matricula: str
    nome: str


@dataclass
class Viagem:
    id: int
    motorista_id: int
    origem: str
    destino: str
    saida: datetime
    vagas: int
    custo_total: Decimal
    cancelada: bool = False

    @property
    def valor_por_vaga(self) -> Decimal:
        return preco_por_vaga(self.custo_total, self.vagas)

    def iniciada(self, agora: datetime) -> bool:
        return agora >= self.saida


@dataclass
class Reserva:
    id: int
    viagem_id: int
    passageiro_id: int
    valor: Decimal
    cancelada: bool = False
    taxa: Decimal = Decimal("0.00")
