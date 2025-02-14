package com.bdd.mer.components.attribute;

import java.awt.*;

public class MultivaluedCardinality implements Cardinality {

    private final int arrowWidth;
    private final int arrowHeight;
    private final static MultivaluedCardinality defaultInstance = new MultivaluedCardinality(3, 3);

    private MultivaluedCardinality(int arrowWidth, int arrowHeight) {

        this.arrowWidth = arrowWidth;
        this.arrowHeight = arrowHeight;
    }

    public static MultivaluedCardinality getInstance() {

        return defaultInstance;
    }

    @SuppressWarnings("unused")
    public MultivaluedCardinality getInstance(int arrowWidth, int arrowHeight) {

        return new MultivaluedCardinality(arrowWidth, arrowHeight);
    }

    @Override
    public Cardinality getOpposite() {
        return UnivaluedCardinality.getInstance();
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {

        g2.drawLine(x - this.arrowWidth, y + this.arrowHeight, x, y);
        g2.drawLine(x - this.arrowWidth, y - this.arrowHeight, x, y);
    }
}
