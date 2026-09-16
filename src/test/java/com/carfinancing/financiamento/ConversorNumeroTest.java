package com.carfinancing.financiamento;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConversorNumeroTest {

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "45000;45000",
            "45000,50;45000.50",
            "45.000,00;45000.00",
            "1.250.000;1250000",
            "R$ 45.000,00;45000.00",
            "45000.5;45000.5",
            "'  300 ';300"
    })
    void converteValoresNoFormatoBrasileiro(String texto, String esperado) {
        assertEquals(new BigDecimal(esperado), ConversorNumero.paraValor(texto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "12a", "1,2,3"})
    void rejeitaValoresInvalidos(String texto) {
        assertThrows(NumberFormatException.class, () -> ConversorNumero.paraValor(texto));
    }

    @Test
    void converteInteiroComSeparadorDeMilhar() {
        assertEquals(15000, ConversorNumero.paraInteiro("15.000"));
    }

    @Test
    void rejeitaInteiroComCasasDecimais() {
        assertThrows(NumberFormatException.class, () -> ConversorNumero.paraInteiro("1,5"));
    }
}
