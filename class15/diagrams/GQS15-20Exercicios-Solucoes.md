Criei um conjunto completo de 20 exercícios progressivos de teste unitário que cobrem desde conceitos básicos do JUnit até cenários complexos com Mockito.  
  
**Estrutura dos exercícios:**  
  
📚 **Exercícios 1-5: Fundamentos JUnit**  
  
- Calculadora, validadores, contadores - focam em assertions básicas, lifecycle e exceptions  
  
🎯 **Exercícios 6-10: Introdução aos Mocks**  
  
- Sistema de notificação, carrinho de compras, autenticação - introduzem @Mock, @InjectMocks e verify()  
  
⚡ **Exercícios 11-15: Mocks Avançados**  
  
- Cache, backup, relatórios - exploram InOrder, @Spy, ArgumentCaptor e cenários complexos  
  
🚀 **Exercícios 16-20: Integração Complexa**  
  
- Reservas, monitoramento, workflow, processamento de arquivos - simulam sistemas reais  
  
**Cada exercício inclui:**  
  
- ✅ Enunciado claro com contexto  
- ✅ Implementação completa da classe principal  
- ✅ Testes abrangentes com diferentes cenários  
- ✅ Comentários explicativos  
- ✅ Boas práticas aplicadas  
  
**Principais conceitos cobertos:**  
  
- Assertions e comparações  
- Lifecycle dos testes (@BeforeEach, @AfterEach)  
- Mocks e stubs  
- Verificação de interações  
- Ordem de execução  
- Tratamento de exceções  
- Testes parametrizados  
- ArgumentCaptor para validação de parâmetros  
  
Os exercícios progridem de forma didática, permitindo que os alunos construam conhecimento gradualmente, desde testes simples até cenários que espelham sistemas reais de produção.  
  
---  
  
# 20 Exercícios de Teste Unitário - JUnit e Mockito  
  
## Exercícios 1-5: Testes Básicos (JUnit apenas)  
  
### Exercício 1: Calculadora Básica  
  
**Enunciado:** Implemente uma classe `Calculadora` com métodos `somar(int a, int b)`, `subtrair(int a, int b)`, `multiplicar(int a, int b)` e `dividir(int a, int b)`. O método dividir deve lançar `ArithmeticException` quando o divisor for zero.  
  
**Solução:**  
  
```java  
// Classe principal  
public class Calculadora {  
    public int somar(int a, int b) {  
        return a + b;  
    }  
  
    public int subtrair(int a, int b) {  
        return a - b;  
    }  
  
    public int multiplicar(int a, int b) {  
        return a * b;  
    }  
  
    public double dividir(int a, int b) {  
        if (b == 0) {  
            throw new ArithmeticException("Divisão por zero não é permitida");  
        }  
        return (double) a / b;  
    }  
}  
  
// Teste  
import org.junit.jupiter.api.Test;  
import static org.junit.jupiter.api.Assertions.*;  
  
class CalculadoraTest {  
  
    private Calculadora calc = new Calculadora();  
  
    @Test  
    void deveSomarDoisNumeros() {  
        assertEquals(5, calc.somar(2, 3));  
    }  
  
    @Test  
    void deveSubtrairDoisNumeros() {  
        assertEquals(1, calc.subtrair(3, 2));  
    }  
  
    @Test  
    void deveMultiplicarDoisNumeros() {  
        assertEquals(6, calc.multiplicar(2, 3));  
    }  
  
    @Test  
    void deveDividirDoisNumeros() {  
        assertEquals(2.5, calc.dividir(5, 2), 0.001);  
    }  
  
    @Test  
    void deveLancarExcecaoAoDividirPorZero() {  
        ArithmeticException exception = assertThrows(  
            ArithmeticException.class,  
            () -> calc.dividir(5, 0)  
        );  
        assertEquals("Divisão por zero não é permitida", exception.getMessage());  
    }  
}  
```  
  
---  
  
### Exercício 2: Validador de String  
  
**Enunciado:** Crie uma classe `ValidadorString` com métodos `ehVazia(String str)`, `temTamanhoMinimo(String str, int min)`, `ehEmail(String email)` e `ehCPF(String cpf)`. Teste todos os cenários possíveis.  
  
**Solução:**  
  
```java  
// Classe principal  
public class ValidadorString {  
  
    public boolean ehVazia(String str) {  
        return str == null || str.trim().isEmpty();  
    }  
  
    public boolean temTamanhoMinimo(String str, int min) {  
        return str != null && str.length() >= min;  
    }  
  
    public boolean ehEmail(String email) {  
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");  
    }  
  
    public boolean ehCPF(String cpf) {  
        return cpf != null && cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");  
    }  
}  
  
// Teste  
import org.junit.jupiter.api.BeforeEach;  
import org.junit.jupiter.api.Test;  
import org.junit.jupiter.params.ParameterizedTest;  
import org.junit.jupiter.params.provider.ValueSource;  
import static org.junit.jupiter.api.Assertions.*;  
  
class ValidadorStringTest {  
  
    private ValidadorString validador;  
  
    @BeforeEach  
    void setUp() {  
        validador = new ValidadorString();  
    }  
  
    @Test  
    void deveRetornarTrueParaStringVazia() {  
        assertTrue(validador.ehVazia(""));  
        assertTrue(validador.ehVazia("   "));  
        assertTrue(validador.ehVazia(null));  
    }  
  
    @Test  
    void deveRetornarFalseParaStringNaoVazia() {  
        assertFalse(validador.ehVazia("texto"));  
    }  
  
    @ParameterizedTest  
    @ValueSource(strings = {"abc", "hello", "test123"})  
    void deveValidarTamanhoMinimo(String str) {  
        assertTrue(validador.temTamanhoMinimo(str, 3));  
        assertFalse(validador.temTamanhoMinimo(str, 10));  
    }  
  
    @Test  
    void deveValidarEmailCorreto() {  
        assertTrue(validador.ehEmail("teste@email.com"));  
        assertTrue(validador.ehEmail("user.name@domain.co.uk"));  
        assertFalse(validador.ehEmail("email-invalido"));  
        assertFalse(validador.ehEmail("@email.com"));  
    }  
  
    @Test  
    void deveValidarCPF() {  
        assertTrue(validador.ehCPF("123.456.789-10"));  
        assertFalse(validador.ehCPF("12345678910"));  
        assertFalse(validador.ehCPF("abc.def.ghi-jk"));  
    }  
}  
```  
  
---  
  
### Exercício 3: Contador com Estado  
  
**Enunciado:** Implemente uma classe `Contador` que mantém um valor interno, com métodos `incrementar()`, `decrementar()`, `reset()`, `getValor()` e `definirValor(int valor)`. Teste o comportamento do estado.  
  
**Solução:**  
  
```java  
// Classe principal  
public class Contador {  
    private int valor = 0;  
  
    public void incrementar() {  
        valor++;  
    }  
  
    public void decrementar() {  
        valor--;  
    }  
  
    public void reset() {  
        valor = 0;  
    }  
  
    public int getValor() {  
        return valor;  
    }  
  
    public void definirValor(int valor) {  
        this.valor = valor;  
    }  
}  
  
// Teste  
import org.junit.jupiter.api.BeforeEach;  
import org.junit.jupiter.api.Test;  
import static org.junit.jupiter.api.Assertions.*;  
  
class ContadorTest {  
  
    private Contador contador;  
  
    @BeforeEach  
    void setUp() {  
        contador = new Contador();  
    }  
  
    @Test  
    void deveIniciarComZero() {  
        assertEquals(0, contador.getValor());  
    }  
  
    @Test  
    void deveIncrementarCorretamente() {  
        contador.incrementar();  
        assertEquals(1, contador.getValor());  
  
        contador.incrementar();  
        assertEquals(2, contador.getValor());  
    }  
  
    @Test  
    void deveDecrementarCorretamente() {  
        contador.decrementar();  
        assertEquals(-1, contador.getValor());  
    }  
  
    @Test  
    void deveResetarParaZero() {  
        contador.definirValor(10);  
        contador.reset();  
        assertEquals(0, contador.getValor());  
    }  
  
    @Test  
    void deveDefinirValorCorretamente() {  
        contador.definirValor(42);  
        assertEquals(42, contador.getValor());  
    }  
}  
```  
  
---  
  
### Exercício 4: Processador de Lista  
  
**Enunciado:** Crie uma classe `ProcessadorLista` com métodos `filtrarPares(List<Integer> lista)`, `somarElementos(List<Integer> lista)`, `encontrarMaior(List<Integer> lista)` e `ehListaVazia(List<Integer> lista)`.  
  
**Solução:**  
  
```java  
// Classe principal  
import java.util.*;  
import java.util.stream.Collectors;  
  
public class ProcessadorLista {  
  
    public List<Integer> filtrarPares(List<Integer> lista) {  
        if (lista == null) return new ArrayList<>();  
        return lista.stream()  
                   .filter(n -> n % 2 == 0)  
                   .collect(Collectors.toList());  
    }  
  
    public int somarElementos(List<Integer> lista) {  
        if (lista == null) return 0;  
        return lista.stream().mapToInt(Integer::intValue).sum();  
    }  
  
    public Integer encontrarMaior(List<Integer> lista) {  
        if (lista == null || lista.isEmpty()) {  
            throw new IllegalArgumentException("Lista não pode ser vazia");  
        }  
        return Collections.max(lista);  
    }  
  
    public boolean ehListaVazia(List<Integer> lista) {  
        return lista == null || lista.isEmpty();  
    }  
}  
  
// Teste  
import org.junit.jupiter.api.Test;  
import java.util.*;  
import static org.junit.jupiter.api.Assertions.*;  
  
class ProcessadorListaTest {  
  
    private ProcessadorLista processador = new ProcessadorLista();  
  
    @Test  
    void deveFiltrarApenasPares() {  
        List<Integer> lista = Arrays.asList(1, 2, 3, 4, 5, 6);  
        List<Integer> resultado = processador.filtrarPares(lista);  
  
        assertEquals(Arrays.asList(2, 4, 6), resultado);  
    }  
  
    @Test  
    void deveRetornarListaVaziaQuandoSemPares() {  
        List<Integer> lista = Arrays.asList(1, 3, 5);  
        assertTrue(processador.filtrarPares(lista).isEmpty());  
    }  
  
    @Test  
    void deveSomarTodosElementos() {  
        List<Integer> lista = Arrays.asList(1, 2, 3, 4, 5);  
        assertEquals(15, processador.somarElementos(lista));  
    }  
  
    @Test  
    void deveRetornarZeroParaListaNula() {  
        assertEquals(0, processador.somarElementos(null));  
    }  
  
    @Test  
    void deveEncontrarMaiorElemento() {  
        List<Integer> lista = Arrays.asList(3, 1, 4, 1, 5, 9);  
        assertEquals(9, processador.encontrarMaior(lista));  
    }  
  
    @Test  
    void deveLancarExcecaoParaListaVaziaAoEncontrarMaior() {  
        assertThrows(IllegalArgumentException.class,  
            () -> processador.encontrarMaior(new ArrayList<>()));  
    }  
  
    @Test  
    void deveVerificarSeListaEhVazia() {  
        assertTrue(processador.ehListaVazia(null));  
        assertTrue(processador.ehListaVazia(new ArrayList<>()));  
        assertFalse(processador.ehListaVazia(Arrays.asList(1, 2, 3)));  
    }  
}  
```  
  
---  
  
### Exercício 5: Conversor de Temperatura  
  
**Enunciado:** Implemente uma classe `ConversorTemperatura` com métodos `celsiusParaFahrenheit(double celsius)`, `fahrenheitParaCelsius(double fahrenheit)`, `celsiusParaKelvin(double celsius)` e `kelvinParaCelsius(double kelvin)`. Kelvin não pode ser negativo.  
  
**Solução:**  
  
```java  
// Classe principal  
public class ConversorTemperatura {  
  
    public double celsiusParaFahrenheit(double celsius) {  
        return (celsius * 9.0 / 5.0) + 32;  
    }  
  
    public double fahrenheitParaCelsius(double fahrenheit) {  
        return (fahrenheit - 32) * 5.0 / 9.0;  
    }  
  
    public double celsiusParaKelvin(double celsius) {  
        return celsius + 273.15;  
    }  
  
    public double kelvinParaCelsius(double kelvin) {  
        if (kelvin < 0) {  
            throw new IllegalArgumentException("Kelvin não pode ser negativo");  
        }  
        return kelvin - 273.15;  
    }  
}  
  
// Teste  
import org.junit.jupiter.api.Test;  
import static org.junit.jupiter.api.Assertions.*;  
  
class ConversorTemperaturaTest {  
  
    private ConversorTemperatura conversor = new ConversorTemperatura();  
  
    @Test  
    void deveConverterCelsiusParaFahrenheit() {  
        assertEquals(32.0, conversor.celsiusParaFahrenheit(0), 0.001);  
        assertEquals(212.0, conversor.celsiusParaFahrenheit(100), 0.001);  
        assertEquals(98.6, conversor.celsiusParaFahrenheit(37), 0.001);  
    }  
  
    @Test  
    void deveConverterFahrenheitParaCelsius() {  
        assertEquals(0.0, conversor.fahrenheitParaCelsius(32), 0.001);  
        assertEquals(100.0, conversor.fahrenheitParaCelsius(212), 0.001);  
    }  
  
    @Test  
    void deveConverterCelsiusParaKelvin() {  
        assertEquals(273.15, conversor.celsiusParaKelvin(0), 0.001);  
        assertEquals(373.15, conversor.celsiusParaKelvin(100), 0.001);  
    }  
  
    @Test  
    void deveConverterKelvinParaCelsius() {  
        assertEquals(0.0, conversor.kelvinParaCelsius(273.15), 0.001);  
        assertEquals(100.0, conversor.kelvinParaCelsius(373.15), 0.001);  
    }  
  
    @Test  
    void deveLancarExcecaoParaKelvinNegativo() {  
        IllegalArgumentException exception = assertThrows(  
            IllegalArgumentException.class,  
            () -> conversor.kelvinParaCelsius(-10)  
        );  
        assertEquals("Kelvin não pode ser negativo", exception.getMessage());  
    }  
}  
```  
  
---  
  
## Exercícios 6-10: Introdução aos Mocks  
  
### Exercício 6: Sistema de Notificação  
  
**Enunciado:** Crie uma classe `SistemaNotificacao` que depende de um `ServicoEmail`. O sistema deve enviar notificações e registrar logs. Teste usando mocks para verificar se o email foi enviado corretamente.  
  
**Solução:**  
  
```java  
// Interfaces e classes  
interface ServicoEmail {  
    boolean enviarEmail(String destinatario, String assunto, String corpo);  
}  
  
public class SistemaNotificacao {  
    private ServicoEmail servicoEmail;  
  
    public SistemaNotificacao(ServicoEmail servicoEmail) {  
        this.servicoEmail = servicoEmail;  
    }  
  
    public boolean enviarNotificacao(String usuario, String mensagem) {  
        if (usuario == null || mensagem == null) {  
            return false;  
        }  
  
        String assunto = "Notificação Importante";  
        return servicoEmail.enviarEmail(usuario, assunto, mensagem);  
    }  
}  
  
// Teste  
import org.junit.jupiter.api.Test;  
import org.junit.jupiter.api.extension.ExtendWith;  
import org.mockito.Mock;  
import org.mockito.InjectMocks;  
import org.mockito.junit.jupiter.MockitoExtension;  
import static org.mockito.Mockito.*;  
import static org.junit.jupiter.api.Assertions.*;  
  
@ExtendWith(MockitoExtension.class)  
class SistemaNotificacaoTest {  
  
    @Mock  
    private ServicoEmail servicoEmail;  
  
    @InjectMocks  
    private SistemaNotificacao sistema;  
  
    @Test  
    void deveEnviarNotificacaoComSucesso() {  
        // Arrange  
        String usuario = "user@test.com";  
        String mensagem = "Teste de notificação";  
        when(servicoEmail.enviarEmail(anyString(), anyString(), anyString())).thenReturn(true);  
  
        // Act  
        boolean resultado = sistema.enviarNotificacao(usuario, mensagem);  
  
        // Assert  
        assertTrue(resultado);  
        verify(servicoEmail).enviarEmail(usuario, "Notificação Importante", mensagem);  
    }  
  
    @Test  
    void naoDeveEnviarNotificacaoComParametrosNulos() {  
        // Act & Assert  
        assertFalse(sistema.enviarNotificacao(null, "mensagem"));  
        assertFalse(sistema.enviarNotificacao("user@test.com", null));  
  
        // Verificar que email não foi enviado  
        verify(servicoEmail, never()).enviarEmail(anyString(), anyString(), anyString());  
    }  
  
    @Test  
    void deveRetornarFalsoQuandoEmailFalha() {  
        // Arrange  
        when(servicoEmail.enviarEmail(anyString(), anyString(), anyString())).thenReturn(false);  
  
        // Act  
        boolean resultado = sistema.enviarNotificacao("user@test.com", "mensagem");  
  
        // Assert  
        assertFalse(resultado);  
    }  
}  
```  
  
---  
  
### Exercício 7: Carrinho de Compras  
  
**Enunciado:** Implemente um `CarrinhoCompras` que depende de um `CalculadoraDesconto`. O carrinho deve calcular o total com desconto. Teste usando mocks.  
  
**Solução:**  
  
```java  
// Classes e interfaces  
class Item {  
    private String nome;  
    private double preco;  
  
    public Item(String nome, double preco) {  
        this.nome = nome;  
        this.preco = preco;  
    }  
  
    public double getPreco() { return preco; }  
    public String getNome() { return nome; }  
}  
  
interface CalculadoraDesconto {  
    double calcularDesconto(double valorTotal, String tipoCliente);  
}  
  
public class CarrinhoCompras {  
    private List<Item> itens = new ArrayList<>();  
    private CalculadoraDesconto calculadora;  
  
    public CarrinhoCompras(CalculadoraDesconto calculadora) {  
        this.calculadora = calculadora;  
    }  
  
    public void adicionarItem(Item item) {  
        itens.add(item);  
    }  
  
    public double calcularTotal() {  
        return itens.stream().mapToDouble(Item::getPreco).sum();  
    }  
  
    public double calcularTotalComDesconto(String tipoCliente) {  
        double total = calcularTotal();  
        double desconto = calculadora.calcularDesconto(total, tipoCliente);  
        return total - desconto;  
    }  
  
    public int getQuantidadeItens() {  
        return itens.size();  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class CarrinhoComprasTest {  
  
    @Mock  
    private CalculadoraDesconto calculadora;  
  
    @InjectMocks  
    private CarrinhoCompras carrinho;  
  
    @Test  
    void deveCalcularTotalSemDesconto() {  
        carrinho.adicionarItem(new Item("Produto A", 100.0));  
        carrinho.adicionarItem(new Item("Produto B", 50.0));  
  
        assertEquals(150.0, carrinho.calcularTotal(), 0.001);  
    }  
  
    @Test  
    void deveCalcularTotalComDesconto() {  
        // Arrange  
        carrinho.adicionarItem(new Item("Produto A", 100.0));  
        when(calculadora.calcularDesconto(100.0, "VIP")).thenReturn(10.0);  
  
        // Act  
        double resultado = carrinho.calcularTotalComDesconto("VIP");  
  
        // Assert  
        assertEquals(90.0, resultado, 0.001);  
        verify(calculadora).calcularDesconto(100.0, "VIP");  
    }  
  
    @Test  
    void deveContarItensCorretamente() {  
        assertEquals(0, carrinho.getQuantidadeItens());  
  
        carrinho.adicionarItem(new Item("Item 1", 10.0));  
        assertEquals(1, carrinho.getQuantidadeItens());  
  
        carrinho.adicionarItem(new Item("Item 2", 20.0));  
        assertEquals(2, carrinho.getQuantidadeItens());  
    }  
}  
```  
  
---  
  
### Exercício 8: Autenticador de Usuário  
  
**Enunciado:** Desenvolva um `AutenticadorUsuario` que depende de um `RepositorioUsuario` e um `CriptografadorSenha`. Deve validar credenciais e retornar token de sessão.  
  
**Solução:**  
  
```java  
// Interfaces e classes  
class Usuario {  
    private String email;  
    private String senhaHash;  
  
    public Usuario(String email, String senhaHash) {  
        this.email = email;  
        this.senhaHash = senhaHash;  
    }  
  
    public String getEmail() { return email; }  
    public String getSenhaHash() { return senhaHash; }  
}  
  
interface RepositorioUsuario {  
    Optional<Usuario> buscarPorEmail(String email);  
}  
  
interface CriptografadorSenha {  
    String criptografar(String senha);  
    boolean verificar(String senha, String hash);  
}  
  
public class AutenticadorUsuario {  
    private RepositorioUsuario repositorio;  
    private CriptografadorSenha criptografador;  
  
    public AutenticadorUsuario(RepositorioUsuario repositorio, CriptografadorSenha criptografador) {  
        this.repositorio = repositorio;  
        this.criptografador = criptografador;  
    }  
  
    public String autenticar(String email, String senha) {  
        Optional<Usuario> usuario = repositorio.buscarPorEmail(email);  
  
        if (usuario.isEmpty()) {  
            throw new RuntimeException("Usuário não encontrado");  
        }  
  
        if (!criptografador.verificar(senha, usuario.get().getSenhaHash())) {  
            throw new RuntimeException("Senha incorreta");  
        }  
  
        return "TOKEN_" + email + "_" + System.currentTimeMillis();  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class AutenticadorUsuarioTest {  
  
    @Mock  
    private RepositorioUsuario repositorio;  
  
    @Mock  
    private CriptografadorSenha criptografador;  
  
    @InjectMocks  
    private AutenticadorUsuario autenticador;  
  
    @Test  
    void deveAutenticarUsuarioValido() {  
        // Arrange  
        String email = "user@test.com";  
        String senha = "123456";  
        String senhaHash = "hash123";  
        Usuario usuario = new Usuario(email, senhaHash);  
  
        when(repositorio.buscarPorEmail(email)).thenReturn(Optional.of(usuario));  
        when(criptografador.verificar(senha, senhaHash)).thenReturn(true);  
  
        // Act  
        String token = autenticador.autenticar(email, senha);  
  
        // Assert  
        assertNotNull(token);  
        assertTrue(token.startsWith("TOKEN_" + email));  
        verify(repositorio).buscarPorEmail(email);  
        verify(criptografador).verificar(senha, senhaHash);  
    }  
  
    @Test  
    void deveLancarExcecaoParaUsuarioInexistente() {  
        // Arrange  
        when(repositorio.buscarPorEmail("inexistente@test.com")).thenReturn(Optional.empty());  
  
        // Act & Assert  
        RuntimeException exception = assertThrows(  
            RuntimeException.class,  
            () -> autenticador.autenticar("inexistente@test.com", "senha")  
        );  
  
        assertEquals("Usuário não encontrado", exception.getMessage());  
        verify(criptografador, never()).verificar(anyString(), anyString());  
    }  
  
    @Test  
    void deveLancarExcecaoParaSenhaIncorreta() {  
        // Arrange  
        String email = "user@test.com";  
        Usuario usuario = new Usuario(email, "hash123");  
  
        when(repositorio.buscarPorEmail(email)).thenReturn(Optional.of(usuario));  
        when(criptografador.verificar("senhaErrada", "hash123")).thenReturn(false);  
  
        // Act & Assert  
        RuntimeException exception = assertThrows(  
            RuntimeException.class,  
            () -> autenticador.autenticar(email, "senhaErrada")  
        );  
  
        assertEquals("Senha incorreta", exception.getMessage());  
    }  
}  
```  
  
---  
  
### Exercício 9: Processador de Pagamento  
  
**Enunciado:** Crie um `ProcessadorPagamento` que depende de `GatewayPagamento` e `ServicoLog`. Deve processar pagamentos e registrar logs de sucesso/falha.  
  
**Solução:**  
  
```java  
// Interfaces e classes  
interface GatewayPagamento {  
    boolean processarPagamento(String cartao, double valor);  
}  
  
interface ServicoLog {  
    void registrarSucesso(String mensagem);  
    void registrarErro(String mensagem);  
}  
  
public class ProcessadorPagamento {  
    private GatewayPagamento gateway;  
    private ServicoLog servicoLog;  
  
    public ProcessadorPagamento(GatewayPagamento gateway, ServicoLog servicoLog) {  
        this.gateway = gateway;  
        this.servicoLog = servicoLog;  
    }  
  
    public boolean processarPagamento(String cartao, double valor) {  
        if (valor <= 0) {  
            servicoLog.registrarErro("Valor inválido: " + valor);  
            return false;  
        }  
  
        try {  
            boolean sucesso = gateway.processarPagamento(cartao, valor);  
  
            if (sucesso) {  
                servicoLog.registrarSucesso("Pagamento de R$" + valor + " processado");  
                return true;  
            } else {  
                servicoLog.registrarErro("Falha no pagamento de R$" + valor);  
                return false;  
            }  
        } catch (Exception e) {  
            servicoLog.registrarErro("Erro no gateway: " + e.getMessage());  
            return false;  
        }  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class ProcessadorPagamentoTest {  
  
    @Mock  
    private GatewayPagamento gateway;  
  
    @Mock  
    private ServicoLog servicoLog;  
  
    @InjectMocks  
    private ProcessadorPagamento processador;  
  
    @Test  
    void deveProcessarPagamentoComSucesso() {  
        // Arrange  
        String cartao = "1234-5678-9012-3456";  
        double valor = 100.0;  
        when(gateway.processarPagamento(cartao, valor)).thenReturn(true);  
  
        // Act  
        boolean resultado = processador.processarPagamento(cartao, valor);  
  
        // Assert  
        assertTrue(resultado);  
        verify(gateway).processarPagamento(cartao, valor);  
        verify(servicoLog).registrarSucesso("Pagamento de R$100.0 processado");  
        verify(servicoLog, never()).registrarErro(anyString());  
    }  
  
    @Test  
    void deveRegistrarErroQuandoPagamentoFalha() {  
        // Arrange  
        String cartao = "1234-5678-9012-3456";  
        double valor = 100.0;  
        when(gateway.processarPagamento(cartao, valor)).thenReturn(false);  
  
        // Act  
        boolean resultado = processador.processarPagamento(cartao, valor);  
  
        // Assert  
        assertFalse(resultado);  
        verify(servicoLog).registrarErro("Falha no pagamento de R$100.0");  
        verify(servicoLog, never()).registrarSucesso(anyString());  
    }  
  
    @Test  
    void deveRejeitarValorInvalido() {  
        // Act  
        boolean resultado = processador.processarPagamento("1234", -50.0);  
  
        // Assert  
        assertFalse(resultado);  
        verify(servicoLog).registrarErro("Valor inválido: -50.0");  
        verify(gateway, never()).processarPagamento(anyString(), anyDouble());  
    }  
  
    @Test  
    void deveTratarExcecaoDoGateway() {  
        // Arrange  
        when(gateway.processarPagamento(anyString(), anyDouble()))  
            .thenThrow(new RuntimeException("Erro de conexão"));  
  
        // Act  
        boolean resultado = processador.processarPagamento("1234", 100.0);  
  
        // Assert  
        assertFalse(resultado);  
        verify(servicoLog).registrarErro("Erro no gateway: Erro de conexão");  
    }  
}  
```  
  
---  
  
### Exercício 10: Gerenciador de Estoque  
  
**Enunciado:** Implemente um `GerenciadorEstoque` que depende de um `RepositorioProduto`. Deve verificar disponibilidade, reservar itens e atualizar quantidades.  
  
**Solução:**  
  
```java  
// Classes e interfaces  
class Produto {  
    private String codigo;  
    private String nome;  
    private int quantidade;  
  
    public Produto(String codigo, String nome, int quantidade) {  
        this.codigo = codigo;  
        this.nome = nome;  
        this.quantidade = quantidade;  
    }  
  
    // getters e setters...  
    public String getCodigo() { return codigo; }  
    public int getQuantidade() { return quantidade; }  
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }  
}  
  
interface RepositorioProduto {  
    Optional<Produto> buscarPorCodigo(String codigo);  
    void atualizar(Produto produto);  
}  
  
public class GerenciadorEstoque {  
    private RepositorioProduto repositorio;  
  
    public GerenciadorEstoque(RepositorioProduto repositorio) {  
        this.repositorio = repositorio;  
    }  
  
    public boolean verificarDisponibilidade(String codigo, int quantidadeDesejada) {  
        Optional<Produto> produto = repositorio.buscarPorCodigo(codigo);  
        return produto.map(p -> p.getQuantidade() >= quantidadeDesejada).orElse(false);  
    }  
  
    public boolean reservarItens(String codigo, int quantidade) {  
        Optional<Produto> produtoOpt = repositorio.buscarPorCodigo(codigo);  
  
        if (produtoOpt.isEmpty()) {  
            return false;  
        }  
  
        Produto produto = produtoOpt.get();  
        if (produto.getQuantidade() < quantidade) {  
            return false;  
        }  
  
        produto.setQuantidade(produto.getQuantidade() - quantidade);  
        repositorio.atualizar(produto);  
        return true;  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class GerenciadorEstoqueTest {  
  
    @Mock  
    private RepositorioProduto repositorio;  
  
    @InjectMocks  
    private GerenciadorEstoque gerenciador;  
  
    @Test  
    void deveVerificarDisponibilidadeComSucesso() {  
        // Arrange  
        String codigo = "PROD001";  
        Produto produto = new Produto(codigo, "Produto Teste", 10);  
        when(repositorio.buscarPorCodigo(codigo)).thenReturn(Optional.of(produto));  
  
        // Act & Assert  
        assertTrue(gerenciador.verificarDisponibilidade(codigo, 5));  
        assertFalse(gerenciador.verificarDisponibilidade(codigo, 15));  
    }  
  
    @Test  
    void deveRetornarFalsoParaProdutoInexistente() {  
        // Arrange  
        when(repositorio.buscarPorCodigo("INEXISTENTE")).thenReturn(Optional.empty());  
  
        // Act & Assert  
        assertFalse(gerenciador.verificarDisponibilidade("INEXISTENTE", 1));  
    }  
  
    @Test  
    void deveReservarItensComSucesso() {  
        // Arrange  
        String codigo = "PROD001";  
        Produto produto = new Produto(codigo, "Produto", 10);  
        when(repositorio.buscarPorCodigo(codigo)).thenReturn(Optional.of(produto));  
  
        // Act  
        boolean resultado = gerenciador.reservarItens(codigo, 3);  
  
        // Assert  
        assertTrue(resultado);  
        assertEquals(7, produto.getQuantidade());  
        verify(repositorio).atualizar(produto);  
    }  
  
    @Test  
    void naoDeveReservarMaisItensQueDisponivel() {  
        // Arrange  
        Produto produto = new Produto("PROD001", "Produto", 5);  
        when(repositorio.buscarPorCodigo("PROD001")).thenReturn(Optional.of(produto));  
  
        // Act  
        boolean resultado = gerenciador.reservarItens("PROD001", 10);  
  
        // Assert  
        assertFalse(resultado);  
        assertEquals(5, produto.getQuantidade()); // Não deve alterar  
        verify(repositorio, never()).atualizar(any(Produto.class));  
    }  
}  
```  
  
---  
  
## Exercícios 11-15: Mocks Avançados  
  
### Exercício 11: Sistema de Cache  
  
**Enunciado:** Implemente um `SistemaCache` que depende de um `RepositorioDados` e um `Cache`. Se o dado estiver no cache, não deve consultar o repositório. Teste com mocks verificando a ordem das chamadas.  
  
**Solução:**  
  
```java  
// Interfaces  
interface Cache {  
    Object get(String chave);  
    void put(String chave, Object valor);  
    boolean containsKey(String chave);  
}  
  
interface RepositorioDados {  
    String buscarDados(String id);  
}  
  
public class SistemaCache {  
    private Cache cache;  
    private RepositorioDados repositorio;  
  
    public SistemaCache(Cache cache, RepositorioDados repositorio) {  
        this.cache = cache;  
        this.repositorio = repositorio;  
    }  
  
    public String obterDados(String id) {  
        String chaveCache = "dados_" + id;  
  
        if (cache.containsKey(chaveCache)) {  
            return (String) cache.get(chaveCache);  
        }  
  
        String dados = repositorio.buscarDados(id);  
        if (dados != null) {  
            cache.put(chaveCache, dados);  
        }  
  
        return dados;  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class SistemaCacheTest {  
  
    @Mock  
    private Cache cache;  
  
    @Mock  
    private RepositorioDados repositorio;  
  
    @InjectMocks  
    private SistemaCache sistema;  
  
    @Test  
    void deveRetornarDadosDoCache() {  
        // Arrange  
        String id = "123";  
        String dadosCache = "dados do cache";  
        when(cache.containsKey("dados_123")).thenReturn(true);  
        when(cache.get("dados_123")).thenReturn(dadosCache);  
  
        // Act  
        String resultado = sistema.obterDados(id);  
  
        // Assert  
        assertEquals(dadosCache, resultado);  
        verify(cache).containsKey("dados_123");  
        verify(cache).get("dados_123");  
        verify(repositorio, never()).buscarDados(anyString());  
    }  
  
    @Test  
    void deveBuscarNoRepositorioQuandoNaoEstiverNoCache() {  
        // Arrange  
        String id = "456";  
        String dadosRepositorio = "dados do repositorio";  
        when(cache.containsKey("dados_456")).thenReturn(false);  
        when(repositorio.buscarDados(id)).thenReturn(dadosRepositorio);  
  
        // Act  
        String resultado = sistema.obterDados(id);  
  
        // Assert  
        assertEquals(dadosRepositorio, resultado);  
  
        InOrder inOrder = inOrder(cache, repositorio);  
        inOrder.verify(cache).containsKey("dados_456");  
        inOrder.verify(repositorio).buscarDados(id);  
        inOrder.verify(cache).put("dados_456", dadosRepositorio);  
    }  
  
    @Test  
    void naoDeveAdicionarNoCacheQuandoDadosNulos() {  
        // Arrange  
        when(cache.containsKey(anyString())).thenReturn(false);  
        when(repositorio.buscarDados("789")).thenReturn(null);  
  
        // Act  
        String resultado = sistema.obterDados("789");  
  
        // Assert  
        assertNull(resultado);  
        verify(cache, never()).put(anyString(), any());  
    }  
}  
```  
  
---  
  
### Exercício 12: Processador de Pedidos  
  
**Enunciado:** Desenvolva um `ProcessadorPedidos` que depende de `ValidadorPedido`, `CalculadoraFrete` e `ServicoEntrega`. Deve validar, calcular frete e agendar entrega.  
  
**Solução:**  
  
```java  
// Classes e interfaces  
class Pedido {  
    private String id;  
    private String destino;  
    private double valor;  
    private List<String> itens;  
  
    public Pedido(String id, String destino, double valor, List<String> itens) {  
        this.id = id;  
        this.destino = destino;  
        this.valor = valor;  
        this.itens = itens;  
    }  
  
    // getters...  
    public String getId() { return id; }  
    public String getDestino() { return destino; }  
    public double getValor() { return valor; }  
    public List<String> getItens() { return itens; }  
}  
  
class ResultadoPedido {  
    private boolean sucesso;  
    private String codigoRastreamento;  
    private double valorTotal;  
  
    public ResultadoPedido(boolean sucesso, String codigoRastreamento, double valorTotal) {  
        this.sucesso = sucesso;  
        this.codigoRastreamento = codigoRastreamento;  
        this.valorTotal = valorTotal;  
    }  
  
    // getters...  
    public boolean isSucesso() { return sucesso; }  
    public String getCodigoRastreamento() { return codigoRastreamento; }  
    public double getValorTotal() { return valorTotal; }  
}  
  
interface ValidadorPedido {  
    boolean validar(Pedido pedido);  
}  
  
interface CalculadoraFrete {  
    double calcularFrete(String destino, double peso);  
}  
  
interface ServicoEntrega {  
    String agendarEntrega(Pedido pedido, double valorTotal);  
}  
  
public class ProcessadorPedidos {  
    private ValidadorPedido validador;  
    private CalculadoraFrete calculadora;  
    private ServicoEntrega servicoEntrega;  
  
    public ProcessadorPedidos(ValidadorPedido validador, CalculadoraFrete calculadora, ServicoEntrega servicoEntrega) {  
        this.validador = validador;  
        this.calculadora = calculadora;  
        this.servicoEntrega = servicoEntrega;  
    }  
  
    public ResultadoPedido processarPedido(Pedido pedido) {  
        if (!validador.validar(pedido)) {  
            return new ResultadoPedido(false, null, 0);  
        }  
  
        double frete = calculadora.calcularFrete(pedido.getDestino(), pedido.getItens().size());  
        double valorTotal = pedido.getValor() + frete;  
  
        String codigoRastreamento = servicoEntrega.agendarEntrega(pedido, valorTotal);  
  
        return new ResultadoPedido(true, codigoRastreamento, valorTotal);  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class ProcessadorPedidosTest {  
  
    @Mock  
    private ValidadorPedido validador;  
  
    @Mock  
    private CalculadoraFrete calculadora;  
  
    @Mock  
    private ServicoEntrega servicoEntrega;  
  
    @InjectMocks  
    private ProcessadorPedidos processador;  
  
    @Test  
    void deveProcessarPedidoComSucesso() {  
        // Arrange  
        Pedido pedido = new Pedido("PED001", "São Paulo", 100.0, Arrays.asList("item1", "item2"));  
        when(validador.validar(pedido)).thenReturn(true);  
        when(calculadora.calcularFrete("São Paulo", 2)).thenReturn(15.0);  
        when(servicoEntrega.agendarEntrega(pedido, 115.0)).thenReturn("TRACK123");  
  
        // Act  
        ResultadoPedido resultado = processador.processarPedido(pedido);  
  
        // Assert  
        assertTrue(resultado.isSucesso());  
        assertEquals("TRACK123", resultado.getCodigoRastreamento());  
        assertEquals(115.0, resultado.getValorTotal(), 0.001);  
  
        // Verificar ordem das chamadas  
        InOrder inOrder = inOrder(validador, calculadora, servicoEntrega);  
        inOrder.verify(validador).validar(pedido);  
        inOrder.verify(calculadora).calcularFrete("São Paulo", 2);  
        inOrder.verify(servicoEntrega).agendarEntrega(pedido, 115.0);  
    }  
  
    @Test  
    void deveRejeitarPedidoInvalido() {  
        // Arrange  
        Pedido pedido = new Pedido("PED002", "", -10.0, Arrays.asList());  
        when(validador.validar(pedido)).thenReturn(false);  
  
        // Act  
        ResultadoPedido resultado = processador.processarPedido(pedido);  
  
        // Assert  
        assertFalse(resultado.isSucesso());  
        assertNull(resultado.getCodigoRastreamento());  
        assertEquals(0, resultado.getValorTotal());  
  
        verify(validador).validar(pedido);  
        verify(calculadora, never()).calcularFrete(anyString(), anyDouble());  
        verify(servicoEntrega, never()).agendarEntrega(any(Pedido.class), anyDouble());  
    }  
}  
```  
  
---  
  
## Exercícios 13-15: Mocks com Comportamentos Complexos  
  
### Exercício 13: Sistema de Backup  
  
**Enunciado:** Crie um `SistemaBackup` que depende de `ArmazenamentoLocal` e `ArmazenamentoNuvem`. Deve tentar backup local primeiro, e se falhar, usar a nuvem. Teste os diferentes cenários de falha.  
  
**Solução:**  
  
```java  
// Interfaces  
interface ArmazenamentoLocal {  
    boolean salvar(String arquivo, byte[] dados);  
    boolean estaDisponivel();  
}  
  
interface ArmazenamentoNuvem {  
    boolean upload(String arquivo, byte[] dados);  
}  
  
public class SistemaBackup {  
    private ArmazenamentoLocal local;  
    private ArmazenamentoNuvem nuvem;  
  
    public SistemaBackup(ArmazenamentoLocal local, ArmazenamentoNuvem nuvem) {  
        this.local = local;  
        this.nuvem = nuvem;  
    }  
  
    public String realizarBackup(String nomeArquivo, byte[] dados) {  
        if (local.estaDisponivel() && local.salvar(nomeArquivo, dados)) {  
            return "BACKUP_LOCAL_" + nomeArquivo;  
        }  
  
        if (nuvem.upload(nomeArquivo, dados)) {  
            return "BACKUP_NUVEM_" + nomeArquivo;  
        }  
  
        throw new RuntimeException("Falha em todos os backups");  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class SistemaBackupTest {  
  
    @Mock  
    private ArmazenamentoLocal local;  
  
    @Mock  
    private ArmazenamentoNuvem nuvem;  
  
    @InjectMocks  
    private SistemaBackup sistema;  
  
    @Test  
    void deveUsarBackupLocal() {  
        // Arrange  
        String arquivo = "dados.txt";  
        byte[] dados = "conteudo".getBytes();  
        when(local.estaDisponivel()).thenReturn(true);  
        when(local.salvar(arquivo, dados)).thenReturn(true);  
  
        // Act  
        String resultado = sistema.realizarBackup(arquivo, dados);  
  
        // Assert  
        assertEquals("BACKUP_LOCAL_dados.txt", resultado);  
        verify(local).estaDisponivel();  
        verify(local).salvar(arquivo, dados);  
        verify(nuvem, never()).upload(anyString(), any(byte[].class));  
    }  
  
    @Test  
    void deveUsarBackupNuvemQuandoLocalIndisponivel() {  
        // Arrange  
        String arquivo = "dados.txt";  
        byte[] dados = "conteudo".getBytes();  
        when(local.estaDisponivel()).thenReturn(false);  
        when(nuvem.upload(arquivo, dados)).thenReturn(true);  
  
        // Act  
        String resultado = sistema.realizarBackup(arquivo, dados);  
  
        // Assert  
        assertEquals("BACKUP_NUVEM_dados.txt", resultado);  
        verify(local).estaDisponivel();  
        verify(local, never()).salvar(anyString(), any(byte[].class));  
        verify(nuvem).upload(arquivo, dados);  
    }  
  
    @Test  
    void deveUsarBackupNuvemQuandoLocalFalha() {  
        // Arrange  
        String arquivo = "dados.txt";  
        byte[] dados = "conteudo".getBytes();  
        when(local.estaDisponivel()).thenReturn(true);  
        when(local.salvar(arquivo, dados)).thenReturn(false);  
        when(nuvem.upload(arquivo, dados)).thenReturn(true);  
  
        // Act  
        String resultado = sistema.realizarBackup(arquivo, dados);  
  
        // Assert  
        assertEquals("BACKUP_NUVEM_dados.txt", resultado);  
        verify(local).salvar(arquivo, dados);  
        verify(nuvem).upload(arquivo, dados);  
    }  
  
    @Test  
    void deveLancarExcecaoQuandoTodosBackupsFalham() {  
        // Arrange  
        when(local.estaDisponivel()).thenReturn(true);  
        when(local.salvar(anyString(), any(byte[].class))).thenReturn(false);  
        when(nuvem.upload(anyString(), any(byte[].class))).thenReturn(false);  
  
        // Act & Assert  
        RuntimeException exception = assertThrows(  
            RuntimeException.class,  
            () -> sistema.realizarBackup("arquivo.txt", "dados".getBytes())  
        );  
  
        assertEquals("Falha em todos os backups", exception.getMessage());  
    }  
}  
```  
  
---  
  
### Exercício 14: Processador de Relatórios  
  
**Enunciado:** Implemente um `ProcessadorRelatorios` que depende de `ColetorDados`, `FormatadorRelatorio` e `ServicoEmail`. Deve coletar dados, formatar e enviar por email. Use `@Spy` para testar comportamento parcial.  
  
**Solução:**  
  
```java  
// Interfaces e classes  
interface ColetorDados {  
    Map<String, Object> coletarDados(String periodo);  
}  
  
interface FormatadorRelatorio {  
    String formatarPDF(Map<String, Object> dados);  
    String formatarHTML(Map<String, Object> dados);  
}  
  
interface ServicoEmail {  
    boolean enviarRelatorio(String destinatario, String conteudo, String formato);  
}  
  
public class ProcessadorRelatorios {  
    private ColetorDados coletor;  
    private FormatadorRelatorio formatador;  
    private ServicoEmail servicoEmail;  
  
    public ProcessadorRelatorios(ColetorDados coletor, FormatadorRelatorio formatador, ServicoEmail servicoEmail) {  
        this.coletor = coletor;  
        this.formatador = formatador;  
        this.servicoEmail = servicoEmail;  
    }  
  
    public boolean gerarEEnviarRelatorio(String periodo, String destinatario, String formato) {  
        try {  
            Map<String, Object> dados = coletor.coletarDados(periodo);  
  
            if (dados.isEmpty()) {  
                return false;  
            }  
  
            String conteudo = formatarRelatorio(dados, formato);  
            return servicoEmail.enviarRelatorio(destinatario, conteudo, formato);  
  
        } catch (Exception e) {  
            return false;  
        }  
    }  
  
    protected String formatarRelatorio(Map<String, Object> dados, String formato) {  
        if ("PDF".equalsIgnoreCase(formato)) {  
            return formatador.formatarPDF(dados);  
        } else {  
            return formatador.formatarHTML(dados);  
        }  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class ProcessadorRelatoriosTest {  
  
    @Mock  
    private ColetorDados coletor;  
  
    @Mock  
    private FormatadorRelatorio formatador;  
  
    @Mock  
    private ServicoEmail servicoEmail;  
  
    @Spy  
    @InjectMocks  
    private ProcessadorRelatorios processador;  
  
    @Test  
    void deveGerarRelatorioHTMLComSucesso() {  
        // Arrange  
        String periodo = "2024-01";  
        String destinatario = "admin@empresa.com";  
        Map<String, Object> dados = Map.of("vendas", 1000, "clientes", 50);  
        String relatorioHTML = "<html>Relatório</html>";  
  
        when(coletor.coletarDados(periodo)).thenReturn(dados);  
        when(formatador.formatarHTML(dados)).thenReturn(relatorioHTML);  
        when(servicoEmail.enviarRelatorio(destinatario, relatorioHTML, "HTML")).thenReturn(true);  
  
        // Act  
        boolean resultado = processador.gerarEEnviarRelatorio(periodo, destinatario, "HTML");  
  
        // Assert  
        assertTrue(resultado);  
        verify(coletor).coletarDados(periodo);  
        verify(formatador).formatarHTML(dados);  
        verify(servicoEmail).enviarRelatorio(destinatario, relatorioHTML, "HTML");  
    }  
  
    @Test  
    void deveGerarRelatorioPDF() {  
        // Arrange  
        Map<String, Object> dados = Map.of("vendas", 1000);  
        String relatorioPDF = "PDF_CONTENT";  
  
        when(coletor.coletarDados("2024-01")).thenReturn(dados);  
        when(formatador.formatarPDF(dados)).thenReturn(relatorioPDF);  
        when(servicoEmail.enviarRelatorio(anyString(), eq(relatorioPDF), eq("PDF"))).thenReturn(true);  
  
        // Act  
        boolean resultado = processador.gerarEEnviarRelatorio("2024-01", "user@test.com", "PDF");  
  
        // Assert  
        assertTrue(resultado);  
        verify(formatador).formatarPDF(dados);  
        verify(formatador, never()).formatarHTML(any());  
    }  
  
    @Test  
    void deveRetornarFalsoQuandoDadosVazios() {  
        // Arrange  
        when(coletor.coletarDados(anyString())).thenReturn(Map.of());  
  
        // Act  
        boolean resultado = processador.gerarEEnviarRelatorio("2024-01", "user@test.com", "HTML");  
  
        // Assert  
        assertFalse(resultado);  
        verify(formatador, never()).formatarHTML(any());  
        verify(servicoEmail, never()).enviarRelatorio(anyString(), anyString(), anyString());  
    }  
  
    @Test  
    void deveTratarExcecaoNaColeta() {  
        // Arrange  
        when(coletor.coletarDados(anyString())).thenThrow(new RuntimeException("Erro na coleta"));  
  
        // Act  
        boolean resultado = processador.gerarEEnviarRelatorio("2024-01", "user@test.com", "HTML");  
  
        // Assert  
        assertFalse(resultado);  
    }  
  
    @Test  
    void deveUsarSpyParaTestarFormatacao() {  
        // Arrange  
        Map<String, Object> dados = Map.of("test", "data");  
  
        // Act  
        String resultado = processador.formatarRelatorio(dados, "PDF");  
  
        // Assert - usando spy para verificar chamada do método protegido  
        verify(processador).formatarRelatorio(dados, "PDF");  
        verify(formatador).formatarPDF(dados);  
    }  
}  
```  
  
---  
  
### Exercício 15: Sistema de Autenticação Multi-fator  
  
**Enunciado:** Desenvolva um `SistemaAutenticacaoMFA` que depende de `ValidadorCredenciais`, `GeradorTokenMFA` e `ServicoSMS`. Deve validar credenciais, gerar token e enviar SMS.  
  
**Solução:**  
  
```java  
// Interfaces e classes  
class CredenciaisUsuario {  
    private String usuario;  
    private String senha;  
  
    public CredenciaisUsuario(String usuario, String senha) {  
        this.usuario = usuario;  
        this.senha = senha;  
    }  
  
    public String getUsuario() { return usuario; }  
    public String getSenha() { return senha; }  
}  
  
class ResultadoAutenticacao {  
    private boolean sucesso;  
    private String tokenMFA;  
    private String mensagem;  
  
    public ResultadoAutenticacao(boolean sucesso, String tokenMFA, String mensagem) {  
        this.sucesso = sucesso;  
        this.tokenMFA = tokenMFA;  
        this.mensagem = mensagem;  
    }  
  
    public boolean isSucesso() { return sucesso; }  
    public String getTokenMFA() { return tokenMFA; }  
    public String getMensagem() { return mensagem; }  
}  
  
interface ValidadorCredenciais {  
    boolean validarCredenciais(CredenciaisUsuario credenciais);  
    String obterTelefone(String usuario);  
}  
  
interface GeradorTokenMFA {  
    String gerarToken();  
}  
  
interface ServicoSMS {  
    boolean enviarSMS(String telefone, String mensagem);  
}  
  
public class SistemaAutenticacaoMFA {  
    private ValidadorCredenciais validador;  
    private GeradorTokenMFA geradorToken;  
    private ServicoSMS servicoSMS;  
  
    public SistemaAutenticacaoMFA(ValidadorCredenciais validador, GeradorTokenMFA geradorToken, ServicoSMS servicoSMS) {  
        this.validador = validador;  
        this.geradorToken = geradorToken;  
        this.servicoSMS = servicoSMS;  
    }  
  
    public ResultadoAutenticacao autenticar(CredenciaisUsuario credenciais) {  
        if (!validador.validarCredenciais(credenciais)) {  
            return new ResultadoAutenticacao(false, null, "Credenciais inválidas");  
        }  
  
        String telefone = validador.obterTelefone(credenciais.getUsuario());  
        if (telefone == null) {  
            return new ResultadoAutenticacao(false, null, "Telefone não cadastrado");  
        }  
  
        String token = geradorToken.gerarToken();  
        String mensagem = "Seu código MFA: " + token;  
  
        if (servicoSMS.enviarSMS(telefone, mensagem)) {  
            return new ResultadoAutenticacao(true, token, "Token enviado");  
        } else {  
            return new ResultadoAutenticacao(false, null, "Falha ao enviar SMS");  
        }  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class SistemaAutenticacaoMFATest {  
  
    @Mock  
    private ValidadorCredenciais validador;  
  
    @Mock  
    private GeradorTokenMFA geradorToken;  
  
    @Mock  
    private ServicoSMS servicoSMS;  
  
    @InjectMocks  
    private SistemaAutenticacaoMFA sistema;  
  
    @Test  
    void deveAutenticarComSucesso() {  
        // Arrange  
        CredenciaisUsuario creds = new CredenciaisUsuario("user123", "senha123");  
        when(validador.validarCredenciais(creds)).thenReturn(true);  
        when(validador.obterTelefone("user123")).thenReturn("11999999999");  
        when(geradorToken.gerarToken()).thenReturn("123456");  
        when(servicoSMS.enviarSMS("11999999999", "Seu código MFA: 123456")).thenReturn(true);  
  
        // Act  
        ResultadoAutenticacao resultado = sistema.autenticar(creds);  
  
        // Assert  
        assertTrue(resultado.isSucesso());  
        assertEquals("123456", resultado.getTokenMFA());  
        assertEquals("Token enviado", resultado.getMensagem());  
  
        InOrder inOrder = inOrder(validador, geradorToken, servicoSMS);  
        inOrder.verify(validador).validarCredenciais(creds);  
        inOrder.verify(validador).obterTelefone("user123");  
        inOrder.verify(geradorToken).gerarToken();  
        inOrder.verify(servicoSMS).enviarSMS("11999999999", "Seu código MFA: 123456");  
    }  
  
    @Test  
    void deveRejeitarCredenciaisInvalidas() {  
        // Arrange  
        CredenciaisUsuario creds = new CredenciaisUsuario("userInvalido", "senhaErrada");  
        when(validador.validarCredenciais(creds)).thenReturn(false);  
  
        // Act  
        ResultadoAutenticacao resultado = sistema.autenticar(creds);  
  
        // Assert  
        assertFalse(resultado.isSucesso());  
        assertNull(resultado.getTokenMFA());  
        assertEquals("Credenciais inválidas", resultado.getMensagem());  
  
        verify(validador).validarCredenciais(creds);  
        verify(validador, never()).obterTelefone(anyString());  
        verify(geradorToken, never()).gerarToken();  
        verify(servicoSMS, never()).enviarSMS(anyString(), anyString());  
    }  
  
    @Test  
    void deveRejeitarUsuarioSemTelefone() {  
        // Arrange  
        CredenciaisUsuario creds = new CredenciaisUsuario("userSemTel", "senha123");  
        when(validador.validarCredenciais(creds)).thenReturn(true);  
        when(validador.obterTelefone("userSemTel")).thenReturn(null);  
  
        // Act  
        ResultadoAutenticacao resultado = sistema.autenticar(creds);  
  
        // Assert  
        assertFalse(resultado.isSucesso());  
        assertEquals("Telefone não cadastrado", resultado.getMensagem());  
        verify(geradorToken, never()).gerarToken();  
    }  
  
    @Test  
    void deveTratarFalhaNoEnvioSMS() {  
        // Arrange  
        CredenciaisUsuario creds = new CredenciaisUsuario("user123", "senha123");  
        when(validador.validarCredenciais(creds)).thenReturn(true);  
        when(validador.obterTelefone("user123")).thenReturn("11999999999");  
        when(geradorToken.gerarToken()).thenReturn("123456");  
        when(servicoSMS.enviarSMS(anyString(), anyString())).thenReturn(false);  
  
        // Act  
        ResultadoAutenticacao resultado = sistema.autenticar(creds);  
  
        // Assert  
        assertFalse(resultado.isSucesso());  
        assertEquals("Falha ao enviar SMS", resultado.getMensagem());  
    }  
}  
```  
  
---  
  
## Exercícios 16-20: Cenários Complexos e Integração  
  
### Exercício 16: Sistema de Reservas  
  
**Enunciado:** Crie um `SistemaReservas` que depende de `RepositorioQuarto`, `ProcessadorPagamento` e `ServicoNotificacao`. Deve verificar disponibilidade, processar pagamento e confirmar reserva.  
  
**Solução:**  
  
```java  
// Classes e interfaces  
class Quarto {  
    private String numero;  
    private boolean disponivel;  
    private double precoDiaria;  
  
    public Quarto(String numero, boolean disponivel, double precoDiaria) {  
        this.numero = numero;  
        this.disponivel = disponivel;  
        this.precoDiaria = precoDiaria;  
    }  
  
    public String getNumero() { return numero; }  
    public boolean isDisponivel() { return disponivel; }  
    public double getPrecoDiaria() { return precoDiaria; }  
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }  
}  
  
class Reserva {  
    private String id;  
    private String quartoNumero;  
    private String cliente;  
    private int dias;  
    private double valorTotal;  
  
    public Reserva(String id, String quartoNumero, String cliente, int dias, double valorTotal) {  
        this.id = id;  
        this.quartoNumero = quartoNumero;  
        this.cliente = cliente;  
        this.dias = dias;  
        this.valorTotal = valorTotal;  
    }  
  
    // getters...  
    public String getId() { return id; }  
    public double getValorTotal() { return valorTotal; }  
    public String getCliente() { return cliente; }  
}  
  
interface RepositorioQuarto {  
    Optional<Quarto> buscarPorNumero(String numero);  
    void atualizar(Quarto quarto);  
}  
  
interface ProcessadorPagamento {  
    boolean processarPagamento(String cartao, double valor);  
}  
  
interface ServicoNotificacao {  
    void enviarConfirmacao(String cliente, Reserva reserva);  
}  
  
public class SistemaReservas {  
    private RepositorioQuarto repositorioQuarto;  
    private ProcessadorPagamento processadorPagamento;  
    private ServicoNotificacao servicoNotificacao;  
  
    public SistemaReservas(RepositorioQuarto repositorioQuarto,  
                          ProcessadorPagamento processadorPagamento,  
                          ServicoNotificacao servicoNotificacao) {  
        this.repositorioQuarto = repositorioQuarto;  
        this.processadorPagamento = processadorPagamento;  
        this.servicoNotificacao = servicoNotificacao;  
    }  
  
    public Reserva criarReserva(String numeroQuarto, String cliente, int dias, String cartao) {  
        Optional<Quarto> quartoOpt = repositorioQuarto.buscarPorNumero(numeroQuarto);  
  
        if (quartoOpt.isEmpty()) {  
            throw new RuntimeException("Quarto não encontrado");  
        }  
  
        Quarto quarto = quartoOpt.get();  
        if (!quarto.isDisponivel()) {  
            throw new RuntimeException("Quarto não disponível");  
        }  
  
        double valorTotal = quarto.getPrecoDiaria() * dias;  
  
        if (!processadorPagamento.processarPagamento(cartao, valorTotal)) {  
            throw new RuntimeException("Falha no pagamento");  
        }  
  
        quarto.setDisponivel(false);  
        repositorioQuarto.atualizar(quarto);  
  
        String reservaId = "RES_" + System.currentTimeMillis();  
        Reserva reserva = new Reserva(reservaId, numeroQuarto, cliente, dias, valorTotal);  
  
        servicoNotificacao.enviarConfirmacao(cliente, reserva);  
  
        return reserva;  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class SistemaReservasTest {  
  
    @Mock  
    private RepositorioQuarto repositorioQuarto;  
  
    @Mock  
    private ProcessadorPagamento processadorPagamento;  
  
    @Mock  
    private ServicoNotificacao servicoNotificacao;  
  
    @InjectMocks  
    private SistemaReservas sistema;  
  
    @Test  
    void deveCriarReservaComSucesso() {  
        // Arrange  
        String numeroQuarto = "101";  
        String cliente = "João Silva";  
        int dias = 3;  
        String cartao = "1234-5678";  
  
        Quarto quarto = new Quarto(numeroQuarto, true, 100.0);  
        when(repositorioQuarto.buscarPorNumero(numeroQuarto)).thenReturn(Optional.of(quarto));  
        when(processadorPagamento.processarPagamento(cartao, 300.0)).thenReturn(true);  
  
        // Act  
        Reserva reserva = sistema.criarReserva(numeroQuarto, cliente, dias, cartao);  
  
        // Assert  
        assertNotNull(reserva);  
        assertEquals(numeroQuarto, reserva.getQuartoNumero());  
        assertEquals(cliente, reserva.getCliente());  
        assertEquals(300.0, reserva.getValorTotal());  
        assertFalse(quarto.isDisponivel());  
  
        InOrder inOrder = inOrder(repositorioQuarto, processadorPagamento, servicoNotificacao);  
        inOrder.verify(repositorioQuarto).buscarPorNumero(numeroQuarto);  
        inOrder.verify(processadorPagamento).processarPagamento(cartao, 300.0);  
        inOrder.verify(repositorioQuarto).atualizar(quarto);  
        inOrder.verify(servicoNotificacao).enviarConfirmacao(cliente, reserva);  
    }  
  
    @Test  
    void deveLancarExcecaoParaQuartoInexistente() {  
        // Arrange  
        when(repositorioQuarto.buscarPorNumero("999")).thenReturn(Optional.empty());  
  
        // Act & Assert  
        RuntimeException exception = assertThrows(  
            RuntimeException.class,  
            () -> sistema.criarReserva("999", "Cliente", 2, "1234")  
        );  
  
        assertEquals("Quarto não encontrado", exception.getMessage());  
        verify(processadorPagamento, never()).processarPagamento(anyString(), anyDouble());  
    }  
  
    @Test  
    void deveLancarExcecaoParaQuartoIndisponivel() {  
        // Arrange  
        Quarto quarto = new Quarto("101", false, 100.0);  
        when(repositorioQuarto.buscarPorNumero("101")).thenReturn(Optional.of(quarto));  
  
        // Act & Assert  
        RuntimeException exception = assertThrows(  
            RuntimeException.class,  
            () -> sistema.criarReserva("101", "Cliente", 2, "1234")  
        );  
  
        assertEquals("Quarto não disponível", exception.getMessage());  
        verify(processadorPagamento, never()).processarPagamento(anyString(), anyDouble());  
    }  
  
    @Test  
    void deveLancarExcecaoQuandoPagamentoFalha() {  
        // Arrange  
        Quarto quarto = new Quarto("101", true, 100.0);  
        when(repositorioQuarto.buscarPorNumero("101")).thenReturn(Optional.of(quarto));  
        when(processadorPagamento.processarPagamento("1234", 200.0)).thenReturn(false);  
  
        // Act & Assert  
        RuntimeException exception = assertThrows(  
            RuntimeException.class,  
            () -> sistema.criarReserva("101", "Cliente", 2, "1234")  
        );  
  
        assertEquals("Falha no pagamento", exception.getMessage());  
        assertTrue(quarto.isDisponivel()); // Quarto deve permanecer disponível  
        verify(repositorioQuarto, never()).atualizar(any(Quarto.class));  
        verify(servicoNotificacao, never()).enviarConfirmacao(anyString(), any(Reserva.class));  
    }  
}  
```  
  
---  
  
### Exercício 17: Analisador de Log  
  
**Enunciado:** Implemente um `AnalisadorLog` que depende de `LeitorArquivo` e `ProcessadorEstatisticas`. Deve ler logs, extrair métricas e gerar relatório de estatísticas.  
  
**Solução:**  
  
```java  
// Classes e interfaces  
class EntradaLog {  
    private String timestamp;  
    private String nivel;  
    private String mensagem;  
  
    public EntradaLog(String timestamp, String nivel, String mensagem) {  
        this.timestamp = timestamp;  
        this.nivel = nivel;  
        this.mensagem = mensagem;  
    }  
  
    public String getNivel() { return nivel; }  
    public String getMensagem() { return mensagem; }  
}  
  
class EstatisticasLog {  
    private int totalEntradas;  
    private Map<String, Integer> contagemPorNivel;  
    private List<String> errosMaisFrequentes;  
  
    public EstatisticasLog(int totalEntradas, Map<String, Integer> contagemPorNivel, List<String> errosMaisFrequentes) {  
        this.totalEntradas = totalEntradas;  
        this.contagemPorNivel = contagemPorNivel;  
        this.errosMaisFrequentes = errosMaisFrequentes;  
    }  
  
    public int getTotalEntradas() { return totalEntradas; }  
    public Map<String, Integer> getContagemPorNivel() { return contagemPorNivel; }  
}  
  
interface LeitorArquivo {  
    List<String> lerLinhas(String caminhoArquivo);  
}  
  
interface ProcessadorEstatisticas {  
    EstatisticasLog calcularEstatisticas(List<EntradaLog> logs);  
}  
  
public class AnalisadorLog {  
    private LeitorArquivo leitor;  
    private ProcessadorEstatisticas processador;  
  
    public AnalisadorLog(LeitorArquivo leitor, ProcessadorEstatisticas processador) {  
        this.leitor = leitor;  
        this.processador = processador;  
    }  
  
    public EstatisticasLog analisarArquivo(String caminhoArquivo) {  
        List<String> linhas = leitor.lerLinhas(caminhoArquivo);  
  
        if (linhas.isEmpty()) {  
            throw new RuntimeException("Arquivo vazio ou não encontrado");  
        }  
  
        List<EntradaLog> logs = linhas.stream()  
            .map(this::parsearLinha)  
            .filter(Objects::nonNull)  
            .collect(Collectors.toList());  
  
        return processador.calcularEstatisticas(logs);  
    }  
  
    private EntradaLog parsearLinha(String linha) {  
        String[] partes = linha.split(" ", 3);  
        if (partes.length >= 3) {  
            return new EntradaLog(partes[0], partes[1], partes[2]);  
        }  
        return null;  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class AnalisadorLogTest {  
  
    @Mock  
    private LeitorArquivo leitor;  
  
    @Mock  
    private ProcessadorEstatisticas processador;  
  
    @InjectMocks  
    private AnalisadorLog analisador;  
  
    @Test  
    void deveAnalisarArquivoComSucesso() {  
        // Arrange  
        String caminho = "/logs/app.log";  
        List<String> linhas = Arrays.asList(  
            "2024-01-01 INFO Aplicacao iniciada",  
            "2024-01-01 ERROR Erro de conexao",  
            "2024-01-01 WARN Cache invalidado"  
        );  
  
        EstatisticasLog estatisticas = new EstatisticasLog(3,  
            Map.of("INFO", 1, "ERROR", 1, "WARN", 1),  
            Arrays.asList("Erro de conexao"));  
  
        when(leitor.lerLinhas(caminho)).thenReturn(linhas);  
        when(processador.calcularEstatisticas(any())).thenReturn(estatisticas);  
  
        // Act  
        EstatisticasLog resultado = analisador.analisarArquivo(caminho);  
  
        // Assert  
        assertNotNull(resultado);  
        assertEquals(3, resultado.getTotalEntradas());  
        verify(leitor).lerLinhas(caminho);  
        verify(processador).calcularEstatisticas(any());  
    }  
  
    @Test  
    void deveLancarExcecaoParaArquivoVazio() {  
        // Arrange  
        when(leitor.lerLinhas(anyString())).thenReturn(Arrays.asList());  
  
        // Act & Assert  
        RuntimeException exception = assertThrows(  
            RuntimeException.class,  
            () -> analisador.analisarArquivo("/logs/vazio.log")  
        );  
  
        assertEquals("Arquivo vazio ou não encontrado", exception.getMessage());  
        verify(processador, never()).calcularEstatisticas(any());  
    }  
  
    @Test  
    void deveFiltrarLinhasInvalidas() {  
        // Arrange  
        List<String> linhas = Arrays.asList(  
            "2024-01-01 INFO Linha valida",  
            "linha_invalida",  
            "2024-01-01 ERROR Outra linha valida"  
        );  
  
        when(leitor.lerLinhas(anyString())).thenReturn(linhas);  
        when(processador.calcularEstatisticas(any())).thenReturn(  
            new EstatisticasLog(2, Map.of(), Arrays.asList()));  
  
        // Act  
        analisador.analisarArquivo("/test.log");  
  
        // Assert  
        ArgumentCaptor<List<EntradaLog>> captor = ArgumentCaptor.forClass(List.class);  
        verify(processador).calcularEstatisticas(captor.capture());  
  
        List<EntradaLog> logsProcessados = captor.getValue();  
        assertEquals(2, logsProcessados.size()); // Apenas linhas válidas  
    }  
}  
```  
  
---  
  
### Exercício 18: Sistema de Monitoramento  
  
**Enunciado:** Desenvolva um `MonitorSistema` que depende de `ColetorMetricas`, `VerificadorLimites` e `AlertaService`. Deve coletar métricas, verificar limites e enviar alertas quando necessário.  
  
**Solução:**  
  
```java  
// Classes e interfaces  
class Metricas {  
    private double cpu;  
    private double memoria;  
    private double disco;  
  
    public Metricas(double cpu, double memoria, double disco) {  
        this.cpu = cpu;  
        this.memoria = memoria;  
        this.disco = disco;  
    }  
  
    public double getCpu() { return cpu; }  
    public double getMemoria() { return memoria; }  
    public double getDisco() { return disco; }  
}  
  
class Alerta {  
    private String tipo;  
    private String mensagem;  
    private double valor;  
  
    public Alerta(String tipo, String mensagem, double valor) {  
        this.tipo = tipo;  
        this.mensagem = mensagem;  
        this.valor = valor;  
    }  
  
    public String getTipo() { return tipo; }  
    public String getMensagem() { return mensagem; }  
}  
  
interface ColetorMetricas {  
    Metricas coletarMetricas();  
}  
  
interface VerificadorLimites {  
    boolean verificarLimiteCPU(double cpu);  
    boolean verificarLimiteMemoria(double memoria);  
    boolean verificarLimiteDisco(double disco);  
}  
  
interface AlertaService {  
    void enviarAlerta(Alerta alerta);  
}  
  
public class MonitorSistema {  
    private ColetorMetricas coletor;  
    private VerificadorLimites verificador;  
    private AlertaService alertaService;  
  
    public MonitorSistema(ColetorMetricas coletor, VerificadorLimites verificador, AlertaService alertaService) {  
        this.coletor = coletor;  
        this.verificador = verificador;  
        this.alertaService = alertaService;  
    }  
  
    public List<Alerta> executarMonitoramento() {  
        Metricas metricas = coletor.coletarMetricas();  
        List<Alerta> alertas = new ArrayList<>();  
  
        if (!verificador.verificarLimiteCPU(metricas.getCpu())) {  
            Alerta alerta = new Alerta("CPU", "CPU acima do limite", metricas.getCpu());  
            alertas.add(alerta);  
            alertaService.enviarAlerta(alerta);  
        }  
  
        if (!verificador.verificarLimiteMemoria(metricas.getMemoria())) {  
            Alerta alerta = new Alerta("MEMORIA", "Memória acima do limite", metricas.getMemoria());  
            alertas.add(alerta);  
            alertaService.enviarAlerta(alerta);  
        }  
  
        if (!verificador.verificarLimiteDisco(metricas.getDisco())) {  
            Alerta alerta = new Alerta("DISCO", "Disco acima do limite", metricas.getDisco());  
            alertas.add(alerta);  
            alertaService.enviarAlerta(alerta);  
        }  
  
        return alertas;  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class MonitorSistemaTest {  
  
    @Mock  
    private ColetorMetricas coletor;  
  
    @Mock  
    private VerificadorLimites verificador;  
  
    @Mock  
    private AlertaService alertaService;  
  
    @InjectMocks  
    private MonitorSistema monitor;  
  
    @Test  
    void deveExecutarMonitoramentoSemAlertas() {  
        // Arrange  
        Metricas metricas = new Metricas(50.0, 60.0, 70.0);  
        when(coletor.coletarMetricas()).thenReturn(metricas);  
        when(verificador.verificarLimiteCPU(50.0)).thenReturn(true);  
        when(verificador.verificarLimiteMemoria(60.0)).thenReturn(true);  
        when(verificador.verificarLimiteDisco(70.0)).thenReturn(true);  
  
        // Act  
        List<Alerta> alertas = monitor.executarMonitoramento();  
  
        // Assert  
        assertTrue(alertas.isEmpty());  
        verify(alertaService, never()).enviarAlerta(any(Alerta.class));  
    }  
  
    @Test  
    void deveGerarAlertaDeCPU() {  
        // Arrange  
        Metricas metricas = new Metricas(95.0, 50.0, 60.0);  
        when(coletor.coletarMetricas()).thenReturn(metricas);  
        when(verificador.verificarLimiteCPU(95.0)).thenReturn(false);  
        when(verificador.verificarLimiteMemoria(50.0)).thenReturn(true);  
        when(verificador.verificarLimiteDisco(60.0)).thenReturn(true);  
  
        // Act  
        List<Alerta> alertas = monitor.executarMonitoramento();  
  
        // Assert  
        assertEquals(1, alertas.size());  
        assertEquals("CPU", alertas.get(0).getTipo());  
        verify(alertaService, times(1)).enviarAlerta(any(Alerta.class));  
    }  
  
    @Test  
    void deveGerarMultiplosAlertas() {  
        // Arrange  
        Metricas metricas = new Metricas(95.0, 90.0, 85.0);  
        when(coletor.coletarMetricas()).thenReturn(metricas);  
        when(verificador.verificarLimiteCPU(95.0)).thenReturn(false);  
        when(verificador.verificarLimiteMemoria(90.0)).thenReturn(false);  
        when(verificador.verificarLimiteDisco(85.0)).thenReturn(false);  
  
        // Act  
        List<Alerta> alertas = monitor.executarMonitoramento();  
  
        // Assert  
        assertEquals(3, alertas.size());  
        verify(alertaService, times(3)).enviarAlerta(any(Alerta.class));  
  
        // Verificar tipos dos alertas  
        Set<String> tiposAlertas = alertas.stream()  
            .map(Alerta::getTipo)  
            .collect(Collectors.toSet());  
        assertTrue(tiposAlertas.contains("CPU"));  
        assertTrue(tiposAlertas.contains("MEMORIA"));  
        assertTrue(tiposAlertas.contains("DISCO"));  
    }  
}  
```  
  
---  
  
### Exercício 19: Sistema de Workflow  
  
**Enunciado:** Crie um `SistemaWorkflow` que depende de `ValidadorEtapa`, `ExecutorTarefa` e `RepositorioWorkflow`. Deve executar etapas sequencialmente e salvar progresso.  
  
**Solução:**  
  
```java  
// Classes e interfaces  
class Etapa {  
    private String id;  
    private String nome;  
    private Map<String, Object> parametros;  
  
    public Etapa(String id, String nome, Map<String, Object> parametros) {  
        this.id = id;  
        this.nome = nome;  
        this.parametros = parametros;  
    }  
  
    public String getId() { return id; }  
    public String getNome() { return nome; }  
    public Map<String, Object> getParametros() { return parametros; }  
}  
  
class Workflow {  
    private String id;  
    private List<Etapa> etapas;  
    private int etapaAtual;  
  
    public Workflow(String id, List<Etapa> etapas) {  
        this.id = id;  
        this.etapas = etapas;  
        this.etapaAtual = 0;  
    }  
  
    public String getId() { return id; }  
    public List<Etapa> getEtapas() { return etapas; }  
    public int getEtapaAtual() { return etapaAtual; }  
    public void setEtapaAtual(int etapaAtual) { this.etapaAtual = etapaAtual; }  
  
    public boolean isCompleto() {  
        return etapaAtual >= etapas.size();  
    }  
}  
  
interface ValidadorEtapa {  
    boolean validarEtapa(Etapa etapa);  
}  
  
interface ExecutorTarefa {  
    boolean executarEtapa(Etapa etapa);  
}  
  
interface RepositorioWorkflow {  
    void salvarProgresso(Workflow workflow);  
}  
  
public class SistemaWorkflow {  
    private ValidadorEtapa validador;  
    private ExecutorTarefa executor;  
    private RepositorioWorkflow repositorio;  
  
    public SistemaWorkflow(ValidadorEtapa validador, ExecutorTarefa executor, RepositorioWorkflow repositorio) {  
        this.validador = validador;  
        this.executor = executor;  
        this.repositorio = repositorio;  
    }  
  
    public boolean executarProximaEtapa(Workflow workflow) {  
        if (workflow.isCompleto()) {  
            return true;  
        }  
  
        Etapa etapaAtual = workflow.getEtapas().get(workflow.getEtapaAtual());  
  
        if (!validador.validarEtapa(etapaAtual)) {  
            return false;  
        }  
  
        if (!executor.executarEtapa(etapaAtual)) {  
            return false;  
        }  
  
        workflow.setEtapaAtual(workflow.getEtapaAtual() + 1);  
        repositorio.salvarProgresso(workflow);  
  
        return true;  
    }  
  
    public boolean executarWorkflowCompleto(Workflow workflow) {  
        while (!workflow.isCompleto()) {  
            if (!executarProximaEtapa(workflow)) {  
                return false;  
            }  
        }  
        return true;  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class SistemaWorkflowTest {  
  
    @Mock  
    private ValidadorEtapa validador;  
  
    @Mock  
    private ExecutorTarefa executor;  
  
    @Mock  
    private RepositorioWorkflow repositorio;  
  
    @InjectMocks  
    private SistemaWorkflow sistema;  
  
    @Test  
    void deveExecutarProximaEtapaComSucesso() {  
        // Arrange  
        Etapa etapa = new Etapa("1", "Primeira Etapa", Map.of());  
        Workflow workflow = new Workflow("WF001", Arrays.asList(etapa));  
  
        when(validador.validarEtapa(etapa)).thenReturn(true);  
        when(executor.executarEtapa(etapa)).thenReturn(true);  
  
        // Act  
        boolean resultado = sistema.executarProximaEtapa(workflow);  
  
        // Assert  
        assertTrue(resultado);  
        assertEquals(1, workflow.getEtapaAtual());  
  
        InOrder inOrder = inOrder(validador, executor, repositorio);  
        inOrder.verify(validador).validarEtapa(etapa);  
        inOrder.verify(executor).executarEtapa(etapa);  
        inOrder.verify(repositorio).salvarProgresso(workflow);  
    }  
  
    @Test  
    void deveRetornarTrueParaWorkflowJaCompleto() {  
        // Arrange  
        Workflow workflow = new Workflow("WF002", Arrays.asList());  
  
        // Act  
        boolean resultado = sistema.executarProximaEtapa(workflow);  
  
        // Assert  
        assertTrue(resultado);  
        verify(validador, never()).validarEtapa(any());  
        verify(executor, never()).executarEtapa(any());  
        verify(repositorio, never()).salvarProgresso(any());  
    }  
  
    @Test  
    void deveFalharQuandoValidacaoFalha() {  
        // Arrange  
        Etapa etapa = new Etapa("1", "Etapa Inválida", Map.of());  
        Workflow workflow = new Workflow("WF003", Arrays.asList(etapa));  
  
        when(validador.validarEtapa(etapa)).thenReturn(false);  
  
        // Act  
        boolean resultado = sistema.executarProximaEtapa(workflow);  
  
        // Assert  
        assertFalse(resultado);  
        assertEquals(0, workflow.getEtapaAtual()); // Não deve avançar  
        verify(executor, never()).executarEtapa(any());  
        verify(repositorio, never()).salvarProgresso(any());  
    }  
  
    @Test  
    void deveExecutarWorkflowCompletoComSucesso() {  
        // Arrange  
        Etapa etapa1 = new Etapa("1", "Etapa 1", Map.of());  
        Etapa etapa2 = new Etapa("2", "Etapa 2", Map.of());  
        Workflow workflow = new Workflow("WF004", Arrays.asList(etapa1, etapa2));  
  
        when(validador.validarEtapa(any())).thenReturn(true);  
        when(executor.executarEtapa(any())).thenReturn(true);  
  
        // Act  
        boolean resultado = sistema.executarWorkflowCompleto(workflow);  
  
        // Assert  
        assertTrue(resultado);  
        assertTrue(workflow.isCompleto());  
        verify(validador, times(2)).validarEtapa(any());  
        verify(executor, times(2)).executarEtapa(any());  
        verify(repositorio, times(2)).salvarProgresso(workflow);  
    }  
  
    @Test  
    void deveInterromperWorkflowQuandoEtapaFalha() {  
        // Arrange  
        Etapa etapa1 = new Etapa("1", "Etapa 1", Map.of());  
        Etapa etapa2 = new Etapa("2", "Etapa 2", Map.of());  
        Workflow workflow = new Workflow("WF005", Arrays.asList(etapa1, etapa2));  
  
        when(validador.validarEtapa(etapa1)).thenReturn(true);  
        when(executor.executarEtapa(etapa1)).thenReturn(false);  
  
        // Act  
        boolean resultado = sistema.executarWorkflowCompleto(workflow);  
  
        // Assert  
        assertFalse(resultado);  
        assertEquals(0, workflow.getEtapaAtual());  
        verify(validador, times(1)).validarEtapa(etapa1);  
        verify(executor, times(1)).executarEtapa(etapa1);  
        verify(repositorio, never()).salvarProgresso(workflow);  
    }  
}  
```  
  
---  
  
### Exercício 20: Sistema de Processamento de Arquivos (Integração Complexa)  
  
**Enunciado:** Implemente um `ProcessadorArquivos` que depende de `ValidadorArquivo`, `ConversorFormato`, `CompactadorArquivo` e `ServicoUpload`. Deve validar, converter, compactar e fazer upload de arquivos. Use todos os recursos de mock aprendidos.  
  
**Solução:**  
  
```java  
// Classes e interfaces  
class Arquivo {  
    private String nome;  
    private String tipo;  
    private byte[] conteudo;  
    private long tamanho;  
  
    public Arquivo(String nome, String tipo, byte[] conteudo) {  
        this.nome = nome;  
        this.tipo = tipo;  
        this.conteudo = conteudo;  
        this.tamanho = conteudo.length;  
    }  
  
    public String getNome() { return nome; }  
    public String getTipo() { return tipo; }  
    public byte[] getConteudo() { return conteudo; }  
    public long getTamanho() { return tamanho; }  
    public void setConteudo(byte[] conteudo) {  
        this.conteudo = conteudo;  
        this.tamanho = conteudo.length;  
    }  
}  
  
class ResultadoProcessamento {  
    private boolean sucesso;  
    private String urlFinal;  
    private long tamanhoOriginal;  
    private long tamanhoFinal;  
    private String mensagem;  
  
    public ResultadoProcessamento(boolean sucesso, String urlFinal, long tamanhoOriginal, long tamanhoFinal, String mensagem) {  
        this.sucesso = sucesso;  
        this.urlFinal = urlFinal;  
        this.tamanhoOriginal = tamanhoOriginal;  
        this.tamanhoFinal = tamanhoFinal;  
        this.mensagem = mensagem;  
    }  
  
    // getters...  
    public boolean isSucesso() { return sucesso; }  
    public String getUrlFinal() { return urlFinal; }  
    public long getTamanhoOriginal() { return tamanhoOriginal; }  
    public long getTamanhoFinal() { return tamanhoFinal; }  
    public String getMensagem() { return mensagem; }  
}  
  
interface ValidadorArquivo {  
    boolean validarTamanho(Arquivo arquivo, long tamanhoMaximo);  
    boolean validarTipo(Arquivo arquivo, List<String> tiposPermitidos);  
}  
  
interface ConversorFormato {  
    Arquivo converterPara(Arquivo arquivo, String novoFormato);  
}  
  
interface CompactadorArquivo {  
    Arquivo compactar(Arquivo arquivo);  
    double calcularTaxaCompressao(long tamanhoOriginal, long tamanhoCompactado);  
}  
  
interface ServicoUpload {  
    String fazerUpload(Arquivo arquivo);  
}  
  
public class ProcessadorArquivos {  
    private ValidadorArquivo validador;  
    private ConversorFormato conversor;  
    private CompactadorArquivo compactador;  
    private ServicoUpload servicoUpload;  
  
    public ProcessadorArquivos(ValidadorArquivo validador, ConversorFormato conversor,  
                              CompactadorArquivo compactador, ServicoUpload servicoUpload) {  
        this.validador = validador;  
        this.conversor = conversor;  
        this.compactador = compactador;  
        this.servicoUpload = servicoUpload;  
    }  
  
    public ResultadoProcessamento processarArquivo(Arquivo arquivo, String formatoDestino,  
                                                  boolean deveCompactar, long tamanhoMaximo) {  
        long tamanhoOriginal = arquivo.getTamanho();  
  
        // Validar tamanho  
        if (!validador.validarTamanho(arquivo, tamanhoMaximo)) {  
            return new ResultadoProcessamento(false, null, tamanhoOriginal, 0, "Arquivo muito grande");  
        }  
  
        // Validar tipo  
        List<String> tiposPermitidos = Arrays.asList("PDF", "DOCX", "TXT", "JPG", "PNG");  
        if (!validador.validarTipo(arquivo, tiposPermitidos)) {  
            return new ResultadoProcessamento(false, null, tamanhoOriginal, 0, "Tipo de arquivo não permitido");  
        }  
  
        try {  
            // Converter formato se necessário  
            if (!arquivo.getTipo().equalsIgnoreCase(formatoDestino)) {  
                arquivo = conversor.converterPara(arquivo, formatoDestino);  
            }  
  
            // Compactar se solicitado  
            if (deveCompactar) {  
                arquivo = compactador.compactar(arquivo);  
            }  
  
            // Fazer upload  
            String url = servicoUpload.fazerUpload(arquivo);  
  
            return new ResultadoProcessamento(true, url, tamanhoOriginal, arquivo.getTamanho(), "Processamento concluído");  
  
        } catch (Exception e) {  
            return new ResultadoProcessamento(false, null, tamanhoOriginal, 0, "Erro no processamento: " + e.getMessage());  
        }  
    }  
}  
  
// Teste  
@ExtendWith(MockitoExtension.class)  
class ProcessadorArquivosTest {  
  
    @Mock  
    private ValidadorArquivo validador;  
  
    @Mock  
    private ConversorFormato conversor;  
  
    @Mock  
    private CompactadorArquivo compactador;  
  
    @Mock  
    private ServicoUpload servicoUpload;  
  
    @InjectMocks  
    private ProcessadorArquivos processador;  
  
    @Test  
    void deveProcessarArquivoCompletoComSucesso() {  
        // Arrange  
        byte[] conteudo = "conteudo do arquivo".getBytes();  
        Arquivo arquivo = new Arquivo("documento.txt", "TXT", conteudo);  
        Arquivo arquivoConvertido = new Arquivo("documento.pdf", "PDF", "pdf_content".getBytes());  
        Arquivo arquivoCompactado = new Arquivo("documento.pdf", "PDF", "compressed".getBytes());  
  
        when(validador.validarTamanho(arquivo, 1000000)).thenReturn(true);  
        when(validador.validarTipo(eq(arquivo), any())).thenReturn(true);  
        when(conversor.converterPara(arquivo, "PDF")).thenReturn(arquivoConvertido);  
        when(compactador.compactar(arquivoConvertido)).thenReturn(arquivoCompactado);  
        when(servicoUpload.fazerUpload(arquivoCompactado)).thenReturn("https://storage.com/file123");  
  
        // Act  
        ResultadoProcessamento resultado = processador.processarArquivo(arquivo, "PDF", true, 1000000);  
  
        // Assert  
        assertTrue(resultado.isSucesso());  
        assertEquals("https://storage.com/file123", resultado.getUrlFinal());  
        assertEquals(conteudo.length, resultado.getTamanhoOriginal());  
        assertEquals(arquivoCompactado.getTamanho(), resultado.getTamanhoFinal());  
  
        InOrder inOrder = inOrder(validador, conversor, compactador, servicoUpload);  
        inOrder.verify(validador).validarTamanho(arquivo, 1000000);  
        inOrder.verify(validador).validarTipo(eq(arquivo), any());  
        inOrder.verify(conversor).converterPara(arquivo, "PDF");  
        inOrder.verify(compactador).compactar(arquivoConvertido);  
        inOrder.verify(servicoUpload).fazerUpload(arquivoCompactado);  
    }  
  
    @Test  
    void deveProcessarSemConversaoNemCompactacao() {  
        // Arrange  
        Arquivo arquivo = new Arquivo("documento.pdf", "PDF", "content".getBytes());  
        when(validador.validarTamanho(arquivo, 1000000)).thenReturn(true);  
        when(validador.validarTipo(eq(arquivo), any())).thenReturn(true);  
        when(servicoUpload.fazerUpload(arquivo)).thenReturn("https://storage.com/file456");  
  
        // Act  
        ResultadoProcessamento resultado = processador.processarArquivo(arquivo, "PDF", false, 1000000);  
  
        // Assert  
        assertTrue(resultado.isSucesso());  
        assertEquals("https://storage.com/file456", resultado.getUrlFinal());  
  
        verify(conversor, never()).converterPara(any(), anyString());  
        verify(compactador, never()).compactar(any());  
        verify(servicoUpload).fazerUpload(arquivo);  
    }  
  
    @Test  
    void deveRejeitarArquivoMuitoGrande() {  
        // Arrange  
        Arquivo arquivo = new Arquivo("arquivo_grande.txt", "TXT", new byte[1000]);  
        when(validador.validarTamanho(arquivo, 500)).thenReturn(false);  
  
        // Act  
        ResultadoProcessamento resultado = processador.processarArquivo(arquivo, "PDF", true, 500);  
  
        // Assert  
        assertFalse(resultado.isSucesso());  
        assertEquals("Arquivo muito grande", resultado.getMensagem());  
        verify(validador).validarTamanho(arquivo, 500);  
        verify(validador, never()).validarTipo(any(), any());  
        verify(conversor, never()).converterPara(any(), anyString());  
    }  
  
    @Test  
    void deveRejeitarTipoNaoPermitido() {  
        // Arrange  
        Arquivo arquivo = new Arquivo("virus.exe", "EXE", "malware".getBytes());  
        when(validador.validarTamanho(arquivo, 1000000)).thenReturn(true);  
        when(validador.validarTipo(eq(arquivo), any())).thenReturn(false);  
  
        // Act  
        ResultadoProcessamento resultado = processador.processarArquivo(arquivo, "EXE", false, 1000000);  
  
        // Assert  
        assertFalse(resultado.isSucesso());  
        assertEquals("Tipo de arquivo não permitido", resultado.getMensagem());  
        verify(conversor, never()).converterPara(any(), anyString());  
        verify(servicoUpload, never()).fazerUpload(any());  
    }  
  
    @Test  
    void deveTratarExcecaoNoProcessamento() {  
        // Arrange  
        Arquivo arquivo = new Arquivo("documento.txt", "TXT", "content".getBytes());  
        when(validador.validarTamanho(arquivo, 1000000)).thenReturn(true);  
        when(validador.validarTipo(eq(arquivo), any())).thenReturn(true);  
        when(conversor.converterPara(arquivo, "PDF")).thenThrow(new RuntimeException("Erro na conversão"));  
  
        // Act  
        ResultadoProcessamento resultado = processador.processarArquivo(arquivo, "PDF", false, 1000000);  
  
        // Assert  
        assertFalse(resultado.isSucesso());  
        assertTrue(resultado.getMensagem().contains("Erro no processamento"));  
        verify(compactador, never()).compactar(any());  
        verify(servicoUpload, never()).fazerUpload(any());  
    }  
  
    @Test  
    void deveUsarArgumentCaptorParaVerificarParametros() {  
        // Arrange  
        Arquivo arquivo = new Arquivo("test.txt", "TXT", "test".getBytes());  
        when(validador.validarTamanho(any(), anyLong())).thenReturn(true);  
        when(validador.validarTipo(any(), any())).thenReturn(true);  
        when(servicoUpload.fazerUpload(arquivo)).thenReturn("url");  
  
        // Act  
        processador.processarArquivo(arquivo, "TXT", false, 1000000);  
  
        // Assert  
        ArgumentCaptor<List<String>> tiposCaptor = ArgumentCaptor.forClass(List.class);  
        verify(validador).validarTipo(eq(arquivo), tiposCaptor.capture());  
  
        List<String> tiposPermitidos = tiposCaptor.getValue();  
        assertTrue(tiposPermitidos.contains("PDF"));  
        assertTrue(tiposPermitidos.contains("TXT"));  
        assertTrue(tiposPermitidos.contains("JPG"));  
    }  
}  
```  
  
---  
  
## Dicas Pedagógicas para a Aula  
  
### Conceitos Importantes a Destacar:  
  
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
  
4. **Boas Práticas Demonstradas:**  
   
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
  
### Sugestões para Ministrar a Aula:  
  
1. **Começar pelos exercícios 1-3** para fixar JUnit básico  
2. **Exercícios 6-8** para introduzir conceitos de mock  
3. **Exercícios 13-15** para técnicas avançadas  
4. **Exercício 20** como desafio final integrador  
  
Cada exercício pode ser usado como:  
  
- **Demonstração**: Professor resolve em tempo real  
- **Exercício guiado**: Alunos fazem junto com professor  
- **Prática individual**: Alunos resolvem sozinhos  
- **Desafio em grupo**: Trabalho colaborativo  
