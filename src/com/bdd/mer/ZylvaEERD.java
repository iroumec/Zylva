package com.bdd.mer;

import com.bdd.mer.frame.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class ZylvaEERD {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception _) {
        }

        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Crear ventana
        JFrame frame2 = new JFrame("Ejemplo con caracteres en hindi");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setSize(400, 200);

        // Crear botón con texto en hindi
        JButton button = new JButton("नमस्ते"); // नमस्ते
        //JButton button = new JButton("नमस्ते"); // "Namaste" en hindi
        // Configurar la fuente (asegúrate de tener la fuente instalada)
        button.setFont(new Font("TamilFont", Font.PLAIN, 18));

        // Agregar botón al marco
        frame2.add(button);

        // Mostrar ventana
        frame2.setVisible(true);
    }
}