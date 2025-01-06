package com.bdd.mer.frame.menuBar;

import com.bdd.mer.frame.MainFrame;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.menuBar.exportacion.Archivo;
import com.bdd.mer.frame.menuBar.exportacion.ExportPNG;

import javax.swing.*;

public class FileMenu extends JMenu {

    FileMenu(MainFrame mainFrame, DrawingPanel drawingPanel, String text) {

        // Menu's Text.
        setText(text);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                               Menu's Options                                               */
        /* ---------------------------------------------------------------------------------------------------------- */

        // Export to PNG.
        JMenuItem exportPNG = new JMenuItem("Exportar a PNG");
        exportPNG.addActionListener(_ -> ExportPNG.exportToPng(drawingPanel));

        // Save diagram.
        JMenuItem saveDiagram = new JMenuItem("Guardar diagrama");
        saveDiagram.addActionListener(_ -> Archivo.saveDiagram(drawingPanel));

        // Load diagram.
        JMenuItem loadDiagram = new JMenuItem("Cargar diagrama");
        loadDiagram.addActionListener(_ -> {
            Archivo.loadDiagram(drawingPanel);
            mainFrame.repaint();
        });

        // The options are added to the menu.
        add(exportPNG);
        add(saveDiagram);
        add(loadDiagram);
    }
}
