package com.bdd.mer.components.line;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.line.lineMultiplicity.LineMultiplicity;
import com.bdd.mer.components.line.lineShape.LineShape;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.awt.*;

public class Line extends Component {

    private Stroke stroke;
    private Component firstComponent;
    private Component secondComponent;
    private final LineShape lineShape;
    private final LineMultiplicity lineMultiplicity;

    public Line(DrawingPanel drawingPanel, Component firstComponent, Component secondComponent, LineShape lineShape, LineMultiplicity lineMultiplicity) {
        super(drawingPanel);
        this.firstComponent = firstComponent;
        this.secondComponent = secondComponent;
        this.lineShape = lineShape;
        this.lineMultiplicity = lineMultiplicity;
        this.stroke = new BasicStroke(1);
        setDrawingPriority(2);
    }

    public Point getCenterPoint() {

        return this.lineShape.getCenterPoint(firstComponent.getX(), firstComponent.getY(), secondComponent.getX(), secondComponent.getY());

    }

    public void setStroke(Stroke stroke) { this.stroke = stroke; }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

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

        Stroke currentStroke = g2.getStroke();

        g2.setStroke(this.stroke);
        this.lineMultiplicity.draw(g2, lineShape, x1, y1, x2, y2);
        g2.setStroke(currentStroke);

    }

    @Override
    public void cleanPresence() {

    }

    @Override
    public void changeReference(Component oldComponent, Component newComponent) {

        if (firstComponent.equals(oldComponent)) {
            firstComponent = newComponent;
        }

        if (secondComponent.equals(oldComponent)) {
            secondComponent = newComponent;
        }

    }

    @Override
    public Rectangle getBounds() {

        Point centerPoint = this.getCenterPoint();

        return new Rectangle(centerPoint.x, centerPoint.y, 0, 0);

    }

}
