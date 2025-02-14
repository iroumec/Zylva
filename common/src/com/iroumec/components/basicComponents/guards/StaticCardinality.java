package com.iroumec.components.basicComponents.guards;

import com.iroumec.components.basicComponents.Line;

import javax.swing.*;

/**
    This is a cardinality whose values cannot be changed.
 */

// TODO: change for composition.
public final class StaticCardinality extends Cardinality {

    public StaticCardinality(String firstValue, String secondValue, Line line) {
        super(firstValue, secondValue, line);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        return new JPopupMenu();
    }
}
