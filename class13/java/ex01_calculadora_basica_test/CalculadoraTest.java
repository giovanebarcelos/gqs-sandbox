package tdd20ex.ex01_calculadora_basica;

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