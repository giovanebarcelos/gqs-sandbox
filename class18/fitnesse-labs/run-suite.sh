#!/usr/bin/env bash
# Roda a suíte Labs em modo headless (CI). Exit code = nº de testes com falha.
set -e
cd "$(dirname "$0")"
mvn -q -B package -DskipTests
[ -f fitnesse.jar ] || cp ../fitnesse/fitnesse.jar . 2>/dev/null || \
  curl -L -o fitnesse.jar https://github.com/unclebob/fitnesse/releases/latest/download/fitnesse-standalone.jar
java -jar fitnesse.jar -d . -o -c "${1:-Labs}?suite&format=${2:-text}"
