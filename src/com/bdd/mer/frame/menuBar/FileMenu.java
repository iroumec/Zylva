package com.bdd.mer.frame.menuBar;

import com.bdd.mer.frame.MainFrame;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.menuBar.exportacion.Archivo;
import com.bdd.mer.frame.menuBar.exportacion.ExportPNG;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileMenu extends JMenu {

    FileMenu(MainFrame mainFrame, DrawingPanel drawingPanel, String texto) {

        // Defino el texto del menú
        setText("File");

        // Defino las opciones del menú
        // -> Exportar a PNG
        JMenuItem exportPNG = new JMenuItem("Exportar a PNG");
        exportPNG.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ExportPNG.exportToPng(drawingPanel);
            }
        });

        // -> Guardar diagrama
        JMenuItem saveDiagram = new JMenuItem("Guardar diagrama");
        saveDiagram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Archivo.guardarDiagrama(drawingPanel);
            }
        });

        // -> Cargar diagrama
        JMenuItem loadDiagram = new JMenuItem("Cargar diagrama");
        loadDiagram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Archivo.cargarDiagrama(drawingPanel);
                mainFrame.repaint();
            }
        });

        // Añado las opciones al menú
        add(exportPNG);
        add(saveDiagram);
        add(loadDiagram);
    }
}
