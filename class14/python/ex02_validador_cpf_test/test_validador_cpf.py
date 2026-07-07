"""Testes pytest para o Exercício 02 — Validador de CPF.

Este é o estudo de caso citado na seção "Artefatos no Repositório" e no
slide de estudo de caso da Aula 14: implementação real do algoritmo de
dígito verificador módulo 11, testada com fixtures e `@pytest.mark.parametrize`.
"""
import pytest

from validador_cpf import ValidadorCPF


@pytest.fixture
def validador():
    """Fixture de escopo `function`: nova instância a cada teste."""
    return ValidadorCPF()


def test_cpf_valido_deve_retornar_true(validador):
    assert validador.validar("529.982.247-25") is True


def test_cpf_valido_sem_formatacao_deve_retornar_true(validador):
    assert validador.validar("52998224725") is True


def test_cpf_none_deve_retornar_false(validador):
    assert validador.validar(None) is False


def test_cpf_tamanho_invalido_deve_retornar_false(validador):
    assert validador.validar("123.456.789") is False


def test_cpf_com_caracteres_invalidos_e_tamanho_correto(validador):
    # 11 dígitos numéricos, mas com dígito verificador incorreto
    assert validador.validar("123.456.789-00") is False


@pytest.mark.parametrize("cpf", [
    "111.111.111-11",
    "222.222.222-22",
    "000.000.000-00",
    "999.999.999-99",
])
def test_cpf_com_todos_digitos_iguais_deve_retornar_false(validador, cpf):
    """CPFs com todos os dígitos iguais passariam no cálculo numérico do
    módulo 11 "por acaso", mas nunca são emitidos na prática — por isso
    o validador precisa rejeitá-los explicitamente."""
    assert validador.validar(cpf) is False


@pytest.mark.parametrize("cpf_valido", [
    "529.982.247-25",
    "390.533.447-05",
    "111.444.777-35",
])
def test_cpfs_validos_conhecidos(validador, cpf_valido):
    assert validador.validar(cpf_valido) is True


@pytest.mark.parametrize("cpf_invalido", [
    "529.982.247-24",  # último dígito verificador errado
    "529.982.247-15",  # primeiro dígito verificador errado
    "529.982.247-99",  # ambos os dígitos verificadores errados
])
def test_cpfs_invalidos_por_digito_verificador(validador, cpf_invalido):
    assert validador.validar(cpf_invalido) is False
