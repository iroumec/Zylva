package com.bdd.mer.components;

import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.components.relationship.Duplex;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

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

    private static DrawingPanel drawingPanel;

    // PopupMenu of the Component.
    private PopupMenu popupMenu;

    public Component() {
        this("", 0, 0);
    }

    public Component(String text) { this(text, 0 , 0); }

    public Component(int x, int y) { this("", x, y); }

    public Component(String text, int x, int y)  {
        this.selected = false;
        this.text = text;
        this.x = x;
        this.y = y;

        this.setPopupMenu(this.getGenericPopupMenu());
    }

    protected abstract PopupMenu getGenericPopupMenu();

    public void setPopupMenu(PopupMenu popupMenu) { this.popupMenu = popupMenu; }

    public DrawingPanel getPanelDibujo() { return drawingPanel; }

    public static void setPanelDibujo(DrawingPanel drawingPanel) { Component.drawingPanel = drawingPanel; }

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
        this.popupMenu.setComponent(this);
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

    public Duplex<Integer, Integer> getFontMetrics(Graphics2D graphics2D) {

        FontMetrics fm = graphics2D.getFontMetrics();

        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        return new Duplex<>(anchoTexto, altoTexto);
    }

}
