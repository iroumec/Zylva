package com.iroumec.bdd.eerd.entity;

import com.iroumec.bdd.derivation.elements.ElementDecorator;
import com.iroumec.bdd.derivation.elements.containers.Replacer;
import com.iroumec.core.Component;
import com.iroumec.bdd.derivation.Derivable;
import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.SingleElement;
import com.iroumec.bdd.eerd.relationship.Relationship;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Weak entity. This is an entity whose identification or existence depends on another one.
 */
final class WeakEntity implements Entity {

    private final EntityWrapper entityWrapper;

    /**
     * Relationship where the entity is weak.
     */
    private final Relationship relationship;

    /**
     * Constructs a {@code WeakEntity}.
     *
     * @param entityWrapper Wrapper of the entity.
     * @param relationship {@code Relationship} where the entity is weak.
     */
    WeakEntity(EntityWrapper entityWrapper, Relationship relationship) {
        this.entityWrapper = entityWrapper;
        this.relationship = relationship;
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void fillShape(Graphics2D graphics2D, RoundRectangle2D rectangle2D) {

        Rectangle bounds = rectangle2D.getBounds();
        RoundRectangle2D shape = new RoundRectangle2D.Float(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6, 10, 10); // The number of the left must be half the number on the right // The number of the left must be half the number on the right

        graphics2D.setColor(Color.WHITE);
        graphics2D.fill(shape);

        this.entityWrapper.setShape(shape);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public String getIdentifier() { return this.entityWrapper.getIdentifier(); }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanReferencesTo(Component component) {

        if (this.relationship.equals(component)) {
            this.entityWrapper.setStrongVersion();
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Derivation> getDerivations() {

        // I assume the program will work perfectly and, so, the weak entity will always have an opposite, being
        // this a strong entity or an association.
        Derivable opposite = this.relationship.getOppositeMember(this.entityWrapper);

        Derivation derivation = new Derivation(this.getIdentifier());

        derivation.addIdentificationElement(new SingleElement(opposite.getIdentifier(),
                new Replacer(ElementDecorator.FOREIGN)));

        return List.of(derivation);
    }
}
