package com.iroumec.gui;

import com.iroumec.executables.Item;
import com.iroumec.userPreferences.LanguageManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public final class FileMenu extends JMenu {

    private final static String LANGUAGE_KEY = "fileMenu";

    private final MenuBar owner;
    private final List<Item> items;

    public FileMenu(MenuBar owner) {
        this.setText(LanguageManager.getMessage(LANGUAGE_KEY));
        this.owner = owner;
        this.items = new ArrayList<>();
    }

    public void addExecutableItem(Item item) {

        if (item.requireLanguageReset()) {
            item.addActionListener(_ -> FileMenu.this.resetLanguage());
        }

        items.add(item);
    }

    void resetLanguage() {

        for (Item item : items) {
            item.resetLanguage();
        }

        owner.resetLanguage();

        this.repaint();
    }

//    private void getAntialiasingMenuItemName() {
//
//        if (this.diagram.isAntialiasingActive()) {
//            changeAntialiasing.setText(LanguageManager.getMessage("fileMenu.deactivateAntialiasing"));
//        } else {
//            changeAntialiasing.setText(LanguageManager.getMessage("fileMenu.activateAntialiasing"));
//        }
//    }

}
