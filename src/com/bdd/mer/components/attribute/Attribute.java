package com.bdd.mer.components.attribute;

import com.bdd.GUI.components.Component;
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
    private AttributeSymbol symbol;

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
    private AttributableEERComponent owner;

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

        item = new JMenuItem("action.delete");
        item.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(item);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanPresence() {
        this.symbol = null;
        this.arrow = null;
        this.ending = null;
        this.owner.removeAttribute(this);
        this.owner = null;
        super.cleanPresence();
    }

    @Override
    protected void cleanReferencesTo(Component component) {

        if (component.equals(this.owner)) {
            this.delete();
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
