#!/usr/bin/env bash
# Compila as fixtures (Maven) e sobe o FitNesse em http://localhost:8080  (rode dentro desta pasta)
set -e
cd "$(dirname "$0")"
mvn -q -B package -DskipTests
[ -f fitnesse.jar ] || cp ../fitnesse/fitnesse.jar . 2>/dev/null || \
  curl -L -o fitnesse.jar https://github.com/unclebob/fitnesse/releases/latest/download/fitnesse-standalone.jar
java -jar fitnesse.jar -p "${1:-8080}" -d .
