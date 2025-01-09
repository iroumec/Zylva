package com.bdd.mer.components.entity;

import com.bdd.mer.components.relationship.Relationship;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class WeakEntity extends Entity {

    private final Relationship relationship;

    public WeakEntity(String text, int x, int y, Relationship relationship) {

        super(text, x, y);

        this.relationship = relationship;
    }

    @Override
    public void draw(Graphics2D g2) {

        super.draw(g2);

        Rectangle bounds = this.getBounds();

        RoundRectangle2D externalRectBounds = new RoundRectangle2D.Float(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6, 10, 10); // The number of the left must be half the number on the right

        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        g2.draw(externalRectBounds);
        this.setShape(externalRectBounds);
    }

    public Entity getStrongVersion() {

        Entity strongVersion = new Entity(this.getText(), this.getX(), this.getY());

        this.copyAttributes(strongVersion);

        return strongVersion;

    }

    @Override
    public void removeRelationship(Relationship relationship) {

        if (this.relationship.equals(relationship)) {
            this.getPanelDibujo().replaceComponent(this, this.getStrongVersion());
        }

    }

}
