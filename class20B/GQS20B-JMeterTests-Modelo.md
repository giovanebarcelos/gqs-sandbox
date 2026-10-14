# README-JMeter.md — Modelo preenchido (Simulado da A1 · Tema 4)

Modelo de documentação para o Tema 4 do **Simulado da A1**. Os números são de uma execução real (JMeter 5.6.3, Java 25, Linux) contra a API Node do simulado; os seus serão diferentes. O que importa é a estrutura: **o que foi configurado → como rodar → o que foi medido → o que significa**.

## 1. Pré-requisitos e como subir a API

- Java 8+ (`java -version`) e JMeter (`jmeter --version`).
- Uma única API na porta 5000 (Node e Flask usam a mesma porta; subir as duas gera `EADDRINUSE`):

```bash
cd 3-Bruno/api-node && npm install && node index.js
curl http://localhost:5000/api/ping      # {"pong":"ok"} — confirma antes de testar
```

## 2. Q15 — Teste de carga (`Q15-Carga.jmx`)

**Configuração**

| Elemento | Valor |
|---|---|
| Variáveis | `HOST=${__P(host,localhost)}`, `PORT=${__P(porta,5000)}` |
| HTTP Request Defaults | `${HOST}` / `${PORT}` / `http` |
| Thread Group | 100 threads, ramp-up 10 s, Loop Count Infinite, *Specify Thread lifetime* = 60 s |
| Sampler | `GET /api/ping` |
| Assertions | Response Code = 200; Duration Assertion = 200 ms |
| Listeners | Summary Report, Aggregate Report (View Results Tree desabilitado) |

A duração de 60 s **inclui** o ramp-up de 10 s.

**Como executar**

```bash
rm -rf q15.jtl relatorios/q15
jmeter -n -t Q15-Carga.jmx -l q15.jtl -e -o relatorios/q15
# relatório: relatorios/q15/index.html
```

**Resultado**

| Label | Amostras | Erro % | Média | 90th pct | 95th pct | 99th pct | Máx | Vazão |
|---|---|---|---|---|---|---|---|---|
| `GET /api/ping` | 969.530 | 0,00% | 5,7 ms | 8 ms | **8 ms** | 10 ms | 32 ms | 16.164/s |

**Interpretação.** Critério: 95% das respostas < 200 ms. O percentil 95 foi **8 ms**, portanto o critério foi **atendido** com ampla folga. A Duration Assertion de 200 ms não falhou nenhuma amostra (Error % = 0%), o que confirma a conclusão por um segundo caminho: com no máximo 5% de falhas por tempo, ao menos 95% das respostas ficam abaixo de 200 ms.

## 3. Q16 — Teste de estresse (`Q16-Estresse.jmx`)

**Como a rampa até 500 usuários em 2 minutos, mantida por 1 minuto, foi modelada**

| Grupo | Threads | Ramp-up | Duração | Papel |
|---|---|---|---|---|
| Base fixa | 10 | 0 s | 180 s | 10 usuários desde o início |
| Rampa | 490 | 120 s | 180 s | +490 usuários ao longo de 2 min |

Somados: 10 → 500 usuários em 120 s, e 500 usuários mantidos até 180 s (a duração do grupo **inclui** o ramp-up: 120 s + 60 s). Medido na execução: 56 threads aos ~10 s, 260 aos ~60 s e 500 a partir de ~119 s; o teste durou 179,9 s.

Sampler: `POST /api/orders` com *HTTP Header Manager* (`Content-Type: application/json`) e corpo

```json
{"product": "Produto ${__threadNum}", "qty": ${__Random(1,5)}}
```

Assertion: Response Code = 201. *Uniform Random Timer* de 100 a 300 ms (think time).

**Como executar**

```bash
rm -rf q16.jtl relatorios/q16
jmeter -n -t Q16-Estresse.jmx -l q16.jtl -e -o relatorios/q16     # ~3 minutos
```

**Resultado**

| Label | Amostras | Erro % | Tempo médio | 90th pct | 95th pct | Máx | Vazão |
|---|---|---|---|---|---|---|---|
| `POST /api/orders` | 301.164 | **0,00%** | **1,8 ms** | 6 ms | 8 ms | 39 ms | 1.674/s |

**Interpretação.** Taxa de erro de 0% e tempo médio de 1,8 ms durante toda a rampa. **O ponto de quebra não foi atingido com 500 usuários**: ele é maior que isso. Para localizá-lo seria preciso subir a carga (mais threads ou menos think time) até o erro ou o tempo começarem a crescer em *Response Times Over Time*.

## 4. Q17 — Cenário de negócio (`Q17-Cenario.jmx`)

**Configuração.** Thread Group com 50 threads, ramp-up 5 s, Loop Infinite, duração 60 s. Sampler `POST /api/process` com o Header Manager JSON e corpo

```json
{"orderId": "${__threadNum}-${__UUID()}", "qty": 2}
```

Assertions: Response Code = 200; JSON Assertion `$.status == processed`; JSON Assertion `$.input.qty == 2` (pega o corpo que não chegou: sem o Header Manager a API responde 200 com `input` vazio). *Uniform Random Timer* de 200 a 500 ms.

**Como executar**

```bash
rm -rf q17.jtl relatorios/q17
jmeter -n -t Q17-Cenario.jmx -l q17.jtl -e -o relatorios/q17
```

**Resultado**

| Label | Amostras | Erro % | Média | 90th pct | 95th pct | Máx | Vazão |
|---|---|---|---|---|---|---|---|
| `POST /api/process` | 7.155 | 0,00% | 50,4 ms | 51 ms | 51 ms | 69 ms | 120/s |

**Interpretação.** A média de ~50 ms vem da própria API, que simula 50 ms de processamento (`setTimeout`). Nenhuma JSON Assertion falhou: todas as respostas vieram com `status == processed` e com o `qty` enviado.

### Extra — fluxo `ping → orders → process` (`Q17-Fluxo-Extra.jmx`)

20 threads, 60 s, *Transaction Controller* `Fluxo completo do pedido`, JSON Extractor `orderId ← $.id` no passo 2 e JSON Assertion (regex `^ORD-\d+$`) em `$.input.orderId` no passo 3.

| Etapa | Amostras | Erro % | Média | 90th pct | Máx |
|---|---|---|---|---|---|
| `1 GET /api/ping` | 379 | 0% | 0,5 ms | 1 ms | 21 ms |
| `2 POST /api/orders` | 375 | 0% | 0,6 ms | 1 ms | 8 ms |
| `3 POST /api/process` | 367 | 0% | 50,7 ms | 51 ms | 52 ms |
| **Fluxo completo** | 375 | 0% | 50,7 ms | 53 ms | 75 ms |

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
| Q15, Q16 e Q17 no mesmo `simuladoA1.jmx` rodando juntas | marcar *Run Thread Groups consecutively* no Test Plan |
