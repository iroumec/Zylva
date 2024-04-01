package com.bdd.mer;

import javax.swing.*;

public class test {
    public static void main(String[] args) {
        // Crea los radio buttons
        JRadioButton option1 = new JRadioButton("Exclusiva", true);
        JRadioButton option2 = new JRadioButton("Compartida");

        // Agrupa los radio buttons para que solo se pueda seleccionar uno a la vez
        ButtonGroup group = new ButtonGroup();
        group.add(option1);
        group.add(option2);

        // Crea un panel para contener los radio buttons
        JPanel panel = new JPanel();
        panel.add(option1);
        panel.add(option2);

        // Muestra el panel en un JOptionPane
        JOptionPane.showOptionDialog(null, panel, "Elige una opción",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
    }
}
