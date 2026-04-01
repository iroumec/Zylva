package com.zylva.bdd.eerd.entity;

import com.zylva.common.core.Deletable;
import com.zylva.bdd.derivation.Derivable;

import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

interface Entity extends Derivable, Deletable {

    /**
     * Fills the {@code Entity}'s shape.
     *
     * @param graphics2D Graphics context.
     * @param shape      Shape of the {@code Entity}.
     */
    void fillShape(Graphics2D graphics2D, RoundRectangle2D shape);
}
