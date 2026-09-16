package com.carfinancing.financiamento;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraFinanciamentoTest {

    @Test
    void calculaValoresDoPrototipo() {
        ResultadoFinanciamento resultado = CalculadoraFinanciamento.calcular(
                new BigDecimal("50000"), new BigDecimal("5000"), 36);

        assertEquals(new BigDecimal("45000.00"), resultado.valorFinanciado());
        assertEquals(new BigDecimal("1650.00"), resultado.valorParcela());
        assertEquals(new BigDecimal("59400.00"), resultado.totalAPagar());
    }

    @Test
    void semEntradaFinanciaValorIntegral() {
        ResultadoFinanciamento resultado = CalculadoraFinanciamento.calcular(
                new BigDecimal("12000"), BigDecimal.ZERO, 12);

        assertEquals(new BigDecimal("12000.00"), resultado.valorFinanciado());
        assertEquals(new BigDecimal("1320.00"), resultado.valorParcela());
        assertEquals(new BigDecimal("15840.00"), resultado.totalAPagar());
    }

    @Test
    void totalAPagarEhParcelaVezesNumeroDeParcelas() {
        ResultadoFinanciamento resultado = CalculadoraFinanciamento.calcular(
                new BigDecimal("10000"), BigDecimal.ZERO, 48);

        // 13200 / 48 = 275.00
        assertEquals(new BigDecimal("275.00"), resultado.valorParcela());
        assertEquals(new BigDecimal("13200.00"), resultado.totalAPagar());
    }
}
