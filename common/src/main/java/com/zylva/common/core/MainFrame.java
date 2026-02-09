package com.zylva.common.core;

import javax.swing.*;
import java.awt.*;

public final class MainFrame extends JFrame {

    public MainFrame(String title, Diagram diagram) {

        setUndecorated(false);  // Removing of the title bar.
        setTitle(title);
        setSize(800, 600);
        setLocationRelativeTo(null);

        MenuBar menuBar = new MenuBar(this, diagram);
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
        Dimension dimension = new Dimension(120, 30);

        menu.setPreferredSize(dimension);
        menu.setBackground(Color.LIGHT_GRAY);

        for (JButton key : diagram.getMainFrameKeys()) {
            getContentPane().add(key);
        }

        add(diagram, BorderLayout.CENTER);
        setJMenuBar(menuBar);
    }
}
