package com.bdd.mer.components.hierarchy;

import com.bdd.mer.actions.Action;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Hierarchy extends Component {

    private int radio; // Centro del óvalo
    private HierarchyExclusivity exclusivity;
    private Entity parent;
    private final List<Entity> children;

    public Hierarchy(HierarchyExclusivity exclusivity, Entity parent, DrawingPanel drawingPanel) {

        super(parent.getX(), parent.getY() + 60, drawingPanel);

        this.exclusivity = exclusivity;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public void addChild(Entity entity) {
        this.children.add(entity);
    }

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(
                this,
                Action.DELETE,
                Action.SWAP_EXCLUSIVITY
        );
    }

    public void draw(Graphics2D g2) {

        // Obtengo la fuente del texto y calculo su tamaño
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(this.exclusivity.getSymbol());
        int textHeight = fm.getHeight();

        // Calcula el diámetro del círculo basado en el tamaño del texto
        int diameter = Math.max(textWidth, textHeight) + 10;
        this.radio = diameter/2;

        // Aplica suavizado a las líneas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawParentLine(g2);

        // Child lines.
        for (Entity e : children) {
            g2.drawLine(this.getX(), this.getY(), e.getX(), e.getY());
        }

        // Rellena el círculo
        g2.setColor(Color.WHITE);
        g2.fillOval(this.getX() - radio, this.getY() - radio, diameter, diameter);

        g2.setColor(Color.BLACK);

        // Si la jerarquía es seleccionada, se pinta de CYAN
        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        // Dibuja el círculo adaptado al tamaño del texto
        g2.drawOval(this.getX() - radio, this.getY() - radio, diameter, diameter);

        // Dibuja el texto dentro del círculo
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawString(this.exclusivity.getSymbol(), this.getX() - 4, this.getY() + 4);
    }

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

    protected void drawParentLine(Graphics2D g2) {
        g2.drawLine(this.getX(), this.getY(), parent.getX(), parent.getY());
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX() - radio, getY() - radio, radio * 2, radio * 2);
    }

    @Override
    public void cleanPresence() {

        this.parent.removeHierarchy(this);

        for (Entity entity : this.children) {
            entity.removeHierarchy(this);
        }

    }

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

    public Entity getParent() { return this.parent; }

    public List<Entity> getChildren() { return new ArrayList<>(this.children); }

    public boolean isChild(Entity entity) {
        return this.children.contains(entity);
    }

    public boolean isParent(Entity entity) {
        return this.parent.equals(entity);
    }

    public boolean canBeDeleted() {

        if (isThereMultipleInheritance()) {

            JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.multipleInheritance"));
            return false;
        }

        return true;

    }

    public HierarchyExclusivity getExclusivity() { return this.exclusivity; }

    public void setExclusivity(HierarchyExclusivity exclusivity) {
        this.exclusivity = exclusivity;
    }

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

    public int getNumberOfChildren() {
        return this.children.size();
    }

    public void cleanEntity(Entity entity) {

        if (!isParent(entity) && (!isChild(entity) || getNumberOfChildren() > 2)) {
            this.children.remove(entity);
        }

        // In other case, we don't have to do anything because, if cleanEntity was called, it is because
        // the entity will be eliminated and, so, the hierarchy also if it doesn't enter into the if statement's body.

    }

}
