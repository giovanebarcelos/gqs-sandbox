"""Integração: serviço + SQLite real (arquivo temporário). Persistência e fluxos de negócio."""
import sqlite3
from datetime import timedelta
from decimal import Decimal

import pytest

from carona import dominio as d
from carona.repositorio import RepositorioSqlite


# --- CT-01 · persistência --------------------------------------------------------
def test_ct01_viagem_criada_e_lida_de_volta_com_todos_os_campos(servico, relogio):
    saida = relogio() + timedelta(hours=2)
    criada = servico.criar_viagem(1, "Campus", "Centro", saida, 4, Decimal("20.00"))
    lida = servico.obter_viagem(criada.id)
    assert (lida.origem, lida.destino, lida.saida, lida.vagas, lida.custo_total, lida.motorista_id) == \
           ("Campus", "Centro", saida, 4, Decimal("20.00"), 1)


def test_ct01_dados_sobrevivem_a_um_novo_repositorio_no_mesmo_arquivo(repo, servico, relogio):
    v = servico.criar_viagem(1, "A", "B", relogio() + timedelta(hours=2), 2, Decimal("9.99"))
    outro = RepositorioSqlite(repo.caminho)
    assert outro.obter_viagem(v.id).custo_total == Decimal("9.99")


def test_ct01_check_do_banco_barra_vagas_invalidas_mesmo_sem_o_servico(repo, relogio):
    with pytest.raises(sqlite3.IntegrityError):
        repo.inserir_viagem(d.Viagem(0, 1, "A", "B", relogio() + timedelta(hours=2), 9, Decimal("5")))


def test_ct01_chave_estrangeira_barra_viagem_de_motorista_inexistente(repo, relogio):
    with pytest.raises(sqlite3.IntegrityError):
        repo.inserir_viagem(d.Viagem(0, 999, "A", "B", relogio() + timedelta(hours=2), 2, Decimal("5")))


# --- CT-05, CT-06 · reserva e lotação, ponta a ponta -------------------------------
def test_ct05_ct06_viagem_de_3_vagas_aceita_3_reservas_e_recusa_a_4a(servico, viagem):
    for passageiro in (2, 3, 4):
        assert servico.reservar(viagem.id, passageiro).valor == Decimal("3.00")
    with pytest.raises(d.ConflitoDeRegra, match="lotada"):
        servico.reservar(viagem.id, 5)


def test_ct06_vaga_liberada_pelo_cancelamento_pode_ser_reocupada(servico, viagem):
    r1 = servico.reservar(viagem.id, 2)
    servico.reservar(viagem.id, 3)
    servico.reservar(viagem.id, 4)
    servico.cancelar_reserva(r1.id, 2)
    servico.reservar(viagem.id, 5)


def test_ct07_nao_reserva_em_viagem_que_ja_saiu(servico, viagem, relogio):
    relogio.avancar(hours=3)
    with pytest.raises(d.ConflitoDeRegra, match="já começou"):
        servico.reservar(viagem.id, 2)


# --- CT-08, CT-09, CT-10 · cancelamento com relógio controlado --------------------------
@pytest.mark.parametrize("avanco, taxa", [
    (timedelta(hours=1, seconds=-1), "0.00"),       # faltam 2 h 00 min 01 s
    (timedelta(hours=2), "0.00"),                   # faltam exatamente 60 min -> sem taxa
    (timedelta(hours=2, seconds=1), "1.50"),        # faltam 59 min 59 s -> taxa
    (timedelta(hours=2, minutes=59), "1.50"),
])
def test_ct08_ct09_taxa_no_limite_de_60_minutos(servico, viagem, relogio, avanco, taxa):
    r = servico.reservar(viagem.id, 2)                # valor R$ 3,00
    relogio.avancar(seconds=avanco.total_seconds())
    cancelada = servico.cancelar_reserva(r.id, 2)
    assert cancelada.taxa == Decimal(taxa)
    assert servico.repo.obter_reserva(r.id).taxa == Decimal(taxa)      # gravou de verdade


def test_ct10_apos_a_saida_nao_cancela_e_a_reserva_continua_ativa(servico, viagem, relogio):
    r = servico.reservar(viagem.id, 2)
    relogio.avancar(hours=3)
    with pytest.raises(d.ConflitoDeRegra):
        servico.cancelar_reserva(r.id, 2)
    assert servico.repo.obter_reserva(r.id).cancelada is False


# --- CT-11, CT-12, CT-13 · avaliação, suspensão e efeito ---------------------------------
def viagens_concluidas(servico, relogio, passageiros, notas):
    """Cria uma viagem por nota (motorista 1), cada uma com um passageiro que a avalia depois."""
    for passageiro, nota in zip(passageiros, notas):
        v = servico.criar_viagem(1, "A", "B", relogio() + timedelta(hours=1), 2, Decimal("6.00"))
        servico.reservar(v.id, passageiro)
        relogio.avancar(hours=2)
        servico.avaliar(v.id, passageiro, nota)


def test_ct11_nao_avalia_antes_da_saida_nem_sem_reserva_nem_duas_vezes(servico, viagem, relogio):
    servico.reservar(viagem.id, 2)
    with pytest.raises(d.ConflitoDeRegra):
        servico.avaliar(viagem.id, 2, 5)
    relogio.avancar(hours=3)
    with pytest.raises(d.SemPermissao):
        servico.avaliar(viagem.id, 3, 5)
    servico.avaliar(viagem.id, 2, 5)
    with pytest.raises(d.ConflitoDeRegra):
        servico.avaliar(viagem.id, 2, 4)


def test_ct11_reserva_cancelada_nao_da_direito_a_avaliar(servico, viagem, relogio):
    r = servico.reservar(viagem.id, 2)
    servico.cancelar_reserva(r.id, 2)
    relogio.avancar(hours=3)
    with pytest.raises(d.SemPermissao):
        servico.avaliar(viagem.id, 2, 1)


def test_ct12_ct13_quinta_avaliacao_baixa_suspende_e_bloqueia_nova_viagem(servico, relogio):
    viagens_concluidas(servico, relogio, [2, 3, 4, 5, 6], [2, 3, 3, 3, 3])      # média 2,8
    rep = servico.reputacao(1)
    assert (rep["avaliacoes"], rep["media"], rep["suspenso"]) == (5, "2.80", True)
    with pytest.raises(d.MotoristaSuspenso):
        servico.criar_viagem(1, "A", "B", relogio() + timedelta(hours=2), 2, Decimal("6"))


def test_ct12_ct13_media_exatamente_3_nao_suspende(servico, relogio):
    viagens_concluidas(servico, relogio, [2, 3, 4, 5, 6], [3, 3, 3, 3, 3])
    assert servico.reputacao(1)["suspenso"] is False
    servico.criar_viagem(1, "A", "B", relogio() + timedelta(hours=2), 2, Decimal("6"))


def test_ct12_quatro_avaliacoes_pessimas_ainda_nao_suspendem(servico, relogio):
    viagens_concluidas(servico, relogio, [2, 3, 4, 5], [1, 1, 1, 1])
    assert servico.reputacao(1)["suspenso"] is False


# --- CT-14 · alterar e cancelar viagem ------------------------------------------------------
def test_ct14_alteracao_e_cancelamento_persistem(servico, viagem):
    servico.atualizar_viagem(viagem.id, 1, vagas=5, destino="Aeroporto")
    lida = servico.obter_viagem(viagem.id)
    assert (lida.vagas, lida.destino) == (5, "Aeroporto")
    servico.excluir_viagem(viagem.id, 1)
    with pytest.raises(d.NaoEncontrado):
        servico.obter_viagem(viagem.id)
    assert viagem.id not in [v.id for v in servico.listar_viagens()]


def test_ct14_depois_que_a_reserva_e_cancelada_a_viagem_pode_ser_cancelada(servico, viagem):
    r = servico.reservar(viagem.id, 2)
    with pytest.raises(d.ConflitoDeRegra):
        servico.excluir_viagem(viagem.id, 1)
    servico.cancelar_reserva(r.id, 2)
    servico.excluir_viagem(viagem.id, 1)


# --- CT-16 · relatório ----------------------------------------------------------------------
def test_ct16_relatorio_conta_so_reservas_ativas_de_viagens_abertas(servico, viagem, relogio):
    outra = servico.criar_viagem(3, "X", "Y", relogio() + timedelta(hours=2), 2, Decimal("6"))
    servico.reservar(viagem.id, 2)
    r2 = servico.reservar(viagem.id, 4)
    servico.cancelar_reserva(r2.id, 4)
    servico.reservar(outra.id, 2)
    rel = servico.relatorio_ocupacao()
    assert (rel["viagens"], rel["vagas"], rel["ocupadas"], rel["taxa_ocupacao"]) == (2, 5, 2, 40.0)


# --- CT-07 · o banco também barra a reserva duplicada (corrida entre duas requisições) -----------
def test_ct07_indice_unico_barra_reserva_duplicada_mesmo_sem_a_checagem_do_servico(repo, viagem):
    repo.reservar_se_ha_vaga(d.Reserva(0, viagem.id, 2, Decimal("3.00")), viagem.vagas)
    with pytest.raises(d.ConflitoDeRegra, match="já tem reserva"):
        repo.reservar_se_ha_vaga(d.Reserva(0, viagem.id, 2, Decimal("3.00")), viagem.vagas)
    assert len(repo.reservas_ativas(viagem.id)) == 1


def test_ct07_reserva_cancelada_nao_impede_nova_reserva_do_mesmo_passageiro(servico, viagem):
    r = servico.reservar(viagem.id, 2)
    servico.cancelar_reserva(r.id, 2)
    assert servico.reservar(viagem.id, 2).id != r.id


# --- CT-06 · o limite de vagas é garantido pelo repositório, sem depender do serviço ----------------
def test_ct06_repositorio_recusa_a_reserva_alem_das_vagas_sem_ajuda_do_servico(repo, relogio):
    v = repo.inserir_viagem(d.Viagem(0, 1, "A", "B", relogio() + timedelta(hours=2), 1, Decimal("6")))
    assert repo.reservar_se_ha_vaga(d.Reserva(0, v.id, 2, Decimal("3.00")), v.vagas) is not None
    assert repo.reservar_se_ha_vaga(d.Reserva(0, v.id, 3, Decimal("3.00")), v.vagas) is None
    assert len(repo.reservas_ativas(v.id)) == 1
