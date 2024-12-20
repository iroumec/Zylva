package com.bdd.mer.components;

import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public abstract class Component {

    // Option of the component.
    public List<String> options;

    // Is the component being selected?
    public boolean selected;

    // Text shown in the component.
    public String text;

    // Position of the component.
    public int x, y;

    private static DrawingPanel drawingPanel;

    // PopupMenu of the Component.
    PopupMenu popupMenu;

    public Component() {
        this("", 0, 0);
    }

    protected Component(String text, int x, int y)  {
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

    public abstract Rectangle getBounds();

    public void setSelected(boolean isSelected) { this.selected = isSelected; }

    public boolean isSelected() { return this.selected; }

    public void setText(String text) { this.text = text; }

    public String getText() { return this.text; }

    public void setX(int x) { this.x = x; }

    public void setY(int y) { this.y = y; }

    public int getX() { return this.x; }

    public int getY() { return this.y; }

    public void showPopupMenu(java.awt.Component origin, int x, int y) {
        this.popupMenu.setComponent(this);
        this.popupMenu.show(origin, x, y);
    }

    public List<Entity> getEntities() { return new ArrayList<>(); }

    public abstract List<Component> getComponentsForRemoval();

    public abstract void cleanPresence();

    public abstract void changeReference(Entity oldEntity, Entity newEntity);
}
