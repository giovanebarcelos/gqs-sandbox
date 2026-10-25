# Relatório de Testes — CaronaCampus v1.0.0 (versão Java)

> Estrutura do Slide 14 da Aula 29: escopo, execução, cobertura, defeitos, métricas e **conclusão**. Todos os números vêm de execuções reais (comandos na seção 8). A versão Python (`../a3-caronacampus`) tem o relatório mais completo; aqui estão os pontos que **mudam** com a pilha Java.

## 1. Escopo executado

Os 16 casos de `casos-de-teste.md` nos níveis unitário, integração e API, mais o teste de mutação. **Mesma coleção Postman** da versão Python, contra a API em Java.

**Não foi executado:** pipeline no GitHub Actions (não incluído nesta versão); medição do RNF-01 (feita na versão Python, mesma API e mesmo banco); carga e segurança avançada (fora do escopo, como no plano).

## 2. Resumo da execução

| Nível | Executados | Aprovados | Reprovados | Tempo |
|---|---:|---:|---:|---|
| Unitários (`RegrasTest` 48, `ServicoComDublesTest` 32, `ArquiteturaTest` 3) | 83 | 83 | 0 | ≈ 1 s |
| Integração (`ApiTest` 42, `ServicoSqliteTest` 17, `RepositorioSqliteTest` 6, `ConcorrenciaTest` 1) | 66 | 66 | 0 | ≈ 15 s |
| **JUnit (total)** | **149** | **149** | **0** | **≈ 20 s** no contêiner (`mvn verify`, com JaCoCo) |
| API (Newman): requests / asserções | 58 / 95 | 58 / 95 | 0 | 3,7 s |

O `ApiTest` é o mais lento (≈ 12 s): cada teste sobe o servidor e cria 3 usuários com PBKDF2 (50.000 iterações, lento de propósito).

### Rastreabilidade: caso de teste × testes automatizados

| Caso | Requisitos | Testes JUnit | Requests Postman |
|---|---|---:|---:|
| CT-01 | RF-01, RF-02 | 11 | 2 |
| CT-02 | RN-01 | 2 | 2 |
| CT-03 | RN-02 | 2 | 2 |
| CT-04 | RN-03, RNF-03 | 3 | 3 |
| CT-05 | RF-03 | 3 | 1 |
| CT-06 | RN-01, RNF-05 | 6 | 3 |
| CT-07 | RF-03 | 8 | 2 |
| CT-08 | RN-04 | 4 | 1 |
| CT-09 | RN-04, RNF-03 | 3 | 3 |
| CT-10 | RN-04, RF-04 | 5 | 2 |
| CT-11 | RF-05 | 8 | 15 |
| CT-12 | RN-05, RF-06 | 8 | 7 |
| CT-13 | RN-05, RF-01 | 2 | 1 |
| CT-14 | RF-02 | 11 | 5 |
| CT-15 | RF-08, RNF-02 | 5 | 6 |
| CT-16 | RF-07 | 4 | 1 |

## 3. Cobertura (JaCoCo)

| Medida | Cobertura |
|---|---:|
| Instruções | **95,3%** (2.776 de 2.914) |
| Ramos | **88,1%** (208 de 236) |
| Linhas | **92,6%** (540 de 583) |

**O que está nos 5%:** `App.main` (sobe o servidor; exercitada pelo `docker compose up` e pelo Newman), os `catch (SQLException)` de `RepositorioSqlite` (só dispararia com o banco falhando) e o ramo `500` de `Api`. O limite do `pom.xml` é 70% e o `mvn verify` falha abaixo dele.

## 4. Defeitos encontrados

### 4.1 Do produto

Nenhum **defeito do produto** foi achado pelos testes nesta versão, e isso precisa de explicação: o Java foi escrito **depois** do Python, já com as três lições da versão anterior (DEF-001 corrida na última vaga, DEF-002 conexões não fechadas, DEF-003 alteração sem validação). Os testes equivalentes foram escritos para **provar** que as correções valem também aqui: o teste de 6 threads (CT-06), o determinístico no repositório e os de origem/destino vazios (CT-14) passam, e os mutantes M17 (lotação no repositório) e M18 (índice único) são detectados.

> Isso **não** significa "código sem defeito": significa que os defeitos conhecidos foram evitados. Defeito desconhecido só aparece com mais teste, revisão ou uso.

### 4.2 Dos testes (achados durante a construção)

| ID | Descrição | Tipo | Solução |
|---|---|---|---|
| **DEF-J1** | `when(...any(), anyInt()).thenReturn(...)` executou o stub anterior com `null` e deu `NullPointerException` | Erro no teste (armadilha do Mockito) | `doReturn(...).when(repo)...` |
| **DEF-J2** | O guarda de arquitetura achou `LocalDateTime.now()` dentro de um Javadoc e reprovou código certo | Falso positivo | Remover comentários antes de procurar |
| **DEF-J3** | O repositório lançava exceções de tipos diferentes para o mesmo tipo de falha de restrição | Inconsistência de design, achada pelo teste | Tratamento único |

## 5. Qualidade dos testes: teste de mutação

`scripts/mutacao.sh` aplica **um mutante por vez** numa cópia do projeto (dentro do contêiner) e roda `mvn test` até a primeira falha.

| # | Arquivo | Mutação | Resultado | Primeiro teste que falhou |
|---|---|---|---|---|
| M01 | `Regras.java` | limite de 60 min: >= vira > | detectado | `RegrasTest.ct08_ate60MinAntesNaoHaTaxa` |
| M02 | `Regras.java` | cancela na hora exata da saída | detectado | `RegrasTest.ct10_naHoraDaSaidaOuDepoisNaoCancela` |
| M03 | `Regras.java` | preço arredonda para baixo | detectado | `RegrasTest.ct04_precoPorVaga` |
| M04 | `Regras.java` | esquece de contar o motorista | detectado | `ApiTest.ct01_criarEConsultarViagem` |
| M05 | `Regras.java` | máximo de vagas 6 -> 7 | detectado | `ApiTest.ct01_dadosInvalidosDevolvem400ComErroPadronizado` |
| M06 | `Regras.java` | antecedência de 30 min exclusiva | detectado | `RegrasTest.ct03_saidaNosLimitesInclusivosEAceita` |
| M07 | `Regras.java` | janela de 14 dias exclusiva | detectado | `RegrasTest.ct03_saidaNosLimitesInclusivosEAceita` |
| M08 | `Regras.java` | média exata 3,0 suspende | detectado | `RegrasTest.ct12_suspensaoPorMediaBaixa` |
| M09 | `Regras.java` | só suspende com 6 avaliações | detectado | `ApiTest.ct12_reputacaoEct13_suspensaoPontaAPonta` |
| M10 | `Regras.java` | taxa de 50% -> 40% | detectado | `ApiTest.ct08_ct09_cancelarReservaComESemTaxaPelaApi` |
| M11 | `Regras.java` | teto de R$ 25 -> 26 | detectado | `RegrasTest.ct04_precoPorVaga` |
| M12 | `Regras.java` | nota 1 vira inválida | detectado | `ApiTest.ct12_reputacaoEct13_suspensaoPontaAPonta` |
| M13 | `ServicoCarona.java` | lotação na checagem do serviço | detectado | `ServicoComDublesTest.ct06_ultimaVagaEAceitaEASeguinteERecusada` |
| M14 | `ServicoCarona.java` | motorista reserva a própria viagem | detectado | `ApiTest.ct05_reservarDevolve201ComValor` |
| M15 | `ServicoCarona.java` | avalia antes da saída | detectado | `ApiTest.ct11_avaliarExigeTerViajado` |
| M16 | `ServicoCarona.java` | qualquer um cancela reserva alheia | detectado | `ApiTest.ct10_cancelarReservaDeOutroOuInexistente` |
| M17 | `RepositorioSqlite.java` | lotação dentro da transação | detectado | `ConcorrenciaTest.ct06_seisReservasSimultaneasParaAUltimaVagaSoUmaVence` |
| M18 | `RepositorioSqlite.java` | índice único de reserva ativa vira comum | detectado | `RepositorioSqliteTest.ct07_indiceUnicoBarraReservaDuplicadaMesmoSemAChecagemDoServico` |
| M19 | `Erros.java` | conflito devolve 400 | detectado | `ApiTest.ct06_viagemLotadaDevolve409` |
| M20 | `Api.java` | autenticação desligada | detectado | `ApiTest.ct15_todoEndpointProtegidoExigeToken` |

**20 mutantes, 20 detectados (100%)**, em ≈ 8 min (cada rodada de Maven recompila e sobe o `ApiTest`).

## 6. Métricas

| Métrica | Valor |
|---|---|
| Requisitos atendidos | 8/8 RF · 5/5 RN · 4/5 RNF verificados nesta versão (RNF-01 medido só na versão Python) |
| Casos aprovados | 16/16 |
| Cobertura de instrução / ramo / linha | 95,3% / 88,1% / 92,6% |
| Mutantes detectados | 20/20 |
| Defeitos do produto encontrados | 0 (3 evitados por lições do Python) |
| Defeitos de teste encontrados / corrigidos | 3 / 3 |
| Linhas de código-fonte / de teste | 1.069 / 1.005 (não vazias, com comentários e Javadoc) |
| Tempo `mvn verify` no contêiner / Newman | ≈ 20 s / 3,7 s |

## 7. Conclusão

**O sistema está apto a ser liberado como versão de demonstração (v1.0.0), com as mesmas ressalvas da versão Python:** (a) autenticação simples (token sem expiração, sal de senha fixo); (b) a coleção Postman não prova os valores-limite de tempo; eles dependem dos testes unitários e de integração com relógio injetado; (c) falta rodar o pipeline no GitHub (não há workflow nesta versão).

**Recomendação:** liberar para o piloto com as ressalvas; trocar a autenticação na próxima versão.

## 8. Como reproduzir cada número

| Número | Comando |
|---|---|
| 149 testes e cobertura | `docker compose run --rm testes` (relatório HTML do JaCoCo em `target/site/jacoco/` dentro do contêiner) |
| 58 requests / 95 asserções | `docker compose --profile api up --build --abort-on-container-exit --exit-code-from newman` |
| 20 mutantes | `docker compose run --rm testes bash scripts/mutacao.sh` |
| Matriz de rastreabilidade | `python3 scripts/matriz_rastreabilidade.py` (precisa de Python 3 na máquina) |

Ambiente: Linux, Docker 29, Compose 5, `maven:3.9-eclipse-temurin-21` (OpenJDK 21), JUnit 5.10.2, Mockito 5.11.0, JaCoCo 0.8.12, sqlite-jdbc 3.46.0.0, Jackson 2.17.1.
