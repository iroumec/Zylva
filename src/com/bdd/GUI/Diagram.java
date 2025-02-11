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
import java.util.stream.Collectors;

public abstract class Diagram extends JPanel implements Cloneable {

    private List<com.bdd.GUI.components.Component> components = new ArrayList<>();
    private com.bdd.GUI.components.Component draggedComponent = null;
    private Set<com.bdd.GUI.components.Component> selectedComponents = new HashSet<>();
    private Rectangle selectionArea;
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

        for (com.bdd.GUI.components.Component component : this.components) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            component.draw(g2d);
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public boolean noComponenteThere(int x, int y) {

        for (com.bdd.GUI.components.Component component : this.components) {
            if (component.getBounds().contains((new Point(x, y)))) {
                return false;
            }
        }
        return true;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void selectComponents() {

        for (com.bdd.GUI.components.Component component : this.components) {

            if (component.canBeSelectedBySelectionArea() && selectionArea.getBounds().contains(new Point(component.getX(), component.getY()))) {

                // If the component is inside the selection area...
                selectedComponents.add(component);
                component.setSelected(Boolean.TRUE);
            }
        }

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void addComponent(@NotNull com.bdd.GUI.components.Component component) {
        int index = Collections.binarySearch(
                this.components,
                component,
                Comparator.comparing(com.bdd.GUI.components.Component::getDrawingPriority) // Cambiado aquí
        );
        if (index < 0) {
            index = -index - 1;
        }
        this.components.add(index, component);

        this.repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void removeComponent(@NotNull com.bdd.GUI.components.Component componentToRemove) {
        componentToRemove.cleanPresence();
        this.components.remove(componentToRemove);
        repaint(componentToRemove.getBounds());
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * The drawing priority of the components may change dynamically. In that case, this method must be invoked to
     * sort the components.
     */
    public void sortComponents() {

        // The algorithm used is a Timsort, a combination of a Merge Sort and an Insertion Sort.
        this.components.sort(Comparator.comparing(com.bdd.GUI.components.Component::getDrawingPriority));
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<com.bdd.GUI.components.Component> getSelectedComponents() {
        return (new ArrayList<>(selectedComponents));
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void cleanSelectedComponents() {

        for (com.bdd.GUI.components.Component selectedComponent : selectedComponents) {
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
        repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @SuppressWarnings("unchecked")
    // The class of the component being added to the list is checked, so the cast is safe.
    public <T extends com.bdd.GUI.components.Component> List<T> getSelectedComponentsByClass(Class<T> specificClass) {
        return (List<T>) this.selectedComponents.stream()
                .filter(e -> e.getClass().equals(specificClass))
                .collect(Collectors.toList());
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

    public List<com.bdd.GUI.components.Component> getListComponents() { return new ArrayList<>(this.components); }

    /* -------------------------------------------------------------------------------------------------------------- */

    @SafeVarargs
    public final boolean onlyTheseClassesAreSelected(Class<? extends com.bdd.GUI.components.Component>... classTypes) {
        // Use a Set for faster lookup.
        Set<Class<?>> allowedClasses = new HashSet<>(Arrays.asList(classTypes));

        // Check if all components are of the allowed types.
        for (com.bdd.GUI.components.Component component : this.selectedComponents) {
            if (!allowedClasses.contains(component.getClass())) {
                return false;
            }
        }

        return true;
    }



    public boolean isNumberOfSelectedComponentsBetween(int a, int b) {

        return this.selectedComponents.size() >= a && this.selectedComponents.size() <= b;

    }

    public boolean isNumberOfSelectedComponents(int n) {

        return this.selectedComponents.size() == n;

    }

    public boolean existsComponent(String componentName) {

        for (com.bdd.GUI.components.Component component : this.components) {
            if (!component.getText().isEmpty() && component.getText().equals(componentName)) {
                return true;
            }
        }

        return false;
    }

    public void resetLanguage() {

        this.backgroundPopupMenu = this.getBackgroundPopupMenu();

        for (com.bdd.GUI.components.Component component : this.components) {
            component.resetLanguage();
        }

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
                List<com.bdd.GUI.components.Component> components = getListComponents().reversed();
                for (com.bdd.GUI.components.Component component : components) {
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
        for (com.bdd.GUI.components.Component component : components.reversed()) {
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

            for (com.bdd.GUI.components.Component component : getListComponents().reversed()) {
                if (component.getBounds().contains(e.getPoint())) {
                    cleanSelectedComponents();
                    selectedComponents.add(component);
                    component.showPopupMenu(e.getComponent(), e.getX(), e.getY());
                    componentClicked = Boolean.TRUE;
                    //repaint();
                    break;
                }
            }

            if (!componentClicked) {
                backgroundPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                //repaint();
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

    public Point getCenterOfComponents(com.bdd.GUI.components.Component... components) {

        List<com.bdd.GUI.components.Component> componentsList = List.of(components);

        if (componentsList.isEmpty()) {
            return new Point(this.getMouseX(), this.getMouseY());
        }

        double sumX = 0, sumY = 0;

        for (com.bdd.GUI.components.Component component : componentsList) {

            sumX += component.getX();
            sumY += component.getY();
        }

        // Calculate the average of the X and Y coordinates
        double centerX = sumX / componentsList.size();
        double centerY = sumY / componentsList.size();

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
     * @return A deep copy of the diagram.
     */
    @Override
    public Diagram clone() {
        try {
            Diagram cloned = (Diagram) super.clone();
            cloned.components = new ArrayList<>();
            for (Component component : this.components) {
                cloned.components.add(component.clone());  // Clonamos las entidades
            }
            cloned.draggedComponent = this.draggedComponent;
            cloned.selectedComponents = new HashSet<>();
            cloned.selectionArea = this.selectionArea; // Must this be saved?
            cloned.selectionAreaStartX = this.selectionAreaStartX; // Must this be saved?
            cloned.selectionAreaStartY = this.selectionAreaStartY; // Must this be saved?
            cloned.selectingArea = this.selectingArea;
            cloned.backgroundPopupMenu = this.backgroundPopupMenu;
            cloned.antialiasing = this.antialiasing;
            return cloned;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
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
}