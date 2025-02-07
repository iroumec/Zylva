package com.bdd.mer.components.line.guard;

import com.bdd.mer.actions.Action;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;

public class Discriminant extends Guard {

    public Discriminant(String text, DrawingPanel drawingPanel) {
        super(text, drawingPanel);
    }

    @Override
    protected JPopupMenu getPopupMenu() {
        return this.getActionManager().getPopupMenu(
                this,
                Action.CHANGE_TEXT
        );
    }
}
