package com.iroumec.components.line.guard;

import com.iroumec.Diagram;
import com.iroumec.components.line.Line;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Discriminant extends Guard {

    public Discriminant(@NotNull String text, Line line, Diagram diagram) {
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
