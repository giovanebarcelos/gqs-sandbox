#!/usr/bin/env python3
"""
GQS2701 - Checklist de Qualidade para Projeto Final.
Verifica se todos os itens do projeto A3 estão completos.
Uso: python3 GQS2701-ChecklistQualidade.py
"""

CHECKLIST = {
    "Plano de Teste": [
        "Escopo positivo e negativo definidos",
        "Critérios de entrada e saída claros",
        "Riscos identificados e mitigados",
        "Estratégia de teste definida",
        "Recursos e cronograma estimados",
    ],
    "Casos de Teste": [
        "Mínimo de 15 casos de teste",
        "Pré-condição para cada caso",
        "Dados de entrada especificados",
        "Passos numerados e claros",
        "Resultado esperado definido",
        "Matriz de rastreabilidade (req × CT)",
    ],
    "Testes Automatizados": [
        "Mínimo de 10 testes automatizados",
        "Cobertura ≥ 70%",
        "Padrão AAA (Arrange-Act-Assert)",
        "Testes independentes e determinísticos",
        "Testes executando e passando",
    ],
    "Postman": [
        "Collection com CRUD completo",
        "Scripts de teste (pm.test)",
        "Variáveis de ambiente configuradas",
        "Execução via Newman funcionando",
    ],
    "Pipeline CI/CD": [
        "Workflow GitHub Actions configurado",
        "Build + Test + Coverage no pipeline",
        "Quality Gate definido",
        "Pipeline executando com sucesso",
    ],
}


def main():
    print("=" * 60)
    print("  GQS2701 - CHECKLIST DE QUALIDADE - PROJETO A3")
    print("=" * 60)
    print("  Marque os itens concluídos (s/n):\n")

    total = 0
    concluidos = 0
    pendentes = []

    for categoria, itens in CHECKLIST.items():
        print(f"--- {categoria} ---")
        for item in itens:
            total += 1
            while True:
                resp = input(f"  {item}? (s/n): ").strip().lower()
                if resp in ("s", "n"):
                    if resp == "s":
                        concluidos += 1
                    else:
                        pendentes.append(f"{categoria}: {item}")
                    break
                print("    Responda 's' ou 'n'.")

    print("\n" + "=" * 60)
    print("  RESUMO")
    print("=" * 60)
    print(f"  Itens totais: {total}")
    print(f"  Concluídos: {concluidos}/{total} ({concluidos/total*100:.0f}%)")
    if pendentes:
        print(f"  Pendentes ({len(pendentes)}):")
        for p in pendentes:
            print(f"    ❌ {p}")
    if concluidos == total:
        print("  ✅ Projeto pronto para entrega!")
    print("=" * 60)


if __name__ == "__main__":
    main()
