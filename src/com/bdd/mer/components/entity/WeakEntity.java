package com.bdd.mer.components.entity;

import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.frame.DrawingPanel;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * This is an entity whose identification or existence depends on another one.
 */
public class WeakEntity extends Entity {

    /**
     * Relationship where the entity is weak.
     */
    private final Relationship relationship;

    /**
     * Constructs a {@code WeakEntity}.
     *
     * @param text {@code WeakEntity}'s name.
     * @param x {@code WeakEntity}'s X coordinate value in the {@code DrawingPanel}.
     * @param y {@code WeakEntity}'s Y coordinate value in the {@code DrawingPanel}.
     * @param relationship {@code Relationship} where the entity is weak.
     * @param drawingPanel {@code DrawingPanel} where the {@code WeakEntity} lives.
     */
    public WeakEntity(String text, int x, int y, Relationship relationship, DrawingPanel drawingPanel) {

        super(text, x, y, drawingPanel);

        this.relationship = relationship;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * @return A strong version of the current weak entity.
     */
    public Entity getStrongVersion() {

        Entity strongVersion = new Entity(this.getText(), this.getX(), this.getY(), this.getPanelDibujo());

        this.copyAttributes(strongVersion);

        return strongVersion;

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        super.draw(g2);

        g2.draw(this.getShape());
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected void fillShape(Graphics2D graphics2D, RoundRectangle2D rectangle2D) {

        Rectangle bounds = rectangle2D.getBounds();
        RoundRectangle2D shape = new RoundRectangle2D.Float(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6, 10, 10); // The number of the left must be half the number on the right // The number of the left must be half the number on the right

        graphics2D.setColor(Color.WHITE);
        graphics2D.fill(shape);

        this.setShape(shape);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void removeRelationship(Relationship relationship) {

        if (this.relationship.equals(relationship)) {
            this.getPanelDibujo().replaceComponent(this, this.getStrongVersion());
        } else {
            super.removeRelationship(relationship);
        }

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * This overridden is important to draw it correctly.
     */
    @Override
    public void addRelationship(Relationship relationship) {

        if (!this.relationship.equals(relationship)) {
            super.addRelationship(relationship);
        }

    }

}
