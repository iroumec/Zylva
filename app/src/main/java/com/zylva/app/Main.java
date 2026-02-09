package com.zylva.app;

import com.formdev.flatlaf.FlatLightLaf;
import com.zylva.bdd.EERDiagram;
import com.zylva.common.core.MainFrame;

import javax.swing.*;

public class Main {

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