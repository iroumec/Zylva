package com.bdd.GUI;

import com.bdd.GUI.components.Component;
import com.bdd.GUI.userPreferences.Preference;
import com.bdd.GUI.userPreferences.UserPreferences;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public abstract class Diagram extends JPanel {

    private List<Component> components = new ArrayList<>();
    private Component draggedComponent = null;
    private Set<Component> selectedComponents = new HashSet<>();
    private final Rectangle selectionArea;
    private int selectionAreaStartX, selectionAreaStartY;
    private boolean selectingArea;
    private JPopupMenu backgroundPopupMenu;
    private boolean antialiasing = true;

    /**
     * Mouse x and y coordinates. This is useful for when a combination of keys are pressed.
     */
    private int mouseX, mouseY;

    /**
     * Useful to save the difference between the component position and the mouse position.
     */
    private int offsetX, offsetY;

    /**
     * Constructs a {@code Diagram}.
     */
    public Diagram() {

        this.setOpaque(Boolean.TRUE);
        this.setBackground(Color.WHITE);

        selectionArea = new Rectangle(0, 0, 0, 0);

        this.backgroundPopupMenu = getBackgroundPopupMenu();

        this.setAntialiasing(UserPreferences.loadBooleanPreference(Preference.ANTIALIASING, true));

        this.initializeMouseListeners();
    }

    public abstract JPopupMenu getBackgroundPopupMenu();

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        // It draws the selection area.
        Graphics2D g2d = (Graphics2D) g;

        if (antialiasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        g2d.draw(selectionArea);

        for (Component component : this.components) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            component.draw(g2d);
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public boolean noComponenteThere(int x, int y) {

        for (Component component : this.components) {
            if (component.getBounds().contains((new Point(x, y)))) {
                return false;
            }
        }
        return true;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void selectComponents() {

        for (Component component : this.components) {

            if (component.canBeSelectedBySelectionArea() && selectionArea.getBounds().contains(new Point(component.getX(), component.getY()))) {

                // If the component is inside the selection area...
                selectedComponents.add(component);
                component.setSelected(Boolean.TRUE);
            }
        }

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void addComponent(@NotNull Component component) {
        int index = Collections.binarySearch(
                this.components,
                component,
                Comparator.comparing(Component::getDrawingPriority) // Cambiado aquí
        );
        if (index < 0) {
            index = -index - 1;
        }
        this.components.add(index, component);

        this.repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void removeComponent(@NotNull Component componentToRemove) {
        this.components.remove(componentToRemove);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * The drawing priority of the components may change dynamically. In that case, this method must be invoked to
     * sort the components.
     */
    public void sortComponents() {

        // The algorithm used is a Timsort, a combination of a Merge Sort and an Insertion Sort.
        this.components.sort(Comparator.comparing(Component::getDrawingPriority));
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<Component> getSelectedComponents() {
        return (new ArrayList<>(selectedComponents));
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void cleanSelectedComponents() {

        for (Component selectedComponent : selectedComponents) {
           selectedComponent.setSelected(Boolean.FALSE);
           repaint(selectedComponent.getBounds());
        }

        selectedComponents.clear();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void reset() {
        this.components = new ArrayList<>();
        this.draggedComponent = null;
        this.selectedComponents = new HashSet<>();
        this.repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public int getMouseX() {
        return this.mouseX;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public int getMouseY() {
        return this.mouseY;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<Component> getListComponents() { return new ArrayList<>(this.components); }

    public boolean existsComponent(String componentName) {

        for (Component component : this.components) {
            if (!component.getText().isEmpty() && component.getText().equals(componentName)) {
                return true;
            }
        }

        return false;
    }

    public void resetLanguage() {

        this.backgroundPopupMenu = this.getBackgroundPopupMenu();

        for (Component component : this.components) {
            component.resetLanguage();
        }
    }

    public Point getCenterOfComponents(List<Component> components) {

        if (components.isEmpty()) {
            return new Point(this.getMouseX(), this.getMouseY());
        }

        double sumX = 0, sumY = 0;

        for (Component component : components) {

            sumX += component.getX();
            sumY += component.getY();
        }

        // Calculate the average of the X and Y coordinates
        double centerX = sumX / components.size();
        double centerY = sumY / components.size();

        // Return the center as a Point object
        return new Point((int) centerX, (int) centerY);
    }

    public void setAntialiasing(boolean antialiasing) {
        this.antialiasing = antialiasing;
        UserPreferences.savePreference(Preference.ANTIALIASING, antialiasing);
        this.repaint();
    }

    public boolean isAntialiasingActive() {
        return this.antialiasing;
    }

    /**
     * Sets focus on the JComponent.
     *
     * @param component {@code JComponent} to be focused.
     */
    public void setFocus(JComponent component) {

        component.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                component.requestFocusInWindow();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                           Mouse Interactions Methods                                           */
    /* -------------------------------------------------------------------------------------------------------------- */

    private void initializeMouseListeners() {

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(e);
            }
        });
    }

    private void handleMousePressed(MouseEvent e) {

        if (noComponenteThere(e.getX(), e.getY()) && !e.isControlDown()) {
            startSelectionArea(e);
        } else {
            selectingArea = false;

            // Cuando se hace clic en el mouse, verifica si se ha seleccionado una entidad
            if (e.isControlDown()) {
                this.handleControlClick();
            } else {
                List<Component> components = getListComponents().reversed();
                for (Component component : components) {
                    if (component.getBounds().contains(e.getPoint())) {
                        draggedComponent = component;
                        offsetX = e.getX() - draggedComponent.getX();
                        offsetY = e.getY() - draggedComponent.getY();
                        draggedComponent.setSelected(Boolean.TRUE);
                        break;
                    }
                }
            }
            mostrarMenu(e);
        }
    }

    private void handleControlClick() {
        for (Component component : components.reversed()) {
            if (component.getBounds().contains(new Point(Diagram.this.mouseX, Diagram.this.mouseY))) {
                selectedComponents.add(component);
                component.setSelected(Boolean.TRUE);
                break;
            }
        }
    }

    private void mostrarMenu(MouseEvent e) {

        // If right click is pressed...
        if (e.isPopupTrigger()) {
            boolean componentClicked = false;

            for (Component component : getListComponents().reversed()) {
                if (component.getBounds().contains(e.getPoint())) {
                    cleanSelectedComponents();
                    selectedComponents.add(component);
                    component.showPopupMenu(e.getComponent(), e.getX(), e.getY());
                    componentClicked = Boolean.TRUE;
                    break;
                }
            }

            if (!componentClicked) {
                backgroundPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

    }

    private void handleMouseReleased(MouseEvent e) {
        // When the mouse is released, the selection finishes.
        if (selectionArea.width != 0 || selectionArea.height != 0) {
            selectComponents();
            selectionArea.setBounds(0, 0, 0, 0);
            repaint();
        } else {
            if (!e.isPopupTrigger()) {
                if (!e.isControlDown()) {
                    // If the mouse is released and the selection area is nonexistent.
                    cleanSelectedComponents();
                }
            }
        }

        // When the mouse is released, the component dragged is unselected and the dragging stops.
        if (draggedComponent != null) {
            draggedComponent.setSelected(Boolean.FALSE);
        }
        draggedComponent = null;
        repaint();

        mostrarMenu(e);

    }

    private void handleMouseDragged(MouseEvent e) {

        if (selectingArea) {
            updateSelectionArea(e);
        } else if (!e.isPopupTrigger() && draggedComponent != null) {
            dragComponent(e);
        }

        repaint();
    }

    private void handleMouseMoved(MouseEvent e) {
        if (Diagram.this.getBounds().contains(e.getX(), e.getY())) {
            Diagram.this.mouseX = e.getX();
            Diagram.this.mouseY = e.getY();
        }
    }

    private void startSelectionArea(MouseEvent e) {
        selectionAreaStartX = e.getX();
        selectionAreaStartY = e.getY();
        selectionArea.setBounds(selectionAreaStartX, selectionAreaStartY, 0, 0);
        selectingArea = true;
        repaint(selectionArea.getBounds());
    }

    private void updateSelectionArea(MouseEvent e) {
        selectionArea.setBounds(
                Math.min(e.getX(), selectionAreaStartX),
                Math.min(e.getY(), selectionAreaStartY),
                Math.abs(e.getX() - selectionAreaStartX),
                Math.abs(e.getY() - selectionAreaStartY)
        );
    }

    private void dragComponent(MouseEvent e) {
        // Offset is subtracted with the goal of making the animation smooth.
        draggedComponent.setX(e.getX() - offsetX);
        draggedComponent.setY(e.getY() - offsetY);
    }
}