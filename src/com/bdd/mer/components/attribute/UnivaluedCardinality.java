package com.bdd.mer.components.attribute;

import java.awt.*;

public class UnivaluedCardinality implements Cardinality {

    private final static UnivaluedCardinality instance = new UnivaluedCardinality();

    private UnivaluedCardinality() {}

    public static UnivaluedCardinality getInstance() {
        return instance;
    }

    @Override
    public Cardinality getOpposite() {
        return MultivaluedCardinality.getInstance();
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        // Empty on purpose.
    }
}
