package com.iroumec.gui;

import com.iroumec.components.Diagram;
import com.iroumec.executables.Button;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class MainFrame extends JFrame {

    private final Diagram diagram;

    public MainFrame(String title, Diagram diagram) {

        setUndecorated(false);  // Removing of the title bar.
        setTitle(title);
        setSize(800, 600);
        setLocationRelativeTo(null);

        this.diagram = diagram;
        MenuBar menuBar = new MenuBar(this, diagram);
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
        Dimension dimension = new Dimension(120, 30);

        menu.setPreferredSize(dimension);
        menu.setBackground(Color.LIGHT_GRAY);

        // TODO: don't use the Button clas here, but another class key.
        List<Button> keys = this.diagram.getMainFrameKeys();

        for (Button key : keys) {
            getContentPane().add(key);
        }

        add(diagram, BorderLayout.CENTER);
        setJMenuBar(menuBar);
    }

    public void resetLanguage() {
        this.diagram.resetLanguage();
    }
}
