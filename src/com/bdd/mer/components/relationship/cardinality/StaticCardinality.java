package com.bdd.mer.components.relationship.cardinality;

import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

public class StaticCardinality extends Cardinality {

    public StaticCardinality(String firstValue, String secondValue, DrawingPanel drawingPanel) {
        super(firstValue, secondValue, drawingPanel);
    }

    @Override
    protected PopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(this);

    }

}
