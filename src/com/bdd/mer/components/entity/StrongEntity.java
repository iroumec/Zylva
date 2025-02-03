package com.bdd.mer.components.entity;

import com.bdd.mer.components.relationship.Relationship;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Strong entity.
 */
record StrongEntity(EntityWrapper entityWrapper) implements Entity {

    /**
     * Constructs an {@code Entity}.
     *
     * @param entityWrapper Wrapper of the entity.
     */
    StrongEntity {
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void fillShape(Graphics2D graphics2D, RoundRectangle2D shape) {

        graphics2D.setColor(Color.WHITE);
        graphics2D.fill(shape);

        this.entityWrapper.setShape(shape);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean relationshipCanBeManipulated(Relationship relationship) {
        return true;
    }

}
