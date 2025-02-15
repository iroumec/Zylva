package com.iroumec.eerd.relationship.cardinalities;

import com.iroumec.eerd.relationship.Cardinality;

import javax.swing.*;

public interface CardinalityMenu {

    JPopupMenu getPopupMenu(Cardinality cardinality);
}
