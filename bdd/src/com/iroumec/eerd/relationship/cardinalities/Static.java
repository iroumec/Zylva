package com.iroumec.eerd.relationship.cardinalities;

import com.iroumec.eerd.relationship.Cardinality;

import javax.swing.*;

public final class Static implements CardinalityMenu {

    private final static Static INSTANCE = new Static();

    private Static() {}

    public static Static getInstance() { return INSTANCE; }

    @Override
    public JPopupMenu getPopupMenu(Cardinality cardinality) {

        return new JPopupMenu();
    }
}
