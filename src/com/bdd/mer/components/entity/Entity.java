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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entity extends AttributableComponent implements Serializable {

    // Atributos de la entidad.
    private Attribute mainAttribute;
    private final List<Attribute> alternativeAttributes;
    private final List<Attribute> commonAttributes;

    // Relaciones en las que participa.
    private List<Relationship> relationships;

    // Jerarquía en la que participa.
    private final List<Hierarchy> hierarchies;

    // Macro-Entidad en la que la entidad participa.
    private final List<MacroEntity> macroEntities;

    private boolean superTipo;
    private boolean subTipo;
    private Rectangle bounds = new Rectangle();

    /* -------------------------------------------------------------------------------------------------------------- */

    public Entity(String text) {
        this(text, 0, 0);
    }

    public Entity(String text, int x, int y) {
        super(text, x, y);

        alternativeAttributes = new ArrayList<>();
        commonAttributes = new ArrayList<>();
        relationships = new ArrayList<>();
        hierarchies = new ArrayList<>();
        macroEntities = new ArrayList<>();

        this.mainAttribute = null;
    }

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

        // Cambia la fuente del texto
        g2.setFont(new Font("Verdana", Font.BOLD, 10));

        // Calcula el ancho del texto
        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        // Calcula la posición del texto para centrarlo en el recuadro
        int xTexto = x - anchoTexto / 2;
        int yTexto = y + altoTexto / 2;

        // Cambia el grosor del recuadro.
        g2.setStroke(new BasicStroke(1));

        // Dibuja el recuadro de la entidad
        int margen = 10; // Margen alrededor del texto

        // Aplica suavizado a las líneas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rectX = x - anchoTexto / 2 - margen;
        int rectY = y - altoTexto / 2 - margen;
        int rectAncho = anchoTexto + 2 * margen;
        int rectAlto = altoTexto + 2 * margen;

        // Rellena el rectángulo
        g2.setColor(Color.WHITE);
        g2.fillRect(rectX, rectY, rectAncho, rectAlto);

        // Name of the entity.
        g2.setColor(Color.BLACK);
        g2.drawString(super.getText(), xTexto, yTexto);

        // Cambia el color de dibujo basándote si la entidad está seleccionada o no
        if (this.isSelected()) {
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(2));
        } else {
            g2.setColor(Color.BLACK);
        }

        // Dibuja el rectángulo
        g2.drawRect(rectX, rectY, rectAncho, rectAlto);
        this.bounds = new Rectangle(rectX, rectY, rectAncho, rectAlto);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public Rectangle getBounds() {
        return bounds;
        //return new Rectangle(x, y, ancho, alto);
    }

    @Override
    public List<Entity> getEntities() {

        List<Entity> out = new ArrayList<>();

        out.add(this);

        return out;
    }

    @Override
    public List<Component> getComponentsForRemoval() {

        List<Component> out = new ArrayList<>();

        out.addAll(this.relationships);
        out.addAll(this.hierarchies);
        out.addAll(this.macroEntities);
        out.addAll(super.getComponentsForRemoval());

        out.add(this);

        return out;
    }

    @Override
    public void cleanPresence() {

    }

    @Override
    public void changeReference(Entity oldComponent, Entity newComponent) {

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void addRelationship(Relationship relationship) { this.relationships.add(relationship); }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void removeRelationship(Relationship relationship) { this.relationships.remove(relationship); }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<Relationship> getRelaciones() { return this.relationships; }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<Hierarchy> getHeriarchiesList() { return this.hierarchies; }

    /* -------------------------------------------------------------------------------------------------------------- */

    public boolean isSuperTipo() {
        return superTipo;
    }

    public void setSuperTipo(boolean superTipo) {
        this.superTipo = superTipo;
    }

    public boolean isSubTipo() {
        return subTipo;
    }

    public void setSubTipo(boolean subTipo) {
        this.subTipo = subTipo;
    }

    public void addHierarchy(Hierarchy hierarchy) {
        hierarchies.add(hierarchy);
    }

    public void removeHierarchy(Hierarchy hierarchy) {
        hierarchies.remove(hierarchy);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                         Attribute Related Methods                                              */
    /* -------------------------------------------------------------------------------------------------------------- */

    public boolean hasMainAttribute() { return this.mainAttribute != null; }

    // -------------------------------------------------------------------------------------------------------------- //

    public void addCommonAttribute(Attribute attribute) {

        this.commonAttributes.add(attribute);

    }

    // -------------------------------------------------------------------------------------------------------------- //

    public void addAlternativeAttribute(Attribute attribute) {

        this.alternativeAttributes.add(attribute);

    }

    // -------------------------------------------------------------------------------------------------------------- //

    public void addMainAttribute(Attribute attribute) {

        if (hasMainAttribute()) {
            addAlternativeAttribute(this.mainAttribute);
        }

        this.mainAttribute = attribute;

    }

    // -------------------------------------------------------------------------------------------------------------- //

    public void removeAttribute(Attribute attribute) {

        if (this.hasMainAttribute()) {
            if (this.mainAttribute.equals(attribute)) {
                this.mainAttribute = null;
            }
        }

        this.alternativeAttributes.remove(attribute);
        this.commonAttributes.remove(attribute);

    }

    // -------------------------------------------------------------------------------------------------------------- //

    public List<Attribute> getAttributes() {

        List<Attribute> out = new ArrayList<>();

        if (this.hasMainAttribute()) {
            out.add(this.mainAttribute);
        }

        out.addAll(this.alternativeAttributes);
        out.addAll(this.commonAttributes);

        // Fix this
        out.addAll(super.getAttributes());

        return out;
    }

    // -------------------------------------------------------------------------------------------------------------- //

    public int getAttributePosition(Attribute attribute) {

        int out = 0;
        List<Attribute> attributes = this.getAttributes();

        for (Attribute attributeInEntity : attributes) {

            System.out.println(attributeInEntity.getText());
            System.out.println(attribute.getText());

            if (attributeInEntity.equals(attribute)) {
                return out;
            }

            out++;
            out += attributeInEntity.getNumberOfAttributes();

        }

        return -1;

    }

    protected void setBounds(Rectangle rectangle) { this.bounds = rectangle; }

    public WeakEntity getWeakVersion() {

        WeakEntity weakVersion = new WeakEntity(this.getText(), this.getX(), this.getY());

        for (Relationship relationship : this.relationships) {
            weakVersion.addRelationship(relationship);
        }

        for (Hierarchy hierarchy : this.hierarchies) {
            weakVersion.addHierarchy(hierarchy);
        }

        /*
        for (MacroEntity macroEntity : this.hierarchies) {
            weakVersion.addMacroEntity(macroEntity);
        }
        */

        List<Attribute> attributes = this.getAttributes();

        for (Attribute attribute : attributes) {
            weakVersion.addAttribute(attribute);
        }

        return weakVersion;

    }

    public Point getClosestPoint(Point point) {

        int minX = (int) this.bounds.getMinX();
        int maxX = (int) this.bounds.getMaxX();
        int minY = (int) this.bounds.getMinY();
        int maxY = (int) this.bounds.getMaxY();

        // Encontrar el punto más cercano en el perímetro del rectángulo
        int closestX = Math.max(minX, Math.min(point.x, maxX)); // Limitar x al rango del rectángulo
        int closestY = Math.max(minY, Math.min(point.y, maxY)); // Limitar y al rango del rectángulo

        return new Point(closestX, closestY);
    }
}
