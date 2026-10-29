package br.edu.gqs.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Testes unitarios da Biblioteca - VERSAO COMPLETA (gabarito da Etapa 7).
 *
 * <p>Copie para java/src/test/java/br/edu/gqs/biblioteca/BibliotecaTest.java.
 * A tabela de multas cobre todos os ramos de calcularMulta: e' ela que
 * garante que a refatoracao (S3776) nao mudou o comportamento.
 */
class BibliotecaTest {

    private final Biblioteca biblioteca = new Biblioteca("token-de-teste");

    /** Na API o titulo chega no JSON como um objeto String NOVO. */
    private static String tituloVindoDaRequisicao(String... palavras) {
        return String.join(" ", palavras);
    }

    @Test
    @DisplayName("Emprestar um livro disponivel gera codigo EMP-xxxxxx")
    void emprestaLivroDisponivel() {
        String codigo = biblioteca.emprestar(tituloVindoDaRequisicao("Clean", "Code"), "2026001");
        assertTrue(codigo.matches("EMP-\\d{6}"), codigo);
        assertTrue(biblioteca.listarEmprestados().contains("Clean Code"));
        assertFalse(biblioteca.listarDisponiveis().contains("Clean Code"));
    }

    @Test
    @DisplayName("Emprestar livro inexistente lanca LivroNaoEncontradoException")
    void recusaLivroInexistente() {
        assertThrows(LivroNaoEncontradoException.class,
                () -> biblioteca.emprestar("Livro Que Nao Existe", "2026001"));
    }

    @Test
    @DisplayName("Emprestar sem matricula lanca IllegalArgumentException")
    void recusaEmprestimoSemMatricula() {
        assertThrows(IllegalArgumentException.class, () -> biblioteca.emprestar("Clean Code", " "));
        assertThrows(IllegalArgumentException.class, () -> biblioteca.emprestar(null, "2026001"));
    }

    @Test
    @DisplayName("Devolver um livro emprestado o coloca de volta no acervo")
    void devolveLivroEmprestado() {
        biblioteca.emprestar("Refactoring", "2026001");
        biblioteca.devolver(tituloVindoDaRequisicao("Refactoring"));
        assertTrue(biblioteca.listarDisponiveis().contains("Refactoring"));
        assertTrue(biblioteca.listarEmprestados().isEmpty());
    }

    @Test
    @DisplayName("Devolver livro que nao foi emprestado lanca LivroNaoEncontradoException")
    void recusaDevolucaoDeLivroNaoEmprestado() {
        assertThrows(LivroNaoEncontradoException.class, () -> biblioteca.devolver("Clean Code"));
    }

    @ParameterizedTest(name = "{0} dias, {1}, raro={2} -> {3}")
    @CsvSource({
        "0,  aluno,     false, 0.0",
        "10, aluno,     false, 15.0",
        "20, aluno,     false, 45.0",
        "20, aluno,     true,  90.0",
        "31, aluno,     false, 93.0",
        "31, aluno,     true,  100.0",
        "10, professor, false, 7.5",
        "31, professor, false, 55.8",
        "31, professor, true,  100.0",
        "5,  visitante, false, 15.0",
        "10, visitante, false, 45.0",
        "10, outro,     false, 15.0",
        "90, outro,     false, 100.0"
    })
    @DisplayName("Tabela de multas por tipo de usuario, faixa de atraso e livro raro")
    void calculaMulta(int dias, String tipo, boolean raro, double esperado) {
        assertEquals(esperado, biblioteca.calcularMulta(dias, tipo, raro), 0.001);
    }

    @Test
    @DisplayName("lerDias converte numero e rejeita texto")
    void lerDias() {
        assertEquals(12, biblioteca.lerDias("12"));
        assertThrows(IllegalArgumentException.class, () -> biblioteca.lerDias("doze"));
    }

    @Test
    @DisplayName("So' o token configurado libera o relatorio")
    void validaToken() {
        assertTrue(biblioteca.tokenValido("token-de-teste"));
        assertFalse(biblioteca.tokenValido("outro"));
        assertFalse(new Biblioteca("").tokenValido(""));
    }

    @Test
    @DisplayName("Relatorios em texto e CSV trazem os mesmos indicadores")
    void relatorios() {
        biblioteca.emprestar("Clean Code", "2026001");
        String texto = biblioteca.relatorioTexto();
        String csv = biblioteca.relatorioCsv();
        assertTrue(texto.startsWith("RELATORIO DA BIBLIOTECA"));
        assertTrue(texto.contains("Emprestados: 1"));
        assertTrue(texto.contains("Taxa de ocupacao: 25.0%") || texto.contains("Taxa de ocupacao: 25,0%"));
        assertTrue(texto.contains("emprestado - Clean Code"));
        assertTrue(csv.startsWith("indicador;valor"));
        assertTrue(csv.contains("Emprestados;1"));
        assertTrue(csv.contains("emprestado;Clean Code"));
    }
}
