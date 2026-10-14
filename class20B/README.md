# class20B — JMeter na Prática: Tema 4 do Simulado da A1

Material da **Aula 20B**, cujo passo a passo é a correção (gabarito comentado) do Tema 4 do **Simulado da A1**. Os cinco planos `.jmx` foram executados com **JMeter 5.6.3** contra a API Node do simulado (`A1/Simulado/WorkSpace/3-Bruno/api-node`). Os resultados medidos estão em `GQS20B-JMeterTests-Modelo.md`.

## Como executar

```bash
# Terminal 1 — UMA API na porta 5000 (Node e Flask usam a mesma porta)
cd A1/Simulado/WorkSpace/3-Bruno/api-node && npm install && node index.js

# Terminal 2 — a partir de repository/class20B/jmeter
./GQS20B-run.sh simulado/simuladoA1.jmx       # entregável completo, ~5 min
./GQS20B-run.sh simulado/Q15-Carga.jmx
./GQS20B-run.sh simulado/Q16-Estresse.jmx        # ~3 min
./GQS20B-run.sh simulado/Q17-Cenario.jmx
./GQS20B-run.sh simulado/Q17-Fluxo-Extra.jmx
./GQS20B-run.sh simulado/Q15-Carga.jmx 3000      # 2º argumento: porta (padrão 5000)
```

O script apaga os resultados anteriores, roda `jmeter -n -t … -l … -e -o …` e imprime o caminho do relatório (`resultados/<pasta>/relatorio/index.html`).

Os planos aceitam `-Jhost=` e `-Jporta=` (variáveis `${__P(host,localhost)}` e `${__P(porta,5000)}`).

## Conteúdo

| Plano | Questão | Configuração |
|---|---|---|
| `jmeter/simulado/simuladoA1.jmx` | **Entregável** | Q15, Q16 (um grupo de 500, ramp-up 120 s, 180 s) e Q17 em um arquivo, *Run Thread Groups consecutively*; ~5 min |  
| `jmeter/simulado/Q15-Carga.jmx` | Q15 do simulado | `GET /api/ping`, 100 threads, 60 s; Response Code 200 + Duration 200 ms |
| `jmeter/simulado/Q16-Estresse.jmx` | Q16 do simulado | `POST /api/orders`, 10 → 500 threads em 2 min + 1 min (dois grupos); alternativa de 1 grupo desabilitada |
| `jmeter/simulado/Q17-Cenario.jmx` | Q17 do simulado | `POST /api/process`, 50 threads, 60 s; JSON Assertions `status == processed` e `input.qty == 2` |
| `jmeter/simulado/Q17-Fluxo-Extra.jmx` | Extra | `GET /api/ping` → `POST /api/orders` (JSON Extractor do `id`) → `POST /api/process`; Transaction Controller, 20 threads, 60 s |

Não testado: a abertura dos planos na GUI do JMeter (os planos foram executados pela CLI).
