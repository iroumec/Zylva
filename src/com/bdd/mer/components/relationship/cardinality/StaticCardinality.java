package com.bdd.mer.components.relationship.cardinality;

import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

public class StaticCardinality extends Cardinality {

    public StaticCardinality(String firstValue, String secondValue) {
        super(firstValue, secondValue);
    }

    @Override
    protected PopupMenu getGenericPopupMenu() {

        DrawingPanel drawingPanel = this.getPanelDibujo();

        return new PopupMenu(drawingPanel);

    }

}
