package com.carfinancing;

import com.carfinancing.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // mantém o look and feel padrão
            }
            new MainFrame().setVisible(true);
        });
    }
}
