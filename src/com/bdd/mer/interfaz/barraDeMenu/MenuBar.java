package com.bdd.mer.interfaz.barraDeMenu;

import com.bdd.mer.interfaz.MarcoPrincipal;
import com.bdd.mer.interfaz.PanelDibujo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuBar extends JMenuBar {

    private static final Point point = new Point(); // Donde apunta el mouse

    public MenuBar(MarcoPrincipal mainFrame, PanelDibujo panelDibujo) {

        /*
        Añade un MouseListener y un MouseMotionListener a la barra de menú.
        Esto es útil para poder arrastrar la ventana.
         */
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                point.x = e.getX();
                point.y = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                // Obtengo la posición del mouse en la ventana
                Point p = mainFrame.getLocation();
                // Actualizo la posición de la ventana
                mainFrame.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
            }
        });

        // Añado el menú de archivos
        add(new FileMenu(mainFrame, panelDibujo, "File"));

        // Añado un botón para limpiar la ventana
        JButton cleanFrameButton = new JButton("Clean");
        cleanFrameButton.addActionListener(_ -> {
            panelDibujo.reiniciar();
            panelDibujo.repaint();
        });
        add(cleanFrameButton);
        // Al pasar el mouse por encima, el fondo se coloca en rojo
        cleanFrameButton.setBackground(UIManager.getColor("control"));
        cleanFrameButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cleanFrameButton.setBackground(new Color(215, 239, 249));
            }

            // Cuando saco el puntero, el botón vuelve a su color original
            public void mouseExited(MouseEvent e) {
                cleanFrameButton.setBackground(UIManager.getColor("control"));
            }
        });
        cleanFrameButton.setBorderPainted(Boolean.FALSE);

        // Añado un botón para aprender
        JButton tipsButton = new JButton("Help");
        tipsButton.addActionListener(_ -> {
            JOptionPane.showMessageDialog(null, "You can press Supr to delete all selected components \n\n Zilva DERExt was made by Iñaki Roumec");
        });
        add(tipsButton);
        // Al pasar el mouse por encima, el fondo se coloca en rojo
        tipsButton.setBackground(UIManager.getColor("control"));
        tipsButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                tipsButton.setBackground(new Color(215, 239, 249));
            }

            // Cuando saco el puntero, el botón vuelve a su color original
            public void mouseExited(MouseEvent e) {
                tipsButton.setBackground(UIManager.getColor("control"));
            }
        });
        tipsButton.setBorderPainted(Boolean.FALSE);

        add(tipsButton);
    }
}
