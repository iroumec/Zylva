package com.bdd.GUI.components.line.guard;

import com.bdd.GUI.components.Component;
import com.bdd.GUI.components.line.GuardedLine;
import com.bdd.GUI.Diagram;

import java.awt.*;

public abstract class Guard extends Component {

    private GuardedLine guardedLine;

    public Guard(String text, Diagram diagram) {
        super(text, diagram);

        setDrawingPriority(3);
    }

    public void setLine(GuardedLine guardedLine) {
        this.guardedLine = guardedLine;
    }

    @Override
    public void draw(Graphics2D g2) {

        Point point = this.guardedLine.getCenterPoint();

        int x = point.x;
        int y = point.y;

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(this.getText());
        int textHeight = fm.getHeight();

        // The coordinates are adjusted so that the text is enclosed correctly.
        int rectX = x - textWidth / 2; // The text is horizontally centred.
        int rectY = y - textHeight / 2;  // The text is vertically centred.

        // The rectangle enclosing the text is created and rendered.
        Rectangle shape = new Rectangle(rectX, rectY, textWidth, textHeight);
        g2.setColor(Color.WHITE);
        g2.fill(shape);
        this.setShape(shape);

        // The text is drawn.
        g2.setColor(Color.BLACK);
        g2.drawString(this.getText(), x - textWidth / 2, y + textHeight / 4);

        // Debugging: the shape is drawn.
        //g2.draw(shape);
    }

    @Override
    public String toString() {
        return this.getText();
    }

    @Override
    public void cleanPresence() {
        // Do nothing.
    }
}
