#!/usr/bin/env bash
# Remove o que o PIPELINE criou fora do docker-compose (Aula 26):
#   - os containers de staging e producao (label gqs26g.ambiente)
#   - as imagens gqs26g/boletim-java e gqs26g/boletim-python, de todas as versoes
# O Gitea, o runner e o Traefik continuam; para eles, use docker compose down.
set -euo pipefail

containers=$(docker ps -aq --filter label=gqs26g.ambiente)
if [ -n "$containers" ]; then
  docker rm -f $containers >/dev/null
  echo "Containers de staging/producao removidos."
fi

imagens=$(docker images -q --filter reference='gqs26g/*' | sort -u)
if [ -n "$imagens" ]; then
  docker rmi -f $imagens >/dev/null
  echo "Imagens gqs26g/* removidas."
fi
echo "Pronto."
