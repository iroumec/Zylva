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

    /**
     * Core {@code Relationship} forming the association.
     */
    private final Relationship relationship;

    /**
     * Constructs an {@code Association}.
     *
     * @param relationship Core {@code Relationship} forming the association.
     * @param drawingPanel {@code DrawingPanel} where the Association lives.
     */
    public Association(Relationship relationship, DrawingPanel drawingPanel) {
        super(drawingPanel);
        this.relationshipsManager = new RelatableImplementation();
        this.relationship = relationship;
        this.relationship.setAssociation(this);

        setDrawingPriority(1);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @SuppressWarnings("Duplicates")
    @Override
    public void draw(Graphics2D g2) {

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        List<Component> components = new ArrayList<>(this.relationship.getRelatedComponents());
        components.add(relationship);

        for (Component component : components) {
            Rectangle bounds = component.getBounds();

            minX = Math.min(minX, (int) bounds.getMinX());
            minY = Math.min(minY, (int) bounds.getMinY());
            maxX = Math.max(maxX, (int) bounds.getMaxX());
            maxY = Math.max(maxY, (int) bounds.getMaxY());
        }

        int margin = 5;

        int rectWidth = (maxX - minX) + 2 * margin;
        int rectHeight = (maxY - minY) + 2 * margin;

        this.setX((maxX + minX) / 2);
        this.setY((maxY + minY) / 2);

        Rectangle shape = new Rectangle(minX - margin, minY - margin, rectWidth, rectHeight);
        g2.setColor(Color.WHITE);
        g2.fill(shape);

        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.BLACK);

        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        g2.draw(shape);

        this.setShape(shape);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(
                this,
                Action.ADD_REFLEXIVE_RELATIONSHIP,
                Action.DELETE
        );

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanPresence() {

        this.relationship.setAssociation(null);

        // This is important in case the relationship has three or more children.
        for (Relationship relationship : this.relationshipsManager.getRelationships()) {
            relationship.cleanRelatable(this);
        }

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Component> getComponentsForRemoval() {

        List<Component> out = super.getComponentsForRemoval();

        // If a relationship has three or more participating entities, if I delete one, it can still exist.
        for (Relationship relationship : this.relationshipsManager.getRelationships()) {
            if (relationship.getNumberOfParticipants() <= 2) {
                out.addAll(relationship.getComponentsForRemoval());
                out.add(relationship);
            }
        }

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void changeReference(Component oldComponent, Component newComponent) {
        // Do nothing, due to a relationship is not able to change.
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void addRelationship(Relationship relationship) {
        this.relationshipsManager.addRelationship(relationship);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void removeRelationship(Relationship relationship) {
        this.relationshipsManager.removeRelationship(relationship);
    }
}
