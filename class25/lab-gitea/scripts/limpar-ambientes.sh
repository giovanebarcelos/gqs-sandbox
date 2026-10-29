#!/usr/bin/env bash
# Remove o que o PIPELINE criou fora do docker-compose (Aula 25):
#   - os containers de staging e producao (label gqs25.ambiente)
#   - as imagens gqs25/frete-java e gqs25/frete-python, de todas as versoes
# O Gitea, o runner e o Traefik continuam; para eles, use docker compose down.
set -euo pipefail

containers=$(docker ps -aq --filter label=gqs25.ambiente)
if [ -n "$containers" ]; then
  docker rm -f $containers >/dev/null
  echo "Containers de staging/producao removidos."
fi

imagens=$(docker images -q --filter reference='gqs25/*' | sort -u)
if [ -n "$imagens" ]; then
  docker rmi -f $imagens >/dev/null
  echo "Imagens gqs25/* removidas."
fi
echo "Pronto."
