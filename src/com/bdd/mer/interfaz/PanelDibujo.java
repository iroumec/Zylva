package com.bdd.mer.interfaz;

import com.bdd.mer.estatica.Arrastrable;
import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Jerarquia;
import com.bdd.mer.estatica.Relacion;
import com.bdd.mer.interfaz.anotacion.Nota;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class PanelDibujo extends JPanel {
    List<Entidad> entidades = new ArrayList<>();
    List<Relacion> relaciones = new ArrayList<>();
    List<Jerarquia> jerarquias = new ArrayList<>();
    List<Nota> notas = new ArrayList<>();
    private boolean seleccionando = false;
    private Arrastrable componenteArrastrada = null;
    private List<Arrastrable> componentesSeleccionadas = new ArrayList<>();

    public PanelDibujo() {

        this.setOpaque(Boolean.TRUE);
        this.setBackground(new Color(213,201,188));

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
                    for (Entidad entidad : entidades) {
                        if (entidad.getBounds().contains(e.getPoint())) {
                            // Si el punto donde se hizo clic está dentro de la entidad,
                            // establece la entidad como la entidad arrastrada
                            componenteArrastrada = entidad;
                            componenteArrastrada.setSeleccionada(Boolean.TRUE);
                            break;
                        }
                    }
                    for (Relacion relacion : relaciones) {
                        if (relacion.getBounds().contains(e.getPoint())) {
                            componenteArrastrada = relacion;
                            componenteArrastrada.setSeleccionada(Boolean.TRUE);
                            break;
                        }
                    }
                    for (Jerarquia jerarquia : jerarquias) {
                        if (jerarquia.getBounds().contains(e.getPoint())) {
                            componenteArrastrada = jerarquia;
                            componenteArrastrada.setSeleccionada(Boolean.FALSE);
                            break;
                        }
                    }
                    for (Nota nota : notas) {
                        if (nota.getBounds().contains(e.getPoint())) {
                            componenteArrastrada = nota;
                            componenteArrastrada.setSeleccionada(Boolean.TRUE);
                            break;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Cuando se suelta el mouse, deselecciona la entidad y detiene el arrastre
                if (componenteArrastrada != null) {
                    componenteArrastrada.setSeleccionada(Boolean.FALSE);
                }
                componenteArrastrada = null;
                repaint();
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

    public void setSeleccionando(boolean seleccionando) {
        this.seleccionando = seleccionando;
    }

    public List<Arrastrable> getComponentesSeleccionadas() {
        return this.componentesSeleccionadas;
    }

    public void limpiarEntidadesSeleccionadas() {
        for (Arrastrable a : componentesSeleccionadas) {
           a.setSeleccionada(Boolean.FALSE);
        }

        // Si hago un clear, borro las referencias a las entidades y no
        // se dibujan las líneas
        componentesSeleccionadas = new ArrayList<>();
    }

    public void agregarJerarquia(Jerarquia nuevaJerarquia) {
        this.jerarquias.add(nuevaJerarquia);
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
        componentesSeleccionadas = new ArrayList<>();
    }
}