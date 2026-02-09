package com.zylva.bdd.eerd.relationship.cardinalities;

import com.zylva.bdd.eerd.relationship.Cardinality;
import com.zylva.common.userPreferences.LanguageManager;

import javax.swing.*;

public final class Dynamic implements CardinalityMenu {

    private final static Dynamic INSTANCE = new Dynamic();

    private Dynamic() {}

    public static Dynamic getInstance() { return INSTANCE; }

    @Override
    public JPopupMenu getPopupMenu(Cardinality cardinality) {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem item = new JMenuItem(LanguageManager.getMessage("action.changeValues"));
        item.addActionListener(_ -> cardinality.changeCardinality());
        popupMenu.add(item);

        return popupMenu;
    }
}
