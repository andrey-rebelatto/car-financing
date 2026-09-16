package com.carfinancing.financiamento;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorSimulacaoTest {

    private static FormularioSimulacao novo(String modelo, String valor) {
        return new FormularioSimulacao(modelo, valor, false, "", "", false, "");
    }

    @Test
    void formularioValidoDeCarroNovoNaoTemErros() {
        assertTrue(ValidadorSimulacao.validar(novo("Argo", "80.000,00")).isEmpty());
    }

    @Test
    void exigeModeloEValor() {
        List<String> erros = ValidadorSimulacao.validar(novo(" ", ""));

        assertEquals(List.of("Informe o modelo do veículo.", "Informe o valor do veículo."), erros);
    }

    @Test
    void valorDeveSerNumericoEPositivo() {
        assertEquals(List.of("Valor do veículo inválido."),
                ValidadorSimulacao.validar(novo("Argo", "abc")));
        assertEquals(List.of("O valor do veículo deve ser maior que zero."),
                ValidadorSimulacao.validar(novo("Argo", "0")));
    }

    @Test
    void camposDeUsadoSoSaoValidadosQuandoTipoUsado() {
        FormularioSimulacao usado = new FormularioSimulacao("Argo", "50000", true, "", "0", false, "");

        assertEquals(List.of(
                "Informe a quilometragem.",
                "A quantidade de proprietários deve ser maior que zero."
        ), ValidadorSimulacao.validar(usado));
    }

    @Test
    void quilometragemNaoPodeSerNegativa() {
        FormularioSimulacao usado = new FormularioSimulacao("Argo", "50000", true, "-1", "1", false, "");

        assertEquals(List.of("A quilometragem não pode ser negativa."), ValidadorSimulacao.validar(usado));
    }

    @Test
    void entradaSoEhValidadaQuandoMarcada() {
        FormularioSimulacao semEntrada = new FormularioSimulacao("Argo", "50000", false, "", "", false, "xyz");

        assertTrue(ValidadorSimulacao.validar(semEntrada).isEmpty());
    }

    @Test
    void entradaDeveSerMenorQueValorDoVeiculo() {
        FormularioSimulacao form = new FormularioSimulacao("Argo", "50000", false, "", "", true, "50.000");

        assertEquals(List.of("A entrada deve ser menor que o valor do veículo."), ValidadorSimulacao.validar(form));
    }

    @Test
    void entradaMarcadaDeveSerInformadaEPositiva() {
        assertEquals(List.of("Informe o valor da entrada."), ValidadorSimulacao.validar(
                new FormularioSimulacao("Argo", "50000", false, "", "", true, "")));
        assertEquals(List.of("A entrada deve ser maior que zero."), ValidadorSimulacao.validar(
                new FormularioSimulacao("Argo", "50000", false, "", "", true, "0")));
    }
}
