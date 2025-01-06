package com.bdd.mer.frame.menuBar.exportacion;

import com.bdd.mer.components.Component;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.io.*;
import java.util.List;

public final class Archivo {

    public static void saveDiagram(DrawingPanel drawingPanel) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select the folder where you want to save the file and put it a name.");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {

            File fileToSave = fileChooser.getSelectedFile();

            List<Component> components = drawingPanel.getListComponents();

            try {

                FileOutputStream fileOut = new FileOutputStream(fileToSave);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);

                out.writeObject(components);

                out.close();
                fileOut.close();

            } catch (IOException i) {

                JOptionPane.showMessageDialog(null,"An unexpected error occurred while saving the file.");
            }
        }
    }

    public static void loadDiagram(DrawingPanel drawingPanel) {

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Especify the file you want to load.");

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {

            File fileToLoad = fileChooser.getSelectedFile();

            try {

                FileInputStream fileIn = new FileInputStream(fileToLoad);
                ObjectInputStream in = new ObjectInputStream(fileIn);

                List<Component> components = (List<Component>) in.readObject();

                for (Component component : components) {
                    drawingPanel.addComponent(component);
                }

                in.close();
                fileIn.close();

            } catch (IOException i) {

                JOptionPane.showMessageDialog(null,"The program didn't find the specified file.");

            } catch (ClassNotFoundException c) {

                // The classes were not found.
                JOptionPane.showMessageDialog(null,"An unexpected error occurred while saving the file.");

            }
        }
    }
}
