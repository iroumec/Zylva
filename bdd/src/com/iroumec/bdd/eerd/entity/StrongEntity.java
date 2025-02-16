package com.iroumec.bdd.eerd.entity;

import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.eerd.relationship.Relationship;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public String getIdentifier() {
        return this.entityWrapper.getIdentifier();
    }

    @Override
    public List<Derivation> getDerivations() {
        return new ArrayList<>();
    }
}
