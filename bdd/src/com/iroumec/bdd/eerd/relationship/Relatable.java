package com.iroumec.bdd.eerd.relationship;

import com.iroumec.bdd.derivation.Derivable;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

public interface Relatable extends Serializable, Derivable {

    List<Rectangle> getAssociationBounds();
}
