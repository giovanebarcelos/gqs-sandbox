#!/usr/bin/env bash
# =============================================================================
# GQS24 - versao JAVA: executa as duas suites automatizadas e publica no Kiwi.
#
#   1. JUnit 5 (mvn test)        -> java/app/target/surefire-reports -> rodada [junit]
#   2. Bruno (API Java)          -> resultados-api.xml                -> rodada [bruno]
#
# Pre-requisitos: JDK 17+, Maven 3.9+, Bruno CLI (npm install -g @usebruno/cli)
# e credenciais do Kiwi em ~/.tcms.conf ou KIWI_URL/KIWI_USERNAME/KIWI_PASSWORD.
#
# Uso (na pasta lab-kiwi):
#   TCMS_PLAN_ID=1 TCMS_BUILD=1.0.0 ./scripts/executar-testes.sh
#
# Variaveis opcionais:
#   BRUNO_ENV   ambiente do Bruno (default: Traefik -> http://app.localhost)
#   PUBLICAR=0  so roda os testes, sem enviar ao Kiwi
# =============================================================================
set -u
cd "$(dirname "$0")/.."
RAIZ="$(pwd)"

: "${TCMS_PLAN_ID:?exporte TCMS_PLAN_ID com o id do plano no Kiwi}"
export TCMS_PLAN_ID
export TCMS_BUILD="${TCMS_BUILD:-1.0.0}"
BRUNO_ENV="${BRUNO_ENV:-Traefik}"
PUBLICAR="${PUBLICAR:-1}"
KIWI_TOOLS="$RAIZ/java/kiwi/target/kiwi-tools.jar"

echo "=== 1/2 Testes unitarios (JUnit 5 via Maven) ==="
# Sem -q para o aluno ver cada teste; o Surefire grava os XML mesmo se falhar.
rm -rf java/app/target/surefire-reports
mvn -f java/app/pom.xml test
STATUS_UNIT=$?

echo "=== 2/2 Testes de API (Bruno, ambiente $BRUNO_ENV) ==="
(cd bruno/TechStore-Carrinho && bru run --env "$BRUNO_ENV" --reporter-junit "$RAIZ/resultados-api.xml")
STATUS_API=$?

if [ "$PUBLICAR" = "1" ]; then
  if [ ! -f "$KIWI_TOOLS" ]; then
    echo "=== Compilando java/kiwi (primeira vez) ==="
    mvn -q -f java/kiwi/pom.xml package || exit 1
  fi
  echo "=== Publicando no Kiwi TCMS (plano TP-$TCMS_PLAN_ID, build $TCMS_BUILD) ==="
  TCMS_PREFIX="[junit]" java -jar "$KIWI_TOOLS" publicar java/app/target/surefire-reports || exit 1
  TCMS_PREFIX="[bruno]" java -jar "$KIWI_TOOLS" publicar resultados-api.xml || exit 1
fi

echo
echo "junit: $([ $STATUS_UNIT -eq 0 ] && echo OK || echo FALHOU)   bruno: $([ $STATUS_API -eq 0 ] && echo OK || echo FALHOU)"
# O script falha (exit 1) se alguma suite falhou - util num pipeline de CI.
[ $STATUS_UNIT -eq 0 ] && [ $STATUS_API -eq 0 ]
