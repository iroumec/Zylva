package com.bdd.mer.interfaz.exportacion;

import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Jerarquia;
import com.bdd.mer.estatica.Relacion;
import com.bdd.mer.interfaz.PanelDibujo;
import com.bdd.mer.interfaz.anotacion.Nota;

import javax.swing.*;
import java.io.*;
import java.util.List;

public final class Archivo {

    public static void guardarDiagrama(PanelDibujo panelDibujo) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccione dónde guardar el archivo y coloque un nombre");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Aquí puedes escribir el código para guardar tus datos en 'fileToSave'

            List<Entidad> entidades = panelDibujo.getEntidades();
            List <Relacion> relaciones = panelDibujo.getRelaciones();
            List<Jerarquia> jerarquias = panelDibujo.getJerarquias();
            List<Nota> notas = panelDibujo.getNotas();

            try {
                FileOutputStream fileOut = new FileOutputStream(fileToSave);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(entidades);
                out.writeObject(relaciones);
                out.writeObject(jerarquias);
                out.writeObject(notas);
                out.close();
                fileOut.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    public static void cargarDiagrama(PanelDibujo panelDibujo) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Especifica un archivo para cargar");

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();

            // Aquí puedes escribir el código para cargar tus datos desde 'fileToLoad'
            try {
                FileInputStream fileIn = new FileInputStream(fileToLoad);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                panelDibujo.setEntidades((List<Entidad>) in.readObject());
                panelDibujo.setRelaciones((List<Relacion>) in.readObject());
                panelDibujo.setJerarquias((List<Jerarquia>) in.readObject());
                panelDibujo.setNotas((List<Nota>) in.readObject());
                in.close();
                fileIn.close();
            } catch (IOException i) {
                JOptionPane.showMessageDialog(null,"El programa no encontró el archivo especificado.");
            } catch (ClassNotFoundException c) {
                System.out.println("Las clases no se encontraron");
                c.printStackTrace();
                return;
            }
        }
    }
}
