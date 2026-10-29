# =============================================================================
# Runner self-hosted do GitHub Actions (Aula 26)
#
# Imagem OFICIAL do runner (a mesma que o GitHub usa no Kubernetes), com o
# plugin "docker compose" acrescentado - o job de deploy precisa dele.
# =============================================================================
FROM ghcr.io/actions/actions-runner:2.338.2

USER root
RUN mkdir -p /usr/local/lib/docker/cli-plugins \
    && curl -fsSL -o /usr/local/lib/docker/cli-plugins/docker-compose \
       "https://github.com/docker/compose/releases/download/v5.6.1/docker-compose-linux-$(uname -m)" \
    && chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# O runner roda como root para usar o docker.sock da maquina sem depender do
# numero do grupo "docker", que muda de um sistema para outro.
ENV RUNNER_ALLOW_RUNASROOT=1

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
WORKDIR /home/runner
ENTRYPOINT ["/entrypoint.sh"]
