package com.iroumec.eerd.components.entity;

import com.iroumec.components.Component;
import com.iroumec.eerd.EERDiagram;
import com.iroumec.eerd.components.attribute.IdAttrEERComp;
import com.iroumec.eerd.components.hierarchy.Hierarchy;
import com.iroumec.eerd.components.relationship.Relationship;
import com.iroumec.eerd.components.relationship.relatable.Relatable;
import com.iroumec.eerd.components.relationship.relatable.RelatableImplementation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public final class EntityWrapper extends IdAttrEERComp implements Relatable {

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
     */
    private EntityWrapper(String text, int x, int y) {
        super(text, x, y);
        this.hierarchies = new ArrayList<>();
        this.entity = new StrongEntity(this); // By default, a strong entity is created.
        this.relationshipsManager = new RelatableImplementation();
        setDrawingPriority(7);
    }

    /**
     * Adds a new entity to the specified diagram.
     *
     * @param diagram {@code EERDiagram}.
     */
     public static void addEntity(EERDiagram diagram) {

        String name = getValidName(diagram);

        if (name == null) {
            return;
        }

        EntityWrapper entity = new EntityWrapper(
                name,
                diagram.getMouseX(),
                diagram.getMouseY()
        );

        Component.addComponent(entity, diagram);
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
                    if (hierarchy.parentsHaveHierarchyInCommon(newHierarchy)) {
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
    public boolean hasAHierarchyInCommon(EntityWrapper entity) {

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

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        int xText = getX() - anchoTexto / 2;
        int yText = getY() + altoTexto / 2;

        g2.setStroke(new BasicStroke(1));

        int margin = 10;

        int rectX = getX() - anchoTexto / 2 - margin;
        int rectY = getY() - altoTexto / 2 - margin;
        int rectAncho = anchoTexto + 2 * margin;
        int rectAlto = altoTexto + 2 * margin;

        RoundRectangle2D shape = new RoundRectangle2D.Float(rectX, rectY, rectAncho, rectAlto, 10, 10);
        this.entity.fillShape(g2, shape);

        g2.setColor(Color.BLACK);
        g2.drawString(super.getText(), xText, yText);

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
        item.addActionListener(_ -> this.addAttribute());
        popupMenu.add(item);

        item = new JMenuItem("action.addReflexiveRelationship");
        item.addActionListener(_ -> Relationship.addReflexiveRelationship((EERDiagram) this.diagram, this));
        popupMenu.add(item);

        //noinspection DuplicatedCode
        item = new JMenuItem("action.rename");
        item.addActionListener(_ -> this.rename());
        popupMenu.add(item);

        item = new JMenuItem("action.setForDelete");
        item.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(item);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected void notifyRemovingOf(Component component) {
        // Empty on purpose.
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
    protected void drawStartLineToAttribute(Graphics2D g2, Point textPosition) {

        Rectangle bounds = this.getBounds();

        int x = (int) bounds.getMaxX();

        g2.drawLine(x, textPosition.y, textPosition.x, textPosition.y);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected void cleanReferencesTo(Component component) {

        super.cleanReferencesTo(component);

        if (component instanceof Relationship relationship) {
            this.removeRelationship(relationship);
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public String getIdentifier() {
        return this.getText();
    }
}
