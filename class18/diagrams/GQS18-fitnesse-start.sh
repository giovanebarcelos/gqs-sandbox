#!/usr/bin/env bash
# Sobe o FitNesse com o workspace completo da Aula 18 (compila as fixtures antes).
# Uso: bash GQS18-fitnesse-start.sh [porta]   (padrao 8080)
cd "$(dirname "$0")/../fitnesse" && ./start.sh "${1:-8080}"
