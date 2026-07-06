package tdd20ex.ex04_processador_de_lista;

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
