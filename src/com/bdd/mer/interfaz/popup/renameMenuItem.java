package com.bdd.mer.interfaz.popup;

import com.bdd.mer.estatica.Arrastrable;
import com.bdd.mer.interfaz.PanelDibujo;

import javax.swing.*;

public class renameMenuItem extends JMenuItem {

    renameMenuItem(String name, PopupMenu popupMenu, PanelDibujo panelDibujo) {
        setText(name);

        addActionListener(_ -> {
            Arrastrable objeto = popupMenu.getObject();
            String newText;

            do {
                newText = JOptionPane.showInputDialog(null, "Ingrese el nuevo texto: ");

                // "newText" can be null when the user pressed "cancel"
                if (newText != null && newText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ingrese al menos un carácter");
                }
            } while (newText != null && newText.isEmpty());

            // If "Cancel" was not pressed
            if (newText != null) {
                objeto.setText(newText);
                panelDibujo.repaint();
            }
        });
    }
}
