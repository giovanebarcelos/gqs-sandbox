# GQS26 — Laboratório GitHub Actions: Boletim API (Java e Python) + Traefik

Esteira de CI/CD no **GitHub Actions** para uma API REST de boletim escolar **com defeitos plantados**, em **Java** (principal) e **Python** (secundária): testes **unitários**, com **mock**, de **integração**, de **API (Bruno)** e **relatório de cobertura** com gate de 80%, em **matriz** de versões (Java 17/21; Python 3.11/3.12/3.13), workflow **reutilizável**, **composite action**, release no **GHCR**, deploy com **aprovação manual** em runner **self-hosted**, **CodeQL** e **Dependabot**.

**Tutorial completo:** [`GQS26-Tutorial-GitHubActions.md`](GQS26-Tutorial-GitHubActions.md)
**Versão só Java:** [`GQS26-Tutorial-GitHubActions-Java.md`](GQS26-Tutorial-GitHubActions-Java.md) (rode `bash somente-java.sh` na cópia do laboratório antes do primeiro commit)
**Tutorial irmão (Gitea Actions, 100% local):** [`../lab-gitea/GQS26-Tutorial-GiteaActions.md`](../lab-gitea/GQS26-Tutorial-GiteaActions.md)

| URL | Serviço |
|---|---|
| `http://boletim.localhost` | Boletim API — Java (produção local, atualizada pelo release) |
| `http://boletim-python.localhost` | Boletim API — Python |
| `http://cobertura.localhost` | relatórios de cobertura (`--profile cobertura`) |
| `http://traefik.localhost` | painel do Traefik |

## Início rápido

```bash
cp -r . ~/gqs26-boletim-ci && cd ~/gqs26-boletim-ci   # o repositório do aluno
git init -b main && git add . && git commit -m "Boletim API: versao inicial"
gh repo create gqs26-boletim-ci --public --source=. --remote=origin --push
# aba Actions: os pipelines falham de propósito — o tutorial conduz as correções

cp .env.example .env
docker compose up -d --build                    # Traefik + APIs locais (depois das correções)
docker compose --profile runner up -d --build   # + runner self-hosted (Etapa 8)
docker compose --profile cobertura up -d        # + relatórios de cobertura
```

## Workflows

| Arquivo | O que faz |
|---|---|
| `ci-java.yml` · `ci-python.yml` | matriz de versões → unitários + mocks → integração → cobertura (gate 80%) → testes de API |
| `testes-api.yml` | workflow **reutilizável**: `docker build` + API no runner + coleção Bruno |
| `.github/actions/bruno/` | **composite action**: instala o Bruno CLI e roda a coleção |
| `release.yml` | tag `v*` → imagens no GHCR → deploy no runner self-hosted (Environment `production`, com aprovação) |
| `codeql.yml` · `dependabot.yml` | SAST e SCA |

## Pastas

| Pasta | Conteúdo |
|---|---|
| `java/` | Spring Boot 3.5, `pom.xml` com JaCoCo + Failsafe, testes unitários, com mock (Mockito) e de integração (`*IT`) |
| `python/` | Flask 3, pytest + pytest-cov, testes unitários, com mock (`unittest.mock`) e de integração (`tests/integracao/`) |
| `bruno/Boletim-API/` | 8 testes de API; ambientes `Java`, `Python`, `Traefik`, `TraefikPython` |
| `runner/` | imagem do runner self-hosted (`actions-runner` + `docker compose`) |
| `cobertura/` | nginx que serve os relatórios gerados na sua máquina |
| `exercicios/` | código sem testes para provocar o gate de cobertura (Etapa 5) |

O lab usa a porta 80, como os labs das Aulas 23B, 24 e 25: pare um antes de subir o outro, ou troque `HTTP_PORT` no `.env`.
