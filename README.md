# Repositório de Código — Garantia da Qualidade de Software  

**Curso:** Garantia da Qualidade de Software | **Período:** 2026/2  
**Origem do material:** adaptado de `/2025-02/4M - Gestão e Qualidade de Software - FAPA` (classes 01-26, 29-30) + conteúdo original da UA8 (classes 27-28, ver §1 de `planejamento.md`)  

---  

## Estrutura  

```  
repository/  
├── class12/ — Fundamentos de Automação de Testes  
├── class13/ — JUnit: Testes Unitários em Java  
├── class15/ — Mocks, Stubs e Doubles (Mockito)  
├── class16/ — TDD — Desenvolvimento Orientado a Testes  
├── class17/ — Selenium WebDriver  
├── class18/ — FitNesse: Testes de Aceitação  
├── class19/ — Postman: Testes de API REST  
├── class20/ — JMeter: Testes de Performance  
├── class21/ — SikuliX: Testes de Interface  
├── class21B/ — Testes Mobile: Maestro e Appium  
├── class23B/ — SonarQube: Análise Contínua e Quality Gate  
├── class24/ — Kiwi TCMS e Bug Tracking  
├── class25/ — CI/CD  
├── class26/ — DevOps e GitHub Actions  
├── class27/ — Estimativa de Software e Métricas de Processo/Projeto  
├── class28/ — Gestão de Configuração, Versionamento e Manutenção  
├── class29/ — Projeto Integrador Final  
└── class30/ — Ética, Boas Práticas e Encerramento  
```  

---  

## class01 — Boas-vindas e Fundamentos da Qualidade  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS01-ProblemasDeSoftware-Dinamica.txt` | Dinâmica em grupos: identificar problemas em softwares conhecidos (origem: GQS0001 4M) |  

---  

## class11 — Inspeção, Revisão e Auditoria  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS11-Requisitos-Dinamica.txt` | Dinâmica de requisitos de software (origem: GQS0102 4M) |  
| `GQS11-EXGA03-Requisitos.txt` | Exercício EXGA03 — 7 perguntas sobre requisitos (origem: GQS0106 4M) |  

---  

## class12 — Fundamentos de Automação de Testes  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS12-CicloVida-TestesAutomacao.png` | Ciclo de vida de testes de automação (imagem original 4M) |  

---  

## class13 — JUnit: Testes Unitários em Java  

**Pré-requisitos:** Java 17+, Maven  
**Como usar:** `cd class13/java && mvn test`  

### java/  

| Diretório | Conteúdo | Exercício |  
|---|---|---|  
| `ex01_calculadora_basica/` | `Calculadora.java` | Operações básicas: somar, subtrair, multiplicar, dividir, exceção divisão por zero |  
| `ex01_calculadora_basica_test/` | `CalculadoraTest.java` | Testes JUnit 5 para a Calculadora |  
| `ex02_validador_string/` | `ValidadorString.java` | Validação de strings: nulo, vazio, tamanho |  
| `ex02_validador_string_test/` | `ValidadorStringTest.java` | Testes JUnit para ValidadorString |  
| `ex03_contador_com_estado/` | `Contador.java` | Contador com incremento/decremento/reset |  
| `ex03_contador_com_estado_test/` | `ContadorTest.java` | Testes com estado e @BeforeEach |  
| `ex04_processador_de_lista/` | `ProcessadorLista.java` | Filtrar, ordenar, mapear listas |  
| `ex04_processador_de_lista_test/` | `ProcessadorListaTest.java` | Testes com coleções |  
| `ex05_conversor_de_temperatura/` | `ConversorTemperatura.java` | Celsius ↔ Fahrenheit ↔ Kelvin |  
| `ex05_conversor_de_temperatura_test/` | `ConversorTemperaturaTest.java` | Testes parametrizados com @CsvSource |  
| `pom.xml` | Configuração Maven + JUnit 5 + JaCoCo | Build do projeto |  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS13-pom.xml` | Cópia do pom.xml para referência |  

---  

## class15 — Mocks, Stubs e Doubles (Mockito)  

**Pré-requisitos:** Java 17+, Maven (pom.xml incluído)  

### java/ — Exercícios com Mockito  

| Diretório | Sistema | Interfaces mockadas |  
|---|---|---|  
| `ex06_sistema_de_notificacao/` | Sistema de notificação | `ServicoEmail` |  
| `ex06..._test/` | Teste: verificar se email foi enviado corretamente | `verify(servicoEmail).enviar(...)` |  
| `ex07_carrinho_de_compras/` | Carrinho + CalculadoraDesconto | `CalculadoraDesconto` |  
| `ex07..._test/` | Stub de desconto, verificar total | `when(...).thenReturn(...)` |  
| `ex08_autenticador_de_usuario/` | Autenticação | `RepositorioUsuario`, `CriptografadorSenha` |  
| `ex08..._test/` | Login sucesso/falha com mocks | `@Mock`, `@InjectMocks` |  
| `ex09_processador_de_pagamento/` | Gateway de pagamento | `GatewayPagamento`, `ServicoLog` |  
| `ex09..._test/` | Stub de gateway, verificar log | Multiple mocks |  
| `ex10_gerenciador_de_estoque/` | Estoque | `RepositorioProduto` |  
| `ex10..._test/` | CRUD com mock de repositório | |  
| `ex11_sistema_de_cache/` | Cache com repositório | `RepositorioDados` |  
| `ex11..._test/` | Cache miss/hit com spy | `@Spy` |  

### java/ — Exemplos base com.tdd.tddmock (originais)  

| Diretório | Conteúdo |  
|---|---|  
| `original_authentication/` | `AuthenticationService.java`, `LoginManager.java` |  
| `original_calculadora/` | `Calculadora.java`, `AulaTDDMock.java` (exemplo de aula) |  
| `original_email/` | `EmailService.java`, `EmailManager.java` |  
| `original_order/` | `Order.java`, `OrderService.java`, `OrderManager.java` |  
| `original_stockmanager/` | `Stock.java`, `StockService.java`, `StockManager.java` |  
| `original_taskmanager/` | `Task.java`, `TaskService.java`, `TaskManager.java` |  
| `original_usermanager/` | `User.java` |  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS15-20Exercicios-Enunciados.md` | 20 enunciados de testes JUnit + Mockito |  
| `GQS15-20Exercicios-Solucoes.md` | 20 exercícios com soluções completas |  
| `GQS15-ExemploInicialComMock.md` | Exemplo inicial de uso de Mockito |  

---  

## class16 — TDD — Desenvolvimento Orientado a Testes  

**Pré-requisitos:** Java 17+, Maven  

### java/ — Exercícios TDD (ex12-20)  

| Diretório | Sistema | Complexidade |  
|---|---|---|  
| `ex12_processador_de_pedidos/` | Processador de pedidos + frete + validação | Média |  
| `ex12..._test/` | TDD: verificar cálculo, entrega, validação | |  
| `ex13_sistema_de_backup/` | Backup local + nuvem | Média |  
| `ex13..._test/` | TDD: mock de armazenamentos | |  
| `ex14_processador_de_relatorios/` | Relatórios com coleta de dados + email | Média |  
| `ex14..._test/` | TDD: mock de coletores e formatadores | |  
| `ex15_sistema_de_autenticacao_multi_factor/` | MFA com SMS, token, credenciais | Alta |  
| `ex15..._test/` | TDD: múltiplos stubs e verificações | |  
| `ex16_sistema_de_reservas/` | Reservas de quartos + pagamento + notificação | Alta |  
| `ex16..._test/` | TDD: integração de múltiplos mocks | |  
| `ex17_analisador_de_log/` | Analisador de logs + estatísticas | Média |  
| `ex17..._test/` | TDD: parsing de logs | |  
| `ex18_sistema_de_monitoramento/` | Monitor de métricas + alertas | Alta |  
| `ex18..._test/` | TDD: spy em métricas, mock de alertas | |  
| `ex19_sistema_de_workflow/` | Workflow com etapas + validadores | Alta |  
| `ex19..._test/` | TDD: workflow steps | |  
| `ex20_sistema_de_processamento_de_arquivos_integracao_complexa/` | Processamento, compactação, upload | Muito Alta |  
| `ex20..._test/` | TDD: integração complexa com múltiplos mocks | |  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS16-TDD-Enunciados.txt` | Enunciados originais TDD JUnit Mockito (4M) |  
| `GQS16-TDD-Calculadora-JUnit-Completo.txt` | Exemplo completo Calculadora com todos os casos JUnit (186 linhas) |  
| `GQS16-TDD-Exercicios-ChatGPT2.txt` | Exercícios TDD detalhados — série 2 (792 linhas) |  
| `GQS16-TDD-Exercicios-ChatGPT3-Detalhado.txt` | Exercícios TDD detalhados — série 3 mais complexa (1406 linhas) |  
| `GQS16-TDD-Notas-Curso.txt` | Notas do curso sobre xUnit, FIRST, anotações JUnit |  
| `GQS16-TDD-Referencia.txt` | Referência rápida TDD |  

---  

## class17 — Selenium WebDriver  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS17-Selenium-Labs-Python.md` | Labs completos Selenium Python (pytest + webdriver-manager) |  
| `GQS17-Selenium-Labs-Java.md` | Labs equivalentes Selenium Java (JUnit + WebDriverManager) |  
| `GQS17-Selenium-Info.txt` | Informações gerais sobre Selenium |  
| `GQS17-Tutorial-Google-Selenium.txt` | Tutorial: pesquisa no Google com Selenium |  
| `GQS17-Tutorial-Login-Selenium.txt` | Tutorial: teste de login com Selenium |  
| `GQS17-Tutorial-Amazon-Selenium.txt` | Tutorial: busca na Amazon com Selenium |  

---  

## class18 — FitNesse: Testes de Aceitação  

### java/  

| Arquivo | Descrição |  
|---|---|  
| `GQS1801-BankAccountFixture.java` | Fixture Java para conta bancária (Lab 1 — inglês) |  
| `GQS1802-ContaBancariaFixture.java` | Fixture Java para conta bancária (Lab 1 — português) |  
| `GQS1803-SistemaAcademiaFixture.java` | Fixture Java para sistema de academia (Lab 2) |  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS18-Lab1-Enunciado.txt` | Enunciado do Lab 1 (sistema bancário, 10 RFs) |  
| `GQS18-Lab1-Resolvido.txt` | Solução completa do Lab 1 |  
| `GQS18-Lab2-Enunciado.txt` | Enunciado do Lab 2 (sistema de academia) |  
| `GQS18-Lab2-Resolvido.txt` | Solução completa do Lab 2 |  
| `GQS18-Lab1-DiagramaClasse.plantuml` | Diagrama de classes Lab 1 (PlantUML) |  
| `GQS18-Lab2-DiagramaClasse.plantuml` | Diagrama de classes Lab 2 (PlantUML) |  
| `GQS18-FitNesse-Labs-Completo.md` | Todos os laboratórios FitNesse documentados |  
| `GQS18-docker-compose-testlink.yml` | Docker Compose para TestLink — **referência histórica**; o laboratório da Aula 24 usa `class24/diagrams/GQS24-kiwi-docker-compose.yml` |  
| `GQS18-FitNesse-TesteGeral.wiki` | Página wiki real usada na aula 4M (TesteGeral) |  
| `GQS18-FitNesse-RegistroVideoDigital.wiki` | Página wiki real usada na aula 4M (RegistroDeVideo) |  
| `GQS18-FitNesse-FrontPage.txt` | FrontPage original do FitNesse da aula 4M |  
| `GQS18-fitnesse-start.sh` | Script shell para iniciar FitNesse no Linux/Mac |  
| `GQS18-fitnesse-start.bat` | Script batch para iniciar FitNesse no Windows |  

---  

## class19 — Postman: Testes de API REST  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS19-1Users.js` | Script Postman: CRUD de usuários (1 usuário) |  
| `GQS19-2Users.js` | Script Postman: CRUD de usuários (2 usuários) |  
| `GQS19-1Users-Enunciado.txt` | Enunciado Lab 1 Postman |  
| `GQS19-1Users-Postman.txt` | Especificação dos testes Postman Lab 1 |  
| `GQS19-2Users-Enunciado.txt` | Enunciado Lab 2 Postman |  
| `GQS19-2Users-Postman.txt` | Especificação dos testes Postman Lab 2 |  

---  

## class20 — JMeter: Testes de Performance  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS20-ConsultaCEP.jmx` | Plano de teste JMeter: consulta de CEP nos Correios |  
| `GQS20-GrupoUsuarios.jmx` | Plano de teste JMeter: grupo de usuários concorrentes |  
| `GQS20-JMeter-Config.txt` | Configuração e dicas de uso do JMeter (proxy Chrome) |  
| `GQS20-JMeter-20Enunciados.md` | 20 enunciados completos de performance (com soluções) |  
| `GQS20-info.txt` | Informações gerais de download e configuração |  
| `jmeter_lab/` | API REST Flask para laboratório de JMeter |  
| `jmeter_lab/api/` | Endpoints da API (requirements.txt + run.sh) |  

---  

## class21 — SikuliX: Testes de Interface  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS21-SikuliX-Enunciado.txt` | 10 enunciados de testes SikuliX (Bloco de Notas, Calculadora, Browser...) |  
| `GQS21-SikuliX-Solucao.txt` | Scripts de solução dos 10 enunciados (Jython/SikuliX) |  
| `GQS21-ComandosSikuliX.md` | Tabela completa de todos os comandos SikuliX com exemplos |  
| `GQS21-SikuliX-Links-Download.txt` | Links de download do SikuliX para Windows, Linux e Mac |  
| `GQS21-SikuliX-Solucao.txt` | Scripts de solução dos 10 enunciados |  
| `GQS21-ComandosSikuliX.md` | Tabela completa de comandos SikuliX |  

---  

## class21B — Testes Mobile: Maestro e Appium  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS21B-TesteMobile-Enunciado.txt` | 10 enunciados de testes mobile (Appium e/ou Maestro) |  
| `GQS21B-TesteMobile-Solucao.txt` | Scripts de solução dos 10 enunciados (flows Maestro + trechos Appium) |  
| `GQS21B-ComandosAppiumMaestro.md` | Tabela completa de comandos do Appium (client) e do Maestro (flow YAML/CLI) |  
| `GQS21B-ArquiteturaAppiumMaestro.mmd` | Diagrama Mermaid da arquitetura Appium (client/server/drivers) e Maestro (CLI) |  
| `GQS21B-Maestro-Login-Flow.yaml` | Flow Maestro de exemplo — login com validação de mensagem de boas-vindas |  
| `GQS21B-Appium-Maestro-Links-Download.txt` | Links de instalação do Appium e do Maestro (CLI, drivers, pré-requisitos) |  

### java/ e python/  

| Arquivo | Descrição |  
|---|---|  
| `GQS21B01-Appium_Conceitos.java` | Exemplo conceitual em Java de sessão Appium (mock, sem dependências externas) |  
| `GQS21B01-Appium_Conceitos.py` | Exemplo conceitual em Python de sessão Appium (mock, sem dependências externas) |  

---  

## class23B — SonarQube: Análise Contínua e Quality Gate  

Laboratório completo da Aula 23B. Todo o material foi executado contra um **SonarQube Community Build 26.9** real antes de ser publicado: os números citados nos slides e no gabarito são medições.  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS23B-Lab-Enunciado.md` | Roteiro do laboratório em 7 etapas, com os números de referência de cada etapa |  
| `GQS23B-Lab-Solucao.md` | Gabarito: código corrigido (compilado e analisado), antes/depois medido e erros mais comuns |  
| `GQS23B-ArquiteturaSonarQube.mmd` | Diagrama Mermaid: scanner, servidor, Compute Engine, Quality Gate e pipeline |  
| `GQS23B-FluxoCleanAsYouCode.mmd` | Diagrama Mermaid: refatorar tudo × limpar o que se toca, e a armadilha do gate verde |  
| `GQS23B-docker-compose.yml` | SonarQube Community Build + PostgreSQL, com volumes nomeados |  
| `GQS23B-.env.example` | Modelo do `.env` do compose (a senha nunca vai versionada) |  
| `GQS23B-criar-quality-gate.sh` | Cria o gate "GQS — Código Legado" e o associa ao projeto, via Web API |  
| `GQS23B-github-actions-sonar.yml` | Workflow com jobs Java e Python, incluindo o passo de Quality Gate |  
| `GQS23B-Jenkinsfile` | Pipeline declarativo com `withSonarQubeEnv` e `waitForQualityGate` |  
| `GQS23B-Links-Download.txt` | Downloads, extensões de IDE, actions e documentação oficial |  

### java/  

Projeto Maven no layout padrão (`src/main/java`, `src/test/java`), executável.  

| Arquivo | Descrição |  
|---|---|  
| `pom.xml` | JUnit 5, JaCoCo 0.8.13 e `sonar-maven-plugin`, com o caminho do `jacoco.xml` já configurado |  
| `src/main/java/GQS23B01-Biblioteca.java` | Sistema de empréstimo com 10 defeitos propositais, cada um mapeado à regra Sonar que dispara |  
| `src/test/java/GQS23B01-BibliotecaTest.java` | Suíte verde e incompleta (46,2% de cobertura de linha) — o contraste central da aula |  

```bash  
cd class23B/java  
mvn clean verify                              # 9 testes verdes + jacoco.xml  
mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN    # 20 problemas, 44,7% de cobertura  
```  

### python/  

| Arquivo | Descrição |  
|---|---|  
| `GQS23B01-Biblioteca.py` | O mesmo programa e os mesmos defeitos, em Python idiomático |  
| `test_GQS23B01-Biblioteca.py` | Suíte espelhada da versão Java (import via `importlib`, por causa do hífen no nome) |  
| `sonar-project.properties` | Configuração do SonarScanner CLI, com o caminho do `coverage.xml` |  

```bash  
cd class23B/python  
pytest --cov=. --cov-report=xml  
docker run --rm --network=host -e SONAR_TOKEN=$SONAR_TOKEN -v "$PWD:/usr/src" sonarsource/sonar-scanner-cli  
# 7 problemas, 50,0% de cobertura  
```  

---  

## class24 — Kiwi TCMS e Bug Tracking

### diagrams/ — ambiente e scripts do Kiwi TCMS

| Arquivo | Descrição |
|---|---|
| `GQS24-kiwi-docker-compose.yml` | Kiwi TCMS 16.3 + MariaDB 11; portas e senhas parametrizadas pelo `.env` |
| `GQS24-kiwi-.env.example` | Modelo do `.env` (senhas do banco e portas alternativas) |
| `GQS24-kiwi-api-exemplo.py` | Monta a hierarquia inteira pela API (`tcms-api`), com estratégia *get or create* |
| `GQS24-kiwi-registrar-defeito.py` | Escala de severidade, defeito vinculado à execução e leitura da telemetria |
| `GQS24-kiwi-publicar-resultados.py` | Publica um `junit.xml` no Kiwi usando o plugin oficial, com correção do carimbo de tempo do pytest 8 |
| `GQS24-Lab-KiwiTCMS-Enunciado.md` | Roteiro do laboratório em 8 etapas (95-115 min) |
| `GQS24-Lab-KiwiTCMS-Solucao.md` | Gabarito com os números medidos (uso do professor) |
| `GQS24-D02-FluxoKiwiTCMS.mmd` | Fluxo requisito → caso → execução → defeito → novo build |
| `GQS24-D03-HierarquiaKiwiTCMS.mmd` | Hierarquia de objetos do Kiwi TCMS |

### diagrams/ — material histórico do TestLink

Mantido como referência: o TestLink continua em uso em muitas empresas e os conceitos que ele consagrou são os mesmos do Kiwi TCMS, com outros nomes. **Não é o laboratório da aula** — a ferramenta adotada na disciplina é o Kiwi TCMS.

| Arquivo | Descrição |
|---|---|
| `GQS24-docker-compose.yml` | Docker Compose para TestLink 2.0 (v1) |
| `GQS24-docker-compose-v2.yml` | Docker Compose para TestLink 2.0 (v2 atualizado) |
| `GQS24-.env.example` | Variáveis de ambiente para TestLink |
| `GQS24-Tutorial-TestLink-TechStore.md` | Tutorial completo TestLink 2.0 — passo a passo e-commerce TechStore |
| `GQS24-D01-flowchart.mmd` | Fluxo TestLink × Jira |

### python/

| Arquivo | Descrição |
|---|---|
| `GQS2401-RelatorioDefeitos.py` | Relatório de defeitos por severidade e prioridade |
| `test_GQS2402-LoginTechStore.py` | Suíte pytest espelhando TS-001/TS-002/TS-003; publicada no Kiwi via JUnit XML |

### java/

| Arquivo | Descrição |
|---|---|
| `GQS2401-RelatorioDefeitos.java` | Versão Java do relatório de defeitos |

**Como iniciar o Kiwi TCMS:**
```bash
cd class24/diagrams
cp GQS24-kiwi-.env.example .env          # e troque as senhas dentro do .env
docker compose -f GQS24-kiwi-docker-compose.yml up -d
docker exec -it kiwi_web /Kiwi/manage.py initial_setup    # interativo: o -it e obrigatorio
# Acesse: https://localhost:8443  (certificado autoassinado — aceite o aviso)
```

**Encerrar:**
```bash
docker compose -f GQS24-kiwi-docker-compose.yml down -v
```

---

## class25 — CI/CD  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS25-CICD-Config.txt` | Configuração completa de pipeline CI/CD (docker, ssh, deploy) |  
| `GQS25-pipeline-config.txt` | Cópia adicional do arquivo de config |  
| `GQS25-docker-compose.yml` | Docker Compose do ambiente CI/CD |  
| `GQS25-config.yaml` | Configuração YAML do pipeline (stages, jobs) |  

---  

## class26 — DevOps e GitHub Actions  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS26-CICD-README.txt` | README do projeto CI/CD |  
| `GQS26-config.yaml` | Configuração do pipeline GitHub Actions |  
| `GQS26-docker-compose.yml` | Docker Compose de produção |  
| `GQS26-ssh-deploy.sh` | Script de deploy via SSH |  
| `GQS26-sample-web-app.zip` | Aplicação web de exemplo para o pipeline |  

---  

## class27 — Estimativa de Software e Métricas de Processo/Projeto  

**Origem:** conteúdo original desta expansão (UA8), não reaproveitado do 4M — criado para cobrir os itens de estimativa/pontos de função do plano de ensino `Gestão e qualidade de software.pdf` (UC 0016257).  

### java/ + python/  

| Arquivo | Descrição |  
|---|---|  
| `GQS2701-CalculadoraPontosFuncao.java` | Calculadora de Pontos de Função (ILF/EIF/EI/EO/EQ, fator de ajuste, PF ajustado) e estimativa COCOMO — executável |  
| `GQS2701-CalculadoraPontosFuncao.py` | Mesma calculadora em Python — executável, mesmos resultados do Java |  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS27-ProcessoEstimativa.mmd` | Diagrama Mermaid do processo de estimativa de software |  

---  

## class28 — Gestão de Configuração, Versionamento e Manutenção  

**Origem:** conteúdo original desta expansão (UA8), não reaproveitado do 4M — cobre gestão de configuração, controle de versão (Git) e manutenção/reengenharia de software do plano de ensino `Gestão e qualidade de software.pdf` (UC 0016257).  

### java/ + python/  

| Arquivo | Descrição |  
|---|---|  
| `GQS2801-SimuladorVersionamento.java` | Simulador de histórico de commits/baseline e versionamento semântico (SemVer) — executável |  
| `GQS2801-SimuladorVersionamento.py` | Mesmo simulador em Python — executável |  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS28-BranchingStrategy.mmd` | Diagrama Mermaid (`gitGraph`) de estratégia de branching (Git Flow × Trunk-Based) |  
| `GQS28-ProcessoManutencao.mmd` | Diagrama Mermaid do processo de manutenção/reengenharia de software |  

---  

## class29 — Projeto Integrador Final  

**Origem:** renumerado de `class27` (posição original antes da expansão UA8); conteúdo inalterado.  

### java/ + python/  

| Arquivo | Descrição |  
|---|---|  
| `GQS2901-ChecklistQualidade.java` | Checklist de qualidade para o projeto final — executável |  
| `GQS2901-ChecklistQualidade.py` | Mesmo checklist em Python — executável |  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS29-MapaMentalTeste.mmd` | Mapa mental de testes (revisão para o projeto integrador) |  

---  

## class30 — Ética, Boas Práticas e Encerramento  

**Origem:** renumerado de `class28` (posição original antes da expansão UA8); conteúdo inalterado.  

### java/ + python/  

| Arquivo | Descrição |  
|---|---|  
| `GQS3001-CodeReviewChecklist.java` | Checklist interativo de code review — executável |  
| `GQS3001-CodeReviewChecklist.py` | Mesmo checklist em Python — executável |  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS30-MapaMentalCurso.mmd` | Mapa mental completo do curso (encerramento) |  

---  

## Resumo Geral  

| Class | Aula | Arquivos | Tipo de conteúdo |  
|---|---|---|---|  
| class01 | 01 — Fundamentos Qualidade | 1 | TXT dinâmica |  
| class11 | 11 — Inspeção/Revisão | 2 | TXT exercícios de requisitos |  
| class12 | 12 — Fundamentos Automação | 1 | Imagem PNG |  
| class13 | 13 — JUnit | 12 | Java (5 exercícios + tests + pom.xml) |  
| class15 | 15 — Mocks/Mockito | 47 | Java (6 exercícios + tests + exemplos base + md) |  
| class16 | 16 — TDD | 65 | Java (9 exercícios TDD + tests + 6 TXT/notas TDD) |  
| class17 | 17 — Selenium | 6 | Markdown labs + TXT tutoriais |  
| class18 | 18 — FitNesse | 16 | Java fixtures + TXT labs + PlantUML + Docker + wiki pages + scripts |  
| class19 | 19 — Postman | 6 | JS scripts + TXT enunciados/specs |  
| class20 | 20 — JMeter | 17 | JMX plans + MD enunciados + API lab Flask |  
| class21 | 21 — SikuliX | 4 | TXT enunciados/soluções + MD comandos + links download |  
| class21B | 21B — Mobile (Maestro/Appium) | 8 | TXT enunciados/soluções + MD comandos + Mermaid + YAML flow + links download + Java/Python |  
| class23B | 23B — SonarQube | 16 | Projeto Maven (src + tests + pom) + Python (módulo + testes + properties) + 2 Mermaid + Compose + .env + script de gate + workflow + Jenkinsfile + roteiro/gabarito |  
| class24 | 24 — Kiwi TCMS | 17 | Compose Kiwi + .env + 3 scripts de API + roteiro/gabarito + suíte pytest + 3 Mermaid + material histórico do TestLink |  
| class25 | 25 — CI/CD | 4 | YAML configs + TXT pipeline |  
| class26 | 26 — DevOps | 5 | YAML + Docker + Shell script + zip |  
| class27 | 27 — Estimativa/Pontos de Função | 3 | Python + Java (calculadora PF/COCOMO) + Mermaid |  
| class28 | 28 — Gestão de Configuração/Versionamento | 4 | Python + Java (simulador versionamento) + 2 Mermaid |  
| class29 | 29 — Projeto Integrador Final | 3 | Python + Java (checklist qualidade) + Mermaid (renumerado de class27) |  
| class30 | 30 — Ética e Encerramento | 3 | Python + Java (checklist code review) + Mermaid (renumerado de class28) |  
| **Total** | | **240** | |  
