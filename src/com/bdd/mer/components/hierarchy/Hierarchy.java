package com.bdd.mer.components.hierarchy;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.PopupMenu;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hierarchy extends Component implements Serializable {

    int radio; // Centro del óvalo
    private String letter;
    private final Entity parent;
    private final List<Entity> childs;

    public Hierarchy(String text, Entity parent, List<Entity> childs, String letter) {
        super(text, parent.getX(), parent.getY() + 50);
        this.parent = parent;
        this.childs = childs;
        this.letter = letter; // d if it is exclusive. o id it is not exclusive.
    }

    @Override
    protected PopupMenu getGenericPopupMenu() {
        return null;
    }

    public void draw(Graphics2D g2) {

        // Cambia la fuente del texto
        g2.setFont(new Font("Verdana", Font.BOLD, 10));

        // Obtengo la fuente del texto y calculo su tamaño
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(this.letter);
        int textHeight = fm.getHeight();

        // Calcula el diámetro del círculo basado en el tamaño del texto
        int diameter = Math.max(textWidth, textHeight) + 10;
        this.radio = diameter/2;

        // Aplica suavizado a las líneas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Si la jerarquía es seleccionada, se pinta de CYAN
        if (this.isSelected()) {
            g2.setColor(Color.CYAN);
        }

        // Dibuja la línea al supertipo
        g2.drawLine(this.x, this.y, parent.getX(), parent.getY());
        // Si es total, dibuja dos líneas
        //if (this.isTotal()) {
        //    g2.drawLine(this.x - 5, this.y - 5, parent.getX() - 5, parent.getY() - 5);
        //}

        // Dibuja las líneas a los subtipos
        for (Entity e : childs) {
            g2.drawLine(this.x, this.y, e.getX(), e.getY());
        }

        // Dibuja el círculo adaptado al tamaño del texto
        g2.drawOval(this.x - radio, this.y - radio, diameter, diameter);

        // Rellena el círculo
        g2.setColor(Color.WHITE);
        g2.fillOval(this.x - radio, this.y - radio, diameter, diameter);

        // Dibuja el texto dentro del círculo
        g2.setColor(Color.BLACK);
        g2.drawString(this.letter, this.x - 4, this.y + 4);
    }

    /*
    En una jerarquía total, toda instancia del supertipo debe ser instancia también de alguno
    de los subtipos.

    Una jerarquía exclusiva se nota con una doble línea del supertipo al ícono de jerarquía.
    Por otro lado, si la jerarquía es parcial, se utiliza una única línea.
     */

    /*
    En una jerarquía exclusiva, los ejemplares de los subtipos son conjuntos disjuntos (solo pueden
    pertenecer a un subtipo a la vez).

    Una jerarquía exclusiva se nota con la letra "d" (Disjunt), mientras que una jerarquía compartida
    se nota con la letra "o" (Overlapping).
     */


    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - radio, y - radio, radio * 2, radio * 2);
    }

    @Override
    public List<Component> getComponentsForRemoval() {
        return List.of();
    }

    @Override
    public void cleanPresence() {

        this.parent.removeHierarchy(this);

        for (Entity entity : this.childs) {
            entity.removeHierarchy(this);
        }

    }

    @Override
    public void changeReference(Entity oldEntity, Entity newEntity) {

    }

    public List<Entity> getChilds() { return new ArrayList<>(this.childs); };
}
