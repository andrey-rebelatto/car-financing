package com.carfinancing.financiamento;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CalculadoraFinanciamento {

    /** Taxa fixa aplicada sobre o valor financiado (32%, conforme o protótipo). */
    public static final BigDecimal TAXA = new BigDecimal("0.32");

    private CalculadoraFinanciamento() {
    }

    public static ResultadoFinanciamento calcular(BigDecimal valorVeiculo, BigDecimal entrada, int numeroParcelas) {
        BigDecimal valorFinanciado = valorVeiculo.subtract(entrada).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal valorTotal = valorFinanciado.multiply(BigDecimal.ONE.add(TAXA));
        BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(numeroParcelas), 2, RoundingMode.HALF_EVEN);
        BigDecimal totalAPagar = valorParcela.multiply(BigDecimal.valueOf(numeroParcelas));
        return new ResultadoFinanciamento(valorFinanciado, valorParcela, totalAPagar);
    }
}
