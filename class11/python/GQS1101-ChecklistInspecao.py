#!/usr/bin/env python3
"""
GQS1101 - Checklist de Inspeção de Código.
Ferramenta interativa para revisão de código.
Uso: python3 GQS1101-ChecklistInspecao.py
"""

CHECKLIST = {
    "Lógica": [
        "Condições de borda tratadas?",
        "Laços têm condição de parada?",
        "Recursão tem caso base?",
    ],
    "Dados": [
        "Variáveis foram inicializadas?",
        "Parâmetros de métodos são validados?",
        "Null pointers são evitados?",
    ],
    "Tratamento de Erros": [
        "Exceções não foram silenciadas?",
        "Recursos são fechados adequadamente?",
        "Mensagens de erro são informativas?",
    ],
    "Legibilidade": [
        "Nomes são significativos?",
        "Métodos são coesos (uma responsabilidade)?",
        "Comentários são necessários e claros?",
    ],
    "Padrões": [
        "Convenções de codificação seguidas?",
        "Formatação consistente?",
        "Código morto foi removido?",
    ],
}


def executar_checklist():
    print("=" * 60)
    print("  GQS1101 - CHECKLIST DE INSPEÇÃO DE CÓDIGO")
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

    print("\n" + "=" * 60)
    print("  RESULTADO DA INSPEÇÃO")
    print("=" * 60)
    print(f"  Itens verificados: {total}")
    print(f"  Aprovados: {aprovados}/{total} ({aprovados/total*100:.0f}%)")
    if falhas:
        print(f"  Falhas ({len(falhas)}):")
        for f in falhas:
            print(f"    ❌ {f}")
    print("=" * 60)


if __name__ == "__main__":
    executar_checklist()
