#!/usr/bin/env bash
# =============================================================================
# Transforma a copia do laboratorio numa versao SO' JAVA (Aula 26 - GitHub Actions).
# Rode na RAIZ do repositorio do aluno, ANTES do primeiro commit:
#
#     bash somente-java.sh
#
# Remove: python/, o CI Python, o servico Python do compose, o Python do
# release, do CodeQL e do Dependabot. Remove tambem este proprio script.
# =============================================================================
set -euo pipefail
[ -d java ] && [ -f docker-compose.yml ] || { echo "Rode na raiz do laboratorio (onde estao java/ e docker-compose.yml)." >&2; exit 1; }

rm -rf python .github/workflows/ci-python.yml exercicios/historico.py \
       bruno/Boletim-API/environments/Python.bru bruno/Boletim-API/environments/TraefikPython.bru

# docker-compose.yml: tira o servico boletim-python (bloco de comentario + servico)
ini=$(grep -n 'Boletim API - PYTHON' docker-compose.yml | cut -d: -f1)
fim=$(grep -n 'Relatorios de cobertura' docker-compose.yml | cut -d: -f1)
sed -i "$((ini-1)),$((fim-2))d" docker-compose.yml
sed -i '\#./python/relatorio-cobertura:#d' docker-compose.yml   # sem Python, a pasta nao existe: o Docker a criaria como root
sed -i '/boletim-python.localhost   Boletim API em Python/d; s#IMAGEM_JAVA / IMAGEM_PYTHON / APP_VERSION#IMAGEM_JAVA / APP_VERSION#' docker-compose.yml

# release.yml
sed -i 's/projeto: \[java, python\]/projeto: [java]/;
        /IMAGEM_PYTHON=/d;
        s/boletim-java boletim-python$/boletim-java/;
        s/gqs26-boletim-java:8080 gqs26-boletim-python:5000/gqs26-boletim-java:8080/;
        s/publica boletim-java e boletim-python/publica boletim-java/' .github/workflows/release.yml

# CodeQL e Dependabot
sed -i 's/\[java-kotlin, python\]/[java-kotlin]/' .github/workflows/codeql.yml
sed -i '/package-ecosystem: pip/,/interval: weekly/d; s#\[/java, /python, /runner\]#[/java, /runner]#' .github/dependabot.yml

rm -- "$0"
echo "Pronto: laboratorio so' Java. Siga o tutorial GQS26-Tutorial-GitHubActions-Java.md"
