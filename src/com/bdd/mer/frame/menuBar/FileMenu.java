package com.bdd.mer.frame.menuBar;

import com.bdd.mer.frame.LanguageManager;
import com.bdd.mer.frame.MainFrame;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.menuBar.exportation.FileManager;
import com.bdd.mer.frame.menuBar.exportation.ExportPNG;

import javax.swing.*;

public class FileMenu extends JMenu {

    private final DrawingPanel drawingPanel;
    private final JMenuItem exportPNG, saveDiagram, loadDiagram, changeLanguage, changeAntialiasing;

    FileMenu(MainFrame mainFrame, DrawingPanel drawingPanel, String text) {

        // Menu's Text.
        setText(text);

        this.drawingPanel = drawingPanel;

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                               Menu's Options                                               */
        /* ---------------------------------------------------------------------------------------------------------- */

        // Export to PNG.
        exportPNG = new JMenuItem(LanguageManager.getMessage("fileMenu.exportPNG"));
        exportPNG.addActionListener(_ -> ExportPNG.exportToPng(drawingPanel));

        // Save diagram.
        saveDiagram = new JMenuItem(LanguageManager.getMessage("fileMenu.saveDiagram"));
        saveDiagram.addActionListener(_ -> FileManager.saveDiagram(drawingPanel));

        // Load diagram.
        loadDiagram = new JMenuItem(LanguageManager.getMessage("fileMenu.loadDiagram"));
        loadDiagram.addActionListener(_ -> FileManager.loadDiagram(drawingPanel));

        // Change language.
        changeLanguage = new JMenuItem(LanguageManager.getMessage("fileMenu.changeLanguage"));
        changeLanguage.addActionListener(_ -> LanguageManager.changeLanguage(mainFrame));

        // Change antialiasing.
        changeAntialiasing = new JMenuItem();
        this.getAntialiasingMenuItemName();

        changeAntialiasing.addActionListener(_ -> {
            drawingPanel.setAntialiasing(!drawingPanel.isAntialiasingActive());
            this.getAntialiasingMenuItemName();
        });

        // The options are added to the menu.
        add(exportPNG);
        add(saveDiagram);
        add(loadDiagram);
        add(changeLanguage);
        add(changeAntialiasing);
    }

    public void resetLanguage() {
        exportPNG.setText(LanguageManager.getMessage("fileMenu.exportPNG"));
        saveDiagram.setText(LanguageManager.getMessage("fileMenu.saveDiagram"));
        loadDiagram.setText(LanguageManager.getMessage("fileMenu.loadDiagram"));
        changeLanguage.setText(LanguageManager.getMessage("fileMenu.changeLanguage"));
        this.getAntialiasingMenuItemName();
        this.repaint();
    }

    private void getAntialiasingMenuItemName() {

        if (this.drawingPanel.isAntialiasingActive()) {
            changeAntialiasing.setText(LanguageManager.getMessage("fileMenu.deactivateAntialiasing"));
        } else {
            changeAntialiasing.setText(LanguageManager.getMessage("fileMenu.activateAntialiasing"));
        }
    }

}
