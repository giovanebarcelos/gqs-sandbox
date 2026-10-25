"""Integração: API HTTP (Flask) + serviço + SQLite. Contrato de status, JSON e autenticação."""
from datetime import timedelta

import pytest

from conftest import login


def corpo_viagem(relogio, **kw):
    base = {"origem": "Campus", "destino": "Centro", "vagas": 3, "custo_total": "12.00",
            "saida": (relogio() + timedelta(hours=3)).isoformat()}
    base.update(kw)
    return base


@pytest.fixture
def ana(cliente):
    return login(cliente, "1", "a")


@pytest.fixture
def bruno(cliente):
    return login(cliente, "2", "b")


# --- CT-15 · autenticação (RF-08, RNF-02) -------------------------------------------
def test_ct15_login_valido_devolve_token_e_usuario(cliente):
    r = cliente.post("/auth/login", json={"matricula": "1", "senha": "a"})
    assert r.status_code == 200 and r.get_json()["usuario_id"] == 1 and len(r.get_json()["token"]) == 32


@pytest.mark.parametrize("dados", [{"matricula": "1", "senha": "errada"}, {"matricula": "999", "senha": "a"},
                                   {"matricula": "1"}, {}])
def test_ct15_login_invalido_devolve_401(cliente, dados):
    assert cliente.post("/auth/login", json=dados).status_code == 401


@pytest.mark.parametrize("metodo, caminho", [("get", "/viagens"), ("post", "/viagens"), ("get", "/viagens/1"),
                                             ("put", "/viagens/1"), ("delete", "/viagens/1"),
                                             ("post", "/viagens/1/reservas"), ("delete", "/reservas/1"),
                                             ("post", "/viagens/1/avaliacoes"),
                                             ("get", "/motoristas/1/reputacao"), ("get", "/relatorios/ocupacao")])
def test_ct15_todo_endpoint_protegido_exige_token(cliente, metodo, caminho):
    r = getattr(cliente, metodo)(caminho)
    assert r.status_code == 401 and r.get_json()["erro"] == "nao_autenticado"


def test_ct15_token_inventado_e_recusado(cliente):
    assert cliente.get("/viagens", headers={"Authorization": "Bearer xxx"}).status_code == 401
    assert cliente.get("/viagens", headers={"Authorization": "Basic abc"}).status_code == 401


def test_ct15_saude_nao_exige_autenticacao(cliente):
    assert cliente.get("/saude").get_json() == {"status": "ok"}


# --- CT-01, CT-14 · CRUD de viagens ------------------------------------------------------
def test_ct01_criar_e_consultar_viagem(cliente, ana, relogio):
    r = cliente.post("/viagens", json=corpo_viagem(relogio), headers=ana)
    assert r.status_code == 201
    v = r.get_json()
    assert v["valor_por_vaga"] == "3.00" and v["motorista_id"] == 1
    assert cliente.get(f"/viagens/{v['id']}", headers=ana).get_json() == v
    assert [x["id"] for x in cliente.get("/viagens", headers=ana).get_json()] == [v["id"]]


@pytest.mark.parametrize("alteracao, codigo", [
    ({"vagas": 0}, 400), ({"vagas": 7}, 400), ({"custo_total": "-1"}, 400), ({"custo_total": "abc"}, 400),
    ({"origem": ""}, 400), ({"saida": "ontem"}, 400), ({"destino": "campus"}, 400),
])
def test_ct01_dados_invalidos_devolvem_400_com_erro_padronizado(cliente, ana, relogio, alteracao, codigo):
    r = cliente.post("/viagens", json=corpo_viagem(relogio, **alteracao), headers=ana)
    assert r.status_code == codigo and r.get_json()["erro"] == "dado_invalido"


def test_ct01_corpo_ausente_ou_nao_json_devolve_400(cliente, ana):
    assert cliente.post("/viagens", headers=ana).status_code == 400
    assert cliente.post("/viagens", data="texto", headers=ana).status_code == 400


def test_ct14_alterar_e_cancelar_viagem_pela_api(cliente, ana, relogio):
    vid = cliente.post("/viagens", json=corpo_viagem(relogio), headers=ana).get_json()["id"]
    r = cliente.put(f"/viagens/{vid}", json={"vagas": 5, "custo_total": "30.00",
                                              "origem": "Biblioteca", "destino": "Rodoviária",
                                              "saida": (relogio() + timedelta(hours=5)).isoformat()},
                    headers=ana)
    assert r.status_code == 200 and r.get_json()["vagas"] == 5 and r.get_json()["valor_por_vaga"] == "5.00"
    assert cliente.delete(f"/viagens/{vid}", headers=ana).status_code == 204
    assert cliente.get(f"/viagens/{vid}", headers=ana).status_code == 404


def test_ct14_alteracao_com_origem_ou_destino_vazios_e_recusada(cliente, ana, relogio):
    vid = cliente.post("/viagens", json=corpo_viagem(relogio), headers=ana).get_json()["id"]
    assert cliente.put(f"/viagens/{vid}", json={"origem": "  "}, headers=ana).status_code == 400
    assert cliente.put(f"/viagens/{vid}", json={"destino": ""}, headers=ana).status_code == 400
    assert cliente.get(f"/viagens/{vid}", headers=ana).get_json()["origem"] == "Campus"      # nada foi gravado


def test_ct14_so_o_motorista_altera_e_viagem_com_reserva_e_imutavel(cliente, ana, bruno, relogio):
    vid = cliente.post("/viagens", json=corpo_viagem(relogio), headers=ana).get_json()["id"]
    assert cliente.put(f"/viagens/{vid}", json={"vagas": 2}, headers=bruno).status_code == 403
    assert cliente.delete(f"/viagens/{vid}", headers=bruno).status_code == 403
    cliente.post(f"/viagens/{vid}/reservas", headers=bruno)
    assert cliente.put(f"/viagens/{vid}", json={"vagas": 2}, headers=ana).status_code == 409
    assert cliente.delete(f"/viagens/{vid}", headers=ana).status_code == 409


def test_ct14_viagem_inexistente_devolve_404(cliente, ana):
    assert cliente.get("/viagens/999", headers=ana).status_code == 404
    assert cliente.put("/viagens/999", json={}, headers=ana).status_code == 404


# --- CT-05..CT-10 · reservas e cancelamento -------------------------------------------------
def test_ct05_reservar_devolve_201_com_valor(cliente, ana, bruno, relogio):
    vid = cliente.post("/viagens", json=corpo_viagem(relogio), headers=ana).get_json()["id"]
    r = cliente.post(f"/viagens/{vid}/reservas", headers=bruno)
    assert r.status_code == 201
    assert r.get_json()["valor"] == "3.00" and r.get_json()["cancelada"] is False


def test_ct07_reservas_invalidas_devolvem_409(cliente, ana, bruno, relogio):
    vid = cliente.post("/viagens", json=corpo_viagem(relogio), headers=ana).get_json()["id"]
    assert cliente.post(f"/viagens/{vid}/reservas", headers=ana).status_code == 409          # própria viagem
    assert cliente.post(f"/viagens/{vid}/reservas", headers=bruno).status_code == 201
    r = cliente.post(f"/viagens/{vid}/reservas", headers=bruno)                              # duplicada
    assert r.status_code == 409 and r.get_json()["erro"] == "conflito_de_regra"
    assert cliente.post("/viagens/999/reservas", headers=bruno).status_code == 404


def test_ct06_viagem_lotada_devolve_409(cliente, ana, relogio):
    vid = cliente.post("/viagens", json=corpo_viagem(relogio, vagas=1), headers=ana).get_json()["id"]
    assert cliente.post(f"/viagens/{vid}/reservas", headers=login(cliente, "2", "b")).status_code == 201
    r = cliente.post(f"/viagens/{vid}/reservas", headers=login(cliente, "3", "c"))
    assert r.status_code == 409 and "lotada" in r.get_json()["mensagem"]


def test_ct08_ct09_cancelar_reserva_com_e_sem_taxa_pela_api(cliente, ana, bruno, relogio):
    vid = cliente.post("/viagens", json=corpo_viagem(relogio), headers=ana).get_json()["id"]
    rid = cliente.post(f"/viagens/{vid}/reservas", headers=bruno).get_json()["id"]
    r = cliente.delete(f"/reservas/{rid}", headers=bruno)
    assert r.status_code == 200 and r.get_json()["taxa"] == "0.00" and r.get_json()["cancelada"] is True
    rid2 = cliente.post(f"/viagens/{vid}/reservas", headers=bruno).get_json()["id"]
    cliente.post("/teste/avancar-relogio", json={"minutos": 121})                            # faltam 59 min
    r2 = cliente.delete(f"/reservas/{rid2}", headers=bruno)
    assert r2.get_json()["taxa"] == "1.50"


def test_ct10_cancelar_reserva_de_outro_ou_inexistente(cliente, ana, bruno, relogio):
    vid = cliente.post("/viagens", json=corpo_viagem(relogio), headers=ana).get_json()["id"]
    rid = cliente.post(f"/viagens/{vid}/reservas", headers=bruno).get_json()["id"]
    assert cliente.delete(f"/reservas/{rid}", headers=ana).status_code == 403
    assert cliente.delete("/reservas/999", headers=bruno).status_code == 404
    cliente.delete(f"/reservas/{rid}", headers=bruno)
    assert cliente.delete(f"/reservas/{rid}", headers=bruno).status_code == 409


# --- CT-11, CT-12, CT-13, CT-16 · avaliação, reputação, relatório ---------------------------------
def test_ct11_avaliar_exige_ter_viajado(cliente, ana, bruno, relogio):
    vid = cliente.post("/viagens", json=corpo_viagem(relogio), headers=ana).get_json()["id"]
    cliente.post(f"/viagens/{vid}/reservas", headers=bruno)
    assert cliente.post(f"/viagens/{vid}/avaliacoes", json={"nota": 5}, headers=bruno).status_code == 409
    cliente.post("/teste/avancar-relogio", json={"minutos": 181})
    assert cliente.post(f"/viagens/{vid}/avaliacoes", json={"nota": 6}, headers=bruno).status_code == 400
    assert cliente.post(f"/viagens/{vid}/avaliacoes", json={"nota": 5}, headers=bruno).status_code == 201
    assert cliente.post(f"/viagens/{vid}/avaliacoes", json={"nota": 4}, headers=bruno).status_code == 409
    assert cliente.post(f"/viagens/{vid}/avaliacoes", json={"nota": 4}, headers=ana).status_code == 403


def test_ct12_reputacao_e_ct13_suspensao_ponta_a_ponta(cliente, ana, relogio):
    assert cliente.get("/motoristas/1/reputacao", headers=ana).get_json() == \
        {"motorista_id": 1, "avaliacoes": 0, "media": None, "suspenso": False}
    notas = [1, 1, 1, 1, 1]
    for passageiro, nota in zip(("2", "3", "4", "5", "6"), notas):
        vid = cliente.post("/viagens", json=corpo_viagem(relogio, saida=(relogio() + timedelta(hours=1)).isoformat()),
                           headers=ana).get_json()["id"]
        h = login(cliente, passageiro, {"2": "b", "3": "c", "4": "d", "5": "e", "6": "f"}[passageiro])
        cliente.post(f"/viagens/{vid}/reservas", headers=h)
        relogio.avancar(minutes=61)          # relógio compartilhado: o do teste e o do servidor andam juntos
        cliente.post(f"/viagens/{vid}/avaliacoes", json={"nota": nota}, headers=h)
    rep = cliente.get("/motoristas/1/reputacao", headers=ana).get_json()
    assert rep["suspenso"] is True and rep["media"] == "1.00"
    r = cliente.post("/viagens", json=corpo_viagem(relogio), headers=ana)
    assert r.status_code == 403 and r.get_json()["erro"] == "motorista_suspenso"
    assert cliente.get("/motoristas/999/reputacao", headers=ana).status_code == 404


def test_ct16_relatorio_de_ocupacao(cliente, ana, bruno, relogio):
    vid = cliente.post("/viagens", json=corpo_viagem(relogio, vagas=4), headers=ana).get_json()["id"]
    cliente.post(f"/viagens/{vid}/reservas", headers=bruno)
    rel = cliente.get("/relatorios/ocupacao", headers=ana).get_json()
    assert rel["viagens"] == 1 and rel["vagas"] == 4 and rel["ocupadas"] == 1 and rel["taxa_ocupacao"] == 25.0


def test_modo_de_teste_desligado_nao_expoe_o_relogio(repo, relogio):
    from carona.api import RelogioAjustavel, criar_app
    from carona.servico import ServicoCarona
    aj = RelogioAjustavel(base=relogio)
    c = criar_app(ServicoCarona(repo, aj), aj, modo_teste=False).test_client()
    assert c.post("/teste/avancar-relogio", json={"minutos": 5}).status_code == 404
