package com.iroumec.eerd.relationship;

import com.iroumec.components.Component;
import com.iroumec.EERDiagram;
import com.iroumec.derivation.Derivable;
import com.iroumec.derivation.Derivation;
import com.iroumec.userPreferences.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Association extends Component implements Relatable, Derivable {

    /**
     * Core {@code Relationship} forming the association.
     */
    private final Relationship relationship;

    /**
     * Constructs an {@code Association}.
     *
     * @param relationship Core {@code Relationship} forming the association.
     */
    Association(Relationship relationship) {
        super();
        this.relationship = relationship;
        this.relationship.setAssociation(this);

        this.subscribeTo(relationship, Subscription.DELETION);

        setDrawingPriority(1);
    }

    @SuppressWarnings("Duplicates")
    private Rectangle calculateBounds() {

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        List<Rectangle> bounds = new ArrayList<>(this.relationship.getAssociationBounds());

        for (Rectangle bound : bounds) {

            minX = Math.min(minX, (int) bound.getMinX());
            minY = Math.min(minY, (int) bound.getMinY());
            maxX = Math.max(maxX, (int) bound.getMaxX());
            maxY = Math.max(maxY, (int) bound.getMaxY());
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

        JMenuItem item = new JMenuItem(LanguageManager.getMessage("action.addReflexiveRelationship"));
        item.addActionListener(_ -> Relationship.addReflexiveRelationship((EERDiagram) this.diagram, this));
        popupMenu.add(item);

        item = new JMenuItem(LanguageManager.getMessage("action.delete"));
        item.addActionListener(_ -> this.setForDelete());
        popupMenu.add(item);

        return popupMenu;
    }

    @Override
    public void notifyRemovingOf(Component component) {

        if (component.equals(this.relationship)) {
            this.setForDelete();
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Rectangle> getAssociationBounds() {
        return List.of(this.getBounds());
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
