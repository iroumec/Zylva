package com.bdd.mer.components.line.guard;

import com.bdd.mer.actions.Action;
import com.bdd.GUI.Diagram;

import javax.swing.*;

public class Discriminant extends Guard {

    public Discriminant(String text, Diagram diagram) {
        super(text, diagram);
    }

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem actionItem = new JMenuItem("action.changeText");
        actionItem.addActionListener(_ -> this.rename());
        popupMenu.add(actionItem);

        return popupMenu;
    }
}
