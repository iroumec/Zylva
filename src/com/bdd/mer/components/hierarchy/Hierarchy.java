package com.bdd.mer.components.hierarchy;

import com.bdd.mer.actions.Action;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.EntityWrapper;
import com.bdd.mer.components.line.Line;
import com.bdd.mer.derivation.Derivable;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.userPreferences.LanguageManager;

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
public class Hierarchy extends Component implements Derivable {

    /**
     * The symbol of the hierarchy, also denoting its exclusivity.
     */
    private HierarchySymbol symbol;

    /**
     * The parent entity in the hierarchy.
     */
    private final EntityWrapper parent;

    /**
     * The line to the parent.
     */
    private Line parentLine;

    /**
     * List of children entities in the hierarchy.
     */
    private final List<EntityWrapper> children;

    /**
     * Constructs a {@code Hierarchy}.
     *
     * @param symbol {@code Hierarchy}'s symbol, denoting its exclusivity.
     * @param parent {@code Hierarchy}'s parent entity.
     * @param drawingPanel {@code DrawingPanel} where the {@code Hierarchy} lives.
     */
    public Hierarchy(HierarchySymbol symbol, EntityWrapper parent, DrawingPanel drawingPanel) {

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
    public void addChild(EntityWrapper entity) {
        this.children.add(entity);
    }

    /**
     *
     * @return The parent of the hierarchy.
     */
    public EntityWrapper getParent() { return this.parent; }

    /**
     *
     * @return A list containing the children of the hierarchy.
     */
    public List<EntityWrapper> getChildren() { return new ArrayList<>(this.children); }

    /**
     *
     * @param entity Entity to be checked.
     * @return {@code TRUE} if the entity is a children in the hierarchy.
     */
    public boolean isChild(EntityWrapper entity) {
        return this.children.contains(entity);
    }

    /**
     *
     * @param entity Entity to be checked.
     * @return {@code TRUE} if the entity is the parent of the hierarchy.
     */
    public boolean isParent(EntityWrapper entity) {
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

        for (EntityWrapper firstChild : this.children) {
            for (EntityWrapper secondChild : this.children) {
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
    public void cleanEntity(EntityWrapper entity) {

        if (!isParent(entity) && (!isChild(entity) || getNumberOfChildren() > 2)) {
            this.children.remove(entity);
        }

        // In another case, we don't have to do anything because, if cleanEntity was called, it is because
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
        int textWidth = fm.stringWidth(this.symbol.toString());
        int textHeight = fm.getHeight();

        // The diameter is calculated according to the text size.
        int diameter = Math.max(textWidth, textHeight) + 10;

        // Radio of the oval.
        int radio = diameter / 2;

        // Child lines.
        for (EntityWrapper e : children) {
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
        g2.drawString(this.symbol.toString(), this.getX() - 4, this.getY() + 4);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanPresence() {

        this.parent.removeHierarchy(this);

        for (EntityWrapper entity : this.children) {
            entity.removeHierarchy(this);
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

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public String parse() {

        StringBuilder out = new StringBuilder(this.getClass() + "[" + this.parent.getText() + "](");

        for (EntityWrapper child : this.children) {
            out.append(child.getText()).append(",");
        }

        return out + ")";
    }

    @Override
    public String getIdentifier() {
        return "";
    }
}
