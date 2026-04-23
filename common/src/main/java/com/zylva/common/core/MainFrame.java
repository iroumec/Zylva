package com.zylva.common.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BoxLayout;

public final class MainFrame extends JFrame {

    public MainFrame(final String title, final Diagram diagram) {

        setUndecorated(false); // Removing of the title bar.
        setTitle(title);

        int FRAME_WIDTH = 800;
        int FRAME_HEIGHT = 600;
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
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
