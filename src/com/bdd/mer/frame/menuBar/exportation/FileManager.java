package com.bdd.mer.frame.menuBar.exportation;

import com.bdd.mer.components.Component;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.LanguageManager;

import javax.swing.*;
import java.io.*;
import java.util.List;

public final class FileManager {

    public static void saveDiagram(DrawingPanel drawingPanel) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(LanguageManager.getMessage("fileManager.saveDiagram.dialog"));

        // Establecer el filtro para solo mostrar archivos .mer
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Diagram Files", "mer"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {

            File fileToSave = fileChooser.getSelectedFile();

            // Verificar si el archivo tiene la extensión .mer, si no, agregarla
            if (!fileToSave.getName().endsWith(".mer")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".mer");
            }

            List<Component> components = drawingPanel.getListComponents();

            try {

                FileOutputStream fileOut = new FileOutputStream(fileToSave);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);

                out.writeObject(components);

                out.close();
                fileOut.close();

            } catch (IOException i) {

                JOptionPane.showMessageDialog(null,LanguageManager.getMessage("fileManager.saveDiagram.exception"));
            }
        }
    }

    public static void loadDiagram(DrawingPanel drawingPanel) {

        JFileChooser fileChooser = new JFileChooser();

        // Filtro de archivo para solo mostrar archivos .mer
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Diagram Files", "mer"));

        fileChooser.setDialogTitle("Specify the file you want to load.");

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {

            File fileToLoad = fileChooser.getSelectedFile();

            try {

                FileInputStream fileIn = new FileInputStream(fileToLoad);
                ObjectInputStream in = new ObjectInputStream(fileIn);

                List<Component> components = (List<Component>) in.readObject();

                for (Component component : components.reversed()) {
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
