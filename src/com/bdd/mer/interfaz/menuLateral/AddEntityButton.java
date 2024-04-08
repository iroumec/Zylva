package com.bdd.mer.interfaz.menuLateral;

import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.interfaz.MarcoPrincipal;
import com.bdd.mer.interfaz.PanelDibujo;

import javax.swing.*;
import java.awt.*;

public class AddEntityButton extends JButton {

    public AddEntityButton(String name, Dimension dimension, String fuente, MarcoPrincipal mainFrame, PanelDibujo panelDibujo) {
        setText(name);
        setMaximumSize(dimension);
        setBackground(Color.WHITE);
        setOpaque(Boolean.TRUE);
        setFont(new Font(fuente, Font.BOLD, 12));

        // Defino la función del botón
        addActionListener(_ -> {
            // Muestra una ventana emergente para ingresar el nombre de la entidad. La ventana está centrada en el marco principal
            String nombre = JOptionPane.showInputDialog(mainFrame, "Ingrese el nombre de la nueva entidad");
            if (nombre != null) {
                // Si el usuario ingresó un nombre, agrega una nueva entidad con ese nombre
                panelDibujo.agregarEntidad(new Entidad(nombre, 50, 50));
            }
        });
    }
}
