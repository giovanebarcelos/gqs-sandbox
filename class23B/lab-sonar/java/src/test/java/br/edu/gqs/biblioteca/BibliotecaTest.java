package br.edu.gqs.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitarios da Biblioteca (JUnit 5) - Aula 23B.
 *
 * <p>Esta suite e' propositalmente INCOMPLETA: cobre emprestimo, devolucao e
 * um caminho da multa, e deixa o resto sem teste. O SonarQube mostra isso
 * como cobertura baixa - e o Quality Gate do curso exige 80%.
 *
 * <pre>
 *   mvn clean verify     # testes + target/site/jacoco/jacoco.xml
 * </pre>
 *
 * Na versao 1.0.0, o teste de emprestimo FALHA por causa do defeito S4973.
 */
class BibliotecaTest {

    private final Biblioteca biblioteca = new Biblioteca();

    /**
     * Na API o titulo chega dentro do JSON e vira um objeto String NOVO.
     * String.join simula isso: o conteudo e' igual, o objeto e' outro.
     */
    private static String tituloVindoDaRequisicao(String... palavras) {
        return String.join(" ", palavras);
    }

    @Test
    @DisplayName("Emprestar um livro disponivel gera codigo EMP-xxxxxx")
    void emprestaLivroDisponivel() {
        String codigo = biblioteca.emprestar(tituloVindoDaRequisicao("Clean", "Code"), "2026001");
        assertTrue(codigo.matches("EMP-\\d{6}"), codigo);
        assertTrue(biblioteca.listarEmprestados().contains("Clean Code"));
    }

    @Test
    @DisplayName("Emprestar livro inexistente lanca LivroNaoEncontradoException")
    void recusaLivroInexistente() {
        assertThrows(LivroNaoEncontradoException.class,
                () -> biblioteca.emprestar("Livro Que Nao Existe", "2026001"));
    }

    @Test
    @DisplayName("Devolver livro que nao foi emprestado lanca LivroNaoEncontradoException")
    void recusaDevolucaoDeLivroNaoEmprestado() {
        assertThrows(LivroNaoEncontradoException.class, () -> biblioteca.devolver("Clean Code"));
    }

    @Test
    @DisplayName("Aluno com 10 dias de atraso paga 10 x 1,50 = 15,00")
    void multaAlunoAtrasoCurto() {
        assertEquals(15.0, biblioteca.calcularMulta(10, "aluno", false), 0.001);
    }
}
