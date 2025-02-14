package com.iroumec.components.basicComponents.line.guard;

import com.iroumec.components.Diagram;
import com.iroumec.components.basicComponents.Line;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Discriminant extends Guard {

    public Discriminant(@NotNull String text, Line line) {
        super(text, line);
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
