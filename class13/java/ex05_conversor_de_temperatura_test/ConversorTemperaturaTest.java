package tdd20ex.ex05_conversor_de_temperatura;

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
