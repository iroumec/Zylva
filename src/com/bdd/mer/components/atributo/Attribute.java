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

    private final AttributeSymbol symbol;
    private AttributeArrow arrow;
    private AttributeEnding ending;
    private AttributableComponent owner;

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                         Initializing Related Methods                                           */
    /* -------------------------------------------------------------------------------------------------------------- */

    public Attribute(AttributableComponent owner, String text, AttributeSymbol symbol, AttributeArrow arrow, AttributeEnding ending, DrawingPanel drawingPanel) {
        super(text, owner.getX(), owner.getY(), drawingPanel);
        this.owner = owner;
        this.symbol = symbol;
        this.arrow = arrow;
        this.ending = ending;
    }

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
    /*                                            Drawing Related Methods                                             */
    /* -------------------------------------------------------------------------------------------------------------- */

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

        // In case it is compound...
        drawComponents(g2, x, y, atributoY);

        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(nombreAMostrar);
        int altoTexto = fm.getHeight();

        int rectY = atributoY - altoTexto;

        setShape(new Rectangle(x, rectY, anchoTexto, altoTexto));

        // The font is reset.
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        g2.setColor(Color.BLACK);

    }

    public String getSymbol() { return this.symbol.getSymbol(); }

    public String getArrow() { return this.arrow.getArrowBody(); }

    public String getEnding() { return this.ending.getArrowEnding(); }

    protected void drawComponents(Graphics g, int x, int y, int atributoY) {
        g.drawLine(x, y, x, atributoY);
    }

    @Override
    public void cleanPresence() {

        this.owner.removeAttribute(this);

    }

    @Override
    public void changeReference(Component oldComponent, Component newComponent) {

        if (newComponent instanceof AttributableComponent) {

            if (this.owner.equals(oldComponent)) {
                this.owner = (AttributableComponent) newComponent;
            }

        }

    }

    public void changeOptionality() {

        if (this.arrow == AttributeArrow.OPTIONAL) {
            this.arrow = AttributeArrow.NON_OPTIONAL;
        } else {
            this.arrow = AttributeArrow.OPTIONAL;
        }
    }

    public void changeMultivalued() {

        if (this.ending == AttributeEnding.MULTIVALUED) {
            this.ending = AttributeEnding.NON_MULTIVALUED;
        } else {
            this.ending = AttributeEnding.MULTIVALUED;
        }
    }

    public boolean isMain() { return this.symbol.equals(AttributeSymbol.MAIN); }
}
