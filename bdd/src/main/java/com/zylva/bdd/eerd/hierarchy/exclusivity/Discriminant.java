package com.zylva.bdd.eerd.hierarchy.exclusivity;

import com.zylva.common.components.Guard;
import com.zylva.common.components.Line;
import com.zylva.common.userPreferences.LanguageManager;
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
