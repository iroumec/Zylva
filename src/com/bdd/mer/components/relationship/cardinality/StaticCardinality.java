package com.bdd.mer.components.relationship.cardinality;

import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;

/**
    This is a cardinality whose values cannot be changed.
 */

// I think it's not correct to it being a subclass... It's just a cardinality with a different JPopupMenu.
public class StaticCardinality extends Cardinality {

    public StaticCardinality(String firstValue, String secondValue, DrawingPanel drawingPanel) {
        super(firstValue, secondValue, drawingPanel);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(this);
    }
}
