package com.bdd.mer.interfaz;

import com.bdd.mer.estatica.Component;
import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Jerarquia;
import com.bdd.mer.estatica.Relacion;
import com.bdd.mer.estatica.coleccion.Dupla;
import com.bdd.mer.interfaz.anotacion.Nota;
import com.bdd.mer.interfaz.barraDeMenu.MenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MarcoPrincipal extends JFrame {

    private final PanelDibujo panelDibujo;

    public MarcoPrincipal() {
        String fuente = "Verdana";

        setUndecorated(false);  // Elimina la barra de título
        setTitle("Zilva DERExt");
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Crea el panel de dibujo, la barra de menú y el menú
        panelDibujo = new PanelDibujo();
        MenuBar menuBar = new MenuBar(this, panelDibujo);
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
        Dimension dimension = new Dimension(120, 30);

        menu.setPreferredSize(dimension);
        // Aesthetic blue
        //menu.setBackground(new Color(215, 239, 249));
        menu.setBackground(Color.WHITE);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                             Add Entity                                                     */
        /* ---------------------------------------------------------------------------------------------------------- */

        JButton addEntityButton = getNewButton("Entidad", dimension, fuente);
        // Defino la función del botón
        addEntityButton.addActionListener(_ -> {
            // Muestra una ventana emergente para ingresar el nombre de la entidad. La ventana está centrada en el marco principal
            String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva entidad");
            if (nombre != null) {
                // Si el usuario ingresó un nombre, agrega una nueva entidad con ese nombre
                panelDibujo.agregarEntidad(new Entidad(nombre, 50, 50));
            }
        });
        menu.add(addEntityButton);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                          Add Relationship                                                  */
        /* ---------------------------------------------------------------------------------------------------------- */

        JButton addRelationshipButton = getNewButton("Relation", dimension, fuente);
        // Defino la funcionalidad del botón
        addRelationshipButton.addActionListener(_ -> {
            boolean creacionRelacionCancelada = false;

            // Solo procede si se han seleccionado entre 1 y 3 entidades
            if (panelDibujo.participaSoloEntidades() && !panelDibujo.getComponentesSeleccionadas().isEmpty() && panelDibujo.getComponentesSeleccionadas().size() <= 3) {
                // Muestra una ventana emergente para ingresar el nombre de la relación

                String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva relación");
                if (nombre != null) {
                    // Si el usuario ingresó un nombre, agrega una nueva relación con ese nombre
                    List<Dupla<Component, Dupla<Character, Character>>> entidadesParticipantes = new ArrayList<>();
                    Dupla<Character, Character> cardinalidad;
                    for (Component entidad : panelDibujo.getComponentesSeleccionadas()) {
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
        menu.add(addRelationshipButton);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                           Add Dependency                                                   */
        /* ---------------------------------------------------------------------------------------------------------- */

        JButton addDependencyButton = getNewButton("Dependencia", dimension, fuente);
        // Defino la función del botón
        addDependencyButton.addActionListener(_ -> {
            boolean creacionRelacionCancelada = false;

            // Solo procede si se han seleccionado entre 1 y 3 entidades
            if (panelDibujo.participaSoloEntidades() && panelDibujo.getComponentesSeleccionadas().size() == 2) {
                // Muestra una ventana emergente para ingresar el nombre de la relación

                String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva dependencia");
                if (nombre != null) {
                    // Si el usuario ingresó un nombre, agrega una nueva relación con ese nombre
                    Entidad entidadDebil = seleccionarEntidadDebil();

                    List<Dupla<Component, Dupla<Character, Character>>> entidadesParticipantes = new ArrayList<>();
                    Dupla<Character, Character> cardinalidad;

                    for (Component entidad : panelDibujo.getComponentesSeleccionadas()) {
                        if (entidad == entidadDebil) {
                            cardinalidad = ingresarCardinalidad(((Entidad) entidad).getNombre());
                            if (cardinalidad != null) {
                                entidadesParticipantes.add(new Dupla<>(entidad, cardinalidad));
                            } else {
                                creacionRelacionCancelada = true;
                                break;
                            }
                        } else {
                            // Una entidad débil solo puede estar relacionada con una entidad fuerte si
                            // esta última tiene cardinalidad 1:1
                            entidadesParticipantes.add(new Dupla<>(entidad, new Dupla<>('1', '1')));
                        }
                    }

                    if (!creacionRelacionCancelada) {
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
        menu.add(addDependencyButton);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                           Add Hierarchy                                                    */
        /* ---------------------------------------------------------------------------------------------------------- */

        JButton addHierarchyButton = getNewButton("Jerarquía", dimension, fuente);
        // Defino la función del botón
        addHierarchyButton.addActionListener(_ -> {
            // Solo procede si se ha seleccionado al menos tres entidades
            if (panelDibujo.getComponentesSeleccionadas().size() >= 3 && panelDibujo.participaSoloEntidades()) {
                Entidad supertipo = seleccionarSupertipo();
                List<Entidad> subtipos = obtenerListaSubtipos(supertipo);
                if (rolesJerarquiaOcupados(supertipo, subtipos)) {
                    ocuparRoles(supertipo, subtipos);
                    Dupla<Boolean, Boolean> tipo = seleccionarTipoJerarquia();

                    Jerarquia newHierarchy = new Jerarquia("",tipo.getPrimero(), tipo.getSegundo(), supertipo, subtipos);

                    panelDibujo.agregarJerarquia(newHierarchy);

                    // Add the hierarchy to the entities
                    for (Component e : panelDibujo.getComponentesSeleccionadas()) {
                        ((Entidad) e).addHierarchy(newHierarchy);
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Una o más entidades seleccionadas ya ocupan el rol seleccionado en otra jerarquía.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione al menos tres entidades.");
            }

            // Desactiva el modo de selección
            panelDibujo.setSeleccionando(false);
            panelDibujo.limpiarEntidadesSeleccionadas();
            panelDibujo.repaint();
        });
        menu.add(addHierarchyButton);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                             Add Note                                                       */
        /* ---------------------------------------------------------------------------------------------------------- */

        // Añade una nota al programa
        JButton botonAgregarNota = getNewButton("Nota", dimension, fuente);
        botonAgregarNota.addActionListener(_ -> {
            // Muestra una ventana emergente para ingresar el contenido de la nota
            String contenido = JOptionPane.showInputDialog(this, "Ingrese el contenido de la nueva nota");
            if (contenido != null) {
                // Si el usuario ingresó contenido, agrega una nueva nota con ese contenido
                panelDibujo.agregarNota(new Nota(contenido, 150, 150));
            }
        });
        menu.add(botonAgregarNota);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                              Delete Key                                                    */
        /* ---------------------------------------------------------------------------------------------------------- */

        // Tecla para eliminar (en realidad, botón oculto que se activa al presionar una tecla).
        JButton teclaEliminar = new JButton("Eliminar");
        teclaEliminar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Suprimir presionado");
        teclaEliminar.getActionMap().put("Suprimir presionado", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // Solo procede si se ha seleccionado al menos una entidad
                if (!panelDibujo.getComponentesSeleccionadas().isEmpty()) {
                    // Muestra una ventana emergente para ingresar el nombre de la relación
                    int confirmation = JOptionPane.showConfirmDialog(null, "¿Seguro de que desea eliminar las componentes seleccionadas?");
                    if (confirmation == JOptionPane.YES_OPTION) {
                        List<Entidad> paraEliminarEntidad = new ArrayList<>();
                        List<Relacion> paraEliminarRelacion = new ArrayList<>();
                        List<Jerarquia> paraEliminarJerarquia = new ArrayList<>();

                        // Encuentra los elementos para eliminar
                        for (Component object : panelDibujo.getComponentesSeleccionadas()) {
                            if (object.getClass().toString().equals("class com.bdd.mer.estatica.Entidad")) {
                                paraEliminarRelacion.addAll(((Entidad) object).getRelaciones());
                                paraEliminarJerarquia.addAll(((Entidad) object).getHeriarchiesList());
                                paraEliminarEntidad.add((Entidad) object);
                            }

                            if (object.getClass().toString().equals("class com.bdd.mer.estatica.Relacion")) {
                                List<Entidad> participatingEntities = new ArrayList<>();

                                assert object instanceof Relacion;

                                for (Dupla<Component, Dupla<Character, Character>> d : ((Relacion) object).getEntidades()) {
                                    participatingEntities.add((Entidad) d.getPrimero());
                                }

                                // Desvinculo a las entidades de la relación
                                for (Entidad entity : participatingEntities) {
                                    entity.removeRelation((Relacion) object);
                                }

                                panelDibujo.eliminarRelacion((Relacion) object);
                            }

                            if (object.getClass().toString().equals("class com.bdd.mer.interfaz.anotacion.Nota")) {
                                assert object instanceof Nota;
                                panelDibujo.deleteNote((Nota) object);
                            }

                            if (object.getClass().toString().equals("class com.bdd.mer.estatica.Jerarquia")) {
                                assert object instanceof Jerarquia;

                                List<Entidad> entitiesParticipating = new ArrayList<>(((Jerarquia) object).getEntitiesList());

                                // Desvinculo cada entidad con la jerarquía a eliminar
                                for (Entidad entity : entitiesParticipating) {
                                    entity.removeHierarchy((Jerarquia) object);
                                }

                                panelDibujo.deleteHierarchy((Jerarquia) object);
                            }
                        }

                        // Elimina los elementos
                        for (Relacion r : paraEliminarRelacion) {
                            panelDibujo.eliminarRelacion(r);
                        }
                        for (Jerarquia j : paraEliminarJerarquia) {
                            panelDibujo.deleteHierarchy(j);
                        }
                        for (Entidad ent : paraEliminarEntidad) {
                            panelDibujo.eliminarEntidad(ent);
                        }

                        panelDibujo.repaint();
                    }

                    repaint();
                } else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar al menos un elemento.");
                }

                // Desactiva el modo de selección
                panelDibujo.setSeleccionando(false);
                panelDibujo.limpiarEntidadesSeleccionadas();
            }
        });

        // Agrega la tecla a las funcionalidades del frame
        getContentPane().add(teclaEliminar);

        // Agrega el panel de dibujo y el menú al marco
        add(panelDibujo, BorderLayout.CENTER);
        add(menu, BorderLayout.WEST);
        // Añade la barra de menú a la ventana
        setJMenuBar(menuBar);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */

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

    /* -------------------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */

    public Entidad seleccionarEntidadDebil() {

        // Define las opciones para los botones
        Object[] opciones = {((Entidad) panelDibujo.getComponentesSeleccionadas().getFirst()).getNombre(),
                ((Entidad) panelDibujo.getComponentesSeleccionadas().getLast()).getNombre()};

        // Muestra el JOptionPane con los botones
        int seleccion = JOptionPane.showOptionDialog(this, "Seleccione la entidad débil de la relación", "Selección",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        // Determina qué botón se seleccionó
        if (seleccion == 0) {
            ((Entidad) panelDibujo.getComponentesSeleccionadas().getFirst()).setEntidadDebil(Boolean.TRUE);
            return ((Entidad) panelDibujo.getComponentesSeleccionadas().getFirst());
        } else if (seleccion == 1) {
            ((Entidad) panelDibujo.getComponentesSeleccionadas().getLast()).setEntidadDebil(Boolean.TRUE);
            return ((Entidad) panelDibujo.getComponentesSeleccionadas().getLast());
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una entidad débil");
            seleccionarEntidadDebil();
        }

        return null;
    }

    public Entidad seleccionarSupertipo() {

        List<Component> entidadesSeleccionadas = panelDibujo.getComponentesSeleccionadas();
        Object[] opciones = new Object[entidadesSeleccionadas.size()];

        for (int i = 0; i < entidadesSeleccionadas.size(); i++) {
            opciones[i] = (((Entidad) entidadesSeleccionadas.get(i)).getNombre());
        }

        // Muestra el JOptionPane con los botones
        int seleccion = JOptionPane.showOptionDialog(null, "Seleccione a la entidad supertipo", "Selección",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion < entidadesSeleccionadas.size()) {
            return ((Entidad) entidadesSeleccionadas.get(seleccion));
        } else {
            // Arreglar
            return null;
        }
    }

    public List<Entidad> obtenerListaSubtipos(Entidad superTipo) {
        List<Component> entidadesSeleccionadas = panelDibujo.getComponentesSeleccionadas();
        List<Entidad> retorno = new ArrayList<>();

        for (Component a : entidadesSeleccionadas) {
            // Si tienen una referencia distinta al superTipo seleccionado
            if (a != superTipo) {
                retorno.add((Entidad) a);
            }
        }

        return retorno;
    }

    public Dupla<Boolean, Boolean> seleccionarTipoJerarquia() {

        // Crea los radio buttons
        JRadioButton opcionExclusiva = new JRadioButton("Exclusiva", true);
        JRadioButton opcionCompartida = new JRadioButton("Compartida");
        JRadioButton opcionTotal = new JRadioButton("Total", true);
        JRadioButton opcionParcial = new JRadioButton("Parcial");

        // Agrupa los radio buttons para que solo se pueda seleccionar uno a la vez
        ButtonGroup groupExclusivaCompartida = new ButtonGroup();
        groupExclusivaCompartida.add(opcionExclusiva);
        groupExclusivaCompartida.add(opcionCompartida);

        ButtonGroup groupTotalExclusiva = new ButtonGroup();
        groupTotalExclusiva.add(opcionTotal);
        groupTotalExclusiva.add(opcionParcial);

        // Crea un panel para contener los radio buttons
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Añade un BoxLayout al panel

        // Crea un panel para cada grupo de radio buttons
        JPanel panelEC = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEC.add(opcionExclusiva);
        panelEC.add(opcionCompartida);

        JPanel panelTP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTP.add(opcionTotal);
        panelTP.add(opcionParcial);

        // Agrega los pares de opciones al panel
        panel.add(panelEC);
        panel.add(panelTP);

        // Muestra el panel en un JOptionPane
        JOptionPane.showOptionDialog(null, panel, "Elige una opción",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        return (new Dupla<>(opcionExclusiva.isSelected(), opcionTotal.isSelected()));
    }

    /*
    Un supertipo solo puede ser supertipo de una única jerarquía. Un subtipo solo puede
    ser subtipo de una única jerarquía.
     */
    public boolean rolesJerarquiaOcupados(Entidad supertipo, List<Entidad> subtipos) {
        if (supertipo.isSuperTipo()) {
            return false;
        }

        for (Entidad e : subtipos) {
            if (e.isSubTipo()) {
                return false;
            }
        }

        return true;
    }

    public void ocuparRoles(Entidad supertipo, List<Entidad> subtipos) {
        supertipo.setSuperTipo(Boolean.TRUE);

        for (Entidad e : subtipos) {
            e.setSubTipo(Boolean.TRUE);
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */

    public JButton getNewButton(String nombreBoton, Dimension dimension, String fuente) {
        JButton botonARetornar = new JButton(nombreBoton);

        botonARetornar.setMaximumSize(dimension);
        botonARetornar.setBackground(Color.WHITE);
        botonARetornar.setOpaque(Boolean.TRUE);
        botonARetornar.setFont(new Font(fuente, Font.BOLD, 12));

        return botonARetornar;
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */

    public PanelDibujo getPanelDibujo() {
        return panelDibujo;
    }
}
