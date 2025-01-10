package com.bdd.mer.components.association;

import com.bdd.mer.actions.Action;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.components.relationship.relatable.Relatable;
import com.bdd.mer.components.relationship.relatable.RelatableImplementation;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Association extends Component implements Relatable {

    private final RelatableImplementation relationshipsManager;

    // List of components forming the Association.
    private final Relationship relationship;

    public Association(Relationship relationship, DrawingPanel drawingPanel) {
        super(drawingPanel);
        this.relationshipsManager = new RelatableImplementation();
        this.relationship = relationship;
    }

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(
                this,
                Action.DELETE
        );

    }

    @Override
    public void draw(Graphics2D g2) {

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        // Crea una copia de la lista para no modificar la original
        List<Component> components = new ArrayList<>(this.relationship.getRelatedComponents());
        components.add(relationship);

        // Calcula los límites del rectángulo que engloba todos los componentes
        for (Component component : components) {
            Rectangle bounds = component.getBounds();

            minX = Math.min(minX, (int) bounds.getMinX());
            minY = Math.min(minY, (int) bounds.getMinY());
            maxX = Math.max(maxX, (int) bounds.getMaxX());
            maxY = Math.max(maxY, (int) bounds.getMaxY());
        }

        int margen = 5;

        // Calcula el ancho y alto del rectángulo correctamente
        int rectWidth = (maxX - minX) + 2 * margen;
        int rectHeight = (maxY - minY) + 2 * margen;

        this.setX((maxX + minX) / 2);
        this.setY((maxY + minY) / 2);

        this.drawLinesToRelationships(g2, this.getX(), this.getY());

        Rectangle shape = new Rectangle(minX - margen, minY - margen, rectWidth, rectHeight);
        g2.setColor(Color.WHITE);
        g2.fill(shape);

        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.BLACK);

        // Cambia el color y grosor del borde si está seleccionado
        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        // Dibuja el rectángulo
        g2.draw(shape);

        // Actualiza la forma de la entidad
        this.setShape(shape);
    }


    @Override
    public void cleanPresence() {

    }

    @Override
    public void changeReference(Component oldComponent, Component newComponent) {

    }

    @Override
    public void addRelationship(Relationship relationship) {
        this.relationshipsManager.addRelationship(relationship);
    }

    @Override
    public void removeRelationship(Relationship relationship) {
        this.relationshipsManager.removeRelationship(relationship);
    }

    @Override
    public void drawLinesToRelationships(Graphics2D graphics2D, int x, int y) {
        this.relationshipsManager.drawLinesToRelationships(graphics2D, x, y);
    }
}
