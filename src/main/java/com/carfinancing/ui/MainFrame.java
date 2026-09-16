package com.carfinancing.ui;

import com.carfinancing.financiamento.CalculadoraFinanciamento;
import com.carfinancing.financiamento.ConversorNumero;
import com.carfinancing.financiamento.FormularioSimulacao;
import com.carfinancing.financiamento.ResultadoFinanciamento;
import com.carfinancing.financiamento.ValidadorSimulacao;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Tela de simulação de financiamento de veículos.
 *
 * <p>Layouts utilizados:
 * <ul>
 *   <li>BorderLayout: janela (título, formulário e rodapé)</li>
 *   <li>BoxLayout: empilhamento vertical das seções</li>
 *   <li>GridBagLayout: rótulos e campos dos painéis de dados</li>
 *   <li>FlowLayout: opções de tipo (novo/usado) e checkbox de entrada</li>
 *   <li>GridLayout: botões e linhas do resultado</li>
 * </ul>
 */
public class MainFrame extends JFrame {

    private static final String[] MARCAS = {
            "CHEVROLET", "FIAT", "FORD", "HONDA", "HYUNDAI",
            "JEEP", "NISSAN", "RENAULT", "TOYOTA", "VOLKSWAGEN"
    };
    private static final Integer[] ANOS = IntStream.iterate(2026, ano -> ano >= 2000, ano -> ano - 1)
            .boxed()
            .toArray(Integer[]::new);
    private static final Integer[] PARCELAS = {12, 24, 36, 48, 60};

    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
    private static final int COLUNAS_CAMPO = 22;

    private final JComboBox<String> marcaCombo = new JComboBox<>(MARCAS);
    private final JTextField modeloField = new JTextField(COLUNAS_CAMPO);
    private final JComboBox<Integer> anoCombo = new JComboBox<>(ANOS);
    private final JTextField valorField = new JTextField(COLUNAS_CAMPO);

    private final JRadioButton novoRadio = new JRadioButton("Novo");
    private final JRadioButton usadoRadio = new JRadioButton("Usado");

    private final JPanel usadoPanel = new JPanel(new GridBagLayout());
    private final JTextField quilometragemField = new JTextField(COLUNAS_CAMPO);
    private final JTextField proprietariosField = new JTextField(COLUNAS_CAMPO);

    private final JCheckBox possuiEntradaCheck = new JCheckBox("Possui entrada?");
    private final JLabel entradaLabel = new JLabel("Entrada");
    private final JTextField entradaField = new JTextField(COLUNAS_CAMPO);
    private final JComboBox<Integer> parcelasCombo = new JComboBox<>(PARCELAS);

    private final JPanel resultadoPanel = new JPanel(new GridLayout(3, 2, 12, 6));
    private final JLabel valorFinanciadoLabel = new JLabel();
    private final JLabel valorParcelaLabel = new JLabel();
    private final JLabel totalAPagarLabel = new JLabel();

    public MainFrame() {
        super("Financiamento de Carros");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Financiamento de Carros", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        titulo.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        adicionarSecao(formulario, criarPainelVeiculo());
        adicionarSecao(formulario, criarPainelTipo());
        adicionarSecao(formulario, criarPainelUsado());
        adicionarSecao(formulario, criarPainelFinanciamento());
        adicionarSecao(formulario, criarPainelBotoes());
        adicionarSecao(formulario, criarPainelResultado());
        formulario.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(formulario);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Dimensiona a janela com todas as seções visíveis (e o resultado preenchido)
        // para que ela não "pule" de tamanho ao exibir/ocultar os painéis condicionais.
        exibirResultado(CalculadoraFinanciamento.calcular(new BigDecimal("999999"), BigDecimal.ZERO, 12));
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);

        limpar();
    }

    // ---------------------------------------------------------------- painéis

    private JPanel criarPainelVeiculo() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(tituloSecao("Dados do Veículo"));
        adicionarLinha(painel, 0, new JLabel("Marca"), marcaCombo);
        adicionarLinha(painel, 1, new JLabel("Modelo"), modeloField);
        adicionarLinha(painel, 2, new JLabel("Ano"), anoCombo);
        adicionarLinha(painel, 3, new JLabel("Valor (R$)"), valorField);
        return painel;
    }

    private JPanel criarPainelTipo() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(novoRadio);
        grupo.add(usadoRadio);

        novoRadio.addActionListener(e -> atualizarVisibilidadeUsado());
        usadoRadio.addActionListener(e -> atualizarVisibilidadeUsado());

        painel.add(new JLabel("Tipo"));
        painel.add(novoRadio);
        painel.add(usadoRadio);
        return painel;
    }

    private JPanel criarPainelUsado() {
        Border tracejada = BorderFactory.createDashedBorder(Color.GRAY, 4, 3);
        usadoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(tracejada, "Dados do Veículo Usado"),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        adicionarLinha(usadoPanel, 0, new JLabel("Quilometragem"), quilometragemField);
        adicionarLinha(usadoPanel, 1, new JLabel("Proprietários"), proprietariosField);
        return usadoPanel;
    }

    private JPanel criarPainelFinanciamento() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(tituloSecao("Financiamento"));

        JPanel entradaCheckPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        entradaCheckPanel.add(possuiEntradaCheck);
        possuiEntradaCheck.addActionListener(e -> atualizarVisibilidadeEntrada());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(4, 4, 4, 4);
        painel.add(entradaCheckPanel, c);

        adicionarLinha(painel, 1, entradaLabel, entradaField);
        adicionarLinha(painel, 2, new JLabel("Parcelas"), parcelasCombo);
        return painel;
    }

    private JPanel criarPainelBotoes() {
        JButton calcularButton = new JButton("Calcular");
        JButton limparButton = new JButton("Limpar");
        calcularButton.addActionListener(e -> calcular());
        limparButton.addActionListener(e -> limpar());
        getRootPane().setDefaultButton(calcularButton);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 16, 0));
        botoes.add(calcularButton);
        botoes.add(limparButton);

        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 12));
        painel.add(botoes);
        return painel;
    }

    private JPanel criarPainelResultado() {
        resultadoPanel.setBorder(BorderFactory.createCompoundBorder(
                tituloSecao("Resultado"),
                BorderFactory.createEmptyBorder(4, 8, 8, 8)));
        adicionarResultado("Valor financiado:", valorFinanciadoLabel);
        adicionarResultado("Valor da parcela:", valorParcelaLabel);
        adicionarResultado("Total a pagar:", totalAPagarLabel);
        return resultadoPanel;
    }

    // ----------------------------------------------------------------- ações

    private void calcular() {
        FormularioSimulacao form = new FormularioSimulacao(
                modeloField.getText(),
                valorField.getText(),
                usadoRadio.isSelected(),
                quilometragemField.getText(),
                proprietariosField.getText(),
                possuiEntradaCheck.isSelected(),
                entradaField.getText());

        List<String> erros = ValidadorSimulacao.validar(form);
        if (!erros.isEmpty()) {
            mostrarResultado(false);
            JOptionPane.showMessageDialog(this,
                    "Corrija os campos abaixo:\n\n• " + String.join("\n• ", erros),
                    "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal valor = ConversorNumero.paraValor(form.valor());
        BigDecimal entrada = form.possuiEntrada() ? ConversorNumero.paraValor(form.entrada()) : BigDecimal.ZERO;
        int parcelas = (Integer) parcelasCombo.getSelectedItem();

        exibirResultado(CalculadoraFinanciamento.calcular(valor, entrada, parcelas));
        mostrarResultado(true);
    }

    private void exibirResultado(ResultadoFinanciamento resultado) {
        valorFinanciadoLabel.setText(MOEDA.format(resultado.valorFinanciado()));
        valorParcelaLabel.setText(MOEDA.format(resultado.valorParcela()));
        totalAPagarLabel.setText(MOEDA.format(resultado.totalAPagar()));
    }

    private void limpar() {
        marcaCombo.setSelectedIndex(0);
        modeloField.setText("");
        anoCombo.setSelectedIndex(0);
        valorField.setText("");

        novoRadio.setSelected(true);
        quilometragemField.setText("");
        proprietariosField.setText("");

        possuiEntradaCheck.setSelected(false);
        entradaField.setText("");
        parcelasCombo.setSelectedIndex(0);

        atualizarVisibilidadeUsado();
        atualizarVisibilidadeEntrada();
        mostrarResultado(false);
        modeloField.requestFocusInWindow();
    }

    private void atualizarVisibilidadeUsado() {
        usadoPanel.setVisible(usadoRadio.isSelected());
        atualizarTela();
    }

    private void atualizarVisibilidadeEntrada() {
        boolean possuiEntrada = possuiEntradaCheck.isSelected();
        entradaLabel.setVisible(possuiEntrada);
        entradaField.setVisible(possuiEntrada);
        atualizarTela();
    }

    private void mostrarResultado(boolean visivel) {
        resultadoPanel.setVisible(visivel);
        atualizarTela();
    }

    private void atualizarTela() {
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    // ------------------------------------------------------------- auxiliares

    private static Border tituloSecao(String titulo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY), titulo);
    }

    private static void adicionarLinha(JPanel painel, int linha, JLabel rotulo, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = linha;
        c.insets = new Insets(4, 4, 4, 4);

        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.ipadx = 20;
        rotulo.setLabelFor(campo);
        painel.add(rotulo, c);

        c.gridx = 1;
        c.ipadx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campo, c);
    }

    private void adicionarResultado(String rotulo, JLabel valor) {
        valor.setFont(valor.getFont().deriveFont(Font.BOLD));
        resultadoPanel.add(new JLabel(rotulo));
        resultadoPanel.add(valor);
    }

    /** Adiciona a seção ao BoxLayout sem deixá-la esticar verticalmente. */
    private static void adicionarSecao(JPanel container, JComponent secao) {
        container.add(new SecaoPanel(secao));
    }

    private static final class SecaoPanel extends JPanel {

        SecaoPanel(JComponent conteudo) {
            super(new BorderLayout());
            setAlignmentX(Component.LEFT_ALIGNMENT);
            add(conteudo, BorderLayout.CENTER);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }
    }
}
