# class20B — JMeter na Prática: Tema 4 da A1

Material da **Aula 20B**. Os seis planos `.jmx` foram executados com **JMeter 5.6.3** contra as APIs Node da prova e do simulado (`A1/`). Os resultados medidos estão em `GQS20B-JMeterTests-Modelo.md`.

## Como executar

```bash
# Terminal 1 — UMA API na porta 5000 (Node e Flask usam a mesma porta)
cd A1/Prova/WorkSpace/3-Bruno/api-node && npm install && node index.js      # prova
# ou: cd A1/Simulado/WorkSpace/3-Bruno/api-node && npm install && node index.js   # simulado

# Terminal 2 — a partir de repository/class20B/jmeter
./GQS20B-run.sh prova/Q15-Carga.jmx
./GQS20B-run.sh prova/Q16-Estresse.jmx        # ~3 min
./GQS20B-run.sh prova/Q17-Cenario.jmx
./GQS20B-run.sh simulado/Q15-Carga.jmx 3000   # 2º argumento: porta (padrão 5000)
```

O script apaga os resultados anteriores, roda `jmeter -n -t … -l … -e -o …` e imprime o caminho do relatório (`resultados/<pasta>/relatorio/index.html`).

Os planos aceitam `-Jhost=` e `-Jporta=` (variáveis `${__P(host,localhost)}` e `${__P(porta,5000)}`).

## Conteúdo

| Plano | Questão | Configuração |
|---|---|---|
| `jmeter/prova/Q15-Carga.jmx` | Q15 da prova | `GET /api/books`, 50 threads, 30 s; Response Code 200 + Duration 300 ms |
| `jmeter/prova/Q16-Estresse.jmx` | Q16 da prova | `POST /api/books`, 10 → 200 threads em 2 min + 1 min (dois grupos); alternativa de 1 grupo desabilitada |
| `jmeter/prova/Q17-Cenario.jmx` | Q17 da prova | GET books → POST loans → POST calculate-fine; 20 threads, 60 s; JSON Extractor |
| `jmeter/simulado/Q15-Carga.jmx` | Q15 do simulado | `GET /api/ping`, 100 threads, 60 s; Duration 200 ms |
| `jmeter/simulado/Q16-Estresse.jmx` | Q16 do simulado | `POST /api/orders`, 10 → 500 threads em 2 min + 1 min |
| `jmeter/simulado/Q17-Cenario.jmx` | Q17 do simulado | `POST /api/process`, 50 threads, 60 s; JSON Assertions `status == processed` e `input.qty == 2` |

Não testado: a abertura dos planos na GUI do JMeter (os planos foram executados pela CLI).
