package com.zylva.common.core;

import com.zylva.common.userPreferences.Multilingual;
import com.zylva.common.userPreferences.LanguageManager;

import java.util.List;

import javax.swing.JMenu;

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
