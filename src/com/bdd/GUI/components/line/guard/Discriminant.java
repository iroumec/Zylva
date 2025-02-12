package com.bdd.GUI.components.line.guard;

import com.bdd.GUI.Diagram;
import com.bdd.GUI.components.line.Line;

import javax.swing.*;

public class Discriminant extends Guard {

    public Discriminant(String text, Line line, Diagram diagram) {
        super(text, line, diagram);
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
