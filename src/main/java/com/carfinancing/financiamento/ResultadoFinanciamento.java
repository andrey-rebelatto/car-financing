package com.carfinancing.financiamento;

import java.math.BigDecimal;

public record ResultadoFinanciamento(BigDecimal valorFinanciado, BigDecimal valorParcela, BigDecimal totalAPagar) {
}
