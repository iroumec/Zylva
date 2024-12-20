package com.bdd.mer.frame;

import com.bdd.mer.actions.Actioner;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.components.entity.MacroEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class DrawingPanel extends JPanel {

    List<Component> components = new ArrayList<>();
    private Actioner actioner;
    private Component componenteArrastrada = null;
    private Set<Component> componentesSeleccionadas = new HashSet<>();
    private final Rectangle selectionArea;
    private int selectionAreaStartX, selectionAreaStartY;
    private boolean selectingArea;

    // Me sirve para cuando coloco componentes apretando una combinación de teclas
    private int mouseX, mouseY;

    public DrawingPanel() {

        this.setOpaque(Boolean.TRUE);
        // Aesthetic brown
        //this.setBackground(new Color(213, 201, 188));
        // Aesthetic blue
        this.setBackground(new Color(215, 239, 249));
        //this.setBackground(Color.WHITE);

        selectionArea = new Rectangle(0, 0, 0, 0);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (noComponenteThere(e.getX(), e.getY())) {
                    // Inicia la selección en el punto donde se presionó el mouse
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
                // Finaliza la selección cuando se suelta el mouse
                if (selectionArea.width != 0 || selectionArea.height != 0) {
                    selectComponents();
                    selectionArea.setBounds(0, 0, 0, 0);
                    repaint();
                } else {
                    if (!e.isPopupTrigger()) {
                        if (!e.isControlDown()) {
                            // Si el mouse se suelta y el área de selección es nula
                            limpiarEntidadesSeleccionadas();
                        }
                    }
                }
            }
        });

        // Eventos en los que el mouse se mueve
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selectingArea) {
                    // Actualiza el tamaño del área de selección mientras se arrastra el mouse
                    selectionArea.setBounds(
                            Math.min(e.getX(), selectionAreaStartX),
                            Math.min(e.getY(), selectionAreaStartY),
                            Math.abs(e.getX() - selectionAreaStartX),
                            Math.abs(e.getY() - selectionAreaStartY)
                    );
                    repaint();
                }
            }

            // It gets the mouse position in the DrawPanel
            @Override
            public void mouseMoved(MouseEvent e) {
                if (DrawingPanel.this.getBounds().contains(e.getX(), e.getY())) {
                    DrawingPanel.this.mouseX = e.getX();
                    DrawingPanel.this.mouseY = e.getY();
                }
            }
        });

        PopupMenu backgroundPopupMenu = this.getBackgroundPopupMenu();


        // Agrega un controlador de eventos de mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Cuando se hace clic en el mouse, verifica si se ha seleccionado una entidad
                if (e.isControlDown()) {
                    for (Component component : components) {
                        if (component.getBounds().contains(new Point(DrawingPanel.this.mouseX, DrawingPanel.this.mouseY))) {
                            componentesSeleccionadas.add(component);
                            component.setSelected(Boolean.TRUE);
                        }
                    }

                } else {
                    // For each entity, relationship, hierarchy and note...
                    List<Component> components = getListComponents();
                    for (Component component : components) {
                        if (component.getBounds().contains(e.getPoint())) {
                            componenteArrastrada = component;
                            componenteArrastrada.setSelected(Boolean.TRUE);
                            break;
                        }
                    }
                }
                mostrarMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Cuando se suelta el mouse, deselecciona la entidad y detiene el arrastre
                if (componenteArrastrada != null) {
                    componenteArrastrada.setSelected(Boolean.FALSE);
                }
                componenteArrastrada = null;
                repaint();

                mostrarMenu(e);
            }

            private void mostrarMenu(MouseEvent e) {
                // Si se presiona el click derecho
                if (e.isPopupTrigger()) {
                    boolean componentClicked = false;

                    for (Component component : getListComponents()) {
                        if (component.getBounds().contains(e.getPoint())) {
                            limpiarEntidadesSeleccionadas();
                            componentesSeleccionadas.add(component);
                            component.showPopupMenu(e.getComponent(), e.getX(), e.getY());
                            componentClicked = Boolean.TRUE;
                            repaint();
                            break;
                        }
                    }

                    if (!componentClicked) {
                        limpiarEntidadesSeleccionadas();
                        backgroundPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                        repaint();
                    }
                }

            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Cuando se arrastra el mouse, mueve la entidad arrastrada
                // solo si no estamos en modo de selección
                if (!e.isControlDown() && componenteArrastrada != null) {
                    componenteArrastrada.setX(e.getX());
                    componenteArrastrada.setY(e.getY());
                    repaint();
                }
            }
        });
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // It draws the selection area
        Graphics2D g2d = (Graphics2D) g;
        g2d.draw(selectionArea);

        for (Component component : this.components) {
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

    public void addComponent(Component component) {
        this.components.addFirst(component);
        repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void removeComponent(Component component) {
        component.cleanPresence();
        this.components.remove(component);
        repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<Component> getComponentesSeleccionadas() {
        return (new ArrayList<>(componentesSeleccionadas));
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void limpiarEntidadesSeleccionadas() {
        for (Component a : componentesSeleccionadas) {
           a.setSelected(Boolean.FALSE);
           if (a.getClass().toString().equals("class com.bdd.mer.estatica.entity.MacroEntity")) {
               for (Component b : ((MacroEntity) a).participatingComponents()) {
                   b.setSelected(Boolean.FALSE);
               }
           }
        }

        // Si hago un clear, borro las referencias a las entidades y no
        // se dibujan las líneas
        componentesSeleccionadas = new HashSet<>();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
/*
    public void addComponents(List<Component> componentsList) {
        for (Component c : componentsList) {
            if (c.getClass().toString().equals("class com.bdd.mer.estatica.entity.MacroEntity")) {
                agregarEntidad((Entity) c);
            } else if (c.getClass().toString().equals("class com.bdd.mer.estatica.relationship.Relationship")) {
                agregarRelacion((Relationship) c);
            }
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /*
    public void deleteComponents(List<Component> componentsList) {
        for (Component c : componentsList) {
            if (c.getClass().toString().equals("class com.bdd.mer.estatica.entity.MacroEntity")) {
                this.eliminarEntidad((Entity) c);
            } else if (c.getClass().toString().equals("class com.bdd.mer.estatica.relationship.Relationship")) {
                this.eliminarRelacion((Relationship) c);
            }
        }
    }
    /* -------------------------------------------------------------------------------------------------------------- */

    public void reset() {
        this.components = new ArrayList<>();
        this.componenteArrastrada = null;
        this.componentesSeleccionadas = new HashSet<>();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /*
    El método corrobora que todos los objetos Component seleccionados son entidades y
    aún no se hallan en ninguna jerarquía.

    El MER no soporta herencia múltiple.
     */
    public boolean participaSoloEntidades() {
        for (Component a : componentesSeleccionadas) {
            if (!a.getClass().toString().equals("class com.bdd.mer.estatica.Entidad") &&
                !a.getClass().toString().equals("class com.bdd.mer.estatica.entity.MacroEntity")) {
                return false;
            }
        }

        return true;
    }

    public List<Entity> getSelectedEntities() {

        List<Entity> out = new ArrayList<>();

        for (Component component : this.components) {
            out.addAll(component.getEntities());
        }

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public boolean oneRelationshipAndEntities() {
        boolean relationshipFound = false;
        for (Component component : componentesSeleccionadas) {
            // If the component is not an entity...
            if (!component.getClass().toString().equals("class com.bdd.mer.estatica.Entidad")) {
                // If the component is a relationship
                if (component.getClass().toString().equals("class com.bdd.mer.estatica.relationship.Relacion")) {
                    // Two relationships were selected
                    if (relationshipFound) {
                        return false;
                    } else {
                        relationshipFound = true;
                    }
                } else {
                    return false;
                }
            }
        }

        return relationshipFound;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public int getMouseX() {
        return this.mouseX;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public int getMouseY() {
        return this.mouseY;
    }

    public List<Component> getListComponents() { return new ArrayList<>(this.components); }

    public void addComponents(List<Component> components) {
    }

    public void deleteComponents(List<Component> components) {
    }

    public Actioner getActioner() { return this.actioner; }

    public void setActioner(Actioner actioner) { this.actioner = actioner; }

    private PopupMenu getBackgroundPopupMenu() {

        PopupMenu backgroundPopupMenu = new PopupMenu(this);

        JMenuItem addEntity = new JMenuItem("Add entity");
        addEntity.addActionListener(_ -> this.getActioner().addEntity());

        JMenuItem addRelationship = new JMenuItem("Add relationship");
        addRelationship.addActionListener(_ -> this.getActioner().addRelationship());

        JMenuItem addDependency = new JMenuItem("Add dependency");
        addDependency.addActionListener(_ -> this.getActioner().addDependency());

        JMenuItem addNote = new JMenuItem("Add note");
        addNote.addActionListener(_ -> this.getActioner().addNote());

        backgroundPopupMenu.addOption(addEntity);
        backgroundPopupMenu.addOption(addRelationship);
        backgroundPopupMenu.addOption(addDependency);
        backgroundPopupMenu.addOption(addNote);

        return backgroundPopupMenu;
    }
}