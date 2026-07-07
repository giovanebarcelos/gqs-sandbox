#!/usr/bin/env python3
"""
GQS2601-GitHubActions_Workflow.py
==================================
Gerador e validador de workflow do GitHub Actions (conceitual).
Aula 26 — DevOps e GitHub Actions | Garantia da Qualidade de Software.

Uso:
    python3 GQS2601-GitHubActions_Workflow.py              # modo interativo
    python3 GQS2601-GitHubActions_Workflow.py --validate    # valida exemplos pré-definidos
"""

import sys
import re
from typing import Dict, List

# ──────────────────────────────────────────────
# 1. Validação de workflow YAML (simulada)
# ──────────────────────────────────────────────

REQUIRED_KEYS: Dict[str, List[str]] = {
    "name": ["É recomendado fornecer um nome para o workflow."],
    "on": ["O gatilho 'on' é obrigatório (push, pull_request, schedule, etc.)."],
    "jobs": ["Pelo menos um job deve ser definido."],
}

def validate_workflow(yaml_content: str) -> List[str]:
    """
    Valida conceitualmente um workflow YAML do GitHub Actions.

    Args:
        yaml_content: String contendo o YAML do workflow.

    Returns:
        Lista de problemas encontrados (vazia se tudo ok).
    """
    issues: List[str] = []

    # Verificações básicas com regex (não é um parser YAML completo)
    if not re.search(r'^name\s*:', yaml_content, re.MULTILINE):
        issues.append("[AVISO] 'name' não encontrado. " + REQUIRED_KEYS["name"][0])

    if not re.search(r'^on\s*:', yaml_content, re.MULTILINE):
        issues.append("[ERRO] 'on' não encontrado. " + REQUIRED_KEYS["on"][0])
    else:
        # Verificar se há pelo menos um evento
        if not re.search(r'push|pull_request|schedule|workflow_dispatch', yaml_content):
            issues.append("[AVISO] Nenhum evento conhecido encontrado em 'on'.")

    if not re.search(r'^jobs\s*:', yaml_content, re.MULTILINE):
        issues.append("[ERRO] 'jobs' não encontrado. " + REQUIRED_KEYS["jobs"][0])
    else:
        # Verificar se os jobs têm 'runs-on'
        job_matches = re.findall(r'^\s{2}(\w[\w-]*)\s*:', yaml_content, re.MULTILINE)
        if not job_matches:
            issues.append("[AVISO] Nenhum job nomeado encontrado em 'jobs'.")

        runs_on_count = len(re.findall(r'runs-on\s*:', yaml_content))
        if runs_on_count == 0:
            issues.append("[ERRO] Nenhum job possui 'runs-on'.")

        steps_count = len(re.findall(r'^\s{4}- name\s*:', yaml_content, re.MULTILINE)) + \
                      len(re.findall(r'^\s{4}- uses\s*:', yaml_content, re.MULTILINE))
        if steps_count == 0:
            issues.append("[AVISO] Nenhum step encontrado nos jobs (use 'steps:' com 'name' ou 'uses').")

    return issues


# ──────────────────────────────────────────────
# 2. Gerador de workflow template
# ──────────────────────────────────────────────

def generate_workflow_template(
    workflow_name: str = "CI - Build and Test",
    java_version: str = "17",
    python_version: str = "3.11",
    os_runner: str = "ubuntu-latest",
    use_matrix: bool = False,
) -> str:
    """
    Gera um template de workflow YAML do GitHub Actions.

    Args:
        workflow_name: Nome do workflow.
        java_version: Versão do Java (ex.: "17", "21").
        python_version: Versão do Python (ex.: "3.11").
        os_runner: Runner do GitHub Actions (ex.: "ubuntu-latest").
        use_matrix: Se True, gera com strategy.matrix para múltiplas versões.

    Returns:
        String com o YAML do workflow.
    """
    lines = [
        f"name: {workflow_name}",
        "",
        "on:",
        "  push:",
        "    branches: [ main ]",
        "  pull_request:",
        "    branches: [ main ]",
        "",
        "jobs:",
        "  test:",
        f"    runs-on: {os_runner}",
    ]

    if use_matrix:
        lines.extend([
            "",
            "    strategy:",
            "      matrix:",
            f"        java: [17, 21]",
            f"        os: [ubuntu-latest, windows-latest]",
            "",
            "    steps:",
            "      - name: Checkout code",
            "        uses: actions/checkout@v4",
            "",
            "      - name: Set up JDK",
            "        uses: actions/setup-java@v4",
            "        with:",
            "          java-version: ${{ matrix.java }}",
            "          distribution: 'temurin'",
            "",
            "      - name: Compile and test with Maven",
            "        run: mvn clean test",
        ])
    else:
        lines.extend([
            "",
            "    steps:",
            "      - name: Checkout code",
            "        uses: actions/checkout@v4",
            "",
            "      - name: Set up JDK",
            "        uses: actions/setup-java@v4",
            "        with:",
            f"          java-version: '{java_version}'",
            "          distribution: 'temurin'",
            "",
            "      - name: Cache Maven dependencies",
            "        uses: actions/cache@v4",
            "        with:",
            "          path: ~/.m2/repository",
            "          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}",
            "          restore-keys: |",
            "            ${{ runner.os }}-maven-",
            "",
            "      - name: Run tests with coverage",
            "        run: mvn clean test jacoco:report",
            "",
            "      - name: Upload coverage report",
            "        uses: actions/upload-artifact@v4",
            "        with:",
            "          name: coverage-report",
            "          path: target/site/jacoco/",
        ])

    return "\n".join(lines) + "\n"


# ──────────────────────────────────────────────
# 3. Exemplos pré-definidos para validação
# ──────────────────────────────────────────────

WORKFLOW_EXAMPLE_VALID = """name: CI - Java 17
on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4
      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run tests
        run: mvn test
"""

WORKFLOW_EXAMPLE_INVALID = """name:
on:
  push

  test:
    steps:
      - run: echo hello
"""


# ──────────────────────────────────────────────
# 4. Modo interativo
# ──────────────────────────────────────────────

def interactive_mode() -> None:
    """Executa o script em modo interativo."""
    print("=" * 60)
    print("  GQS2601 - Gerador de Workflow GitHub Actions")
    print("  Garantia da Qualidade de Software — Aula 26")
    print("=" * 60)

    while True:
        print("\nOpções:")
        print("  1 - Validar workflow YAML existente")
        print("  2 - Gerar template de workflow")
        print("  3 - Validar exemplos pré-definidos")
        print("  0 - Sair")

        choice = input("\nEscolha: ").strip()

        if choice == "1":
            print("\nCole o YAML do workflow (digite 'EOF' em linha vazia para finalizar):")
            lines = []
            while True:
                line = input()
                if line.strip().upper() == "EOF":
                    break
                lines.append(line)
            yaml_text = "\n".join(lines)
            issues = validate_workflow(yaml_text)
            if issues:
                print("\n❌ Problemas encontrados:")
                for issue in issues:
                    print(f"   • {issue}")
            else:
                print("\n✅ Workflow válido! Nenhum problema encontrado.")

        elif choice == "2":
            print("\n--- Gerar Template de Workflow ---")
            name = input("Nome do workflow [CI - Build and Test]: ").strip() or "CI - Build and Test"
            java_ver = input("Versão Java [17]: ").strip() or "17"
            py_ver = input("Versão Python [3.11]: ").strip() or "3.11"
            os_runner = input("Runner OS [ubuntu-latest]: ").strip() or "ubuntu-latest"
            matrix_str = input("Usar matrix (s/N)? ").strip().lower()
            use_matrix = matrix_str in ("s", "sim", "y", "yes")

            print("\n--- Workflow Gerado ---\n")
            print(generate_workflow_template(name, java_ver, py_ver, os_runner, use_matrix))

        elif choice == "3":
            print("\n--- Validando workflow VÁLIDO ---")
            issues = validate_workflow(WORKFLOW_EXAMPLE_VALID)
            if issues:
                for i in issues:
                    print(f"   • {i}")
            else:
                print("   ✅ Válido!")

            print("\n--- Validando workflow INVÁLIDO ---")
            issues = validate_workflow(WORKFLOW_EXAMPLE_INVALID)
            if issues:
                for i in issues:
                    print(f"   • {i}")
            else:
                print("   ✅ Válido!")

        elif choice == "0":
            print("\nEncerrando. Até a próxima aula!")
            break

        else:
            print("\nOpção inválida. Tente novamente.")


# ──────────────────────────────────────────────
# 5. Ponto de entrada
# ──────────────────────────────────────────────

if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "--validate":
        print("=" * 60)
        print("  GQS2601 - Validação de Workflows")
        print("=" * 60)

        print("\n1. Workflow válido:")
        issues_valid = validate_workflow(WORKFLOW_EXAMPLE_VALID)
        if issues_valid:
            for i in issues_valid:
                print(f"   ❌ {i}")
        else:
            print("   ✅ Nenhum problema.")

        print("\n2. Workflow inválido:")
        issues_invalid = validate_workflow(WORKFLOW_EXAMPLE_INVALID)
        if issues_invalid:
            for i in issues_invalid:
                print(f"   ❌ {i}")
        else:
            print("   ✅ Nenhum problema.")

        print("\n3. Template gerado (com matrix):")
        print(generate_workflow_template(use_matrix=True))

        sys.exit(0)

    interactive_mode()
