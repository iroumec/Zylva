package com.bdd.mer.frame.menuBar;

import com.bdd.mer.frame.LanguageManager;
import com.bdd.mer.frame.MainFrame;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.menuBar.exportation.FileManager;
import com.bdd.mer.frame.menuBar.exportation.ExportPNG;

import javax.swing.*;
import java.util.Arrays;

public class FileMenu extends JMenu {

    private JMenuItem exportPNG, saveDiagram, loadDiagram, changeLanguage;

    FileMenu(MainFrame mainFrame, DrawingPanel drawingPanel, String text) {

        // Menu's Text.
        setText(text);

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
        loadDiagram.addActionListener(_ -> {
            FileManager.loadDiagram(drawingPanel);
            //mainFrame.repaint();
        });

        // Change language.
        changeLanguage = new JMenuItem(LanguageManager.getMessage("fileMenu.changeLanguage"));
        changeLanguage.addActionListener(_ -> LanguageManager.changeLanguage(mainFrame));


        // The options are added to the menu.
        add(exportPNG);
        add(saveDiagram);
        add(loadDiagram);
        add(changeLanguage);
    }

    public void resetLanguage() {
        exportPNG.setText(LanguageManager.getMessage("fileMenu.exportPNG"));
        saveDiagram.setText(LanguageManager.getMessage("fileMenu.saveDiagram"));
        loadDiagram.setText(LanguageManager.getMessage("fileMenu.loadDiagram"));
        changeLanguage.setText(LanguageManager.getMessage("fileMenu.changeLanguage"));
        this.repaint();
    }

}
