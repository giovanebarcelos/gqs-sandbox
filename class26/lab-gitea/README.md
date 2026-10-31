# GQS26 — Laboratório Gitea Actions: Boletim API (Java e Python) + Traefik

Esteira de CI/CD **100% local**: **Gitea** (servidor Git + Actions), um **runner** próprio e o **Traefik**, com a API de boletim em **Java** (principal) e **Python** (secundária), cada uma com defeitos plantados. O pipeline executa testes **unitários**, com **mock**, de **integração**, de **API (Bruno)** e **relatório de cobertura** com gate de 80%, com **matriz** de JDK 17/21.

**Tutorial completo:** [`GQS26-Tutorial-GiteaActions.md`](GQS26-Tutorial-GiteaActions.md)
**Versão só Java:** [`GQS26-Tutorial-GiteaActions-Java.md`](GQS26-Tutorial-GiteaActions-Java.md) (rode `bash scripts/somente-java.sh` em `~/boletim` antes do primeiro commit)
**Tutorial irmão (GitHub Actions):** [`../lab-github/GQS26-Tutorial-GitHubActions.md`](../lab-github/GQS26-Tutorial-GitHubActions.md)

| URL | Serviço |
|---|---|
| `http://gitea.localhost` | Gitea (repositórios + Actions) |
| `http://boletim-staging.localhost` · `http://boletim.localhost` | API Java em staging e produção (criadas pelo pipeline) |
| `http://boletim-python-staging.localhost` · `http://boletim-python.localhost` | API Python em staging e produção |
| `http://cobertura.localhost` | relatórios de cobertura (`--profile cobertura`) |
| `http://traefik.localhost` | painel do Traefik |

## Início rápido

```bash
cp .env.example .env                 # cole em RUNNER_TOKEN a saída de: openssl rand -hex 20
docker compose up -d --build
docker exec -u git gqs26g_gitea gitea admin user create --admin \
  --username aluno --email aluno@gqs.local --password 'Troque-esta-senha!' --must-change-password=false

cp -r boletim ~/boletim && cd ~/boletim
git init -b main && git add . && git commit -m "Boletim API: versao inicial"
git remote add origin http://gitea.localhost/aluno/boletim.git
git push -u origin main              # acompanhe em http://gitea.localhost/aluno/boletim/actions
```

## O pipeline (`.gitea/workflows/ci-java.yml` e `ci-python.yml`)

1. **Testes** — Java: matriz JDK 17/21, `mvn verify` (unitários + mocks → integração → JaCoCo ≥ 80%) · Python: unitários + mocks → integração → `coverage report --fail-under=80`
2. **Imagem Docker** — `gqs26g/boletim-<linguagem>:<commit>` (só em `push`/`workflow_dispatch`)
3. **Deploy em staging** — `scripts/deploy.sh <linguagem> staging <commit>`
4. **Testes de API em staging** — Bruno; se passar, a imagem vira `<commit>-homologada`

Produção: workflow **Promover para producao** (`workflow_dispatch`), que só aceita versão homologada e também serve de rollback.

## Encerrar

```bash
./scripts/limpar-ambientes.sh        # staging, produção e imagens gqs26g/*
docker compose --profile cobertura down
docker compose down -v               # -v apaga também o Gitea e o cache do runner
```

O lab usa a porta 80, como os labs das Aulas 23B, 24, 25 e o do GitHub: pare um antes de subir o outro, ou troque `HTTP_PORT` no `.env`.
