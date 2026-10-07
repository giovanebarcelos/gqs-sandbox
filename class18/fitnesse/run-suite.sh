#!/usr/bin/env bash
# Executa a suíte pela linha de comando (sem navegador) - ideal para CI/CD.
# Exit code != 0 quando algum teste falha.
set -e
cd "$(dirname "$0")"
mkdir -p classes
javac -encoding UTF-8 -d classes src/*.java
java -jar fitnesse.jar -d . -o -c "${1:-SuiteAula18}?suite&format=text"
