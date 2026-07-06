1. **Progressão dos Exercícios:**  
   
   - Exercícios 1-5: Fundamentos do JUnit (assertions, lifecycle, exceptions)  
   - Exercícios 6-10: Introdução aos mocks (@Mock, @InjectMocks, verify)  
   - Exercícios 11-15: Mocks avançados (InOrder, ArgumentCaptor, @Spy)  
   - Exercícios 16-20: Cenários reais e integração complexa  
  
2. **Padrões de Teste:**  
   
   - **AAA Pattern**: Arrange, Act, Assert  
   - **Given-When-Then**: Estrutura narrativa dos testes  
   - **Nomenclatura descritiva**: métodos que explicam o comportamento  
  
3. **Técnicas de Mock:**  
   
   - `@Mock`: Criar objetos simulados  
   - `@InjectMocks`: Injetar mocks automaticamente  
   - `@Spy`: Misturar comportamento real e simulado  
   - `verify()`: Verificar interações  
   - `when().thenReturn()`: Definir comportamentos  
   - `ArgumentCaptor`: Capturar argumentos passados  
   - `InOrder`: Verificar ordem das chamadas  
  
4. **Boas Práticas:**  
   
   - Testes isolados e independentes  
   - Nomes de testes descritivos  
   - Uso de `@BeforeEach` para setup  
   - Verificação de comportamentos e estados  
   - Tratamento de cenários de erro  
   - Uso de constantes para valores de teste  
  
5. **Cenários Cobertos:**  
   
   - Testes de unidade pura (sem dependências)  
   - Testes com mocks simples  
   - Testes de integração entre componentes  
   - Tratamento de exceções  
   - Validação de ordem de execução  
   - Verificação de não-execução (`never()`)  
  
### Sugestões de execução dos exercícios:  
  
1. **Começar pelos exercícios 1-3** para fixar JUnit básico  
2. **Exercícios 6-8** para introduzir conceitos de mock  
3. **Exercícios 13-15** para técnicas avançadas  
4. **Exercício 20** como desafio final integrador  
  
## Exercícios 1-5: Testes Básicos (JUnit apenas)  
  
### Exercício 1: Calculadora Básica  
  
**Enunciado:** Implemente uma classe `Calculadora` com métodos `somar(int a, int b)`, `subtrair(int a, int b)`, `multiplicar(int a, int b)` e `dividir(int a, int b)`. O método dividir deve lançar `ArithmeticException` quando o divisor for zero.  
  
### Exercício 2: Validador de String  
  
**Enunciado:** Crie uma classe `ValidadorString` com métodos `ehVazia(String str)`, `temTamanhoMinimo(String str, int min)`, `ehEmail(String email)` e `ehCPF(String cpf)`. Teste todos os cenários possíveis.  
  
### Exercício 3: Contador com Estado  
  
**Enunciado:** Implemente uma classe `Contador` que mantém um valor interno, com métodos `incrementar()`, `decrementar()`, `reset()`, `getValor()` e `definirValor(int valor)`. Teste o comportamento do estado.  
  
### Exercício 4: Processador de Lista  
  
**Enunciado:** Crie uma classe `ProcessadorLista` com métodos `filtrarPares(List<Integer> lista)`, `somarElementos(List<Integer> lista)`, `encontrarMaior(List<Integer> lista)` e `ehListaVazia(List<Integer> lista)`.  
  
### Exercício 5: Conversor de Temperatura  
  
**Enunciado:** Implemente uma classe `ConversorTemperatura` com métodos `celsiusParaFahrenheit(double celsius)`, `fahrenheitParaCelsius(double fahrenheit)`, `celsiusParaKelvin(double celsius)` e `kelvinParaCelsius(double kelvin)`. Kelvin não pode ser negativo.  
  
## Exercícios 6-10: Introdução aos Mocks  
  
### Exercício 6: Sistema de Notificação  
  
**Enunciado:** Crie uma classe `SistemaNotificacao` que depende de um `ServicoEmail`. O sistema deve enviar notificações e registrar logs. Teste usando mocks para verificar se o email foi enviado corretamente.  
  
### Exercício 7: Carrinho de Compras  
  
**Enunciado:** Implemente um `CarrinhoCompras` que depende de um `CalculadoraDesconto`. O carrinho deve calcular o total com desconto. Teste usando mocks.  
  
### Exercício 8: Autenticador de Usuário  
  
**Enunciado:** Desenvolva um `AutenticadorUsuario` que depende de um `RepositorioUsuario` e um `CriptografadorSenha`. Deve validar credenciais e retornar token de sessão.  
  
### Exercício 9: Processador de Pagamento  
  
**Enunciado:** Crie um `ProcessadorPagamento` que depende de `GatewayPagamento` e `ServicoLog`. Deve processar pagamentos e registrar logs de sucesso/falha.  
  
### Exercício 10: Gerenciador de Estoque  
  
**Enunciado:** Implemente um `GerenciadorEstoque` que depende de um `RepositorioProduto`. Deve verificar disponibilidade, reservar itens e atualizar quantidades.  
  
## Exercícios 11-15: Mocks Avançados  
  
### Exercício 11: Sistema de Cache  
  
**Enunciado:** Implemente um `SistemaCache` que depende de um `RepositorioDados` e um `Cache`. Se o dado estiver no cache, não deve consultar o repositório. Teste com mocks verificando a ordem das chamadas.  
  
### Exercício 12: Processador de Pedidos  
  
**Enunciado:** Desenvolva um `ProcessadorPedidos` que depende de `ValidadorPedido`, `CalculadoraFrete` e `ServicoEntrega`. Deve validar, calcular frete e agendar entrega.  
  
## Exercícios 13-15: Mocks com Comportamentos Complexos  
  
### Exercício 13: Sistema de Backup  
  
**Enunciado:** Crie um `SistemaBackup` que depende de `ArmazenamentoLocal` e `ArmazenamentoNuvem`. Deve tentar backup local primeiro, e se falhar, usar a nuvem. Teste os diferentes cenários de falha.  
  
### Exercício 14: Processador de Relatórios  
  
**Enunciado:** Implemente um `ProcessadorRelatorios` que depende de `ColetorDados`, `FormatadorRelatorio` e `ServicoEmail`. Deve coletar dados, formatar e enviar por email. Use `@Spy` para testar comportamento parcial.  
  
### Exercício 15: Sistema de Autenticação Multi-fator  
  
**Enunciado:** Desenvolva um `SistemaAutenticacaoMFA` que depende de `ValidadorCredenciais`, `GeradorTokenMFA` e `ServicoSMS`. Deve validar credenciais, gerar token e enviar SMS.  
  
## Exercícios 16-20: Cenários Complexos e Integração  
  
### Exercício 16: Sistema de Reservas  
  
**Enunciado:** Crie um `SistemaReservas` que depende de `RepositorioQuarto`, `ProcessadorPagamento` e `ServicoNotificacao`. Deve verificar disponibilidade, processar pagamento e confirmar reserva.  
  
### Exercício 17: Analisador de Log  
  
**Enunciado:** Implemente um `AnalisadorLog` que depende de `LeitorArquivo` e `ProcessadorEstatisticas`. Deve ler logs, extrair métricas e gerar relatório de estatísticas.  
  
### Exercício 18: Sistema de Monitoramento  
  
**Enunciado:** Desenvolva um `MonitorSistema` que depende de `ColetorMetricas`, `VerificadorLimites` e `AlertaService`. Deve coletar métricas, verificar limites e enviar alertas quando necessário.  
  
### Exercício 19: Sistema de Workflow  
  
**Enunciado:** Crie um `SistemaWorkflow` que depende de `ValidadorEtapa`, `ExecutorTarefa` e `RepositorioWorkflow`. Deve executar etapas sequencialmente e salvar progresso.  
  
### Exercício 20: Sistema de Processamento de Arquivos (Integração Complexa)  
  
**Enunciado:** Implemente um `ProcessadorArquivos` que depende de `ValidadorArquivo`, `ConversorFormato`, `CompactadorArquivo` e `ServicoUpload`. Deve validar, converter, compactar e fazer upload de arquivos. Use todos os recursos de mock aprendidos.  
