package com.iroumec.bdd.eerd.entity;

import com.iroumec.core.Deletable;
import com.iroumec.bdd.derivation.Derivable;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;

interface Entity extends Serializable, Derivable, Deletable {

    /**
     * Fills the {@code Entity}'s shape.
     *
     * @param graphics2D Graphics context.
     * @param shape Shape of the {@code Entity}.
     */
    void fillShape(Graphics2D graphics2D, RoundRectangle2D shape);
}
