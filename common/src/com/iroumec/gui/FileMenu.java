package com.iroumec.gui;

import com.iroumec.components.Diagram;
import com.iroumec.userPreferences.LanguageManager;
import com.iroumec.userPreferences.Multilingual;

import javax.swing.*;
import java.util.List;

final class FileMenu extends JMenu implements Multilingual {

    private final static String LANGUAGE_KEY = "fileMenu";

    public FileMenu(Diagram diagram) {

        super(LanguageManager.getMessage(LANGUAGE_KEY));

        LanguageManager.suscribeToLanguageResetList(this);

        List<Item> items = diagram.getFileMenuItems();

        for (Item item : items) {
            LanguageManager.suscribeToLanguageResetList(item);
            this.add(item);
        }
    }

    @Override
    public void resetLanguage() {

        this.setText(LanguageManager.getMessage(LANGUAGE_KEY));

        this.repaint();
    }
}
