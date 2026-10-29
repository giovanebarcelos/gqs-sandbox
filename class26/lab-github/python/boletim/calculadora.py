"""
Regras do boletim escolar.

    R1  media = media aritmetica das notas, com 1 casa decimal (arredonda para cima no 5)
    R2  frequencia abaixo de 75%   -> REPROVADO_POR_FALTA (vale sobre a media)
        media >= 7,0               -> APROVADO
        media >= 4,0               -> EXAME
        abaixo disso               -> REPROVADO
    R3  de 1 a 4 notas, cada uma de 0 a 10; frequencia de 0 a 100
"""

from decimal import ROUND_HALF_UP, Decimal

MEDIA_APROVACAO = Decimal("7.0")
MEDIA_EXAME = Decimal("4.0")
FREQUENCIA_MINIMA = 75


def _validar(aluno, notas, frequencia):
    if not aluno or not str(aluno).strip():
        raise ValueError("Informe o nome do aluno")
    if not isinstance(notas, list) or not 1 <= len(notas) <= 4:
        raise ValueError("Informe de 1 a 4 notas")
    for nota in notas:
        if not isinstance(nota, (int, float)) or isinstance(nota, bool) or not 0 <= nota <= 10:
            raise ValueError(f"Nota fora do intervalo 0 a 10: {nota}")
    if not isinstance(frequencia, (int, float)) or not 0 <= frequencia <= 100:
        raise ValueError(f"Frequencia fora do intervalo 0 a 100: {frequencia}")


def _situacao(media, frequencia):
    if frequencia <= FREQUENCIA_MINIMA:
        return "REPROVADO_POR_FALTA"
    if media >= MEDIA_APROVACAO:
        return "APROVADO"
    if media >= MEDIA_EXAME:
        return "EXAME"
    return "REPROVADO"


def calcular_boletim(aluno, notas, frequencia):
    _validar(aluno, notas, frequencia)

    soma = sum(Decimal(str(nota)) for nota in notas)
    media = (soma / len(notas)).quantize(Decimal("0.1"), rounding=ROUND_HALF_UP)

    boletim = {
        "aluno": str(aluno).strip(),
        "media": media,
        "situacao": _situacao(media, frequencia),
        "maiorNota": float(max(notas)),
        "menorNota": float(min(notas)),
    }
    boletim["resumo"] = f"{boletim["aluno"]}: {boletim["situacao"]} com media {boletim["media"]}"
    return boletim
