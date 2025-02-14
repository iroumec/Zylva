package com.iroumec.gui;

import com.iroumec.Diagram;
import com.iroumec.userPreferences.LanguageManager;
import com.iroumec.derivation.DerivationManager;

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
        exportPNG.addActionListener(_ -> diagram.exportToPng());

        // Save diagram.
        saveDiagram = new JMenuItem(LanguageManager.getMessage("fileMenu.saveDiagram"));
        saveDiagram.addActionListener(_ -> diagram.saveDiagram());

        // Load diagram.
        loadDiagram = new JMenuItem(LanguageManager.getMessage("fileMenu.loadDiagram"));
        // TODO: loading the diagram should be a static method which returns a new diagram an opens a new tab with it.
        // The previous diagram, if it had elements, it should be maintained in another tab.
        loadDiagram.addActionListener(_ -> diagram.loadDiagram());

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
