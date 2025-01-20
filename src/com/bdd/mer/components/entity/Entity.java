package com.bdd.mer.components.entity;

import com.bdd.mer.actions.Action;
import com.bdd.mer.components.attribute.Attribute;
import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.hierarchy.Hierarchy;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.components.relationship.relatable.Relatable;
import com.bdd.mer.components.relationship.relatable.RelatableImplementation;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Entity extends AttributableComponent implements Relatable {

    /**
     * {@code Entity}'s {@code RelationshipManager}.
     */
    private final RelatableImplementation relationshipsManager;

    /**
     * List of {@code Hierarchy} in which the {@code Entity} participates.
     */
    protected final List<Hierarchy> hierarchies;

    /**
     * Constructs an {@code Entity}.
     *
     * @param text {@code Entity}'s name.
     * @param x {@code Entity}'s X coordinate value in the {@code DrawingPanel}.
     * @param y {@code Entity}'s Y coordinate value in the {@code DrawingPanel}.
     * @param drawingPanel {@code DrawingPanel} where the {@code Entity} lives.
     */
    public Entity(String text, int x, int y, DrawingPanel drawingPanel) {
        super(text, x, y, drawingPanel);

        relationshipsManager = new RelatableImplementation();
        hierarchies = new ArrayList<>();

        setDrawingPriority(7);
    }

    /**
     * Fills the {@code Entity}'s shape.
     *
     * @param graphics2D Graphics context.
     * @param shape Shape of the {@code Entity}.
     */
    protected void fillShape(Graphics2D graphics2D, RoundRectangle2D shape) {

        graphics2D.setColor(Color.WHITE);
        graphics2D.fill(shape);

        this.setShape(shape);
    }

    /**
     * Adds a new {@code Hierarchy} to the {@code Entity}.
     *
     * @param newHierarchy New {@code Hierarchy} to be related.
     * @return {@code TRUE} if the {@code Hierarchy} was successfully added. {@code False} in any other case.
     */
    public boolean addHierarchy(Hierarchy newHierarchy) {

        if (newHierarchy.isChild(this)) {
            for (Hierarchy hierarchy : this.hierarchies) {
                if (hierarchy.isChild(this)) {
                    if (!hierarchy.getParent().hasAHierarchyInCommon(newHierarchy.getParent())) {
                        return false;
                    }
                }
            }
        }

        hierarchies.add(newHierarchy);
        return true;
    }

    /**
     * Removes a {@code Hierarchy} from the {@code Entity}.
     *
     * @param hierarchy {@code Hierarchy} to be removed.
     */
    public void removeHierarchy(Hierarchy hierarchy) {
        hierarchies.remove(hierarchy);
    }

    /**
     *
     * @return {@code TRUE} if the {@code Entity} is parent in any of their hierarchies.
     */
    public boolean isAlreadyParent() {

        for (Hierarchy hierarchy : this.hierarchies) {
            if (hierarchy.isParent(this)) {
                return true;
            }
        }

        return false;

    }

    /**
     * Checks if this entity and the one passed by parameter have a hierarchy in common.
     * <p></p>
     * This method is specially useful in the checking of multiple inheritance.
     *
     * @param entity Entity to be compared.
     * @return {@code TRUE} if they have a hierarchy in common.
     */
    private boolean hasAHierarchyInCommon(Entity entity) {

        for (Hierarchy hierarchy : this.hierarchies) {
            if (hierarchy.isChild(this) && hierarchy.isChild(entity)) {
                return true;
            }
        }

        return false;

    }

    /**
     *
     * @return A list containing all the hierarchical children of the entity.
     */
    private List<Entity> getHierarchicalChildren() {

        List<Entity> out = new ArrayList<>();

        for (Hierarchy hierarchy : this.hierarchies) {

            if (hierarchy.isParent(this)) {

                out.addAll(hierarchy.getChildren());

            }
        }

        return out;
    }

    /**
     * Checks if this entity and the one passed by parameter have a hierarchical child in common.
     * <p></p>
     * This method is specially useful in the checking of multiple inheritance.
     *
     * @param entity Entity to be compared.
     * @return {@code TRUE} if they have a hierarchical child in common.
     */
    public boolean shareHierarchicalChild(Entity entity) {

        List<Entity> thisEntityHierarchicalChildren = this.getHierarchicalChildren();
        List<Entity> secondEntityHierarchicalChildren = entity.getHierarchicalChildren();

        for (Entity child : thisEntityHierarchicalChildren) { // It could be optimized.

            if (secondEntityHierarchicalChildren.contains(child)) {
                return true;
            }

        }

        return false;

    }

    /**
     *
     * @param relationship {@code Relationship} from which the weak entity will be dependent.
     * @return A weak version of the current strong entity.
     */
    public WeakEntity getWeakVersion(Relationship relationship) {

        WeakEntity weakVersion = new WeakEntity(this.getText(), this.getX(), this.getY(), relationship, this.getPanelDibujo());

        this.copyAttributes(weakVersion);

        return weakVersion;

    }

    /**
     * Disclaimer: the word "attributes" refers to the OOP attributes.
     *
     * @param entity Entity where the attributes will be copied.
     * @see #getWeakVersion(Relationship)
     */
    protected void copyAttributes(Entity entity) {

        for (Relationship relationship : this.relationshipsManager.getRelationships()) {
            entity.addRelationship(relationship);
        }

        for (Hierarchy hierarchy : this.hierarchies) {
            entity.addHierarchy(hierarchy);
        }

        List<Attribute> attributes = this.getAttributes();

        for (Attribute attribute : attributes) {
            entity.addAttribute(attribute);
        }

        entity.setShape(this.getBounds());

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        // Calcula el ancho del texto
        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        // Calcula la posición del texto para centrarlo en el recuadro
        int xTexto = getX() - anchoTexto / 2;
        int yTexto = getY() + altoTexto / 2;

        // Cambia el grosor del recuadro.
        g2.setStroke(new BasicStroke(1));

        // Dibuja el recuadro de la entidad
        int margen = 10; // Margen alrededor del texto

        int rectX = getX() - anchoTexto / 2 - margen;
        int rectY = getY() - altoTexto / 2 - margen;
        int rectAncho = anchoTexto + 2 * margen;
        int rectAlto = altoTexto + 2 * margen;

        RoundRectangle2D shape = new RoundRectangle2D.Float(rectX, rectY, rectAncho, rectAlto, 10, 10);
        this.fillShape(g2, shape);

        // Name of the entity.
        g2.setColor(Color.BLACK);
        g2.drawString(super.getText(), xTexto, yTexto);

        // Cambia el color de dibujo basándote si la entidad está seleccionada o no
        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        g2.draw(shape);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(
                this,
                Action.ADD_COMPLEX_ATTRIBUTE,
                Action.ADD_REFLEXIVE_RELATIONSHIP,
                Action.RENAME,
                Action.DELETE
        );

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanPresence() {

        // This is important in case the relationship has three or more children.
        for (Relationship relationship : this.relationshipsManager.getRelationships()) {
            relationship.cleanRelatable(this);
        }

        // This is important in case the entity we want to delete is a child and
        // the hierarchy has three or more children.
        for (Hierarchy hierarchy : this.hierarchies) {
            hierarchy.cleanEntity(this);
        }

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Entity> getEntities() {

        List<Entity> out = new ArrayList<>();

        out.add(this);

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Component> getComponentsForRemoval() {

        List<Component> out = super.getComponentsForRemoval();

        // If a relationship has three or more participating entities, if I delete one, it can still exist.
        for (Relationship relationship : this.relationshipsManager.getRelationships()) {
            if (relationship.getNumberOfParticipants() <= 2) {
                out.addAll(relationship.getComponentsForRemoval());
                out.add(relationship);
            }
        }

        // If a hierarchy has three or more children, if I delete one, it can still exist.
        for (Hierarchy hierarchy : hierarchies) {
            if (hierarchy.isParent(this) || (hierarchy.isChild(this) && hierarchy.getNumberOfChildren() <= 2)) {
                out.addAll(hierarchy.getComponentsForRemoval());
                out.add(hierarchy);
            }
        }

        /*
        Whether an association where an entity participate must be deleted or not when the latter is deleted, it
        depends on the relationship forming the association.
         */

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void changeReference(Component oldComponent, Component newComponent) {
        // Do nothing.
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean hasMainAttribute() {

        List<Attribute> attributes = this.getAttributes();

        for (Attribute attribute : attributes) {
            if (attribute.isMain()) {
                return true;
            }
        }

        return false;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void addRelationship(Relationship relationship) {
        this.relationshipsManager.addRelationship(relationship);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void removeRelationship(Relationship relationship) {
        this.relationshipsManager.removeRelationship(relationship);
    }

}
