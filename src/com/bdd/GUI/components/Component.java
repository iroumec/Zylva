package com.bdd.GUI.components;

import com.bdd.GUI.Diagram;
import com.bdd.GUI.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.awt.*;
import java.util.Set;

public abstract class Component implements Serializable, Cloneable {

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
    protected Diagram diagram;

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
     * @param diagram The drawing panel where the component lives.
     */
    public Component(@NotNull Diagram diagram) {
        this("", 0, 0, diagram);
    }

    /**
     * Constructs a <code>Component</code> with coordinates in (0, 0). This constructor is useful for those components
     * which don't have a text.
     *
     * @param text The text of the component.
     * @param diagram The drawing panel where the component lives.
     */
    public Component(@NotNull String text, @NotNull Diagram diagram) { this(text, 0 , 0, diagram); }

    /**
     * Constructs a <code>Component</code> with an empty text. This constructor is useful for those components
     * which don't have a text.
     *
     * @param x The x coordinate of the component in the drawing panel.
     * @param y The y coordinate of the component in the drawing panel.
     * @param diagram The drawing panel where the component lives.
     */
    public Component(int x, int y, @NotNull Diagram diagram) { this("", x, y, diagram); }

    /**
     * Constructs a <code>Component</code>.
     *
     * @param text The text of the component.
     * @param x The x coordinate of the component in the drawing panel.
     * @param y The y coordinate of the component in the drawing panel.
     * @param diagram The drawing panel where the component lives.
     */
    public Component(@NotNull String text, int x, int y, @NotNull Diagram diagram)  {
        this.selected = false;
        this.text = text;
        this.x = x;
        this.y = y;

        this.diagram = diagram;
        this.popupMenu = this.getPopupMenu();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                  Method                                                        */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * @return A {@code JPopupMenu} loaded with the actions the component can do.
     */
    @SuppressWarnings("duplicated")
    protected abstract JPopupMenu getPopupMenu();

    /**
     * Resets the current {@code JPopupMenu}. This method is useful in cases where the {@code JPopupMenu} obtained
     * depends on another variable.
     */
    public void resetPopupMenu() { this.popupMenu = getPopupMenu(); }

    /**
     * @return The {@code Diagram} where the component lives.
     */
    public Diagram getPanelDibujo() { return this.diagram; }

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

    public void resetLanguage() {
        this.popupMenu = this.getPopupMenu();
    }

    public Shape getShape() { return this.shape; }

    public final int getDrawingPriority() {
        return this.drawingPriority;
    }

    public void setDrawingPriority(int priority) { this.drawingPriority = priority; }

    @Override
    public String toString() {
        return this.text;
    }

    public void setDrawingPanel(Diagram diagram) {
        this.diagram = diagram;
    }

    /**
     * Renames the component.
     */
    protected void rename() {

        String newText;

        do {

            newText= JOptionPane.showInputDialog(
                    this.diagram,
                    null,
                    LanguageManager.getMessage("input.newText"),
                    JOptionPane.QUESTION_MESSAGE
            );

            // "newText" can be null when the user pressed "cancel"
            if (newText != null && newText.isEmpty()) {
                JOptionPane.showMessageDialog(this.diagram, LanguageManager.getMessage("warning.oneCharacter"));
            }
        } while (newText != null && newText.isEmpty());

        // If "Cancel" was not pressed
        if (newText != null) {
            this.setText(newText);
            this.diagram.repaint();
        }
    }

    /**
     * Deletes the component and their close-related components.
     */
    protected void delete() {

        int confirmation = JOptionPane.showConfirmDialog(
                this.diagram,
                LanguageManager.getMessage("input.delete"),
                LanguageManager.getMessage("title.delete"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {

            Set<Component> componentsForRemoval = new HashSet<>(this.getComponentsForRemoval());
            componentsForRemoval.add(this);

            for (Component component : componentsForRemoval) {
                this.diagram.removeComponent(component);
            }
        }

        this.diagram.repaint();
    }

    @Override
    public Component clone() {
        try {
            Component clone = (Component) super.clone();
            clone.drawingPriority = this.getDrawingPriority();
            clone.selected = this.selected;
            clone.text = this.text;
            clone.x = this.x;
            clone.y = this.y;
            clone.shape = this.shape;
            clone.diagram = this.diagram;
            clone.popupMenu = this.getPopupMenu();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
