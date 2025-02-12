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

    private Component firstComponent;
    private Component secondComponent;

    /**
     * The interface {@code Stroke} is not used here because it's not serializable.
     */
    private final int strokeWidth;
    private final LineShape lineShape;
    private final LineMultiplicity lineMultiplicity;

    Line(Builder builder) {
        super(builder.diagram);
        this.firstComponent = builder.firstComponent;
        this.secondComponent = builder.secondComponent;
        this.lineShape = builder.lineShape;
        this.lineMultiplicity = builder.lineMultiplicity;
        this.strokeWidth = builder.strokeWidth;
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
        return new JPopupMenu();
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
        this.firstComponent = null;
        this.secondComponent = null;
    }

    @Override
    public boolean canBeDeleted() {
        return true;
    }

    @Override
    public void cleanReferencesTo(Component component) {
        if (component.equals(this.firstComponent) || component.equals(this.secondComponent)) {
            this.delete();
        }
    }

    @Override
    public Rectangle getBounds() {

        Point centerPoint = this.getCenterPoint();

        return new Rectangle(centerPoint.x, centerPoint.y, 0, 0);

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                     Builder                                                    */
    /* -------------------------------------------------------------------------------------------------------------- */

    public static class Builder {

        // Required parameters
        private final Diagram diagram;
        private final Component firstComponent;
        private final Component secondComponent;

        // Optional parameters - initialized to default values
        private LineShape lineShape = new DirectLine();
        private LineMultiplicity lineMultiplicity = new SingleLine();
        private int strokeWidth = 1;

        public Builder(Diagram diagram, Component firstComponent, Component secondComponent) {
            this.diagram = diagram;
            this.firstComponent = firstComponent;
            this.secondComponent = secondComponent;
        }

        public Builder lineShape(LineShape lineShape) {
            this.lineShape = lineShape;
            return this;
        }

        public Builder lineMultiplicity(LineMultiplicity lineMultiplicity) {
            this.lineMultiplicity = lineMultiplicity;
            return this;
        }

        public Builder strokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }
}
