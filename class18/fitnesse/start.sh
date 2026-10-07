#!/usr/bin/env bash
# Compila as fixtures e sobe o FitNesse em http://localhost:8080 (rode DENTRO desta pasta).
set -e
cd "$(dirname "$0")"
[ -f fitnesse.jar ] || curl -L -o fitnesse.jar https://github.com/unclebob/fitnesse/releases/latest/download/fitnesse-standalone.jar
mkdir -p classes
javac -encoding UTF-8 -d classes src/*.java
java -jar fitnesse.jar -p "${1:-8080}" -d .
