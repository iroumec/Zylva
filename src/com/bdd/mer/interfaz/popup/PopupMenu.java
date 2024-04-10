package com.bdd.mer.interfaz.popup;

import com.bdd.mer.estatica.Component;
import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Jerarquia;
import com.bdd.mer.estatica.Relacion;
import com.bdd.mer.estatica.atributo.Atributo;
import com.bdd.mer.estatica.atributo.TipoAtributo;
import com.bdd.mer.estatica.coleccion.Dupla;
import com.bdd.mer.interfaz.PanelDibujo;
import com.bdd.mer.interfaz.anotacion.Nota;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PopupMenu extends JPopupMenu {

    private int x, y;
    private Component objeto;
    private final PanelDibujo panelDibujo;

    public PopupMenu(PanelDibujo panelDibujo) {
        this.panelDibujo = panelDibujo;

        // Obtengo la posición del mouse
        panelDibujo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    setMousePosition(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    setMousePosition(e);
                }
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                              Add Entity                                                    */
        /* ---------------------------------------------------------------------------------------------------------- */

        JMenuItem addEntity = new JMenuItem("Agregar entidad");
        addEntity.addActionListener(_ -> {
            // Muestra una ventana emergente para ingresar el nombre de la entidad. La ventana está centrada en el marco principal
            String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva entidad");
            if (nombre != null) {
                // Si el usuario ingresó un nombre, agrega una nueva entidad con ese nombre
                panelDibujo.agregarEntidad(new Entidad(nombre, this.x, this.y));
            }
        });
        add(addEntity);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                          Add Relationship                                                  */
        /* ---------------------------------------------------------------------------------------------------------- */

        JMenuItem addRelationship = new JMenuItem("Agregar relación");
        addRelationship.addActionListener(_ -> {
            boolean creacionRelacionCancelada = false;

            // Solo procede si se han seleccionado entre 1 y 3 entidades
            if (panelDibujo.participaSoloEntidades() && !panelDibujo.getComponentesSeleccionadas().isEmpty() && panelDibujo.getComponentesSeleccionadas().size() <= 3) {
                // Muestra una ventana emergente para ingresar el nombre de la relación

                String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva relación");
                if (nombre != null) {
                    // Si el usuario ingresó un nombre, agrega una nueva relación con ese nombre
                    java.util.List<Dupla<Component, Dupla<Character, Character>>> entidadesParticipantes = new ArrayList<>();
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
                        panelDibujo.agregarRelacion(new Relacion(nombre, entidadesParticipantes, this.x, this.y));
                        panelDibujo.limpiarEntidadesSeleccionadas();
                    }

                }
            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar entre 1 y 3 entidades para crear una relación.");
            }

            // Desactiva el modo de selección
            //panelDibujo.setSeleccionando(false);
        });
        add(addRelationship);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                           Add Dependency                                                   */
        /* ---------------------------------------------------------------------------------------------------------- */

        JMenuItem addDependency = new JMenuItem("Agregar dependencia");
        // Defino la función del botón
        addDependency.addActionListener(_ -> {
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
                        panelDibujo.agregarRelacion(new Relacion(nombre, entidadesParticipantes, this.x, this.y));
                        panelDibujo.limpiarEntidadesSeleccionadas();
                    }

                }
            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar 2 entidades para crear una dependencia.");
            }

            // Desactiva el modo de selección
            //panelDibujo.setSeleccionando(false);
        });
        add(addDependency);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                           Add Hierarchy                                                    */
        /* ---------------------------------------------------------------------------------------------------------- */

        JMenuItem addHierarchy = new JMenuItem("Agregar jerarquía");
        addHierarchy.addActionListener(_ -> {
            // Solo procede si se ha seleccionado al menos tres entidades
            if (panelDibujo.getComponentesSeleccionadas().size() >= 3 && panelDibujo.participaSoloEntidades()) {
                Entidad supertipo = seleccionarSupertipo();
                List<Entidad> subtipos = obtenerListaSubtipos(supertipo);
                if (rolesJerarquiaOcupados(supertipo, subtipos)) {
                    ocuparRoles(supertipo, subtipos);
                    Dupla<Boolean, Boolean> tipo = seleccionarTipoJerarquia();

                    Jerarquia newHierarchy = new Jerarquia(tipo.getPrimero(), tipo.getSegundo(), supertipo, subtipos);

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
            //panelDibujo.setSeleccionando(false);
            panelDibujo.limpiarEntidadesSeleccionadas();
            panelDibujo.repaint();
        });
        add(addHierarchy);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                             Add Note                                                       */
        /* ---------------------------------------------------------------------------------------------------------- */

        JMenuItem addNote = new JMenuItem("Agregar nota");
        addNote.addActionListener(_ -> {
            // Muestra una ventana emergente para ingresar el contenido de la nota
            String contenido = JOptionPane.showInputDialog(this, "Ingrese el contenido de la nueva nota");
            if (contenido != null) {
                // Si el usuario ingresó contenido, agrega una nueva nota con ese contenido
                panelDibujo.agregarNota(new Nota(contenido, this.x, this.y));
            }
            panelDibujo.repaint();
        });
        add(addNote);
    }

    public PopupMenu(PanelDibujo panelDibujo, boolean attribute, boolean rename, boolean delete, boolean compound) {
        this.panelDibujo = panelDibujo;

        if (attribute) {
            add(new addAtributeMenuItem("Agregar atributo", this, panelDibujo));
        }
        if (rename) {
            JMenuItem renameComponent = new JMenuItem("Renombrar");
            renameComponent.addActionListener(_ -> {
                Component objeto = this.getObject();
                String newText;

                do {
                    newText = JOptionPane.showInputDialog(null, "Ingrese el nuevo texto: ");

                    // "newText" can be null when the user pressed "cancel"
                    if (newText != null && newText.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Ingrese al menos un carácter");
                    }
                } while (newText != null && newText.isEmpty());

                // If "Cancel" was not pressed
                if (newText != null) {
                    objeto.setText(newText);
                    panelDibujo.repaint();
                }
            });
            add(renameComponent);
        }
        if (delete) {
            add(new deleteMenuItem("Eliminar", this, panelDibujo));
        }
        if (compound) {
            JMenuItem addComponent =  new JMenuItem("Agregar componente (atributo compuesto)");
            addComponent.addActionListener(_ ->  {
                Component object = this.getObject();

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

                    // Una componente de un atributo compuesto siempre es común
                    if (nombre != null) {
                        if (object.getClass().toString().equals("class com.bdd.mer.estatica.atributo.Atributo")) {
                            ((Atributo) object).addComponent(new Atributo(object, nombre,
                                    boxOpcional.isSelected(),
                                    boxMultivaluado.isSelected(),
                                    TipoAtributo.COMMON));
                        }
                    }

                    panelDibujo.repaint();

                }
            });
            add(addComponent);
        }
    }

    private TipoAtributo selectAtributeType() {

        // Crea los radio buttons
        JRadioButton commonAttributeOption = new JRadioButton("Común", true);
        JRadioButton alternativeAttributeOption = new JRadioButton("Alternativo");
        JRadioButton mainAttributeOption = new JRadioButton("Principal");

        // Agrupa los radio buttons para que solo se pueda seleccionar uno a la vez
        ButtonGroup group = new ButtonGroup();
        group.add(commonAttributeOption);
        group.add(alternativeAttributeOption);
        group.add(mainAttributeOption);

        // Crea un panel para contener los radio buttons
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Añade un BoxLayout al panel

        // Crea un panel para el grupo de radio buttons
        JPanel panelAttribute = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAttribute.add(commonAttributeOption);
        panelAttribute.add(alternativeAttributeOption);
        panelAttribute.add(mainAttributeOption);

        // Agrega los pares de opciones al panel
        panel.add(panelAttribute);

        // Muestra el panel en un JOptionPane
        JOptionPane.showOptionDialog(null, panel, "Tipo de atributo: ",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if (commonAttributeOption.isSelected()) {
            return TipoAtributo.COMMON;
        } else if (alternativeAttributeOption.isSelected()) {
            return TipoAtributo.ALTERNATIVE;
        } else {
            return TipoAtributo.MAIN;
        }
    }

    public void setObject(Component object) {
        this.objeto = object;
    }

    Component getObject() {
        return objeto;
    }

    public void setMousePosition(MouseEvent e) {
        this.x = e.getX();
        this.y = e.getY();
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
}
