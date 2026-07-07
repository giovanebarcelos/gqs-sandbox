"""Exercício 02 — Validador de CPF (dígito verificador módulo 11).

Implementação de referência do algoritmo cobrado como Atividade Prática
na Aula 14 (`Aula_14_pytest_Testes_Python.md`, seção "Atividade Prática"),
que até então só apresentava a assinatura da classe sem a lógica real.

Algoritmo (módulo 11), resumido:
1. Remove tudo que não for dígito (pontos, hífen).
2. CPF deve ter exatamente 11 dígitos.
3. Rejeita sequências com todos os dígitos iguais (ex.: "111.111.111-11"),
   que são numericamente "válidas" pelo cálculo mas nunca são CPFs reais.
4. Calcula o 1º dígito verificador a partir dos 9 primeiros dígitos.
5. Calcula o 2º dígito verificador a partir dos 10 primeiros dígitos
   (os 9 originais + o 1º dígito verificador já calculado).
6. Compara os dígitos calculados com os dois últimos dígitos informados.
"""
import re


class ValidadorCPF:
    """Valida números de CPF usando o algoritmo oficial de dígito
    verificador módulo 11."""

    TAMANHO_CPF = 11

    def validar(self, cpf: str) -> bool:
        if cpf is None:
            return False

        cpf_numerico = self._somente_digitos(cpf)

        if len(cpf_numerico) != self.TAMANHO_CPF:
            return False

        if self._todos_digitos_iguais(cpf_numerico):
            return False

        primeiro_dv = self._calcular_digito_verificador(cpf_numerico[:9])
        if primeiro_dv != int(cpf_numerico[9]):
            return False

        segundo_dv = self._calcular_digito_verificador(cpf_numerico[:9] + str(primeiro_dv))
        if segundo_dv != int(cpf_numerico[10]):
            return False

        return True

    def _somente_digitos(self, cpf: str) -> str:
        return re.sub(r"\D", "", cpf)

    def _todos_digitos_iguais(self, cpf_numerico: str) -> bool:
        return cpf_numerico == cpf_numerico[0] * len(cpf_numerico)

    def _calcular_digito_verificador(self, base: str) -> int:
        """Calcula um dígito verificador módulo 11 a partir de `base`.

        O peso inicial é `len(base) + 1` e decresce até 2, multiplicando
        cada dígito. O resto da divisão por 11 (multiplicado por 10) vira
        o dígito; se der 10, o dígito verificador é 0.
        """
        peso_inicial = len(base) + 1
        soma = sum(
            int(digito) * peso
            for digito, peso in zip(base, range(peso_inicial, 1, -1))
        )
        resto = (soma * 10) % 11
        return 0 if resto == 10 else resto
