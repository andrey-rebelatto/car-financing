package com.carfinancing.financiamento;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Converte textos digitados pelo usuário (formato brasileiro, ex.: "45.000,00") em números.
 */
public final class ConversorNumero {

    private static final Pattern MILHAR_SEM_DECIMAL = Pattern.compile("-?\\d{1,3}(\\.\\d{3})+");

    private ConversorNumero() {
    }

    public static BigDecimal paraValor(String texto) {
        String limpo = texto == null ? "" : texto.replace("R$", "").strip();
        if (limpo.isEmpty()) {
            throw new NumberFormatException("Valor vazio");
        }
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        } else if (MILHAR_SEM_DECIMAL.matcher(limpo).matches()) {
            limpo = limpo.replace(".", "");
        }
        return new BigDecimal(limpo);
    }

    public static int paraInteiro(String texto) {
        try {
            return paraValor(texto).intValueExact();
        } catch (ArithmeticException e) {
            throw new NumberFormatException("Número inteiro inválido: " + texto);
        }
    }
}
