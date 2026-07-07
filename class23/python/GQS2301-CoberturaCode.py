#!/usr/bin/env python3
"""
GQS2301 - Módulo de exemplo para a Aula 23 (Cobertura de Código).

Contém 3 funções pequenas e independentes, propositalmente testadas de
forma DESIGUAL pelo arquivo `test_GQS2301-CoberturaCode.py`, para que o
relatório `pytest --cov` gerado mostre linhas cobertas, parcialmente
cobertas e não cobertas (veja o Slide sobre "Saída Real de Cobertura").

Uso direto:
    python3 GQS2301-CoberturaCode.py

Uso com cobertura (a partir desta pasta):
    pytest --cov=GQS2301-CoberturaCode --cov-report=term-missing
"""


def validar_senha(senha: str) -> bool:
    """Valida uma senha segundo 4 regras (bom exemplo para MC/DC):
    - Tamanho mínimo de 8 caracteres
    - Ao menos 1 letra maiúscula
    - Ao menos 1 letra minúscula
    - Ao menos 1 dígito

    Levanta ValueError se `senha` for None ou vazia.
    """
    if senha is None or senha == "":
        raise ValueError("Senha não pode ser vazia ou None")

    tem_tamanho = len(senha) >= 8
    tem_maiuscula = any(c.isupper() for c in senha)
    tem_minuscula = any(c.islower() for c in senha)
    tem_digito = any(c.isdigit() for c in senha)

    return tem_tamanho and tem_maiuscula and tem_minuscula and tem_digito


def calcular_desconto(valor: float, tipo_cliente: str) -> float:
    """Calcula o valor final aplicando desconto conforme o tipo de cliente.

    Tipos aceitos: "regular" (0%), "silver" (5%), "gold" (10%), "platinum" (15%).
    Levanta ValueError para valor negativo ou tipo desconhecido.
    """
    if valor < 0:
        raise ValueError("Valor não pode ser negativo")

    percentuais = {
        "regular": 0.0,
        "silver": 0.05,
        "gold": 0.10,
        "platinum": 0.15,
    }

    if tipo_cliente not in percentuais:
        raise ValueError(f"Tipo de cliente desconhecido: {tipo_cliente}")

    desconto = percentuais[tipo_cliente]
    return round(valor * (1 - desconto), 2)


def classificar_nota(nota: float) -> str:
    """Classifica uma nota de 0 a 10 em conceito A/B/C/D/F.

    NOTA PROPOSITAL: os testes em `test_GQS2301-CoberturaCode.py` cobrem
    apenas os ramos A, B e C — os ramos D e F ficam sem teste de propósito,
    para servir de exemplo real de "Miss" no relatório `--cov-report=term-missing`.
    """
    if nota < 0 or nota > 10:
        raise ValueError("Nota deve estar entre 0 e 10")

    if nota >= 9:
        return "A"
    elif nota >= 7:
        return "B"
    elif nota >= 5:
        return "C"
    elif nota >= 3:
        return "D"  # pragma: sem teste direto (ver Slide de cobertura)
    else:
        return "F"  # pragma: sem teste direto (ver Slide de cobertura)


def main():
    print("=" * 60)
    print("  GQS2301 - EXEMPLO PARA COBERTURA DE CÓDIGO")
    print("=" * 60)

    print("\n--- validar_senha ---")
    for senha in ["Abc12345", "abc12345", "12345678", "Ab1"]:
        try:
            print(f"  {senha!r:<15} -> {validar_senha(senha)}")
        except ValueError as e:
            print(f"  {senha!r:<15} -> ERRO: {e}")

    print("\n--- calcular_desconto ---")
    for valor, tipo in [(100.0, "regular"), (100.0, "silver"), (100.0, "gold"), (100.0, "platinum")]:
        print(f"  R$ {valor:.2f} ({tipo}) -> R$ {calcular_desconto(valor, tipo):.2f}")

    print("\n--- classificar_nota ---")
    for nota in [10, 8, 6, 4, 1]:
        print(f"  nota={nota:<3} -> conceito {classificar_nota(nota)}")


if __name__ == "__main__":
    main()
