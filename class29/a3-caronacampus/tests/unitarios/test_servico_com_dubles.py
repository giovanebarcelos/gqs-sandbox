"""Testes unitários do `ServicoCarona` com o repositório substituído por dublê (Mock).

Isolam a orquestração: o que o serviço decide e o que ele NÃO chama quando uma
regra recusa a operação. O banco real entra nos testes de integração.
"""
from datetime import datetime, timedelta
from decimal import Decimal
from unittest.mock import Mock

import pytest

from carona import dominio as d
from carona.repositorio import RepositorioCarona
from carona.servico import ServicoCarona

AGORA = datetime(2026, 11, 10, 12, 0, 0)


def viagem(**kw):
    base = dict(id=10, motorista_id=1, origem="Campus", destino="Centro",
                saida=AGORA + timedelta(hours=3), vagas=2, custo_total=Decimal("9.00"))
    base.update(kw)
    return d.Viagem(**base)


@pytest.fixture
def repo():
    r = Mock(spec=RepositorioCarona)
    r.notas_do_motorista.return_value = []
    r.reservas_ativas.return_value = []
    r.obter_viagem.return_value = viagem()
    r.inserir_viagem.side_effect = lambda v: v
    r.reservar_se_ha_vaga.side_effect = lambda rv, vagas: rv
    return r


@pytest.fixture
def servico(repo):
    return ServicoCarona(repo, lambda: AGORA)


# --- CT-01 · criação -------------------------------------------------------------
def test_ct01_criar_viagem_valida_persiste_uma_vez(servico, repo):
    v = servico.criar_viagem(1, " Campus ", "Centro", AGORA + timedelta(hours=2), 3, Decimal("12.00"))
    repo.inserir_viagem.assert_called_once()
    assert v.origem == "Campus" and v.vagas == 3


@pytest.mark.parametrize("origem, destino", [("", "Centro"), ("Campus", "  "), (None, "Centro"),
                                             ("Centro", "centro")])
def test_ct01_origem_e_destino_obrigatorios_e_diferentes(servico, repo, origem, destino):
    with pytest.raises(d.DadoInvalido):
        servico.criar_viagem(1, origem, destino, AGORA + timedelta(hours=2), 3, Decimal("12"))
    repo.inserir_viagem.assert_not_called()


def test_ct01_custo_nao_positivo_e_recusado(servico, repo):
    with pytest.raises(d.DadoInvalido):
        servico.criar_viagem(1, "A", "B", AGORA + timedelta(hours=2), 3, Decimal("0"))
    repo.inserir_viagem.assert_not_called()


# --- CT-13 · motorista suspenso ----------------------------------------------------
def test_ct13_motorista_suspenso_nao_cria_viagem(servico, repo):
    repo.notas_do_motorista.return_value = [1, 1, 2, 2, 1]
    with pytest.raises(d.MotoristaSuspenso):
        servico.criar_viagem(1, "A", "B", AGORA + timedelta(hours=2), 3, Decimal("12"))
    repo.inserir_viagem.assert_not_called()


def test_ct13_motorista_com_media_exata_3_ainda_cria_viagem(servico, repo):
    repo.notas_do_motorista.return_value = [3, 3, 3, 3, 3]
    servico.criar_viagem(1, "A", "B", AGORA + timedelta(hours=2), 3, Decimal("12"))
    repo.inserir_viagem.assert_called_once()


# --- CT-05, CT-06, CT-07 · reservas --------------------------------------------------
def test_ct05_reservar_grava_o_valor_calculado_pelo_dominio(servico, repo):
    rv = servico.reservar(10, passageiro_id=2)
    assert rv.valor == Decimal("3.00") and rv.passageiro_id == 2   # 9,00 / (2+1)
    repo.reservar_se_ha_vaga.assert_called_once()


def test_ct06_ultima_vaga_e_aceita_e_a_seguinte_e_recusada(servico, repo):
    repo.reservas_ativas.return_value = [d.Reserva(1, 10, 3, Decimal("3.00"))]   # 1 de 2
    servico.reservar(10, passageiro_id=2)
    repo.reservas_ativas.return_value = [d.Reserva(1, 10, 3, Decimal("3.00")),
                                         d.Reserva(2, 10, 4, Decimal("3.00"))]   # 2 de 2
    repo.reservar_se_ha_vaga.reset_mock()
    with pytest.raises(d.ConflitoDeRegra, match="lotada"):
        servico.reservar(10, passageiro_id=2)
    repo.reservar_se_ha_vaga.assert_not_called()


def test_ct06_se_o_repositorio_diz_que_lotou_na_hora_o_servico_recusa(servico, repo):
    """A checagem do serviço passou, mas outra requisição pegou a vaga antes (corrida)."""
    repo.reservas_ativas.return_value = []
    repo.reservar_se_ha_vaga.side_effect = None
    repo.reservar_se_ha_vaga.return_value = None
    with pytest.raises(d.ConflitoDeRegra, match="lotada"):
        servico.reservar(10, passageiro_id=2)


@pytest.mark.parametrize("passageiro, ativas, saida, trecho", [
    (1, [], AGORA + timedelta(hours=3), "própria viagem"),
    (2, [d.Reserva(1, 10, 2, Decimal("3"))], AGORA + timedelta(hours=3), "já tem reserva"),
    (2, [], AGORA, "já começou"),
    (2, [], AGORA - timedelta(minutes=1), "já começou"),
])
def test_ct07_reservas_invalidas_sao_recusadas_sem_gravar(servico, repo, passageiro, ativas, saida, trecho):
    repo.obter_viagem.return_value = viagem(saida=saida)
    repo.reservas_ativas.return_value = ativas
    with pytest.raises(d.ConflitoDeRegra, match=trecho):
        servico.reservar(10, passageiro)
    repo.reservar_se_ha_vaga.assert_not_called()


def test_ct07_viagem_inexistente_ou_cancelada_nao_e_encontrada(servico, repo):
    repo.obter_viagem.return_value = None
    with pytest.raises(d.NaoEncontrado):
        servico.reservar(99, 2)
    repo.obter_viagem.return_value = viagem(cancelada=True)
    with pytest.raises(d.NaoEncontrado):
        servico.reservar(10, 2)


# --- CT-08, CT-09, CT-10 · cancelamento de reserva --------------------------------------
def test_ct08_cancelar_com_antecedencia_nao_cobra_taxa(servico, repo):
    repo.obter_reserva.return_value = d.Reserva(5, 10, 2, Decimal("6.00"))
    r = servico.cancelar_reserva(5, usuario_id=2)
    assert r.cancelada and r.taxa == Decimal("0.00")
    repo.atualizar_reserva.assert_called_once_with(r)


def test_ct09_cancelar_em_cima_da_hora_cobra_metade(servico, repo):
    repo.obter_viagem.return_value = viagem(saida=AGORA + timedelta(minutes=59, seconds=59))
    repo.obter_reserva.return_value = d.Reserva(5, 10, 2, Decimal("6.00"))
    assert servico.cancelar_reserva(5, 2).taxa == Decimal("3.00")


def test_ct10_cancelar_depois_da_saida_e_recusado_sem_gravar(servico, repo):
    repo.obter_viagem.return_value = viagem(saida=AGORA)
    repo.obter_reserva.return_value = d.Reserva(5, 10, 2, Decimal("6.00"))
    with pytest.raises(d.ConflitoDeRegra):
        servico.cancelar_reserva(5, 2)
    repo.atualizar_reserva.assert_not_called()


def test_ct10_so_o_dono_cancela_e_nao_cancela_duas_vezes(servico, repo):
    repo.obter_reserva.return_value = d.Reserva(5, 10, 2, Decimal("6.00"))
    with pytest.raises(d.SemPermissao):
        servico.cancelar_reserva(5, usuario_id=3)
    repo.obter_reserva.return_value = d.Reserva(5, 10, 2, Decimal("6.00"), cancelada=True)
    with pytest.raises(d.ConflitoDeRegra, match="já cancelada"):
        servico.cancelar_reserva(5, 2)
    repo.obter_reserva.return_value = None
    with pytest.raises(d.NaoEncontrado):
        servico.cancelar_reserva(5, 2)


# --- CT-11 · avaliação ----------------------------------------------------------------
def passou(servico, repo, **kw):
    repo.obter_viagem.return_value = viagem(saida=AGORA - timedelta(hours=1))
    repo.reservas_ativas.return_value = [d.Reserva(1, 10, 2, Decimal("3.00"))]
    repo.avaliacao_existe.return_value = False


def test_ct11_passageiro_que_viajou_avalia(servico, repo):
    passou(servico, repo)
    servico.avaliar(10, 2, 5)
    repo.inserir_avaliacao.assert_called_once_with(10, 2, 5)


@pytest.mark.parametrize("quem, saida_delta, ja_avaliou, excecao", [
    (2, timedelta(hours=1), False, d.ConflitoDeRegra),    # ainda não saiu
    (3, timedelta(hours=-1), False, d.SemPermissao),      # não tinha reserva
    (2, timedelta(hours=-1), True, d.ConflitoDeRegra),    # já avaliou
])
def test_ct11_avaliacoes_recusadas(servico, repo, quem, saida_delta, ja_avaliou, excecao):
    passou(servico, repo)
    repo.obter_viagem.return_value = viagem(saida=AGORA + saida_delta)
    repo.avaliacao_existe.return_value = ja_avaliou
    with pytest.raises(excecao):
        servico.avaliar(10, quem, 4)
    repo.inserir_avaliacao.assert_not_called()


def test_ct11_nota_invalida_nem_consulta_o_repositorio(servico, repo):
    with pytest.raises(d.DadoInvalido):
        servico.avaliar(10, 2, 6)
    repo.obter_viagem.assert_not_called()


# --- CT-12 · reputação ------------------------------------------------------------------
def test_ct12_reputacao_informa_media_e_suspensao(servico, repo):
    repo.obter_usuario.return_value = d.Usuario(1, "1", "Ana")
    repo.notas_do_motorista.return_value = [3, 3, 3, 3, 2]
    assert servico.reputacao(1) == {"motorista_id": 1, "avaliacoes": 5, "media": "2.80", "suspenso": True}
    repo.notas_do_motorista.return_value = []
    assert servico.reputacao(1)["media"] is None


def test_ct12_reputacao_de_usuario_inexistente(servico, repo):
    repo.obter_usuario.return_value = None
    with pytest.raises(d.NaoEncontrado):
        servico.reputacao(99)


# --- CT-14 · alterar e cancelar viagem ------------------------------------------------------
def test_ct14_motorista_altera_viagem_sem_reservas(servico, repo):
    v = servico.atualizar_viagem(10, 1, vagas=4, custo_total=Decimal("20.00"), destino="Rodoviária")
    assert (v.vagas, v.custo_total, v.destino) == (4, Decimal("20.00"), "Rodoviária")
    repo.atualizar_viagem.assert_called_once()


@pytest.mark.parametrize("campos", [{"vagas": 7}, {"custo_total": Decimal("0")}, {"destino": "campus"},
                                    {"origem": "   "}, {"destino": ""}, {"origem": None},
                                    {"saida": AGORA + timedelta(minutes=29)}])
def test_ct14_alteracao_invalida_nao_grava(servico, repo, campos):
    with pytest.raises(d.DadoInvalido):
        servico.atualizar_viagem(10, 1, **campos)
    repo.atualizar_viagem.assert_not_called()


def test_ct14_outro_usuario_nao_altera_nem_cancela(servico, repo):
    with pytest.raises(d.SemPermissao):
        servico.atualizar_viagem(10, 2, vagas=3)
    with pytest.raises(d.SemPermissao):
        servico.excluir_viagem(10, 2)
    repo.atualizar_viagem.assert_not_called()


def test_ct14_viagem_com_reservas_nao_muda_nem_e_cancelada(servico, repo):
    repo.reservas_ativas.return_value = [d.Reserva(1, 10, 2, Decimal("3.00"))]
    with pytest.raises(d.ConflitoDeRegra):
        servico.atualizar_viagem(10, 1, vagas=3)
    with pytest.raises(d.ConflitoDeRegra):
        servico.excluir_viagem(10, 1)
    repo.atualizar_viagem.assert_not_called()


def test_ct14_excluir_viagem_marca_como_cancelada(servico, repo):
    servico.excluir_viagem(10, 1)
    assert repo.atualizar_viagem.call_args.args[0].cancelada is True


# --- CT-16 · relatório -------------------------------------------------------------------------
def test_ct16_relatorio_de_ocupacao(servico, repo):
    repo.ocupacao_por_viagem.return_value = [(1, 4, 3), (2, 2, 0), (3, 3, 3)]
    r = servico.relatorio_ocupacao()
    assert (r["viagens"], r["vagas"], r["ocupadas"], r["taxa_ocupacao"]) == (3, 9, 6, 66.7)


def test_ct16_relatorio_sem_viagens_nao_divide_por_zero(servico, repo):
    repo.ocupacao_por_viagem.return_value = []
    assert servico.relatorio_ocupacao()["taxa_ocupacao"] == 0.0
