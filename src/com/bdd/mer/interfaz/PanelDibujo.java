package com.bdd.mer.interfaz;

import com.bdd.mer.estatica.Component;
import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Jerarquia;
import com.bdd.mer.estatica.Relacion;
import com.bdd.mer.estatica.atributo.Atributo;
import com.bdd.mer.interfaz.anotacion.Nota;
import com.bdd.mer.interfaz.popup.PopupMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class PanelDibujo extends JPanel {
    List<Entidad> entidades = new ArrayList<>();
    List<Relacion> relaciones = new ArrayList<>();
    List<Jerarquia> jerarquias = new ArrayList<>();
    List<Nota> notas = new ArrayList<>();
    private boolean seleccionando = false;
    private Component componenteArrastrada = null;
    private Set<Component> componentesSeleccionadas = new HashSet<>();
    private final Rectangle selectionArea;
    private boolean selectingArea;

    public PanelDibujo() {

        this.setOpaque(Boolean.TRUE);
        // Aesthetic brown
        this.setBackground(new Color(213, 201, 188));
        // Aesthetic blue
        this.setBackground(new Color(215, 239, 249));

        /* NUNCA PUDE HACERLO FUNCIONAR
        // Asigna la acción de comenzar la selección a la tecla Ctrl
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl pressed"), "comenzarSeleccion");
        getActionMap().put("comenzarSeleccion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSeleccionando(true);
                System.out.println("ss");
            }
        });*/

        selectionArea = new Rectangle(0, 0, 0, 0);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (noComponenteThere(e.getX(), e.getY())) {
                    // Inicia la selección en el punto donde se presionó el mouse
                    selectionArea.setBounds(e.getX(), e.getY(), 0, 0);
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
                        // Si el mouse se suelta y el área de selección es nula
                        limpiarEntidadesSeleccionadas();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selectingArea) {
                    // Actualiza el tamaño del área de selección mientras se arrastra el mouse
                    selectionArea.setBounds(
                            Math.min(e.getX(), selectionArea.x),
                            Math.min(e.getY(), selectionArea.y),
                            Math.abs(e.getX() - selectionArea.x),
                            Math.abs(e.getY() - selectionArea.y)
                    );
                    repaint();
                }
            }
        });

        // Asigna la acción de finalizar la selección a la liberación de la tecla Ctrl
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0, true), "finalizarSeleccion");
        getActionMap().put("finalizarSeleccion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (seleccionando) {
                    limpiarEntidadesSeleccionadas();
                    setSeleccionando(false);
                } else {
                    setSeleccionando(true);
                }
                repaint();
            }
        });

        PopupMenu popupMenuAttribute = new PopupMenu(this, true, true, true, false);
        PopupMenu popupMenuNonAttribute = new PopupMenu(this, false, true, true, false);
        PopupMenu popupMenuCompound = new PopupMenu(this, false, true, true, true);
        PopupMenu backgroundPopupMenu = new PopupMenu(this);

        // Agrega un controlador de eventos de mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Cuando se hace clic en el mouse, verifica si se ha seleccionado una entidad
                if (seleccionando) {
                    for (Entidad entidad : entidades) {
                        if (entidad.getBounds().contains(e.getPoint())) {
                            // Si el punto donde se hizo clic está dentro de la entidad,
                            // agrega la entidad a la lista de entidades seleccionadas
                            componentesSeleccionadas.add(entidad);
                            entidad.setSeleccionada(Boolean.TRUE);
                            break;
                        }
                    }
                    for (Relacion relacion : relaciones) {
                        if (relacion.getBounds().contains(e.getPoint())) {
                            // Si el punto donde se hizo clic está dentro de la relación,
                            // agrega la relación a la lista de elementos seleccionados
                            componentesSeleccionadas.add(relacion);
                            relacion.setSeleccionada(Boolean.TRUE);
                            break;
                        }
                    }
                } else {
                    // For each entity, relationship, hierarchy and note...
                    List<Component> components = getListComponents();
                    for (Component component : components) {
                        if (component.getBounds().contains(e.getPoint())) {
                            componenteArrastrada = component;
                            componenteArrastrada.setSeleccionada(Boolean.TRUE);
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
                    componenteArrastrada.setSeleccionada(Boolean.FALSE);
                }
                componenteArrastrada = null;
                repaint();

                mostrarMenu(e);
            }

            private void mostrarMenu(MouseEvent e) {
                // Si se presiona el click derecho
                if (e.isPopupTrigger()) {
                    boolean componentClicked = false;
                    // Comprobar si el clic fue sobre un arrastrable
                    for (Entidad entidad : entidades) {
                        if (entidad.getBounds().contains(e.getPoint())) {
                            // Defino la entidad sobre la que se hizo click derecho
                            popupMenuAttribute.setObject(entidad);
                            // Si el clic fue sobre una entidad, muestra el menú emergente
                            popupMenuAttribute.show(e.getComponent(), e.getX(), e.getY());
                            componentClicked = true;
                            repaint();
                            break;
                        }
                    }
                    for (Relacion relacion : relaciones) {
                        if (relacion.getBounds().contains(e.getPoint())) {
                            // Defino la entidad sobre la que se hizo click derecho
                            popupMenuAttribute.setObject(relacion);
                            // Si el clic fue sobre una entidad, muestra el menú emergente
                            popupMenuAttribute.show(e.getComponent(), e.getX(), e.getY());
                            componentClicked = true;
                            repaint();
                            break;
                        }
                    }
                    for (Jerarquia jerarquia : jerarquias) {
                        if (jerarquia.getBounds().contains(e.getPoint())) {
                            // Defino la jerarquía sobre la que se hizo click derecho
                            popupMenuNonAttribute.setObject(jerarquia);
                            // Si el clic fue una sobre una jerarquía, muestra el menú emergente
                            popupMenuNonAttribute.show(e.getComponent(), e.getX(), e.getY());
                            componentClicked = true;
                            repaint();
                            break;
                        }
                    }
                    for (Nota note : notas) {
                        if (note.getBounds().contains(e.getPoint())) {
                            popupMenuNonAttribute.setObject(note);
                            popupMenuNonAttribute.show(e.getComponent(), e.getX(), e.getY());
                            componentClicked = true;
                            repaint();
                            break;
                        }
                    }
                    List<Atributo> attributes = new ArrayList<>();
                    for (Entidad entity : entidades) {
                        attributes.addAll(entity.getAttributes());
                    }
                    for (Atributo attribute : attributes) {
                        if (attribute.getBounds().contains(e.getPoint())) {
                            popupMenuCompound.setObject(attribute);
                            popupMenuCompound.show(e.getComponent(), e.getX(), e.getY());
                            componentClicked = true;
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
                // Cuando se arrastra el mouse, mueve la entidad arrastrada
                // solo si no estamos en modo de selección
                if (!seleccionando && componenteArrastrada != null) {
                    componenteArrastrada.setX(e.getX());
                    componenteArrastrada.setY(e.getY());
                    repaint();
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // It draws the selection area
        Graphics2D g2d = (Graphics2D) g;
        g2d.draw(selectionArea);

        for (Relacion relacion : relaciones) {
            relacion.dibujar(g);
        }

        for (Jerarquia jerarquia : jerarquias) {
            jerarquia.dibujar(g);
        }

        for (Entidad entidad : entidades) {
            entidad.dibujar(g);
        }

        for (Nota nota : notas) {
            nota.dibujar(g);
        }
    }

    public boolean noComponenteThere(int x, int y) {
        List<Component> components = getListComponents();
        for (Component component : components) {
            if (component.getBounds().contains((new Point(x, y)))) {
                return false;
            }
        }
        return true;
    }

    public void selectComponents() {
        List<Component> components = getListComponents();
        for (Component component : components) {
            if (selectionArea.getBounds().contains(new Point(component.getX(), component.getY()))) {
                // If the entity, relationship, note or hierarchy is inside the selection area...
                componentesSeleccionadas.add(component);
                component.setSeleccionada(Boolean.TRUE);
            }
        }
    }

    public void agregarEntidad(Entidad entidad) {
        entidades.add(entidad);
        repaint();
    }

    public void eliminarEntidad(Entidad entidad) {
        entidades.remove(entidad);
        repaint();
    }

    public void agregarRelacion(Relacion relacion) {
        relaciones.add(relacion);
        repaint();
    }

    public void eliminarRelacion(Relacion relacion) {
        relaciones.remove(relacion);
        repaint();
    }

    public void agregarNota(Nota nota) {
        notas.add(nota);
        repaint();
    }

    public void deleteNote(Nota note) {
        notas.remove(note);
        repaint();
    }

    public void setSeleccionando(boolean seleccionando) {
        this.seleccionando = seleccionando;
    }

    public List<Component> getComponentesSeleccionadas() {
        return (new ArrayList<>(componentesSeleccionadas));
    }

    public void limpiarEntidadesSeleccionadas() {
        for (Component a : componentesSeleccionadas) {
           a.setSeleccionada(Boolean.FALSE);
        }

        // Si hago un clear, borro las referencias a las entidades y no
        // se dibujan las líneas
        componentesSeleccionadas = new HashSet<>();
    }

    public List<Component> getListComponents() {
        List<Component> list = new ArrayList<>();
        list.addAll(entidades);
        list.addAll(relaciones);
        list.addAll(jerarquias);
        list.addAll(notas);

        return list;
    }

    public void agregarJerarquia(Jerarquia nuevaJerarquia) {
        this.jerarquias.add(nuevaJerarquia);
    }

    public void deleteHierarchy(Jerarquia hierarchy) {
        jerarquias.remove(hierarchy);
        repaint();
    }

    public List<Entidad> getEntidades() {
        return entidades;
    }

    public List<Relacion> getRelaciones() {
        return relaciones;
    }

    public List<Jerarquia> getJerarquias() {
        return jerarquias;
    }

    public List<Nota> getNotas() {
        return notas;
    }

    public void setEntidades(List<Entidad> entidades) {
        this.entidades = entidades;
    }

    public void setRelaciones(List<Relacion> relaciones) {
        this.relaciones = relaciones;
    }

    public void setJerarquias(List<Jerarquia> jerarquias) {
        this.jerarquias = jerarquias;
    }

    public void setNotas(List<Nota> notas) {
        this.notas = notas;
    }

    public void reiniciar() {
        entidades = new ArrayList<>();
        relaciones = new ArrayList<>();
        jerarquias = new ArrayList<>();
        notas = new ArrayList<>();
        seleccionando = false;
        componenteArrastrada = null;
        componentesSeleccionadas = new HashSet<>();
    }

    /*
    El método corrobora que todos los objetos Component seleccionados son entidades y
    aún no se hallan en ninguna jerarquía.

    El MER no soporta herencia múltiple.
     */
    public boolean participaSoloEntidades() {
        for (Component a : componentesSeleccionadas) {
            if (!a.getClass().toString().equals("class com.bdd.mer.estatica.Entidad")) {
                return false;
            }
        }

        return true;
    }
}