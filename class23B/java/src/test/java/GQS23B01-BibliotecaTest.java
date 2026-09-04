import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * GQS23B01 - Suíte de testes PROPOSITALMENTE INCOMPLETA da Aula 23B.
 *
 * Todos os testes daqui PASSAM. A cobertura, porém, fica abaixo dos 80%
 * exigidos pelo Quality Gate do curso, porque:
 *   - `buscarPorTitulo` não é testada (precisa de banco);
 *   - `relatorioCsv` não é testada;
 *   - `calcularMulta` é testada em 1 dos 6 caminhos possíveis.
 *
 * É esse contraste que o laboratório explora: uma suíte verde não é
 * sinônimo de código são. Rode `mvn verify` e depois `mvn sonar:sonar`.
 */
class GQS23B01_BibliotecaTest {

    private GQS23B01_Biblioteca biblioteca;

    @BeforeEach
    void criarBiblioteca() {
        biblioteca = new GQS23B01_Biblioteca();
    }

    @Test
    @DisplayName("O acervo inicial tem 3 livros")
    void acervoInicialTemTresLivros() {
        assertEquals(3, biblioteca.totalDisponivel());
    }

    @Test
    @DisplayName("Emprestar um livro do acervo o remove da lista de disponíveis")
    void emprestarRemoveDoAcervo() {
        String resultado = biblioteca.emprestar("Clean Code", "2026001");
        assertEquals("Empréstimo registrado: Clean Code", resultado);
        assertEquals(2, biblioteca.totalDisponivel());
    }

    @Test
    @DisplayName("Emprestar um título inexistente devolve 'Livro não encontrado'")
    void emprestarTituloInexistente() {
        assertEquals("Livro não encontrado", biblioteca.emprestar("Livro Fantasma", "2026001"));
    }

    @Test
    @DisplayName("Emprestar sem matrícula lança IllegalArgumentException")
    void emprestarSemMatriculaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> biblioteca.emprestar("Clean Code", null));
    }

    @Test
    @DisplayName("Devolver um livro emprestado o recoloca no acervo")
    void devolverRecolocaNoAcervo() {
        biblioteca.emprestar("Clean Code", "2026001");
        assertEquals("Devolução registrada: Clean Code", biblioteca.devolver("Clean Code"));
        assertEquals(3, biblioteca.totalDisponivel());
    }

    @Test
    @DisplayName("Devolver um livro que não estava emprestado devolve 'Livro não encontrado'")
    void devolverLivroNaoEmprestado() {
        assertEquals("Livro não encontrado", biblioteca.devolver("Clean Code"));
    }

    @Test
    @DisplayName("Multa de aluno com 10 dias de atraso é R$ 15,00")
    void multaAlunoDezDias() {
        assertEquals(15.0, biblioteca.calcularMulta(10, "aluno", false));
    }

    @Test
    @DisplayName("Sem atraso não há multa")
    void semAtrasoNaoHaMulta() {
        assertEquals(0.0, biblioteca.calcularMulta(0, "aluno", false));
    }

    @Test
    @DisplayName("O relatório em texto lista os títulos disponíveis")
    void relatorioTextoListaTitulos() {
        String relatorio = biblioteca.relatorioTexto();
        assertTrue(relatorio.contains("RELATORIO DO ACERVO"));
        assertTrue(relatorio.contains("Clean Code"));
    }
}
