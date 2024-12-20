package com.bdd.mer.components.relationship;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class Cardinality extends Component implements Serializable {

    private Entity owner;
    private Relationship relationship;
    private Rectangle bounds;

    public Cardinality(String firstValue, String secondValue) {
        this.setText(giveFormat(firstValue, secondValue));
    }

    @Override
    protected PopupMenu getGenericPopupMenu() {

        DrawingPanel drawingPanel = this.getPanelDibujo();
        PopupMenu cardinalityPopupMenu = new PopupMenu(drawingPanel);

        JMenuItem changeCardinality = new JMenuItem("Change values");
        changeCardinality.addActionListener(_ -> drawingPanel.getActioner().changeCardinality(this));

        cardinalityPopupMenu.addOption(changeCardinality);

        return cardinalityPopupMenu;

    }

    @Override
    public void draw(Graphics2D g2) {

        g2.setFont(new Font("Verdana", Font.BOLD, 10));

        Point closestPoint = owner.getClosestPoint(new Point(this.relationship.getX(), this.relationship.getY()));

        g2.drawString(this.getText(),
                (closestPoint.x + relationship.getX()) / 2,
                (closestPoint.y + relationship.getY()) / 2);

        // Calcula el ancho del texto
        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        //g2.drawRect(x, rectY, anchoTexto, altoTexto); Uncomment this to see the hitbox
        // Maybe I'll need to fix this later...

        this.bounds = new Rectangle(x, y, anchoTexto, altoTexto);

    }

    @Override
    public Rectangle getBounds() {
        return this.bounds;
    }

    @Override
    public List<Component> getComponentsForRemoval() {
        return List.of();
    }

    @Override
    public void cleanPresence() {

    }

    @Override
    public void changeReference(Entity oldEntity, Entity newEntity) {

        if (this.owner.equals(oldEntity)) {
            this.owner = newEntity;
        }

    }

    private String giveFormat(String firstValue, String secondValue) {
        return "(" + firstValue + ", " + secondValue + ")";
    }

    public void setOwner(Entity entity) { this.owner = entity; }
    public void setRelationship(Relationship relationship) { this.relationship = relationship; }
}
