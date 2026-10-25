# CaronaCampus

API de **caronas solidárias entre alunos do mesmo campus**: motoristas oferecem vagas, passageiros reservam, o preço é dividido, há taxa para cancelamento em cima da hora e motoristas mal avaliados são suspensos.

> **Projeto de referência da A3 de Garantia da Qualidade de Software.** O sistema é pequeno de propósito: o que se mostra aqui é o **plano de qualidade** em volta dele (plano, casos, testes automatizados, testes de API, pipeline, mutação e relatório). Use como guia, não como modelo para copiar. O grupo, as sessões com o agente e as datas do histórico são fictícios; **os testes, as medições e os defeitos descritos foram executados de verdade**.

## Como rodar (só precisa do Docker)

```bash
docker compose up --build        # sobe a API em http://localhost:5000
docker compose run --rm testes   # 172 testes pytest + cobertura (falha abaixo de 70%)
docker compose --profile api up --build --abort-on-container-exit --exit-code-from newman
                                 # coleção Postman (58 requests, 95 asserções) contra a API em modo de teste
```

Usuários fictícios de demonstração: `2026001`/`ana123`, `2026002`/`bruno123`, `2026003`/`carla123`.

```bash
TOKEN=$(curl -s localhost:5000/auth/login -H 'Content-Type: application/json' \
        -d '{"matricula":"2026001","senha":"ana123"}' | python3 -c 'import sys,json; print(json.load(sys.stdin)["token"])')
curl -s localhost:5000/viagens -H "Authorization: Bearer $TOKEN" \
     -H 'Content-Type: application/json' \
     -d '{"origem":"Campus","destino":"Centro","vagas":3,"custo_total":"12.00","saida":"2099-01-01T12:00:00"}'
```

Sem Docker: `python -m venv .venv && . .venv/bin/activate && pip install -r requirements.txt && PYTHONPATH=src pytest --cov`.

## Regras de negócio

| ID | Regra |
|---|---|
| RN-01 | 1 a 6 vagas por viagem; não se reserva além da lotação |
| RN-02 | Saída entre 30 min e 14 dias à frente |
| RN-03 | Preço por vaga = custo ÷ (vagas + 1), arredondado para cima, entre R$ 2,00 e R$ 25,00 |
| RN-04 | Cancelar com ≥ 60 min de antecedência: sem taxa; com menos: 50%; depois da saída: proibido |
| RN-05 | Com 5+ avaliações e média < 3,0, o motorista é suspenso (não cria viagens) |

## Estrutura

```
carona-campus/
├── README.md · AGENTS.md · opencode.json · CHANGELOG.md
├── plano-de-teste.md          # escopo, estratégia, critérios, riscos
├── casos-de-teste.md          # 16 casos com técnica e requisito
├── relatorio-testes.md        # resultados, métricas, defeitos, conclusão
├── src/carona/                # dominio.py (regras) · servico.py · repositorio.py · api.py · app.py
├── tests/unitarios/           # regras puras e serviço com dublês (Mock)
├── tests/integracao/          # serviço+SQLite, API Flask, concorrência
├── postman/                   # coleção + environment (Newman)
├── scripts/                   # mutacao.py · matriz_rastreabilidade.py · medir_desempenho.py
├── .github/workflows/ci.yml   # pipeline
├── Dockerfile · docker-compose.yml
└── requirements.txt · pytest.ini · .coveragerc
```

## Resultados (resumo — detalhes em `relatorio-testes.md`)

| Medida | Resultado |
|---|---|
| Casos de teste | 16 · todos automatizados e aprovados |
| Testes pytest | 172 (105 unitários, 67 de integração) · 0 falhas |
| Cobertura | 100% de instrução e de ramo (433 instruções, 94 ramos) |
| Testes de API | 58 requests · 95 asserções · 0 falhas |
| Mutação manual | 21 mutantes · 21 detectados |
| Defeitos do produto | 3 (todos corrigidos) · 4 falhas de teste/lacunas corrigidas |
| Desempenho (RNF-01) | `GET /viagens` com 1.000 viagens: p95 de 6,3 ms (limite: 300 ms) |

## Uso de IA

**Ferramenta e modelo:** OpenCode + Nemotron 3 Ultra (`:free`) via OpenRouter, na configuração da Aula 01B.
**Skills:** `grilling`, `to-spec`, `tdd`, `caveman-review`.
**Configuração versionada:** `opencode.json` (push negado; comandos com `ask`) e `AGENTS.md`.

> **Nota do exemplo:** os registros 2, 3, 4, 8 e 9 descrevem erros que **de fato ocorreram** na construção deste exemplo e foram achados por testes ou por revisão (DEF-001, DEF-002, DEF-003 e as falhas de teste do relatório). Os registros 1, 5, 6 e 7 são ilustrativos do formato.

### Registros (9 registros, 6 tipos de artefato)

| # | Artefato | Pedido (resumo) | O que o agente errou ou omitiu | O que o grupo fez |
|---|---|---|---|---|
| 1 | `plano-de-teste.md` | `grilling` + `to-spec` sobre escopo e riscos | Listou só o que seria testado; **não** disse o que ficou fora nem deu mitigação aos riscos | Acrescentou a tabela "Fora do escopo" e a coluna de mitigação |
| 2 | `servico.py` (reserva) | Skill `tdd` para a regra de lotação (RN-01) | Implementou "contar as reservas e depois gravar" e afirmou que a lotação estava garantida; o teste com 6 threads aceitou **até 6 reservas para 1 vaga** (DEF-001) | Moveu contagem e gravação para a mesma transação (`BEGIN IMMEDIATE`) no repositório e criou índice único; repetiu o teste 200 vezes sem falha |
| 3 | `repositorio.py` | Adaptador SQLite | Usou `with sqlite3.connect()` achando que fechava a conexão; ela só confirma a transação (DEF-002). Centenas de `ResourceWarning` | Criou um `contextmanager` que fecha; `filterwarnings = error` no `pytest.ini`; mutante M18 protege |
| 4 | `tests/unitarios/test_regras.py` | Valores-limite do preço (RN-03) e da suspensão (RN-05) | Calculou `10,01 ÷ (4+1)` como 2,5025 (é 2,002 → 2,01) e marcou "não suspende" para 6 avaliações com média 2,5 | Refez as contas à mão, conferiu linha por linha; os dois testes erravam, o código estava certo |
| 5 | `postman/` | Traduzir os casos em requests | Gerou requests só com `status 200`, sem asserções sobre o corpo, e com `localhost` fixo | Pediu asserções por campo e `{{base_url}}`; quebrou a API de propósito (mutantes) e viu o Newman falhar |
| 6 | `.github/workflows/ci.yml` | Rascunho com build, teste e cobertura | Colocou `continue-on-error: true` no passo de testes | Removeu; quebrou um teste, fez push e viu o job vermelho |
| 7 | `relatorio-testes.md` | Rascunho da conclusão | Concluiu "100% de cobertura, portanto sem defeitos" | Reescreveu a conclusão com mutação, defeitos e risco residual |
| 8 | `tests/integracao/` | Revisar se a lotação estava protegida | Disse que o teste de concorrência bastava; numa rodada de mutação o mutante M17 (`>=` → `>` no repositório) **sobreviveu**: o teste é probabilístico | Acrescentou teste determinístico direto no repositório; M17 passou a morrer sempre |
| 9 | `servico.py` (alterar viagem) | Caso de uso de alteração (`PUT`) | Validou origem e destino só na **criação**; na alteração aceitava vazios (DEF-003). Nenhum teste cobria o caminho | Achado ao portar o sistema para Java (a versão Java validava); 4 testes novos falham no código antigo; mutante M21 protege |

**Testes escritos pelo agente que o grupo quebrou de propósito:** todos os de regra (RN-01 a RN-05), pelos 21 mutantes de `scripts/mutacao.py`.

### Reflexão do grupo

- **Onde a IA mais ajudou:** gerar a grade de valores-limite e a coleção Postman — trabalho repetitivo que o grupo só revisou.
- **Onde mais errou:** concorrência e recursos (itens 2 e 3), **aritmética** nos valores esperados (item 4) e uma validação esquecida num caminho que ninguém testou (item 9). Em nenhum caso o erro apareceu sem execução ou revisão.
- **O que faria diferente:** pedir o teste de concorrência **antes** do código da reserva, e rodar a mutação mais cedo.
- **Regra que ficou no `AGENTS.md`:** "teste novo só vale se falhar quando o código é quebrado de propósito".

### Defesa oral

Todos os integrantes sabem explicar qualquer caso, asserção e etapa do workflow — escritos por nós ou pelo agente.

## Licença

Uso didático.
