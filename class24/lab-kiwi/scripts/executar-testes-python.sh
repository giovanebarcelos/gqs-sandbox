#!/usr/bin/env bash
# =============================================================================
# GQS24 - versao PYTHON: executa as duas suites automatizadas e publica no Kiwi.
#
#   1. pytest (testes unitarios)  -> resultados-unit.xml -> rodada [pytest]
#   2. Bruno  (API Python)        -> resultados-api.xml  -> rodada [bruno]
#
# Pre-requisitos: venv com python/app/requirements-dev.txt e python/kiwi/requirements.txt,
# a API Python no ar (docker compose --profile python up -d),
# Bruno CLI (npm install -g @usebruno/cli) e credenciais do Kiwi em
# ~/.tcms.conf ou KIWI_URL/KIWI_USERNAME/KIWI_PASSWORD.
#
# Uso (na pasta lab-kiwi):
#   TCMS_PLAN_ID=1 TCMS_BUILD=1.0.0 ./scripts/executar-testes-python.sh
#
# Variaveis opcionais:
#   BRUNO_ENV   ambiente do Bruno (default: TraefikPython -> http://app-python.localhost)
#   PUBLICAR=0  so roda os testes, sem enviar ao Kiwi
# =============================================================================
set -u
cd "$(dirname "$0")/.."
RAIZ="$(pwd)"

: "${TCMS_PLAN_ID:?exporte TCMS_PLAN_ID com o id do plano no Kiwi}"
export TCMS_PLAN_ID
export TCMS_BUILD="${TCMS_BUILD:-1.0.0}"
BRUNO_ENV="${BRUNO_ENV:-TraefikPython}"
PUBLICAR="${PUBLICAR:-1}"

echo "=== 1/2 Testes unitarios (pytest) ==="
(cd python/app && pytest -v --junitxml="$RAIZ/resultados-unit.xml")
STATUS_UNIT=$?

echo "=== 2/2 Testes de API (Bruno, ambiente $BRUNO_ENV) ==="
(cd bruno/TechStore-Carrinho && bru run --env "$BRUNO_ENV" --reporter-junit "$RAIZ/resultados-api.xml")
STATUS_API=$?

if [ "$PUBLICAR" = "1" ]; then
  echo "=== Publicando no Kiwi TCMS (plano TP-$TCMS_PLAN_ID, build $TCMS_BUILD) ==="
  TCMS_PREFIX="[pytest]" python3 python/kiwi/publicar_resultados.py resultados-unit.xml || exit 1
  TCMS_PREFIX="[bruno]"  python3 python/kiwi/publicar_resultados.py resultados-api.xml  || exit 1
fi

echo
echo "pytest: $([ $STATUS_UNIT -eq 0 ] && echo OK || echo FALHOU)   bruno: $([ $STATUS_API -eq 0 ] && echo OK || echo FALHOU)"
# O script falha (exit 1) se alguma suite falhou - util num pipeline de CI.
[ $STATUS_UNIT -eq 0 ] && [ $STATUS_API -eq 0 ]
