package com.carfinancing.financiamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class ValidadorSimulacao {

    private ValidadorSimulacao() {
    }

    /**
     * @return mensagens de erro; lista vazia quando o formulário é válido
     */
    public static List<String> validar(FormularioSimulacao form) {
        List<String> erros = new ArrayList<>();

        if (form.modelo() == null || form.modelo().isBlank()) {
            erros.add("Informe o modelo do veículo.");
        }

        BigDecimal valor = null;
        if (vazio(form.valor())) {
            erros.add("Informe o valor do veículo.");
        } else {
            try {
                valor = ConversorNumero.paraValor(form.valor());
                if (valor.signum() <= 0) {
                    erros.add("O valor do veículo deve ser maior que zero.");
                    valor = null;
                }
            } catch (NumberFormatException e) {
                erros.add("Valor do veículo inválido.");
            }
        }

        if (form.usado()) {
            validarQuilometragem(form.quilometragem(), erros);
            validarProprietarios(form.proprietarios(), erros);
        }

        if (form.possuiEntrada()) {
            validarEntrada(form.entrada(), valor, erros);
        }

        return erros;
    }

    private static void validarQuilometragem(String texto, List<String> erros) {
        if (vazio(texto)) {
            erros.add("Informe a quilometragem.");
            return;
        }
        try {
            if (ConversorNumero.paraInteiro(texto) < 0) {
                erros.add("A quilometragem não pode ser negativa.");
            }
        } catch (NumberFormatException e) {
            erros.add("Quilometragem inválida. Informe um número inteiro.");
        }
    }

    private static void validarProprietarios(String texto, List<String> erros) {
        if (vazio(texto)) {
            erros.add("Informe a quantidade de proprietários.");
            return;
        }
        try {
            if (ConversorNumero.paraInteiro(texto) < 1) {
                erros.add("A quantidade de proprietários deve ser maior que zero.");
            }
        } catch (NumberFormatException e) {
            erros.add("Quantidade de proprietários inválida. Informe um número inteiro.");
        }
    }

    private static void validarEntrada(String texto, BigDecimal valorVeiculo, List<String> erros) {
        if (vazio(texto)) {
            erros.add("Informe o valor da entrada.");
            return;
        }
        try {
            BigDecimal entrada = ConversorNumero.paraValor(texto);
            if (entrada.signum() <= 0) {
                erros.add("A entrada deve ser maior que zero.");
            } else if (valorVeiculo != null && entrada.compareTo(valorVeiculo) >= 0) {
                erros.add("A entrada deve ser menor que o valor do veículo.");
            }
        } catch (NumberFormatException e) {
            erros.add("Valor da entrada inválido.");
        }
    }

    private static boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }
}
