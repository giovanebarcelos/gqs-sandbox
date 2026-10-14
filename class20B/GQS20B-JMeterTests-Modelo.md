# JMeterTests.md — Modelo preenchido (A1 · Tema 4)

Modelo de documentação para o Tema 4 da A1. Os números são de uma execução real (JMeter 5.6.3, Java 25, Linux) contra a API Node da **prova**; os seus serão diferentes. O que importa é a estrutura: **o que foi configurado → como rodar → o que foi medido → o que significa**.

## 1. Pré-requisitos e como subir a API

- Java 8+ (`java -version`) e JMeter (`jmeter --version`).
- Uma única API na porta 5000 (Node e Flask usam a mesma porta; subir as duas gera `EADDRINUSE`):

```bash
cd 3-Bruno/api-node && npm install && node index.js
curl http://localhost:5000/api/books      # confirma antes de testar
```

## 2. Q15 — Teste de carga (`Q15-Carga.jmx`)

**Configuração**

| Elemento | Valor |
|---|---|
| Variáveis | `HOST=${__P(host,localhost)}`, `PORT=${__P(porta,5000)}` |
| HTTP Request Defaults | `${HOST}` / `${PORT}` / `http` |
| Thread Group | 50 threads, ramp-up 10 s, Loop Count Infinite, *Specify Thread lifetime* = 30 s |
| Sampler | `GET /api/books` |
| Assertions | Response Code = 200; Duration Assertion = 300 ms |
| Listeners | Summary Report, Aggregate Report (View Results Tree desabilitado) |

A duração de 30 s **inclui** o ramp-up de 10 s.

**Como executar**

```bash
rm -rf q15.jtl relatorios/q15
jmeter -n -t Q15-Carga.jmx -l q15.jtl -e -o relatorios/q15
# relatório: relatorios/q15/index.html
```

**Resultado**

| Label | Amostras | Erro % | Média | 90th pct | 95th pct | 99th pct | Máx | Vazão |
|---|---|---|---|---|---|---|---|---|
| `GET /api/books` | 242.461 | 0,00% | 5 ms | **7 ms** | 8 ms | 11 ms | 21 ms | 8.094/s |

**Interpretação.** Critério: 90% das respostas < 300 ms. O percentil 90 foi **7 ms**, portanto o critério foi **atendido** com ampla folga. A Duration Assertion de 300 ms não falhou nenhuma amostra (Error % = 0%), o que confirma a conclusão por um segundo caminho: com no máximo 10% de falhas por tempo, ao menos 90% das respostas ficam abaixo de 300 ms.

## 3. Q16 — Teste de estresse (`Q16-Estresse.jmx`)

**Como a rampa de 10 a 200 usuários em 2 minutos, mantida por 1 minuto, foi modelada**

| Grupo | Threads | Ramp-up | Duração | Papel |
|---|---|---|---|---|
| Base fixa | 10 | 0 s | 180 s | 10 usuários desde o início |
| Rampa | 190 | 120 s | 180 s | +190 usuários ao longo de 2 min |

Somados: 10 → 200 usuários em 120 s, e 200 usuários mantidos até 180 s (a duração do grupo **inclui** o ramp-up: 120 s + 60 s). Medido na execução: 27 threads aos ~10 s, 122 aos ~60 s e 200 a partir de ~115 s.

Sampler: `POST /api/books` com *HTTP Header Manager* (`Content-Type: application/json`) e corpo

```json
{"title": "Livro ${__threadNum}-${__UUID()}", "author": "Autor ${__time(HHmmss)}"}
```

Assertion: Response Code = 201. *Uniform Random Timer* de 100 a 300 ms (think time).

**Como executar**

```bash
rm -rf q16.jtl relatorios/q16
jmeter -n -t Q16-Estresse.jmx -l q16.jtl -e -o relatorios/q16     # ~3 minutos
```

**Resultado**

| Label | Amostras | Erro % | Tempo médio | 90th pct | Máx | Vazão |
|---|---|---|---|---|---|---|
| `POST /api/books` | 122.989 | **0,00%** | **0,7 ms** | 1 ms | 35 ms | 684/s |

**Interpretação.** Taxa de erro de 0% e tempo médio de 0,7 ms, estável durante toda a rampa. **O ponto de quebra não foi atingido com 200 usuários**: ele é maior que isso. Para localizá-lo seria preciso subir a carga (mais threads ou menos think time) até o erro ou o tempo começarem a crescer em *Response Times Over Time*.

## 4. Q17 — Cenário de negócio (`Q17-Cenario.jmx`)

**Configuração.** Thread Group com 20 threads, ramp-up 5 s, Loop Infinite, duração 60 s. Dentro de um *Transaction Controller* (`Fluxo completo do empréstimo`), três samplers numerados:

1. `1 GET /api/books` — *JSON Extractor* guarda `$.books[0].id` em `bookId` (default `L001`)
2. `2 POST /api/loans` — corpo `{"userId": "U${__threadNum}", "bookId": "${bookId}", "daysLimit": 14}`; Response Code = 201; JSON Assertion `$.bookId == ${bookId}`
3. `3 POST /api/calculate-fine` — corpo `{"daysLate": ${__Random(0,30)}}`; Response Code = 200; JSON Assertion `$.currency == BRL`

*Uniform Random Timer* de 500 a 1.500 ms entre as requisições. Todos os passos usam a mesma API (porta 5000).

**Como executar**

```bash
rm -rf q17.jtl relatorios/q17
jmeter -n -t Q17-Cenario.jmx -l q17.jtl -e -o relatorios/q17
```

**Tempos de cada etapa**

| Etapa | Amostras | Erro % | Média | 90th pct | Máx |
|---|---|---|---|---|---|
| `1 GET /api/books` | 387 | 0% | 1,4 ms | 2 ms | 25 ms |
| `2 POST /api/loans` | 382 | 0% | 1,7 ms | 3 ms | 4 ms |
| `3 POST /api/calculate-fine` | 377 | 0% | 1,7 ms | 2 ms | 3 ms |
| **Fluxo completo** | 382 | 0% | 4,8 ms | 7 ms | 30 ms |

**Interpretação.** O fluxo completo (4,8 ms) é a soma dos três passos; os timers não entram na medida. A diferença entre o número de amostras (387, 382, 377) vem do fim do teste, que interrompe algumas threads no meio do fluxo.

## 5. Como gerar e interpretar o relatório HTML

`-e -o pasta` gera o dashboard ao final da execução; `jmeter -g arquivo.jtl -o pasta` gera a partir de um `.jtl` existente. A pasta de saída precisa estar vazia e o `.jtl` antigo deve ser apagado (o JMeter acrescenta ao arquivo existente).

Na tabela *Statistics*: `Error %` (taxa de erro), `Average` (tempo médio), `90th/95th/99th pct` (percentis), `Transactions/s` (vazão). Nos gráficos *Response Times Over Time* e *Active Threads Over Time* procura-se o nível de carga em que o tempo ou o erro disparam (ponto de quebra).

## 6. Conceitos

- **Thread Group:** conjunto de usuários virtuais; define threads, ramp-up, laços e duração.
- **Ramp-up:** tempo para todos os usuários entrarem.
- **Sampler:** a requisição enviada (HTTP Request).
- **Listener:** coleta e mostra resultados.
- **Assertion:** valida a resposta (conteúdo, código, tempo).
- **Percentil (90th/95th):** tempo abaixo do qual está 90%/95% das amostras.
- **Think time (Timer):** pausa entre requisições, para simular um usuário real.

## 7. Problemas encontrados e como foram resolvidos

| Problema | Solução |
|---|---|
| API Node e Flask na mesma porta (`EADDRINUSE`) | subir só uma API |
| `Cannot write to '...' as folder is not empty` | apagar a pasta do `-o` antes de rodar |
| Relatório com dados de execuções antigas | apagar o `.jtl` antes de rodar |

---

## Apêndice — Simulado (mesmos métodos, outras APIs)

| Questão | Plano | Amostras | Erro % | Média | Percentil | Vazão |
|---|---|---|---|---|---|---|
| Q15 | `GET /api/ping`, 100 threads, 60 s | 481.083 | 0,00% | 11 ms | 95th = **16 ms** (< 200 ms) | 8.023/s |
| Q16 | `POST /api/orders`, 10 → 500 threads em 2 min + 1 min | 302.867 | 0,00% | 0,5 ms | 95th = 1 ms | 1.683/s |
| Q17 | `POST /api/process`, 50 threads, 60 s | 7.194 | 0,00% | 52 ms | 95th = 53 ms | 120/s |

A API do simulado simula 50 ms de processamento em `/api/process`, o que explica a média de ~52 ms na Q17.
