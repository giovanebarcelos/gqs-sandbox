#!/usr/bin/env bash
# =============================================================================
# GQS23B - versao JAVA: testes + cobertura + SonarQube (com Quality Gate) + Bruno
#
#   1. mvn clean verify sonar:sonar   JUnit 5 + JaCoCo + analise, esperando o gate
#   2. bru run --env Traefik          testes funcionais da API no ar
#
# Pre-requisitos: JDK 17+, Maven 3.9+, Bruno CLI (npm install -g @usebruno/cli)
# e um token de analise do SonarQube em SONAR_TOKEN.
#
# Uso (na pasta lab-sonar):
#   export SONAR_TOKEN=sqa_xxxxxxxx
#   ./scripts/analisar-java.sh
#
# Variaveis opcionais:
#   SONAR_HOST_URL  default http://sonar.localhost (com a porta, se trocou HTTP_PORT)
#   BRUNO_ENV       default Traefik -> http://biblioteca.localhost
# =============================================================================
set -u
cd "$(dirname "$0")/.."

: "${SONAR_TOKEN:?exporte SONAR_TOKEN com um token de analise do SonarQube}"
export SONAR_TOKEN
SONAR_HOST_URL="${SONAR_HOST_URL:-http://sonar.localhost}"
BRUNO_ENV="${BRUNO_ENV:-Traefik}"

echo "=== 1/2 JUnit + JaCoCo + SonarQube ($SONAR_HOST_URL) ==="
# -Dmaven.test.failure.ignore: teste vermelho NAO impede a analise (o Sonar
#   mostra o resultado dos testes e a cobertura mesmo assim).
# -Dsonar.qualitygate.wait: o Maven espera o servidor avaliar o gate e FALHA
#   se ele reprovar - e' isso que transforma o relatorio em trava de pipeline.
mvn -f java/pom.xml clean verify sonar:sonar \
    -Dmaven.test.failure.ignore=true \
    -Dsonar.host.url="$SONAR_HOST_URL" \
    -Dsonar.qualitygate.wait=true
STATUS_SONAR=$?
# Soma failures + errors dos relatorios do Surefire (so' a tag <testsuite>)
FALHAS=$(cat java/target/surefire-reports/TEST-*.xml 2>/dev/null | grep -o '<testsuite [^>]*' \
         | grep -oE '(failures|errors)="[0-9]+"' | grep -oE '[0-9]+' | awk '{s+=$1} END {print s+0}')

echo "=== 2/2 Testes funcionais da API (Bruno, ambiente $BRUNO_ENV) ==="
(cd bruno/Biblioteca && bru run --env "$BRUNO_ENV")
STATUS_API=$?

echo
echo "junit: $([ "$FALHAS" = "0" ] && echo OK || echo "FALHOU ($FALHAS)")   quality gate: $([ $STATUS_SONAR -eq 0 ] && echo PASSOU || echo REPROVOU)   bruno: $([ $STATUS_API -eq 0 ] && echo OK || echo FALHOU)"
echo "dashboard: $SONAR_HOST_URL/dashboard?id=gqs23b-biblioteca-java"
[ "$FALHAS" = "0" ] && [ $STATUS_SONAR -eq 0 ] && [ $STATUS_API -eq 0 ]
