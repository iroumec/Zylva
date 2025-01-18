package com.bdd.mer.components.line.lineShape;

import java.awt.*;

// We moved to the second component Y axis value, and then we move in the x axis.
public class SquaredLine implements LineShape {

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2, int x1, int y1, int x2, int y2) {

        g2.drawLine(x1, y1, x1, y2);
        g2.drawLine(x1, y2, x2, y2);

    }

    @Override
    public Point getCenterPoint(int x1, int y1, int x2, int y2) {
        return new Point(x1, y2); // Due to how we move.
    }
}
