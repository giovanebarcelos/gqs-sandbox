#!/usr/bin/env bash
# =============================================================================
# GQS23B - Cria o Quality Gate "GQS - Codigo Legado" via Web API do SonarQube
# =============================================================================
# Aula 23B (Garantia da Qualidade de Software) - Etapa 4 do laboratorio.
#
# POR QUE ESTE SCRIPT EXISTE
#   O Quality Gate padrao "Sonar way" avalia SOMENTE o New Code (metodologia
#   Clean as You Code). Num projeto que acabou de ser importado nao ha "codigo
#   novo", entao o gate passa mesmo com dezenas de problemas no codigo antigo.
#   Este gate acrescenta condicoes sobre o Overall Code - e e por isso que ele
#   reprova o projeto GQS23B01-Biblioteca na primeira analise.
#
# USO
#   export SONAR_TOKEN=squ_xxxxxxxx
#   ./GQS23B-criar-quality-gate.sh [http://localhost:9000] [gqs-biblioteca-java]
#
# REQUISITOS: curl; token de um usuario com permissao "Administer Quality Gates"
# =============================================================================
set -euo pipefail

HOST="${1:-http://localhost:9000}"
PROJETO="${2:-gqs-biblioteca-java}"
GATE="GQS - Codigo Legado"

: "${SONAR_TOKEN:?Defina SONAR_TOKEN antes de rodar (export SONAR_TOKEN=squ_...)}"

api() { curl -sS -u "${SONAR_TOKEN}:" -X POST "${HOST}/$1" "${@:2}"; }

echo ">> Criando o Quality Gate '${GATE}'..."
api "api/qualitygates/create" -d "name=${GATE}" >/dev/null || \
  echo "   (ja existe - seguindo em frente)"

# --- Condicoes sobre o Overall Code -----------------------------------------
# metric                   | op | error | leitura
# coverage                 | LT | 80    | cobertura total abaixo de 80% reprova
# duplicated_lines_density | GT | 3     | mais de 3% de linhas duplicadas reprova
# reliability_rating       | GT | 1     | nota de confiabilidade pior que A reprova
# security_rating          | GT | 1     | nota de seguranca pior que A reprova
# sqale_rating             | GT | 1     | nota de manutenibilidade pior que A reprova
adicionar() {
  echo ">> Condicao: $1 $2 $3"
  api "api/qualitygates/create_condition" \
      --data-urlencode "gateName=${GATE}" \
      -d "metric=$1" -d "op=$2" -d "error=$3" >/dev/null || \
    echo "   (condicao ja existe)"
}

adicionar coverage                 LT 80
adicionar duplicated_lines_density GT 3
adicionar reliability_rating       GT 1
adicionar security_rating          GT 1
adicionar sqale_rating             GT 1

echo ">> Associando o gate ao projeto '${PROJETO}'..."
api "api/qualitygates/select" \
    --data-urlencode "gateName=${GATE}" -d "projectKey=${PROJETO}" >/dev/null

echo
echo "OK. Rode a analise de novo para o gate ser reavaliado:"
echo "    mvn sonar:sonar -Dsonar.token=\$SONAR_TOKEN"
echo "E consulte o resultado em:"
echo "    curl -s -u \$SONAR_TOKEN: \"${HOST}/api/qualitygates/project_status?projectKey=${PROJETO}\""
