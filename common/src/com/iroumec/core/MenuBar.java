package com.iroumec.core;

import com.iroumec.userPreferences.LanguageManager;
import com.iroumec.userPreferences.Multilingual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

final class MenuBar extends JMenuBar implements Multilingual {

    private final static Color HIGHLIGHT_COLOR = new Color(215, 239, 249);
    private final static String HELP_KEY = "menuBar.help";
    private final static String CLEAN_KEY = "menuBar.clean";
    private final JButton cleanFrameButton, helpButton;

    public MenuBar(MainFrame mainFrame, Diagram diagram) {

        LanguageManager.suscribeToLanguageResetList(this);

        initializeMouseListeners(mainFrame);

        initializeFileMenu(diagram);

        this.cleanFrameButton = new JButton(LanguageManager.getMessage(CLEAN_KEY));
        initializeCleanButton(diagram);

        this.helpButton = new JButton(LanguageManager.getMessage(HELP_KEY));
        initializeHelpButton(diagram);
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

    private void initializeFileMenu(Diagram diagram) {

        FileMenu fileMenu = new FileMenu(diagram);

        addMouseInteraction(fileMenu);

        add(fileMenu);
    }

    private void initializeCleanButton(Diagram diagram) {

        cleanFrameButton.addActionListener(_ -> {
            diagram.reset();
            diagram.repaint();
        });
        add(cleanFrameButton);

        addMouseInteraction(cleanFrameButton);

        cleanFrameButton.setFocusable(false); // If this is not disabled, when enter is pressed, the frame is cleaned.
    }

    private void addMouseInteraction(AbstractButton button) {

        // The button changes its color when the mouse pass over it.
        button.setBackground(UIManager.getColor("control"));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HIGHLIGHT_COLOR);
            }

            // It returns to its original color.
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIManager.getColor("control"));
            }
        });
        button.setBorderPainted(Boolean.FALSE);
    }

    private void initializeHelpButton(Diagram diagram) {

        helpButton.addActionListener(_ -> {

            String controls = LanguageManager.getMessage("menuBar.controls") + ": "
                    + diagram.getControls();

            String credits = LanguageManager.getMessage("credits.author")
                    + "\nhttps://github.com/iroumec";

            JOptionPane.showMessageDialog(null, controls + "\n\n" + credits); // Show the updated message
        });

        this.addMouseInteraction(helpButton);

        helpButton.setFocusable(false);
        add(helpButton);
    }

    @Override
    public void resetLanguage() {

        this.cleanFrameButton.setText(LanguageManager.getMessage(CLEAN_KEY));
        this.helpButton.setText(LanguageManager.getMessage(HELP_KEY));
    }
}
