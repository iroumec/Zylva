package com.bdd.mer.components.line.lineMultiplicity;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.line.lineShape.LineShape;

import java.awt.*;

public class SingleLine implements LineMultiplicity {

    @Override
    public void draw(Graphics2D g2, LineShape lineShape, int x1, int y1, int x2, int y2) {

        lineShape.draw(g2, x1, y1, x2, y2);

    }
}
