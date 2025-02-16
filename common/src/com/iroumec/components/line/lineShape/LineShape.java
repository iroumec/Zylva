package com.iroumec.components.line.lineShape;

import java.awt.*;
import java.io.Serializable;

public interface LineShape extends Serializable {

    void draw(Graphics2D g2, int x1, int y1, int x2, int y2);
    Point getCenterPoint(int x1, int y1, int x2, int y2);
}
