package com.zylva.bdd.eerd.relationship;

import com.zylva.bdd.derivation.Derivable;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

public interface Relatable extends Serializable, Derivable {

    List<Rectangle> getAssociationBounds();
}
