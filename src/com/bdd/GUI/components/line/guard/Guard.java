package com.bdd.GUI.components.line.guard;

import com.bdd.GUI.Component;
import com.bdd.GUI.Diagram;
import com.bdd.GUI.components.line.Line;

import java.awt.*;

public abstract class Guard extends Component {

    private final Line line;

    public Guard(String text, Line line, Diagram diagram) {
        super(text, diagram);
        this.line = line;
        line.setText(this.getText());

        // The guard will always be drawn above its line.
        setDrawingPriority(line.getDrawingPriority() + 1);
    }

    @Override
    public void draw(Graphics2D g2) {

        Point point = this.line.getCenterPoint();

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
    public boolean canBeDeleted() { return true; }

    @Override
    public void cleanReferencesTo(Component component) {
        /*
        Method lef empty in purpose.

        There is no important reference in the class that would not produce its elimination in the
        notifyRemovingOf() method.
         */
    }

    @Override
    public void notifyRemovingOf(Component component) {

        if (component.equals(line)) {
            this.setForDelete();
        }
    }
}
