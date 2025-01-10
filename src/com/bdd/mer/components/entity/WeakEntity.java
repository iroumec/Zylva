package com.bdd.mer.components.entity;

import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.frame.DrawingPanel;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class WeakEntity extends Entity {

    private final Relationship relationship;

    public WeakEntity(String text, int x, int y, Relationship relationship, DrawingPanel drawingPanel) {

        super(text, x, y, drawingPanel);

        this.relationship = relationship;
    }

    @Override
    public void draw(Graphics2D g2) {

        // Calcula el ancho del texto
        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        // Calcula la posición del texto para centrarlo en el recuadro
        int xTexto = getX() - anchoTexto / 2;
        int yTexto = getY() + altoTexto / 2;

        // Cambia el grosor del recuadro.
        g2.setStroke(new BasicStroke(1));

        // Dibuja el recuadro de la entidad
        int margen = 10; // Margen alrededor del texto

        int rectX = getX() - anchoTexto / 2 - margen;
        int rectY = getY() - altoTexto / 2 - margen;
        int rectAncho = anchoTexto + 2 * margen;
        int rectAlto = altoTexto + 2 * margen;

        this.drawLinesToRelationships(g2, this.getX(), this.getY());
        g2.drawLine(this.getX() - 3, this.getY() - 3, this.relationship.getX() - 3, this.relationship.getY() - 3);
        g2.drawLine(this.getX() + 3, this.getY() + 3, this.relationship.getX() + 3, this.relationship.getY() + 3);

        RoundRectangle2D shape = new RoundRectangle2D.Float(rectX, rectY, rectAncho, rectAlto, 10, 10);
        this.setShape(shape);

        Rectangle bounds = this.getBounds();
        RoundRectangle2D externalRectBounds = new RoundRectangle2D.Float(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6, 10, 10); // The number of the left must be half the number on the right

        g2.setColor(Color.WHITE);
        g2.fill(externalRectBounds);

        // Name of the entity.
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawString(super.getText(), xTexto, yTexto);

        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        g2.draw(shape);
        g2.draw(externalRectBounds);

        this.setShape(externalRectBounds);
    }

    public Entity getStrongVersion() {

        Entity strongVersion = new Entity(this.getText(), this.getX(), this.getY(), this.getPanelDibujo());

        this.copyAttributes(strongVersion);

        return strongVersion;

    }

    @Override
    public void removeRelationship(Relationship relationship) {

        if (this.relationship.equals(relationship)) {
            this.getPanelDibujo().replaceComponent(this, this.getStrongVersion());
        } else {
            super.removeRelationship(relationship);
        }

    }

    // This overdrive is important to draw it correctly.

    @Override
    public void addRelationship(Relationship relationship) {

        if (!this.relationship.equals(relationship)) {
            super.addRelationship(relationship);
        }

    }

}
