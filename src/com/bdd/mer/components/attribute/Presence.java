package com.bdd.mer.components.attribute;

import java.awt.*;

public interface Presence {

    Presence getOpposite();

    void draw(Graphics2D g2, int x1, int y1, int x2, int y2);
}
