package com.bdd.mer.components.entity;

import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.frame.DrawingPanel;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class WeakEntity extends Entity {

    private final Relationship relationship; // Relationship where the entity is weak.

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                         Initializing Related Methods                                           */
    /* -------------------------------------------------------------------------------------------------------------- */

    public WeakEntity(String text, int x, int y, Relationship relationship, DrawingPanel drawingPanel) {

        super(text, x, y, drawingPanel);

        this.relationship = relationship;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public Entity getStrongVersion() {

        Entity strongVersion = new Entity(this.getText(), this.getX(), this.getY(), this.getPanelDibujo());

        this.copyAttributes(strongVersion);

        return strongVersion;

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                            Drawing Related Methods                                             */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        g2.drawLine(this.getX() - 3, this.getY() - 3, this.relationship.getX() - 3, this.relationship.getY() - 3);
        g2.drawLine(this.getX() + 3, this.getY() + 3, this.relationship.getX() + 3, this.relationship.getY() + 3);

        super.draw(g2);

        g2.draw(this.getShape());
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void fillShape(Graphics2D graphics2D, RoundRectangle2D rectangle2D) {

        Rectangle bounds = rectangle2D.getBounds();
        RoundRectangle2D shape = new RoundRectangle2D.Float(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6, 10, 10); // The number of the left must be half the number on the right // The number of the left must be half the number on the right

        graphics2D.setColor(Color.WHITE);
        graphics2D.fill(shape);

        this.setShape(shape);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                        Relationships Related Methods                                           */
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

    // This overdrive is important to draw it correctly.
    @Override
    public void addRelationship(Relationship relationship) {

        if (!this.relationship.equals(relationship)) {
            super.addRelationship(relationship);
        }

    }

}
