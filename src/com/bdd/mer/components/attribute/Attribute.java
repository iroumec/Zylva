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

        Point textPosition = calculateTextPosition();
        String displayText = prepareText();
        Rectangle textBounds = calculateTextBounds(g2, displayText, textPosition);

        setShape(textBounds);
        drawText(g2, displayText, textPosition);
        drawConnectingLine(g2, textPosition);

        resetGraphics(g2);

        //g2.draw(this.getShape());
    }

    /**
     * Calculates the text position based on the owner.
     *
     * @return A {@code Point} containing the position of the text.
     */
    private Point calculateTextPosition() {

        int attributePosition = this.owner.getAbsoluteAttributePosition(this);

        Rectangle ownerBounds = this.owner.getBounds();

        // Dependiendo del nivel de laa jerarquía, es dónde se dibuja.

        int x = (int) ownerBounds.getMaxX() + 5;
        int y = ((int) ownerBounds.getCenterY() + (int) ownerBounds.getMaxY()) / 2 +
                attributePosition * 16;

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
        g2.drawString(text, position.x, position.y + 1); // Necessary correction.
    }

    /**
     * Draws a line to the owner.
     *
     * @param g2 Graphics context.
     * @param textPosition Position of the attribute's text.
     */
    private void drawConnectingLine(Graphics2D g2, Point textPosition) {

        int attributePosition = this.owner.getRelativeAttributePosition(this);

        if (attributePosition == 0) {

            owner.drawStartLineToAttribute(g2, textPosition);
        } else {

            // It's under another attribute.
            Rectangle attributeBounds = owner.getAttributeBounds(attributePosition - 1);

            g2.drawLine(textPosition.x, (int) attributeBounds.getMaxY(), textPosition.x, textPosition.y);
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

    @Override
    public int getAbsoluteAttributePosition(Attribute attribute) {
        return this.owner.getAbsoluteAttributePosition(this)
                + this.getRelativeAttributePosition(attribute) + 1;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void drawStartLineToAttribute(Graphics2D g2, Point textPosition) {

        Rectangle bounds = this.getBounds();

        // Vertical line that comes from inside the relationship (in entities is not visible).
        int x = (int) bounds.getMaxX();
        int y = (int) bounds.getMaxY();
        g2.drawLine(x, y, x, textPosition.y);

        // Horizontal line that comes from inside the attributable component.
        g2.drawLine(x, textPosition.y, textPosition.x, textPosition.y);
    }

    @Override
    protected void cleanReferencesTo(Component component) {
        /*
        Method lef empty in purpose.

        There is no important reference in the class that would not produce its elimination in the
        notifyRemovingOf() method.
         */
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
