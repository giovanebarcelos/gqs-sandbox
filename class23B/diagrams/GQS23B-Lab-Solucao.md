# Gabarito GQS23B — Laboratório SonarQube

**Aula 23B — Garantia da Qualidade de Software** | Prof. Giovane Barcelos
**Uso do professor.** O código deste gabarito foi compilado, testado e analisado num SonarQube Community Build 26.9 real: 23 testes verdes, **0 problemas**, cobertura **84,0%**, duplicação **0,0%**, ratings **A/A/A**, Quality Gate **OK**.

---

## Resultado medido — antes e depois

| Métrica | Java com defeitos | Java corrigido |
|---|---|---|
| Linhas de código (ncloc) | 220 | 209 |
| Problemas | 20 | **0** |
| Bugs | 3 | 0 |
| Vulnerabilidades | 2 | 0 |
| Code Smells | 15 | 0 |
| Cobertura (Sonar) | 44,7% | **84,0%** |
| Cobertura de linha (JaCoCo) | 46,2% | 88,2% |
| Linhas duplicadas | 14,3% (2 blocos) | **0,0%** |
| Débito técnico | 134 min (2h14) | **0 min** |
| Complexidade cognitiva total | 47 | 18 |
| Confiabilidade | E | **A** |
| Segurança | E | **A** |
| Manutenibilidade | A | **A** |
| Quality Gate "GQS — Código Legado" | **ERROR** | **OK** |

---

## Etapa 3.1 — Tabela de regras (gabarito)

| Regra | Onde está | O que o Sonar diz | Qualidade impactada |
|---|---|---|---|
| `java:S6437` | L62, `SENHA_BD` no `getConnection` | *Revoke and change this password, as it is compromised* | Segurança — **Blocker** |
| `java:S2095` | L62 e L63, `Connection` e `Statement` | *Use try-with-resources or close this "..." in a "finally" clause* | Confiabilidade — **Blocker** (2 ocorrências) |
| `java:S2077` | L64, `executeQuery` com `+ titulo` | *Make sure using a dynamically formatted SQL query is safe here* | Segurança — Medium; Manutenibilidade — Low |
| `java:S108` | L68, `catch` vazio | *Remove this block of code, fill it in, or add a comment explaining why* | Manutenibilidade — Medium |
| `java:S4973` | L83, `livro == titulo` | *Strings and Boxed types should be compared using "equals()"* | Confiabilidade — Medium |
| `java:S3776` | L110, `calcularMulta` | *Reduce its Cognitive Complexity from 35 to the 15 allowed* | Manutenibilidade — High |
| `java:S1192` | L70, L163, L169 | *Define a constant instead of duplicating this literal N times* | Manutenibilidade — High (3 ocorrências) |
| `java:S1481` / `java:S1854` | L111 (`hoje`), L112 (`multa`) | *Remove this unused local variable* / *useless assignment* | Manutenibilidade — Low / Medium |
| `java:S106` | L217-L220, `System.out` | *Replace this use of System.out by a logger* | Manutenibilidade — Medium (4 ocorrências) |
| `java:S101` / `java:S1220` | classe e arquivo | *Rename this class name* / *Move this file to a named package* | Manutenibilidade — Low |

**Total: 20 problemas** — 3 bugs (2 de S2095, 1 de S4973), 2 vulnerabilidades (S6437, S2077) e 15 code smells.

---

## Etapa 3.2 — Respostas

**1. Por que os testes passam com `livro == titulo`?**

Porque o Java mantém um *pool* de literais de String internados: `"Clean Code"` escrito no construtor e `"Clean Code"` escrito no teste são **o mesmo objeto** em memória, então `==` dá `true` por acidente. O bug só aparece quando o título chega de fora — de um formulário, de um `ResultSet`, de um JSON, de uma concatenação em tempo de execução. Aí são dois objetos diferentes com o mesmo conteúdo, `==` dá `false` e o empréstimo falha em produção com o livro visivelmente no acervo.

É o exemplo mais didático da aula: **a suíte está verde e o código está errado**. Nenhum teste dessa suíte pegaria isso; a análise estática pega na primeira execução. O teste que expõe o bug está neste gabarito (`emprestarTituloMontadoEmTempoDeExecucao`) e usa `new StringBuilder("Clean").append(" Code").toString()` justamente para escapar do pool de literais.

**2. `S101` e `S1220` são defeitos de verdade?**

Não neste repositório — são **falsos positivos por convenção**. O nome `GQS23B01_Biblioteca` e o pacote default existem porque o repositório da disciplina usa o padrão `GQSxxxx-NomeDoArquivo`, para o aluno localizar o arquivo pela aula. A ferramenta não sabe disso.

Num projeto real há três saídas legítimas, nesta ordem de preferência:

1. **Aceitar o achado e corrigir** — é o certo em produção: pacote nomeado e classe em PascalCase. Foi o que este gabarito fez.
2. **Ajustar a regra no Quality Profile** — a `S101` aceita uma expressão regular configurável; se a convenção da empresa é outra, muda-se o parâmetro da regra, não se desliga a regra.
3. **Marcar o problema individualmente** como *Accept* / *False Positive* no dashboard, **com justificativa escrita**. Fica rastreável e revisável.

O que **não** se faz: espalhar `// NOSONAR` pelo código para calar a ferramenta. Isso transforma o silêncio em hábito e a ferramenta em enfeite.

---

## Etapa 4 — Por que o gate padrão passou (gabarito)

Porque o "Sonar way" só tem condições sobre **New Code**, e a chamada da API devolve `"conditions":[]`: não havia código novo a avaliar na primeira análise de um projeto importado inteiro.

Essa é a metodologia **Clean as You Code**: em vez de exigir que uma equipe pare tudo para limpar 200 mil linhas herdadas — o que na prática nunca acontece —, exige-se que **tudo que for escrito ou alterado a partir de hoje** saia limpo. Como o código que se mexe com frequência é uma fração pequena da base, em 6 a 12 meses a parte viva do sistema já está saudável, sem nenhuma refatoração de big bang.

O gate "GQS — Código Legado" da Etapa 4 é o complemento: ele existe para o exercício de **auditar** uma base existente (o que a Aula 11 chama de auditoria e a Aula 23 mede como Quality Gate sobre a cobertura total). Na vida real, use os dois: as condições de New Code como trava do dia a dia, e uma medição periódica do Overall Code como termômetro da dívida acumulada.

---

## Etapa 5 — Código corrigido

### `src/main/java/com/gqs/biblioteca/Biblioteca.java`

```java
package com.gqs.biblioteca;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GQS23B02 - Versao CORRIGIDA do sistema de emprestimo (Aula 23B, Etapa 5).
 *
 * Cada correcao abaixo elimina um achado real do SonarQube na versao com
 * defeitos (`repository/class23B/java`):
 *   S1220  arquivo movido para o pacote com.gqs.biblioteca
 *   S101   classe renomeada para Biblioteca (PascalCase)
 *   S6437  senha lida de variavel de ambiente, nao mais fixa no codigo
 *   S2077  PreparedStatement com parametro ligado, no lugar de concatenacao
 *   S2095  try-with-resources fecha Connection/PreparedStatement/ResultSet
 *   S108   catch registra a falha no logger em vez de ficar vazio
 *   S4973  comparacao de String por equals()
 *   S1192  literal "Livro nao encontrado" virou constante
 *   S3776  calcularMulta dividida em metodos pequenos (complexidade 35 -> 3)
 *   S1481  variavel `hoje`, que nunca era usada, removida
 *   S1854  atribuicao morta de `multa` removida
 *   (dup)  relatorios compartilham montarRelatorio(separador, formatoItem)
 *   S106   ponto de entrada movido para BibliotecaDemo, com logger
 */
public class Biblioteca {

    private static final Logger LOGGER = Logger.getLogger(Biblioteca.class.getName());

    private static final String LIVRO_NAO_ENCONTRADO = "Livro nao encontrado";
    private static final String URL_BD = "jdbc:postgresql://localhost:5432/biblioteca";
    private static final double MULTA_DIARIA = 1.50;
    private static final double TETO_MULTA = 100.0;

    private final List<String> acervo = new ArrayList<>();
    private final List<String> emprestados = new ArrayList<>();

    public Biblioteca() {
        acervo.add("Engenharia de Software Moderna");
        acervo.add("Clean Code");
        acervo.add("Test Driven Development");
    }

    /** A senha nunca fica no codigo-fonte: vem do ambiente (S6437). */
    private static String senhaDoBanco() {
        String senha = System.getenv("BIBLIOTECA_DB_PASSWORD");
        return senha == null ? "" : senha;
    }

    /**
     * Busca um livro pelo titulo. Corrige S2077 (PreparedStatement),
     * S2095 (try-with-resources) e S108 (catch que registra a falha).
     */
    public String buscarPorTitulo(String titulo) {
        String sql = "SELECT titulo FROM livros WHERE titulo = ?";
        try (Connection conexao = DriverManager.getConnection(URL_BD, "admin", senhaDoBanco());
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, titulo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("titulo");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e, () -> "Falha ao consultar o livro: " + titulo);
        }
        return LIVRO_NAO_ENCONTRADO;
    }

    /** Corrige S4973: comparacao de String por equals(). */
    public String emprestar(String titulo, String matricula) {
        if (titulo == null || matricula == null) {
            throw new IllegalArgumentException("Titulo e matricula sao obrigatorios");
        }
        if (acervo.remove(titulo)) {
            emprestados.add(titulo);
            return "Emprestimo registrado: " + titulo;
        }
        return LIVRO_NAO_ENCONTRADO;
    }

    public String devolver(String titulo) {
        if (emprestados.remove(titulo)) {
            acervo.add(titulo);
            return "Devolucao registrada: " + titulo;
        }
        return LIVRO_NAO_ENCONTRADO;
    }

    /**
     * Corrige S3776: a decisao foi quebrada em tres metodos pequenos.
     * A complexidade cognitiva cai de 35 para 3.
     */
    public double calcularMulta(int diasAtraso, String tipoUsuario, boolean livroRaro) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        double multa = diasAtraso * MULTA_DIARIA
                * fatorPorPerfil(diasAtraso, tipoUsuario)
                * (livroRaro ? 2 : 1);
        return arredondar(Math.min(multa, TETO_MULTA));
    }

    private static double fatorPorPerfil(int diasAtraso, String tipoUsuario) {
        switch (tipoUsuario) {
            case "aluno":
                return fatorAluno(diasAtraso);
            case "professor":
                return diasAtraso > 30 ? 1.2 : 0.5;
            case "visitante":
                return diasAtraso > 7 ? 3 : 2;
            default:
                return 1;
        }
    }

    private static double fatorAluno(int diasAtraso) {
        if (diasAtraso > 30) {
            return 2;
        }
        return diasAtraso > 15 ? 1.5 : 1;
    }

    private static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /** Corrige a duplicacao: um unico montador, dois formatos. */
    public String relatorioTexto() {
        return montarRelatorio(": ", "%d. %s [%s]%n");
    }

    public String relatorioCsv() {
        return montarRelatorio("; ", "%d; %s; %s%n");
    }

    private String montarRelatorio(String separador, String formatoItem) {
        StringBuilder sb = new StringBuilder();
        sb.append("RELATORIO DO ACERVO - BIBLIOTECA CENTRAL").append(System.lineSeparator());
        sb.append("Disponiveis").append(separador).append(acervo.size()).append(System.lineSeparator());
        sb.append("Emprestados").append(separador).append(emprestados.size()).append(System.lineSeparator());
        sb.append("Total geral").append(separador)
          .append(acervo.size() + emprestados.size()).append(System.lineSeparator());
        int contador = 0;
        for (String livro : acervo) {
            contador++;
            sb.append(String.format(formatoItem, contador, livro, "DISPONIVEL"));
        }
        for (String livro : emprestados) {
            contador++;
            sb.append(String.format(formatoItem, contador, livro, "EMPRESTADO"));
        }
        sb.append("Itens listados").append(separador).append(contador).append(System.lineSeparator());
        return sb.toString();
    }

    public int totalDisponivel() {
        return acervo.size();
    }

    public int totalEmprestado() {
        return emprestados.size();
    }
}
```

### `src/main/java/com/gqs/biblioteca/BibliotecaDemo.java`

```java
package com.gqs.biblioteca;

import java.util.logging.Logger;

/**
 * Ponto de entrada de demonstracao da Aula 23B.
 *
 * Separar o `main` da classe de dominio resolve dois problemas de uma vez:
 *   - S106: a saida vai para o logger, nao para System.out;
 *   - cobertura: um ponto de entrada nao e testavel por teste de unidade, e
 *     inflar a cobertura chamando `main` num teste so enganaria a metrica.
 *     Por isso este arquivo entra em `sonar.coverage.exclusions` no pom.xml -
 *     a exclusao e declarada e justificada, nao escondida.
 */
public final class BibliotecaDemo {

    private static final Logger LOGGER = Logger.getLogger(BibliotecaDemo.class.getName());

    private BibliotecaDemo() {
    }

    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        LOGGER.info(() -> "Acervo inicial: " + biblioteca.totalDisponivel());
        LOGGER.info(() -> biblioteca.emprestar("Clean Code", "2026001"));
        LOGGER.info(() -> "Multa (10 dias, aluno): R$ " + biblioteca.calcularMulta(10, "aluno", false));
        LOGGER.info(biblioteca::relatorioTexto);
    }
}
```

### `src/test/java/com/gqs/biblioteca/BibliotecaTest.java`

```java
package com.gqs.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Suite completa da Etapa 5: leva a cobertura de 46% para acima de 80%. */
class BibliotecaTest {

    private Biblioteca biblioteca;

    @BeforeEach
    void criarBiblioteca() {
        biblioteca = new Biblioteca();
    }

    @Test
    @DisplayName("O acervo inicial tem 3 livros")
    void acervoInicialTemTresLivros() {
        assertEquals(3, biblioteca.totalDisponivel());
    }

    @Test
    @DisplayName("Emprestar remove do acervo e registra em emprestados")
    void emprestarRemoveDoAcervo() {
        assertEquals("Emprestimo registrado: Clean Code", biblioteca.emprestar("Clean Code", "2026001"));
        assertEquals(2, biblioteca.totalDisponivel());
        assertEquals(1, biblioteca.totalEmprestado());
    }

    @Test
    @DisplayName("Titulo montado em tempo de execucao tambem e encontrado (o bug do == so aparece aqui)")
    void emprestarTituloMontadoEmTempoDeExecucao() {
        // Nao e um literal: o objeto String nao esta no pool de internados,
        // entao `livro == titulo` daria false e o emprestimo falharia.
        String titulo = new StringBuilder("Clean").append(" Code").toString();
        assertEquals("Emprestimo registrado: Clean Code", biblioteca.emprestar(titulo, "2026001"));
        assertEquals(2, biblioteca.totalDisponivel());
    }

    @Test
    @DisplayName("Emprestar titulo inexistente devolve 'Livro nao encontrado'")
    void emprestarTituloInexistente() {
        assertEquals("Livro nao encontrado", biblioteca.emprestar("Livro Fantasma", "2026001"));
    }

    @Test
    @DisplayName("Emprestar sem matricula lanca IllegalArgumentException")
    void emprestarSemMatriculaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> biblioteca.emprestar("Clean Code", null));
    }

    @Test
    @DisplayName("Emprestar sem titulo lanca IllegalArgumentException")
    void emprestarSemTituloLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> biblioteca.emprestar(null, "2026001"));
    }

    @Test
    @DisplayName("Devolver recoloca no acervo")
    void devolverRecolocaNoAcervo() {
        biblioteca.emprestar("Clean Code", "2026001");
        assertEquals("Devolucao registrada: Clean Code", biblioteca.devolver("Clean Code"));
        assertEquals(3, biblioteca.totalDisponivel());
    }

    @Test
    @DisplayName("Devolver livro que nao estava emprestado devolve 'Livro nao encontrado'")
    void devolverLivroNaoEmprestado() {
        assertEquals("Livro nao encontrado", biblioteca.devolver("Clean Code"));
    }

    @ParameterizedTest(name = "{0} dias, {1}, raro={2} -> R$ {3}")
    @CsvSource({
        " 0, aluno,      false,   0.0",
        "-5, aluno,      false,   0.0",
        "10, aluno,      false,  15.0",
        "20, aluno,      false,  45.0",
        "40, aluno,      false, 100.0",
        "10, aluno,      true,   30.0",
        "10, professor,  false,   7.5",
        "40, professor,  false,  72.0",
        " 5, visitante,  false,  15.0",
        "10, visitante,  false,  45.0",
        "10, bibliotecario, false, 15.0"
    })
    @DisplayName("Multa por perfil, dias de atraso e raridade")
    void calcularMultaPorPerfil(int dias, String perfil, boolean raro, double esperado) {
        assertEquals(esperado, biblioteca.calcularMulta(dias, perfil, raro));
    }

    @Test
    @DisplayName("A multa respeita o teto de R$ 100,00")
    void multaRespeitaTeto() {
        assertEquals(100.0, biblioteca.calcularMulta(365, "visitante", true));
    }

    @Test
    @DisplayName("O relatorio em texto lista disponiveis e emprestados")
    void relatorioTextoListaTitulos() {
        biblioteca.emprestar("Clean Code", "2026001");
        String relatorio = biblioteca.relatorioTexto();
        assertTrue(relatorio.contains("RELATORIO DO ACERVO"));
        assertTrue(relatorio.contains("[DISPONIVEL]"));
        assertTrue(relatorio.contains("[EMPRESTADO]"));
        assertTrue(relatorio.contains("Itens listados: 3"));
    }

    @Test
    @DisplayName("O relatorio CSV usa ponto e virgula como separador")
    void relatorioCsvUsaPontoEVirgula() {
        String relatorio = biblioteca.relatorioCsv();
        assertTrue(relatorio.contains("Disponiveis; 3"));
        assertTrue(relatorio.contains("; DISPONIVEL"));
    }

    @Test
    @DisplayName("Busca sem banco disponivel devolve 'Livro nao encontrado' sem estourar excecao")
    void buscarSemBancoNaoQuebra() {
        assertEquals("Livro nao encontrado", biblioteca.buscarPorTitulo("Clean Code"));
    }
}
```

### Ajustes no `pom.xml`

```xml
<!-- Ponto de entrada não testável por unidade: exclusão declarada e justificada -->
<sonar.coverage.exclusions>**/BibliotecaDemo.java</sonar.coverage.exclusions>
```

E a dependência `junit-jupiter-params`, necessária para o `@ParameterizedTest`:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-params</artifactId>
    <version>${junit.jupiter.version}</version>
    <scope>test</scope>
</dependency>
```

### O que sobrou sem cobertura (e por quê)

Mesmo com 84%, `buscarPorTitulo` continua parcialmente descoberta: o caminho feliz exige um PostgreSQL de verdade. Duas leituras possíveis, ambas defensáveis:

- **Aceitar** — é código de integração, coberto por teste de integração (Aula 05), não por teste de unidade. O teste `buscarSemBancoNaoQuebra` já garante o comportamento de falha.
- **Refatorar para testar** — extrair um `ConnectionFactory` injetável e testar com um *fake* (Aula 15, Mocks e Stubs). Melhora o desenho e a cobertura de uma vez.

O que **não** vale é chamar `main()` dentro de um teste só para o número subir. Vale discutir isso em aula: a métrica existe para revelar risco, e um teste sem asserção, que apenas executa linhas, destrói exatamente essa informação.

---

## Etapa 6 — Comparação Java e Python (gabarito)

| Defeito plantado | Java | Python | Detectado nos dois? |
|---|---|---|---|
| Senha fixa no código | `S6437` (Blocker) | `S2068` (Medium) | **Sim** — severidades diferentes |
| SQL por concatenação/f-string | `S2077` | — | **Não** |
| Exceção engolida | `S108` | `S5754` | **Sim** — regras diferentes, mesmo problema |
| Complexidade cognitiva | `S3776` (35 para 15) | `S3776` (34 para 15) | **Sim** |
| Literal duplicado | `S1192` (3x) | `S1192` (2x) | **Sim** |
| Variável não usada | `S1481` + `S1854` | `S1481` | **Sim** — Java também acusa a atribuição morta |
| Argumento default mutável | — | `S5717` | **Não** — o problema não existe em Java |
| Bloco duplicado | 14,3% | 13,7% | **Sim** |

**Comentário esperado do aluno, sobre as duas descobertas:**

A ausência da injeção de SQL no Python não é falha do laboratório: a *taint analysis* — seguir o dado desde a entrada do usuário até o `execute` — só existe da **Developer Edition** em diante. O Community Build faz correspondência de padrão, que pega senha fixa e `except` vazio, mas não rastreia fluxo de dados. E a regra `S2077` que disparou em Java é de outra natureza: ela sinaliza a query montada dinamicamente **sem** provar que o dado é hostil — por isso a mensagem começa com *"Make sure ... is safe here"*, uma pergunta ao revisor, não uma acusação.

A segunda descoberta é mais desconfortável: bastou a constante chamar-se `DB_PASSWORD` em vez de `SENHA_BD` para a regra disparar. A detecção de segredos usa padrões de nome em inglês. Ou seja, **a ferramenta não é um oráculo** — ela tem cobertura desigual, e a revisão humana (Aula 11) continua necessária exatamente onde a ferramenta é cega.

---

## Erros mais comuns dos alunos

| Sintoma | Causa | Correção |
|---|---|---|
| Container sobe e morre em segundos | `vm.max_map_count` baixo | `sudo sysctl -w vm.max_map_count=524288` |
| `You must provide a token` | esqueceu `-Dsonar.token` ou a variável `SONAR_TOKEN` | exportar a variável |
| Senha do admin recusada no primeiro login | o SonarQube exige no mínimo 12 caracteres | escolher uma senha mais longa |
| Cobertura aparece como 0% no dashboard | rodou `mvn sonar:sonar` sem `mvn verify` antes, ou o caminho do `jacoco.xml` está errado | rodar `verify` primeiro; conferir `sonar.coverage.jacoco.xmlReportPaths` |
| Cobertura 0% no Python | esqueceu `--cov-report=xml`; o Sonar não lê o `.coverage` binário | `pytest --cov=. --cov-report=xml` |
| Quality Gate sempre verde | está usando o "Sonar way" sem código novo | criar o gate da Etapa 4 |
| Workflow do GitHub passa sempre | falta o passo `sonarqube-quality-gate-action` | acrescentar o passo |
| `waitForQualityGate` trava no Jenkins | webhook não configurado no SonarQube | criar o webhook apontando para `http://jenkins:8080/sonarqube-webhook/` |
