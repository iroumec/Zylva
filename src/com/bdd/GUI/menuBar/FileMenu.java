package com.bdd.GUI.menuBar;

import com.bdd.mer.derivation.DerivationManager;
import com.bdd.GUI.Diagram;
import com.bdd.GUI.userPreferences.LanguageManager;
import com.bdd.GUI.MainFrame;
import com.bdd.GUI.menuBar.exportation.FileManager;
import com.bdd.GUI.menuBar.exportation.ExportPNG;

import javax.swing.*;

class FileMenu extends JMenu {

    private final Diagram diagram;
    private final JMenuItem exportPNG, saveDiagram, loadDiagram, changeLanguage, changeAntialiasing, derivateDiagram;

    FileMenu(MainFrame mainFrame, Diagram diagram, String text) {

        // Menu's Text.
        setText(text);

        this.diagram = diagram;

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                               Menu's Options                                               */
        /* ---------------------------------------------------------------------------------------------------------- */

        // Export to PNG.
        exportPNG = new JMenuItem(LanguageManager.getMessage("fileMenu.exportPNG"));
        exportPNG.addActionListener(_ -> ExportPNG.exportToPng(diagram));

        // Save diagram.
        saveDiagram = new JMenuItem(LanguageManager.getMessage("fileMenu.saveDiagram"));
        saveDiagram.addActionListener(_ -> FileManager.saveDiagram(diagram));

        // Load diagram.
        loadDiagram = new JMenuItem(LanguageManager.getMessage("fileMenu.loadDiagram"));
        loadDiagram.addActionListener(_ -> FileManager.loadDiagram(diagram));

        // Derivate diagram.
        derivateDiagram = new JMenuItem("Derivate"/*LanguageManager.getMessage("fileMenu.derivateDiagram")*/);
        derivateDiagram.addActionListener(_ -> DerivationManager.derivate(diagram));

        // Change language.
        changeLanguage = new JMenuItem(LanguageManager.getMessage("fileMenu.changeLanguage"));
        changeLanguage.addActionListener(_ -> LanguageManager.changeLanguage(mainFrame));

        // Change antialiasing.
        changeAntialiasing = new JMenuItem();
        this.getAntialiasingMenuItemName();

        changeAntialiasing.addActionListener(_ -> {
            diagram.setAntialiasing(!diagram.isAntialiasingActive());
            this.getAntialiasingMenuItemName();
        });

        // The options are added to the menu.
        add(exportPNG);
        add(saveDiagram);
        add(loadDiagram);
        add(derivateDiagram);
        add(changeLanguage);
        add(changeAntialiasing);
    }

    void resetLanguage() {
        exportPNG.setText(LanguageManager.getMessage("fileMenu.exportPNG"));
        saveDiagram.setText(LanguageManager.getMessage("fileMenu.saveDiagram"));
        loadDiagram.setText(LanguageManager.getMessage("fileMenu.loadDiagram"));
        changeLanguage.setText(LanguageManager.getMessage("fileMenu.changeLanguage"));
        this.getAntialiasingMenuItemName();
        this.repaint();
    }

    private void getAntialiasingMenuItemName() {

        if (this.diagram.isAntialiasingActive()) {
            changeAntialiasing.setText(LanguageManager.getMessage("fileMenu.deactivateAntialiasing"));
        } else {
            changeAntialiasing.setText(LanguageManager.getMessage("fileMenu.activateAntialiasing"));
        }
    }

}
