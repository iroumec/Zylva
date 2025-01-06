package com.bdd.mer.components.relationship;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Cardinality extends Component {

    private Entity owner;
    private Relationship relationship;

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

        Point closestPoint = owner.getClosestPoint(new Point(this.relationship.getX(), this.relationship.getY()));

        g2.drawString(this.getText(),
                (closestPoint.x + relationship.getX()) / 2 + 5, // Este cumple una función parecida a lo que dice abajo, pero vertical.
                (closestPoint.y + relationship.getY()) / 2 - 5); // EL -3 es para que no se solape la cardinalidad con la línea cuando está completamente en vertical.

        // Calcula el ancho del texto
        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        //g2.drawRect(x, rectY, anchoTexto, altoTexto); Uncomment this to see the hitbox
        // Maybe I'll need to fix this later...

        this.setShape(new Rectangle((closestPoint.x + relationship.getX()) / 2 + 5, (closestPoint.y + relationship.getY()) / 2 - 5, anchoTexto, altoTexto));
        //g2.drawRect((closestPoint.x + relationship.getX()) / 2 + 5, (closestPoint.y + relationship.getY()) / 2 - 5, anchoTexto, altoTexto);

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
