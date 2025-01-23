package com.bdd.mer.components.hierarchy;

import com.bdd.mer.actions.Action;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.components.line.Line;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/*
En una jerarquía total, toda instancia del supertipo debe ser instancia también de alguno
de los subtipos.

Una jerarquía exclusiva se nota con una doble línea del supertipo al ícono de jerarquía.
Por otro lado, si la jerarquía es parcial, se utiliza una única línea.
 */

/*
En una jerarquía exclusiva, los ejemplares de los subtipos son conjuntos disjuntos (solo pueden
pertenecer a un subtipo a la vez).

Una jerarquía exclusiva se nota con la letra "d" (Disjunct), mientras que una jerarquía compartida
se nota con la letra "o" (Overlapping).
 */
public class Hierarchy extends Component {

    /**
     * The symbol of the hierarchy, also denoting its exclusivity.
     */
    private HierarchySymbol symbol;

    /**
     * The parent entity in the hierarchy.
     */
    private Entity parent;

    /**
     * The line to the parent.
     */
    private Line parentLine;

    /**
     * List of children entities in the hierarchy.
     */
    private final List<Entity> children;

    /**
     * Constructs a {@code Hierarchy}.
     *
     * @param symbol {@code Hierarchy}'s symbol, denoting its exclusivity.
     * @param parent {@code Hierarchy}'s parent entity.
     * @param drawingPanel {@code DrawingPanel} where the {@code Hierarchy} lives.
     */
    public Hierarchy(HierarchySymbol symbol, Entity parent, DrawingPanel drawingPanel) {

        super(parent.getX(), parent.getY() + 60, drawingPanel);

        this.symbol = symbol;
        this.parent = parent;
        this.children = new ArrayList<>();

        setDrawingPriority(5);
    }

    /**
     * Adds a child to the hierarchy.
     *
     * @param entity Child to be added.
     */
    public void addChild(Entity entity) {
        this.children.add(entity);
    }

    /**
     *
     * @return The parent of the hierarchy.
     */
    public Entity getParent() { return this.parent; }

    /**
     *
     * @return A list containing the children of the hierarchy.
     */
    public List<Entity> getChildren() { return new ArrayList<>(this.children); }

    /**
     *
     * @param entity Entity to be checked.
     * @return {@code TRUE} if the entity is a children in the hierarchy.
     */
    public boolean isChild(Entity entity) {
        return this.children.contains(entity);
    }

    /**
     *
     * @param entity Entity to be checked.
     * @return {@code TRUE} if the entity is the parent of the hierarchy.
     */
    public boolean isParent(Entity entity) {
        return this.parent.equals(entity);
    }

    /**
     *
     * @return The exclusivity of the hierarchy.
     */
    public HierarchySymbol getSymbol() { return this.symbol; }

    /**
     * Changes the hierarchy's exclusivity.
     *
     * @param symbol New exclusivity.
     */
    public void setSymbol(HierarchySymbol symbol) {
        this.symbol = symbol;
    }

    /**
     *
     * @return {@code TRUE} if the hierarchy is affected by multiple inheritance.
     */
    private boolean isThereMultipleInheritance() {

        for (Entity firstChild : this.children) {
            for (Entity secondChild : this.children) {
                if (!firstChild.equals(secondChild) && firstChild.shareHierarchicalChild(secondChild)) {
                    return true;
                }
            }
        }

        return false;

    }

    /**
     *
     * @return Number of children in the hierarchy.
     */
    public int getNumberOfChildren() {
        return this.children.size();
    }

    /**
     * Clean all the references to an entity.
     *
     * @param entity Entity to be cleaned.
     */
    public void cleanEntity(Entity entity) {

        if (!isParent(entity) && (!isChild(entity) || getNumberOfChildren() > 2)) {
            this.children.remove(entity);
        }

        // In other case, we don't have to do anything because, if cleanEntity was called, it is because
        // the entity will be eliminated and, so, the hierarchy also if it doesn't enter into the if statement's body.

    }

    /**
     * Sets the line to the parent.
     *
     * @param line New {@code Line} drawn to the parent.
     */
    public void setParentLine(Line line) {
        this.parentLine = line;
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(
                this,
                Action.DELETE,
                Action.SWAP_EXCLUSIVITY
        );
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(this.symbol.getSymbol());
        int textHeight = fm.getHeight();

        // The diameter is calculated according to the text size.
        int diameter = Math.max(textWidth, textHeight) + 10;

        // Radio of the oval.
        int radio = diameter / 2;

        // Child lines.
        for (Entity e : children) {
            g2.drawLine(this.getX(), this.getY(), e.getX(), e.getY());
        }

        Ellipse2D.Double ovalShape = new Ellipse2D.Double(
                this.getX() - radio,
                this.getY() - radio,
                diameter,
                diameter
        );
        this.setShape(ovalShape);

        g2.setColor(Color.WHITE);
        g2.fill(ovalShape);

        g2.setColor(Color.BLACK);

        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        g2.draw(ovalShape);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawString(this.symbol.getSymbol(), this.getX() - 4, this.getY() + 4);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanPresence() {

        this.parent.removeHierarchy(this);

        for (Entity entity : this.children) {
            entity.removeHierarchy(this);
        }

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void changeReference(Component oldComponent, Component newComponent) {

        if (newComponent instanceof Entity) {

            if (this.parent.equals(oldComponent)) {
                this.parent = (Entity) newComponent;
            } else {

                boolean removeOldEntity = false;

                for (Entity child : this.children) {
                    if (child.equals(oldComponent)) {
                        removeOldEntity = true;
                        break; // There will only be at most one coincidence.
                    }
                }

                if (removeOldEntity) {
                    this.children.remove((Entity) oldComponent);
                    this.children.add((Entity) newComponent);
                }

            }
        }

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean canBeDeleted() {

        if (isThereMultipleInheritance()) {

            JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.multipleInheritance"));
            return false;
        }

        return true;

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Component> getComponentsForRemoval() {

        List<Component> out = new ArrayList<>(this.parentLine.getComponentsForRemoval());

        out.add(this.parentLine);

        return out;
    }

}
