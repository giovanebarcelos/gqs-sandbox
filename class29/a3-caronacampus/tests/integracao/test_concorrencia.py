"""Integração: reservas simultâneas disputando a última vaga (RN-01)."""
from datetime import timedelta
from decimal import Decimal
from threading import Barrier, Thread

from carona import dominio as d


def test_ct06_duas_reservas_simultaneas_para_a_ultima_vaga_so_uma_vence(servico, relogio):
    v = servico.criar_viagem(1, "A", "B", relogio() + timedelta(hours=2), 1, Decimal("6"))
    largada, resultados = Barrier(6), []

    def tentar(passageiro):
        largada.wait()
        try:
            servico.reservar(v.id, passageiro)
            resultados.append("ok")
        except d.ConflitoDeRegra:
            resultados.append("lotada")

    fios = [Thread(target=tentar, args=(p,)) for p in (2, 3, 4, 5, 6, 7)]
    [f.start() for f in fios]
    [f.join() for f in fios]
    assert sorted(resultados) == ["lotada"] * 5 + ["ok"]
    assert len(servico.repo.reservas_ativas(v.id)) == 1
