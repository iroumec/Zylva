package com.iroumec.bdd.eerd.entity;

import com.iroumec.core.Component;
import com.iroumec.bdd.EERDiagram;
import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.eerd.attribute.IdentifierAttributable;
import com.iroumec.bdd.eerd.hierarchy.Hierarchy;
import com.iroumec.bdd.eerd.relationship.Relationship;
import com.iroumec.bdd.eerd.relationship.Relatable;
import com.iroumec.userPreferences.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public final class EntityWrapper extends IdentifierAttributable implements Relatable {

    private static final int DRAWING_PRIORITY = 100;

    /**
     * Wrapped entity.
     */
    private Entity entity;

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
    }

    /**
     * Adds a new entity to the specified diagram.
     *
     * @param diagram {@code EERDiagram}.
     */
     public static void addEntity(EERDiagram diagram) {

        String name = EntityWrapper.getValidText(diagram);

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
     */
    public void addHierarchy(Hierarchy newHierarchy) {
        this.hierarchies.add(newHierarchy);
        this.subscribeTo(newHierarchy, Subscription.REFERENCE);
    }

    public boolean isThereMultipleInheritanceConflict(EntityWrapper parent) {

        // For each hierarchy in which the entity is a child, it will be checked
        // if it shares parent with the parent to be added.
        for (Hierarchy hierarchy : this.hierarchies) {
            if (hierarchy.isChild(this)) {
                if (!parent.isChildOfTheParentOf(hierarchy)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isChildOfTheParentOf(Hierarchy anotherHierarchy) {

        for (Hierarchy hierarchy : this.hierarchies) {
            if (hierarchy.isChild(this)) {
                if (hierarchy.isChild(anotherHierarchy)) {
                    return true;
                }
            }
        }

        return false;
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
        this.entity = new WeakEntity(this, relationship);
    }

    /**
     * Replace the wrapped entity with a strong version of itself.
     */
    public void setStrongVersion() {
        this.entity = new StrongEntity(this);
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

        JMenuItem item = new JMenuItem(LanguageManager.getMessage("action.addAttribute"));
        item.addActionListener(_ -> this.addAttribute());
        popupMenu.add(item);

        item = new JMenuItem(LanguageManager.getMessage("action.addReflexiveRelationship"));
        item.addActionListener(_ -> Relationship.addReflexiveRelationship((EERDiagram) this.diagram, this));
        popupMenu.add(item);

        //noinspection DuplicatedCode
        item = new JMenuItem(LanguageManager.getMessage("action.rename"));
        item.addActionListener(_ -> this.rename());
        popupMenu.add(item);

        item = new JMenuItem(LanguageManager.getMessage("action.delete"));
        item.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(item);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public int getDrawingPriority() { return DRAWING_PRIORITY; }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Rectangle> getAssociationBounds() {

        List<Rectangle> out = super.getAttributeBounds();

        out.add(this.getBounds());

        return out;
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
    public void cleanReferencesTo(Component component) {

        super.cleanReferencesTo(component);

        if (component instanceof Hierarchy hierarchy) {
            this.hierarchies.remove(hierarchy);
        }

        this.entity.cleanReferencesTo(component);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean hasMainAttribute() {

        if (super.hasMainAttribute()) {
            return true;
        }

        for (Hierarchy hierarchy : this.hierarchies) {
            if (hierarchy.isChild(this)) {
                return true;
            }
        }

        return false;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public String getIdentifier() { return this.getText(); }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Derivation> getDerivations() { return this.entity.getDerivations(); }
}
