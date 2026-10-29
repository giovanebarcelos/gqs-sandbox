#!/usr/bin/env bash
# =============================================================================
# Publica uma versao da API de frete num ambiente (Aula 25 - CI/CD).
#
#   scripts/deploy.sh staging  <versao>    ->  http://frete-staging.localhost
#   scripts/deploy.sh producao <versao>    ->  http://frete.localhost
#
# <versao> = 7 primeiros caracteres do commit, a tag da imagem gerada pelo
# pipeline (gqs25/frete-java:<versao>). Roda dentro do runner, que fala com o
# Docker da maquina pelo docker.sock: o container novo nasce ao lado do Gitea
# e do Traefik, na rede gqs25_rede.
# =============================================================================
set -euo pipefail

AMBIENTE=${1:?uso: deploy.sh <staging|producao> <versao>}
VERSAO=${2:?uso: deploy.sh <staging|producao> <versao>}

APP=frete-java
PORTA=8080
case "$AMBIENTE" in
  staging)  HOST=frete-staging.localhost ;;
  producao) HOST=frete.localhost ;;
  *) echo "Ambiente desconhecido: $AMBIENTE (use staging ou producao)" >&2; exit 2 ;;
esac

IMAGEM="gqs25/$APP:$VERSAO"
# Hifen, nao sublinhado: o Tomcat recusa (HTTP 400) nomes de host com "_".
CONTAINER="gqs25-$APP-$AMBIENTE"

docker image inspect "$IMAGEM" >/dev/null 2>&1 || {
  echo "Imagem $IMAGEM nao existe. O pipeline gerou essa versao?" >&2
  exit 1
}

echo "==> Publicando $IMAGEM em $AMBIENTE (http://$HOST)"
docker rm -f "$CONTAINER" >/dev/null 2>&1 || true
docker run -d --name "$CONTAINER" \
  --network gqs25_rede --restart unless-stopped \
  -e APP_VERSION="$VERSAO" \
  --label gqs25.ambiente="$AMBIENTE" \
  --label traefik.enable=true \
  --label "traefik.http.routers.$CONTAINER.rule=Host(\`$HOST\`)" \
  --label "traefik.http.routers.$CONTAINER.entrypoints=web" \
  --label "traefik.http.services.$CONTAINER.loadbalancer.server.port=$PORTA" \
  "$IMAGEM" >/dev/null

# Smoke test: o deploy so' termina quando a versao nova responde no health.
for _ in $(seq 1 45); do
  if curl -fs "http://$CONTAINER:$PORTA/api/health" | grep -q "\"$VERSAO\""; then
    echo "==> $AMBIENTE no ar com a versao $VERSAO"
    exit 0
  fi
  sleep 2
done
echo "A versao $VERSAO nao respondeu em 90 s. Ultimas linhas do log:" >&2
docker logs --tail 40 "$CONTAINER" >&2
exit 1
