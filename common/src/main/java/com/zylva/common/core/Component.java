package com.zylva.common.core;

import com.zylva.common.userPreferences.LanguageManager;
import com.zylva.common.userPreferences.Multilingual;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.Serializable;
import java.util.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public abstract class Component implements Serializable, Deletable, Selectable, Multilingual {

    /**
     * A component subscribes in DELETION to another when the latter's elimination implies the former's elimination.
     * This is the subscription the component should choose if its life dependes on another one.
     * <p>
     * A component suscribes in REFERENCE to another when the latter's elimination implies the former cleaning its
     * references to the latter.
     */
    public enum Subscription {
        DELETION,
        REFERENCE
    }

    private final Map<Subscription, Set<Component>> subscribers;
    private final Map<Subscription, Set<Component>> subscriptions;

    /**
     * Regex that defines if the name is valid.
     * <p>
     * Save as a constant due to it doesn't change and the compile process is expensive.
     */
    private final static Pattern validNamePattern = Pattern
            .compile("^[a-zA-Z0-9_\\-áéíóúÁÉÍÓÚñÑ]+$");

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

    protected Component(int x, int y) {
        this("", x, y);
    }

    protected Component(String text) { this(text, 0, 0); }

    /**
     * Constructs a <code>Component</code>.
     *
     * @param text The text of the component.
     * @param x The x coordinate of the component in the drawing panel.
     * @param y The y coordinate of the component in the drawing panel.
     */
    protected Component(@NotNull String text, int x, int y) {
        this.selected = false;
        this.text = text;
        this.x = x;
        this.y = y;

        this.subscribers = new HashMap<>();
        this.subscribers.put(Subscription.DELETION, new HashSet<>());
        this.subscribers.put(Subscription.REFERENCE, new HashSet<>());

        this.subscriptions = new ConcurrentHashMap<>();
        this.subscriptions.put(Subscription.DELETION, new HashSet<>());
        this.subscriptions.put(Subscription.REFERENCE, new HashSet<>());
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                  Methods                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Subscribes to a component.
     *
     * @param component Component who will gain a subscription.
     * @param subscription Type of subscription.
     */
    public void subscribeTo(Component component, Subscription subscription) {

        // The subscription is added to the another component's subscribers.
        Set<Component> subscribers = component.subscribers.get(subscription);
        subscribers.add(this);

        // The subscription is added to this component's subscriptions.
        Set<Component> subscriptions = this.subscriptions.get(subscription);
        subscriptions.add(component);
    }

    /**
     * The component unsubscribes from all its suscriptions. This way, the component doesn't keep a reference
     * to this subscriber which it's already in the deletion process.
     */
    private void clearSubscriberFromSubscriptions() {

        this.subscriptions.values().forEach(subscriptions ->
            subscriptions.forEach(component ->
                component.removeSubscriber(this)
            )
        );
    }

    /**
     * Removes the subscriber from its subscribers.
     *
     * @param subscriber Subscriber to be removed.
     */
    private void removeSubscriber(Component subscriber) {

        this.subscribers.values().forEach(subscribers -> subscribers.remove(subscriber));
    }

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

        if (this.popupMenu == null) {
            this.popupMenu = this.getPopupMenu();
        }

        this.popupMenu.show(origin, x, y);
    }

    void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

    public Shape getShape() { return this.shape; }

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

    public void setDrawingPanel(Diagram diagram) {
        this.diagram = diagram;
    }

    /**
     * Renames the component.
     */
    public void rename() {

        String newText = Component.getValidText(this.diagram);

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
    protected static String getValidText(@NotNull Diagram diagram) {
        return getValidText(diagram, LanguageManager.getMessage("text.input"));
    }

    /**
     * It makes sure to return a non-empty name.
     *
     * @return {@code String} entered by the user.
     */
    protected static String getValidText(@NotNull Diagram diagram, @NotNull String message) {

        String name = JOptionPane.showInputDialog(
                diagram,
                null,
                message,
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
                LanguageManager.getMessage("delete.warning"),
                LanguageManager.getMessage("delete.title"),
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

            notifyDeletion(this);

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

        deletionList.forEach(component -> {

            component.subscribers.forEach((key, subscribers) ->

                    subscribers.forEach(subscriber -> {

                        // The subscriptions to the component from its subscribers are removed.
                        subscriber.subscriptions.values().forEach(subscriptions ->
                                subscriptions.remove(component)
                        );

                        if (key == Subscription.REFERENCE) {
                            subscriber.cleanReferencesTo(component);
                        }
                    })
            );

            component.clearSubscriberFromSubscriptions();

            component.subscribers.clear();
            component.subscriptions.clear();

            this.diagram.removeComponent(component);
        });

        this.diagram.repaint();
    }

    private void notifyDeletion(Component component) {

        Set<Component> subscribedToDeletion = this.subscribers.get(Subscription.DELETION);

        for (Component subscriptor : subscribedToDeletion) {

            // If it was not removed from the diagram already...
            if (this.diagram.existsComponent(subscriptor)) {
                subscriptor.notifyRemovingOf(component);
            }
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                 Abstract Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Draws the component.
     *
     * @param g2 Graphics context.
     */
    public abstract void draw(Graphics2D g2);

    /**
     *
     * @return The drawing priority of the component. The lower the number, the higher the priority.
     */
    public abstract int getDrawingPriority();

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void setSelected(boolean isSelected) { this.selected = isSelected; }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean isSelected() { return this.selected; }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void resetLanguage() {
        this.popupMenu = this.getPopupMenu();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public String toString() {

        if (this.text.isEmpty()) {
            return super.toString();
        } else {
            return this.text;
        }
    }
}
