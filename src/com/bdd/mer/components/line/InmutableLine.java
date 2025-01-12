package com.bdd.mer.components.line;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.line.lineShape.LineShape;
import com.bdd.mer.frame.DrawingPanel;

import java.awt.*;

public class InmutableLine extends Line {

    public InmutableLine(DrawingPanel drawingPanel, Component firstComponent, Component secondComponent, LineShape lineShape) {
        super(drawingPanel, firstComponent, secondComponent, lineShape);
    }


}
