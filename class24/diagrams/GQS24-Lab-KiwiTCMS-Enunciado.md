# Laboratório GQS24 — Kiwi TCMS: do caso de teste ao defeito rastreado

**Aula 24 — Garantia da Qualidade de Software** | Prof. Giovane Barcelos
**Duração estimada:** 95 a 115 minutos | **Pré-requisitos:** Docker, Python 3.10+

---

## O que você vai construir

Um servidor de gestão de testes (Kiwi TCMS) rodando localmente, com um produto modelado do zero, três casos de teste escritos à mão, uma rodada de execução registrada, um defeito aberto no bug tracker **integrado** e — na segunda metade — a mesma coisa feita por API, com os resultados de uma suíte `pytest` publicados automaticamente.

O objetivo não é decorar menus. É entender **por que uma ferramenta de TCMS existe**: uma planilha guarda o texto do caso de teste, mas não guarda o histórico de execução por versão, não liga o defeito à execução que o encontrou e não responde "este caso de teste falha sempre ou falhou só hoje?".

**Números de referência** (medidos numa execução real com Kiwi TCMS 16.3, MariaDB 11, `tcms-api` 15.0, `kiwitcms-junit.xml-plugin` 15.0 e pytest 8 — os seus devem bater com estes):

| Medida | Rodada manual (TR-1) | Rodada automatizada (TR-2) |
|---|---|---|
| Casos na rodada | 3 | 3 |
| PASSED | 1 | 2 |
| FAILED | 1 | 1 |
| IDLE (não executado) | 1 | 0 |
| Progresso (executados / total) | 67% | 100% |
| Taxa de aprovação (passed / executados) | 50% | 67% |

Ao final, `TELEMETRY > Testing > Breakdown` deve mostrar `manual: 3, automated: 3, all: 6`.

---

## Etapa 1 — Subir o Kiwi TCMS (15 min)

### 1.1 Preparar o `.env`

```bash
cd repository/class24/diagrams
cp GQS24-kiwi-.env.example .env
```

Abra o `.env` e **troque as duas senhas**. Elas ficam só na sua máquina: o `.env` está no `.gitignore` justamente porque senha não vai para o repositório — é a mesma regra que o SonarQube cobra com a regra `S6437` na Aula 23B.

### 1.2 Subir os containers

```bash
docker compose -f GQS24-kiwi-docker-compose.yml up -d
```

São dois containers: `kiwi_db` (MariaDB) e `kiwi_web` (a aplicação Django). O `kiwi_web` só inicia depois que o banco responde ao *healthcheck*.

**Se aparecer `port is already allocated`**, alguma coisa na sua máquina já usa a 8080 ou a 8443. Não edite o compose — acrescente ao `.env`:

```
KIWI_HTTP_PORT=8090
KIWI_HTTPS_PORT=8453
```

e suba de novo. O laboratório inteiro funciona igual, só muda a porta na URL.

### 1.3 Esperar ficar pronto

```bash
docker inspect -f '{{.State.Health.Status}}' kiwi_web
# starting  ... starting ... healthy
```

Na medição de referência o `kiwi_web` ficou `healthy` em cerca de **35 segundos**.

### 1.4 Rodar o setup inicial

```bash
docker exec -it kiwi_web /Kiwi/manage.py initial_setup
```

Repare no `-it`: o comando é **interativo** e vai perguntar, nesta ordem:

1. `Username:` → `admin`
2. `Email address:` → `admin@techstore.local`
3. `Password:` e `Password (again):` → escolha uma senha e **anote**
4. `Enter Kiwi TCMS domain:` → `localhost:8443` (ou a porta que você definiu)

Sem TTY o comando não cria o superusuário — ele imprime *"Superuser creation skipped due to not running in a TTY"* e você fica sem conseguir entrar.

O comando executa quatro passos numerados na tela: `1. Applying migrations`, `2. Creating superuser`, `3. Setting the domain name` e `5. Setting permissions`. O passo 4 (`Creating public & empty tenants`) só aparece na edição Enterprise, que tem suporte a múltiplos inquilinos — na Community ele é pulado.

### 1.5 Acessar

Abra `https://localhost:8443` (ou a porta do seu `.env`). O certificado é autoassinado e gerado dentro da imagem — o navegador vai reclamar; aceite o aviso. Faça login com o usuário do passo anterior.

**Anote no relatório:** a versão que aparece no rodapé do menu de ajuda (`?` no canto superior direito).

---

## Etapa 2 — Modelar o produto (15 min)

O Kiwi TCMS organiza tudo abaixo de um **Product**, e todo Product precisa de uma **Classification**. Uma instalação nova **não tem nenhuma classificação cadastrada** — se você tentar criar o produto direto, a lista vem vazia.

Vá em `ADMIN > Everything else` (o admin do Django) e crie, nesta ordem:

| Passo | Onde | O que criar |
|---|---|---|
| 2.1 | `Management > Classifications` | `Aplicacoes Web` |
| 2.2 | `Management > Products` | `TechStore`, classificação `Aplicacoes Web` |
| 2.3 | `Management > Versions` | `1.5.0` para o produto `TechStore` |
| 2.4 | `Management > Builds` | `v1.5.0-RC1` para a versão `1.5.0` |
| 2.5 | `Testcases > Categories` | `Autenticacao` para o produto `TechStore` |

**Observe:** ao criar o produto, o Kiwi cria sozinho uma versão `unspecified` e uma categoria `--default--`. Por isso a sua versão `1.5.0` não recebe o id 1.

**Pergunta para o relatório:** qual a diferença entre **Version** e **Build**? Se o time corrigir um defeito e gerar um novo pacote da mesma versão, o que muda no Kiwi?

---

## Etapa 3 — Escrever os casos de teste (20 min)

### 3.1 Criar o plano

`TESTING > New Test Plan`:

| Campo | Valor |
|---|---|
| Name | `Sprint 5 - Modulo Autenticacao` |
| Product | `TechStore` |
| Product version | `1.5.0` |
| Type | `System` |
| Text | descreva em duas linhas o escopo do plano |

Os tipos de plano que já vêm cadastrados são: `Unit`, `Integration`, `Function`, `System`, `Acceptance`, `Installation`, `Performance`, `Product`, `Interoperability`, `Smoke` e `Regression`.

### 3.2 Criar os três casos

Ainda dentro do plano, use o botão de novo caso de teste. Escreva os três, **no formato Dado/Quando/Então**:

| Caso | Prioridade | Roteiro |
|---|---|---|
| `TS-001 Login com credenciais validas` | P1 | Dado um usuário cadastrado e ativo / Quando informar e-mail e senha corretos / Então o sistema deve exibir a página inicial autenticada |
| `TS-002 Login com senha incorreta` | P1 | Dado um usuário cadastrado e ativo / Quando informar a senha errada / Então o sistema deve exibir "Credenciais inválidas" **e não revelar se o e-mail existe** |
| `TS-003 Bloqueio apos 3 tentativas` | P2 | Dado um usuário que errou a senha 2 vezes / Quando errar a senha pela terceira vez / Então a conta deve ser bloqueada por 15 minutos |

Todos com **Category** `Autenticacao` e **Status** `CONFIRMED`.

**Atenção ao status:** um caso `PROPOSED` não pode ser adicionado a uma rodada de execução. Os quatro status são `PROPOSED`, `CONFIRMED`, `DISABLED` e `NEED_UPDATE` — só o `CONFIRMED` é executável.

**Pergunta para o relatório:** por que o roteiro do TS-002 diz explicitamente o que o sistema **não** pode fazer? Que tipo de defeito esse critério pega e um "verificar mensagem de erro" genérico não pegaria?

---

## Etapa 4 — Executar a rodada (15 min)

### 4.1 Criar a rodada

`TESTING > New Test Run`, a partir do plano `Sprint 5`:

| Campo | Valor |
|---|---|
| Summary | `Execucao v1.5.0-RC1` |
| Build | `v1.5.0-RC1` |
| Manager / Default tester | você |
| Casos | os três |

Ao adicionar os casos, o Kiwi cria uma **TestExecution** para cada um, todas em `IDLE`.

### 4.2 Registrar os resultados

Abra a rodada e registre, exatamente assim:

| Caso | Status | Comentário |
|---|---|---|
| TS-001 | `PASSED` | — |
| TS-002 | `FAILED` | *A mensagem exibida foi "Usuário não encontrado", revelando a existência do e-mail. Defeito TS-002-D1.* |
| TS-003 | deixe em `IDLE` | rodada propositalmente incompleta |

Os oito status possíveis de uma execução são `IDLE`, `RUNNING`, `PAUSED`, `PASSED`, `FAILED`, `BLOCKED`, `ERROR` e `WAIVED`.

**Deixar o TS-003 em `IDLE` é de propósito.** Ele é o que separa "taxa de aprovação" de "progresso":

- progresso = executados / total = 2/3 = **67%**
- taxa de aprovação = passados / executados = 1/2 = **50%**

Uma equipe que reporta só "50% de aprovação" está escondendo que um terço da rodada nem foi testado.

**Anote no relatório:** as duas taxas, e a diferença entre elas.

---

## Etapa 5 — Registrar o defeito (10 min)

Aqui está a diferença mais visível em relação ao TestLink: o Kiwi TCMS **tem bug tracker próprio**. Não é preciso Jira, Mantis ou Bugzilla para registrar o defeito.

### 5.1 Criar a escala de severidade

O Kiwi sobe **sem nenhuma severidade cadastrada**. Em `ADMIN > Everything else > Bugs > Severities`, crie quatro, com pesos crescentes:

| Nome | Weight | Icon | Color |
|---|---|---|---|
| Baixa | 1 | `fa fa-info-circle` | `#0088ce` |
| Media | 2 | `fa fa-exclamation-triangle` | `#f0ab00` |
| Alta | 3 | `fa fa-exclamation-circle` | `#ec7a08` |
| Critica | 4 | `fa fa-fire` | `#cc0000` |

`icon` e `color` são obrigatórios — sem eles a criação é recusada.

### 5.2 Abrir o defeito

`TESTING > New Bug`:

| Campo | Valor |
|---|---|
| Summary | `Mensagem de login revela se o e-mail existe na base` |
| Product / Version / Build | `TechStore` / `1.5.0` / `v1.5.0-RC1` |
| Severity | `Alta` |
| Assignee | você |

No corpo, escreva **passos para reproduzir, resultado esperado e resultado obtido** — sem isso o desenvolvedor não corrige.

### 5.3 Amarrar o defeito à execução

Ligue o defeito à execução `FAILED` do TS-002. É esse vínculo que fecha a rastreabilidade **caso de teste → execução → defeito** e permite, mais tarde, responder "quais defeitos saíram do build v1.5.0-RC1?".

**Pergunta para o relatório:** a severidade deste defeito é Alta. E a prioridade — quem decide, e por quê ela pode ser diferente da severidade?

---

## Etapa 6 — Fazer o mesmo por API (15 min)

Tudo o que você fez nas Etapas 2 a 5 pela interface também é feito por API. É assim que a ferramenta entra num pipeline.

### 6.1 Instalar o cliente

```bash
python3 -m venv venv
source venv/bin/activate          # Windows: venv\Scripts\activate
pip install tcms-api
```

### 6.2 Configurar as credenciais **fora do código**

Crie `~/.tcms.conf` com permissão restrita:

```ini
[tcms]
url = https://localhost:8443/xml-rpc/
username = admin
password = sua-senha
```

```bash
chmod 600 ~/.tcms.conf
```

Alternativamente, exporte `KIWI_URL`, `KIWI_USERNAME` e `KIWI_PASSWORD` — os scripts desta aula aceitam as duas formas. O que **não** se faz é escrever a senha dentro do `.py`.

### 6.3 Rodar o exemplo

```bash
python3 GQS24-kiwi-api-exemplo.py
```

O script recria a hierarquia inteira usando a estratégia *get or create*: como você já criou tudo pela interface com os mesmos nomes, ele **reaproveita** e não duplica nada. Rode duas vezes e confira que os ids não mudam.

Leia o código antes de rodar e responda, no relatório:

1. Que chamada cria a `TestExecution`? (dica: não é `TestExecution.create`)
2. Por que o script precisa buscar o id de `PlanType`, `Priority`, `TestCaseStatus` e `TestExecutionStatus` em vez de passar o nome direto?
3. O que o script faria de diferente se rodasse contra um servidor com certificado válido?

---

## Etapa 7 — Publicar resultados automatizados e ler a telemetria (20 min)

### 7.1 Rodar a suíte

O arquivo [`repository/class24/python/test_GQS2402-LoginTechStore.py`](../python/test_GQS2402-LoginTechStore.py) implementa em `pytest` os mesmos três casos que você escreveu à mão — e contém, de propósito, o defeito TS-002-D1.

```bash
pip install pytest "kiwitcms-junit.xml-plugin"
pytest -q --junitxml=resultados.xml
# 1 failed, 2 passed
```

### 7.2 Publicar no Kiwi

```bash
export TCMS_PRODUCT="TechStore"
export TCMS_PRODUCT_VERSION="1.5.0"
export TCMS_BUILD="v1.5.0-RC1"
export TCMS_PLAN_ID=1        # id do plano "Sprint 5" — confira o seu
python3 GQS24-kiwi-publicar-resultados.py resultados.xml
```

Uma nova rodada aparece no plano, chamada `[junit.xml] Results for TechStore, 1.5.0, v1.5.0-RC1`, com os três resultados já preenchidos.

**Sem `TCMS_PLAN_ID`**, o plugin cria um plano separado, `[junit.xml] Plan for TechStore (1.5.0)`. Teste as duas formas e explique no relatório qual faz mais sentido e por quê.

### 7.3 Ler as métricas

Rode:

```bash
python3 GQS24-kiwi-registrar-defeito.py
```

e compare a saída com o menu `TELEMETRY` da interface. Registre no relatório:

| Métrica | Onde vê na interface | Seu valor |
|---|---|---|
| Casos manuais × automatizados | `Testing > Breakdown` | |
| Casos que mais falham | `Testing > TestCase health` | |
| Evolução entre as rodadas | `Testing > Execution > Trends` | |

**Pergunta final:** o `TestCase health` lista dois casos com falha — `TS-002` e `test_login_com_senha_incorreta`. São dois casos diferentes no Kiwi, mas testam a mesma coisa. Isso é problema? O que você faria para o time não acabar com duas bases de casos de teste paralelas?

---

## Etapa 8 — Encerrar o ambiente

```bash
docker compose -f GQS24-kiwi-docker-compose.yml down     # para, mantendo os dados
docker compose -f GQS24-kiwi-docker-compose.yml down -v  # para e APAGA tudo
```

---

## O que entregar

Um documento (PDF ou Markdown) com:

1. **Print** do plano `Sprint 5` mostrando os três casos.
2. **Print** da rodada manual com 1 PASSED, 1 FAILED e 1 IDLE.
3. **Print** do defeito, mostrando a severidade e o vínculo com a execução.
4. **Print** de `TELEMETRY > Testing > Breakdown` depois da publicação automatizada.
5. As **duas taxas** da Etapa 4.2 (progresso e aprovação), com a conta feita.
6. As respostas às perguntas das Etapas 2, 3, 5, 6, 7.2 e 7.3.

---

## Armadilhas conhecidas

| Sintoma | Causa | Solução |
|---|---|---|
| `port is already allocated` | 8080/8443 já ocupadas | defina `KIWI_HTTP_PORT` / `KIWI_HTTPS_PORT` no `.env` |
| Não consegue fazer login depois do setup | `initial_setup` rodou sem `-it` | rode de novo com `docker exec -it` |
| Lista de classificações vazia ao criar o produto | instalação nova não tem nenhuma | crie a classificação primeiro (Etapa 2.1) |
| `RuntimeError: Config file 'c:/tcms.conf' not found` | `~/.tcms.conf` ausente | crie o arquivo ou use as variáveis de ambiente |
| `SSLCertVerificationError` | certificado autoassinado do container | os scripts da aula já tratam; veja o comentário "APENAS EM LABORATÓRIO" |
| `ValueError: Unknown timestamp format` | pytest 8 grava o horário em ISO-8601 com fuso, que o plugin 15.0 não entende | use o `GQS24-kiwi-publicar-resultados.py` desta aula, que corrige isso |
| Caso não aparece para adicionar à rodada | status `PROPOSED` | mude para `CONFIRMED` |
