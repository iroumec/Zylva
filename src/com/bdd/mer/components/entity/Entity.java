package com.bdd.mer.components.entity;

import com.bdd.mer.components.atributo.Attribute;
import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.hierarchy.Hierarchy;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.components.relationship.relatable.Relatable;
import com.bdd.mer.components.relationship.relatable.RelatableImplementation;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Entity extends AttributableComponent implements Relatable {

    /* -------------------------------------------------------------------------------------------------------------- */

    private final RelatableImplementation relationshipsManager;
    protected final List<Hierarchy> hierarchies;

    /* -------------------------------------------------------------------------------------------------------------- */

    public Entity(String text) {
        this(text, 0, 0);
    }

    public Entity(String text, int x, int y) {
        super(text, x, y);

        relationshipsManager = new RelatableImplementation();
        hierarchies = new ArrayList<>();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected PopupMenu getGenericPopupMenu() {

        DrawingPanel drawingPanel = this.getPanelDibujo();
        PopupMenu entityPopupMenu = new PopupMenu(drawingPanel);

        JMenuItem renameEntity = new JMenuItem("Rename");
        renameEntity.addActionListener(_ -> drawingPanel.getActioner().renameComponent(this));

        JMenuItem deleteEntity = new JMenuItem("Delete");
        deleteEntity.addActionListener(_ -> drawingPanel.getActioner().deleteSelectedComponents());

        JMenuItem addComplexAttribute = new JMenuItem("Add attribute");
        addComplexAttribute.addActionListener(_ -> drawingPanel.getActioner().addComplexAttribute(this));

        entityPopupMenu.addOption(addComplexAttribute);
        entityPopupMenu.addOption(renameEntity);
        entityPopupMenu.addOption(deleteEntity);

        return entityPopupMenu;

    }

    /* -------------------------------------------------------------------------------------------------------------- */

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

        this.drawLinesToRelationships(g2, this.getX(), this.getY());

        RoundRectangle2D shape = new RoundRectangle2D.Float(rectX, rectY, rectAncho, rectAlto, 10, 10);

        g2.setColor(Color.WHITE);
        g2.fill(shape);

        // Name of the entity.
        g2.setColor(Color.BLACK);
        g2.drawString(super.getText(), xTexto, yTexto);

        // Cambia el color de dibujo basándote si la entidad está seleccionada o no
        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        g2.draw(shape);
        this.setShape(shape);

        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.BLACK);
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

        // If a relationship has three or more participating entities, if I delete one, it can still exists.
        for (Relationship relationship : this.relationshipsManager.getRelationships()) {
            if (relationship.getNumberOfEntities() <= 2) {
                out.add(relationship);
            }
        }

        // If a hierarchy has three or more children, if I delete one, it can still exists.
        for (Hierarchy hierarchy : hierarchies) {
            if (hierarchy.isParent(this) || (hierarchy.isChild(this) && hierarchy.getNumberOfChildren() <= 2)) {
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
    public void cleanPresence() {

        // This is important in case the relationship has three or more children.
        for (Relationship relationship : this.relationshipsManager.getRelationships()) {
            relationship.cleanEntity(this);
        }

        // This is important in case the entity we want to delete is a child and
        // the hierarchy has three or more children.
        for (Hierarchy hierarchy : this.hierarchies) {
            hierarchy.cleanEntity(this);
        }

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void changeReference(Entity oldComponent, Entity newComponent) {
        // Do nothing.
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /* -------------------------------------------------------------------------------------------------------------- */

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

    public void removeHierarchy(Hierarchy hierarchy) {
        hierarchies.remove(hierarchy);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                         Attribute Related Methods                                              */
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

    // -------------------------------------------------------------------------------------------------------------- //

    public WeakEntity getWeakVersion() {

        WeakEntity weakVersion = new WeakEntity(this.getText(), this.getX(), this.getY());

        this.copyAttributes(weakVersion);

        return weakVersion;

    }

    public boolean isAlreadyParent() {

        for (Hierarchy hierarchy : this.hierarchies) {
            if (hierarchy.isParent(this)) {
                return true;
            }
        }

        return false;

    }

    private boolean hasAHierarchyInCommon(Entity entity) {

        for (Hierarchy hierarchy : this.hierarchies) {
            if (hierarchy.isChild(this) && hierarchy.isChild(entity)) {
                return true;
            }
        }

        return false;

    }

    private List<Entity> getHierarchicalChildren() {

        List<Entity> out = new ArrayList<>();

        for (Hierarchy hierarchy : this.hierarchies) {

            if (hierarchy.isParent(this)) {

                out.addAll(hierarchy.getChilds());

            }
        }

        return out;
    }

    public boolean shareHierarchicalChild(Entity entiy) {

        List<Entity> thisEntityhierarchicalChildren = this.getHierarchicalChildren();
        List<Entity> secondEntityHierarchicalChildren = entiy.getHierarchicalChildren();

        for (Entity child : thisEntityhierarchicalChildren) { // It could be optimized.

            if (secondEntityHierarchicalChildren.contains(child)) {
                return true;
            }

        }

        return false;

    }

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

    @Override
    public void addRelationship(Relationship relationship) {
        this.relationshipsManager.addRelationship(relationship);
    }

    @Override
    public void removeRelationship(Relationship relationship) {
        this.relationshipsManager.removeRelationship(relationship);
    }

    @Override
    public void drawLinesToRelationships(Graphics2D graphics2D, int x, int y) {
        this.relationshipsManager.drawLinesToRelationships(graphics2D, x, y);
    }

}
