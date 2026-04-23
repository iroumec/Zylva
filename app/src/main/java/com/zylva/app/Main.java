package com.zylva.app;

import com.formdev.flatlaf.FlatLightLaf;
import com.zylva.bdd.EERDiagram;
import com.zylva.common.core.MainFrame;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Application's entry point.
 */
public class Main {

    /**
     * Entry point.
     *
     * @param args List of arguments.
     */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {
        }

        MainFrame frame = new MainFrame("Zylva", new EERDiagram());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}