package com.bdd.mer.components.entity;

import com.bdd.mer.components.relationship.Relationship;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Strong entity.
 */
class StrongEntity implements Entity {

    final EntityWrapper entityWrapper;

    /**
     * Constructs an {@code Entity}.
     *
     * @param entityWrapper Wrapper of the entity.
     */
    StrongEntity(EntityWrapper entityWrapper) {
        this.entityWrapper = entityWrapper;
    }

    /**
     * Fills the {@code Entity}'s shape.
     *
     * @param graphics2D Graphics context.
     * @param shape Shape of the {@code Entity}.
     */
    @Override
    public void fillShape(Graphics2D graphics2D, RoundRectangle2D shape) {

        graphics2D.setColor(Color.WHITE);
        graphics2D.fill(shape);

        this.entityWrapper.setShape(shape);
    }

    /**
     * Determines if a relationship can be added or not.
     *
     * @param relationship Relationship to be added,
     */
    @Override
    public boolean relationshipCanBeManipulated(Relationship relationship) {
        return true;
    }

}
