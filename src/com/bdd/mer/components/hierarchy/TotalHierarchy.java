package com.bdd.mer.components.hierarchy;

import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.DrawingPanel;

import java.awt.*;

public class TotalHierarchy extends Hierarchy {

    public TotalHierarchy(HierarchyType exclusivity, Entity parent, DrawingPanel drawingPanel) {
        super(exclusivity, parent, drawingPanel);
    }

    @Override
    protected void drawParentLine(Graphics2D g2) {
        g2.drawLine(this.getX() + 3, this.getY() + 3, this.getParent().getX() + 3, this.getParent().getY() + 3);
        g2.drawLine(this.getX() - 3, this.getY() - 3, this.getParent().getX() - 3, this.getParent().getY() - 3);
    }

}
