package com.zylva.bdd.eerd.relationship.cardinalities;

import com.zylva.bdd.eerd.relationship.Cardinality;

import javax.swing.*;
import java.io.Serializable;

public interface CardinalityMenu extends Serializable {

    JPopupMenu getPopupMenu(Cardinality cardinality);
}
