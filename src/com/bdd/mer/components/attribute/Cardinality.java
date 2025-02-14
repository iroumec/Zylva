package com.bdd.mer.components.attribute;

import java.awt.*;

public interface Cardinality {

    Cardinality getOpposite();

    void draw(Graphics2D g2, int x, int y);
}
