package com.bdd.mer.components.relationship;

import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.atributo.symbology.AttributeSymbol;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.components.relationship.cardinality.Cardinality;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Relationship extends AttributableComponent {

    private final List<Dupla<Entity, Cardinality>> entities; // I use a Dupla and not a HashMap because I cannot change dynamically an entity in it
    private int diagonalHorizontal, diagonalVertical; // Posición del centro del rombo
    private final Polygon forma;
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

        int anchoTexto = fontMetrics.getFirst();
        int altoTexto = fontMetrics.getSecond();

        // Calcula la posición del texto para centrarlo en el recuadro
        int xTexto = getX() - anchoTexto / 2;
        int yTexto = getY() + altoTexto / 2;

        // Cambia el grosor del recuadro
        g2.setStroke(new BasicStroke(1));

        // Dibuja el recuadro de la entidad
        int margen = 20; // Margen alrededor del texto

        // Dibuja una línea desde el centro de la relación hasta el centro de cada entidad
        for (Dupla<Entity, Cardinality> dupla : this.entities) {

            Entity entity = dupla.getFirst();

            g2.drawLine(this.getX(), this.getY(), entity.getX(), entity.getY());
        }

        // Le doy forma al polígono
        // La diagonal solo necesito calcularla una única vez
        if (!diagonalDefinida) {
            diagonalHorizontal = anchoTexto + 2 * margen; // Diagonal horizontal basada en el ancho del texto
            diagonalVertical = altoTexto + 2 * margen; // Diagonal vertical basada en el alto del texto
            diagonalDefinida = true;
        }

        forma.reset(); // Desactivating this is funny.
        forma.addPoint(getX(), getY() - diagonalVertical / 2); // Punto superior
        forma.addPoint(getX() + diagonalHorizontal / 2, getY()); // Punto derecho
        forma.addPoint(getX(), getY() + diagonalVertical / 2); // Punto inferior
        forma.addPoint(getX() - diagonalHorizontal / 2, getY()); // Punto izquierdo

        g2.setColor(Color.WHITE);
        g2.fillPolygon(forma); // Rellena el polígono con el gradiente

        g2.setColor(Color.BLACK);
        g2.drawString(this.getText(), xTexto, yTexto); // Dibuja el nombre de la relación

        // Cambia el color de dibujo basándote en si la entidad está seleccionada o no
        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        // Dibuja el rombo;
        g2.drawPolygon(forma);
    }

    public Rectangle getBounds() {

        return this.forma.getBounds();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                         Attribute Related Methods                                              */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanPresence() {

        // We break the bound between the relationship and their entities.
        for (Dupla<Entity, Cardinality> dupla : this.entities) {
            dupla.getFirst().removeRelationship(this);
        }

    }

    @Override
    public void changeReference(Entity oldEntity, Entity newEntity) {

        for (Dupla<Entity, Cardinality> dupla : this.entities) {

            if (dupla.getFirst().equals(oldEntity)) {
                dupla.setFirst(newEntity);
            }

        }

    }

    public void addEntity(Entity entity, Cardinality cardinality) {
        this.entities.add(new Dupla<>(entity, cardinality));
        entity.addRelationship(this);
        cardinality.setOwner(entity);
        cardinality.setRelationship(this);
    }

    public void removeEntity(Entity entity) {

        for (Dupla<Entity, Cardinality> dupla : this.entities) {
            if (dupla.getFirst().equals(entity)) {
                this.entities.remove(dupla);
                break;
            }
        }

    }

    public List<Component> getRelatedComponents() {

        List<Component> out = new ArrayList<>(this.getAttributes());

        for (Dupla<Entity, Cardinality> dupla : this.entities) {
            out.add(dupla.getFirst());
            out.addAll(dupla.getFirst().getAttributes());
            out.add(dupla.getSecond());
        }

        return out;

    }

    public int getNumberOfEntities() {
        return this.entities.size();
    }

    public void cleanEntity(Entity entity) {

        if (getNumberOfEntities() > 2) {
            this.removeEntity(entity);
        }

        // In other case, we don't have to do anything because, if cleanEntity was called, it is because
        // the entity will be eliminated and, so, the relationship also if it doesn't enter the if body.

    }
}