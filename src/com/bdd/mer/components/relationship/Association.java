package com.bdd.mer.components.relationship;

import com.bdd.GUI.Component;
import com.bdd.mer.EERDiagram;
import com.bdd.mer.components.EERComponent;
import com.bdd.mer.components.relationship.relatable.Relatable;
import com.bdd.mer.components.relationship.relatable.RelatableImplementation;
import com.bdd.mer.derivation.Derivable;
import com.bdd.mer.derivation.Derivation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Association extends EERComponent implements Relatable, Derivable {

    /**
     * Core {@code Relationship} forming the association.
     */
    private final Relationship relationship;

    private final RelatableImplementation relationshipsManager;

    /**
     * Constructs an {@code Association}.
     *
     * @param relationship Core {@code Relationship} forming the association.
     */
    Association(Relationship relationship) {
        super();
        this.relationshipsManager = new RelatableImplementation();
        this.relationship = relationship;
        this.relationship.setAssociation(this);

        setDrawingPriority(1);
    }

    @SuppressWarnings("Duplicates")
    private Rectangle calculateBounds() {

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

        return new Rectangle(minX - margin, minY - margin, rectWidth, rectHeight);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        Rectangle shape = this.calculateBounds();

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

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem item = new JMenuItem("action.addReflexiveRelationship");
        item.addActionListener(_ -> Relationship.addReflexiveRelationship((EERDiagram) this.diagram, this));
        popupMenu.add(item);

        item = new JMenuItem("action.setForDelete");
        item.addActionListener(_ -> this.setForDelete());
        popupMenu.add(item);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected void cleanReferencesTo(Component component) {

        if (!component.equals(this.relationship)) {
            this.removeRelationship(relationship);
        }
    }

    @Override
    protected void notifyRemovingOf(Component component) {

        if (component.equals(this.relationship)) {
            this.setForDelete();
        }
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

    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * @return Always an empty list, due to its derivation process is contained in its relationship.
     */
    @Override
    public List<Derivation> getDerivations() {
        return new ArrayList<>();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * @return Its relationship's identifier.
     */
    @Override
    public String getIdentifier() {
        return this.relationship.getIdentifier();
    }
}
