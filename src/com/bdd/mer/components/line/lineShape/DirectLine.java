package com.bdd.mer.components.line.lineShape;

import java.awt.*;

public class DirectLine implements LineShape {

    @Override
    public void draw(Graphics2D g2, int x1, int y1, int x2, int y2) {

        g2.drawLine(x1, y1, x2, y2);

    }

    @Override
    public Point getCenterPoint(int x1, int y1, int x2, int y2) {

        return new Point((x1 + x2) / 2, (y1 + y2) / 2);

    }

}
