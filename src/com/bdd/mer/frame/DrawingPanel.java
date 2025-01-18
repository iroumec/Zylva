package com.bdd.mer.frame;

import com.bdd.mer.actions.ActionManager;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class DrawingPanel extends JPanel {

    private List<Component> components = new ArrayList<>();
    private ActionManager actionManager;
    private Component componenteArrastrada = null;
    private Set<Component> componentesSeleccionadas = new HashSet<>();
    private final Rectangle selectionArea;
    private int selectionAreaStartX, selectionAreaStartY;
    private boolean selectingArea;
    private JPopupMenu backgroundPopupMenu;

    /**
     * Mouse x and y coordinates. This is useful for when a combination of keys are pressed.
     */
    private int mouseX, mouseY;

    /**
     * Useful to save the difference between the component position and the mouse position.
     */
    private int offsetX, offsetY;

    public DrawingPanel() {

        this.setOpaque(Boolean.TRUE);
        this.setBackground(Color.WHITE);

        selectionArea = new Rectangle(0, 0, 0, 0);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (noComponenteThere(e.getX(), e.getY())) {
                    // The selection starts at the point where the mouse was pressed.
                    selectionAreaStartX = e.getX();
                    selectionAreaStartY = e.getY();
                    selectionArea.setBounds(selectionAreaStartX, selectionAreaStartY, 0, 0);
                    repaint();
                    selectingArea = true;
                } else {
                    selectingArea = false;
                }
            }

            public void mouseReleased(MouseEvent e) {
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
            }
        });

        // Events where the mouse moves.
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selectingArea) {
                    // The selection area size is updated according to the mouse dragging.
                    selectionArea.setBounds(
                            Math.min(e.getX(), selectionAreaStartX),
                            Math.min(e.getY(), selectionAreaStartY),
                            Math.abs(e.getX() - selectionAreaStartX),
                            Math.abs(e.getY() - selectionAreaStartY)
                    );
                    repaint();
                }
            }

            /**
             * This method gets the mouse position in the drawing panel.
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                if (DrawingPanel.this.getBounds().contains(e.getX(), e.getY())) {
                    DrawingPanel.this.mouseX = e.getX();
                    DrawingPanel.this.mouseY = e.getY();
                }
            }
        });

        backgroundPopupMenu = this.getBackgroundPopupMenu();

        // Agrega un controlador de eventos de mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Cuando se hace clic en el mouse, verifica si se ha seleccionado una entidad
                if (e.isControlDown()) {
                    for (Component component : components.reversed()) {
                        if (component.getBounds().contains(new Point(DrawingPanel.this.mouseX, DrawingPanel.this.mouseY))) {
                            componentesSeleccionadas.add(component);
                            component.setSelected(Boolean.TRUE);
                            break;
                        }
                    }

                } else {
                    List<Component> components = getListComponents().reversed();
                    for (Component component : components) {
                        if (component.getBounds().contains(e.getPoint())) {
                            componenteArrastrada = component;
                            offsetX = e.getX() - componenteArrastrada.getX();
                            offsetY = e.getY() - componenteArrastrada.getY();
                            componenteArrastrada.setSelected(Boolean.TRUE);
                            break;
                        }
                    }
                }
                mostrarMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // When the mouse is released, the component dragged is unselected and the dragging stops.
                if (componenteArrastrada != null) {
                    componenteArrastrada.setSelected(Boolean.FALSE);
                }
                componenteArrastrada = null;
                repaint();

                mostrarMenu(e);
            }

            private void mostrarMenu(MouseEvent e) {

                // If right click is pressed...
                if (e.isPopupTrigger()) {
                    boolean componentClicked = false;

                    for (Component component : getListComponents().reversed()) {
                        if (component.getBounds().contains(e.getPoint())) {
                            cleanSelectedComponents();
                            componentesSeleccionadas.add(component);
                            component.showPopupMenu(e.getComponent(), e.getX(), e.getY());
                            componentClicked = Boolean.TRUE;
                            repaint();
                            break;
                        }
                    }

                    if (!componentClicked) {
                        backgroundPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                        repaint();
                    }
                }

            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                // In case selection mode is not activated, the component is dragged with the mouse.
                if (!e.isControlDown() && componenteArrastrada != null) {

                    // Offset is subtracted with the objective of making the animation smooth.
                    componenteArrastrada.setX(e.getX() - offsetX);
                    componenteArrastrada.setY(e.getY() - offsetY);
                    repaint();
                }
            }
        });
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        // It draws the selection area.
        Graphics2D g2d = (Graphics2D) g;

        // Antialiasing in the lines.
        // Maybe it is a good idea to be able to deactivate or activate it.
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

            if (selectionArea.getBounds().contains(new Point(component.getX(), component.getY()))) {

                // If the component is inside the selection area...
                componentesSeleccionadas.add(component);
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
        componentToRemove.cleanPresence();
        this.components.remove(componentToRemove);
        repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void replaceComponent(@NotNull Component oldComponent, @NotNull Component newComponent) {

        if (this.components.contains(oldComponent)) {

            int oldComponentIndex = this.components.indexOf(oldComponent);

            this.components.set(oldComponentIndex, newComponent);

            this.components.remove(oldComponent);

            this.components.add(oldComponentIndex, newComponent);

        }

        for (Component component : components) {
            component.changeReference(oldComponent, newComponent);
        }

        this.repaint();

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<Component> getSelectedComponents() {
        return (new ArrayList<>(componentesSeleccionadas));
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void cleanSelectedComponents() {

        for (Component a : componentesSeleccionadas) {
           a.setSelected(Boolean.FALSE);
        }

        componentesSeleccionadas.clear();
        repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void reset() {
        this.components = new ArrayList<>();
        this.componenteArrastrada = null;
        this.componentesSeleccionadas = new HashSet<>();
        repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<Entity> getSelectedEntities() {

        List<Entity> out = new ArrayList<>();

        for (Component component : this.componentesSeleccionadas) {
            out.addAll(component.getEntities());
        }

        return out;
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

    /* -------------------------------------------------------------------------------------------------------------- */

    public ActionManager getActionManager() { return this.actionManager; }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void setActionManager(ActionManager actionManager) { this.actionManager = actionManager; }

    private JPopupMenu getBackgroundPopupMenu() {

        JPopupMenu backgroundPopupMenu = new JPopupMenu();

        JMenuItem addEntity = new JMenuItem(LanguageManager.getMessage("option.addEntity"));
        addEntity.addActionListener(_ -> this.getActionManager().addEntity());

        JMenuItem addRelationship = new JMenuItem(LanguageManager.getMessage("option.addRelationship"));
        addRelationship.addActionListener(_ -> this.getActionManager().addRelationship());

        JMenuItem addDependency = new JMenuItem(LanguageManager.getMessage("option.addDependency"));
        addDependency.addActionListener(_ -> this.getActionManager().addDependency());

        JMenuItem addHierarchy = new JMenuItem(LanguageManager.getMessage("option.addHierarchy"));
        addHierarchy.addActionListener(_ -> this.getActionManager().addHierarchy());

        JMenuItem addNote = new JMenuItem(LanguageManager.getMessage("option.addNote"));
        addNote.addActionListener(_ -> this.getActionManager().addNote());

        JMenuItem addAssociation = new JMenuItem(LanguageManager.getMessage("option.addAssociation"));
        addAssociation.addActionListener(_ -> this.getActionManager().addAssociation());

        backgroundPopupMenu.add(addEntity);
        backgroundPopupMenu.add(addRelationship);
        backgroundPopupMenu.add(addDependency);
        backgroundPopupMenu.add(addHierarchy);
        backgroundPopupMenu.add(addNote);
        backgroundPopupMenu.add(addAssociation);

        return backgroundPopupMenu;
    }

    @SafeVarargs
    public final boolean onlyTheseClassesAreSelected(Class<? extends Component>... classTypes) {
        // Use a Set for faster lookup.
        Set<Class<?>> allowedClasses = new HashSet<>(Arrays.asList(classTypes));

        // Check if all components are of the allowed types.
        for (Component component : this.componentesSeleccionadas) {
            if (!allowedClasses.contains(component.getClass())) {
                return false;
            }
        }

        return true;
    }



    public boolean isNumberOfSelectedComponentsBetween(int a, int b) {

        return this.componentesSeleccionadas.size() >= a && this.componentesSeleccionadas.size() <= b;

    }

    public boolean isNumberOfSelectedComponents(int n) {

        return this.componentesSeleccionadas.size() == n;

    }

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

}