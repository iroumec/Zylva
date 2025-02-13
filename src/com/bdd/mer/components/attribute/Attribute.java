package com.bdd.mer.components.attribute;

import com.bdd.GUI.Component;
import com.bdd.mer.components.AttributableEERComponent;
import com.bdd.mer.components.attribute.symbology.AttributeArrow;
import com.bdd.mer.components.attribute.symbology.AttributeEnding;
import com.bdd.mer.components.attribute.symbology.AttributeSymbol;
import com.bdd.mer.derivation.Derivable;
import com.bdd.GUI.Diagram;

import javax.swing.*;
import java.awt.*;

public class Attribute extends AttributableEERComponent implements Derivable {

    /**
     * {@code Attribute}'s symbol.
     */
    private final AttributeSymbol symbol;

    /**
     * {@code Attribute}'s arrow.
     */
    private AttributeArrow arrow;

    /**
     * {@code Attribute}'s ending.
     */
    private AttributeEnding ending;

    /**
     * {@code Attribute}'s owner.
     */
    private final AttributableEERComponent owner;

    private final static int lineLength = 25;
    private final static int circleRadius = 5;
    // Necessary to draw everything correctly.
    private final static int minorCorrection = 3;

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                         Initializing Related Methods                                           */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Constructs an {@code Attribute}.
     *
     * @param owner {@code Attribute}'s owner.
     * @param text {@code Attribute}'s text.
     * @param symbol {@code Attribute}'s symbol.
     * @param arrow {@code Attribute}'s arrow.
     * @param ending {@code Attribute}'s ending.
     * @param diagram {@code Diagram} where the {@code Attribute} lives.
     */
    public Attribute(AttributableEERComponent owner, String text, AttributeSymbol symbol, AttributeArrow arrow, AttributeEnding ending, Diagram diagram) {
        super(text, owner.getX(), owner.getY(), diagram);
        this.owner = owner;
        this.symbol = symbol;
        this.arrow = arrow;
        this.ending = ending;

        setDrawingPriority(0);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                            Drawing Related Methods                                             */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Swaps the optionality of the {@code Attribute}.
     */
    public void swapOptionality() {

        if (this.arrow == AttributeArrow.OPTIONAL) {
            this.arrow = AttributeArrow.NON_OPTIONAL;
        } else {
            this.arrow = AttributeArrow.OPTIONAL;
        }

        this.diagram.repaint();
    }

    /**
     * Change the number of values of the {@code Attribute}.
     */
    public void swapMultivalued() {

        if (this.ending == AttributeEnding.MULTIVALUED) {
            this.ending = AttributeEnding.NON_MULTIVALUED;
        } else {
            this.ending = AttributeEnding.MULTIVALUED;
        }

        this.diagram.repaint();
    }

    /**
     *
     * @return {@code TRUE} if the {@code Attribute} is main.
     */
    public boolean isMain() { return this.symbol.equals(AttributeSymbol.MAIN); }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                       Overridden Methods and Related                                           */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        Point textPosition = calculateTextPosition();
        Rectangle textBounds = calculateTextBounds(g2, this.getText(), textPosition);

        setShape(textBounds);
        drawText(g2, this.getText(), textPosition);
        drawOwnerLine(g2, textPosition);
        drawConnectingLine(g2, textPosition);
        drawArrowAtEnd(g2, textPosition);
        drawCircleAtEnd(g2, textPosition);

        setShape(new Rectangle(
                textBounds.x + lineLength + circleRadius * 2 + minorCorrection,
                textBounds.y + minorCorrection * 2,
                textBounds.width,
                textBounds.height)
        );

        resetGraphics(g2);

        g2.draw(this.getShape());
    }

    @Override
    public int getLevel() {
        return this.owner.getLevel() + 1;
    }

    /**
     * Calculates the text position based on the owner.
     *
     * @return A {@code Point} containing the position of the text.
     */
    private Point calculateTextPosition() {

        int attributePosition = this.owner.getAbsoluteAttributePosition(this);

        Rectangle ownerBounds = this.owner.getBounds();

        int x, y;

        y = ((int) ownerBounds.getCenterY() + (int) ownerBounds.getMaxY()) / 2;

        if (this.getLevel() == 1) {

            x = (int) ownerBounds.getMaxX() + 5;
        } else {

            attributePosition ++; // Due to attributes of level > 1 are drawn under the owner attribute.
            x = (int) ownerBounds.getMinX() + 5;
            y -= minorCorrection;
        }

        y += attributePosition * 16;

        return new Point(x, y);
    }

    /**
     * Calculates the text limits as a Rectangle shape.
     *
     * @param g2 Graphics context.
     * @param text Text.
     * @param position Position of the text.
     * @return {@code Rectangle} containing the limits of the text.
     */
    private Rectangle calculateTextBounds(Graphics2D g2, String text, Point position) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int rectY = position.y - textHeight;
        return new Rectangle(position.x, rectY, textWidth, textHeight);
    }

    /**
     * Draws the text.
     *
     * @param g2 Graphics context.
     * @param text Text to be drawn.
     * @param position Text position.
     */
    private void drawText(Graphics2D g2, String text, Point position) {
        g2.setFont(new Font("Arial Unicode MS", Font.BOLD, 10));
        g2.drawString(text,
                position.x + lineLength + circleRadius * 2 + minorCorrection,
                position.y + minorCorrection)
        ;
    }

    /**
     * Draws a line to the owner.
     *
     * @param g2 Graphics context.
     * @param textPosition Position of the attribute's text.
     */
    private void drawOwnerLine(Graphics2D g2, Point textPosition) {

        int attributePosition = this.owner.getRelativeAttributePosition(this);

        if (attributePosition == 0) {

            owner.drawStartLineToAttribute(g2, textPosition);
        } else {

            // It's under another attribute.
            Rectangle attributeBounds = owner.getAttributeBounds(attributePosition - 1);

            g2.drawLine(
                    textPosition.x,
                    (int) attributeBounds.getMaxY() - minorCorrection * 2,
                    textPosition.x,
                    textPosition.y
            );
        }
    }

    private void drawConnectingLine(Graphics2D g2, Point textPosition) {
        Stroke currentStroke = g2.getStroke();

        if (this.arrow == AttributeArrow.OPTIONAL) {

            // Set the dashed pattern
            float[] dashPattern = {2f, 2f};  // 5 pixels on, 5 pixels off
            BasicStroke dashedStroke = new BasicStroke(1,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10f, dashPattern,
                    0f
            );

            g2.setStroke(dashedStroke);
        }

        g2.drawLine(textPosition.x, textPosition.y, textPosition.x + lineLength, textPosition.y);

        g2.setStroke(currentStroke);
    }

    private void drawArrowAtEnd(Graphics2D g2, Point textPosition) {

        if (this.ending == AttributeEnding.MULTIVALUED) {

            int arrowHeight = 3;
            int arrowWidth = 3;

            int x = textPosition.x + lineLength;

            g2.drawLine(x - arrowWidth, textPosition.y + arrowHeight, x, textPosition.y);
            g2.drawLine(x - arrowWidth, textPosition.y - arrowHeight, x, textPosition.y);
        }
    }

    private void drawCircleAtEnd(Graphics2D g2, Point textPosition) {

        int x = textPosition.x + lineLength;
        int y = textPosition.y - circleRadius;
        int doubleRadius = circleRadius * 2;

        if (this.symbol == AttributeSymbol.COMMON) {
            g2.drawOval(x, y, doubleRadius, doubleRadius);
        } else if (this.symbol == AttributeSymbol.ALTERNATIVE) {
            g2.drawOval(x, y, doubleRadius, doubleRadius);
            g2.fillArc(x, y, doubleRadius, doubleRadius, 270, 180);  // Right middle filled.
        } else if (this.symbol == AttributeSymbol.MAIN) {
            g2.fillOval(x, y, doubleRadius, doubleRadius);
        }
    }

    private void resetGraphics(Graphics2D g2) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(Color.BLACK);
    }

    public boolean isMultivalued () {
        return this.ending == AttributeEnding.MULTIVALUED;
    }

    public boolean isOptional() {
        return this.arrow == AttributeArrow.OPTIONAL;
    }

    public boolean isAlternative() {
        return this.symbol == AttributeSymbol.ALTERNATIVE;
    }


    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem item = new JMenuItem("action.addAttribute");
        item.addActionListener(_ -> this.addAttribute());
        popupMenu.add(item);

        item = new JMenuItem("action.swapOptionality");
        item.addActionListener(_ -> this.swapOptionality());
        popupMenu.add(item);

        item = new JMenuItem("action.swapMultivalued");
        item.addActionListener(_ -> this.swapMultivalued());
        // noinspection DuplicatedCode
        popupMenu.add(item);

        item = new JMenuItem("action.rename");
        item.addActionListener(_ -> this.rename());
        popupMenu.add(item);

        item = new JMenuItem("action.setForDelete");
        item.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(item);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void drawStartLineToAttribute(Graphics2D g2, Point textPosition) {

        Rectangle bounds = this.getBounds();

        // Vertical line that comes from inside the relationship.
        int x = (int) bounds.getMinX() - circleRadius - minorCorrection;
        int y = (int) bounds.getMaxY();
        g2.drawLine(x, y - 1, x, textPosition.y);
        // This minus one is important so there is no space between the circle and the line.

        // Horizontal line that comes from inside the attributable component.
        g2.drawLine(x, textPosition.y, textPosition.x, textPosition.y);
    }

    @Override
    protected void notifyRemovingOf(Component component) {

        if (component.equals(this.owner)) {
            this.setForDelete();
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean canBeSelectedBySelectionArea() { return false; }

    @Override
    public String toString() {
        return this.getText();
    }

    @Override
    public String getIdentifier() {
        return this.getText();
    }
}
