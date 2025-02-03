package com.bdd.mer.components.entity;

import com.bdd.mer.components.relationship.Relationship;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Strong entity.
 */
class Entity {

    final EntityWrapper entityWrapper;

    /**
     * Constructs an {@code Entity}.
     *
     * @param entityWrapper Wrapper of the entity.
     */
    Entity(EntityWrapper entityWrapper) {
        this.entityWrapper = entityWrapper;
    }

    /**
     * Fills the {@code Entity}'s shape.
     *
     * @param graphics2D Graphics context.
     * @param shape Shape of the {@code Entity}.
     */
    void fillShape(Graphics2D graphics2D, RoundRectangle2D shape) {

        graphics2D.setColor(Color.WHITE);
        graphics2D.fill(shape);

        this.entityWrapper.setShape(shape);
    }

    /**
     * Determines if a relationship can be added or not.
     *
     * @param relationship Relationship to be added,
     */
    boolean relationshipCanBeManipulated(Relationship relationship) {
        return true;
    }

}
