package com.iroumec.gui;

import com.iroumec.components.Diagram;
import com.iroumec.executables.Item;
import com.iroumec.userPreferences.LanguageManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

final class FileMenu extends JMenu {

    private final static String LANGUAGE_KEY = "fileMenu";

    private final MenuBar owner;
    private final List<Item> items;

    public FileMenu(MenuBar owner, Diagram diagram) {
        this.setText(LanguageManager.getMessage(LANGUAGE_KEY));
        this.owner = owner;
        this.items = new ArrayList<>();

        List<Item> items = diagram.getFileMenuItems();

        for (Item item : items) {
            this.addExecutableItem(item);
        }
    }

    public void addExecutableItem(Item item) {

        // TODO: the language don't have to be reset yet.
        if (item.requireLanguageReset()) {
            item.addActionListener(_ -> FileMenu.this.resetLanguage());
        }

        items.add(item);
        this.add(item);
    }

    void resetLanguage() {

        for (Item item : items) {
            item.resetLanguage();
        }

        owner.resetLanguage();

        this.repaint();
    }
}
