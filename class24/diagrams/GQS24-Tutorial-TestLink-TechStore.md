# Tutorial Completo TestLink 2.0.0 - Passo a Passo TechStore  
  
## Projeto Exemplo: Sistema de E-commerce TechStore  
  
Vamos gerenciar os testes de um sistema de e-commerce com funcionalidades de catálogo, carrinho, checkout e área do cliente.  
  
---  
  
## 1. Configuração Inicial do Projeto  
  
### 1.1 Criar um Novo Test Project  
  
1. Acesse o TestLink e faça login  
2. Clique em **"Test Project Management"** no menu superior direito  
3. Clique em **"Create"**  
4. Preencha os campos:  
   - **Name**: TechStore  
   - **Test Case Prefix**: TS (aparecerá em todos os casos de teste)  
   - **Description**: Sistema de E-commerce para venda de produtos tecnológicos  
   - **Active**: ✓ (marcado)  
   - **Public**: ✓ (para permitir acesso de todos)  
   - **Requirements**: ✓ (habilita gestão de requisitos)  
   - **Priority**: ✓ (habilita priorização de testes)  
   - **Automation**: ✓ (habilita marcação de testes automatizados)  
   - **Inventory**: ✓ (habilita inventário de recursos)  
5. Clique em **"Create"**  
  
### 1.2 Configurar Palavras-chave (Keywords)  
  
1. Vá em **"Keywords Management"** (menu lateral esquerdo)  
2. Crie as seguintes keywords:  
   - **smoke**: Para testes críticos de fumaça  
   - **regressao**: Para testes de regressão  
   - **integracao**: Para testes de integração  
   - **funcional**: Para testes funcionais  
   - **critico**: Para funcionalidades críticas  
  
**Como criar cada keyword:**  
  
- Clique em **"Create"**  
- Digite o nome e descrição  
- Clique em **"Save"**  
  
---  
  
## 2. Gerenciamento de Usuários e Papéis  
  
### 2.1 Criar Usuários  
  
1. Acesse **"Users"** no menu superior direito  
2. Clique em **"Create"**  
3. Crie os seguintes usuários:  
  
**Testador Júnior:**  
  
- Login: testador.junior  
- First Name: João  
- Last Name: Silva  
- Email: joao.silva@techstore.com  
- Role: tester  
  
**Testador Sênior:**  
  
- Login: testador.senior  
- First Name: Maria  
- Last Name: Santos  
- Email: maria.santos@techstore.com  
- Role: senior tester  
  
**Test Lead:**  
  
- Login: test.lead  
- First Name: Carlos  
- Last Name: Oliveira  
- Email: carlos.oliveira@techstore.com  
- Role: test lead  
  
### 2.2 Atribuir Usuários ao Projeto  
  
1. Vá em **"Test Project Management"**  
2. Selecione o projeto **TechStore**  
3. Clique em **"Assign Users"**  
4. Adicione todos os usuários criados  
  
---  
  
## 3. Estrutura de Test Suite (Suítes de Teste)  
  
### 3.1 Criar Hierarquia de Test Suites  
  
1. Selecione o projeto **TechStore** no topo da página  
2. Vá em **"Test Specification"** no menu lateral esquerdo  
3. Clique no ícone de pasta **"Create Test Suite"** ao lado do projeto  
  
**Estrutura a criar:**  
  
```  
TechStore  
├── Módulo: Autenticação e Cadastro  
│   ├── Login  
│   ├── Cadastro de Usuário  
│   └── Recuperação de Senha  
├── Módulo: Catálogo de Produtos  
│   ├── Busca de Produtos  
│   ├── Filtros e Ordenação  
│   └── Detalhes do Produto  
├── Módulo: Carrinho de Compras  
│   ├── Adicionar/Remover Produtos  
│   ├── Atualizar Quantidade  
│   └── Cálculo de Frete  
└── Módulo: Checkout e Pagamento  
    ├── Dados de Entrega  
    ├── Formas de Pagamento  
    └── Confirmação do Pedido  
```  
  
**Para cada suite:**  
  
- Clique em **"Create Test Suite"**  
- Digite o nome  
- Adicione uma descrição detalhada  
- Clique em **"Save"**  
  
---  
  
## 4. Criação de Casos de Teste Detalhados  
  
### 4.1 Caso de Teste Completo - Exemplo 1  
  
**Localização:** Módulo: Autenticação e Cadastro > Login  
  
1. Clique na suite **"Login"**  
2. Clique em **"Create Test Case"**  
3. Preencha:  
  
**Informações Básicas:**  
  
- **Test Case Title**: Login com credenciais válidas  
- **Summary**: Verificar se o usuário consegue fazer login com email e senha corretos  
- **Preconditions**:  
  - Usuário cadastrado no sistema  
  - Browser atualizado  
  - Conexão com internet ativa  
  
**Steps:**  
  
1. **Step 1:**  
   
   - **Step Actions**: Acessar a página inicial do TechStore (https://techstore.com)  
   - **Expected Results**: Página inicial carregada com sucesso, botão "Entrar" visível  
  
2. **Step 2:**  
   
   - **Step Actions**: Clicar no botão "Entrar" no canto superior direito  
   - **Expected Results**: Modal de login exibido com campos Email e Senha  
  
3. **Step 3:**  
   
   - **Step Actions**: Preencher o campo Email com: usuario@teste.com  
   - **Expected Results**: Email inserido corretamente no campo  
  
4. **Step 4:**  
   
   - **Step Actions**: Preencher o campo Senha com: Senha@123  
   - **Expected Results**: Senha inserida (oculta com asteriscos)  
  
5. **Step 5:**  
   
   - **Step Actions**: Clicar no botão "Fazer Login"  
   - **Expected Results**:  
     - Redirecionamento para página inicial logada  
     - Nome do usuário exibido no topo  
     - Menu "Minha Conta" disponível  
  
**Configurações Adicionais:**  
  
- **Importance**: High  
  
- **Execution Type**: Manual  
  
- **Estimated Exec. Duration**: 2 minutos  
  
- **Keywords**: smoke, funcional, critico  
4. Clique em **"Save"**  
  
### 4.2 Caso de Teste Completo - Exemplo 2  
  
**Localização:** Módulo: Carrinho de Compras > Adicionar/Remover Produtos  
  
**Test Case Title**: Adicionar produto ao carrinho e validar cálculo  
  
**Summary**: Verificar se o produto é adicionado corretamente e se o subtotal é calculado adequadamente  
  
**Preconditions**:  
  
- Usuário logado no sistema  
- Ao menos um produto disponível no catálogo  
- Carrinho vazio  
  
**Steps:**  
  
1. **Step 1:**  
   
   - **Actions**: Na página inicial, clicar em "Notebooks" no menu de categorias  
   - **Expected**: Lista de notebooks exibida com preços e botões "Adicionar ao Carrinho"  
  
2. **Step 2:**  
   
   - **Actions**: Selecionar o produto "Notebook Dell Inspiron 15" (preço R$ 3.499,00)  
   - **Expected**: Página de detalhes do produto aberta  
  
3. **Step 3:**  
   
   - **Actions**: Verificar informações: nome, preço, estoque disponível  
   - **Expected**:  
     - Nome: Notebook Dell Inspiron 15  
     - Preço: R$ 3.499,00  
     - Estoque: Em estoque  
  
4. **Step 4:**  
   
   - **Actions**: Alterar quantidade para 2 unidades  
   - **Expected**: Campo quantidade exibe "2"  
  
5. **Step 5:**  
   
   - **Actions**: Clicar em "Adicionar ao Carrinho"  
   - **Expected**:  
     - Mensagem "Produto adicionado com sucesso"  
     - Ícone do carrinho mostra badge com "2"  
  
6. **Step 6:**  
   
   - **Actions**: Clicar no ícone do carrinho  
   - **Expected**:  
     - Produto listado: Notebook Dell Inspiron 15  
     - Quantidade: 2  
     - Preço unitário: R$ 3.499,00  
     - Subtotal: R$ 6.998,00  
  
**Importance**: High  
**Keywords**: funcional, regressao, critico  
  
### 4.3 Caso de Teste com Cenários Alternativos  
  
**Test Case Title**: Login com credenciais inválidas  
  
**Summary**: Verificar comportamento do sistema ao tentar login com dados incorretos  
  
**Preconditions**: Acesso à página de login  
  
**Steps (Cenário 1 - Email inválido):**  
  
1. Acessar página de login  
2. Inserir email inválido: "emailinvalido@"  
3. Inserir senha válida  
4. Clicar em "Fazer Login"  
5. **Expected**: Mensagem "Email inválido. Verifique o formato."  
  
**Steps (Cenário 2 - Senha incorreta):**  
  
1. Acessar página de login  
2. Inserir email válido: usuario@teste.com  
3. Inserir senha incorreta: "SenhaErrada123"  
4. Clicar em "Fazer Login"  
5. **Expected**: Mensagem "Email ou senha incorretos. Tente novamente."  
  
**Steps (Cenário 3 - Campos vazios):**  
  
1. Acessar página de login  
2. Deixar campos Email e Senha vazios  
3. Clicar em "Fazer Login"  
4. **Expected**: Mensagens de validação nos campos obrigatórios  
  
**Importance**: Medium  
**Keywords**: funcional, regressao  
  
---  
  
## 5. Gestão de Requisitos  
  
### 5.1 Criar Especificação de Requisitos  
  
1. Vá em **"Requirements Specification"** no menu lateral  
2. Clique em **"Create"**  
3. Crie a especificação:  
   - **Title**: Requisitos Funcionais - TechStore v1.0  
   - **Scope**: Requisitos funcionais do sistema de e-commerce  
   - **Type**: 2 - Use Case  
  
### 5.2 Criar Requisitos  
  
Na especificação criada, adicione requisitos:  
  
**Requisito 1:**  
  
- **Req Doc ID**: REQ-001  
- **Title**: Sistema de Login  
- **Coverage**: Permitir que usuários façam login com email e senha  
- **Type**: Feature  
- **Status**: Valid  
- **Expected Coverage**: 100%  
  
**Requisito 2:**  
  
- **Req Doc ID**: REQ-002  
- **Title**: Gerenciamento de Carrinho  
- **Coverage**: Usuários devem adicionar, remover e atualizar produtos no carrinho  
- **Type**: Feature  
- **Status**: Valid  
  
**Requisito 3:**  
  
- **Req Doc ID**: REQ-003  
- **Title**: Processo de Checkout  
- **Coverage**: Finalização de compra com cálculo de frete e impostos  
- **Type**: Feature  
- **Status**: Valid  
  
### 5.3 Vincular Requisitos aos Casos de Teste  
  
1. Vá em **"Test Specification"**  
2. Selecione um caso de teste (ex: "Login com credenciais válidas")  
3. Clique na aba **"Requirements"**  
4. Clique em **"Assign Requirements"**  
5. Selecione **REQ-001** e clique em **"Assign"**  
6. Repita para outros casos de teste  
  
---  
  
## 6. Planos de Teste (Test Plans)  
  
### 6.1 Criar Test Plan  
  
1. Vá em **"Test Plan Management"**  
2. Clique em **"Create"**  
3. Preencha:  
   - **Name**: Sprint 1 - Módulo Autenticação  
   - **Description**: Plano de testes para validação do módulo de autenticação na Sprint 1  
   - **Active**: ✓  
   - **Public**: ✓  
  
### 6.2 Adicionar Casos de Teste ao Plano  
  
1. Com o Test Plan selecionado, vá em **"Add/Remove Test Cases"**  
2. Navegue pela árvore de Test Suites  
3. Marque os casos de teste desejados (ex: todos da suite "Login")  
4. Clique em **"Add Selected Test Cases"**  
  
### 6.3 Criar Builds  
  
1. Vá em **"Builds/Releases"** no menu lateral  
2. Clique em **"Create"**  
3. Crie builds:  
  
**Build 1:**  
  
- **Build Name**: v1.0.0-RC1  
- **Build Notes**: Release Candidate 1 - Módulo de autenticação completo  
- **Active**: ✓  
- **Open**: ✓  
  
**Build 2:**  
  
- **Build Name**: v1.0.0-RC2  
- **Build Notes**: Correções de bugs do RC1  
- **Active**: ✓  
- **Open**: ✓  
  
### 6.4 Configurar Milestones  
  
1. Vá em **"Milestones"**  
2. Clique em **"Create"**  
3. Configure:  
   - **Name**: Release 1.0  
   - **Date**: [Defina uma data]  
   - **Target Date**: [Defina data alvo]  
   - **Description**: Primeira versão estável do TechStore  
  
---  
  
## 7. Atribuição e Execução de Testes  
  
### 7.1 Atribuir Testes aos Testadores  
  
1. Selecione o Test Plan  
2. Vá em **"Assign Test Case Execution"**  
3. Selecione um Build (ex: v1.0.0-RC1)  
4. Selecione os casos de teste  
5. Escolha o testador (ex: João Silva - testador.junior)  
6. Clique em **"Assign"**  
  
### 7.2 Executar Casos de Teste  
  
1. Vá em **"Test Execution"** no menu lateral  
2. Selecione o Test Plan e Build  
3. Visualize os testes atribuídos a você  
4. Clique em um caso de teste  
5. Execute passo a passo:  
   - Leia cada step  
   - Execute a ação  
   - Verifique o resultado esperado  
   - Marque o resultado:  
     - **Passed** ✓ (verde)  
     - **Failed** ✗ (vermelho)  
     - **Blocked** ⊘ (amarelo)  
     - **Not Run** − (cinza)  
  
### 7.3 Registrar Falha com Bug  
  
1. Durante a execução, se encontrar uma falha, selecione **Failed**  
2. Preencha **Notes**: "Mensagem de erro não está sendo exibida corretamente"  
3. Na seção **Bug Management**, clique em **"Create New Issue"**  
4. Se integrado com Jira/Bugzilla, preencha:  
   - **Summary**: Mensagem de erro incorreta no login  
   - **Description**: Ao inserir senha errada, a mensagem exibida é genérica  
   - **Priority**: High  
   - **Assignee**: Desenvolvedor responsável  
5. Clique em **"Save"**  
  
### 7.4 Adicionar Anexos  
  
Durante a execução, você pode anexar:  
  
- **Screenshots**: Clique em "Attachments" > "Upload File"  
- **Logs**: Anexe arquivos de log da aplicação  
- **Vídeos**: Grave a execução e anexe  
  
---  
  
## 8. Plataformas (Platforms)  
  
### 8.1 Criar Plataformas  
  
1. Vá em **"Platforms"** (dentro do Test Plan)  
2. Crie as seguintes plataformas:  
  
**Plataforma 1:**  
  
- **Name**: Windows 10 + Chrome  
- **Notes**: Browser Chrome versão 120+  
  
**Plataforma 2:**  
  
- **Name**: macOS + Safari  
- **Notes**: Safari versão 17+  
  
**Plataforma 3:**  
  
- **Name**: Android + Chrome Mobile  
- **Notes**: Android 12+ com Chrome Mobile  
  
### 8.2 Atribuir Plataformas aos Testes  
  
1. Em **"Add/Remove Platforms"**  
2. Selecione os casos de teste  
3. Marque as plataformas desejadas  
4. Salve  
  
Agora cada teste pode ser executado em múltiplas plataformas, e você verá resultados separados por plataforma.  
  
---  
  
## 9. Custom Fields (Campos Personalizados)  
  
### 9.1 Criar Custom Fields  
  
1. Vá em **"Define Custom Fields"** (menu superior direito > Custom Fields)  
2. Clique em **"Create"**  
  
**Campo 1 - Ambiente de Teste:**  
  
- **Name**: ambiente_teste  
- **Label**: Ambiente de Teste  
- **Type**: Dropdown List  
- **Possible Values**: Desenvolvimento|Homologação|Produção  
- **Available for**: Test Cases, Test Plan, Test Execution  
- **Display on Test Execution**: ✓  
  
**Campo 2 - Criticidade de Negócio:**  
  
- **Name**: criticidade_negocio  
- **Label**: Criticidade de Negócio  
- **Type**: Dropdown List  
- **Possible Values**: Baixa|Média|Alta|Crítica  
- **Available for**: Test Cases  
  
**Campo 3 - Tempo Real de Execução:**  
  
- **Name**: tempo_real_execucao  
- **Label**: Tempo Real de Execução (min)  
- **Type**: Numeric  
- **Available for**: Test Execution  
  
### 9.2 Usar Custom Fields  
  
Ao criar ou executar testes, você verá os campos personalizados disponíveis para preenchimento.  
  
---  
  
## 10. Relatórios e Métricas  
  
### 10.1 Test Report - General Test Plan Metrics  
  
1. Vá em **"Test Reports and Metrics"**  
2. Selecione **"General Test Plan Metrics"**  
3. Selecione Test Plan e Build  
4. Visualize:  
   - Total de casos de teste  
   - Casos executados vs. não executados  
   - Taxa de sucesso/falha  
   - Gráfico de pizza com distribuição de status  
  
### 10.2 Test Report - Test Results  
  
1. Selecione **"Test Results"**  
2. Aplique filtros:  
   - Test Suite específica  
   - Keyword  
   - Plataforma  
   - Testador  
3. **Export**: Excel, HTML, OpenDocument  
4. Visualize histórico de execuções  
  
### 10.3 Requirements Coverage  
  
1. Selecione **"Requirements Coverage"**  
2. Visualize:  
   - Requisitos cobertos por testes  
   - Requisitos sem cobertura  
   - Porcentagem de cobertura total  
3. **Ações**: Identifique gaps e crie testes adicionais  
  
### 10.4 Test Cases Created Per User  
  
1. Selecione **"Test Cases Created Per User"**  
2. Visualize produtividade da equipe  
3. Identifique quem criou mais casos de teste  
  
### 10.5 Blocked Test Cases  
  
1. Selecione **"Blocked Test Cases"**  
2. Identifique impedimentos  
3. Planeje ações para desbloquear  
  
### 10.6 Métricas Avançadas  
  
1. **Results by Tester**: Desempenho individual  
2. **Results by Build**: Evolução entre builds  
3. **Results by Platform**: Comparação entre ambientes  
4. **Execution History**: Timeline de execuções  
  
---  
  
## 11. Recursos Avançados  
  
### 11.1 Test Case Import/Export  
  
**Exportar:**  
  
1. Vá em **"Test Specification"**  
2. Selecione uma suite  
3. Clique em **"Export Test Cases"**  
4. Escolha formato: XML  
5. Salve o arquivo  
  
**Importar:**  
  
1. Vá em **"Import Test Cases"**  
2. Faça upload do XML  
3. Configure mapeamento de campos  
4. Clique em **"Import"**  
  
### 11.2 Copy/Move Test Cases  
  
1. Selecione um caso de teste  
2. Clique em **"Actions"** > **"Copy"** ou **"Move"**  
3. Selecione destino na árvore  
4. Confirme  
  
### 11.3 Test Case Versions  
  
1. Abra um caso de teste  
2. Faça modificações  
3. Clique em **"Save"** - uma nova versão é criada automaticamente  
4. Acesse **"Version History"** para ver todas as versões  
5. Compare versões lado a lado  
  
### 11.4 Test Suite Reorder  
  
1. Na árvore de Test Specification  
2. Clique em **"Reorder Test Suites"**  
3. Arraste e solte para reorganizar  
4. Salve  
  
---  
  
## 12. Integração com Bug Tracking  
  
### 12.1 Configurar Integração com Jira  
  
1. Acesse **"Test Project Management"**  
2. Clique em **"Issue Tracker Management"**  
3. Clique em **"Create"**  
4. Configure:  
   - **Type**: Jira (SOAP API)  
   - **Name**: Jira TechStore  
   - **Config**: Insira URL, usuário e API token  
5. Teste a conexão  
6. Associe ao Test Project  
  
### 12.2 Criar Bugs Durante Execução  
  
1. Durante execução de teste com falha  
2. Clique em **"Create New Issue"**  
3. Bug é criado automaticamente no Jira  
4. Link bidirecional é estabelecido  
5. Acompanhe status do bug no TestLink  
  
---  
  
## 13. API e Automação  
  
### 13.1 Habilitar API Key  
  
1. Vá em **"Personal Settings"** (seu perfil)  
2. Seção **"API Interface"**  
3. Clique em **"Generate Key"**  
4. Copie a API Key gerada  
  
### 13.2 Exemplo de Uso da API (Python)  
  
```python  
import testlink  
  
# Conectar ao TestLink  
tl = testlink.TestLink(  
    'http://testlink.com/lib/api/xmlrpc.php',  
    'SUA_API_KEY_AQUI'  
)  
  
# Criar um caso de teste via API  
test_case = tl.createTestCase(  
    testcasename='Login via API',  
    testsuiteid=123,  
    testprojectid=1,  
    authorlogin='testador.senior',  
    summary='Teste criado via API',  
    steps=[  
        {'step_number': 1,  
         'actions': 'Acessar página',  
         'expected_results': 'Página carregada'}  
    ]  
)  
  
# Reportar resultado de execução  
result = tl.reportTCResult(  
    testcaseid=456,  
    testplanid=1,  
    buildname='v1.0.0-RC1',  
    status='p',  # 'p'=passed, 'f'=failed, 'b'=blocked  
    notes='Teste executado com sucesso via automação'  
)  
```  
  
---  
  
## 14. Best Practices  
  
### 14.1 Organização  
  
- Use nomenclatura consistente para suites e casos  
- Mantenha hierarquia lógica (máx. 3-4 níveis)  
- Agrupe testes relacionados na mesma suite  
  
### 14.2 Escrita de Casos de Teste  
  
- **Título claro**: Descreva o objetivo do teste  
- **Steps atômicos**: Cada step uma ação  
- **Expected results específicos**: Seja preciso  
- **Preconditions completas**: Liste todos os pré-requisitos  
  
### 14.3 Keywords Strategy  
  
- Use keywords para filtrar testes rapidamente  
- Combine keywords: smoke + critico para testes essenciais  
- Padronize keywords no projeto  
  
### 14.4 Execução de Testes  
  
- Execute testes na ordem lógica  
- Documente falhas com detalhes  
- Anexe evidências (prints, logs)  
- Mantenha comunicação com desenvolvedores  
  
### 14.5 Relatórios  
  
- Gere relatórios semanalmente  
- Compartilhe métricas com stakeholders  
- Identifique tendências (aumento de bugs, etc.)  
  
---  
  
## 15. Fluxo de Trabalho Completo - Exemplo Prático  
  
### Cenário: Sprint de 2 semanas  
  
**Semana 1:**  
  
**Segunda-feira:**  
  
1. Test Lead cria Test Plan "Sprint 5"  
2. Test Lead cria Build "v1.5.0-DEV"  
3. Test Lead adiciona casos de teste ao plano (30 casos)  
4. Test Lead atribui testes:  
   - 15 casos para João (júnior)  
   - 15 casos para Maria (sênior)  
  
**Terça a Quinta:** 5. Testadores executam casos atribuídos  
6. João encontra 3 bugs, registra no Jira via TestLink  
7. Maria executa testes em 2 plataformas (Web + Mobile)  
  
**Sexta:** 8. Test Lead gera relatório semanal  
9. Métricas: 20/30 executados (66%), 17 passed, 3 failed  
10. Reunião de status com time de desenvolvimento  
  
**Semana 2:**  
  
**Segunda:** 11. Desenvolvedores corrigem bugs  
12. Test Lead cria novo Build "v1.5.0-RC1"  
  
**Terça a Quinta:** 13. Re-execução dos testes falhados  
14. Execução dos 10 casos restantes  
15. Testes de regressão (keyword: regressao)  
  
**Sexta:** 16. Relatório final: 30/30 executados (100%), 28 passed, 2 failed  
17. Decisão: 2 bugs não-críticos, aprovado para produção  
18. Arquivamento do Test Plan  
  
---  
  
## 16. Troubleshooting  
  
### Problema: Não consigo criar casos de teste  
  
**Solução**: Verifique se você tem permissão de "Test Designer" ou superior  
  
### Problema: Não vejo meus testes atribuídos  
  
**Solução**: Certifique-se de estar no Test Plan e Build corretos  
  
### Problema: Integração com Jira não funciona  
  
**Solução**: Verifique credenciais, URL e se o plugin está ativo  
  
### Problema: Relatórios não exibem dados  
  
**Solução**: Execute ao menos alguns testes primeiro, selecione filtros corretos  
  
---  
  
## Conclusão  
  
Este tutorial cobriu todos os recursos principais do TestLink 2.0.0:  
  
- ✅ Configuração de projetos  
- ✅ Gestão de usuários e permissões  
- ✅ Criação de test suites e casos de teste  
- ✅ Gestão de requisitos e rastreabilidade  
- ✅ Planos de teste e builds  
- ✅ Execução e registro de resultados  
- ✅ Plataformas multi-ambiente  
- ✅ Custom fields  
- ✅ Relatórios e métricas  
- ✅ Integração com bug tracking  
- ✅ API e automação  
  
Com este conhecimento, você está pronto para gerenciar projetos de teste complexos no TestLink!  
  
**Próximos passos sugeridos:**  
  
1. Pratique criando seu próprio projeto  
2. Explore integrações com CI/CD (Jenkins)  
3. Automatize relatórios com a API  
4. Customize campos para necessidades específicas do seu time  
