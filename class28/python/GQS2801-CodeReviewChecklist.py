#!/usr/bin/env python3
"""
GQS2801 - Checklist de Code Review.
Ferramenta interativa para revisão de código antes do merge,
cobrindo ética, boas práticas e qualidade do Pull Request.
Uso: python3 GQS2801-CodeReviewChecklist.py
"""

CHECKLIST = {
    "Código Limpo": [
        "Nomes de variáveis e funções são significativos?",
        "Funções são pequenas e coesas (uma responsabilidade)?",
        "Não há código duplicado?",
    ],
    "Testes": [
        "Existem testes automatizados para a mudança?",
        "Os testes cobrem casos de borda?",
        "A suíte de testes passou no CI?",
    ],
    "Segurança e Dados": [
        "Dados sensíveis (senhas, tokens) não estão hardcoded?",
        "Entradas do usuário são validadas?",
        "A mudança respeita a LGPD (dados pessoais anonimizados em testes)?",
    ],
    "Ética e Boas Práticas": [
        "O código não introduz viés ou discriminação?",
        "Comentários e mensagens de commit são profissionais?",
        "A mudança foi comunicada de forma transparente ao time?",
    ],
    "Pull Request": [
        "Descrição do PR explica o 'porquê' da mudança?",
        "Escopo do PR é pequeno e revisável?",
        "Pipeline de CI/CD está verde?",
    ],
}


def executar_checklist() -> None:
    print("=" * 60)
    print("  GQS2801 - CHECKLIST DE CODE REVIEW")
    print("=" * 60)

    total = 0
    aprovados = 0
    falhas = []

    for categoria, itens in CHECKLIST.items():
        print(f"\n--- {categoria} ---")
        for item in itens:
            total += 1
            while True:
                resp = input(f"  {item} (s/n): ").strip().lower()
                if resp in ("s", "n"):
                    if resp == "s":
                        aprovados += 1
                    else:
                        falhas.append(f"{categoria}: {item}")
                    break
                print("    Responda 's' ou 'n'.")

    percentual = aprovados / total * 100 if total else 0

    print("\n" + "=" * 60)
    print("  RESULTADO DO CODE REVIEW")
    print("=" * 60)
    print(f"  Itens verificados: {total}")
    print(f"  Aprovados: {aprovados}/{total} ({percentual:.0f}%)")

    if percentual >= 90:
        veredito = "APROVADO — pronto para merge"
    elif percentual >= 70:
        veredito = "APROVADO COM RESSALVAS — ajustar pontos pendentes"
    else:
        veredito = "REPROVADO — revisar antes de novo review"
    print(f"  Veredito: {veredito}")

    if falhas:
        print(f"\n  Pontos de atenção ({len(falhas)}):")
        for f in falhas:
            print(f"    - {f}")
    print("=" * 60)


if __name__ == "__main__":
    executar_checklist()
