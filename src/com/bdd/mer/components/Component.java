package com.bdd.mer.components;

import com.bdd.mer.actions.ActionManager;
import com.bdd.mer.frame.DrawingPanel;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public abstract class Component implements Serializable {

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                  Attributes                                                    */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Drawing priority of the component.
     */
    private int drawingPriority = 0;

    /**
     * This attribute indicates if the component is being selected or not.
     */
    private boolean selected;

    /**
     * The text shown in the component.
     */
    private String text;

    /**
     * Position of the component.
     */
    private int x, y;

    /**
     * Shape of the component.
     */
    private Shape shape;

    /**
     * Drawing panel where the component lives.
     */
    private final DrawingPanel drawingPanel;

    /**
     * JPopupMenu of the component.
     */
    private JPopupMenu popupMenu;

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                Constructors                                                    */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Constructs a <code>Component</code> with an empty text and coordinates in (0, 0). This constructor is useful
     * for those components which don't have a text nor their coordinates matter or are calculated.
     *
     * @param drawingPanel The drawing panel where the component lives.
     */
    public Component(DrawingPanel drawingPanel) {
        this("", 0, 0, drawingPanel);
    }

    /**
     * Constructs a <code>Component</code> with coordinates in (0, 0). This constructor is useful for those components
     * which don't have a text.
     *
     * @param text The text of the component.
     * @param drawingPanel The drawing panel where the component lives.
     */
    public Component(String text, DrawingPanel drawingPanel) { this(text, 0 , 0, drawingPanel); }

    /**
     * Constructs a <code>Component</code> with an empty text. This constructor is useful for those components
     * which don't have a text.
     *
     * @param x The x coordinate of the component in the drawing panel.
     * @param y The y coordinate of the component in the drawing panel.
     * @param drawingPanel The drawing panel where the component lives.
     */
    public Component(int x, int y, DrawingPanel drawingPanel) { this("", x, y, drawingPanel); }

    /**
     * Constructs a <code>Component</code>.
     *
     * @param text The text of the component.
     * @param x The x coordinate of the component in the drawing panel.
     * @param y The y coordinate of the component in the drawing panel.
     * @param drawingPanel The drawing panel where the component lives.
     */
    public Component(String text, int x, int y, DrawingPanel drawingPanel)  {
        this.selected = false;
        this.text = text;
        this.x = x;
        this.y = y;

        this.drawingPanel = drawingPanel;
        this.popupMenu = this.getPopupMenu();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                  Method                                                        */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * @return A {@code JPopupMenu} loaded with the actions the component can do.
     */
    protected abstract JPopupMenu getPopupMenu();

    /**
     * Resets the current {@code JPopupMenu}. This method is useful in cases where the {@code JPopupMenu} obtained
     * depends on another variable.
     */
    public void resetPopupMenu() { this.popupMenu = getPopupMenu(); }

    /**
     * @return The {@code DrawingPanel} where the component lives.
     */
    public DrawingPanel getPanelDibujo() { return this.drawingPanel; }

    /**
     * Draws the component.
     *
     * @param g2 Graphics context.
     */
    public abstract void draw(Graphics2D g2);

    /**
     *
     * @return {@code Rectangle} containing the component.
     */
    public Rectangle getBounds() {

        if (this.shape == null) {
            return new Rectangle(x, y, 0, 0);
        }

        return this.shape.getBounds();
    }

    /**
     * Updates the {@code Shape} of the component.
     *
     * @param shape New {@code Shape}.
     */
    public void setShape(Shape shape) { this.shape = shape; }

    /**
     * Changes the selection state of the component.
     *
     * @param isSelected New selection state.
     */
    public void setSelected(boolean isSelected) { this.selected = isSelected; }

    /**
     *
     * @return {@code TRUE} if the entity is being selected.
     */
    protected boolean isSelected() { return this.selected; }

    /**
     * Updates the text of the component.
     *
     * @param text New text.
     */
    public void setText(String text) { this.text = text; }

    /**
     *
     * @return The text or name of the component.
     */
    public String getText() { return this.text; }

    /**
     * Updates the x coordinate value of the component.
     *
     * @param x New x coordinate value.
     */
    public void setX(int x) {

        if (x >= 0) {
            this.x = x;
        }

    }

    /**
     * Updates the y coordinate value of the component.
     *
     * @param y New y coordinate value.
     */
    public void setY(int y) {

        if (y >= 0) {
            this.y = y;
        }

    }

    /**
     *
     * @return X coordinate value of the component.
     */
    public int getX() { return this.x; }

    /**
     *
     * @return y coordinate value of the component.
     */
    public int getY() { return this.y; }

    /**
     * Shows the {@code JPopupMenu} of the component.
     *
     * @param origin {@code java.awt.Component} necessary to show the menu.
     * @param x X coordinate value where the {@code JPopupMenu} will be shown.
     * @param y Y coordinate value where the {@code JPopupMenu} will be shown.
     */
    public void showPopupMenu(java.awt.Component origin, int x, int y) {
        this.popupMenu.show(origin, x, y);
    }

    public List<Component> getComponentsForRemoval() {

        return new ArrayList<>();
    }

    public abstract void cleanPresence();

    // The color and the stroke are changed if the entity is selected.
    public void setSelectionOptions(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(120, 190, 235));
        graphics2D.setStroke(new BasicStroke(2));
    }

    public boolean canBeSelectedBySelectionArea() { return true; }

    public boolean canBeDeleted() { return true; }

    public ActionManager getActionManager() { return this.drawingPanel.getActionManager(); }

    public void resetLanguage() {
        this.popupMenu = this.getPopupMenu();
    }

    public Shape getShape() { return this.shape; }

    public final int getDrawingPriority() {
        return this.drawingPriority;
    }

    public void setDrawingPriority(int priority) { this.drawingPriority = priority; }
}
