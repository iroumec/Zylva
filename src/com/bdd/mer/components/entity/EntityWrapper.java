package com.bdd.mer.components.entity;

import com.bdd.mer.EERDiagram;
import com.bdd.mer.components.AttributableEERComponent;
import com.bdd.GUI.components.Component;
import com.bdd.mer.components.attribute.Attribute;
import com.bdd.mer.components.hierarchy.Hierarchy;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.components.relationship.relatable.Relatable;
import com.bdd.mer.components.relationship.relatable.RelatableImplementation;
import com.bdd.mer.derivation.derivationObjects.DerivationObject;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class EntityWrapper extends AttributableEERComponent implements Relatable {

    /**
     * Wrapped entity.
     */
    private Entity entity;

    /**
     * {@code Entity}'s {@code RelationshipManager}.
     */
    private final RelatableImplementation relationshipsManager;

    /**
     * List of hierarchies in which the entity participates.
     */
    private final List<Hierarchy> hierarchies;

    /**
     * Constructs an {@code EntityWrapper}.
     *
     * @param text Name of the entity.
     * @param x X coordinate of the entity.
     * @param y Y coordinate of the entity.
     * @param diagram {@code Diagram} in which the entity participates.
     */
    public EntityWrapper(String text, int x, int y, EERDiagram diagram) {
        super(text, x, y, diagram);
        this.hierarchies = new ArrayList<>();
        this.entity = new StrongEntity(this); // By default, a strong entity is created.
        this.relationshipsManager = new RelatableImplementation();
        setDrawingPriority(7);
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
    private boolean hasAHierarchyInCommon(EntityWrapper entity) {

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
    private List<EntityWrapper> getHierarchicalChildren() {

        List<EntityWrapper> out = new ArrayList<>();

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
    public boolean shareHierarchicalChild(EntityWrapper entity) {

        List<EntityWrapper> thisEntityHierarchicalChildren = this.getHierarchicalChildren();
        List<EntityWrapper> secondEntityHierarchicalChildren = entity.getHierarchicalChildren();

        for (EntityWrapper child : thisEntityHierarchicalChildren) { // It could be optimized.

            if (secondEntityHierarchicalChildren.contains(child)) {
                return true;
            }

        }

        return false;

    }


    /**
     * Replace the wrapped entity with a weak version of itself.
     *
     * @param relationship Relationship in which the {@code WeakEntity} is dependant of.
     */
    public void setWeakVersion(Relationship relationship) {

        if (this.entity.getClass() == StrongEntity.class) {
            this.entity = new WeakEntity(this, relationship);
        }
    }

    /**
     * Replace the wrapped entity with a strong version of itself.
     */
    public void setStrongVersion() {

        if (this.entity.getClass() == WeakEntity.class) {
            this.entity = new StrongEntity(this);
        }
    }

    public static void addEntity(EERDiagram diagram) {

        String name = getValidName(diagram);

        if (name == null) {
            return;
        }

        EntityWrapper entityWrapper = new EntityWrapper(
                name,
                diagram.getMouseX(),
                diagram.getMouseY(),
                diagram
        );

        diagram.addComponent(entityWrapper);
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
        this.entity.fillShape(g2, shape);

        // Name of the entity.
        g2.setColor(Color.BLACK);
        g2.drawString(super.getText(), xTexto, yTexto);

        // Cambia el color de dibujo basándote si la entidad está seleccionada o no
        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        g2.draw(shape);
        g2.draw(this.getShape());
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem item = new JMenuItem("action.addAttribute");
        item.addActionListener(_ -> this.addComplexAttribute());
        popupMenu.add(item);

        item = new JMenuItem("action.addReflexiveRelationship");
        item.addActionListener(_ -> Relationship.addReflexiveRelationship((EERDiagram) this.diagram, this));
        popupMenu.add(item);

        //noinspection DuplicatedCode
        item = new JMenuItem("action.rename");
        item.addActionListener(_ -> this.rename());
        popupMenu.add(item);

        item = new JMenuItem("action.delete");
        item.addActionListener(_ -> this.delete());
        popupMenu.add(item);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected void cleanReferencesTo(Component component) {

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void addRelationship(Relationship relationship) {

        if (this.entity.relationshipCanBeManipulated(relationship)) {
            this.relationshipsManager.addRelationship(relationship);
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void removeRelationship(Relationship relationship) {

        if (this.entity.relationshipCanBeManipulated(relationship)) {
            this.relationshipsManager.removeRelationship(relationship);
        } else {
            // The only case in which it could be false when removing is the case where the relationship to be removed
            // is the one a weakEntity is dependent of.
            this.setStrongVersion();
        }
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

            /*
        Whether an association where an entity participates must be deleted or not when the latter is deleted, it
        depends on the relationship forming the association.
         */

    @Override
    public String toString() {
        return this.getText();
    }

    @Override
    public String getIdentifier() {
        return this.getText();
    }

    @Override
    public List<DerivationObject> getDerivationObjects() {

        List<DerivationObject> out = new ArrayList<>(super.getDerivationObjects());
        out.addAll(this.entity.getDerivationObjects());

        return out;
    }

    @Override
    public void delete() {
        this.entity = null;
        this.diagram = null;
    }
}
