package com.bdd.mer.components.relationship;

import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.components.relationship.cardinality.Cardinality;
import com.bdd.mer.components.relationship.relatable.Relatable;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.actions.Action;
import com.bdd.mer.structures.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Relationship extends AttributableComponent {

    private final List<Pair<Relatable, Cardinality>> participants; // I use a Pair and not a HashMap because I cannot change dynamically an entity in it
    private int horizontalDiagonal, verticalDiagonal; // Posición del centro del rombo
    private final Polygon forma;

    public Relationship(String text, int x, int y, DrawingPanel drawingPanel) {
        super(text, x, y, drawingPanel);

        this.participants = new ArrayList<>();

        forma = new Polygon();
    }

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(
                this,
                Action.ADD_ATTRIBUTE,
                Action.ADD_ASSOCIATION,
                Action.RENAME,
                Action.DELETE
        );

    }

    public void draw(Graphics2D g2) {

        Pair<Integer, Integer> fontMetrics = this.getFontMetrics(g2);

        int anchoTexto = fontMetrics.getFirst();
        int altoTexto = fontMetrics.getSecond();

        // Posición centrada del texto en la diagonal del rombo
        int xTexto = getX() - anchoTexto / 2;
        int yTexto = getY() + altoTexto / 4; // Dividir alto por 4 para compensar el baseline del texto

        // Cambia el grosor del recuadro
        g2.setStroke(new BasicStroke(1));

        // Dibuja el recuadro de la entidad
        int margen = 15; // Margen alrededor del texto

        // It is not necessary to do this all the time. Only if the text is changed.
        this.updateDiagonals(anchoTexto, altoTexto, margen);

        forma.reset();
        forma.addPoint(getX(), getY() - verticalDiagonal / 2); // Punto superior
        forma.addPoint(getX() + horizontalDiagonal / 2, getY()); // Punto derecho
        forma.addPoint(getX(), getY() + verticalDiagonal / 2); // Punto inferior
        forma.addPoint(getX() - horizontalDiagonal / 2, getY()); // Punto izquierdo

        // Rellena el polígono
        g2.setColor(Color.WHITE);
        g2.fillPolygon(forma);

        // Dibuja el texto centrado
        g2.setColor(Color.BLACK);
        g2.drawString(this.getText(), xTexto, yTexto);

        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        // Dibuja el rombo
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

        // We break the bound between the relationship and their participants.
        for (Pair<Relatable, Cardinality> pair : this.participants) {
            pair.getFirst().removeRelationship(this);
        }

    }

    @Override
    public void changeReference(Component oldComponent, Component newComponent) {

        if (oldComponent instanceof Relatable && newComponent instanceof Relatable) {

            for (Pair<Relatable, Cardinality> pair : this.participants) {

                if (pair.getFirst().equals(oldComponent)) {
                    pair.setFirst((Relatable) newComponent);
                }

            }

        }

    }

    public void addParticipant(Relatable relatableComponent, Cardinality cardinality) {
        this.participants.add(new Pair<>(relatableComponent, cardinality));
        relatableComponent.addRelationship(this);
        cardinality.setOwner(relatableComponent);
        cardinality.setRelationship(this);
    }

    public void removeEntity(Entity entity) {

        for (Pair<Relatable, Cardinality> pair : this.participants) {
            if (pair.getFirst().equals(entity)) {
                this.participants.remove(pair);
                break;
            }
        }

    }

    public List<Component> getRelatedComponents() {

        List<Component> out = new ArrayList<>(this.getAttributes());

        for (Pair<Relatable, Cardinality> pair : this.participants) {

            if (pair.getFirst() instanceof Component relatable) {

                out.add(relatable);

                if (relatable instanceof AttributableComponent attributable) {

                    out.addAll(attributable.getAttributes());
                }

                out.add(pair.getSecond());

            }
        }

        return out;

    }

    public int getNumberOfEntities() {
        return this.participants.size();
    }

    public void cleanEntity(Entity entity) {

        if (getNumberOfEntities() > 2) {
            this.removeEntity(entity);
        }

        // In other case, we don't have to do anything because, if cleanEntity was called, it is because
        // the entity will be eliminated and, so, the relationship also if it doesn't enter the if statement's body.

    }

    public void updateDiagonals(int textWidth, int textHeight, int margin) {
        horizontalDiagonal = textWidth + 2 * margin; // Diagonal horizontal basada en el ancho del texto
        verticalDiagonal = textHeight + 2 * margin; // Diagonal vertical basada en el alto del texto
    }

    @Override
    public List<Component> getComponentsForRemoval() {

        List<Component> out = super.getComponentsForRemoval();

        for (Pair<Relatable, Cardinality> pair : this.participants) {
            out.add(pair.getSecond());
        }

        return out;
    }
}