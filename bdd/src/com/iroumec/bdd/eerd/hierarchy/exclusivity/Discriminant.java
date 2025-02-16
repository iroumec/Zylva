package com.iroumec.bdd.eerd.hierarchy.exclusivity;

import com.iroumec.components.Guard;
import com.iroumec.components.Line;
import com.iroumec.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class Discriminant extends Guard {

    public Discriminant(@NotNull String text, Line line) {
        super(text, line);
    }

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem actionItem = new JMenuItem(LanguageManager.getMessage("action.rename"));
        actionItem.addActionListener(_ -> this.rename());
        popupMenu.add(actionItem);

        return popupMenu;
    }
}
