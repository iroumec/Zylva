package com.iroumec.components;

import com.iroumec.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.util.regex.Pattern;

public abstract class Component implements Serializable, Deletable {

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                  Attributes                                                    */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Regex that defines if the name is valid.
     * <p>
     * Save as a constant due to it doesn't change and the compile process is expensive.
     */
    private final static Pattern validNamePattern = Pattern.compile("^[a-zA-Z0-9_-]+$");

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

    protected Component(String text) { this(text, 0, 0); }

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

    void setDiagram(Diagram diagram) {
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

        String newText = getValidName(this.diagram);

        if (newText != null) {

            this.setText(newText);
            this.diagram.repaint();
        }
    }

    /**
     * It makes sure to return a non-empty name.
     *
     * @return {@code String} entered by the user.
     */
    protected static String getValidName(Diagram diagram) {

        String name = JOptionPane.showInputDialog(
                diagram,
                null,
                LanguageManager.getMessage("text.input"),
                JOptionPane.QUESTION_MESSAGE
        );

        boolean nameIsEmpty = false;
        boolean nameIsDuplicated = false;
        boolean nameIsInvalid = false;

        if (name != null) {

            nameIsEmpty = name.trim().isEmpty();
            nameIsInvalid = !validNamePattern.matcher(name).matches();

            if (!nameIsEmpty && !nameIsInvalid) {
                nameIsDuplicated = diagram.existsComponent(name);
            }
        }

        while (name != null && (nameIsEmpty || nameIsDuplicated || nameIsInvalid)) {

            if (nameIsEmpty) {
                JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("text.warning.empty"));
            } else if (nameIsInvalid) {
                JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("text.warning.invalid"));
            } else {
                JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("text.warning.duplicated"));
            }

            name = JOptionPane.showInputDialog(
                    diagram,
                    null,
                    LanguageManager.getMessage("text.input"),
                    JOptionPane.QUESTION_MESSAGE
            );

            if (name != null) {
                nameIsEmpty = name.trim().isEmpty();
                nameIsInvalid = !validNamePattern.matcher(name).matches();

                if (!nameIsEmpty && !nameIsInvalid) {
                    nameIsDuplicated = diagram.existsComponent(name);
                }
            }
        }

        return name;
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

    private static int componentsBeingProcessed = 0;
    private static final List<Component> deletionList = new ArrayList<>();

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

        for (Component componentNotRemoved : this.diagram.getDiagramComponents()) {

            for (Component componentRemoved : deletionList) {

                componentNotRemoved.cleanReferencesTo(componentRemoved);
            }
        }

        this.diagram.repaint();
    }

    private void notifyRemoving(Component component) {

        List<Component> componentsToNotify = new ArrayList<>(this.diagram.getDiagramComponents());

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
