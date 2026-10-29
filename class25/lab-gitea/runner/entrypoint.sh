#!/usr/bin/env bash
# Faz os nomes *.localhost funcionarem DENTRO do runner como na sua maquina.
#
# Dentro do runner, os nomes *.localhost apontam para 127.0.0.1 (o curl e o
# git fazem isso sozinhos; para o Bruno, o extra_hosts do docker-compose.yml).
# So' que 127.0.0.1, aqui, e' o proprio runner, onde nao ha' nada escutando.
# Estes encaminhadores repassam a porta 80 (e a HTTP_PORT, se for outra) do
# 127.0.0.1 do runner para o Traefik. Resultado: os testes de API do pipeline
# em http://frete-staging.localhost passam pelo mesmo Traefik que o seu
# navegador usa.
set -e

for porta in $(printf '%s\n' 80 "${HTTP_PORT:-80}" | sort -u); do
  socat "TCP-LISTEN:$porta,bind=127.0.0.1,fork,reuseaddr" TCP:traefik:80 &
done

exec run.sh
