package com.bdd.mer.components.relationship;

import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

public class StaticCardinality extends Cardinality{

    public StaticCardinality(Entity owner, String firstValue, String secondValue) {
        super(firstValue, secondValue);
    }

    @Override
    protected PopupMenu getGenericPopupMenu() {

        DrawingPanel drawingPanel = this.getPanelDibujo();

        return new PopupMenu(drawingPanel);

    }

}
