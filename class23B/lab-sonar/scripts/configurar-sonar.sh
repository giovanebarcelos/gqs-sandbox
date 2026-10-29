#!/usr/bin/env bash
# =============================================================================
# GQS23B - Configura o SonarQube para o laboratorio (Etapa 5 do tutorial)
# =============================================================================
# Faz, pela Web API, o mesmo que a Etapa 5 ensina pela interface:
#
#   1. troca o Quality PROFILE (quais regras rodam) dos dois projetos para
#      "Sonar way comprehensive" - o padrao do SonarQube 26.x, "Sonar way core",
#      nao tem as regras de code smell que o laboratorio quer mostrar;
#   2. cria o Quality GATE "GQS - Codigo Legado" (quando o projeto passa),
#      com condicoes sobre o Overall Code - o gate padrao "Sonar way" so'
#      avalia codigo novo e por isso aprova um projeto recem-importado;
#   3. associa o gate aos dois projetos.
#
# USO
#   export SONAR_ADMIN_TOKEN=squ_xxxxxxxx     # token de USUARIO do admin
#   ./scripts/configurar-sonar.sh [http://sonar.localhost]
#
# O token de ANALISE (sqa_...) nao serve aqui: ele so' envia analises.
# Rodar duas vezes nao tem problema - o que ja existe e' mantido.
# =============================================================================
set -uo pipefail

HOST="${1:-http://sonar.localhost}"
GATE="GQS - Codigo Legado"
PERFIL="Sonar way comprehensive"

: "${SONAR_ADMIN_TOKEN:?Defina SONAR_ADMIN_TOKEN (token de usuario do admin, squ_...)}"

api() { curl -sS -u "${SONAR_ADMIN_TOKEN}:" -X POST "${HOST}/$1" "${@:2}"; }

# projeto (chave) -> linguagem do perfil
for par in "gqs23b-biblioteca-java:java" "gqs23b-biblioteca-python:py"; do
  projeto="${par%%:*}"; linguagem="${par##*:}"
  echo ">> Projeto ${projeto}"
  api "api/projects/create" -d "project=${projeto}" -d "name=${projeto}" >/dev/null 2>&1 || true
  api "api/qualityprofiles/add_project" -d "language=${linguagem}" \
      -d "project=${projeto}" --data-urlencode "qualityProfile=${PERFIL}" >/dev/null &&
    echo "   perfil (${linguagem}): ${PERFIL}"
done

echo ">> Criando o Quality Gate '${GATE}'"
api "api/qualitygates/create" --data-urlencode "name=${GATE}" >/dev/null

# metrica                  | op | erro | leitura
# coverage                 | LT | 80   | cobertura total abaixo de 80% reprova
# duplicated_lines_density | GT | 3    | mais de 3% de linhas duplicadas reprova
# reliability_rating       | GT | 1    | confiabilidade pior que A reprova (1=A ... 5=E)
# security_rating          | GT | 1    | seguranca pior que A reprova
# sqale_rating             | GT | 1    | manutenibilidade pior que A reprova
adicionar() {
  if api "api/qualitygates/create_condition" --data-urlencode "gateName=${GATE}" \
         -d "metric=$1" -d "op=$2" -d "error=$3" | grep -q '"id"'; then
    echo "   condicao: $1 $2 $3"
  else
    echo "   condicao: $1 $2 $3 (ja existia)"
  fi
}
adicionar coverage                 LT 80
adicionar duplicated_lines_density GT 3
adicionar reliability_rating       GT 1
adicionar security_rating          GT 1
adicionar sqale_rating             GT 1

for projeto in gqs23b-biblioteca-java gqs23b-biblioteca-python; do
  api "api/qualitygates/select" --data-urlencode "gateName=${GATE}" \
      -d "projectKey=${projeto}" >/dev/null && echo ">> Gate associado a ${projeto}"
done

echo
echo "OK. Rode a analise de novo para perfil e gate valerem (Etapa 5.3 do tutorial)."
