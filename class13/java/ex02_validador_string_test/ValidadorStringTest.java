package tdd20ex.ex02_validador_string;

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
