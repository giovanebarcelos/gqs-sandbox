#!/usr/bin/env bash
# =============================================================================
# GQS23B - versao PYTHON: pytest + cobertura + SonarScanner (com Quality Gate) + Bruno
#
#   1. pytest --cov                     testes + coverage.xml
#   2. sonar-scanner-cli (Docker)       analise, esperando o gate
#   3. bru run --env TraefikPython      testes funcionais da API Python no ar
#
# Pre-requisitos: venv com python/requirements-dev.txt, Docker, Bruno CLI,
# a API Python no ar (docker compose --profile python up -d) e SONAR_TOKEN.
#
# Uso (na pasta lab-sonar):
#   export SONAR_TOKEN=sqa_xxxxxxxx
#   ./scripts/analisar-python.sh
#
# O scanner roda num container NA REDE gqs23b_rede e fala com o SonarQube
# pelo nome do servico (http://sonarqube:9000) - funciona igual em Linux,
# Windows e macOS, sem --network=host.
# =============================================================================
set -u
cd "$(dirname "$0")/.."

: "${SONAR_TOKEN:?exporte SONAR_TOKEN com um token de analise do SonarQube}"
BRUNO_ENV="${BRUNO_ENV:-TraefikPython}"

echo "=== 1/3 pytest + cobertura ==="
(cd python && rm -f coverage.xml .coverage && pytest -v --cov --cov-report=xml --cov-report=term)
STATUS_TESTES=$?

echo "=== 2/3 SonarScanner (container na rede gqs23b_rede) ==="
# MSYS_NO_PATHCONV: no Git Bash do Windows, impede que /usr/src vire C:/...
MSYS_NO_PATHCONV=1 docker run --rm --network gqs23b_rede \
    -e SONAR_TOKEN="$SONAR_TOKEN" \
    -v "$(pwd)/python:/usr/src" \
    sonarsource/sonar-scanner-cli -Dsonar.qualitygate.wait=true
STATUS_SONAR=$?

echo "=== 3/3 Testes funcionais da API (Bruno, ambiente $BRUNO_ENV) ==="
(cd bruno/Biblioteca && bru run --env "$BRUNO_ENV")
STATUS_API=$?

echo
echo "pytest: $([ $STATUS_TESTES -eq 0 ] && echo OK || echo FALHOU)   quality gate: $([ $STATUS_SONAR -eq 0 ] && echo PASSOU || echo REPROVOU)   bruno: $([ $STATUS_API -eq 0 ] && echo OK || echo FALHOU)"
[ $STATUS_TESTES -eq 0 ] && [ $STATUS_SONAR -eq 0 ] && [ $STATUS_API -eq 0 ]
