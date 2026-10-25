"""Integração: a montagem real (`app.montar`) com os usuários fictícios da demonstração."""
from carona.app import USUARIOS_DEMO, montar


def test_ct15_montar_cria_os_usuarios_de_demo_e_permite_login(tmp_path):
    c = montar(str(tmp_path / "demo.db")).test_client()
    for matricula, _, senha in USUARIOS_DEMO:
        assert c.post("/auth/login", json={"matricula": matricula, "senha": senha}).status_code == 200


def test_ct15_montar_duas_vezes_no_mesmo_banco_nao_duplica_usuarios(tmp_path):
    caminho = str(tmp_path / "demo.db")
    montar(caminho)
    c = montar(caminho).test_client()                       # reinício do servidor
    assert c.post("/auth/login", json={"matricula": "2026001", "senha": "ana123"}).status_code == 200


def test_ct15_montar_em_modo_normal_nao_expoe_o_relogio(tmp_path):
    assert montar(str(tmp_path / "d.db")).test_client().post("/teste/avancar-relogio", json={}).status_code == 404


def test_ct15_montar_em_modo_de_teste_expoe_o_relogio(tmp_path):
    c = montar(str(tmp_path / "d.db"), modo_teste=True).test_client()
    assert c.post("/teste/avancar-relogio", json={"minutos": 10}).status_code == 200
