# CaronaCampus — versão Java

O **mesmo sistema** do exemplo em Python (`../a3-caronacampus`): API de caronas solidárias entre alunos do mesmo campus, com as mesmas cinco regras de negócio, o mesmo contrato HTTP e a **mesma coleção Postman**. Muda a pilha: **Java 21, JDK `HttpServer`, SQLite via JDBC, JUnit 5, Mockito e JaCoCo**.

> **Projeto de referência da A3 de Garantia da Qualidade de Software, rota Java.** O grupo e as datas são fictícios; **os testes, as medições e os defeitos descritos foram executados de verdade**. Use como guia, não como modelo para copiar. Esta pasta traz plano, casos e relatório próprios (adaptados da versão Python); **uma entrega em Java precisa dos seus próprios** — com o seu tema.

## Como rodar (só precisa do Docker)

```bash
docker compose up --build        # sobe a API em http://localhost:5000
docker compose run --rm testes   # mvn verify: 149 testes JUnit + JaCoCo (falha abaixo de 70%)
docker compose --profile api up --build --abort-on-container-exit --exit-code-from newman
                                 # a MESMA coleção Postman do exemplo Python: 58 requests, 95 asserções
```

A **primeira** execução baixa as dependências do Maven e leva alguns minutos (≈ 2 min para construir a imagem); as seguintes usam o cache. O serviço `testes` roda **offline** (`mvn -o`): um teste rápido no build baixa o provider do JUnit e o JaCoCo. Avisem quem for avaliar e testem a cópia limpa com antecedência.

Usuários fictícios de demonstração: `2026001`/`ana123`, `2026002`/`bruno123`, `2026003`/`carla123`. Os exemplos de `curl` do README Python valem aqui.

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
carona-campus-java/
├── README.md · AGENTS.md · opencode.json · CHANGELOG.md
├── plano-de-teste.md · casos-de-teste.md · relatorio-testes.md
├── pom.xml · Dockerfile · docker-compose.yml
├── src/main/java/br/edu/carona/
│   ├── Regras.java            # RN-01 a RN-05, funções puras
│   ├── ServicoCarona.java     # casos de uso (depende só da porta)
│   ├── RepositorioCarona.java # porta · RepositorioSqlite.java = adaptador JDBC
│   ├── Api.java · App.java    # HTTP e ponto de entrada
│   └── Erros.java · Viagem.java · Reserva.java · Usuario.java · Senhas.java · RelogioAjustavel.java
├── src/test/java/br/edu/carona/   # RegrasTest · ServicoComDublesTest · ServicoSqliteTest
│                                  # RepositorioSqliteTest · ApiTest · ConcorrenciaTest · ArquiteturaTest
├── postman/                   # coleção + environment (idênticos aos do exemplo Python)
└── scripts/                   # mutacao.sh · matriz_rastreabilidade.py
```

## Diferenças em relação ao Python

| Ponto | Python | Java |
|---|---|---|
| Dublê do repositório | `unittest.mock.Mock(spec=...)` | Mockito `@Mock` sobre a interface |
| Testes parametrizados | `pytest.mark.parametrize` | `@ParameterizedTest` (`@CsvSource`, `@ValueSource`) |
| Teste da API | cliente de teste do Flask (em processo) | servidor `HttpServer` real em porta livre + `HttpClient` |
| Dinheiro | `Decimal` | `BigDecimal` (`RoundingMode.CEILING` / `HALF_UP`) |
| Atomicidade da reserva | `BEGIN IMMEDIATE` explícito | `SQLiteConfig.TransactionMode.IMMEDIATE` + `setAutoCommit(false)` |
| Fechar conexões | `contextmanager` que fecha (DEF-002) | `try-with-resources` desde o início |
| Cobertura | `pytest-cov` | JaCoCo no `mvn verify`, com regra mínima de 70% no `pom.xml` |
| Mutação | `scripts/mutacao.py` (21 mutantes, local) | `scripts/mutacao.sh` (20 mutantes, **dentro do contêiner**) |
| Teste de guarda | `ast` lê os `import` | lê o texto dos `.java` |
| Pipeline | `.github/workflows/ci.yml` incluído | não incluído (use `actions/setup-java@v4` e `mvn verify`) |

## Resultados (resumo — detalhes em `relatorio-testes.md`)

| Medida | Resultado |
|---|---|
| Casos de teste | 16 · todos automatizados e aprovados |
| Testes JUnit | 149 (83 unitários, 66 de integração) · 0 falhas |
| Cobertura (JaCoCo) | 95,3% de instruções · 88,1% de ramos · 92,6% de linhas |
| Testes de API | 58 requests · 95 asserções · 0 falhas, com a coleção do exemplo Python |
| Mutação manual | 20 mutantes · 20 detectados (≈ 8 min) |

## Uso de IA

O registro completo de Uso de IA (9 registros, erros reais) está no README do exemplo Python. **Vale para o exemplo inteiro; a sua entrega precisa do seu próprio.** O que esta versão acrescenta: o agente reescreveu em Java o que a versão Python havia ensinado (transação única na reserva, conexões fechadas, validação na alteração) e **errou de outras maneiras**:

| # | Artefato | Pedido (resumo) | O que o agente errou ou omitiu | O que o grupo fez |
|---|---|---|---|---|
| 1 | `ServicoComDublesTest` | Teste do caso "repositório diz que lotou" | `when(repo.reservarSeHaVaga(any(), anyInt())).thenReturn(...)` **executa o stub anterior** com argumentos nulos e dá `NullPointerException` antes do teste começar | Trocou por `doReturn(...).when(repo)...`, que não executa o stub anterior |
| 2 | `ArquiteturaTest` | Teste "relógio real só em `RelogioAjustavel`" | A expressão regular achou `LocalDateTime.now()` **dentro de um Javadoc** de `Regras.java` e reprovou código certo | Passou a remover comentários antes de procurar |
| 3 | `RepositorioSqlite` | Traduzir erros do banco | Converteu uma violação de restrição em `IllegalArgumentException` num método e em `IllegalStateException` nos outros | Tratamento único (`IllegalStateException`); o teste cobre FK e CHECK |

## Licença

Uso didático.
