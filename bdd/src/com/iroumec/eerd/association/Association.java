package com.iroumec.eerd.association;

import com.iroumec.components.Component;
import com.iroumec.EERDiagram;
import com.iroumec.eerd.relationship.Relationship;
import com.iroumec.eerd.relationship.relatable.Relatable;
import com.iroumec.eerd.relationship.relatable.RelatableImplementation;
import com.iroumec.derivation.Derivable;
import com.iroumec.derivation.Derivation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Association extends Component implements Relatable, Derivable {

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
    public Association(Relationship relationship) {
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

        item = new JMenuItem("action.delete");
        item.addActionListener(_ -> this.setForDelete());
        popupMenu.add(item);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanReferencesTo(Component component) {

        if (!component.equals(this.relationship)) {
            this.removeRelationship(relationship);
        }
    }

    @Override
    public void notifyRemovingOf(Component component) {

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
