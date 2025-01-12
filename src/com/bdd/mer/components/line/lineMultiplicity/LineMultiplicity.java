package com.bdd.mer.components.line.lineMultiplicity;

import com.bdd.mer.components.line.lineShape.LineShape;

import java.awt.*;

public interface LineMultiplicity {

    void draw(Graphics2D g2, LineShape lineShape, int x1, int y1, int x2, int y2);
}
