package com.bdd.mer.interfaz;

import com.bdd.mer.estatica.Arrastrable;
import com.bdd.mer.estatica.atributo.Atributo;
import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Relacion;
import com.bdd.mer.estatica.atributo.TipoAtributo;
import com.bdd.mer.estatica.coleccion.Dupla;
import com.bdd.mer.interfaz.anotacion.Nota;
import com.bdd.mer.interfaz.exportacion.ExportPNG;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class MarcoPrincipal extends JFrame {

    private final PanelDibujo panelDibujo;
    private final String fuente = "Verdana";

    public MarcoPrincipal() {
        setTitle("Diagrama Entidad-Relación Extendido");
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Crea el panel de dibujo y el menú
        panelDibujo = new PanelDibujo();
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
        Dimension dimension = new Dimension(190, 30);

        menu.setPreferredSize(dimension);

        // Agrega un botón al menú para agregar entidades
        JButton botonAgregarEntidad = new JButton("Agregar entidad");
        botonAgregarEntidad.setMaximumSize(dimension);
        botonAgregarEntidad.setBackground(Color.WHITE);
        botonAgregarEntidad.setOpaque(Boolean.TRUE);
        botonAgregarEntidad.setFont(new Font(fuente, Font.BOLD, 12));
        botonAgregarEntidad.addActionListener(e -> {
            // Muestra una ventana emergente para ingresar el nombre de la entidad
            String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva entidad");
            if (nombre != null) {
                // Si el usuario ingresó un nombre, agrega una nueva entidad con ese nombre
                panelDibujo.agregarEntidad(new Entidad(nombre, 50, 50));
            }
        });
        menu.add(botonAgregarEntidad);

        // Agrega un botón al menú para agregar atributos
        JButton botonAgregarAtributo = new JButton("Agregar atributo");
        botonAgregarAtributo.setMaximumSize(dimension);
        botonAgregarAtributo.setBackground(Color.WHITE);
        botonAgregarAtributo.setOpaque(Boolean.TRUE);
        botonAgregarAtributo.setFont(new Font(fuente, Font.BOLD, 12));
        botonAgregarAtributo.addActionListener(e -> {
            // Solo procede si se ha seleccionado al menos una entidad
            if (!panelDibujo.getComponentesSeleccionadas().isEmpty()) {
                // Crea los componentes del panel
                JTextField fieldNombre = new JTextField(10);
                JCheckBox boxOpcional = new JCheckBox("Opcional");
                JCheckBox boxMultivaluado = new JCheckBox("Multivaluado");

                // Crea un array de los componentes
                Object[] message = {
                        "Ingrese el nombre del atributo:", fieldNombre,
                        "Seleccione las opciones:", boxOpcional, boxMultivaluado
                };

                // Muestra el JOptionPane
                int option = JOptionPane.showConfirmDialog(null, message, "Ingrese la información del atributo", JOptionPane.OK_CANCEL_OPTION);

                // Muestra una ventana emergente para ingresar el nombre del atributo y seleccionar las opciones
                if (option == JOptionPane.OK_OPTION) {
                    String nombre = fieldNombre.getText();

                    if (nombre != null) {
                        for (Arrastrable arrastrable : panelDibujo.getComponentesSeleccionadas()) {
                            arrastrable.agregarAtributo(new Atributo(nombre,
                                    boxOpcional.isSelected(),
                                    boxMultivaluado.isSelected(),
                                    TipoAtributo.COMUN));
                        }
                    }

                    repaint();

                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione al menos una entidad.");
            }
        });
        menu.add(botonAgregarAtributo);

        // Agrega un botón al menú para finalizar la selección de entidades y agregar la relación
        JButton botonAgregarRelacion = new JButton("Agregar relación");
        botonAgregarRelacion.setMaximumSize(dimension);
        botonAgregarRelacion.setBackground(Color.WHITE);
        botonAgregarRelacion.setOpaque(Boolean.TRUE);
        botonAgregarRelacion.setFont(new Font(fuente, Font.BOLD, 12));
        botonAgregarRelacion.addActionListener(e -> {
            boolean relacionable = true;
            boolean creacionRelacionCancelada = false;

            // Chequeo que todos los elementos seleccionados sean Entidades
            for (Arrastrable a : panelDibujo.getComponentesSeleccionadas()) {
                if (!(a.getClass().toString()).equals("class com.bdd.mer.estatica.Entidad")) {
                    relacionable = false;
                }
            }

            // Solo procede si se han seleccionado entre 1 y 3 entidades
            if (relacionable && !panelDibujo.getComponentesSeleccionadas().isEmpty() && panelDibujo.getComponentesSeleccionadas().size() <= 3) {
                // Muestra una ventana emergente para ingresar el nombre de la relación

                String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva relación");
                if (nombre != null) {
                    // Si el usuario ingresó un nombre, agrega una nueva relación con ese nombre
                    List<Dupla<Arrastrable, Dupla<Character, Character>>> entidadesParticipantes = new ArrayList<>();
                    Dupla<Character, Character> cardinalidad;
                    for (Arrastrable entidad : panelDibujo.getComponentesSeleccionadas()) {
                        cardinalidad = ingresarCardinalidad(((Entidad) entidad).getNombre());
                        if (cardinalidad != null) {
                            entidadesParticipantes.add(new Dupla<>(entidad, cardinalidad));
                        } else {
                            creacionRelacionCancelada = true;
                            break;
                        }
                    }
                    if (!creacionRelacionCancelada) {
                        panelDibujo.agregarRelacion(new Relacion(nombre, entidadesParticipantes, 150, 150));
                        panelDibujo.limpiarEntidadesSeleccionadas();
                    }

                }
            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar entre 1 y 3 entidades para crear una relación.");
            }

            // Desactiva el modo de selección
            panelDibujo.setSeleccionando(false);
        });
        menu.add(botonAgregarRelacion);

        // Agrega un botón al menú para finalizar la selección de entidades y agregar una dependencia
        JButton botonAgregarDependencia = new JButton("Agregar dependencia");
        botonAgregarDependencia.setMaximumSize(dimension);
        botonAgregarDependencia.setBackground(Color.WHITE);
        botonAgregarDependencia.setOpaque(Boolean.TRUE);
        botonAgregarDependencia.setFont(new Font(fuente, Font.BOLD, 12));
        botonAgregarDependencia.addActionListener(e -> {
            boolean relacionable = true;
            boolean creacionRelacionCancelada = false;

            // Chequeo que todos los elementos seleccionados sean Entidades
            for (Arrastrable a : panelDibujo.getComponentesSeleccionadas()) {
                if (!(a.getClass().toString()).equals("class com.bdd.mer.estatica.Entidad")) {
                    relacionable = false;
                }
            }

            // Solo procede si se han seleccionado entre 1 y 3 entidades
            if (relacionable && panelDibujo.getComponentesSeleccionadas().size() == 2) {
                // Muestra una ventana emergente para ingresar el nombre de la relación

                String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva dependencia");
                if (nombre != null) {
                    // Si el usuario ingresó un nombre, agrega una nueva relación con ese nombre
                    List<Dupla<Arrastrable, Dupla<Character, Character>>> entidadesParticipantes = new ArrayList<>();
                    Dupla<Character, Character> cardinalidad;
                    for (Arrastrable entidad : panelDibujo.getComponentesSeleccionadas()) {
                        cardinalidad = ingresarCardinalidad(((Entidad) entidad).getNombre());
                        if (cardinalidad != null) {
                            entidadesParticipantes.add(new Dupla<>(entidad, cardinalidad));
                        } else {
                            creacionRelacionCancelada = true;
                            break;
                        }
                    }
                    if (!creacionRelacionCancelada) {
                        seleccionarEntidadDebil();
                        panelDibujo.agregarRelacion(new Relacion(nombre, entidadesParticipantes, 150, 150));
                        panelDibujo.limpiarEntidadesSeleccionadas();
                    }

                }
            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar 2 entidades para crear una dependencia.");
            }

            // Desactiva el modo de selección
            panelDibujo.setSeleccionando(false);
        });
        menu.add(botonAgregarDependencia);

        // Agrega un botón al menú para finalizar la selección de entidades y agregar la relación
        JButton botonAgregarIdentificador = new JButton("Agregar ID Principal");
        botonAgregarIdentificador.setMaximumSize(dimension);
        botonAgregarIdentificador.setBackground(Color.WHITE);
        botonAgregarIdentificador.setOpaque(Boolean.TRUE);
        botonAgregarIdentificador.setFont(new Font(fuente, Font.BOLD, 12));
        botonAgregarIdentificador.addActionListener(e -> {
            // Solo procede si se ha seleccionado una entidad
            if (panelDibujo.getComponentesSeleccionadas().size() == 1) {

                if (((Entidad) panelDibujo.getComponentesSeleccionadas().getFirst()).tieneIDPrincipal()) {
                    JOptionPane.showMessageDialog(this, "Esta entidad ya tiene un identificador principal.");
                } else {
                    seleccionarIdentificadorPrincipal();

                    panelDibujo.limpiarEntidadesSeleccionadas();
                    panelDibujo.setSeleccionando(false);
                }

                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Esta opción solo permite una entidad a la vez.");
            }

            // Desactiva el modo de selección
            panelDibujo.setSeleccionando(false);
            panelDibujo.limpiarEntidadesSeleccionadas();
        });
        menu.add(botonAgregarIdentificador);

        // Agrega un botón para eliminar lo seleccionado
        JButton botonEliminar = new JButton("Eliminar");
        botonEliminar.setMaximumSize(dimension);
        botonEliminar.setBackground(Color.WHITE);
        botonEliminar.setOpaque(Boolean.TRUE);
        botonEliminar.setFont(new Font(fuente, Font.BOLD, 12));
        botonEliminar.addActionListener(e -> {
            // Solo procede si se ha seleccionado al menos una entidad
            if (!panelDibujo.getComponentesSeleccionadas().isEmpty()) {
                // Muestra una ventana emergente para ingresar el nombre de la relación
                int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro de que desea eliminar las componentes seleccionadas?");
                if (confirmacion == JOptionPane.YES_OPTION) {
                    List<Entidad> paraEliminarEntidad = new ArrayList<>();
                    List<Relacion> paraEliminarRelacion = new ArrayList<>();
                    // Encuentra los elementos para eliminar
                    for (Arrastrable a : panelDibujo.getComponentesSeleccionadas()) {
                        if (a.getClass().toString().equals("class com.bdd.mer.estatica.Entidad")) {
                            for (Arrastrable relacion : ((Entidad) a).getRelaciones()) {
                                paraEliminarRelacion.add((Relacion) relacion);
                            }
                            paraEliminarEntidad.add((Entidad) a);
                        } else {
                            paraEliminarRelacion.add((Relacion) a);
                        }
                    }

                    // Elimina los elementos
                    for (Relacion r : paraEliminarRelacion) {
                        panelDibujo.eliminarRelacion(r);
                    }
                    for (Entidad ent : paraEliminarEntidad) {
                        panelDibujo.eliminarEntidad(ent);
                    }

                    panelDibujo.limpiarEntidadesSeleccionadas();
                    panelDibujo.setSeleccionando(false);
                }

                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un elemento.");
            }

            // Desactiva el modo de selección
            panelDibujo.setSeleccionando(false);
            panelDibujo.limpiarEntidadesSeleccionadas();
        });
        menu.add(botonEliminar);

        // Exporta el diagrama como PNG
        JButton botonExportarComoImagen = new JButton("Exportar como PNG");
        botonExportarComoImagen.setMaximumSize(dimension);
        botonExportarComoImagen.setBackground(Color.WHITE);
        botonExportarComoImagen.setOpaque(Boolean.TRUE);
        botonExportarComoImagen.setFont(new Font(fuente, Font.BOLD, 12));
        botonExportarComoImagen.addActionListener(e -> {
            ExportPNG.exportToPng(panelDibujo);
        });
        menu.add(botonExportarComoImagen);

        // Añade una nota al programa
        JButton botonAgregarNota = new JButton("Agregar nota");
        botonAgregarNota.setMaximumSize(dimension);
        botonAgregarNota.setBackground(Color.WHITE);
        botonAgregarNota.setOpaque(Boolean.TRUE);
        botonAgregarNota.setFont(new Font(fuente, Font.BOLD, 12));
        botonAgregarNota.addActionListener(e -> {
            // Muestra una ventana emergente para ingresar el contenido de la nota
            String contenido = JOptionPane.showInputDialog(this, "Ingrese el contenido de la nueva nota");
            if (contenido != null) {
                // Si el usuario ingresó contenido, agrega una nueva nota con ese contenido
                panelDibujo.agregarNota(new Nota(contenido, 150, 150));
            }
        });
        menu.add(botonAgregarNota);

        // Agrega el panel de dibujo y el menú al marco
        add(panelDibujo, BorderLayout.CENTER);
        add(menu, BorderLayout.WEST);
    }

    private Dupla<Character, Character> ingresarCardinalidad(String nombre) {
        JTextField cardinalidadMinimaCampo = new JTextField(1);
        JTextField cardinalidadMaximaCampo = new JTextField(1);
        JButton okButton = new JButton("OK");
        okButton.setEnabled(false); // Deshabilita el botón OK inicialmente

        JPanel miPanel = new JPanel();
        miPanel.add(new JLabel("Ingrese las cardinalidad para la entidad " + nombre + ": "));
        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
        miPanel.add(new JLabel("Cardinalidad mínima:"));
        miPanel.add(cardinalidadMinimaCampo);
        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
        miPanel.add(new JLabel("Cardinalidad máxima:"));
        miPanel.add(cardinalidadMaximaCampo);

        int resultado = JOptionPane.showConfirmDialog(null, miPanel,
                "Por favor ingrese dos valores", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            String cardinalidadMinima = cardinalidadMinimaCampo.getText();
            String cardinalidadMaxima = cardinalidadMaximaCampo.getText();

            if (cardinalidadMinima.isEmpty() || cardinalidadMaxima.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ambos campos deben tener un valor");
                return ingresarCardinalidad(nombre);
            } else {
                return new Dupla<>(cardinalidadMinima.charAt(0), cardinalidadMaxima.charAt(0));
            }
        } else {
            return null;
        }

    }

    public void seleccionarIdentificadorPrincipal() {

        List<Atributo> atributosEntidad = ((Entidad) panelDibujo.getComponentesSeleccionadas().getFirst()).getAtributos();
        Object[] opciones = new Object[atributosEntidad.size()];

        for (int i = 0; i < atributosEntidad.size(); i++) {
            opciones[i] = (atributosEntidad.get(i).getNombre());
        }

        // Muestra el JOptionPane con los botones
        int seleccion = JOptionPane.showOptionDialog(null, "Seleccione el identificador unívoco", "Selección",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion < atributosEntidad.size()) {
            atributosEntidad.get(seleccion).setTipo(TipoAtributo.PRINCIPAL);
            ((Entidad) panelDibujo.getComponentesSeleccionadas().getFirst()).setTieneIDPrincipal(Boolean.TRUE);
        }

    }

    public void seleccionarEntidadDebil() {

        // Define las opciones para los botones
        Object[] opciones = {((Entidad) panelDibujo.getComponentesSeleccionadas().getFirst()).getNombre(),
                ((Entidad) panelDibujo.getComponentesSeleccionadas().getLast()).getNombre()};

        // Muestra el JOptionPane con los botones
        int seleccion = JOptionPane.showOptionDialog(null, "Seleccione la entidad débil de la relación", "Selección",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        // Determina qué botón se seleccionó
        String resultado;
        if (seleccion == 0) {
            ((Entidad) panelDibujo.getComponentesSeleccionadas().getFirst()).setEntidadDebil(Boolean.TRUE);
        } else if (seleccion == 1) {
            ((Entidad) panelDibujo.getComponentesSeleccionadas().getLast()).setEntidadDebil(Boolean.TRUE);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una entidad débil");
            seleccionarEntidadDebil();
        }
    }

    private void eliminarRelacion(Relacion relacion) {
        for (Dupla<Arrastrable, Dupla<Character, Character>> dupla : relacion.getEntidades()) {
            ((Entidad) dupla.getPrimero()).getRelaciones().remove(relacion);
        }
        panelDibujo.relaciones.remove(relacion);
    }

    public PanelDibujo getPanelDibujo() {
        return panelDibujo;
    }
}
