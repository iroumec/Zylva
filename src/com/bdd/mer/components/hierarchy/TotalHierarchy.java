package com.bdd.mer.components.hierarchy;

import com.bdd.mer.components.entity.Entity;

import java.awt.*;

public class TotalHierarchy extends Hierarchy {

    public TotalHierarchy(HierarchyType exclusivity, Entity parent) {
        super(exclusivity, parent);
    }

    @Override
    protected void drawSuperentityLine(Graphics2D g2) {
        g2.drawLine(this.getX() + 3, this.getY() + 3, this.getParent().getX() + 3, this.getParent().getY() + 3);
        g2.drawLine(this.getX() - 3, this.getY() - 3, this.getParent().getX() - 3, this.getParent().getY() - 3);
    }

}
