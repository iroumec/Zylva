package com.bdd.mer.components.entity;

import com.bdd.mer.components.relationship.Relationship;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

interface Entity {

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
