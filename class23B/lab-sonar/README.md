# GQS23B — Laboratório SonarQube + Biblioteca API (Java e Python) + Traefik

SonarQube Community Build analisando uma API REST de biblioteca **com defeitos plantados** — em **Java** (principal) e em **Python** (secundária) —, com testes unitários (JUnit 5 + JaCoCo / pytest + cobertura), testes funcionais (Bruno), Quality Profile e Quality Gate do curso e a análise como trava automática do build.

**Roteiro passo a passo:** [`GQS23B-Tutorial-SonarQube-Biblioteca.md`](GQS23B-Tutorial-SonarQube-Biblioteca.md) — Etapa 0 (preparar a máquina), Parte I (Java), Parte II (Python).

## Início rápido

```bash
cp .env.example .env                    # troque POSTGRES_PASSWORD
docker compose up -d --build            # + "--profile python" para subir também a API Python
# aguarde o SonarQube ficar "healthy", entre em http://sonar.localhost (admin/admin),
# troque a senha e gere um Global Analysis Token em My Account > Security
export SONAR_TOKEN=sqa_...
./scripts/analisar-java.sh              # JUnit + JaCoCo + SonarQube (espera o gate) + Bruno
```

| URL | Serviço |
|---|---|
| http://sonar.localhost | SonarQube |
| http://biblioteca.localhost | Biblioteca API — Java (Spring Boot) |
| http://biblioteca-python.localhost | Biblioteca API — Python (Flask), profile `python` |
| http://traefik.localhost | painel do Traefik |

| Pasta / arquivo | Conteúdo |
|---|---|
| `java/` | API Spring Boot com os defeitos plantados, suíte JUnit incompleta, `pom.xml` com JaCoCo e `sonar-maven-plugin` |
| `python/` | Mesma API em Flask, suíte pytest incompleta, `sonar-project.properties` e `.coveragerc` |
| `bruno/Biblioteca/` | 8 testes funcionais; ambientes `Traefik`, `TraefikPython`, `Local`, `LocalPython` |
| `solucao/` | Gabarito: código corrigido e suítes completas (Java e Python) |
| `scripts/configurar-sonar.sh` | Perfil "Sonar way comprehensive" + Quality Gate "GQS - Codigo Legado" pela Web API |
| `scripts/analisar-java.sh` / `analisar-python.sh` | Testes + análise com `sonar.qualitygate.wait` + Bruno; saem com erro se algo falhar |

O lab usa a porta 80, como o da Aula 24 (`class24/lab-kiwi`): pare um antes de subir o outro, ou troque `HTTP_PORT` no `.env`.
