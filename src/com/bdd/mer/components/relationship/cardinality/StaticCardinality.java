package com.bdd.mer.components.relationship.cardinality;

import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;

public class StaticCardinality extends Cardinality {

    public StaticCardinality(String firstValue, String secondValue, DrawingPanel drawingPanel) {
        super(firstValue, secondValue, drawingPanel);
    }

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(this);

    }

}
