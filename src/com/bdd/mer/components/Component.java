package com.bdd.mer.components;

import com.bdd.mer.actions.ActionManager;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.structures.Pair;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public abstract class Component implements Serializable {

    // Is the component being selected?
    private boolean selected;

    // Text shown in the component.
    private String text;

    // Position of the component.
    private int x, y;

    private Shape shape;

    private final DrawingPanel drawingPanel;

    // PopupMenu of the Component.
    private JPopupMenu popupMenu;

    public Component(DrawingPanel drawingPanel) {
        this("", 0, 0, drawingPanel);
    }

    public Component(String text, DrawingPanel drawingPanel) { this(text, 0 , 0, drawingPanel); }

    public Component(int x, int y, DrawingPanel drawingPanel) { this("", x, y, drawingPanel); }

    public Component(String text, int x, int y, DrawingPanel drawingPanel)  {
        this.selected = false;
        this.text = text;
        this.x = x;
        this.y = y;

        this.drawingPanel = drawingPanel;
        this.setPopupMenu(this.getPopupMenu());
    }

    protected abstract JPopupMenu getPopupMenu();

    public void setPopupMenu(JPopupMenu popupMenu) { this.popupMenu = popupMenu; }

    public DrawingPanel getPanelDibujo() { return this.drawingPanel; }

    public abstract void draw(Graphics2D g2);

    public Rectangle getBounds() {
        return this.shape.getBounds();
    }

    public void setShape(Shape shape) { this.shape = shape; }

    public void setSelected(boolean isSelected) { this.selected = isSelected; }

    public boolean isSelected() { return this.selected; }

    public void setText(String text) { this.text = text; }

    public String getText() { return this.text; }

    public void setX(int x) {

        if (x >= 0) {
            this.x = x;
        }

    }

    public void setY(int y) {

        if (y >= 0) {
            this.y = y;
        }

    }

    public int getX() { return this.x; }

    public int getY() { return this.y; }

    public void showPopupMenu(java.awt.Component origin, int x, int y) {
        this.popupMenu.show(origin, x, y);
    }

    public List<Entity> getEntities() { return new ArrayList<>(); }

    public List<Component> getComponentsForRemoval() {

        List<Component> out = new ArrayList<>();

        out.add(this);

        return out;
    }

    public abstract void cleanPresence();

    public abstract void changeReference(Component oldComponent, Component newComponent);

    // The color and the stroke are changed if the entity is selected.
    public void setSelectionOptions(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(120, 190, 235));
        graphics2D.setStroke(new BasicStroke(2));
    }

    public boolean canBeDeleted() { return true; }

    public Pair<Integer, Integer> getFontMetrics(Graphics2D graphics2D) {

        FontMetrics fm = graphics2D.getFontMetrics();

        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        return new Pair<>(anchoTexto, altoTexto);
    }

    public ActionManager getActionManager() { return this.drawingPanel.getActioner(); }

    public void resetLanguage() {
        this.popupMenu = this.getPopupMenu();
    }

    public Shape getShape() { return this.shape; }

}
