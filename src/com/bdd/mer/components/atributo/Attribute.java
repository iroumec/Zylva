package com.bdd.mer.components.atributo;

import com.bdd.mer.actions.Action;
import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.atributo.symbology.AttributeArrow;
import com.bdd.mer.components.atributo.symbology.AttributeEnding;
import com.bdd.mer.components.atributo.symbology.AttributeSymbol;
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
    private AttributableComponent owner;

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
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                            Drawing Related Methods                                             */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     *
     * @return The {@code Attribute}'s symbol.
     */
    public String getSymbol() { return this.symbol.getSymbol(); }

    /**
     *
     * @return The {@code Attribute}'s arrow.
     */
    public String getArrow() { return this.arrow.getArrowBody(); }

    /**
     *
     * @return The {@code Attribute}'s ending.
     */
    public String getEnding() { return this.ending.getArrowEnding(); }

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
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        Rectangle ownerBounds = this.owner.getBounds();

        int x = ((int) ownerBounds.getCenterX() + (int) ownerBounds.getMaxX())/2;
        int y = (int) ownerBounds.getMaxY();
        int i = this.owner.getAttributePosition(this);

        int atributoY = y + 15 + i * 16; // Attributes position in Y axis.

        // It's necessary to change the font so the UNICODE chars can be shown.
        g2.setFont(new Font("Arial Unicode MS", Font.BOLD, 10));

        String nombreAMostrar = this.getArrow() + getEnding() + this.getSymbol() + this.getText();
        g2.drawString(nombreAMostrar, x, atributoY);

        // Draw a line to the owner.
        g2.drawLine(x, y, x, atributoY);

        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(nombreAMostrar);
        int altoTexto = fm.getHeight();

        int rectY = atributoY - altoTexto;

        setShape(new Rectangle(x, rectY, anchoTexto, altoTexto));

        // The font is reset.
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
    public void changeReference(Component oldComponent, Component newComponent) {

        if (newComponent instanceof AttributableComponent) {

            if (this.owner.equals(oldComponent)) {
                this.owner = (AttributableComponent) newComponent;
            }

        }

    }

}
