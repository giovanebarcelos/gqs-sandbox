#!/usr/bin/env bash
# =============================================================================
# Publica uma versao da Boletim API num ambiente (Aula 26 - Gitea Actions).
#
#   scripts/deploy.sh <java|python> staging  <versao>
#   scripts/deploy.sh <java|python> producao <versao>
#
#   java    staging  -> http://boletim-staging.localhost
#           producao -> http://boletim.localhost
#   python  staging  -> http://boletim-python-staging.localhost
#           producao -> http://boletim-python.localhost
#
# <versao> = 7 primeiros caracteres do commit, a tag da imagem gerada pelo
# pipeline (gqs26g/boletim-<linguagem>:<versao>). Roda dentro do runner, que
# fala com o Docker da maquina pelo docker.sock: o container novo nasce ao lado
# do Gitea e do Traefik, na rede gqs26g_rede.
#
# Configuracao do ambiente (vem do Gitea: Settings > Actions):
#   INSTITUICAO   variavel  - aparece em /api/health
#   TOKEN_ADMIN   secret    - protege /api/admin/estatisticas
# =============================================================================
set -euo pipefail

LINGUAGEM=${1:?uso: deploy.sh <java|python> <staging|producao> <versao>}
AMBIENTE=${2:?uso: deploy.sh <java|python> <staging|producao> <versao>}
VERSAO=${3:?uso: deploy.sh <java|python> <staging|producao> <versao>}

case "$LINGUAGEM" in
  java)   APP=boletim-java;   PORTA=8080; PREFIXO=boletim ;;
  python) APP=boletim-python; PORTA=5000; PREFIXO=boletim-python ;;
  *) echo "Linguagem desconhecida: $LINGUAGEM (use java ou python)" >&2; exit 2 ;;
esac
case "$AMBIENTE" in
  staging)  HOST=$PREFIXO-staging.localhost ;;
  producao) HOST=$PREFIXO.localhost ;;
  *) echo "Ambiente desconhecido: $AMBIENTE (use staging ou producao)" >&2; exit 2 ;;
esac

IMAGEM="gqs26g/$APP:$VERSAO"
# Hifen, nao sublinhado: o Tomcat recusa (HTTP 400) nomes de host com "_".
CONTAINER="gqs26g-$APP-$AMBIENTE"

docker image inspect "$IMAGEM" >/dev/null 2>&1 || {
  echo "Imagem $IMAGEM nao existe. O pipeline gerou essa versao?" >&2
  exit 1
}

echo "==> Publicando $IMAGEM em $AMBIENTE (http://$HOST)"
docker rm -f "$CONTAINER" >/dev/null 2>&1 || true
# Secret e variavel entram pelo ambiente do container; o valor do secret nunca
# aparece na linha de comando nem no log.
docker run -d --name "$CONTAINER" \
  --network gqs26g_rede --restart unless-stopped \
  -e APP_VERSION="$VERSAO" \
  -e INSTITUICAO="${INSTITUICAO:-ambiente $AMBIENTE}" \
  -e TOKEN_ADMIN \
  --label gqs26g.ambiente="$AMBIENTE" \
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
