package com.bdd.mer;

import com.bdd.mer.frame.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class DiagramaERApp {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            // Si FlatLightLaf no está disponible, puedes configurar otro look and feel.
        }
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}