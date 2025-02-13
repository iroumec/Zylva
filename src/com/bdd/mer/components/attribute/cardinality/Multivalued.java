package com.bdd.mer.components.attribute.cardinality;

import java.awt.*;

public class Multivalued implements Cardinality {

    private final int arrowWidth;
    private final int arrowHeight;
    private final static Multivalued defaultInstance = new Multivalued(3, 3);

    private Multivalued(int arrowWidth, int arrowHeight) {

        this.arrowWidth = arrowWidth;
        this.arrowHeight = arrowHeight;
    }

    public static Multivalued getInstance() {

        return defaultInstance;
    }

    @SuppressWarnings("unused")
    public Multivalued getInstance(int arrowWidth, int arrowHeight) {

        return new Multivalued(arrowWidth, arrowHeight);
    }

    @Override
    public Cardinality getOpposite() {
        return Univalued.getInstance();
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {

        g2.drawLine(x - this.arrowWidth, y + this.arrowHeight, x, y);
        g2.drawLine(x - this.arrowWidth, y - this.arrowHeight, x, y);
    }
}
