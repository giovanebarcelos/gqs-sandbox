# Repositório de Código — Garantia da Qualidade de Software  

**Curso:** Garantia da Qualidade de Software | **Período:** 2026/2  
**Origem do material:** adaptado de `/2025-02/4M - Gestão e Qualidade de Software - FAPA`  

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
├── class24/ — TestLink e Bug Tracking  
├── class25/ — CI/CD  
└── class26/ — DevOps e GitHub Actions  
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
| `GQS18-docker-compose-testlink.yml` | Docker Compose para TestLink (compartilhado) |  
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

## class24 — TestLink e Bug Tracking  

### diagrams/  

| Arquivo | Descrição |  
|---|---|  
| `GQS24-docker-compose.yml` | Docker Compose para TestLink 2.0 (v1) |  
| `GQS24-docker-compose-v2.yml` | Docker Compose para TestLink 2.0 (v2 atualizado) |  
| `GQS24-.env.example` | Variáveis de ambiente para TestLink |  
| `GQS24-Tutorial-TestLink-TechStore.md` | Tutorial completo TestLink 2.0 — passo a passo e-commerce TechStore |  

**Como iniciar TestLink:**  
```bash  
cd class24/diagrams  
cp GQS24-.env.example .env  
docker-compose -f GQS24-docker-compose-v2.yml up -d  
# Acesse: http://localhost:8181 | admin / admin123  
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
| class24 | 24 — TestLink | 4 | Docker Compose + .env + Tutorial MD |  
| class25 | 25 — CI/CD | 4 | YAML configs + TXT pipeline |  
| class26 | 26 — DevOps | 5 | YAML + Docker + Shell script + zip |  
| **Total** | | **191** | |  
