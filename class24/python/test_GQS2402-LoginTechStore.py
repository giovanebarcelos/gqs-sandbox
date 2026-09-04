"""
test_GQS2402-LoginTechStore.py
Garantia da Qualidade de Software - Aula 24

Suite automatizada que espelha os tres casos de teste manuais cadastrados no
Kiwi TCMS durante o laboratorio:

    TS-001 Login com credenciais validas      -> test_login_com_credenciais_validas
    TS-002 Login com senha incorreta          -> test_login_com_senha_incorreta
    TS-003 Bloqueio apos 3 tentativas         -> test_bloqueio_apos_3_tentativas

A funcao autenticar() abaixo contem, de proposito, o mesmo defeito registrado
na execucao manual (TS-002-D1): a mensagem de erro revela se o e-mail existe
na base, o que permite enumerar usuarios. Por isso o teste TS-002 falha - e
essa falha e' exatamente o que deve aparecer no Kiwi TCMS como FAILED.

COMO USAR
    pip install pytest
    pytest -q --junitxml=resultados.xml

    # publicar no Kiwi TCMS (ver GQS24-kiwi-publicar-resultados.py):
    export TCMS_PRODUCT="TechStore"
    export TCMS_PRODUCT_VERSION="1.5.0"
    export TCMS_BUILD="v1.5.0-RC1"
    export TCMS_PLAN_ID=1          # publica dentro do plano criado no lab
    python3 GQS24-kiwi-publicar-resultados.py resultados.xml
"""

# Base de usuarios simulada. Em um sistema real isto seria o banco de dados.
USUARIOS = {"ana@techstore.local": "Senha!2026"}

# Contador de tentativas erradas por e-mail (simula o controle de bloqueio).
_tentativas_erradas = {}

LIMITE_DE_TENTATIVAS = 3


def autenticar(email, senha):
    """Codigo sob teste: autentica um usuario da TechStore."""
    if _tentativas_erradas.get(email, 0) >= LIMITE_DE_TENTATIVAS:
        return {"ok": False, "mensagem": "Conta bloqueada por 15 minutos"}

    if USUARIOS.get(email) == senha:
        _tentativas_erradas[email] = 0
        return {"ok": True, "mensagem": "Bem-vindo"}

    _tentativas_erradas[email] = _tentativas_erradas.get(email, 0) + 1

    # DEFEITO TS-002-D1 (severidade Alta): a mensagem diferencia "senha
    # incorreta" de "usuario nao encontrado" e permite enumerar contas.
    # O correto seria devolver "Credenciais invalidas" nos dois casos.
    if email in USUARIOS:
        return {"ok": False, "mensagem": "Senha incorreta"}
    return {"ok": False, "mensagem": "Usuario nao encontrado"}


def test_login_com_credenciais_validas():
    """TS-001: usuario cadastrado e ativo entra com e-mail e senha corretos."""
    resultado = autenticar("ana@techstore.local", "Senha!2026")

    assert resultado["ok"] is True
    assert resultado["mensagem"] == "Bem-vindo"


def test_login_com_senha_incorreta():
    """TS-002: a mensagem nao pode revelar se o e-mail existe.

    Este teste FALHA de proposito - ele denuncia o defeito TS-002-D1.
    """
    resultado = autenticar("ana@techstore.local", "senha-errada")

    assert resultado["ok"] is False
    assert resultado["mensagem"] == "Credenciais invalidas"


def test_bloqueio_apos_3_tentativas():
    """TS-003: a conta e' bloqueada na terceira senha errada."""
    for _ in range(LIMITE_DE_TENTATIVAS):
        autenticar("bia@techstore.local", "senha-errada")

    resultado = autenticar("bia@techstore.local", "senha-errada")

    assert resultado["ok"] is False
    assert "bloqueada" in resultado["mensagem"]
