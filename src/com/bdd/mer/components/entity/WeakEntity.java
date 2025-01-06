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

        this.setShape(new Rectangle(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6));
    }

    public Entity getStrongVersion() {

        Entity strongVersion = new Entity(this.getText(), this.getX(), this.getY());

        this.copyAttributes(strongVersion);

        return strongVersion;

    }

    public void drawClosestPointLine(Graphics2D g2, int x, int y) {

        Point closestPoint = getClosestPoint(new Point(x, y));

        // Dibujar la línea hacia el punto más cercano
//        g2.drawLine(x + 3, y + 3, closestPoint.x + 3, closestPoint.y + 3);
//        g2.drawLine(x - 3, y - 3, closestPoint.x - 3, closestPoint.y - 3);
        g2.drawLine(x - 3, y - 3, this.getX() - 3, this.getY() - 3);
        g2.drawLine(x + 3, y + 3, this.getX() + 3, this.getY() + 3);

    }

}
