package com.bdd.GUI.components.line;

import com.bdd.GUI.components.Component;
import com.bdd.GUI.components.line.lineMultiplicity.LineMultiplicity;
import com.bdd.GUI.components.line.lineMultiplicity.SingleLine;
import com.bdd.GUI.components.line.lineShape.DirectLine;
import com.bdd.GUI.components.line.lineShape.LineShape;
import com.bdd.GUI.Diagram;

import javax.swing.*;
import java.awt.*;

public class Line extends Component {

    private final Component firstComponent;
    private final Component secondComponent;

    /**
     * The interface {@code Stroke} is not used here because it's not serializable.
     */
    private final int strokeWidth;
    private final LineShape lineShape;
    private final LineMultiplicity lineMultiplicity;

    Line(Init<?> init) {
        super(init.diagram);
        this.firstComponent = init.firstComponent;
        this.secondComponent = init.secondComponent;
        this.lineShape = init.lineShape;
        this.lineMultiplicity = init.lineMultiplicity;
        this.strokeWidth = init.strokeWidth;
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

        g2.setStroke(new BasicStroke(this.strokeWidth));
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
        private final Diagram diagram;
        private final Component firstComponent;
        private final Component secondComponent;

        // Optional parameters - initialized to default values
        private LineShape lineShape = new DirectLine();
        private LineMultiplicity lineMultiplicity = new SingleLine();
        private int strokeWidth = 1;

        protected abstract T self();

        public Init(Diagram diagram, Component firstComponent, Component secondComponent) {
            this.diagram = diagram;
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

        public T strokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
            return self();
        }

        public Line build() {
            return new Line(this);
        }
    }

    public static class Builder extends Init<Builder> {

        public Builder(Diagram diagram, Component firstComponent, Component secondComponent) {
            super(diagram, firstComponent, secondComponent);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
