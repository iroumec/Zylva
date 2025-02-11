package com.bdd.mer.components.line.guard.cardinality;

import com.bdd.mer.actions.Action;
import com.bdd.mer.components.line.guard.Guard;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.structures.Pair;

import javax.swing.*;

public class Cardinality extends Guard {

    public Cardinality(String firstValue, String secondValue, DrawingPanel drawingPanel) {
        super(giveFormat(firstValue, secondValue), drawingPanel);
    }

    public static String giveFormat(String firstValue, String secondValue) {
        return "(" + firstValue + ", " + secondValue + ")";
    }

    public static Pair<String, String> removeFormat(String text) {

        String[] cardinalities = text.replaceAll("[()]", "").split(", ");

        return new Pair<>(cardinalities[0], cardinalities[1]);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(
                this,
                Action.CHANGE_CARDINALITY
        );

    }
}
