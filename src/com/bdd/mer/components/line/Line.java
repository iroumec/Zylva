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
    private final Stroke stroke;
    private final LineShape lineShape;
    private final LineMultiplicity lineMultiplicity;

    Line(Init<?> init) {
        super(init.drawingPanel);
        this.firstComponent = init.firstComponent;
        this.secondComponent = init.secondComponent;
        this.lineShape = init.lineShape;
        this.lineMultiplicity = init.lineMultiplicity;
        this.stroke = init.stroke;
        setDrawingPriority(2);
    }

    public Point getCenterPoint() {

        return this.lineShape.getCenterPoint(firstComponent.getX(), firstComponent.getY(), secondComponent.getX(), secondComponent.getY());

    }

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
    public Rectangle getBounds() {

        Point centerPoint = this.getCenterPoint();

        return new Rectangle(centerPoint.x, centerPoint.y, 0, 0);

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                     Builder                                                    */
    /* -------------------------------------------------------------------------------------------------------------- */

    // This is created due to hierarchy related problems. It wasn't necessary in case the Line was final.
    protected static abstract class Init<T extends Init<T>> {
        // Required parameters
        private final DrawingPanel drawingPanel;
        private final Component firstComponent;
        private final Component secondComponent;

        // Optional parameters - initialized to default values
        private LineShape lineShape = new DirectLine();
        private LineMultiplicity lineMultiplicity = new SingleLine();
        private Stroke stroke = new BasicStroke(1);

        protected abstract T self();

        public Init(DrawingPanel drawingPanel, Component firstComponent, Component secondComponent) {
            this.drawingPanel = drawingPanel;
            this.firstComponent = firstComponent;
            this.secondComponent = secondComponent;
        }

        public T lineShape(LineShape lineShape) {
            this.lineShape = lineShape;
            return self();
        }

        public T lineMultiplicity(LineMultiplicity lineMultiplicity) {
            this.lineMultiplicity = lineMultiplicity;
            return self();
        }

        public T stroke(Stroke stroke) {
            this.stroke = stroke;
            return self();
        }

        public Line build() {
            return new Line(this);
        }
    }

    public static class Builder extends Init<Builder> {

        public Builder(DrawingPanel drawingPanel, Component firstComponent, Component secondComponent) {
            super(drawingPanel, firstComponent, secondComponent);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
