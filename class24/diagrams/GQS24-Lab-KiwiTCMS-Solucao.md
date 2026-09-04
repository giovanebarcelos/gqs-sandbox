# Gabarito GQS24 — Laboratório Kiwi TCMS

**Aula 24 — Garantia da Qualidade de Software** | Prof. Giovane Barcelos
**Uso do professor.** Todo o roteiro foi executado num Kiwi TCMS 16.3 real (imagem `kiwitcms/kiwi:latest`, MariaDB 11), com `tcms-api` 15.0, `kiwitcms-junit.xml-plugin` 15.0 e pytest 8. Os números abaixo são medidos, não estimados.

---

## Estado final medido

### Hierarquia criada

| Objeto | id | Valor |
|---|---|---|
| Classification | 1 | `Aplicacoes Web` |
| Product | 1 | `TechStore` |
| Version | **2** | `1.5.0` |
| Build | **3** | `v1.5.0-RC1` |
| Category | **2** | `Autenticacao` |
| TestPlan | 1 | `Sprint 5 - Modulo Autenticacao` (tipo `System`) |
| TestCase | 1, 2, 3 | `TS-001` (P1), `TS-002` (P1), `TS-003` (P2) |
| TestRun | 1 | `Execucao v1.5.0-RC1` |
| TestRun | 2 | `[junit.xml] Results for TechStore, 1.5.0, v1.5.0-RC1` |
| TestCase | 4, 5, 6 | os três testes `pytest` (criados pelo plugin) |
| Bug | 1 | `Mensagem de login revela se o e-mail existe na base`, severidade `Alta` |

**Por que Version = 2 e Category = 2, e não 1?** Ao criar o Product, o Kiwi cria sozinho a versão `unspecified` (id 1) e a categoria `--default--` (id 1). É o tipo de detalhe que só aparece rodando a ferramenta — e que confunde o aluno que espera ids sequenciais a partir de 1.

### Rodadas

```
TR-1 Execucao v1.5.0-RC1  (plano 1)
    TE-1 PASSED   TS-001 Login com credenciais validas
    TE-2 FAILED   TS-002 Login com senha incorreta
    TE-3 IDLE     TS-003 Bloqueio apos 3 tentativas

TR-2 [junit.xml] Results for TechStore, 1.5.0, v1.5.0-RC1  (plano 1)
    TE-4 PASSED   test_login_com_credenciais_validas
    TE-5 FAILED   test_login_com_senha_incorreta
    TE-6 PASSED   test_bloqueio_apos_3_tentativas
```

| Medida | TR-1 (manual) | TR-2 (automatizada) |
|---|---|---|
| Total | 3 | 3 |
| PASSED | 1 | 2 |
| FAILED | 1 | 1 |
| IDLE | 1 | 0 |
| Progresso | 2/3 = **67%** | 3/3 = **100%** |
| Taxa de aprovação | 1/2 = **50%** | 2/3 = **67%** |

### Telemetria (saída literal)

`Testing.breakdown`:

```json
{
  "count": {"manual": 3, "automated": 3, "all": 6},
  "priorities": {"CONFIRMED": {"P1": 1, "P2": 1}, "OTHER": {}},
  "categories": {"CONFIRMED": {"--default--": 1, "Autenticacao": 1}, "OTHER": {}}
}
```

`Testing.test_case_health`:

```
TC-2 TS-002 Login com senha incorreta:        1 falha em 1 execução
TC-5 test_login_com_senha_incorreta:          1 falha em 1 execução
```

`Testing.execution_trends`:

```
rodadas: [1, 2]
IDLE     [1, 0]
PASSED   [1, 2]
FAILED   [1, 1]
TOTAL    [3, 3]
contagem: {'positive': 3, 'negative': 2, 'neutral': 1}
```

---

## Etapa 1 — comentários

- `kiwi_web` chegou a `healthy` em **~35 s** na medição. Antes disso, qualquer acesso falha.
- O `initial_setup` imprime os passos numerados **1, 2, 3 e 5**. O passo 4 (`Creating public & empty tenants`) está no código sob `if "tcms_tenants" in settings.INSTALLED_APPS` — só existe na edição Enterprise. Alunos costumam achar que "faltou um passo"; não faltou.
- Sem `-it`, o Django imprime *"Superuser creation skipped due to not running in a TTY"* e o setup segue adiante. O aluno só descobre na hora de logar.
- O certificado embutido tem CN `buildkitsandbox` — **não** `localhost`. Por isso confiar no certificado não resolve: a verificação de hostname continua falhando. É por isso que os scripts desligam a verificação em vez de importar o certificado.

---

## Etapa 2 — resposta esperada

> **Version × Build.** A *Version* é a versão do produto sob teste (`1.5.0`); o *Build* é o pacote concreto daquela versão que foi exercitado (`v1.5.0-RC1`). Corrigir um defeito e regerar o pacote produz um **novo Build** dentro da **mesma Version** — e isso é exatamente o que permite comparar `RC1` com `RC2` sem duplicar nenhum caso de teste. O histórico de execução fica preso ao Build; o caso de teste continua único.

---

## Etapa 3 — resposta esperada

> O critério do TS-002 é escrito como **negativa explícita** ("não revelar se o e-mail existe") porque o defeito que ele persegue não é uma falha funcional: o login *rejeita* a senha errada corretamente. O que está errado é a **quantidade de informação** que a mensagem entrega — um atacante consegue descobrir quais e-mails estão cadastrados testando um a um (enumeração de usuários). Um critério genérico do tipo "verificar mensagem de erro" passaria: existe mensagem, e ela é coerente. Só um critério que diz o que **não pode** aparecer reprova o comportamento.

Ligação com o resto do curso: é o mesmo raciocínio da Aula 11 — teste não encontra o que ninguém pensou em especificar.

---

## Etapa 4 — resposta esperada

> **Progresso** responde "quanto da rodada já foi feito" (2/3 = 67%). **Taxa de aprovação** responde "do que foi feito, quanto passou" (1/2 = 50%). São perguntas diferentes e podem andar em direções opostas: uma rodada com 1 caso executado e aprovado tem 100% de aprovação e 33% de progresso. Reportar só a aprovação esconde o que não foi testado — e é justamente o que não foi testado que costuma quebrar em produção.

O `IDLE` proposital do TS-003 existe para forçar essa distinção. Se todos os casos estiverem executados, o aluno não percebe que são duas métricas.

---

## Etapa 5 — resposta esperada

> **Severidade** é técnica e quem mede é quem testou: esta falha permite enumerar contas válidas, logo **Alta**. **Prioridade** é de negócio e quem decide é o Product Owner: pode ser alta (se a base de usuários for alvo conhecido de ataque) ou média (se houver rate limiting no proxy que reduza a exploração). As duas dimensões são independentes — é exatamente o conteúdo do Slide 11.

### Detalhes de implementação medidos

- `Severity` **não vem preenchido** numa instalação nova: `Severity.filter({})` devolve `[]`.
- `Severity.create` exige `icon` e `color`; sem eles a API responde
  `Internal error: [('icon', ['This field is required.']), ('color', ['This field is required.'])]`.
- `Bug.create` exige `product`, `version` e `build`. Sem eles:
  `Internal error: [('product', ['This field is required.']), ('version', ...), ('build', ...)]`.
- `status: True` significa **aberto**; `False`, fechado. É um booleano, não uma máquina de estados como no Jira — a Community Edition não tem workflow configurável de defeitos.
- Passar `execution` dentro de `Bug.create` **não** cria o vínculo (o campo `executions` volta `[]`). O vínculo se faz depois, com `Bug.add_execution(bug_id, execution_id)`.

---

## Etapa 6 — respostas esperadas

**1. Que chamada cria a `TestExecution`?**

> `TestRun.add_case(run_id, case_id)`. Não existe caminho normal por `TestExecution.create`: a execução nasce como efeito de colocar o caso na rodada, sempre em `IDLE`. Registrar o resultado é depois um `TestExecution.update(id, {"status": ...})`.

**2. Por que buscar os ids de `PlanType`, `Priority`, `TestCaseStatus` e `TestExecutionStatus`?**

> Porque a API trabalha com chaves estrangeiras, não com rótulos. `type`, `priority`, `case_status` e `status` esperam **inteiros**. Os nomes são só a apresentação — e são traduzíveis, o que os torna instáveis como identificador. Buscar o id pelo nome uma vez, no começo do script, é o que mantém o código legível sem depender de números mágicos.

**3. O que mudaria com certificado válido?**

> A linha `ssl._create_default_https_context = ssl._create_unverified_context` sairia. Ela existe só porque o certificado da imagem é autoassinado e com CN errado. Em produção, mantê-la significa aceitar qualquer certificado — inclusive o de um ataque de intermediário — para falar com o servidor que guarda os resultados de teste da empresa.

---

## Etapa 7 — respostas esperadas

### 7.2 — com e sem `TCMS_PLAN_ID`

> **Sem** a variável, o plugin cria um plano próprio, `[junit.xml] Plan for TechStore (1.5.0)`, e a automação vive num universo paralelo ao trabalho manual. **Com** `TCMS_PLAN_ID` apontando para o plano da sprint, a rodada automatizada entra no mesmo plano e o relatório da sprint passa a somar as duas origens. A segunda forma é a que faz sentido quando existe um plano de sprint; a primeira serve quando a suíte automatizada é o único artefato (por exemplo, um repositório de biblioteca sem QA manual).

### 7.3 — pergunta final (a mais importante do laboratório)

> Sim, é um problema — e é o problema clássico de quem liga automação a um TCMS sem combinar antes. O plugin cria o caso a partir do **nome do teste** no `junit.xml`; como esse nome não bate com o título do caso manual, nasce um caso novo. Resultado: `TS-002` e `test_login_com_senha_incorreta` cobrem a mesma regra e aparecem como dois casos, cada um com seu histórico. O `TestCase health` acusa dois "casos doentes" onde há um só defeito.
>
> Saídas possíveis, em ordem de custo:
>
> 1. **Convenção de nomes**: nomear o teste automatizado com o mesmo título do caso manual e usar `--summary-template` para reproduzi-lo; o plugin passa a reaproveitar o caso existente em vez de criar outro.
> 2. **Casos manuais como fonte única**: marcar o caso no Kiwi como `is_automated=True` e deixar a automação apenas atualizar a execução, nunca criar caso.
> 3. **Separação assumida**: manter os dois conjuntos, mas com categorias distintas (`Autenticacao` × `--default--`, como aconteceu aqui) e relatórios separados, sabendo que a soma de "6 casos" não é comparável a "6 requisitos cobertos".
>
> A resposta errada é não decidir: é assim que uma base de casos de teste vira duas, e o número total de casos deixa de significar qualquer coisa.

---

## Limitações reais da versão 16.3, medidas em sala

| Chamada | Sintoma | Encaminhamento |
|---|---|---|
| `Testing.status_matrix` | falha por XML-RPC: `Unable to serialize result data: ... dictionary key must be string` | funciona pelo endpoint `/json-rpc/`, que é o que a própria interface web usa |
| `Bug.details` | `Internal error: 'int' object has no attribute 'decode'` nos **dois** protocolos | usar `Bug.filter` |
| `Bug.filter` × `Bug.create` | `create` devolve a chave `id`; `filter` devolve `pk` para o mesmo campo | normalizar no cliente (o script da aula faz isso) |
| `kiwitcms-junit.xml-plugin` 15.0 | `ValueError: Unknown timestamp format 2026-09-04T08:37:48.180560-03:00` com pytest 8 | o plugin só entende três formatos de data, nenhum com fuso; a aula sobrescreve `parse_timestamp` com `datetime.fromisoformat` |

Nenhuma dessas é erro de instalação. Vale dizer isso em voz alta: **a ferramenta que mede a qualidade dos outros também tem defeitos**, e descobrir isso rodando — em vez de acreditar na documentação — é parte do trabalho.

---

## Edição Community: o que dizer aos alunos

| Ponto | Realidade |
|---|---|
| Licença | GPL-2.0, funcionalidade completa |
| Anúncios | a interface exibe anúncios (EthicalAds); a receita vai para o Open Collective do projeto |
| Versionamento | *rolling release*: só a tag `latest` é publicada, sem tags de versão fixa |
| Arquitetura | a imagem pública é **x86_64**; em Mac com Apple Silicon roda sob emulação, lento |
| Bug tracker | próprio, sem workflow configurável; integrações com Bugzilla e JIRA já vêm pré-cadastradas em `BugTracker.filter` |

O ponto pedagógico: "open source e gratuito" não significa "sem contrapartidas". Avaliar ferramenta é comparar contrapartidas, não contar funcionalidades.

---

## Encerramento do ambiente

```bash
docker compose -f GQS24-kiwi-docker-compose.yml down -v
docker rmi kiwitcms/kiwi:latest mariadb:11
```
