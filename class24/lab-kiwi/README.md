# GQS24 — Laboratório Kiwi TCMS + TechStore API (Java e Python) + Traefik  

Ambiente completo da Aula 24: Kiwi TCMS, uma API de carrinho **com dois defeitos plantados** — em **Java** (principal) e em **Python** (secundária) —, testes unitários (JUnit 5 / pytest), testes de API (Bruno) e a publicação automática dos resultados no Kiwi.  

**Roteiro passo a passo:** [`GQS24-Tutorial-KiwiTCMS-TechStore.md`](GQS24-Tutorial-KiwiTCMS-TechStore.md) — Parte I (Java), depois Parte II (Python).  

## Início rápido  

```bash  
cp .env.example .env                    # troque as senhas  
docker compose up -d --build            # + "--profile python" para subir também a API Python  
docker exec -it gqs24_kiwi_web /Kiwi/manage.py initial_setup   # domínio: kiwi.localhost  
```  

| URL | Serviço |  
|---|---|  
| https://kiwi.localhost | Kiwi TCMS |  
| http://app.localhost | TechStore API — Java (Spring Boot) |  
| http://app-python.localhost | TechStore API — Python (Flask), profile `python` |  
| http://traefik.localhost | painel do Traefik |  

### Parte I — Java  

```bash  
npm install -g @usebruno/cli  
mvn -q -f java/kiwi/pom.xml package                             # gera kiwi-tools.jar  
java -jar java/kiwi/target/kiwi-tools.jar preparar              # plano + casos CT01..CT16  
TCMS_PLAN_ID=1 TCMS_BUILD=1.0.0 ./scripts/executar-testes.sh    # JUnit + Bruno -> Kiwi  
```  

### Parte II — Python  

```bash  
python3 -m venv .venv && source .venv/bin/activate  
pip install -r python/app/requirements-dev.txt -r python/kiwi/requirements.txt  
TCMS_PLAN_ID=1 TCMS_BUILD=1.0.0 ./scripts/executar-testes-python.sh   # pytest + Bruno -> Kiwi  
```  

| Pasta | Conteúdo |  
|---|---|  
| `java/app/` | API Spring Boot, testes JUnit 5 (CT01–CT08), Dockerfile multi-stage |  
| `java/kiwi/` | `kiwi-tools.jar`: cliente JSON-RPC do Kiwi com os comandos `preparar` e `publicar` |  
| `python/app/` | API Flask, testes pytest (CT01–CT08), Dockerfile |  
| `python/kiwi/` | `preparar_kiwi.py` e `publicar_resultados.py` (biblioteca oficial `tcms-api`) |  
| `bruno/TechStore-Carrinho/` | coleção Bruno CT10–CT16; ambientes `Traefik`, `TraefikPython`, `Local`, `LocalPython` |  
| `scripts/` | `executar-testes.sh` (Java) e `executar-testes-python.sh` (Python) |  
| `tcms.conf.example` | modelo do `~/.tcms.conf`, usado pelas ferramentas Java e Python |  
