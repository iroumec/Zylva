package com.bdd.mer.components.entity;

import com.bdd.mer.components.atributo.Attribute;
import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.hierarchy.Hierarchy;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Entity extends AttributableComponent {

    /* -------------------------------------------------------------------------------------------------------------- */

    protected final List<Relationship> relationships;
    protected final List<Hierarchy> hierarchies;

    /* -------------------------------------------------------------------------------------------------------------- */

    public Entity(String text) {
        this(text, 0, 0);
    }

    public Entity(String text, int x, int y) {
        super(text, x, y);

        relationships = new ArrayList<>();
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

        // Agrega una sombra suave
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(rectX + 4, rectY + 4, rectAncho, rectAlto, 15, 15);

        GradientPaint gp = new GradientPaint(
                rectX, rectY, new Color(240, 240, 240),
                rectX, rectY + rectAlto, new Color(210, 210, 210)
        );

        g2.setPaint(gp);
        g2.fillRoundRect(rectX, rectY, rectAncho, rectAlto, 2, 2);

        // Name of the entity.
        g2.setColor(Color.BLACK);
        g2.drawString(super.getText(), xTexto, yTexto);



        // Cambia el color de dibujo basándote si la entidad está seleccionada o no
        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        // Dibuja el rectángulo
        g2.drawRoundRect(rectX, rectY, rectAncho, rectAlto, 2, 2);
        this.setShape(new Rectangle(rectX, rectY, rectAncho, rectAlto));

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
        for (Relationship relationship : relationships) {
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
        for (Relationship relationship : this.relationships) {
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

    public void addRelationship(Relationship relationship) { this.relationships.add(relationship); }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void removeRelationship(Relationship relationship) { this.relationships.remove(relationship); }

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

    public Point getClosestPoint(Point point) {

        Rectangle bounds = this.getBounds();

        int minX = (int) bounds.getMinX();
        int maxX = (int) bounds.getMaxX();
        int minY = (int) bounds.getMinY();
        int maxY = (int) bounds.getMaxY();

        // Encontrar el punto más cercano en el perímetro del rectángulo
        int closestX = Math.max(minX, Math.min(point.x, maxX)); // Limitar x al rango del rectángulo
        int closestY = Math.max(minY, Math.min(point.y, maxY)); // Limitar y al rango del rectángulo

        return new Point(closestX, closestY);
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

        for (Relationship relationship : this.relationships) {
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
}
