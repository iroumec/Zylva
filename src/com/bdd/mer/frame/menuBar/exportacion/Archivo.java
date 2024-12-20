package com.bdd.mer.frame.menuBar.exportacion;

import com.bdd.mer.components.Component;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.io.*;
import java.util.List;

public final class Archivo {

    public static void guardarDiagrama(DrawingPanel drawingPanel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccione dónde guardar el archivo y coloque un nombre");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Aquí puedes escribir el código para guardar tus datos en 'fileToSave'

            List<Component> components = drawingPanel.getListComponents();

            try {
                FileOutputStream fileOut = new FileOutputStream(fileToSave);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(components);
                out.close();
                fileOut.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    public static void cargarDiagrama(DrawingPanel drawingPanel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Especifica un archivo para cargar");

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();

            // Aquí puedes escribir el código para cargar tus datos desde 'fileToLoad'
            try {
                FileInputStream fileIn = new FileInputStream(fileToLoad);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                drawingPanel.addComponents((List<Component>) in.readObject());
                in.close();
                fileIn.close();
            } catch (IOException i) {
                JOptionPane.showMessageDialog(null,"El programa no encontró el archivo especificado.");
            } catch (ClassNotFoundException c) {
                System.out.println("Las clases no se encontraron");
                c.printStackTrace();
            }
        }
    }
}
