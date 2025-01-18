package com.bdd.mer.components.hierarchy;

import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.DrawingPanel;

import java.awt.*;

// Is this class necessary? It's just a change in the line drawn to the parent.
public class TotalHierarchy extends Hierarchy {

    /**
     * Constructs a {@code TotalHierarchy}.
     *
     * @param exclusivity {@code TotalHierarchy}'s exclusivity.
     * @param parent {@code TotalHierarchy}'s parent entity.
     * @param drawingPanel {@code DrawingPanel} where the {@code TotalHierarchy} lives.
     */
    public TotalHierarchy(HierarchyExclusivity exclusivity, Entity parent, DrawingPanel drawingPanel) {
        super(exclusivity, parent, drawingPanel);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected void drawParentLine(Graphics2D g2) {
        g2.drawLine(this.getX() + 3, this.getY() + 3, this.getParent().getX() + 3, this.getParent().getY() + 3);
        g2.drawLine(this.getX() - 3, this.getY() - 3, this.getParent().getX() - 3, this.getParent().getY() - 3);
    }

}
