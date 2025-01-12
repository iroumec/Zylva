package com.bdd.mer.components.line;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.line.lineMultiplicity.LineMultiplicity;
import com.bdd.mer.components.line.lineMultiplicity.SingleLine;
import com.bdd.mer.components.line.lineShape.DirectLine;
import com.bdd.mer.components.line.lineShape.LineShape;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.awt.*;

public class Line extends Component {

    private final Component firstComponent;
    private final Component secondComponent;
    private LineShape lineShape;
    private final LineMultiplicity lineMultiplicity;

    public Line(DrawingPanel drawingPanel, Component firstComponent, Component secondComponent) {
        this(drawingPanel, firstComponent, secondComponent, new DirectLine());
    }

    public Line(DrawingPanel drawingPanel, Component firstComponent, Component secondComponent, LineShape lineShape) {
        this(drawingPanel, firstComponent, secondComponent, lineShape, new SingleLine());
    }

    public Line(DrawingPanel drawingPanel, Component firstComponent, Component secondComponent, LineShape lineShape, LineMultiplicity lineMultiplicity) {
        super(drawingPanel);
        this.firstComponent = firstComponent;
        this.secondComponent = secondComponent;
        this.lineShape = lineShape;
        this.lineMultiplicity = lineMultiplicity;
    }

    public LineShape getLineShape() { return this.lineShape; }

    public void setLineShape(LineShape lineShape) { this.lineShape = lineShape; }

    @Override
    protected JPopupMenu getPopupMenu() {
        return null;
    }

    @Override
    public void draw(Graphics2D g2) {

        int x1 = firstComponent.getX();
        int y1 = firstComponent.getY();
        int x2 = secondComponent.getX();
        int y2 = secondComponent.getY();

        this.lineMultiplicity.draw(g2, lineShape, x1, y1, x2, y2);

    }

    @Override
    public void cleanPresence() {

    }

    @Override
    public void changeReference(Component oldComponent, Component newComponent) {

    }
}
