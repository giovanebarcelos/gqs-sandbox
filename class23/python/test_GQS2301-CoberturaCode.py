"""Testes pytest para o Exemplo de Cobertura da Aula 23.

Demonstra os conceitos dos Slides 3, 5 e 6 (tipos de cobertura, coverage.py
e interpretação de relatório):
- `validar_senha` e `calcular_desconto` ficam 100% cobertos (instrução e ramo)
- `classificar_nota` é testado APENAS nos ramos A, B e C de propósito, para
  que `pytest --cov=GQS2301-CoberturaCode --cov-report=term-missing` mostre
  linhas "Miss" reais (ramos D e F) — ver Slide "Saída Real de Cobertura".

Observação sobre o import: o arquivo do módulo usa hífen no nome
(`GQS2301-CoberturaCode.py`), seguindo a convenção de nomenclatura do
repositório da disciplina (GQSxxxx-NomeDoArquivo.py). Como hífen não é
válido em `import`, o módulo é carregado dinamicamente via `importlib`.
"""
import importlib.util
import pathlib

import pytest

_MODULE_PATH = pathlib.Path(__file__).parent / "GQS2301-CoberturaCode.py"
_spec = importlib.util.spec_from_file_location("GQS2301_CoberturaCode", _MODULE_PATH)
_modulo = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(_modulo)

validar_senha = _modulo.validar_senha
calcular_desconto = _modulo.calcular_desconto
classificar_nota = _modulo.classificar_nota


# ---------------------------------------------------------------------------
# validar_senha — cobertura de instrução E de ramo (100%)
# ---------------------------------------------------------------------------

def test_senha_valida_retorna_true():
    assert validar_senha("Abc12345") is True


@pytest.mark.parametrize("senha", [
    "abc12345",   # sem maiúscula
    "ABC12345",   # sem minúscula
    "Abcdefgh",   # sem dígito
    "Ab1",        # sem tamanho mínimo
])
def test_senha_invalida_retorna_false(senha):
    assert validar_senha(senha) is False


@pytest.mark.parametrize("senha", [None, ""])
def test_senha_vazia_ou_none_lanca_excecao(senha):
    with pytest.raises(ValueError) as exc_info:
        validar_senha(senha)
    assert "vazia ou None" in str(exc_info.value)


# ---------------------------------------------------------------------------
# calcular_desconto — cobertura de instrução E de ramo (100%)
# ---------------------------------------------------------------------------

@pytest.mark.parametrize("tipo, esperado", [
    ("regular", 100.0),
    ("silver", 95.0),
    ("gold", 90.0),
    ("platinum", 85.0),
])
def test_calcular_desconto_por_tipo_de_cliente(tipo, esperado):
    assert calcular_desconto(100.0, tipo) == pytest.approx(esperado)


def test_calcular_desconto_valor_negativo_lanca_excecao():
    with pytest.raises(ValueError) as exc_info:
        calcular_desconto(-10.0, "regular")
    assert "negativo" in str(exc_info.value)


def test_calcular_desconto_tipo_desconhecido_lanca_excecao():
    with pytest.raises(ValueError) as exc_info:
        calcular_desconto(100.0, "diamante")
    assert "desconhecido" in str(exc_info.value)


# ---------------------------------------------------------------------------
# classificar_nota — cobertura PARCIAL de propósito (ramos D e F não testados)
# ---------------------------------------------------------------------------

def test_classificar_nota_conceito_a():
    assert classificar_nota(9.5) == "A"


def test_classificar_nota_conceito_b():
    assert classificar_nota(7.0) == "B"


def test_classificar_nota_conceito_c():
    assert classificar_nota(5.0) == "C"

# Propositalmente NÃO há testes para nota < 5 (conceitos D e F) nem para o
# ValueError de nota fora do intervalo [0, 10]. Isso deixa "Miss" real no
# relatório de cobertura, usado como exemplo no Slide de interpretação.
