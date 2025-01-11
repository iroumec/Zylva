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
    private JButton helpButton;
    private String controls, credits;

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

        fileMenu.setBackground(UIManager.getColor("control"));
        fileMenu.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                fileMenu.setBackground(new Color(215, 239, 249));
            }

            // Cuando saco el puntero, el botón vuelve a su color original
            public void mouseExited(MouseEvent e) {
                fileMenu.setBackground(UIManager.getColor("control"));
            }
        });
        fileMenu.setBorderPainted(Boolean.FALSE);

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

        this.setHelpButton();

        add(helpButton);
    }

    private void setHelpButton() {

        this.controls = LanguageManager.getMessage("menuBar.controls") + ": "
                + "\nCtrl + E: " + LanguageManager.getMessage("add.entity")
                + "\nCtrl + R: " + LanguageManager.getMessage("add.relationship")
                + "\nCtrl + D: " + LanguageManager.getMessage("add.dependency")
                + "\nCtrl + H: " + LanguageManager.getMessage("add.hierarchy")
                + "\nCtrl + N: " + LanguageManager.getMessage("add.note")
                + "\nCtrl + A: " + LanguageManager.getMessage("add.association")
                + "\nSupr: " + LanguageManager.getMessage("menuBar.delete");

        this.credits = LanguageManager.getMessage("credits.author")
                + '\n' + LanguageManager.getMessage("credits.credits")
                + "\nhttps://github.com/iroumec";

        helpButton = new JButton(LanguageManager.getMessage("menuBar.help"));
        helpButton.addActionListener(_ -> JOptionPane.showMessageDialog(null, controls + "\n\n" + credits));

        // Al pasar el mouse por encima, el botón se vuelve azulito..
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

    }

    public void resetLanguage() {
        this.fileMenu.setText(LanguageManager.getMessage("menuBar.file"));
        this.fileMenu.resetLanguage();
        this.cleanFrameButton.setText(LanguageManager.getMessage("menuBar.clean"));
        this.cleanFrameButton.repaint();
        this.setHelpButton(); // The only way I found to change the language of the message is to create it again.
        System.out.println(LanguageManager.getMessage("menuBar.help"));
        this.helpButton.setText(LanguageManager.getMessage("menuBar.help"));
        System.out.println(LanguageManager.getMessage("menuBar.help"));
        System.out.println(this.helpButton.getText());
        this.helpButton.repaint();
    }
}
