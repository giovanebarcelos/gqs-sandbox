package tdd20ex.ex03_contador_com_estado;

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
