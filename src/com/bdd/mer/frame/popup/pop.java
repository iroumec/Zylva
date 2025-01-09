//package com.bdd.mer.interfaz.popup;
//
//import com.bdd.mer.estatica.atributo.AttributeType;
//import com.bdd.mer.estatica.component.Component;
//import com.bdd.mer.estatica.entity.Entity;
//import com.bdd.mer.estatica.entity.Association;
//import com.bdd.mer.estatica.hierarchy.Hierarchy;
//import com.bdd.mer.estatica.relationship.Cardinality;
//import com.bdd.mer.estatica.relationship.Relationship;
//import com.bdd.mer.estatica.coleccion.Duplex;
//import com.bdd.mer.interfaz.DrawingPanel;
//import com.bdd.mer.components.note.Nota;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.util.ArrayList;
//import java.util.List;
//
//public class pop extends JPopupMenu {
//
//    private int x, y;
//    private Component objeto;
//    private final DrawingPanel drawingPanel;
//
//    public pop(DrawingPanel drawingPanel) {
//        this.drawingPanel = drawingPanel;
//
//
//        // Obtengo la posición del mouse
//        drawingPanel.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                if (e.isPopupTrigger()) {
//                    setMousePosition(e);
//                }
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                if (e.isPopupTrigger()) {
//                    setMousePosition(e);
//                }
//            }
//        });
//
//        /* ---------------------------------------------------------------------------------------------------------- */
//        /*                                              Add Entity                                                    */
//        /* ---------------------------------------------------------------------------------------------------------- */
//
//        JMenuItem addEntity = new JMenuItem("Agregar entidad");
//        addEntity.addActionListener(_ -> this.drawingPanel.getActioner().addEntity());
//        add(addEntity);
//
//        /* ---------------------------------------------------------------------------------------------------------- */
//        /*                                          Add Relationship                                                  */
//        /* ---------------------------------------------------------------------------------------------------------- */
//
//        JMenuItem addRelationship = new JMenuItem("Agregar relación");
//        addRelationship.addActionListener(_ -> {
//            boolean creacionRelacionCancelada = false;
//
//            // Solo procede si se han seleccionado entre 1 y 3 entidades
//            if (drawingPanel.participaSoloEntidades() && !drawingPanel.getSelectedComponents().isEmpty() && drawingPanel.getSelectedComponents().size() <= 3) {
//                // Muestra una ventana emergente para ingresar el nombre de la relación
//
//                String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva relación");
//                if (nombre != null) {
//                    // Si el usuario ingresó un nombre, agrega una nueva relación con ese nombre
//                    java.util.List<Duplex<Entity, Cardinality>> entidadesParticipantes = new ArrayList<>();
//                    Cardinality cardinalidad;
//                    for (Component entidad : drawingPanel.getSelectedComponents()) {
//                        cardinalidad = ingresarCardinalidad(((Entity) entidad).getText());
//                        if (cardinalidad != null) {
//                            entidadesParticipantes.add(new Duplex<>((Entity) entidad, cardinalidad));
//                        } else {
//                            creacionRelacionCancelada = true;
//                            break;
//                        }
//                    }
//                    if (!creacionRelacionCancelada) {
//                        drawingPanel.addComponent(new Relationship(nombre, entidadesParticipantes, this.x, this.y));
//                        drawingPanel.limpiarEntidadesSeleccionadas();
//                    }
//
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Debe seleccionar entre 1 y 3 entidades para crear una relación.");
//            }
//        });
//        add(addRelationship);
//
//        /* ---------------------------------------------------------------------------------------------------------- */
//        /*                                           Add Dependency                                                   */
//        /* ---------------------------------------------------------------------------------------------------------- */
//
//        JMenuItem addDependency = new JMenuItem("Agregar dependencia");
//        // Defino la función del botón
//        addDependency.addActionListener(_ -> {
//            boolean creacionRelacionCancelada = false;
//
//            // Solo procede si se han seleccionado entre 1 y 3 entidades
//            if (drawingPanel.participaSoloEntidades() && drawingPanel.getSelectedComponents().size() == 2) {
//                // Muestra una ventana emergente para ingresar el nombre de la relación
//
//                String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva dependencia");
//                if (nombre != null) {
//                    // Si el usuario ingresó un nombre, agrega una nueva relación con ese nombre
//                    Entity EntityDebil = seleccionarEntidadDebil();
//
//                    List<Duplex<Entity, Cardinality>> entidadesParticipantes = new ArrayList<>();
//                    Cardinality cardinalidad;
//
//                    for (Component entidad : drawingPanel.getSelectedComponents()) {
//                        if (entidad == EntityDebil) {
//                            cardinalidad = ingresarCardinalidad(((Entity) entidad).getText());
//                            if (cardinalidad != null) {
//                                entidadesParticipantes.add(new Duplex<>((Entity) entidad, cardinalidad));
//                            } else {
//                                creacionRelacionCancelada = true;
//                                break;
//                            }
//                        } else {
//                            // Una entidad débil solo puede estar relacionada con una entidad fuerte si
//                            // esta última tiene cardinalidad 1:1
//                            entidadesParticipantes.add(new Duplex<>((Entity) entidad, new Cardinality(entidad, "1", "1")));
//                        }
//                    }
//
//                    if (!creacionRelacionCancelada) {
//                        drawingPanel.addComponent(new Relationship(nombre, entidadesParticipantes, this.x, this.y));
//                        drawingPanel.limpiarEntidadesSeleccionadas();
//                    }
//
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Debe seleccionar 2 entidades para crear una dependencia.");
//            }
//
//            // Desactiva el modo de selección
//            //panelDibujo.setSeleccionando(false);
//        });
//        add(addDependency);
//
//        /* ---------------------------------------------------------------------------------------------------------- */
//        /*                                           Add Hierarchy                                                    */
//        /* ---------------------------------------------------------------------------------------------------------- */
//
//        JMenuItem addHierarchy = new JMenuItem("Agregar jerarquía");
//        addHierarchy.addActionListener(_ -> {
//            // Solo procede si se ha seleccionado al menos tres entidades
//            if (drawingPanel.getSelectedComponents().size() >= 3 && drawingPanel.participaSoloEntidades()) {
//                Entity supertipo = seleccionarSupertipo();
//                List<Entity> subtipos = obtenerListaSubtipos(supertipo);
//                if (rolesJerarquiaOcupados(supertipo, subtipos)) {
//                    ocuparRoles(supertipo, subtipos);
//                    Duplex<Boolean, Boolean> tipo = seleccionarTipoJerarquia();
//
//                    Hierarchy newHierarchy = new Hierarchy("", supertipo, subtipos, "u");
//
//                    drawingPanel.addComponent(newHierarchy);
//
//                    // Add the hierarchy to the entities
//                    for (Component e : drawingPanel.getSelectedComponents()) {
//                        ((Entity) e).addHierarchy(newHierarchy);
//                    }
//
//                } else {
//                    JOptionPane.showMessageDialog(this, "Una o más entidades seleccionadas ya ocupan el rol seleccionado en otra jerarquía.");
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Seleccione al menos tres entidades.");
//            }
//
//            // Desactiva el modo de selección
//            //panelDibujo.setSeleccionando(false);
//            drawingPanel.limpiarEntidadesSeleccionadas();
//            drawingPanel.repaint();
//        });
//        add(addHierarchy);
//
//        /* ---------------------------------------------------------------------------------------------------------- */
//        /*                                         Add Macro-entity                                                   */
//        /* ---------------------------------------------------------------------------------------------------------- */
//
//        JMenuItem addMacroEntity = new JMenuItem("Agregar macro-entidad");
//        addMacroEntity.addActionListener(_ -> {
//            // There must be selected at least an entity and a relationship (unary relationship)
//            if (drawingPanel.getSelectedComponents().size() >= 2 && drawingPanel.oneRelationshipAndEntities()) {
//                String text = JOptionPane.showInputDialog(this, "Ingrese el nombre de la macro-entidad");
//                if (text != null && !text.isBlank()) {
//                    Association macroEntity = new Association(text);
//                    for (Component component : drawingPanel.getSelectedComponents()) {
//                        if (component.getClass().toString().equals("class com.bdd.mer.estatica.Entidad")) {
//                            macroEntity.addEntity((Entity) component);
//                        } else {
//                            macroEntity.setParticipatingRelationship((Relationship) component);
//                        }
//                    }
//
//                    drawingPanel.addComponent(macroEntity);
//                    drawingPanel.limpiarEntidadesSeleccionadas();
//                    drawingPanel.repaint();
//                } else {
//                    JOptionPane.showMessageDialog(this, "El texto no puede estar vacío");
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Seleccione al menos dos componentes, entre las que se halle una relación.");
//            }
//        });
//        add(addMacroEntity);
//
//        /* ---------------------------------------------------------------------------------------------------------- */
//        /*                                             Add Note                                                       */
//        /* ---------------------------------------------------------------------------------------------------------- */
//
//        JMenuItem addNote = new JMenuItem("Agregar nota");
//        addNote.addActionListener(_ -> {
//            // Muestra una ventana emergente para ingresar el contenido de la nota
//            String contenido = JOptionPane.showInputDialog(this, "Ingrese el contenido de la nueva nota");
//            if (contenido != null) {
//                // Si el usuario ingresó contenido, agrega una nueva nota con ese contenido
//                drawingPanel.addComponent(new Nota(contenido, this.x, this.y));
//            }
//            drawingPanel.repaint();
//        });
//        add(addNote);
//    }
//
//    public pop(DrawingPanel drawingPanel, boolean attribute, boolean rename, boolean delete, boolean compound) {
//        this.drawingPanel = drawingPanel;
//
//        if (compound) {
//            JMenuItem addComponent =  new JMenuItem("Agregar componente (atributo compuesto)");
//            addComponent.addActionListener(_ ->  {
//                Component object = this.getObject();
//
//                // Crea los componentes del panel
//                JTextField fieldNombre = new JTextField(10);
//                JCheckBox boxOpcional = new JCheckBox("Opcional");
//                JCheckBox boxMultivaluado = new JCheckBox("Multivaluado");
//
//                // Crea un array de los componentes
//                Object[] message = {
//                        "Ingrese el nombre del atributo:", fieldNombre,
//                        "Seleccione las opciones:", boxOpcional, boxMultivaluado
//                };
//
//                // Muestra el JOptionPane
//                int option = JOptionPane.showConfirmDialog(null, message, "Ingrese la información del atributo", JOptionPane.OK_CANCEL_OPTION);
//
//                // Muestra una ventana emergente para ingresar el nombre del atributo y seleccionar las opciones
//                if (option == JOptionPane.OK_OPTION) {
//                    String nombre = fieldNombre.getText();
//
//                    // Una componente de un atributo compuesto siempre es común
//                    if (nombre != null) {
//                        if (object.getClass().toString().equals("class com.bdd.mer.estatica.atributo.Atributo")) {
//
//                        }
//                    }
//
//                    drawingPanel.repaint();
//
//                }
//            });
//            add(addComponent);
//        }
//    }
//
//    private JMenuItem activateConcealment() {
//        JMenuItem activateConcealment = new JMenuItem("Activar ocultamiento");
//        activateConcealment.addActionListener(_ -> {
//            Component component = this.getObject();
//
//            if (((Association) component).isConcealmentActivated()) {
//                drawingPanel.addComponents(((Association) component).participatingComponents());
//            } else {
//                drawingPanel.deleteComponents(((Association) component).participatingComponents());
//            }
//
//            ((Association) component).exchangeConcealment();
//            drawingPanel.repaint();
//        });
//
//        return activateConcealment;
//    }
//
//    private JMenuItem changeCardinality() {
//        JMenuItem changeCardinality = new JMenuItem("Modificar cardinalidad");
//        changeCardinality.addActionListener(_ -> {
//
//            List<Duplex<Entity, Cardinality>> entitiesAndCardinalities = ((Relationship) this.objeto).getEntidades();
//            List<Cardinality> newCardinalities = new ArrayList<>();
//
//            JTextField cardinalidadMinimaCampo = new JTextField(1);
//            JTextField cardinalidadMaximaCampo = new JTextField(1);
//            JButton okButton = new JButton("OK");
//            okButton.setEnabled(false); // Deshabilita el botón OK inicialmente
//
//            boolean changeCardinalitiesWasCanceled = false;
//
//            mainLoop: for (Duplex<Entity, Cardinality> pair : entitiesAndCardinalities) {
//                boolean complete = false;
//                while (!complete) {
//                    JPanel miPanel = new JPanel();
//                    miPanel.add(new JLabel("Ingrese la nueva cardinalidad para la entidad " + pair.getFirst().getText() + ": "));
//                    miPanel.add(Box.createVerticalStrut(15)); // Espaciador
//                    miPanel.add(new JLabel("Cardinalidad mínima:"));
//                    miPanel.add(cardinalidadMinimaCampo);
//                    miPanel.add(Box.createVerticalStrut(15)); // Espaciador
//                    miPanel.add(new JLabel("Cardinalidad máxima:"));
//                    miPanel.add(cardinalidadMaximaCampo);
//
//                    int resultado = JOptionPane.showConfirmDialog(null, miPanel,
//                            "Por favor ingrese dos valores", JOptionPane.OK_CANCEL_OPTION);
//                    if (resultado == JOptionPane.OK_OPTION) {
//                        String cardinalidadMinima = cardinalidadMinimaCampo.getText();
//                        String cardinalidadMaxima = cardinalidadMaximaCampo.getText();
//
//                        if (cardinalidadMinima.isEmpty() || cardinalidadMaxima.isEmpty()) {
//                            JOptionPane.showMessageDialog(null, "Ambos campos deben tener un valor");
//                        } else {
//                            complete = true;
//                            newCardinalities.add(new Cardinality(cardinalidadMinima.charAt(0), cardinalidadMaxima.charAt(0)));
//                        }
//                    } else {
//                        changeCardinalitiesWasCanceled = true;
//                        break mainLoop;
//                    }
//                }
//            }
//
//            if (!changeCardinalitiesWasCanceled) {
//                ((Relationship) this.objeto).changeCardinality(newCardinalities);
//            }
//
//            drawingPanel.repaint();
//        });
//
//        return changeCardinality;
//    }
//
//    private AttributeType selectAtributeType() {
//
//        // Crea los radio buttons
//        JRadioButton commonAttributeOption = new JRadioButton("Común", true);
//        JRadioButton alternativeAttributeOption = new JRadioButton("Alternativo");
//        JRadioButton mainAttributeOption = new JRadioButton("Principal");
//
//        // Agrupa los radio buttons para que solo se pueda seleccionar uno a la vez
//        ButtonGroup group = new ButtonGroup();
//        group.add(commonAttributeOption);
//        group.add(alternativeAttributeOption);
//        group.add(mainAttributeOption);
//
//        // Crea un panel para contener los radio buttons
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Añade un BoxLayout al panel
//
//        // Crea un panel para el grupo de radio buttons
//        JPanel panelAttribute = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        panelAttribute.add(commonAttributeOption);
//        panelAttribute.add(alternativeAttributeOption);
//        panelAttribute.add(mainAttributeOption);
//
//        // Agrega los pares de opciones al panel
//        panel.add(panelAttribute);
//
//        // Muestra el panel en un JOptionPane
//        JOptionPane.showOptionDialog(null, panel, "Tipo de atributo: ",
//                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
//
//        if (commonAttributeOption.isSelected()) {
//            return AttributeType.COMMON;
//        } else if (alternativeAttributeOption.isSelected()) {
//            return AttributeType.ALTERNATIVE;
//        } else {
//            return AttributeType.MAIN;
//        }
//    }
//
//    public void setObject(Component object) {
//        this.objeto = object;
//    }
//
//    Component getObject() {
//        return objeto;
//    }
//
//    public void setMousePosition(MouseEvent e) {
//        this.x = e.getX();
//        this.y = e.getY();
//    }
//
//    /* -------------------------------------------------------------------------------------------------------------- */
//    /* -------------------------------------------------------------------------------------------------------------- */
//    /* -------------------------------------------------------------------------------------------------------------- */
//
//    private Cardinality ingresarCardinalidad(String nombre) {
//        JTextField cardinalidadMinimaCampo = new JTextField(1);
//        JTextField cardinalidadMaximaCampo = new JTextField(1);
//        JButton okButton = new JButton("OK");
//        okButton.setEnabled(false); // Deshabilita el botón OK inicialmente
//
//        JPanel miPanel = new JPanel();
//        miPanel.add(new JLabel("Ingrese las cardinalidad para la entidad " + nombre + ": "));
//        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
//        miPanel.add(new JLabel("Cardinalidad mínima:"));
//        miPanel.add(cardinalidadMinimaCampo);
//        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
//        miPanel.add(new JLabel("Cardinalidad máxima:"));
//        miPanel.add(cardinalidadMaximaCampo);
//
//        int resultado = JOptionPane.showConfirmDialog(null, miPanel,
//                "Por favor ingrese dos valores", JOptionPane.OK_CANCEL_OPTION);
//        if (resultado == JOptionPane.OK_OPTION) {
//            String cardinalidadMinima = cardinalidadMinimaCampo.getText();
//            String cardinalidadMaxima = cardinalidadMaximaCampo.getText();
//
//            if (cardinalidadMinima.isEmpty() || cardinalidadMaxima.isEmpty()) {
//                JOptionPane.showMessageDialog(null, "Ambos campos deben tener un valor");
//                return ingresarCardinalidad(nombre);
//            } else {
//                return new Cardinality(cardinalidadMinima.charAt(0), cardinalidadMaxima.charAt(0));
//            }
//        } else {
//            return null;
//        }
//    }
//
//    /* -------------------------------------------------------------------------------------------------------------- */
//    /* -------------------------------------------------------------------------------------------------------------- */
//    /* -------------------------------------------------------------------------------------------------------------- */
//
//    public Entity seleccionarEntidadDebil() {
//
//        // Define las opciones para los botones
//        Object[] opciones = {((Entity) drawingPanel.getSelectedComponents().getFirst()).getText(),
//                ((Entity) drawingPanel.getSelectedComponents().getLast()).getText()};
//
//        // Muestra el JOptionPane con los botones
//        int seleccion = JOptionPane.showOptionDialog(this, "Seleccione la entidad débil de la relación", "Selección",
//                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);
//
//        // Determina qué botón se seleccionó
//        if (seleccion == 0) {
//            //((Entity) panelDibujo.getSelectedComponents().getFirst()).setEntidadDebil(Boolean.TRUE);
//            return ((Entity) drawingPanel.getSelectedComponents().getFirst());
//        } else if (seleccion == 1) {
//            //((Entity) panelDibujo.getSelectedComponents().getLast()).setEntidadDebil(Boolean.TRUE);
//            return ((Entity) drawingPanel.getSelectedComponents().getLast());
//        } else {
//            JOptionPane.showMessageDialog(this, "Seleccione una entidad débil");
//            seleccionarEntidadDebil();
//        }
//
//        return null;
//    }
//
//    public Entity seleccionarSupertipo() {
//
//        List<Component> entidadesSeleccionadas = drawingPanel.getSelectedComponents();
//        Object[] opciones = new Object[entidadesSeleccionadas.size()];
//
//        for (int i = 0; i < entidadesSeleccionadas.size(); i++) {
//            opciones[i] = (((Entity) entidadesSeleccionadas.get(i)).getText());
//        }
//
//        // Muestra el JOptionPane con los botones
//        int seleccion = JOptionPane.showOptionDialog(null, "Seleccione a la entidad supertipo", "Selección",
//                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);
//
//        if (seleccion < entidadesSeleccionadas.size()) {
//            return ((Entity) entidadesSeleccionadas.get(seleccion));
//        } else {
//            // Arreglar
//            return null;
//        }
//    }
//
//    public List<Entity> obtenerListaSubtipos(Entity superTipo) {
//        List<Component> entidadesSeleccionadas = drawingPanel.getSelectedComponents();
//        List<Entity> retorno = new ArrayList<>();
//
//        for (Component a : entidadesSeleccionadas) {
//            // Si tienen una referencia distinta al superTipo seleccionado
//            if (a != superTipo) {
//                retorno.add((Entity) a);
//            }
//        }
//
//        return retorno;
//    }
//
//    public Duplex<Boolean, Boolean> seleccionarTipoJerarquia() {
//
//        // Crea los radio buttons
//        JRadioButton opcionExclusiva = new JRadioButton("Exclusiva", true);
//        JRadioButton opcionCompartida = new JRadioButton("Compartida");
//        JRadioButton opcionTotal = new JRadioButton("Total", true);
//        JRadioButton opcionParcial = new JRadioButton("Parcial");
//
//        // Agrupa los radio buttons para que solo se pueda seleccionar uno a la vez
//        ButtonGroup groupExclusivaCompartida = new ButtonGroup();
//        groupExclusivaCompartida.add(opcionExclusiva);
//        groupExclusivaCompartida.add(opcionCompartida);
//
//        ButtonGroup groupTotalExclusiva = new ButtonGroup();
//        groupTotalExclusiva.add(opcionTotal);
//        groupTotalExclusiva.add(opcionParcial);
//
//        // Crea un panel para contener los radio buttons
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Añade un BoxLayout al panel
//
//        // Crea un panel para cada grupo de radio buttons
//        JPanel panelEC = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        panelEC.add(opcionExclusiva);
//        panelEC.add(opcionCompartida);
//
//        JPanel panelTP = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        panelTP.add(opcionTotal);
//        panelTP.add(opcionParcial);
//
//        // Agrega los pares de opciones al panel
//        panel.add(panelEC);
//        panel.add(panelTP);
//
//        // Muestra el panel en un JOptionPane
//        JOptionPane.showOptionDialog(null, panel, "Elige una opción",
//                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
//
//        return (new Duplex<>(opcionExclusiva.isSelected(), opcionTotal.isSelected()));
//    }
//
//    /*
//    Un supertipo solo puede ser supertipo de una única jerarquía. Un subtipo solo puede
//    ser subtipo de una única jerarquía.
//     */
//    public boolean rolesJerarquiaOcupados(Entity supertipo, List<Entity> subtipos) {
//        if (supertipo.isSuperTipo()) {
//            return false;
//        }
//
//        for (Entity e : subtipos) {
//            if (e.isSubTipo()) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    public void ocuparRoles(Entity supertipo, List<Entity> subtipos) {
//        supertipo.setSuperTipo(Boolean.TRUE);
//
//        for (Entity e : subtipos) {
//            e.setSubTipo(Boolean.TRUE);
//        }
//    }
//}
