package com.bdd.mer.frame.menuBar;

import com.bdd.mer.frame.LanguageManager;
import com.bdd.mer.frame.MainFrame;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuBar extends JMenuBar {

    private static final Point point = new Point(); // Donde apunta el mouse

    public MenuBar(MainFrame mainFrame, DrawingPanel drawingPanel) {

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
        add(new FileMenu(mainFrame, drawingPanel, LanguageManager.getMessage("menuBar.file")));

        // Añado un botón para limpiar la ventana
        JButton cleanFrameButton = new JButton(LanguageManager.getMessage("menuBar.clean"));
        cleanFrameButton.addActionListener(_ -> {
            drawingPanel.reset();
            drawingPanel.repaint();
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
        JButton tipsButton = new JButton(LanguageManager.getMessage("menuBar.help"));

        String controls = """
                CONTROLS: \
                
                Ctrl + E: Add a new entity\
                
                Ctrl + R: Add a new relationship\
                
                Ctrl + D: Add a new dependency\
                
                Ctrl + H: Add a new hierarchy\
                
                Ctrl + N: Add a new note\
                
                Ctrl + A: Add a new association\
                
                Supr: Delete all selected components""";

        String credits = """
                AUTHOR: \
                
                Zilva DERExt was made by Iñaki Roumec\
                
                https://github.com/iroumec""";

        tipsButton.addActionListener(_ -> JOptionPane.showMessageDialog(null, controls + "\n\n" + credits));
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
