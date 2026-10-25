# Plano de Teste — CaronaCampus

> Baseado na estrutura IEEE 829 simplificada (Aula 04). Versão 1.0 · grupo fictício do exemplo de referência.

## 1. Identificação

| Campo | Conteúdo |
|---|---|
| **Sistema sob teste** | CaronaCampus — API de caronas solidárias entre alunos do mesmo campus |
| **Versão testada** | `v1.0.0` (Python 3.12, Flask 3.1, SQLite) |
| **Responsável** | Grupo (4 integrantes) · revisão cruzada entre duplas |
| **Documentos relacionados** | `casos-de-teste.md`, `relatorio-testes.md`, `postman/`, `.github/workflows/ci.yml` |

## 2. Escopo

**Dentro do escopo (o que será testado):**

- As **cinco regras de negócio** (RN-01 a RN-05), nos valores-limite
- O ciclo das viagens e reservas pela **API HTTP** (CRUD, reserva, cancelamento, avaliação, relatório)
- **Autenticação** por token e permissões (quem pode alterar o quê)
- **Persistência** em SQLite e **concorrência** na última vaga
- Execução em **contêiner** (`docker compose`) e no pipeline de CI

**Fora do escopo (e por quê):**

| Item | Motivo |
|---|---|
| Interface gráfica | O sistema entregue é uma API; não há tela |
| Pagamento real | Os valores são apenas calculados; não há cobrança |
| Desempenho sob carga (JMeter) | Medimos só o RNF-01 com um script simples; carga contínua fica para a próxima versão |
| Segurança avançada (OWASP, força bruta, expiração de token) | Declarado como risco aceito (R-04) |
| Aplicativo móvel | Não existe nesta versão |

## 3. Itens a testar (requisitos)

### 3.1 Funcionais

| ID | Requisito | Prioridade |
|---|---|---|
| RF-01 | Criar viagem (origem, destino, saída, vagas, custo) | Alta |
| RF-02 | Consultar, listar, alterar e cancelar viagem | Alta |
| RF-03 | Reservar uma vaga em uma viagem | Alta |
| RF-04 | Cancelar a própria reserva (com taxa quando em cima da hora) | Alta |
| RF-05 | Avaliar o motorista (nota de 1 a 5) após a viagem | Média |
| RF-06 | Consultar a reputação do motorista | Média |
| RF-07 | Relatório de ocupação das viagens abertas | Baixa |
| RF-08 | Autenticar com matrícula e senha | Alta |

### 3.2 Regras de negócio

| ID | Regra | Valores-limite a testar |
|---|---|---|
| RN-01 | Uma viagem tem de 1 a 6 vagas; não se reserva além da lotação | 0, 1, 6, 7 vagas; a última vaga e a seguinte |
| RN-02 | A saída fica entre 30 min e 14 dias à frente (limites inclusivos) | 29 min 59 s, 30 min, 14 dias, 14 dias + 1 s |
| RN-03 | Preço por vaga = custo ÷ (vagas + 1), arredondado **para cima** ao centavo, entre R$ 2,00 e R$ 25,00 | Piso, teto, arredondamento (R$ 10,00 ÷ 3 = R$ 3,34) |
| RN-04 | Cancelar com ≥ 60 min de antecedência: sem taxa; com menos: taxa de 50%; após a saída: proibido | 60 min 00 s, 59 min 59 s, 0 s |
| RN-05 | Com 5 ou mais avaliações, média < 3,0 suspende o motorista (não cria viagem) | 4 avaliações, 5 avaliações, média exata 3,0 |

### 3.3 Não funcionais

| ID | Requisito (mensurável) | Como medir |
|---|---|---|
| RNF-01 | `GET /viagens` com 1.000 viagens responde em ≤ 300 ms (p95) | `scripts/medir_desempenho.py` |
| RNF-02 | Todo endpoint, exceto `/saude` e `/auth/login`, exige token; senhas guardadas com hash | Testes CT-15 (parametrizado nos 10 endpoints) |
| RNF-03 | Valores monetários em `Decimal`, 2 casas, sem erro de ponto flutuante | CT-04, CT-09 |
| RNF-04 | `docker compose up --build` e `docker compose run --rm testes` funcionam numa máquina só com Docker | Execução em cópia limpa |
| RNF-05 | Nunca existem mais reservas ativas que vagas, mesmo com requisições simultâneas | CT-06 (6 threads na última vaga) |

## 4. Estratégia de teste

**Pirâmide (Aula 05):** muitos testes unitários rápidos, poucos de integração, uma coleção de API.

| Nível | O que cobre | Ferramenta | Pasta |
|---|---|---|---|
| **Unitário** | Regras puras (limites) e o serviço isolado, com repositório e relógio substituídos por dublês | pytest, `unittest.mock` | `tests/unitarios/` |
| **Integração** | Serviço + SQLite real; API Flask + SQLite; concorrência | pytest, cliente de teste do Flask | `tests/integracao/` |
| **API (sistema)** | Contrato HTTP, fluxos completos, mensagens de erro | Postman + Newman | `postman/` |
| **Qualidade do teste** | Os testes detectam defeito? | Mutação manual (21 mutantes) | `scripts/mutacao.py` |

**Técnicas de projeto de casos (Aulas 08 e 10):** partição de equivalência e **análise de valor-limite** para RN-01 a RN-05; **tabela de decisão** para as reservas inválidas (CT-07); **transição de estados** para a reserva (ativa → cancelada); **baseada em risco** para priorizar concorrência e autenticação.

**Relógio controlado:** nenhuma regra chama `datetime.now()`. Os testes injetam um relógio fixo, o que torna os limites de minutos testáveis sem `sleep`.

## 5. Critérios

| Tipo | Critério |
|---|---|
| **Entrada** | Código compilando/importando; ambiente Docker disponível; casos de teste revisados por outra dupla |
| **Saída (aprovação)** | 100% dos casos executados · 0 defeitos de severidade Alta ou Crítica abertos · cobertura de instrução ≥ 70% (meta 90%) · pipeline verde · coleção Newman sem falhas |
| **Suspensão** | Ambiente não sobe, ou falha em mais de 30% dos testes por causa de um único defeito de ambiente |
| **Retomada** | Ambiente corrigido e smoke test (`/saude` + login) verde |

## 6. Ambiente e ferramentas

| Item | Versão |
|---|---|
| Contêiner | `python:3.12-slim`, Docker Compose |
| Biblioteca de testes | pytest 9, pytest-cov 7 |
| Testes de API | Postman Collection v2.1, Newman (imagem `postman/newman`) |
| Pipeline | GitHub Actions (`.github/workflows/ci.yml`) |
| Dados | Usuários fictícios criados na montagem (`2026001`, `2026002`, `2026003`); nenhum dado pessoal real |

## 7. Riscos de teste

| ID | Risco | Prob. | Impacto | Mitigação |
|---|---|---|---|---|
| R-01 | Testes de tempo dependem do relógio real e ficam instáveis | Alta | Alto | Relógio injetado; modo de teste com `/teste/avancar-relogio` (desligado fora do compose de testes) |
| R-02 | Condição de corrida na última vaga passa despercebida | Média | Alto | Teste de concorrência com 6 threads e barreira; repetido 200 vezes após a correção |
| R-03 | Cobertura alta com testes fracos (assertions vazias) | Média | Alto | Teste de mutação manual e revisão das asserções |
| R-04 | Falhas de segurança fora do escopo (token sem expiração) | Alta | Médio | Declarado no relatório como risco aceito; recomendado para a próxima versão |
| R-05 | Dublê de repositório divergir do SQLite real | Média | Médio | O mesmo comportamento é conferido nos testes de integração |

## 8. Cronograma e responsabilidades

| Semana | Atividade | Quem |
|---|---|---|
| 1 | Plano de teste e levantamento de requisitos testáveis | Todos |
| 2 | Casos de teste e revisão cruzada | Duplas A e B |
| 3 | Testes unitários e de integração | Dupla A |
| 4 | Coleção Postman/Newman e pipeline | Dupla B |
| 5 | Mutação, relatório e ensaio da demonstração | Todos |

## 9. Métricas coletadas

Casos executados/aprovados, testes automatizados, cobertura de instrução e de ramo, mutantes detectados, defeitos por severidade, densidade de defeitos (por 100 linhas de código-fonte), tempo de execução da suíte e do Newman.
