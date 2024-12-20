package com.bdd.mer.components.relationship;

import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.atributo.Attribute;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Relationship extends AttributableComponent implements Serializable {

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

        JMenuItem addComplexAttribute = new JMenuItem("Add attribute");
        addComplexAttribute.addActionListener(_ -> drawingPanel.getActioner().addComplexAttribute(this));

        relationPopupMenu.addOption(addComplexAttribute);
        relationPopupMenu.addOption(rename);
        relationPopupMenu.addOption(delete);

        return relationPopupMenu;

    }

    public void draw(Graphics2D g2) {

        // Cambia la fuente del texto
        g2.setFont(new Font("Verdana", Font.BOLD, 10));

        // Calcula el ancho del texto
        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        // Calcula la posición del texto para centrarlo en el recuadro
        int xTexto = x - anchoTexto / 2;
        int yTexto = y + altoTexto / 2;

        // Cambia el grosor del recuadro
        g2.setStroke(new BasicStroke(1));

        // Dibuja el recuadro de la entidad
        int margen = 20; // Margen alrededor del texto

        // Aplica suavizado a las líneas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibuja una línea desde el centro de la relación hasta el centro de cada entidad
        for (Dupla<Entity, Cardinality> dupla : this.entities) {

            Point closestPoint = dupla.getPrimero().getClosestPoint(new Point(x, y));

            // Dibujar la línea hacia el punto más cercano
            g2.drawLine(x, y, closestPoint.x, closestPoint.y);

            /*
            // Dibujar la cardinalidad en el punto intermedio
            dupla.getSegundo().dibujar(g2,
                    (closestPoint.x + x) / 2,
                    (closestPoint.y + y) / 2
            );

             */
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
            forma.addPoint(x, y - diagonalVertical / 2 + 8); // Punto superior
            forma.addPoint(x + diagonalHorizontal / 2, y); // Punto derecho
            forma.addPoint(x, y + diagonalVertical / 2 - 8); // Punto inferior
            forma.addPoint(x - diagonalHorizontal / 2, y); // Punto izquierdo
            formaDefinida = true;
        }

        // Cambia el color de dibujo basándote en si la entidad está seleccionada o no
        if (this.isSelected()) {
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(3));
        } else {
            g2.setColor(Color.BLACK);
        }

        // Dibuja el rombo
        g2.drawPolygon(forma);

        // Relleno el polígono
        g2.setColor(Color.WHITE);
        g2.fillPolygon(forma);

        g2.setColor(Color.BLACK);
        g2.drawString(this.getText(), xTexto, yTexto); // Dibuja el nombre de la relación

        // Libera los recursos utilizados por g2
        //g2.dispose();
    }

    public Rectangle getBounds() {
        // Calcula las coordenadas del punto superior izquierdo del rectángulo delimitador
        int rectX = this.x - diagonalHorizontal / 2;
        int rectY = this.y - diagonalVertical / 2;

        // Crea y devuelve el rectángulo delimitador
        return new Rectangle(rectX, rectY, diagonalHorizontal, diagonalVertical);
    }

    private void updateShape() {
        forma.reset();
        forma.addPoint(x, y - diagonalVertical / 2 + 10); // Punto superior
        forma.addPoint(x + diagonalHorizontal / 2, y); // Punto derecho
        forma.addPoint(x, y + diagonalVertical / 2 - 10); // Punto inferior
        forma.addPoint(x - diagonalHorizontal / 2, y); // Punto izquierdo
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
    public List<Component> getComponentsForRemoval() {

        List<Component> out = new ArrayList<>();

        out.add(this);

        return out;
    }

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

    @Override
    public int getAttributePosition(Attribute attribute) {
        return 0;
    }

    public void addEntity(Entity entity, Cardinality cardinality) {
        this.entities.add(new Dupla<>(entity, cardinality));
        entity.addRelationship(this);
        cardinality.setOwner(entity);
        cardinality.setRelationship(this);
    }
}