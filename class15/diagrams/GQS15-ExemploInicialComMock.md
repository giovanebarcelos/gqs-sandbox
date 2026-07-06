Para usar JUnit e Mocking efetivamente no VS Code com Java, aqui está um guia completo:  
  
## Configuração Inicial  
  
**1. Extensões essenciais para VS Code:**  
  
- Extension Pack for Java (Microsoft)  
- Test Runner for Java  
- Java Test Runner  
  
**2. Dependências no projeto (Maven):**  
  
```xml  
<project xmlns="http://maven.apache.org/POM/4.0.0"  
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0  
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">  
  
    <modelVersion>4.0.0</modelVersion>  
  
    <!-- Informações do projeto -->  
    <groupId>com.exemplo</groupId>  
    <artifactId>meu-projeto</artifactId>  
    <version>1.0-SNAPSHOT</version>  
    <packaging>jar</packaging>  
  
    <!-- Propriedades -->  
    <properties>  
        <maven.compiler.source>17</maven.compiler.source>  
        <maven.compiler.target>17</maven.compiler.target>  
        <junit.jupiter.version>5.10.0</junit.jupiter.version>  
        <mockito.version>5.5.0</mockito.version>  
    </properties>  
  
    <!-- Dependências -->  
    <dependencies>  
        <!-- JUnit 5 -->  
        <dependency>  
            <groupId>org.junit.jupiter</groupId>  
            <artifactId>junit-jupiter</artifactId>  
            <version>${junit.jupiter.version}</version>  
            <scope>test</scope>  
        </dependency>  
  
        <!-- Mockito Core -->  
        <dependency>  
            <groupId>org.mockito</groupId>  
            <artifactId>mockito-core</artifactId>  
            <version>${mockito.version}</version>  
            <scope>test</scope>  
        </dependency>  
  
        <!-- Mockito + JUnit 5 Integration -->  
        <dependency>  
            <groupId>org.mockito</groupId>  
            <artifactId>mockito-junit-jupiter</artifactId>  
            <version>${mockito.version}</version>  
            <scope>test</scope>  
        </dependency>  
    </dependencies>  
  
    <!-- Plugins -->  
    <build>  
        <plugins>  
            <!-- Compilador Java -->  
            <plugin>  
                <groupId>org.apache.maven.plugins</groupId>  
                <artifactId>maven-compiler-plugin</artifactId>  
                <version>3.11.0</version>  
                <configuration>  
                    <source>${maven.compiler.source}</source>  
                    <target>${maven.compiler.target}</target>  
                </configuration>  
            </plugin>  
  
            <!-- Surefire para testes JUnit 5 -->  
            <plugin>  
                <groupId>org.apache.maven.plugins</groupId>  
                <artifactId>maven-surefire-plugin</artifactId>  
                <version>3.1.2</version>  
                <configuration>  
                    <useModulePath>false</useModulePath>  
                </configuration>  
            </plugin>  
        </plugins>  
    </build>  
</project>  
```  
  
## Estrutura de Pastas  
  
```  
src/  
├── main/java/  
│   └── com/exemplo/  
│       └── MinhaClasse.java  
└── test/java/  
    └── com/exemplo/  
        └── MinhaClasseTest.java  
```  
  
## Exemplo Prático com JUnit e Mockito## Dicas para usar no VS Code  
  
**1. Executar testes:**  
  
- Clique no ícone de "play" ao lado do método de teste  
- Use `Ctrl+Shift+P` → "Java: Run Tests"  
- Painel "Test Explorer" mostra todos os testes  
  
**2. Debug de testes:**  
  
- Clique no ícone de "debug" ao lado do teste  
- Adicione breakpoints clicando na margem esquerda  
- Use F10 (step over) e F11 (step into)  
  
**3. Atalhos úteis:**  
  
- `Ctrl+Shift+T`: Criar nova classe de teste  
- `F5`: Executar com debug  
- `Ctrl+F5`: Executar sem debug  
  
## Melhores Práticas  
  
**Estrutura de teste (AAA):**  
  
- **Arrange**: Configurar dados e mocks  
- **Act**: Executar método testado  
- **Assert**: Verificar resultados  
  
**Naming conventions:**  
  
- `deveRetornarUsuarioQuandoIdValido()`  
- `deveLancarExcecaoQuandoEmailInvalido()`  
  
**Verificações importantes:**  
  
- Use `verify()` para confirmar chamadas aos mocks  
- Use `never()` para garantir que algo NÃO foi chamado  
- Use `times(n)` para verificar número específico de chamadas  
- Use `InOrder` para verificar ordem das chamadas  
  
**Configurações úteis no settings.json do VS Code:**  
  
```json  
{  
    "java.test.report.position": "sideView",  
    "java.test.defaultConfig": "junit-jupiter",  
    "java.debug.settings.enableRunDebugCodeLens": true  
}  
```  
  
Essa abordagem te dará uma base sólida para testes eficazes no VS Code com Java!  
  
Código Prático  
  
// Classe de exemplo (src/main/java)  
public class UsuarioService {  
    private UsuarioRepository repository;  
    private EmailService emailService;  
  
    public UsuarioService(UsuarioRepository repository, EmailService emailService) {  
        this.repository = repository;  
        this.emailService = emailService;  
    }  
    
    public Usuario criarUsuario(String nome, String email) {  
        if (nome == null || nome.trim().isEmpty()) {  
            throw new IllegalArgumentException("Nome não pode ser vazio");  
        }  
    
        if (repository.existePorEmail(email)) {  
            throw new IllegalStateException("Email já existe");  
        }  
    
        Usuario usuario = new Usuario(nome, email);  
        Usuario salvo = repository.salvar(usuario);  
        emailService.enviarBoasVindas(email);  
    
        return salvo;  
    }  
    
    public Usuario buscarPorId(Long id) {  
        return repository.buscarPorId(id)  
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));  
    }  
  
}  
  
// Classes auxiliares  
class Usuario {  
    private String nome;  
    private String email;  
  
    public Usuario(String nome, String email) {  
        this.nome = nome;  
        this.email = email;  
    }  
    
    // getters e setters...  
  
}  
  
interface UsuarioRepository {  
    boolean existePorEmail(String email);  
    Usuario salvar(Usuario usuario);  
    Optional<Usuario> buscarPorId(Long id);  
}  
  
interface EmailService {  
    void enviarBoasVindas(String email);  
}  
  
// ===== TESTES (src/test/java) =====  
  
import org.junit.jupiter.api.*;  
import org.mockito.*;  
import org.mockito.junit.jupiter.MockitoExtension;  
import static org.junit.jupiter.api.Assertions.*;  
import static org.mockito.Mockito.*;  
  
@ExtendWith(MockitoExtension.class)  
class UsuarioServiceTest {  
  
    @Mock  
    private UsuarioRepository repository;  
    
    @Mock  
    private EmailService emailService;  
    
    @InjectMocks  
    private UsuarioService usuarioService;  
    
    @BeforeEach  
    void setUp() {  
        // Configurações que rodam antes de cada teste  
        System.out.println("Preparando teste...");  
    }  
    
    @AfterEach  
    void tearDown() {  
        // Limpeza após cada teste  
        System.out.println("Finalizando teste...");  
    }  
    
    @Test  
    @DisplayName("Deve criar usuário com sucesso")  
    void deveCriarUsuarioComSucesso() {  
        // Arrange (Preparação)  
        String nome = "João Silva";  
        String email = "joao@email.com";  
        Usuario usuarioEsperado = new Usuario(nome, email);  
    
        when(repository.existePorEmail(email)).thenReturn(false);  
        when(repository.salvar(any(Usuario.class))).thenReturn(usuarioEsperado);  
    
        // Act (Ação)  
        Usuario resultado = usuarioService.criarUsuario(nome, email);  
    
        // Assert (Verificação)  
        assertNotNull(resultado);  
        assertEquals(nome, resultado.getNome());  
        assertEquals(email, resultado.getEmail());  
    
        // Verificar interações com mocks  
        verify(repository).existePorEmail(email);  
        verify(repository).salvar(any(Usuario.class));  
        verify(emailService).enviarBoasVindas(email);  
    }  
    
    @Test  
    @DisplayName("Deve lançar exceção quando nome é vazio")  
    void deveLancarExcecaoQuandoNomeVazio() {  
        // Arrange  
        String nomeVazio = "";  
        String email = "teste@email.com";  
    
        // Act & Assert  
        IllegalArgumentException exception = assertThrows(  
            IllegalArgumentException.class,  
            () -> usuarioService.criarUsuario(nomeVazio, email)  
        );  
    
        assertEquals("Nome não pode ser vazio", exception.getMessage());  
    
        // Verificar que repository não foi chamado  
        verify(repository, never()).existePorEmail(anyString());  
        verify(repository, never()).salvar(any(Usuario.class));  
        verify(emailService, never()).enviarBoasVindas(anyString());  
    }  
    
    @Test  
    @DisplayName("Deve lançar exceção quando email já existe")  
    void deveLancarExcecaoQuandoEmailJaExiste() {  
        // Arrange  
        String nome = "Maria";  
        String email = "maria@email.com";  
    
        when(repository.existePorEmail(email)).thenReturn(true);  
    
        // Act & Assert  
        IllegalStateException exception = assertThrows(  
            IllegalStateException.class,  
            () -> usuarioService.criarUsuario(nome, email)  
        );  
    
        assertEquals("Email já existe", exception.getMessage());  
        verify(repository).existePorEmail(email);  
        verify(repository, never()).salvar(any(Usuario.class));  
    }  
    
    @ParameterizedTest  
    @DisplayName("Deve validar nomes inválidos")  
    @ValueSource(strings = {"", "   ", "\t", "\n"})  
    void deveValidarNomesInvalidos(String nomeInvalido) {  
        assertThrows(  
            IllegalArgumentException.class,  
            () -> usuarioService.criarUsuario(nomeInvalido, "teste@email.com")  
        );  
    }  
    
    @Test  
    @DisplayName("Deve buscar usuário por ID")  
    void deveBuscarUsuarioPorId() {  
        // Arrange  
        Long id = 1L;  
        Usuario usuario = new Usuario("João", "joao@email.com");  
    
        when(repository.buscarPorId(id)).thenReturn(Optional.of(usuario));  
    
        // Act  
        Usuario resultado = usuarioService.buscarPorId(id);  
    
        // Assert  
        assertNotNull(resultado);  
        assertEquals("João", resultado.getNome());  
        verify(repository).buscarPorId(id);  
    }  
    
    @Test  
    @DisplayName("Deve lançar exceção quando usuário não encontrado")  
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {  
        // Arrange  
        Long id = 999L;  
        when(repository.buscarPorId(id)).thenReturn(Optional.empty());  
    
        // Act & Assert  
        RuntimeException exception = assertThrows(  
            RuntimeException.class,  
            () -> usuarioService.buscarPorId(id)  
        );  
    
        assertEquals("Usuário não encontrado", exception.getMessage());  
    }  
    
    @Nested  
    @DisplayName("Testes de integração com email")  
    class TestesEmail {  
    
        @Test  
        @DisplayName("Deve enviar email após criar usuário")  
        void deveEnviarEmailAposCriarUsuario() {  
            // Arrange  
            String nome = "Ana";  
            String email = "ana@email.com";  
            Usuario usuario = new Usuario(nome, email);  
    
            when(repository.existePorEmail(email)).thenReturn(false);  
            when(repository.salvar(any(Usuario.class))).thenReturn(usuario);  
    
            // Act  
            usuarioService.criarUsuario(nome, email);  
    
            // Assert - verificar ordem das chamadas  
            InOrder inOrder = inOrder(repository, emailService);  
            inOrder.verify(repository).salvar(any(Usuario.class));  
            inOrder.verify(emailService).enviarBoasVindas(email);  
        }  
    
        @Test  
        @DisplayName("Deve falhar graciosamente se email não enviar")  
        void deveFalharGraciosamenteSeEmailNaoEnviar() {  
            // Arrange  
            String nome = "Carlos";  
            String email = "carlos@email.com";  
            Usuario usuario = new Usuario(nome, email);  
    
            when(repository.existePorEmail(email)).thenReturn(false);  
            when(repository.salvar(any(Usuario.class))).thenReturn(usuario);  
            doThrow(new RuntimeException("Falha no email")).when(emailService).enviarBoasVindas(email);  
    
            // Act & Assert  
            assertThrows(RuntimeException.class,  
                () -> usuarioService.criarUsuario(nome, email));  
        }  
    }  
  
}  
