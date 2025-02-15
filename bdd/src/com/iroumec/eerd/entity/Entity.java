package com.iroumec.eerd.entity;

import com.iroumec.derivation.Derivable;
import com.iroumec.eerd.relationship.Relationship;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;

interface Entity extends Serializable, Derivable {

    /**
     * Fills the {@code Entity}'s shape.
     *
     * @param graphics2D Graphics context.
     * @param shape Shape of the {@code Entity}.
     */
    void fillShape(Graphics2D graphics2D, RoundRectangle2D shape);

    /**
     * Determines if a relationship can be added or not.
     *
     * @param relationship Relationship to be added,
     */
    boolean relationshipCanBeManipulated(Relationship relationship);
}
