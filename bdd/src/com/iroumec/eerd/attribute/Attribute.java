package com.iroumec.eerd.attribute;

import com.iroumec.components.Component;
import com.iroumec.derivation.Derivation;
import com.iroumec.eerd.attribute.cardinalities.Cardinality;
import com.iroumec.eerd.attribute.cardinalities.Univalued;
import com.iroumec.eerd.attribute.presences.Obligatory;
import com.iroumec.eerd.attribute.presences.Presence;
import com.iroumec.eerd.attribute.roles.Rol;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class Attribute extends DescriptiveAttributable {

    private final Rol rol;
    private Presence presence;
    private Cardinality cardinality;

    /**
     * {@code Attribute}'s owner.
     */
    private final DescriptiveAttributable owner;

    public final static int lineLength = 25;
    public final static int circleRadius = 5;
    // Necessary to draw everything correctly.
    public final static int minorCorrection = 3;

    Attribute(Builder builder) {
        super(builder.text, builder.owner.getX(), builder.owner.getY());
        this.owner = builder.owner;
        this.rol = builder.rol;
        this.presence = builder.presence;
        this.cardinality = builder.cardinality;

        setDrawingPriority(0);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                            Drawing Related Methods                                             */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void swapPresence() {

        this.presence = this.presence.getOpposite();

        this.diagram.repaint();
    }

    public void swapCardinality() {

        this.cardinality = this.cardinality.getOpposite();

        this.diagram.repaint();
    }

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

        g2.drawString(this.getText(),
                textBounds.x + lineLength + circleRadius * 2 + minorCorrection,
                textBounds.y + minorCorrection
        );

        drawOwnerLine(g2, textPosition);
        this.presence.draw(g2, textPosition.x, textPosition.y, textPosition.x + lineLength, textPosition.y);
        this.cardinality.draw(g2, textPosition.x + lineLength, textPosition.y);
        this.rol.draw(g2, textPosition.x + lineLength, textPosition.y - circleRadius, circleRadius);

        setShape(new Rectangle(
                textBounds.x + lineLength + circleRadius * 2 + minorCorrection,
                textBounds.y + minorCorrection * 2,
                textBounds.width,
                textBounds.height)
        );

        //g2.draw(this.getShape());
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

    /**
     * Provided with legibility purposes.
     *
     * @return {@code TRUE} if the attribute is compound.
     */
    public boolean isCompound() {
        return this.hasAttributes();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                  Builder                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    static class Builder {

        // Required parameters.
        public final Rol rol;
        public final String text;
        public final DescriptiveAttributable owner;

        // Optional parameters - initialized with a default value.
        public Presence presence = Obligatory.getInstance();
        public Cardinality cardinality = Univalued.getInstance();

        public Builder(Rol rol, String text, DescriptiveAttributable owner) {
            this.rol = rol;
            this.text = text;
            this.owner = owner;
        }

        public Builder presence(Presence presence) {
            this.presence = presence;
            return this;
        }

        public Builder cardinality(Cardinality cardinality) {
            this.cardinality = cardinality;
            return this;
        }

        public Attribute build() {
            return new Attribute(this);
        }
    }


    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = this.rol.getPopupMenu(this);

        JMenuItem item = new JMenuItem("action.rename");
        item.addActionListener(_ -> this.rename());
        popupMenu.add(item);

        item = new JMenuItem("action.delete");
        item.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(item);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected void drawStartLineToAttribute(Graphics2D g2, Point textPosition) {

        Rectangle bounds = this.getBounds();

        // Vertical line that comes from inside the relationship.
        int x = (int) bounds.getMinX() - circleRadius - minorCorrection;
        int y = (int) bounds.getMaxY();
        g2.drawLine(x, y - 1, x, textPosition.y);
        // This minus one is important so there is no space between the circle and the line.

        // Horizontal line that comes from inside the attributable component.
        g2.drawLine(x, textPosition.y, textPosition.x, textPosition.y);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void notifyRemovingOf(Component component) {

        if (component.equals(this.owner)) {
            this.setForDelete();
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean canBeSelectedBySelectionArea() { return false; }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public String getIdentifier() {
        return this.getText();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Derivation> getDerivations() {

        return this.rol.getDerivations(this.owner, this, this.presence, this.cardinality);
    }
}
