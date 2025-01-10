package com.bdd.mer.components.relationship.cardinality;

import com.bdd.mer.actions.Action;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.components.relationship.relatable.Relatable;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Cardinality extends Component {

    private Relatable owner;
    private Relationship relationship;

    public Cardinality(String firstValue, String secondValue, DrawingPanel drawingPanel) {
        super(drawingPanel);
        this.setText(giveFormat(firstValue, secondValue));
    }

    @Override
    protected PopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(this, Action.CHANGE_CARDINALITY);

    }

    @Override
    public void draw(Graphics2D g2) {

        int x = (((Component) this.getOwner()).getX() + this.relationship.getX()) / 2;
        int y = (((Component) this.getOwner()).getY() + this.relationship.getY()) / 2;

        // Calcula el ancho y alto del texto
        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        // Ajusta las coordenadas para que el rectángulo encierre el texto correctamente
        int rectX = x - anchoTexto / 2; // Centra el texto horizontalmente
        int rectY = y - altoTexto / 2;  // Centra el texto verticalmente

        // Crea y dibuja el rectángulo que encierra el texto
        Rectangle shape = new Rectangle(rectX, rectY, anchoTexto, altoTexto);
        g2.setColor(Color.WHITE);
        g2.fill(shape);
        this.setShape(shape);

        // Dibuja el texto
        g2.setColor(Color.BLACK);
        g2.drawString(this.getText(), x - anchoTexto / 2, y + altoTexto / 4);

        // Debugging: Dibuja el rectángulo
        //g2.draw(shape);
    }


    @Override
    public List<Component> getComponentsForRemoval() {
        return new ArrayList<>();
    }

    @Override
    public void cleanPresence() {}

    @Override
    public void changeReference(Component oldComponent, Component newComponent) {

        if (newComponent instanceof Relatable) { // I don't have to check if oldComponents is instance of Relatable due to the equals below.

            if (this.owner.equals(oldComponent)) {
                this.owner = (Relatable) newComponent;
            }

        }

    }

    public static String giveFormat(String firstValue, String secondValue) {
        return "(" + firstValue + ", " + secondValue + ")";
    }

    public Relatable getOwner() { return this.owner; }

    public void setOwner(Relatable relatableComponent) { this.owner = relatableComponent; }
    public void setRelationship(Relationship relationship) { this.relationship = relationship; }
}
