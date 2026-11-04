# GQS25 — Laboratório de CI/CD com Gitea Actions

Esteira de CI/CD completa na sua máquina: **Gitea** (servidor Git + Actions), um **runner** próprio e o **Traefik**, com a API de frete da LojaOnline em **Java** (principal) e **Python** (secundária), cada uma com dois defeitos plantados.

**Tutorial completo:** [`GQS25-Tutorial-CICD-Gitea.md`](GQS25-Tutorial-CICD-Gitea.md)

| URL | Serviço |
|---|---|
| `http://gitea.localhost` | Gitea (repositórios + Actions) |
| `http://frete-staging.localhost` · `http://frete.localhost` | API Java em staging e produção (criadas pelo pipeline) |
| `http://frete-python-staging.localhost` · `http://frete-python.localhost` | API Python em staging e produção |
| `http://traefik.localhost` | painel do Traefik |

## Início rápido

**Linux/macOS (bash):**

```bash
cp .env.example .env                 # cole em RUNNER_TOKEN a saída de: openssl rand -hex 20
docker compose up -d --build
docker exec -u git gqs25_gitea gitea admin user create --admin \
  --username aluno --email aluno@gqs.local --password 'Troque-esta-senha!' --must-change-password=false

cp -r frete-java ~/frete-java && cd ~/frete-java
git init -b main && git add . && git commit -m "API de frete: versao inicial"
git remote add origin http://gitea.localhost/aluno/frete-java.git
git push -u origin main              # acompanhe em http://gitea.localhost/aluno/frete-java/actions
```

**Windows 11 (PowerShell):**

```powershell
Copy-Item .env.example .env
$b = New-Object byte[] 20; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); -join ($b | ForEach-Object { $_.ToString('x2') })
notepad .env                         # cole a saída acima em RUNNER_TOKEN e salve
docker compose up -d --build
docker exec -u git gqs25_gitea gitea admin user create --admin `
  --username aluno --email aluno@gqs.local --password 'Troque-esta-senha!' --must-change-password=false

Copy-Item -Recurse -Force frete-java $HOME\frete-java; cd $HOME\frete-java
git init -b main; git add .; git commit -m "API de frete: versao inicial"
git remote add origin http://gitea.localhost/aluno/frete-java.git
git push -u origin main              # acompanhe em http://gitea.localhost/aluno/frete-java/actions
```

## O pipeline (`.gitea/workflows/pipeline.yml`)

1. **Build, testes unitários e cobertura** — `mvn verify` (JUnit + JaCoCo ≥ 80%) ou `pytest --cov-fail-under=80`
2. **Imagem Docker** — `gqs25/frete-java:<commit>` (só em push no `main`)
3. **Deploy em staging** — `scripts/deploy.sh staging <commit>`
4. **Testes de API em staging** — Bruno; se passar, a imagem vira `<commit>-homologada`

Produção: workflow **Promover para producao** (`workflow_dispatch`), que só aceita versão homologada e também serve de rollback.

## Encerrar

**Linux/macOS (bash):**

```bash
./scripts/limpar-ambientes.sh        # staging, produção e imagens gqs25/*
docker compose down -v               # -v apaga também o Gitea e o cache do runner
```

**Windows 11 (PowerShell):**

```powershell
& "$env:ProgramFiles\Git\bin\bash.exe" scripts/limpar-ambientes.sh   # staging, produção e imagens gqs25/*
docker compose down -v               # -v apaga também o Gitea e o cache do runner
```

O lab usa a porta 80, como os labs das Aulas 23B e 24: pare um antes de subir o outro, ou troque `HTTP_PORT` no `.env`.
