#!/usr/bin/env bash
# Roda UM plano JMeter pela CLI, apagando resultados antigos antes (a pasta do -o precisa estar
# vazia e o .jtl existente seria ACRESCENTADO, misturando execuções).
# Uso: ./GQS20B-run.sh <plano.jmx> [porta] [host]
#   ./GQS20B-run.sh prova/Q15-Carga.jmx
#   ./GQS20B-run.sh simulado/Q16-Estresse.jmx 3000
set -euo pipefail

PLANO="${1:?uso: $0 <plano.jmx> [porta] [host]}"
PORTA="${2:-5000}"
HOST="${3:-localhost}"

command -v jmeter >/dev/null || { echo "jmeter não encontrado no PATH"; exit 1; }
curl -s -o /dev/null --max-time 5 "http://${HOST}:${PORTA}/" || {
  echo "Nada respondendo em http://${HOST}:${PORTA}. Suba a API antes (uma só)."; exit 1; }

NOME="$(basename "${PLANO%.jmx}")"
PASTA="resultados/$(basename "$(dirname "$PLANO")")-${NOME}"
rm -rf "$PASTA"
mkdir -p "$PASTA"

jmeter -n -t "$PLANO" -l "$PASTA/${NOME}.jtl" -e -o "$PASTA/relatorio" \
  -Jhost="$HOST" -Jporta="$PORTA" \
  -Jjmeter.reportgenerator.apdex_satisfied_threshold=300 \
  -Jjmeter.reportgenerator.apdex_tolerated_threshold=1200

echo
echo "Relatório: $PASTA/relatorio/index.html"
