package com.bdd.mer.components.entity;

import java.awt.*;

public class WeakEntity extends Entity {

    public WeakEntity(String text, int x, int y) {

        super(text, x, y);
    }

    @Override
    public void draw(Graphics2D g2) {

        super.draw(g2);

        Rectangle bounds = this.getBounds();

        // Mejorarlo usando el método draw, al que se le pasa un Shape
        g2.drawRect(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6); // The number of the left must be half the number on the right

        this.setBounds(new Rectangle(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6));
    }

}
