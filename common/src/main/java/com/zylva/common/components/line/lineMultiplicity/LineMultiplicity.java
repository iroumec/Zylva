package com.zylva.common.components.line.lineMultiplicity;

import com.zylva.common.components.line.lineShape.LineShape;

import java.awt.*;
import java.io.Serializable;

public interface LineMultiplicity extends Serializable {

    void draw(Graphics2D g2, LineShape lineShape, int x1, int y1, int x2, int y2);
}
