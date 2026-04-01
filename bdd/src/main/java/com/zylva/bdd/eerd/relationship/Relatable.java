package com.zylva.bdd.eerd.relationship;

import com.zylva.bdd.derivation.Derivable;

import java.util.List;
import java.awt.Rectangle;

public interface Relatable extends Derivable {

    List<Rectangle> getAssociationBounds();
}
