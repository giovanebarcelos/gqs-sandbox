"""Testes unitários das regras puras (RN-01 a RN-05). Sem banco, sem HTTP, sem relógio real.

Cada teste cita o caso de teste (CT-xx) de casos-de-teste.md que automatiza.
"""
from datetime import datetime, timedelta
from decimal import Decimal

import pytest

from carona import dominio as d

AGORA = datetime(2026, 11, 10, 12, 0, 0)


# --- CT-02 · RN-01 · valor-limite das vagas ----------------------------------
@pytest.mark.parametrize("vagas", [1, 2, 5, 6])
def test_ct02_vagas_dentro_do_intervalo_sao_aceitas(vagas):
    assert d.validar_vagas(vagas) == vagas


@pytest.mark.parametrize("vagas", [0, 7, -1, 100])
def test_ct02_vagas_fora_do_intervalo_sao_recusadas(vagas):
    with pytest.raises(d.DadoInvalido):
        d.validar_vagas(vagas)


@pytest.mark.parametrize("vagas", [True, 2.5, "3", None])
def test_ct02_vagas_que_nao_sao_inteiro_sao_recusadas(vagas):
    with pytest.raises(d.DadoInvalido):
        d.validar_vagas(vagas)


# --- CT-03 · RN-02 · valor-limite da saída -----------------------------------
@pytest.mark.parametrize("delta", [timedelta(minutes=30), timedelta(minutes=30, seconds=1),
                                   timedelta(days=14), timedelta(days=1)])
def test_ct03_saida_nos_limites_inclusivos_e_aceita(delta):
    assert d.validar_saida(AGORA + delta, AGORA) == AGORA + delta


@pytest.mark.parametrize("delta", [timedelta(minutes=29, seconds=59), timedelta(0),
                                   timedelta(days=14, seconds=1), timedelta(days=-1)])
def test_ct03_saida_fora_dos_limites_e_recusada(delta):
    with pytest.raises(d.DadoInvalido):
        d.validar_saida(AGORA + delta, AGORA)


# --- CT-04 · RN-03 · preço por vaga ------------------------------------------
@pytest.mark.parametrize("custo, vagas, esperado", [
    ("12.00", 3, "3.00"),      # 12 / 4, exato
    ("10.00", 2, "3.34"),      # 3,333... arredonda PARA CIMA (3,33 seria erro)
    ("10.01", 4, "2.01"),      # 2,002 -> 2,01 (truncar daria 2,00)
    ("2.00", 6, "2.00"),       # 0,2857 -> piso de R$ 2,00
    ("100.00", 1, "25.00"),    # 50,00 -> teto de R$ 25,00
    ("50.00", 1, "25.00"),     # 25,00 exato no teto
    ("50.02", 1, "25.00"),     # 25,01 -> teto
    ("4.00", 1, "2.00"),       # 2,00 exato no piso
    ("3.98", 1, "2.00"),       # 1,99 -> piso
])
def test_ct04_preco_por_vaga(custo, vagas, esperado):
    assert d.preco_por_vaga(Decimal(custo), vagas) == Decimal(esperado)


@pytest.mark.parametrize("custo", ["0", "-5.00"])
def test_ct04_custo_nao_positivo_e_recusado(custo):
    with pytest.raises(d.DadoInvalido):
        d.preco_por_vaga(Decimal(custo), 3)


def test_ct04_preco_usa_decimal_e_tem_duas_casas():
    p = d.preco_por_vaga(Decimal("10.00"), 2)
    assert isinstance(p, Decimal) and p.as_tuple().exponent == -2


# --- CT-08, CT-09, CT-10 · RN-04 · taxa de cancelamento ----------------------
SAIDA = AGORA + timedelta(hours=5)


@pytest.mark.parametrize("antecedencia", [timedelta(hours=5), timedelta(minutes=61),
                                          timedelta(minutes=60)])
def test_ct08_ate_60_min_antes_nao_ha_taxa(antecedencia):
    agora = SAIDA - antecedencia
    assert d.taxa_de_cancelamento(Decimal("6.00"), SAIDA, agora) == Decimal("0.00")


@pytest.mark.parametrize("antecedencia, taxa", [
    (timedelta(minutes=59, seconds=59), "3.00"),
    (timedelta(minutes=30), "3.00"),
    (timedelta(seconds=1), "3.00"),
])
def test_ct09_com_menos_de_60_min_a_taxa_e_metade_do_valor(antecedencia, taxa):
    assert d.taxa_de_cancelamento(Decimal("6.00"), SAIDA, SAIDA - antecedencia) == Decimal(taxa)


def test_ct09_taxa_arredonda_meio_centavo_para_cima():
    assert d.taxa_de_cancelamento(Decimal("3.01"), SAIDA, SAIDA - timedelta(minutes=10)) == Decimal("1.51")


@pytest.mark.parametrize("depois", [timedelta(0), timedelta(seconds=1), timedelta(hours=2)])
def test_ct10_na_hora_da_saida_ou_depois_nao_cancela(depois):
    with pytest.raises(d.ConflitoDeRegra):
        d.taxa_de_cancelamento(Decimal("6.00"), SAIDA, SAIDA + depois)


# --- CT-11 · validação da nota -----------------------------------------------
@pytest.mark.parametrize("nota", [1, 2, 3, 4, 5])
def test_ct11_notas_de_1_a_5_sao_aceitas(nota):
    assert d.validar_nota(nota) == nota


@pytest.mark.parametrize("nota", [0, 6, -1, 3.5, "5", True, None])
def test_ct11_notas_fora_do_dominio_sao_recusadas(nota):
    with pytest.raises(d.DadoInvalido):
        d.validar_nota(nota)


# --- CT-12 · RN-05 · suspensão por reputação ---------------------------------
@pytest.mark.parametrize("notas, suspenso", [
    ([], False),
    ([1, 1, 1, 1], False),            # 4 avaliações: ainda não suspende
    ([1, 1, 1, 1, 1], True),          # 5ª avaliação, média 1,0
    ([3, 3, 3, 3, 3], False),         # média exata 3,0: NÃO suspende
    ([3, 3, 3, 3, 2], True),          # média 2,8
    ([5, 5, 5, 1, 1], False),         # média 3,4
    ([1, 5, 5, 1, 2], True),          # média 2,8 em outra ordem
    ([4, 4, 4, 1, 1, 1], True),       # 6 avaliações, média 2,5
])
def test_ct12_suspensao_por_media_baixa(notas, suspenso):
    assert d.esta_suspenso(notas) is suspenso


def test_ct12_media_e_arredondada_em_duas_casas():
    assert d.media([5, 4, 4]) == Decimal("4.33")
    assert d.media([]) is None
