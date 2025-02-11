package com.bdd.GUI.menuBar;

import com.bdd.GUI.Diagram;
import com.bdd.GUI.userPreferences.LanguageManager;
import com.bdd.GUI.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuBar extends JMenuBar {

    private final FileMenu fileMenu;
    private final JButton cleanFrameButton, helpButton;

    public MenuBar(MainFrame mainFrame, Diagram diagram) {

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                      Mouse Interactions Related                                            */
        /* ---------------------------------------------------------------------------------------------------------- */

        // Mouse position.
        Point point = new Point();

        // They are useful for when the frame is dragged.
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                point.x = e.getX();
                point.y = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                // Mouse position in the frame.
                Point p = mainFrame.getLocation();
                // Main frame location is updated.
                mainFrame.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                            File Menu                                                       */
        /* ---------------------------------------------------------------------------------------------------------- */

        this.fileMenu = new FileMenu(mainFrame, diagram, LanguageManager.getMessage("menuBar.file"));

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

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                           Clean Frame Button                                               */
        /* ---------------------------------------------------------------------------------------------------------- */

        // Añado un botón para limpiar la ventana
        this.cleanFrameButton = new JButton(LanguageManager.getMessage("menuBar.clean"));
        cleanFrameButton.addActionListener(_ -> {
            diagram.reset();
            diagram.repaint();
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

        cleanFrameButton.setFocusable(false); // If this is not disabled, when enter is pressed, the frame is cleaned.

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                              Help Button                                                   */
        /* ---------------------------------------------------------------------------------------------------------- */

        helpButton = new JButton(LanguageManager.getMessage("menuBar.help"));
        helpButton.addActionListener(_ -> {

            // The texts are define here and a stringBuilder is used so that they're automatically
            // updated when the language is changed.
            String controls = LanguageManager.getMessage("menuBar.controls") + ": "
                    + "\nCtrl + E: " + LanguageManager.getMessage("add.entity")
                    + "\nCtrl + R: " + LanguageManager.getMessage("add.relationship")
                    + "\nCtrl + D: " + LanguageManager.getMessage("add.dependency")
                    + "\nCtrl + H: " + LanguageManager.getMessage("add.hierarchy")
                    + "\nCtrl + N: " + LanguageManager.getMessage("add.note")
                    + "\nCtrl + A: " + LanguageManager.getMessage("add.association")
                    + "\nSupr: " + LanguageManager.getMessage("menuBar.delete");

            String credits = LanguageManager.getMessage("credits.author")
                    + '\n' + LanguageManager.getMessage("credits.credits")
                    + "\nhttps://github.com/iroumec";

            JOptionPane.showMessageDialog(null, controls + "\n\n" + credits); // Show the updated message
        });

        // The button becomes blue when the mouse enter its bounds.
        helpButton.setBackground(UIManager.getColor("control"));
        helpButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                helpButton.setBackground(new Color(215, 239, 249));
            }

            public void mouseExited(MouseEvent e) {
                helpButton.setBackground(UIManager.getColor("control"));
            }
        });
        helpButton.setBorderPainted(Boolean.FALSE);

        helpButton.setFocusable(false);

        add(helpButton);
    }

    public void resetLanguage() {
        this.fileMenu.setText(LanguageManager.getMessage("menuBar.file"));
        this.fileMenu.resetLanguage();
        this.cleanFrameButton.setText(LanguageManager.getMessage("menuBar.clean"));
        this.helpButton.setText(LanguageManager.getMessage("menuBar.help"));
    }
}
