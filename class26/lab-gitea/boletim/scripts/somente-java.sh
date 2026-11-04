#!/usr/bin/env bash
# =============================================================================
# Transforma a copia do projeto numa versao SO' JAVA (Aula 26 - Gitea Actions).
# Rode na RAIZ do projeto (~/boletim), ANTES do primeiro commit:
#
#     bash scripts/somente-java.sh
#
# Remove python/, o CI Python e as opcoes de Python da promocao.
# =============================================================================
set -euo pipefail
[ -d java ] && [ -d .gitea/workflows ] || { echo "Rode na raiz do projeto (onde estao java/ e .gitea/)." >&2; exit 1; }

rm -rf python .gitea/workflows/ci-python.yml \
       bruno/Boletim-API/environments/StagingPython.bru bruno/Boletim-API/environments/ProducaoPython.bru \
       bruno/Boletim-API/environments/Python.bru
sed -i 's/options: \[java, python\]/options: [java]/; s/Aplicacao: java ou python/Aplicacao: java/' .gitea/workflows/promover-producao.yml
sed -i 's/(java|python)/(java)/; s/Use java ou python/Use java/' .gitea/workflows/promover-producao.yml
rm -- "$0"
echo "Pronto: projeto so' Java. Siga o tutorial GQS26-Tutorial-GiteaActions-Java.md"
