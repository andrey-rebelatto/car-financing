package com.carfinancing.financiamento;

/**
 * Valores digitados no formulário, ainda sem validação.
 */
public record FormularioSimulacao(
        String modelo,
        String valor,
        boolean usado,
        String quilometragem,
        String proprietarios,
        boolean possuiEntrada,
        String entrada) {
}
