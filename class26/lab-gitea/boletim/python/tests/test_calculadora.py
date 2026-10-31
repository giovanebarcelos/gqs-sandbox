"""Testes unitarios das regras R1 a R3."""

from decimal import Decimal

import pytest

from boletim.calculadora import calcular_boletim


def test_calcula_media_maior_e_menor_nota():
    boletim = calcular_boletim("Ana", [7, 9, 8], 90)
    assert boletim["media"] == Decimal("8.0")
    assert boletim["situacao"] == "APROVADO"
    assert boletim["maiorNota"] == 9.0
    assert boletim["menorNota"] == 7.0
    assert boletim["resumo"] == "Ana: APROVADO com media 8.0"


def test_media_arredonda_com_uma_casa():
    # 7 + 6,5 + 6,5 = 20 / 3 = 6,666... -> 6,7
    assert calcular_boletim("Bia", [7, 6.5, 6.5], 100)["media"] == Decimal("6.7")


@pytest.mark.parametrize(
    ("nota", "frequencia", "esperada"),
    [
        (7.0, 100, "APROVADO"),
        (6.9, 100, "EXAME"),
        (4.0, 100, "EXAME"),
        (3.9, 100, "REPROVADO"),
        (10, 74, "REPROVADO_POR_FALTA"),
        (10, 75, "APROVADO"),
        (5.0, 75, "EXAME"),
    ],
)
def test_situacao_pela_media_e_frequencia(nota, frequencia, esperada):
    assert calcular_boletim("Caio", [nota], frequencia)["situacao"] == esperada


@pytest.mark.parametrize(
    ("aluno", "notas", "frequencia"),
    [
        ("Davi", [11], 80),               # nota acima de 10
        ("Eva", [], 80),                  # sem notas
        ("Eva", [1, 2, 3, 4, 5], 80),     # mais de 4 notas
        ("Fabi", [7], 101),               # frequencia acima de 100
        (" ", [7], 80),                   # aluno sem nome
        ("Gil", ["7"], 80),               # nota que nao e' numero
    ],
)
def test_entrada_invalida_e_rejeitada(aluno, notas, frequencia):
    with pytest.raises(ValueError):
        calcular_boletim(aluno, notas, frequencia)
