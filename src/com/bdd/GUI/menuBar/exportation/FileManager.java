package com.bdd.GUI.menuBar.exportation;

import com.bdd.GUI.components.Component;
import com.bdd.GUI.Diagram;
import com.bdd.GUI.userPreferences.LanguageManager;

import javax.swing.*;
import java.io.*;
import java.util.List;

public final class FileManager {

    private FileManager() {
        throw new AssertionError(); // Security oriented.
    }

    public static void saveDiagram(Diagram diagram) {

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

            List<Component> components = diagram.getListComponents();

            try {

                FileOutputStream fileOut = new FileOutputStream(fileToSave);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);

                out.writeObject(components);

                out.close();
                fileOut.close();

                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("fileSavedSuccessfully"));

            } catch (IOException i) {

                JOptionPane.showMessageDialog(null,LanguageManager.getMessage("fileManager.saveDiagram.exception"));
            }
        }
    }

    public static void loadDiagram(Diagram diagram) {

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

                @SuppressWarnings("unchecked")
                List<Component> components = (List<Component>) in.readObject();

                diagram.reset();

                for (Component component : components.reversed()) {

                    diagram.addComponent(component);

                    // If the PopupMenu is not reset, the actions menus are not shown.
                    component.resetPopupMenu();

                    // If the Diagram is not set again, the actions have no effect.
                    // This doesn't fix anything. Why attributes are not visible?
                    component.setDrawingPanel(diagram);
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
