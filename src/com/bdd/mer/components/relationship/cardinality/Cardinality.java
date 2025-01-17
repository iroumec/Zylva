package com.bdd.mer.components.relationship.cardinality;

import com.bdd.mer.actions.Action;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.line.GuardedLine;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Cardinality extends Component {

    private GuardedLine guardedLine;

    public Cardinality(String firstValue, String secondValue, DrawingPanel drawingPanel) {
        super(drawingPanel);
        this.setText(giveFormat(firstValue, secondValue));
    }

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(
                this,
                Action.CHANGE_CARDINALITY
        );

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
    public List<Component> getComponentsForRemoval() {
        return new ArrayList<>();
    }

    @Override
    public void cleanPresence() {}

    @Override
    public void changeReference(Component oldComponent, Component newComponent) {

        if (newComponent instanceof GuardedLine) {

            if (this.guardedLine.equals(oldComponent)) {
                this.guardedLine = (GuardedLine) newComponent;
            }
        }

        this.guardedLine.changeReference(oldComponent, newComponent);

    }

    public void setLine(GuardedLine guardedLine) {
        this.guardedLine = guardedLine;
    }

    public static String giveFormat(String firstValue, String secondValue) {
        return "(" + firstValue + ", " + secondValue + ")";
    }
}
