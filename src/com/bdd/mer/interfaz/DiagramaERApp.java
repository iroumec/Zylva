package com.bdd.mer.interfaz;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class DiagramaERApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                /*for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    System.out.println(info.getName());
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }*/
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception e) {
                // If Nimbus is not available, you can set the look and feel to another.
            }

            MarcoPrincipal frame = new MarcoPrincipal();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });

    }
}