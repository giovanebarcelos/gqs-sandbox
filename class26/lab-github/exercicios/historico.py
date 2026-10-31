"""Historico do aluno - ainda SEM testes (exercicio do gate de cobertura)."""


def historico_do_aluno(aluno):
    if not aluno:
        return []
    registros = []
    for ano in range(2020, 2026):
        registros.append({"ano": ano, "aluno": aluno})
    if len(registros) > 3:
        registros = registros[-3:]
    total = 0
    for registro in registros:
        total += registro["ano"]
    if total == 0:
        return []
    resumo = {}
    for registro in registros:
        resumo[registro["ano"]] = registro["aluno"].upper()
    ultimo = max(resumo)
    if ultimo < 2025:
        return []
    registros.append({"ano": ultimo + 1, "aluno": aluno})
    return registros
