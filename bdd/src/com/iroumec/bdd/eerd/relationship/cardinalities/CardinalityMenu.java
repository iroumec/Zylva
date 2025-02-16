package com.iroumec.bdd.eerd.relationship.cardinalities;

import com.iroumec.bdd.eerd.relationship.Cardinality;

import javax.swing.*;

public interface CardinalityMenu {

    JPopupMenu getPopupMenu(Cardinality cardinality);
}
