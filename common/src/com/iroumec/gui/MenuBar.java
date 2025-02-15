package com.iroumec.gui;

import com.iroumec.components.Diagram;
import com.iroumec.userPreferences.LanguageManager;
import com.iroumec.userPreferences.Multilingual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

final class MenuBar extends JMenuBar implements Multilingual {

    private final static String HELP_KEY = "menuBar.help";
    private final static String CLEAN_KEY = "menuBar.clean";
    private final JButton cleanFrameButton, helpButton;

    // TODO: separate this giant constructor in various methods.
    public MenuBar(MainFrame mainFrame, Diagram diagram) {

        LanguageManager.suscribeToLanguageResetList(this);

        initializeMouseListeners(mainFrame);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                            File Menu                                                       */
        /* ---------------------------------------------------------------------------------------------------------- */

        FileMenu fileMenu = new FileMenu(diagram);
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

        this.cleanFrameButton = new JButton(LanguageManager.getMessage(CLEAN_KEY));
        cleanFrameButton.addActionListener(_ -> {
            diagram.reset();
            diagram.repaint();
        });
        add(cleanFrameButton);

        // The button turns red when it detects the mouse.
        cleanFrameButton.setBackground(UIManager.getColor("control"));
        cleanFrameButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cleanFrameButton.setBackground(new Color(215, 239, 249));
            }

            // It returns to its original color.
            public void mouseExited(MouseEvent e) {
                cleanFrameButton.setBackground(UIManager.getColor("control"));
            }
        });
        cleanFrameButton.setBorderPainted(Boolean.FALSE);
        cleanFrameButton.setFocusable(false); // If this is not disabled, when enter is pressed, the frame is cleaned.

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                              Help Button                                                   */
        /* ---------------------------------------------------------------------------------------------------------- */

        // TODO: los controles dependen del diagrama.
        this.helpButton = new JButton(LanguageManager.getMessage(HELP_KEY));
        helpButton.addActionListener(_ -> {

            String controls = LanguageManager.getMessage("menuBar.controls") + ": "
                    + "\nCtrl + E: " + LanguageManager.getMessage("control.addEntity")
                    + "\nCtrl + R: " + LanguageManager.getMessage("control.addRelationship")
                    + "\nCtrl + D: " + LanguageManager.getMessage("control.addDependency")
                    + "\nCtrl + H: " + LanguageManager.getMessage("control.addHierarchy")
                    + "\nCtrl + N: " + LanguageManager.getMessage("control.addNote")
                    + "\nSupr: " + LanguageManager.getMessage("control.delete");

            String credits = LanguageManager.getMessage("credits.author")
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

    private void initializeMouseListeners(MainFrame mainFrame) {

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
                // MainRol frame location is updated.
                mainFrame.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
            }
        });
    }

    @Override
    public void resetLanguage() {

        this.cleanFrameButton.setText(LanguageManager.getMessage(CLEAN_KEY));
        this.helpButton.setText(LanguageManager.getMessage(HELP_KEY));
    }
}
