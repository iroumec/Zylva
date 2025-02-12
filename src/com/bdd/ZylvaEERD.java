package com.bdd;

import com.bdd.GUI.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class ZylvaEERD {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception _) {
        }

        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}