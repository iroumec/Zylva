package com.bdd.mer.frame.menuBar;

import com.bdd.mer.frame.LanguageManager;
import com.bdd.mer.frame.MainFrame;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuBar extends JMenuBar {

    private static final Point point = new Point(); // Donde apunta el mouse
    private final FileMenu fileMenu;
    private final JButton cleanFrameButton;
    private final JButton helpButton;

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
        this.fileMenu = new FileMenu(mainFrame, drawingPanel, LanguageManager.getMessage("menuBar.file"));
        add(fileMenu);

        // Añado un botón para limpiar la ventana
        this.cleanFrameButton = new JButton(LanguageManager.getMessage("menuBar.clean"));
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
        this.helpButton = new JButton(LanguageManager.getMessage("menuBar.help"));

        String controls = """
                CONTROLS: \
                
                Ctrl + E: Add a new entity\
                
                Ctrl + R: Add a new relationship\
                
                Ctrl + D: Add a new dependency\
                
                Ctrl + H: Add a new hierarchy\
                
                Ctrl + N: Add a new note\
                
                Ctrl + A: Add a new association\
                
                Supr: Delete all selected components""";

        String credits = LanguageManager.getMessage("credits.author") + """
                \
                
                Zylva DERExt was made by Iñaki Roumec\
                
                https://github.com/iroumec""";

        helpButton.addActionListener(_ -> JOptionPane.showMessageDialog(null, controls + "\n\n" + credits));
        add(helpButton);

        // Al pasar el mouse por encima, el fondo se coloca en rojo
        helpButton.setBackground(UIManager.getColor("control"));
        helpButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                helpButton.setBackground(new Color(215, 239, 249));
            }

            // Cuando saco el puntero, el botón vuelve a su color original
            public void mouseExited(MouseEvent e) {
                helpButton.setBackground(UIManager.getColor("control"));
            }
        });
        helpButton.setBorderPainted(Boolean.FALSE);

        add(helpButton);
    }

    public void resetLanguage() {
        this.fileMenu.setText(LanguageManager.getMessage("menuBar.file"));
        this.fileMenu.resetLanguage();
        this.cleanFrameButton.setText(LanguageManager.getMessage("menuBar.clean"));
        this.cleanFrameButton.repaint();
        this.helpButton.setText(LanguageManager.getMessage("menuBar.help"));
        this.helpButton.repaint();
    }
}
