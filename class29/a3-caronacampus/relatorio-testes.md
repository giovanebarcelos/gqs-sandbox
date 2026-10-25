# Relatório de Testes — CaronaCampus v1.0.0

> Estrutura do Slide 14 da Aula 29: escopo, execução, cobertura, defeitos, métricas e **conclusão**. Todos os números abaixo vêm de execuções reais (comandos na seção 8). Nada foi estimado.

## 1. Escopo executado

Foi executado tudo o que o `plano-de-teste.md` prevê: os 16 casos de teste (`casos-de-teste.md`), nos níveis unitário, integração e API, mais o teste de mutação e a medição do RNF-01.

**Não foi executado (e por quê):**

| Item | Motivo |
|---|---|
| Workflow no GitHub Actions | O repositório de referência vive numa subpasta de outro repositório, e o Actions só lê `.github/workflows` da raiz. Os **mesmos comandos** do workflow foram executados à mão (seção 8). **O seu grupo precisa de uma execução verde na aba Actions** |
| Desempenho sob carga | Fora do escopo do plano (só o RNF-01 foi medido, em processo, sem rede) |
| Expiração de token, força bruta, OWASP | Fora do escopo; risco R-04 aceito (seção 6) |
| SonarQube / Quality Gate | Opcional na A3; não configurado neste exemplo |

## 2. Resumo da execução

| Nível | Executados | Aprovados | Reprovados | Bloqueados | Tempo |
|---|---:|---:|---:|---:|---|
| Unitários (`tests/unitarios/`) | 105 | 105 | 0 | 0 | — |
| Integração (`tests/integracao/`) | 67 | 67 | 0 | 0 | — |
| **pytest (total)** | **172** | **172** | **0** | **0** | 3,4 s local · 10,6 s no contêiner (com cobertura) |
| API (Newman): requests | 58 | 58 | 0 | 0 | 1,3 s |
| API (Newman): asserções | 95 | 95 | 0 | 0 | — |
| **Casos de teste (16)** | **16** | **16** | **0** | **0** | — |

Taxa de aprovação: 100%. A taxa de reprovação **na última execução** é 0%, mas a suíte já reprovou antes: os defeitos da seção 4 foram achados por testes vermelhos.

### Rastreabilidade: caso de teste × testes automatizados

Gerada por `scripts/matriz_rastreabilidade.py` (que falha se um caso não tem teste, ou se um teste cita um caso que não existe):

| Caso | Requisitos | Testes pytest | Requests Postman |
|---|---|---:|---:|
| CT-01 | RF-01, RF-02 | 10 | 2 |
| CT-02 | RN-01 | 3 | 2 |
| CT-03 | RN-02 | 2 | 2 |
| CT-04 | RN-03, RNF-03 | 3 | 3 |
| CT-05 | RF-03 | 3 | 1 |
| CT-06 | RN-01, RNF-05 | 7 | 3 |
| CT-07 | RF-03 | 6 | 2 |
| CT-08 | RN-04 | 4 | 1 |
| CT-09 | RN-04, RNF-03 | 5 | 3 |
| CT-10 | RN-04, RF-04 | 5 | 2 |
| CT-11 | RF-05 | 8 | 15 |
| CT-12 | RN-05, RF-06 | 8 | 7 |
| CT-13 | RN-05, RF-01 | 5 | 1 |
| CT-14 | RF-02 | 11 | 5 |
| CT-15 | RF-08, RNF-02 | 9 | 6 |
| CT-16 | RF-07 | 4 | 1 |

Um caso é aprovado quando **todos** os seus testes (pytest e Postman) passam.

## 3. Cobertura

| Arquivo | Instruções | Ramos | Cobertura |
|---|---:|---:|---:|
| `api.py` | 117 | 12 | 100% |
| `app.py` | 13 | 4 | 100% |
| `dominio.py` | 92 | 20 | 100% |
| `repositorio.py` | 99 | 2 | 100% |
| `servico.py` | 112 | 56 | 100% |
| **Total** | **433** | **94** | **100%** (linha e ramo) |

**O que NÃO está nos números:** `app.main()` está excluída da cobertura (`# pragma: no cover`): ela só sobe o servidor, e foi exercitada pelo `docker compose up` e pelo Newman. Os métodos de `Protocol` (`...`) também são excluídos.

**100% de cobertura não é 100% de qualidade.** A cobertura diz quais linhas rodaram, não se os testes **percebem** um defeito nelas. Por isso a seção 5.

## 4. Defeitos encontrados

### 4.1 Defeitos do produto

| ID | Descrição | Severidade | Prioridade | Estado | Como foi achado | Solução |
|---|---|---|---|---|---|---|
| **DEF-001** | Reservas simultâneas ultrapassam as vagas: o serviço lia a contagem e gravava em seguida, sem transação. Com 6 threads disputando **1 vaga**, foram aceitas 1, 6, 5, 6 e 6 reservas em 5 execuções | **Alta** (viola RN-01 e o RNF-05) | Alta | Corrigido | Teste CT-06 de concorrência (vermelho nas 5 execuções do pytest) | `reservar_se_ha_vaga`: contagem e gravação na mesma transação (`BEGIN IMMEDIATE`) e índice único parcial contra reserva duplicada. 200 execuções seguidas sem falha |
| **DEF-002** | Conexões SQLite nunca fechadas: `with sqlite3.connect()` só confirma a transação. Em servidor de longa duração vaza descritores de arquivo | Média | Média | Corrigido | `ResourceWarning: unclosed database` aos montes ao rodar a suíte (884 avisos na execução em que foi notado) | `contextmanager` que confirma, desfaz e **fecha**; `filterwarnings = error` no `pytest.ini` para nunca mais passar despercebido |

**Passos de reprodução do DEF-001** (antes da correção): criar viagem de 1 vaga; 6 passageiros chamam `POST /viagens/{id}/reservas` ao mesmo tempo; contar `reservas ativas > 1`. Reprodução automatizada em `tests/integracao/test_concorrencia.py`.

| **DEF-003** | A **alteração** de viagem (`PUT`) aceitava `origem` e `destino` vazios ou só com espaços (e não removia os espaços das pontas): só a criação validava | Baixa | Média | Corrigido | **Revisão comparativa ao portar o sistema para Java**: o serviço Java validava, o Python não. Nenhum teste cobria o caminho. 4 testes novos falham no código antigo | `atualizar_viagem` passou a aplicar `strip()` e a exigir origem e destino não vazios; mutante M21 protege |

Observação: o teste falhou nas 5 execuções do pytest, mas num script avulso com as mesmas 6 threads uma das 5 rodadas teve sorte (só 1 reserva aceita) — **um teste de concorrência é probabilístico**, e isso motivou o DEF-T4.

### 4.2 Falhas e lacunas dos próprios testes

Também são achados de qualidade: um teste errado reprova código certo, e um teste fraco aprova código errado.

| ID | Descrição | Tipo | Estado | Solução |
|---|---|---|---|---|
| **DEF-T1** | Expectativa errada: `10,01 ÷ (4+1)` esperado como 2,51 (é 2,002 → **2,01**) | Erro no teste | Corrigido | Conta refeita à mão; o código estava certo |
| **DEF-T2** | Linha da suspensão: 6 avaliações `[4,4,4,1,1,1]` (média 2,5) marcadas como "não suspende" | Erro no teste | Corrigido | Valor esperado passou a `True` |
| **DEF-T3** | Teste de suspensão pela API avançava o relógio do servidor, mas o relógio do teste não andava: as viagens seguintes nasciam no passado e davam 400 | Erro no teste | Corrigido | Os dois relógios passaram a ser o mesmo objeto |
| **DEF-T4** | Mutante M17 (`>=` → `>` na lotação do repositório) **sobreviveu** em uma rodada de mutação: o teste de concorrência é probabilístico | Lacuna de teste | Corrigido | Teste determinístico direto no repositório; M17 morre sempre |

### 4.3 Densidade

- Defeitos do produto: **3** em 593 linhas não vazias de código-fonte → **5,1 defeitos/KLOC**
- Defeitos de teste: 4 em 677 linhas de teste → 5,9 por KLOC de teste
- Relação teste/código: 677 / 593 = **1,14**

Dos 3 defeitos do produto, 2 foram achados **por testes** (DEF-001, DEF-002) e 1 por **revisão comparativa** (DEF-003); nenhum chegou à "produção".

## 5. Qualidade dos testes: teste de mutação

`scripts/mutacao.py` aplica **um mutante por vez** (um operador trocado, uma constante alterada, uma verificação removida) e roda a suíte. Mutante que a suíte não percebe **sobrevive**.

| # | Arquivo | Mutação | Resultado | Primeiro teste que falhou |
|---|---|---|---|---|
| M01 | `dominio.py` | limite de 60 min: >= vira > | detectado | `test_repositorio_sqlite.py::test_ct08_ct09_taxa_no_limite_de_60_minutos` |
| M02 | `dominio.py` | cancela na hora exata da saída | detectado | `test_repositorio_sqlite.py::test_ct10_apos_a_saida_nao_cancela_e_a_reserva_continua_ativa` |
| M03 | `dominio.py` | preço arredonda para baixo | detectado | `test_api.py::test_ct01_criar_e_consultar_viagem` |
| M04 | `dominio.py` | esquece de contar o motorista | detectado | `test_api.py::test_ct01_criar_e_consultar_viagem` |
| M05 | `dominio.py` | máximo de vagas 6 -> 7 | detectado | `test_api.py::test_ct01_dados_invalidos_devolvem_400_com_erro_padronizado` |
| M06 | `dominio.py` | antecedência de 30 min exclusiva | detectado | `test_regras.py::test_ct03_saida_nos_limites_inclusivos_e_aceita` |
| M07 | `dominio.py` | janela de 14 dias exclusiva | detectado | `test_regras.py::test_ct03_saida_nos_limites_inclusivos_e_aceita` |
| M08 | `dominio.py` | média exata 3,0 suspende | detectado | `test_repositorio_sqlite.py::test_ct12_ct13_media_exatamente_3_nao_suspende` |
| M09 | `dominio.py` | só suspende com 6 avaliações | detectado | `test_api.py::test_ct12_reputacao_e_ct13_suspensao_ponta_a_ponta` |
| M10 | `dominio.py` | taxa de 50% -> 40% | detectado | `test_api.py::test_ct08_ct09_cancelar_reserva_com_e_sem_taxa_pela_api` |
| M11 | `dominio.py` | teto de R$ 25 -> 26 | detectado | `test_regras.py::test_ct04_preco_por_vaga` |
| M12 | `dominio.py` | nota 1 vira inválida | detectado | `test_api.py::test_ct12_reputacao_e_ct13_suspensao_ponta_a_ponta` |
| M13 | `servico.py` | lotação na checagem do serviço | detectado | `test_servico_com_dubles.py::test_ct06_ultima_vaga_e_aceita_e_a_seguinte_e_recusada` |
| M14 | `servico.py` | motorista reserva a própria viagem | detectado | `test_api.py::test_ct14_so_o_motorista_altera_e_viagem_com_reserva_e_imutavel` |
| M15 | `servico.py` | avalia antes da saída | detectado | `test_api.py::test_ct11_avaliar_exige_ter_viajado` |
| M16 | `servico.py` | qualquer um cancela reserva alheia | detectado | `test_api.py::test_ct10_cancelar_reserva_de_outro_ou_inexistente` |
| M17 | `repositorio.py` | lotação dentro da transação | detectado | `test_concorrencia.py::test_ct06_duas_reservas_simultaneas_para_a_ultima_vaga_so_uma_vence` |
| M18 | `repositorio.py` | esquece de fechar a conexão | detectado | `test_api.py::test_ct15_login_invalido_devolve_401` |
| M19 | `api.py` | conflito devolve 400 | detectado | `test_api.py::test_ct14_so_o_motorista_altera_e_viagem_com_reserva_e_imutavel` |
| M20 | `api.py` | autenticação desligada | detectado | `test_api.py::test_ct15_todo_endpoint_protegido_exige_token` |
| M21 | `servico.py` | alteração aceita origem/destino vazios | detectado | `test_api.py::test_ct14_alteracao_com_origem_ou_destino_vazios_e_recusada` |

**21 mutantes, 21 detectados (100%)**, em 49 s.

### 5.1 E a coleção Postman, detecta?

O mesmo exercício, dessa vez rodando o Newman contra a API mutante (`docker compose --profile api up ...`):

| # | Mutação | Newman |
|---|---|---|
| M01 | limite de 60 min: >= vira > | **sobreviveu** |
| M02 | cancela na hora exata da saída | **sobreviveu** |
| M04 | esquece de contar o motorista | detectado |
| M08 | média exata 3,0 suspende | **sobreviveu** |
| M09 | só suspende com 6 avaliações | detectado |
| M10 | taxa de 50% -> 40% | detectado |
| M11 | teto de R$ 25 -> 26 | detectado |
| M19 | conflito devolve 400 | detectado |
| M20 | autenticação desligada | detectado |

**Achado:** o Newman detecta 6 de 9 mutantes. Os três que sobrevivem (M01, M02, M08) são **valores-limite exatos** (exatamente 60 min, exatamente a hora da saída, média exatamente 3,0). Pela API com relógio real é impraticável acertar o segundo exato. Isso confirma a estratégia do plano: **limites vivem nos testes unitários e de integração com relógio injetado; a coleção cobre partições e contrato**. Cada nível pega o que o outro não consegue.

## 6. Métricas

| Métrica | Valor |
|---|---|
| Requisitos atendidos | 8/8 RF · 5/5 RN · 5/5 RNF (RNF-01 medido: seção 6.1) |
| Casos aprovados | 16/16 (100%) |
| Cobertura de instrução / de ramo | 100% / 100% |
| Mutantes detectados | 21/21 (pytest) · 6/9 (Newman, amostra) |
| Defeitos do produto: encontrados / corrigidos / abertos | 3 / 3 / 0 |
| Defeitos de teste: encontrados / corrigidos | 4 / 4 |
| Densidade de defeitos do produto | 5,1 /KLOC |
| Tempo da suíte pytest (local / contêiner) | 3,4 s / 10,6 s |
| Tempo do Newman | 1,3 s |

### 6.1 RNF-01 (desempenho)

`scripts/medir_desempenho.py`: `GET /viagens` com 1.000 viagens, 200 repetições, em processo, sobre SQLite: **mediana 5,4 ms · p95 6,3 ms · máximo 19,5 ms**. Limite do requisito: 300 ms (p95) → **atendido**. A medida não inclui rede nem servidor de produção; vale para comparar versões, não como promessa de latência.

### 6.2 Riscos residuais

| Risco | Estado |
|---|---|
| R-01 instabilidade por relógio | Mitigado: relógio injetado |
| R-02 corrida na última vaga | Mitigado e verificado (200 execuções) |
| R-03 cobertura alta com testes fracos | Mitigado: 21 mutantes, 100% detectados |
| R-04 segurança fora do escopo | **Aceito**: tokens não expiram; o hash de senha usa sal fixo (suficiente para a demonstração, não para produção) |
| R-05 dublê diferente do SQLite | Mitigado: os mesmos cenários rodam nos dois |

## 7. Conclusão

**O sistema está apto a ser liberado como versão de demonstração (v1.0.0), com duas ressalvas.**

1. Os 16 casos passam, a cobertura é de 100% e os 21 mutantes são detectados. Os três defeitos do produto (corrida na última vaga, vazamento de conexões e alteração sem validação) foram corrigidos; nenhum está aberto.
2. **Ressalvas para um uso real:** (a) a autenticação é simples demais (token sem expiração, sal de senha fixo) e deve ser trocada antes de qualquer dado real; (b) a coleção Postman **não** prova os valores-limite de tempo, que dependem dos testes unitários: não removam esses testes achando que a API os cobre. Além disso, o workflow ainda precisa de uma execução verde no GitHub.

**Recomendação:** liberar para o piloto no campus com a lista de ressalvas acima; tratar a autenticação como o primeiro item da próxima versão.

## 8. Como reproduzir cada número

| Número | Comando |
|---|---|
| 172 testes e 100% de cobertura | `docker compose run --rm testes` |
| 58 requests / 95 asserções | `docker compose --profile api up --build --abort-on-container-exit --exit-code-from newman` |
| 21 mutantes | `python scripts/mutacao.py` (pytest instalado) |
| Matriz de rastreabilidade | `python scripts/matriz_rastreabilidade.py` |
| RNF-01 | `docker compose run --rm testes python scripts/medir_desempenho.py` |
| Mutantes na coleção Newman | aplicar o mutante de `scripts/mutacao.py` numa cópia e rodar o comando do Newman acima (a tag `carona-campus:1.0.0` é compartilhada: **reconstruam a imagem depois**, ou a próxima execução usa o mutante) |

Ambiente das medições: Linux, Docker 29, Compose 5, `python:3.12-slim`, pytest 9.1, pytest-cov 7.1, Flask 3.1, Newman (imagem `postman/newman`).
