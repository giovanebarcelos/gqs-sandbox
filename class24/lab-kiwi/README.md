# GQS24 — Laboratório Kiwi TCMS + TechStore API + Traefik

Ambiente completo da Aula 24: Kiwi TCMS, uma API de carrinho **com dois defeitos plantados**, testes unitários (pytest), testes de API (Bruno) e a publicação automática dos resultados no Kiwi.

**Roteiro passo a passo:** [`GQS24-Tutorial-KiwiTCMS-TechStore.md`](GQS24-Tutorial-KiwiTCMS-TechStore.md)

## Início rápido

```bash
cp .env.example .env                    # troque as senhas
docker compose up -d --build
docker exec -it gqs24_kiwi_web /Kiwi/manage.py initial_setup   # domínio: kiwi.localhost
```

| URL | Serviço |
|---|---|
| https://kiwi.localhost | Kiwi TCMS |
| http://app.localhost | TechStore API |
| http://traefik.localhost | painel do Traefik |

```bash
python3 -m venv .venv && source .venv/bin/activate
pip install -r app/requirements-dev.txt -r kiwi/requirements.txt
npm install -g @usebruno/cli

python3 kiwi/preparar_kiwi.py                                   # plano + casos CT01..CT16
TCMS_PLAN_ID=1 TCMS_BUILD=1.0.0 ./scripts/executar-testes.sh    # roda e publica no Kiwi
```

| Pasta | Conteúdo |
|---|---|
| `app/` | API Flask (`techstore/`), testes unitários (`tests/`), Dockerfile |
| `bruno/TechStore-Carrinho/` | coleção Bruno com os casos CT10 a CT16 (ambientes `Traefik` e `Local`) |
| `kiwi/` | scripts `tcms-api`: preparar o Kiwi e publicar `junit.xml` |
| `scripts/` | `executar-testes.sh` — pytest + Bruno + publicação |
