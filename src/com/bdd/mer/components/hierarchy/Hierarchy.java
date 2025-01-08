package com.bdd.mer.components.hierarchy;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Hierarchy extends Component {

    private int radio; // Centro del óvalo
    private HierarchyType exclusivity;
    private Entity parent;
    private final List<Entity> childs;

    public Hierarchy(HierarchyType exclusivity, Entity parent) {

        super(exclusivity.getSymbol(), parent.getX(), parent.getY() + 60);

        this.exclusivity = exclusivity;
        this.parent = parent;
        this.childs = new ArrayList<>();
    }

    public void addChild(Entity entity) {
        this.childs.add(entity);
    }

    @Override
    protected PopupMenu getGenericPopupMenu() {

        DrawingPanel drawingPanel = this.getPanelDibujo();
        PopupMenu hierarchyPopupMenu = new PopupMenu(drawingPanel);

        JMenuItem deleteHierarchy = new JMenuItem("Delete");
        deleteHierarchy.addActionListener(_ -> drawingPanel.getActioner().deleteSelectedComponents());

        JMenuItem swapExclusivity = new JMenuItem("Swap exclusivity");
        swapExclusivity.addActionListener(_ -> drawingPanel.getActioner().swapExclusivity(this));

        hierarchyPopupMenu.addOption(deleteHierarchy);
        hierarchyPopupMenu.addOption(swapExclusivity);

        return hierarchyPopupMenu;

    }

    public void draw(Graphics2D g2) {

        // Obtengo la fuente del texto y calculo su tamaño
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(this.getText());
        int textHeight = fm.getHeight();

        // Calcula el diámetro del círculo basado en el tamaño del texto
        int diameter = Math.max(textWidth, textHeight) + 10;
        this.radio = diameter/2;

        // Aplica suavizado a las líneas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawSuperentityLine(g2);

        // Dibuja las líneas a los subtipos
        for (Entity e : childs) {
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
        g2.drawString(this.getText(), this.getX() - 4, this.getY() + 4);
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

    Una jerarquía exclusiva se nota con la letra "d" (Disjunt), mientras que una jerarquía compartida
    se nota con la letra "o" (Overlapping).
     */

    protected void drawSuperentityLine(Graphics2D g2) {
        g2.drawLine(this.getX(), this.getY(), parent.getX(), parent.getY());
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX() - radio, getY() - radio, radio * 2, radio * 2);
    }

    @Override
    public void cleanPresence() {

        this.parent.removeHierarchy(this);

        for (Entity entity : this.childs) {
            entity.removeHierarchy(this);
        }

    }

    @Override
    public void changeReference(Entity oldEntity, Entity newEntity) {

        if (this.parent.equals(oldEntity)) {
            this.parent = newEntity;
        }

        boolean removeOldEntity = false;

        for (Entity child : this.childs) {
            if (child.equals(oldEntity)) {
                removeOldEntity = true;
                break; // There will only be at most one coincidence.
            }
        }

        if (removeOldEntity) {
            this.childs.remove(oldEntity);
            this.childs.add(newEntity);
        }

    }

    public Entity getParent() { return this.parent; }

    public List<Entity> getChilds() { return new ArrayList<>(this.childs); }

    public boolean isChild(Entity entity) {
        return this.childs.contains(entity);
    }

    public boolean isParent(Entity entity) {
        return this.parent.equals(entity);
    }

    public boolean canBeDeleted() {

        if (isThereMultipleInheritance()) {

            JOptionPane.showMessageDialog(null, "La jerarquía no puede eliminarse puesto a que existe una herencia múltiple que depende de ella.");
            return false;
        }

        return true;

    }

    public HierarchyType getExclusivity() { return this.exclusivity; }

    public void setExclusivity(HierarchyType exclusivity) {
        this.exclusivity = exclusivity;
        this.setText(exclusivity.getSymbol());
    }

    private boolean isThereMultipleInheritance() {

        for (Entity firstChild : this.childs) {
            for (Entity secondChild : this.childs) {
                if (!firstChild.equals(secondChild) && firstChild.shareHierarchicalChild(secondChild)) {
                    return true;
                }
            }
        }

        return false;

    }

    public int getNumberOfChildren() {
        return this.childs.size();
    }

    public void cleanEntity(Entity entity) {

        if (!isParent(entity) && (!isChild(entity) || getNumberOfChildren() > 2)) {
            this.childs.remove(entity);
        }

        // In other case, we don't have to do anything because, if cleanEntity was called, it is because
        // the entity will be eliminated and, so, the hierarchy also if it doesn't enter in the if body.

    }

}
