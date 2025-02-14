package com.iroumec.components.basicComponents.line.lineMultiplicity;

import com.iroumec.components.basicComponents.line.lineShape.LineShape;

import java.awt.*;

public class SingleLine implements LineMultiplicity {

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2, LineShape lineShape, int x1, int y1, int x2, int y2) {

        lineShape.draw(g2, x1, y1, x2, y2);
    }
}
