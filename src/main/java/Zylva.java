package main.java;

import com.formdev.flatlaf.FlatLightLaf;
import com.iroumec.eerd.EERDiagram;
import com.iroumec.gui.MainFrame;

import javax.swing.*;

public class Zylva {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception _) {
        }

        MainFrame frame = new MainFrame("Zylva", new EERDiagram());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}