package com.bdd.mer.components.attribute.cardinality;

import java.awt.*;

public class Univalued implements Cardinality {

    private final static Univalued instance = new Univalued();

    private Univalued() {}

    public static Univalued getInstance() {
        return instance;
    }

    @Override
    public Cardinality getOpposite() {
        return Multivalued.getInstance();
    }

    @Override
    public void draw(Graphics2D g2, int x, int y) {
        // Empty on purpose.
    }
}
