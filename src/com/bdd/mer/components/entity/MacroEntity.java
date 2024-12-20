package com.bdd.mer.components.entity;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.relationship.Relationship;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MacroEntity extends Entity {

    private Relationship participatingRelationship;
    private final List<Entity> participatingEntities;
    private boolean selected = false;
    private boolean concealment = false;

    public MacroEntity(String text) {
        super(text);
        this.participatingEntities = new ArrayList<>();
    }

    @Override
    public void draw(Graphics2D g2) {

        if (this.concealment) {
            // It draws the macro-entity as an entity
            super.draw(g2);
        } else {
            this.nonConcealmentDraw(g2);
        }
    }

    public Rectangle getBounds() {

        // Define los límites de la macro entidad, partiendo de la relación
        Rectangle macroEntityBounds = new Rectangle(participatingRelationship.getBounds());
        this.x = participatingRelationship.getX();
        this.y = participatingRelationship.getY();

        // Itero sobre los rectángulos y los unimos al rectángulo contenedor
        for (Entity e : participatingEntities) {
            macroEntityBounds = macroEntityBounds.union(e.getBounds());
        }

        return macroEntityBounds;
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setText(String newText) {

    }

    private void reDrawParticipatingComponents(Graphics2D g) {
        participatingRelationship.draw(g);

        for (Entity e : participatingEntities) {
            e.draw(g);
        }
    }

    public boolean isSelected() {
        boolean selected = true;

        if (!participatingRelationship.isSelected()) {
            selected = false;
        } else {
            for (Entity e : participatingEntities) {
                if (!e.isSelected()) {
                    selected = false;
                    break;
                }
            }
        }

        return selected;
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                           Activating Concealment                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void exchangeConcealment() { this.concealment = !(this.concealment); }

    public boolean isConcealmentActivated() { return this.concealment; }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                          Drawing the Macro-Entity                                              */
    /* -------------------------------------------------------------------------------------------------------------- */

    private void nonConcealmentDraw(Graphics2D g) {
        Rectangle macroEntityBounds = this.getBounds();
        this.x = macroEntityBounds.x + macroEntityBounds.width/2;
        this.y = macroEntityBounds.y + macroEntityBounds.height/2;

        g.setColor(new Color(215, 239, 249));
        g.fillRect(macroEntityBounds.x, macroEntityBounds.y, macroEntityBounds.width, macroEntityBounds.height);

        //reDrawParticipatingComponents(g);

        if (selected) {
            g.setStroke(new BasicStroke(2));
            g.setColor(Color.CYAN);
        } else {
            g.setStroke(new BasicStroke(1));
            g.setColor(Color.BLACK);
        }

        g.drawRect(macroEntityBounds.x - 20, macroEntityBounds.y - 20, macroEntityBounds.width + 40, macroEntityBounds.height + 40);

        // Cambia el grosor del recuadro
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.BLACK);
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<Component> participatingComponents() {
        List<Component> list = new ArrayList<>(participatingEntities);
        list.add(participatingRelationship);
        return list;
    }

    public void addEntity(Entity participatingentity) {
        participatingEntities.add(participatingentity);
    }

    public void setParticipatingRelationship(Relationship participatingRelationship) {
        this.participatingRelationship = participatingRelationship;
    }
}
