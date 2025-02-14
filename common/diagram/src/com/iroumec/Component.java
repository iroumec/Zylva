package com.iroumec;

import com.iroumec.GUI.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

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
    protected Diagram diagram;

    /**
     * JPopupMenu of the component.
     */
    private JPopupMenu popupMenu;

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                Constructors                                                    */
    /* -------------------------------------------------------------------------------------------------------------- */

    protected Component() {
        this(0, 0);
    }

    /**
     * Constructs a <code>Component</code> with an empty text and coordinates in (0, 0). This constructor is useful
     * for those components which don't have a text nor their coordinates matter or are calculated.
     *
     * @param diagram The drawing panel where the component lives.
     */
    protected Component(@NotNull Diagram diagram) {
        this("", 0, 0, diagram);
    }

    protected Component(int x, int y) {
        this("", x, y);
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

    protected Component(@NotNull String text, int x, int y) {
        this.selected = false;
        this.text = text;
        this.x = x;
        this.y = y;

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
    protected void setSelected(boolean isSelected) { this.selected = isSelected; }

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

    private void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

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

    /**
     * Adds the new component to the same diagram as this component.
     *
     * @param component Component to be added.
     */
    protected void addComponent(@NotNull Component component) {
        this.diagram.addComponent(component);
        component.setDiagram(this.diagram);
    }

    /**
     * This way, the subclasses don't hace direct access to the diagram.
     *
     * @param component Component to be added.
     * @param diagram Diagram in which the component will be added.
     */
    protected static void addComponent(@NotNull Component component, @NotNull Diagram diagram) {
        diagram.addComponent(component);
        component.setDiagram(diagram);
    }

    /**
     *
     * @return {@code TRUE} if the components live in the same diagram and {@code FALSE} in any other case.
     */
    @SuppressWarnings("unused")
    protected static boolean liveInTheSameDiagram(@NotNull Component firstComponent,
                                                  @NotNull Component secondComponent) {

        return firstComponent.diagram.equals(secondComponent.diagram);
    }

    /**
     *
     * @return {@code TRUE} if the components have the same name.
     */

    @Override
    public String toString() {

        if (this.text.isEmpty()) {
            return super.toString();
        } else {
            return this.text;
        }
    }

    public void setDrawingPanel(Diagram diagram) {
        this.diagram = diagram;
    }

    /**
     * Renames the component.
     */
    public void rename() {

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

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                 Deletion Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Deletes the component and their close-related components.
     */
    public final void deleteWithConfirmation() {

        int confirmation = JOptionPane.showConfirmDialog(
                this.diagram,
                LanguageManager.getMessage("input.delete"),
                LanguageManager.getMessage("title.delete"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {

            this.setForDelete();
        }
    }

    /**
     * Cleans all the references to the component.
     * <p></p>
     * At the moment of implementing this method, it must be taken into consideration that the component attached
     * is no longer available in the diagram. So, all references to it must be eliminated.
     * <p></p>
     * If the component cannot exist after deleting all the references to the attached component,
     * the method {@code notifyRemovingOf()} has been bad implemented. This method shouldn't efectuate any
     * deletion for the correct working of the application.
     *
     * @param component Component no longer available in the diagram.
     */
    protected abstract void cleanReferencesTo(Component component);

    /**
     * The component notified handle the removing of the component attached in case of being related to it.
     * <p></p>
     * Despite the component notified depends in existence on the component attached in the notification,
     * it's not guaranteed that it will be removed until all related components have been analyzed. For that reason,
     * the component should not implement any change in its values at the moment of implementing this method.
     *
     * @param component Component attached. If everything goes okay, it will be removed from the diagram.
     */
    protected abstract void notifyRemovingOf(Component component);

    private static int componentsBeingProcessed = 0;
    private static final List<Component> deletionList = new ArrayList<>();

    protected abstract boolean canBeDeleted();

    /**
     * Each component knows when it must be deleted.
     * <p></p>
     * This method doesn't guarantee that the entity will be removed.
     */
    public final void setForDelete() {

        // In case of multiple references to a same component.
        if (deletionList.contains(this)) {
            return;
        }

        boolean startOfDeletion = (componentsBeingProcessed == 0);

        componentsBeingProcessed++;

        if (this.canBeDeleted()) {

            notifyRemoving(this);

            deletionList.add(this);

            componentsBeingProcessed--;
        }

        if (startOfDeletion) {

            if (componentsBeingProcessed == 0) {
                this.finishDeletion();
            } else {
                deletionList.clear();
            }
        }
    }

    private void finishDeletion() {

        for (Component component : deletionList) {
            this.diagram.removeComponent(component);
        }

        for (Component componentNotRemoved : this.diagram.getListComponents()) {

            for (Component componentRemoved : deletionList) {

                componentNotRemoved.cleanReferencesTo(componentRemoved);
            }
        }

        this.diagram.repaint();
    }

    private void notifyRemoving(Component component) {

        List<Component> componentsToNotify = new ArrayList<>(this.diagram.getListComponents());

        for (Component c : componentsToNotify) {

            // If it was not removed from the diagram already.
            if (this.diagram.existsComponent(c)) {
                c.notifyRemovingOf(component);
            }
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                 Abstract Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    // Maybe this should be moved to a class drawable.

    /**
     * Draws the component.
     *
     * @param g2 Graphics context.
     */
    public abstract void draw(Graphics2D g2);
}
