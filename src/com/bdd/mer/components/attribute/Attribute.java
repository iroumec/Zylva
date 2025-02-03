package com.bdd.mer.components.attribute;

import com.bdd.mer.actions.Action;
import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.attribute.symbology.AttributeArrow;
import com.bdd.mer.components.attribute.symbology.AttributeEnding;
import com.bdd.mer.components.attribute.symbology.AttributeSymbol;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.awt.*;

public class Attribute extends AttributableComponent {

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
    private final AttributableComponent owner;

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
     * @param drawingPanel {@code DrawingPanel} where the {@code Attribute} lives.
     */
    public Attribute(AttributableComponent owner, String text, AttributeSymbol symbol, AttributeArrow arrow, AttributeEnding ending, DrawingPanel drawingPanel) {
        super(text, owner.getX(), owner.getY(), drawingPanel);
        this.owner = owner;
        this.symbol = symbol;
        this.arrow = arrow;
        this.ending = ending;

        this.owner.addAttribute(this);

        setDrawingPriority(0);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                            Drawing Related Methods                                             */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Swaps the optionality of the {@code Attribute}.
     */
    public void changeOptionality() {

        if (this.arrow == AttributeArrow.OPTIONAL) {
            this.arrow = AttributeArrow.NON_OPTIONAL;
        } else {
            this.arrow = AttributeArrow.OPTIONAL;
        }
    }

    /**
     * Change the number of values of the {@code Attribute}.
     */
    public void changeMultivalued() {

        if (this.ending == AttributeEnding.MULTIVALUED) {
            this.ending = AttributeEnding.NON_MULTIVALUED;
        } else {
            this.ending = AttributeEnding.MULTIVALUED;
        }
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

        Rectangle ownerBounds = this.owner.getBounds();
        Point textPosition = calculateTextPosition(ownerBounds);
        String displayText = prepareText();
        Rectangle textBounds = calculateTextBounds(g2, displayText, textPosition);

        setShape(textBounds);
        drawText(g2, displayText, textPosition);
        drawConnectingLine(g2, ownerBounds, textPosition);

        resetGraphics(g2);

        //g2.draw(this.getShape());
    }

    /**
     * Calculates the text position based on the owner.
     *
     * @param ownerBounds Owner's bounds.
     * @return A {@code Point} containing the position of the text.
     */
    private Point calculateTextPosition(Rectangle ownerBounds) {
        int attributePosition = this.owner.getAttributePosition(this);

        int x = ((int) ownerBounds.getCenterX() + (int) ownerBounds.getMaxX()) / 2;
        int y = (int) ownerBounds.getMaxY() + 10 + attributePosition * 16;

        return new Point(x, y);
    }

    /**
     * Prepares the text to be shown.
     *
     * @return Prepared text.
     */
    private String prepareText() {
        return this.arrow + this.ending.toString() + this.symbol + this.getText();
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
        int textWidth = fm.stringWidth(text) - 12;
        // Manual correction because, I don't know why,
        // the width number is not accurate
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
        g2.drawString(text, position.x, position.y);
    }

    /**
     * Draws a line to the owner.
     *
     * @param g2 Graphics context.
     * @param ownerBounds Owner's bounds.
     * @param textPosition Position of the attribute's text.
     */
    private void drawConnectingLine(Graphics2D g2, Rectangle ownerBounds, Point textPosition) {
        int x = textPosition.x;
        int y = (int) ownerBounds.getCenterY();
        g2.drawLine(x, y, x, textPosition.y);
    }


    private void resetGraphics(Graphics2D g2) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(Color.BLACK);
    }


    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(
                this,
                Action.ADD_ATTRIBUTE,
                Action.SWAP_OPTIONALITY,
                Action.SWAP_MULTIVALUED,
                Action.RENAME,
                Action.DELETE
        );

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanPresence() {

        this.owner.removeAttribute(this);

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean canBeSelectedBySelectionArea() { return false; }

}
