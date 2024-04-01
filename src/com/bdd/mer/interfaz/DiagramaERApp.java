package com.bdd.mer.interfaz;

import javax.swing.*;
import java.awt.*;

public class DiagramaERApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MarcoPrincipal frame = new MarcoPrincipal();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });

    }
}