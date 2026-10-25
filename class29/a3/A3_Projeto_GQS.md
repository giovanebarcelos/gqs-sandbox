# A3 — Projeto Integrador de Garantia da Qualidade de Software  

**Unidade Curricular:** Garantia da Qualidade de Software  
**Cursos:** C&TIC, Engenharias e Administração  
**Período:** 2026/2  
**Valor:** 40 pontos  
**Apresentação:** Aula 30 (12 min por dupla, mais as perguntas do professor)  
**Grupos:** duplas (trio só com autorização do professor)  
**Exemplo de referência:** [A3 — Projeto Exemplo: CaronaCampus](A3_Exemplo_CaronaCampus.md) (sistema testado em Python **e** em Java)  

---  

## Visão Geral e Critérios de Avaliação  

A A3 é o trabalho integrador final da disciplina. Em dupla, vocês escolhem um **sistema pequeno, com regras de negócio de verdade**, e entregam o que garante a sua qualidade: **plano de teste, casos de teste, testes automatizados, testes de API, pipeline de integração contínua e relatório de testes com conclusão**. O que se avalia **não é o sistema**, e sim a evidência de que vocês sabem testá-lo. O trabalho é feito **com um agente de IA** (obrigatório, seção "Uso do Agente de IA") e é defendido oralmente: quem construiu sabe explicar.  

A nota total é de **40 pontos**:  

| Critério | Pontos | O que é avaliado |  
|---|---|---|  
| **1. Plano de Teste** | 8 pts | Escopo (dentro e fora), estratégia, critérios de entrada e saída, riscos com mitigação |  
| **2. Casos de Teste** | 8 pts | No mínimo 15 casos, com técnica declarada, rastreados ao requisito, com resultado esperado |  
| **3. Testes Automatizados** | 8 pts | Unitários e de integração (JUnit ou pytest), padrão AAA, valores-limite, cobertura, e **rodando sem configurar nada** (Docker Compose) |  
| **4. Testes de API** | 5 pts | Coleção Postman com CRUD, asserções nos testes e `{{base_url}}`, executada pelo Newman |  
| **5. Pipeline CI/CD** | 5 pts | GitHub Actions com build, testes e cobertura, **com uma execução verde** |  
| **6. Relatório e Uso de IA** | 4 pts | Relatório de testes com conclusão (2 pts) + registro de Uso de IA no README (2 pts) |  
| **7. Apresentação** | 2 pts | Demonstração ao vivo, clareza e defesa oral |  
| **TOTAL** | **40 pts** | |  

> **O critério que atravessa todos: o que não executa não pontua.** O repositório precisa ser clonável e a suíte precisa rodar numa máquina que só tenha o Docker.  
>  
> **Dica:** um sistema **pequeno e bem testado** vale mais do que um sistema grande e mal testado. O que separa um projeto muito bom de um correto é o **relatório que conclui** ("liberar ou não, e sob qual risco") e a **prova de que os testes pegam defeito** (quebrar o código de propósito).  

---  

## Tema do Projeto  

O **sistema a ser testado é de livre escolha** do grupo. Escolham um sistema pequeno, com regras de negócio de verdade, que vocês consigam rodar, entender e modificar.  

### Critérios para um tema válido  

| Critério | O que verificar |  
|---|---|  
| **Regras de negócio** | Pelo menos **3 regras não triviais, com valores-limite testáveis** (prazos, faixas, limites, perfis, estados). Só cadastrar e listar (CRUD) **não** basta: não há o que testar com técnica |  
| **Interface testável por API** | O sistema expõe uma **API HTTP** com CRUD de pelo menos uma entidade. Sem API não há como cumprir o critério 4 |  
| **Persistência** | Guarda dados em SQLite, H2 ou similar, para existirem testes de integração de verdade |  
| **Código de vocês, ou de código aberto pequeno** | O ideal é escrever o sistema (300 a 700 linhas bastam). Sistema de terceiros só se for pequeno, se roda em contêiner e se o professor aprovar |  
| **Tamanho certo** | 5 a 8 requisitos funcionais. O esforço deve ir para **testar**, não para programar |  
| **Originalidade** | Não vale o sistema de Gestão de Tarefas (Trello) das versões anteriores, o checkout do app de delivery do Projeto Parcial A1 nem o do exemplo de referência (caronas) |  

### Exemplos de temas  

- Reserva de vagas de estacionamento de um campus, com tolerância de atraso e tarifa por faixa  
- Controle de estoque de uma cozinha comunitária, com validade e reposição  
- Fila de atendimento com prioridades legais e tempo máximo de espera  
- Mensalidades de uma academia, com planos, descontos e inadimplência  
- Sistema de pontuação de um campeonato, com critérios de desempate  
- Empréstimo de equipamentos de laboratório, com multa por atraso e limite por perfil  
- Cálculo de frete por faixa de peso e distância, com regras de gratuidade  
- Rateio de despesas de uma república, com regras de divisão  

> **Teste rápido do tema:** consigam, em 2 minutos, listar 3 regras de negócio com um número (por exemplo "cancelar com menos de 60 min é tardio"). Se não conseguirem, o tema é só um cadastro. Mostrem o tema ao professor **antes** de se comprometerem (veja o checkpoint 1).  

---  

## Metodologia e Roteiro ao Longo do Curso  

A A3 **não é feita na última semana**. Cada unidade produz um pedaço da entrega, e as aulas ensinam, na ordem, o que o projeto precisa.  

| Quando | Artefato da A3 | Aulas | Checkpoint com o professor |  
|---|---|---|---|  
| Aulas 1-4 | Escolha do sistema e das regras; esqueleto do `plano-de-teste.md` | Fundamentos, normas, ciclo de vida, plano de teste | **1.** Aprovação do tema (3 regras com número), até a Aula 4 |  
| Aulas 5-7 | Estratégia e níveis de teste; primeiros casos; registro de defeitos | Níveis, casos de teste, regressão e defeitos | Projeto Parcial A1 (Aula 7) |  
| Aulas 8-11 | Casos por **partição, valor-limite, caixa-branca, risco**; inspeção por pares | Técnicas de teste, inspeção e revisão | **2.** `plano-de-teste.md` e 8 casos revisados (Aula 11) |  
| Aulas 12-16 | Primeiros testes automatizados, mocks e TDD | Automação, JUnit, pytest, Mockito, TDD | **3.** Testes unitários verdes e `docker compose run --rm testes` funcionando (Aula 16) |  
| Aulas 17-21 | Testes de API (coleção Postman). Selenium, FitNesse, JMeter e mobile são **opcionais** | Aulas 19A e 19B (API); demais ferramentas | Coleção com CRUD e asserções |  
| Aulas 22-24 | Métricas, cobertura, análise estática, registro de defeitos | Métricas, cobertura, SonarQube, KiwiTCMS | Cobertura medida |  
| Aulas 25-26 | Pipeline no GitHub Actions | CI/CD, GitHub Actions | **4.** Workflow **executado e verde** (Aula 26) |  
| Aulas 27-28 | Estimativa e gestão de configuração (Git, versões, `CHANGELOG`) | Estimativa, versionamento | Histórico de commits distribuído |  
| Aula 29 | Relatório, checklist e ensaio da demonstração | Projeto integrador (esta aula) | Revisão final com o checklist |  
| Aula 30 | Apresentação e defesa oral | Encerramento | — |  

> **Faça o pipeline rodar cedo, mesmo com um teste só.** Um workflow criado na véspera falha por detalhe de ambiente e leva junto os 5 pontos, e o tempo que faltava para o relatório.  

---  

## O Projeto  

A A3 é entregue como um **repositório Git único**, com os conjuntos de artefatos abaixo. Para cada um, a seção "No material" indica onde a técnica foi ensinada.  

### 1. Plano de Teste  

**8 pontos | Local: `plano-de-teste.md`**  

- **Identificação:** sistema, versão testada, responsáveis  
- **Escopo:** o que **será** testado e o que **ficou de fora, com o motivo** (escopo sem "fora de escopo" é plano pela metade)  
- **Itens a testar:** requisitos funcionais, **regras de negócio com os valores-limite** e requisitos não funcionais **mensuráveis** ("responde em até 300 ms", não "rápido")  
- **Estratégia:** níveis (unitário, integração, API), **técnicas** de projeto de casos (partição, valor-limite, tabela de decisão, baseada em risco) e como o relógio e os dados serão controlados  
- **Critérios:** de **entrada**, de **saída** (o que é "pronto"), de **suspensão** e de **retomada**  
- **Ambiente e ferramentas**, com versões  
- **Riscos de teste:** cada um com probabilidade, impacto e **mitigação**  
- **Cronograma e responsabilidades**, e as **métricas** que serão coletadas  

> **No material:** Aula 03 (ciclo de vida do teste), Aula 04 (planejamento da qualidade e planos de teste), Aula 05 (níveis), Aula 10 (técnicas baseadas em requisitos e risco).  

### 2. Casos de Teste  

**8 pontos | Local: `casos-de-teste.md`**  

- **No mínimo 15 casos**, cada um com: **ID, requisito coberto, técnica, pré-condição, dados e passos, resultado esperado**  
- **Técnica declarada** em cada caso (partição, valor-limite, tabela de decisão, transição de estados...). "Teste do cadastro" não é técnica  
- **Valores-limite exatos** das regras com número (o que acontece em 60 min? Em 59 min 59 s?)  
- Casos **negativos** (o que o sistema deve recusar), não só o caminho feliz  
- **Rastreabilidade:** todo caso aponta um requisito que existe no plano, e todo requisito importante tem pelo menos um caso  

> **No material:** Aula 06 (especificação e documentação de casos), Aula 08 (caixa-preta), Aula 09 (caixa-branca), Aula 10 (requisitos e risco).  

### 3. Testes Automatizados  

**8 pontos | Local: `tests/` (ou `src/test/`)**  

- **No mínimo 10 testes** (a maioria dos projetos bons tem dezenas, com parametrização), em **JUnit 5** (Java) ou **pytest** (Python), no padrão **AAA** (Arrange, Act, Assert)  
- **Unitários** (regra isolada, com **dublê** do repositório e **relógio injetado**) **e de integração** (serviço + banco real, API + banco real)  
- **Valores-limite das regras** testados nos dois lados do limite (exatamente 60 min e 59 min 59 s)  
- **Cobertura de instrução ≥ 70%** (JaCoCo ou coverage.py), com a cobertura **lida**: o que está nos pontos percentuais que faltam?  
- **Cada teste cita o caso** que automatiza (`test_ct07_...`), para a rastreabilidade ser conferida por script  
- **Prova de que os testes pegam defeito:** quebrem o código de propósito em **pelo menos 3 pontos** (por exemplo `>=` vira `>` num limite) e mostrem que algum teste fica vermelho. Teste que nunca foi visto falhando não vale  
- **Sistema que roda, empacotado com Docker (obrigatório):** `Dockerfile` e `docker-compose.yml` **na raiz do repositório**. Quem avalia roda, sem instalar nada além do Docker:  
  - `docker compose up --build` → sobe o sistema  
  - `docker compose run --rm testes` → roda a suíte de testes **com cobertura** (serviço `testes` no compose; falha se a cobertura ficar abaixo de 70%)  
- **Regras do compose:** versão da linguagem fixada na imagem (ex.: `python:3.12-slim`, `maven:3.9-eclipse-temurin-21`); nenhuma chave de API, senha ou `.env` na imagem ou no repositório; se houver banco, ele é **um serviço do compose** (com volume)  

#### Rota Java e rota Python: exemplos de referência  

O exemplo de referência está **nas duas linguagens**, com o mesmo sistema, as mesmas regras e a **mesma coleção Postman**. Use o da sua linguagem como guia de **estrutura e de Docker**, não como código para copiar.  

| Ponto | Python | Java |  
|---|---|---|  
| Biblioteca de testes | pytest (parametrização com `@pytest.mark.parametrize`) | JUnit 5 (`@ParameterizedTest`) |  
| Dublê do repositório | `unittest.mock.Mock(spec=...)` | Mockito |  
| Cobertura | pytest-cov (`--cov-fail-under=70`) | JaCoCo (`check` no `mvn verify`) |  
| Teste da API | cliente de teste do Flask | servidor `HttpServer` real + `HttpClient` |  
| Imagem do compose | `python:3.12-slim` | `maven:3.9-eclipse-temurin-21` |  
| Mutação manual | `scripts/mutacao.py` (21 mutantes) | `scripts/mutacao.sh` (20 mutantes, dentro do contêiner) |  

🔗 [Repositório de referência — Python (a3-caronacampus)](https://github.com/giovanebarcelos/gqs-sandbox/tree/main/class29/a3-caronacampus)  
🔗 [Repositório de referência — Java (a3-caronacampus-java)](https://github.com/giovanebarcelos/gqs-sandbox/tree/main/class29/a3-caronacampus-java)  
🔗 [Zip — Python, com histórico Git e tag v1.0.0](https://raw.githubusercontent.com/giovanebarcelos/gqs-sandbox/main/class29/a3/a3-caronacampus-python.zip)  
🔗 [Zip — Java](https://raw.githubusercontent.com/giovanebarcelos/gqs-sandbox/main/class29/a3/a3-caronacampus-java.zip)  

**No Java:** a **primeira** execução baixa as dependências do Maven e leva alguns minutos; as seguintes usam o cache. **Avisem quem for avaliar** e testem a cópia limpa com antecedência.  

> **No material:** Aula 12 (fundamentos de automação), Aula 13 (JUnit), Aula 14 (pytest), Aula 15 (mocks, stubs e dublês), Aula 16 (TDD), Aula 23 (cobertura).  

### 4. Testes de API  

**5 pontos | Local: `postman/`**  

- **Coleção Postman (v2.1) e environment** versionados  
- **CRUD completo** da entidade principal: criar, consultar, listar, alterar e excluir  
- **Asserções nos testes de cada request:** o status **e** o conteúdo (campo, valor, tipo). Coleção só com requisições, sem asserções, é nota parcial  
- **Casos negativos** (400, 401, 403, 404, 409) e as **regras de negócio** vistas pela API  
- **`{{base_url}}`:** nenhuma URL fixa em `localhost`  
- **Executável pelo Newman**, de preferência como serviço do compose (`docker compose --profile api up --abort-on-container-exit --exit-code-from newman`)  
- Se a API tem autenticação, a coleção faz **login** e guarda o token em variável  

> **No material:** Aula 19A (Bruno), Aula 19B (Postman e Newman).  

### 5. Pipeline CI/CD  

**5 pontos | Local: `.github/workflows/ci.yml`**  

- Workflow do **GitHub Actions** com **build, testes e cobertura** (o job falha se algum teste falhar ou se a cobertura ficar abaixo do mínimo)  
- Passo dos **testes de API** (Newman) ou, no mínimo, justificativa de por que não foi possível  
- **Artefatos** (relatório de cobertura e de testes) publicados com `upload-artifact`  
- **Nenhum `continue-on-error` escondendo falha**, e nenhuma credencial no arquivo (segredos no GitHub Secrets)  
- **A aba Actions precisa ter pelo menos uma execução verde.** Arquivo `.yml` que nunca rodou vale zero. **Entreguem o link da execução verde na capa**  
- **Diferencial (não muda a nota):** análise estática com Quality Gate (SonarQube, Aula 23B)  

> **No material:** Aula 25 (CI/CD), Aula 26 (GitHub Actions), Aula 23B (SonarQube).  

### 6. Relatório de Testes e Uso de IA  

**4 pontos | Local: `relatorio-testes.md` (2 pts) e seção "Uso de IA" do `README.md` (2 pts)**  

O relatório **não é a saída da ferramenta colada num arquivo: é a leitura dela**. Estrutura mínima:  

| Seção | O que escrever | Origem |  
|---|---|---|  
| **1. Escopo executado** | O que foi testado e o que ficou de fora, com motivo | Plano de Teste |  
| **2. Resumo da execução** | Total, aprovados, reprovados, bloqueados, por nível | Relatório do JUnit/pytest e do Newman |  
| **3. Cobertura** | % de instrução e de ramo; **o que não foi exercitado** | JaCoCo / coverage.py |  
| **4. Defeitos encontrados** | ID, severidade, prioridade, estado, passos de reprodução, solução. **Só defeitos reais** | Aula 24 |  
| **5. Métricas** | Densidade de defeitos, taxa de aprovação, mutantes detectados | Aula 22 |  
| **6. Conclusão** | O sistema está apto a ser liberado? **Sob quais riscos e ressalvas?** | Critérios de saída do plano |  

> **O que separa 2 pontos de 1:** a seção 6. Números sem conclusão são dados; conclusão sem números é opinião.  
>  
> **Não inventem defeitos.** Se não acharam nenhum, o relatório diz isso, e mostra o que foi feito para procurar (teste de mutação, revisão por pares, exploratório). Um defeito real, bem descrito, vale mais que dez inventados, e a defesa oral pergunta.  

> **No material:** Aula 22 (métricas), Aula 23 e 23B (cobertura e análise estática), Aula 24 (defeitos), Aula 29 (estrutura do relatório). O registro de Uso de IA está descrito na próxima seção.  

### 7. Apresentação  

**2 pontos | Aula 30**  

Veja a seção "Apresentação" abaixo.  

---  

## Uso do Agente de IA (obrigatório)  

A A3 **exige** o uso do agente de IA da disciplina e a **declaração** do que foi feito com ele. A razão: um engenheiro de qualidade hoje trabalha com agentes, e o que se ensina aqui é **usar, conferir e corrigir**, não aceitar. **O agente escreve, o grupo duvida.**  

### O que usar  

O setup é o da **Aula 01B** (IA Assistida: Skills, OpenCode e OpenRouter): OpenCode aberto **na raiz do repositório do projeto**, modelo gratuito via OpenRouter, skills em `.agents/skills/`, `opencode.json` com `push` negado e `ask` para comandos (Slide 31) e `AGENTS.md` mantido atualizado (Slide 33). Quem usa o Claude Code segue o Slide 29. A política completa é o **Slide 32 da Aula 01B**.  

### Onde o agente entra: mapa por etapa  

Cada aula de 02 a 30 tem um slide **🤖 Com o Agente**. Para a A3, estes são os pontos de entrada:  

| Etapa da A3 | Use o agente para | Material |  
|---|---|---|  
| Plano de Teste | `grilling` + `to-spec` sobre escopo, riscos e decisões de teste | Aula 01B (Slides 20-21, 25-27), Aulas 04 e 10 (🤖) |  
| Casos de Teste | Derivar casos por partição, limite e tabela de decisão; checar rastreabilidade (agente `plan`) | Aulas 06, 08, 09 e 29 (🤖) |  
| Testes automatizados | Rascunho com `tdd`; revisão de asserções; **mutação manual** | Aulas 13, 14, 15 e 16 (🤖) |  
| Testes de API | Traduzir e revisar a coleção | Aula 19B (🤖) |  
| Pipeline CI/CD | Revisar o workflow contra os anti-padrões | Aulas 25 e 26 (🤖) |  
| Análise estática | Corrigir a causa, **sem suprimir o aviso** | Aulas 23 e 23B (🤖) |  
| Relatório | Revisar a estrutura e conferir números e conclusão | Aulas 22, 24 e 29 (🤖) |  

### Regras do jogo  

1. **Revise tudo o que o agente produz como um pull request.** **Teste escrito por IA só entra depois de falhar quando o código é quebrado de propósito**  
2. **Primeiro à mão, depois o agente** nas atividades de aula (Slide 32 da Aula 01B). **Na A3 o agente é parte do método**, mas a decisão e o entendimento são seus  
3. **Dados:** nada de dados pessoais reais (CPF, e-mail, cartão), credenciais ou dados de clientes nos prompts nem nos dados de teste  
4. **Chave de API fora do Git.** Ela fica em `~/.local/share/opencode/auth.json`, nunca no repositório  
5. **O agente propõe, a equipe publica.** `git push` é de vocês (negado no `opencode.json`)  
6. **A responsabilidade é do grupo.** "Foi a IA" não é justificativa para erro, falha ou plágio  
7. **A1 e A2 continuam sem agente**, e a A3 é a única avaliação em que ele é parte do método  

### O registro de uso (seção "Uso de IA" do README)  

O README tem uma seção **Uso de IA** com:  

- **Ferramenta e modelo** (ex.: OpenCode + Nemotron 3 Ultra `:free` via OpenRouter) e **skills** utilizadas  
- Uma **tabela de registros**, no mínimo **5 linhas**, cobrindo **pelo menos 4 tipos de artefato diferentes** (plano, casos de teste, testes automatizados, coleção, workflow, relatório):  

| # | Artefato | Pedido (resumo) | O que o agente errou ou omitiu | O que o grupo fez |  
|---|---|---|---|---|  

- Em **pelo menos 2 linhas**, a coluna "o que o agente errou ou omitiu" deve ter um **erro real** encontrado e corrigido. Não encontrar nenhum erro ao longo do projeto inteiro é sinal de que a revisão não foi feita  
- Quais testes escritos pelo agente foram **quebrados de propósito** e falharam antes de serem aceitos  
- Uma **reflexão curta**: onde a IA mais ajudou, onde mais errou, o que o grupo faria diferente  
- O `AGENTS.md` e o `opencode.json` **versionados** no repositório  

> **Pontuação do registro (2 pontos do item 6):** 2 pts = tabela completa, erros reais documentados e `AGENTS.md` coerente com o repositório; 1 pt = tabela presente, mas genérica ("a IA ajudou muito"), sem erros reais; 0 = seção ausente. O registro mede **transparência e senso crítico**, não a quantidade de uso.  

Modelos prontos: [`AGENTS.md`](https://github.com/giovanebarcelos/gqs-sandbox/blob/main/class29/AGENTS.md) e [`USO_DE_IA_modelo.md`](https://github.com/giovanebarcelos/gqs-sandbox/blob/main/class29/USO_DE_IA_modelo.md).  

### Defesa oral  

Na apresentação, o professor escolhe um trecho de qualquer artefato (um caso de teste, uma asserção, uma etapa do workflow) e pede à dupla que o explique. **Artefato que a dupla não sabe explicar é avaliado como Insuficiente naquele critério, feito com ou sem IA** (Aula 01B, Slide 32). Usar IA e não declarar, sabendo explicar tudo, não reduz a nota do critério; não saber explicar, sim.  

---  

## Rubrica de Avaliação (40 pontos)  

**Níveis de desempenho aplicados ao peso de cada critério:**  

- **Excelente (90-100% do peso):** todos os itens presentes, completos, executáveis e coerentes entre si  
- **Bom (60-89% do peso):** itens presentes, com lacunas pontuais  
- **Insuficiente (< 60% do peso):** item ausente, incompleto, que não executa, ou que a dupla não sabe explicar  

| Critério | Pts | Excelente | Insuficiente |  
|---|---|---|---|  
| **Plano de Teste** | 8 | Escopo (com o "fora de escopo"), estratégia, critérios de entrada e saída e riscos **com mitigação**, todos ligados ao sistema escolhido | Texto genérico, sem relação com o sistema; riscos sem mitigação; sem critério de saída |  
| **Casos de Teste** | 8 | 15+ casos com técnica declarada, valores-limite exatos, negativos e rastreados ao requisito | Casos sem resultado esperado ou sem pré-condição; sem técnica; sem rastreabilidade |  
| **Testes Automatizados** | 8 | 10+ testes em AAA, unitários e de integração, valores-limite, cobertura ≥ 70% lida, prova de que falham ao quebrar o código, **rodam em `docker compose run --rm testes`** | Suíte que não executa na máquina do professor; só caminho feliz; cobertura abaixo de 70% |  
| **Testes de API** | 5 | Coleção com CRUD, asserções de status e conteúdo, negativos, `{{base_url}}`, rodando no Newman | Coleção sem asserções; que não importa; que aponta para host inexistente |  
| **Pipeline** | 5 | Workflow verde com build, teste e cobertura, artefatos e link da execução na capa | Workflow que só roda o build; `.yml` presente mas nunca executado |  
| **Relatório** | 2 | Escopo, execução, cobertura **lida**, defeitos reais, métricas e **conclusão** (liberar? sob quais riscos?) | Só a saída bruta da ferramenta; sem conclusão |  
| **Uso de IA** | 2 | Tabela completa (≥ 5 registros, ≥ 4 tipos), erros reais, reflexão, `AGENTS.md` coerente | Seção ausente ou genérica |  
| **Apresentação** | 2 | 12 min com demonstração ao vivo, teste falhando de propósito, resposta às perguntas | Só slides; sem executar nada; não sabe explicar o próprio teste |  

---  

## Apresentação (Aula 30)  

**12 minutos por dupla.** Todos falam. Não é leitura de slides: é a **demonstração** dos testes rodando e a defesa das decisões.  

| Parte | Tempo | Conteúdo |  
|---|---|---|  
| **Plano de Teste** | 2 min | O sistema (1 frase), o escopo, as regras e os riscos principais |  
| **Casos de Teste** | 2 min | Três casos à escolha: a técnica de cada um e o requisito que cobre |  
| **Testes automatizados** | 3 min | `docker compose run --rm testes` **ao vivo**, a cobertura e **um teste falhando de propósito** |  
| **Testes de API** | 2 min | A coleção no Newman (ou Postman), com um caso negativo |  
| **Pipeline** | 2 min | A execução verde na aba Actions e o que cada job faz |  
| **Relatório e IA** | 1 min | As métricas, a conclusão e o que o agente errou |  
| **Perguntas do professor** | — | Depois dos 12 minutos (ver abaixo) |  

### As seis perguntas da defesa  

| Pergunta | O que ela verifica |  
|---|---|  
| "Por que estes 15 casos, e não outros?" | Se a seleção foi por técnica ou por intuição |  
| "O que este teste detectaria se quebrasse?" | Se o aluno entende o próprio teste |  
| "Sua cobertura é X%. O que está nos pontos que faltam?" | Se a métrica foi lida ou só reportada |  
| "Este teste é unitário ou de integração?" | Se o nível foi escolhido conscientemente |  
| "O que aconteceria se este mock não existisse?" | Se o isolamento foi decisão ou cópia |  
| "O agente escreveu este teste? O que você mudou nele?" | Se o uso de IA foi **revisado** ou aceito às cegas |  

> **A resposta que vale mais que a certa:** "não testei este caminho, e o motivo é X". Reconhecer um limite conhecido demonstra domínio; inventar uma justificativa desmonta na pergunta seguinte.  
>  
> **Preparem a demonstração para falhar com graça:** relógio e dados fixos, comandos já testados, um vídeo de 2 min de reserva e a imagem do contêiner já construída.  

---  

## Como Entregar no Anima Hub  

A entrega é **uma por dupla**, postada por **um integrante** na atividade "A3" do Anima Hub, dentro do prazo definido na própria atividade. Postem **todos** os itens abaixo:  

| # | O que postar | Formato |  
|---|---|---|  
| 1 | **Capa da entrega:** nome do projeto, integrantes (nome e RA), link do repositório, **tag da entrega**, **hash do commit** e **link da execução verde** na aba Actions | PDF de 1 página |  
| 2 | **Repositório completo**, incluindo a pasta `.git` (o histórico é avaliado) | `.zip` (sem `.venv`, `target` nem `node_modules`) |  
| 3 | **Link do repositório no GitHub** (público, ou com o professor convidado como colaborador), no campo de texto da atividade | Link |  

**Antes de postar, confiram:**  

- [ ] O `zip` abre e o `README` ensina a rodar com Docker  
- [ ] Numa **cópia limpa** (zip descompactado em outra pasta), `docker compose up --build` funciona e `docker compose run --rm testes` passa  
- [ ] **Um colega de outra dupla** clonou o repositório e rodou a suíte seguindo **só o README**, em até 5 minutos  
- [ ] A tag da entrega aponta para o commit que está no zip  
- [ ] A aba Actions tem uma execução **verde** desse commit, e o link está na capa  
- [ ] Nenhuma chave de API, `.env` ou banco de dados no repositório (se commitaram uma credencial, **revoguem-na**: apagar em outro commit não resolve)  
- [ ] Os dois integrantes têm commits no histórico  

> Mudanças **depois** da data de entrega não são consideradas: o commit da tag vale.  

---  

## Checklist Completo da A3  

Mesmo checklist da Aula 29 (Slide 10), com os itens do enunciado:  

**Plano de Teste**  
- [ ] Escopo positivo **e negativo** (o que ficou de fora, com motivo)  
- [ ] Requisitos, regras com valores-limite e RNF **mensuráveis**  
- [ ] Critérios de entrada, saída, suspensão e retomada  
- [ ] Riscos identificados **e mitigados**  

**Casos de Teste**  
- [ ] Mínimo de 15 casos: ID, requisito, técnica, pré-condição, dados, passos, resultado esperado  
- [ ] Valores-limite exatos e casos negativos  
- [ ] Todo caso aponta um requisito que existe  

**Testes Automatizados**  
- [ ] Mínimo de 10 testes (AAA), unitários **e** de integração  
- [ ] Cobertura ≥ 70%, com a análise do que ficou de fora  
- [ ] Cada teste cita o caso que automatiza; matriz conferida por script  
- [ ] Código quebrado de propósito em 3+ pontos, e a suíte ficou vermelha  
- [ ] `Dockerfile` e `docker-compose.yml` na raiz; `docker compose up --build` e `docker compose run --rm testes` funcionam  

**Testes de API**  
- [ ] Coleção com CRUD, asserções (status **e** conteúdo), casos negativos e `{{base_url}}`  
- [ ] Newman roda a coleção (de preferência como serviço do compose)  

**Pipeline**  
- [ ] Workflow com build, testes e cobertura; sem `continue-on-error`  
- [ ] **Execução verde na aba Actions**, com o link na capa  

**Relatório e Uso de IA**  
- [ ] Relatório com as 6 seções e uma **conclusão** (liberar? sob quais riscos?)  
- [ ] Defeitos reais, sem invenção  
- [ ] README com a seção **Uso de IA** (ferramenta, modelo, skills, ≥ 5 registros, ≥ 2 erros reais, reflexão)  
- [ ] `AGENTS.md` e `opencode.json` versionados e coerentes com o repositório  
- [ ] Todo teste escrito pelo agente foi quebrado de propósito e falhou  

**Versionamento e apresentação**  
- [ ] Commits ao longo das semanas, de todos, em Conventional Commits; `.gitignore`; `CHANGELOG.md`; tag da entrega  
- [ ] Os dois integrantes sabem explicar qualquer artefato  

---  

## Estrutura de Diretórios Esperada  

```  
meu-projeto/  
├── README.md              # com a seção "Uso de IA" e os comandos de execução  
├── AGENTS.md              # memória do projeto para o agente  
├── opencode.json          # modelo e permissões do agente  
├── Dockerfile             # imagem do sistema (e dos testes)  
├── docker-compose.yml     # serviços: sistema, testes, (api de teste e newman)  
├── .dockerignore · .gitignore · CHANGELOG.md  
├── plano-de-teste.md  
├── casos-de-teste.md  
├── relatorio-testes.md  
├── src/                   # código do sistema  
├── tests/                 # ou src/test/ (Java)  
│   ├── unitarios/  
│   └── integracao/  
├── postman/               # coleção + environment  
├── scripts/               # mutação, rastreabilidade (opcional, mas vale a pena)  
└── .github/  
    └── workflows/  
        └── ci.yml  
```  

A estrutura é uma sugestão; adaptem à linguagem. Em Java, os testes ficam em `src/test/java` e a raiz ganha o `pom.xml`. Os repositórios de referência ([`a3-caronacampus`](https://github.com/giovanebarcelos/gqs-sandbox/tree/main/class29/a3-caronacampus) em Python e [`a3-caronacampus-java`](https://github.com/giovanebarcelos/gqs-sandbox/tree/main/class29/a3-caronacampus-java)) seguem esta organização.  

---  

## Perguntas Frequentes  

**O sistema precisa ser feito por nós?**  
É o recomendado: vocês conhecem o código e conseguem quebrá-lo de propósito. Um sistema de código aberto pequeno só é aceito com aprovação do professor, se rodar em contêiner e se vocês escreverem **todos** os testes.  

**Preciso de interface gráfica?**  
Não. O que se avalia são os testes. Uma API pequena e bem testada vale mais que uma tela bonita sem teste.  

**Posso testar também com Selenium, JMeter ou FitNesse?**  
Pode, como **complemento**, e conta como diferencial na apresentação. Não substitui nenhum dos sete critérios.  

**Python ou Java?**  
Os dois valem. Mantenham uma linguagem só no projeto. Os dois exemplos de referência estão testados em Docker.  

**Preciso saber Docker a fundo?**  
Não. Um `Dockerfile` de poucas linhas e um compose com 2 a 4 serviços bastam; os repositórios de referência têm um que funciona e pode servir de base. O que se avalia é que **outra pessoa rode o projeto sem configurar nada**.  

**O relatório precisa ter defeitos?**  
Precisa dizer a verdade. Se acharam defeitos, descrevam-nos (ID, severidade, passos, solução). Se não acharam, mostrem o que fizeram para procurar: teste de mutação, revisão por pares, teste exploratório. **Inventar defeito é pior que não ter**: a defesa oral pergunta.  

**Cobertura de 100% é a meta?**  
Não. A meta é 70% como piso, e **entender** o que está fora. Cobertura alta com asserção fraca engana: o teste de mutação mostra isso.  

**Posso usar um modelo de IA diferente do da Aula 01B?**  
Pode, desde que declarado no README (ferramenta, modelo e versão) e respeitadas as regras de dados e de chave de API.  

**A IA escreveu a maior parte dos testes. Isso é um problema?**  
Não por si só, **se vocês entendem e sabem defender**, e se já viram o teste falhar quando o código quebra. O problema é um teste que o grupo não consegue explicar.  

**E se o agente errar e eu não notar?**  
O erro é de vocês; é por isso que o registro de uso pede erros reais encontrados. Teste de mutação, execução em contêiner limpo e conferência da rastreabilidade são as defesas.  

**O workflow do GitHub Actions não roda. E agora?**  
Rodem cedo. Os motivos mais comuns: o `.github/workflows` não está na **raiz** do repositório; a versão da linguagem do workflow é diferente da do contêiner; um passo depende de arquivo que está no `.gitignore`. Leiam o log do job, que sempre aponta a linha.  

---  

## Ferramentas Recomendadas  

| Categoria | Ferramenta | Acesso |  
|---|---|---|  
| Agente de IA | OpenCode + OpenRouter (modelo `:free`) | Gratuito (Aula 01B) |  
| Agente alternativo | Claude Code | Conforme sua conta (Aula 01B, Slide 29) |  
| Testes | pytest + pytest-cov (Python), JUnit 5 + Mockito + JaCoCo (Java) | Gratuito |  
| Testes de API | Postman (ou Bruno) + Newman | Gratuito |  
| Contêineres | Docker e Docker Compose | Gratuito (Docker Engine, ou Docker Desktop conforme a licença) |  
| CI/CD | GitHub Actions | Gratuito para repositórios públicos |  
| Análise estática | SonarQube Community / SonarCloud | Gratuito (opcional, Aula 23B) |  
| Defeitos e casos | KiwiTCMS, GitHub Issues | Gratuito |  
| Versionamento | Git + GitHub | Gratuito |  

---  

## Referências  

- MYERS, Glenford J. *The Art of Software Testing*. 3. ed. Wiley, 2011.  
- PRESSMAN, Roger S.; MAXIM, Bruce R. *Engenharia de software: uma abordagem profissional*. 9. ed. AMGH, 2021.  
- ISTQB. *Certified Tester Foundation Level Syllabus* (CTFL 4.0).  
- BECK, Kent. *Test Driven Development: By Example*. Addison-Wesley, 2002.  
- IEEE Std 829-2008. *Standard for Software and System Test Documentation*.  
- Conventional Commits 1.0.0. conventionalcommits.org.  
- Material da disciplina: Aulas 1-30, Aula 01B (IA assistida) e o exemplo de referência *CaronaCampus*.  
