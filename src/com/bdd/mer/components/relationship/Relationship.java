package com.bdd.mer.components.relationship;

import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.atributo.AttributeSymbol;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Relationship extends AttributableComponent {

    private final List<Dupla<Entity, Cardinality>> entities; // I use a Dupla and not a HashMap because I cannot change dynamically an entity in it
    private int diagonalHorizontal, diagonalVertical; // Posición del centro del rombo
    private final Polygon forma;
    private boolean formaDefinida = false;
    private boolean diagonalDefinida = false;

    public Relationship(String text, int x, int y) {
        super(text, x, y);

        this.entities = new ArrayList<>();

        forma = new Polygon();
    }

    @Override
    protected PopupMenu getGenericPopupMenu() {

        DrawingPanel drawingPanel = this.getPanelDibujo();
        PopupMenu relationPopupMenu = new PopupMenu(drawingPanel);

        JMenuItem rename = new JMenuItem("Rename");
        rename.addActionListener(_ -> drawingPanel.getActioner().renameComponent(this));

        JMenuItem delete = new JMenuItem("Delete");
        delete.addActionListener(_ -> drawingPanel.getActioner().deleteSelectedComponents());

        JMenuItem addAttribute = new JMenuItem("Add attribute");
        addAttribute.addActionListener(_ -> drawingPanel.getActioner().addAttribute(this, AttributeSymbol.COMMON));

        relationPopupMenu.addOption(addAttribute);
        relationPopupMenu.addOption(rename);
        relationPopupMenu.addOption(delete);

        return relationPopupMenu;

    }

    public void draw(Graphics2D g2) {

        Dupla<Integer, Integer> fontMetrics = this.getFontMetrics(g2);

        int anchoTexto = fontMetrics.getPrimero();
        int altoTexto = fontMetrics.getSegundo();

        // Calcula la posición del texto para centrarlo en el recuadro
        int xTexto = getX() - anchoTexto / 2;
        int yTexto = getY() + altoTexto / 2;

        // Cambia el grosor del recuadro
        g2.setStroke(new BasicStroke(1));

        // Dibuja el recuadro de la entidad
        int margen = 20; // Margen alrededor del texto

        // Dibuja una línea desde el centro de la relación hasta el centro de cada entidad
        for (Dupla<Entity, Cardinality> dupla : this.entities) {

            Entity entity = dupla.getPrimero();

            g2.drawLine(this.getX(), this.getY(), entity.getX(), entity.getY());
        }

        // Le doy forma al polígono
        // La diagonal solo necesito calcularla una única vez
        if (!diagonalDefinida) {
            diagonalHorizontal = anchoTexto + 2 * margen; // Diagonal horizontal basada en el ancho del texto
            diagonalVertical = altoTexto + 2 * margen; // Diagonal vertical basada en el alto del texto
            diagonalDefinida = true;
        }

        // Define la forma del polígono y me aseguro de hacerlo una sola vez
        // Si no hago esto, el polígono titila al mover las entidades
        if (!formaDefinida) {
            forma.addPoint(getX(), getY() - diagonalVertical / 2 + 8); // Punto superior
            forma.addPoint(getX() + diagonalHorizontal / 2, getY()); // Punto derecho
            forma.addPoint(getX(), getY() + diagonalVertical / 2 - 8); // Punto inferior
            forma.addPoint(getX() - diagonalHorizontal / 2, getY()); // Punto izquierdo
            formaDefinida = true;
        }

        // Aplica un gradiente al relleno del polígono
        GradientPaint gp = new GradientPaint(
                getX(), getY() - (float) diagonalVertical / 2, new Color(240, 240, 240), // Color superior (celeste claro)
                getX(), getY() + (float) diagonalVertical / 2, new Color(210, 210, 210) // Color inferior (celeste oscuro)
        );
        g2.setPaint(gp);
        g2.fillPolygon(forma); // Rellena el polígono con el gradiente

        // Cambia el color de dibujo basándote en si la entidad está seleccionada o no
        if (this.isSelected()) {
            g2.setColor(new Color(120, 190, 235));
            g2.setStroke(new BasicStroke(2));
        } else {
            g2.setColor(Color.BLACK);
        }

        // Dibuja el rombo
        g2.drawPolygon(forma);

        g2.setColor(Color.BLACK);
        g2.drawString(this.getText(), xTexto, yTexto); // Dibuja el nombre de la relación
    }

    public Rectangle getBounds() {

        return this.forma.getBounds();
    }

    private void updateShape() {
        forma.reset();
        forma.addPoint(getX(), getY() - diagonalVertical / 2 + 10); // Punto superior
        forma.addPoint(getX() + diagonalHorizontal / 2, getY()); // Punto derecho
        forma.addPoint(getX(), getY() + diagonalVertical / 2 - 10); // Punto inferior
        forma.addPoint(getX() - diagonalHorizontal / 2, getY()); // Punto izquierdo
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void setX(int x) {
        super.setX(x);
        updateShape();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        updateShape();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                         Attribute Related Methods                                              */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanPresence() {

        // We break the bound between the relationship and their entities.
        for (Dupla<Entity, Cardinality> dupla : this.entities) {
            dupla.getPrimero().removeRelationship(this);
        }

    }

    @Override
    public void changeReference(Entity oldEntity, Entity newEntity) {

        for (Dupla<Entity, Cardinality> dupla : this.entities) {

            if (dupla.getPrimero().equals(oldEntity)) {
                dupla.setPrimero(newEntity);
            }

        }

    }

    public void addEntity(Entity entity, Cardinality cardinality) {
        this.entities.add(new Dupla<>(entity, cardinality));
        entity.addRelationship(this);
        cardinality.setOwner(entity);
        cardinality.setRelationship(this);
    }

    public List<Component> getRelatedComponents() {

        List<Component> out = new ArrayList<>(this.getAttributes());

        for (Dupla<Entity, Cardinality> dupla : this.entities) {
            out.add(dupla.getPrimero());
            out.addAll(dupla.getPrimero().getAttributes());
            out.add(dupla.getSegundo());
        }

        return out;

    }
}