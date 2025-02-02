package com.bdd.mer.components.line.lineMultiplicity;

import com.bdd.mer.components.line.lineShape.LineShape;

import java.awt.*;

public class DoubleLine implements LineMultiplicity {

    // Separation between the lines.
    private final int separation;

    public DoubleLine(int separation) {
         this.separation = separation;
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2, LineShape lineShape, int x1, int x2, int y1, int y2) {

        lineShape.draw(g2, x1 + separation, x2 + separation, y1 + separation, y2 + separation);
        lineShape.draw(g2, x1 - separation, x2 - separation, y1 - separation, y2 - separation);
    }
}
