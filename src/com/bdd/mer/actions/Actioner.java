package com.bdd.mer.actions;

import com.bdd.mer.components.atributo.*;
import com.bdd.mer.components.atributo.symbology.AttributeArrow;
import com.bdd.mer.components.atributo.symbology.AttributeEnding;
import com.bdd.mer.components.atributo.symbology.AttributeSymbol;
import com.bdd.mer.components.macroEntity.MacroEntity;
import com.bdd.mer.components.hierarchy.HierarchyType;
import com.bdd.mer.components.hierarchy.TotalHierarchy;
import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.components.entity.WeakEntity;
import com.bdd.mer.components.hierarchy.Hierarchy;
import com.bdd.mer.components.relationship.cardinality.Cardinality;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.components.relationship.cardinality.StaticCardinality;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.components.note.Note;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Actioner implements Serializable {

    private final DrawingPanel drawingPanel;

    public Actioner(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    /* ---------------------------------------------------------------------------------------------------------- */
    /*                                              Add Entity                                                    */
    /* ---------------------------------------------------------------------------------------------------------- */

    public void addEntity() {

        // Muestra una ventana emergente para ingresar el nombre de la entidad. La ventana está centrada en el marco principal
        String entityName = JOptionPane.showInputDialog(this.drawingPanel, "Ingrese el nombre de la nueva entidad");

        if (entityName != null) {
            if (!entityName.isEmpty()) {
                if (!this.drawingPanel.existsComponent(entityName)) {
                    // Si el usuario ingresó un nombre, agrega una nueva entidad con ese nombre
                    Entity newEntity = new Entity(entityName, drawingPanel.getMouseX(), drawingPanel.getMouseY());
                    //newEntity.setPopupMenu(getGenericPop(newEntity));
                    drawingPanel.addComponent(newEntity);
                } else {
                    JOptionPane.showMessageDialog(this.drawingPanel, "El nombre de la entidad ya está en uso.");
                }
            } else {
                JOptionPane.showMessageDialog(this.drawingPanel, "El nombre de la entidad no puede estar vacío");
            }
        }
    }

    /* ---------------------------------------------------------------------------------------------------------- */
    /*                                           Add Relationship                                                 */
    /* ---------------------------------------------------------------------------------------------------------- */

    public void addRelationship() {
        boolean canceledRelationship = false;

        // Solo procede si se han seleccionado entre 1 y 3 entidades
        if (drawingPanel.onlyThisClassIsSelected(Entity.class) && drawingPanel.isNumberOfSelectedComponentsBetween(1, 3)) {
            // Muestra una ventana emergente para ingresar el nombre de la relación

            String nombre = JOptionPane.showInputDialog(this.drawingPanel, "Ingrese el nombre de la nueva relación");

            if (nombre != null && !nombre.isEmpty()) {

                Relationship newRelationship = new Relationship(nombre, drawingPanel.getMouseX(),drawingPanel.getMouseY());

                List<Cardinality> cardinalities = new ArrayList<>();

                for (Component entity : drawingPanel.getSelectedComponents()) {

                    // It's safe, due to I asked at the stat if only objects from the Entity class are selected.
                    Entity castedEntity = (Entity) entity;

                    Cardinality cardinality = getCardinality(castedEntity);

                    if (cardinality != null) {

                        cardinalities.add(cardinality);
                        newRelationship.addEntity(castedEntity, cardinality);

                    } else {
                        canceledRelationship = true;
                        break;
                    }
                }

                if (!canceledRelationship) {

                    drawingPanel.addComponent(newRelationship);

                    for (Cardinality cardinality : cardinalities) {
                        drawingPanel.addComponent(cardinality);
                    }

                    drawingPanel.limpiarEntidadesSeleccionadas();
                }

            }
        } else {
            JOptionPane.showMessageDialog(this.drawingPanel, "Debe seleccionar entre 1 y 3 entidades para crear una relación.");
        }
    }

    private Cardinality getCardinality(Entity entity) {
        JTextField cardinalidadMinimaCampo = new JTextField(1);
        JTextField cardinalidadMaximaCampo = new JTextField(1);
        JButton okButton = new JButton("OK");
        okButton.setEnabled(false); // Deshabilita el botón OK inicialmente

        JPanel miPanel = new JPanel();
        miPanel.add(new JLabel("Ingrese las cardinalidad para la entidad " + entity.getText() + ": "));
        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
        miPanel.add(new JLabel("Cardinalidad mínima:"));
        miPanel.add(cardinalidadMinimaCampo);
        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
        miPanel.add(new JLabel("Cardinalidad máxima:"));
        miPanel.add(cardinalidadMaximaCampo);

        // Crea un JOptionPane personalizado
        JOptionPane optionPane = new JOptionPane(miPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

        // Crea el cuadro de diálogo basado en el JOptionPane
        JDialog dialog = optionPane.createDialog("Por favor ingrese dos valores");

        // Establece el foco en el campo de la cardinalidad mínima al abrir el cuadro de diálogo
        dialog.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                cardinalidadMinimaCampo.requestFocusInWindow();
            }
        });

        // Muestra el cuadro de diálogo
        dialog.setVisible(true);

        int resultado = (int) optionPane.getValue();
        if (resultado == JOptionPane.OK_OPTION) {
            String cardinalidadMinima = cardinalidadMinimaCampo.getText();
            String cardinalidadMaxima = cardinalidadMaximaCampo.getText();

            if (cardinalidadMinima.isEmpty() || cardinalidadMaxima.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ambos campos deben tener un valor");
                return getCardinality(entity);
            } else {
                return new Cardinality(String.valueOf(cardinalidadMinima.charAt(0)), String.valueOf(cardinalidadMaxima.charAt(0)));
            }
        } else {
            return null;
        }
    }

    public void changeCardinality(Cardinality cardinality) {
        JTextField cardinalidadMinimaCampo = new JTextField(1);
        JTextField cardinalidadMaximaCampo = new JTextField(1);
        JButton okButton = new JButton("OK");
        okButton.setEnabled(false); // Deshabilita el botón OK inicialmente

        JPanel miPanel = new JPanel();
        miPanel.add(new JLabel("Ingrese las cardinalidad para la entidad " + cardinality.getOwner().getText() + ": "));
        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
        miPanel.add(new JLabel("Cardinalidad mínima:"));
        miPanel.add(cardinalidadMinimaCampo);
        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
        miPanel.add(new JLabel("Cardinalidad máxima:"));
        miPanel.add(cardinalidadMaximaCampo);

        // Crea un JOptionPane personalizado
        JOptionPane optionPane = new JOptionPane(miPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

        // Crea el cuadro de diálogo basado en el JOptionPane
        JDialog dialog = optionPane.createDialog("Por favor ingrese dos valores");

        // Establece el foco en el campo de la cardinalidad mínima al abrir el cuadro de diálogo
        dialog.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                cardinalidadMinimaCampo.requestFocusInWindow();
            }
        });

        // Muestra el cuadro de diálogo
        dialog.setVisible(true);

        int resultado = (int) optionPane.getValue();
        if (resultado == JOptionPane.OK_OPTION) {
            String cardinalidadMinima = cardinalidadMinimaCampo.getText();
            String cardinalidadMaxima = cardinalidadMaximaCampo.getText();

            if (cardinalidadMinima.isEmpty() || cardinalidadMaxima.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ambos campos deben tener un valor");
            } else {
                cardinality.setText(Cardinality.giveFormat(cardinalidadMinima, cardinalidadMaxima));
                this.drawingPanel.repaint();
            }
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                           Add Dependency                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void addDependency() {

        boolean canceledRelationship = false;

        if (drawingPanel.onlyThisClassIsSelected(Entity.class) && drawingPanel.isNumberOfSelectedComponents(2)) {

            String nombre = JOptionPane.showInputDialog(this.drawingPanel, "Ingrese el nombre de la nueva dependencia");

            if (nombre != null) {

                Relationship newRelationship = new Relationship(nombre, drawingPanel.getMouseX(),drawingPanel.getMouseY());

                Entity entitySelected = selectWeakEntity();

                if (entitySelected != null) {
                    WeakEntity weakVersion = entitySelected.getWeakVersion();

                    Cardinality cardinality = null;
                    StaticCardinality staticCardinality = null;

                    for (Entity entity : drawingPanel.getSelectedEntities()) {

                        if (entity.equals(entitySelected)) {

                            cardinality = getCardinality(entity);

                            if (cardinality != null) {

                                newRelationship.addEntity(entity, cardinality);

                            } else {

                                canceledRelationship = true;
                                break;

                            }
                        } else {

                            // Una entidad débil solo puede estar relacionada con una entidad fuerte si
                            // esta última tiene cardinalidad 1:1
                            staticCardinality = new StaticCardinality("1", "1");
                            newRelationship.addEntity(entity, staticCardinality);

                        }
                    }

                    if (!canceledRelationship) {

                        drawingPanel.addComponent(newRelationship);
                        drawingPanel.addComponent(cardinality);
                        drawingPanel.addComponent(staticCardinality);

                        changeEntity(entitySelected, weakVersion);

                        drawingPanel.limpiarEntidadesSeleccionadas();
                    }
                }

            }
        } else {
            JOptionPane.showMessageDialog(this.drawingPanel, "Debe seleccionar 2 entidades para crear una dependencia.");
        }
    }

    private Entity selectWeakEntity() {

        // Define las opciones para los botones
        Object[] opciones = {drawingPanel.getSelectedEntities().getFirst().getText(),
                drawingPanel.getSelectedEntities().getLast().getText()};

        // Muestra el JOptionPane con los botones
        int seleccion = JOptionPane.showOptionDialog(
                this.drawingPanel,
                "Seleccione la entidad débil de la relación",
                "Selección",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        return switch (seleccion) {
            case 0 -> (drawingPanel.getSelectedEntities().getFirst());
            case 1 -> (drawingPanel.getSelectedEntities().getLast());
            default -> {
                JOptionPane.showMessageDialog(this.drawingPanel, "Seleccione una entidad débil");
                yield selectWeakEntity();
            }
        };
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                            Add Hierarchy                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void addHierarchy() {
        // Solo procede si se ha seleccionado al menos tres entidades
        if (drawingPanel.onlyThisClassIsSelected(Entity.class) && drawingPanel.getSelectedComponents().size() >= 3) {

            Entity supertipo = seleccionarSupertipo();

            main: if (supertipo != null && !supertipo.isAlreadyParent()) {

                List<Entity> subtipos = obtenerListaSubtipos(supertipo);

                Hierarchy newHierarchy = getHierarchy(supertipo);

                if (newHierarchy == null) {
                    return;
                }

                supertipo.addHierarchy(newHierarchy);

                for (Entity subtipo : subtipos) {
                    newHierarchy.addChild(subtipo);

                    if (!subtipo.addHierarchy(newHierarchy)) {

                        // Acción reparadora.
                        supertipo.removeHierarchy(newHierarchy);
                        for (Entity s : subtipos) {
                            s.removeHierarchy(newHierarchy);

                            String message = "La entidad "
                                    + '\"' + subtipo.getText() + '\"'
                                    + " ya participa de una jerarquía. "
                                    + "La herencia múltiple solamente es permitida si se tiene el mismo padre.";

                            JOptionPane.showMessageDialog(this.drawingPanel, message);

                            // Salida
                            break main;
                        }
                    }
                }

                drawingPanel.addComponent(newHierarchy);

                } else {
                    JOptionPane.showMessageDialog(this.drawingPanel, "La entidad seleccionada ya participa en otra jerarquía.");
                }
        } else {
            JOptionPane.showMessageDialog(this.drawingPanel, "Seleccione al menos tres entidades.");
        }

        // Desactiva el modo de selección
        drawingPanel.limpiarEntidadesSeleccionadas();
        drawingPanel.repaint();
    }

    public Hierarchy getHierarchy(Entity supertipo) {

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
        int option = JOptionPane.showOptionDialog(null, panel, "Elige una opción",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        // If the user clicked Cancel or closed the window
        if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.CANCEL_OPTION) {
            return null; // Cancel the process
        }

        HierarchyType letter = (opcionExclusiva.isSelected()) ? HierarchyType.DISJUNCT : HierarchyType.OVERLAPPING;

        Hierarchy newHierarchy;

        if (opcionTotal.isSelected()) {
            newHierarchy = new TotalHierarchy(letter, supertipo);
        } else {
            newHierarchy = new Hierarchy(letter, supertipo);
        }

        return newHierarchy;
    }

    public Entity seleccionarSupertipo() {

        List<Entity> entidadesSeleccionadas = drawingPanel.getSelectedEntities();
        Object[] opciones = new Object[entidadesSeleccionadas.size()];

        for (int i = 0; i < entidadesSeleccionadas.size(); i++) {
            opciones[i] = (entidadesSeleccionadas.get(i)).getText();
        }

        // Muestra el JOptionPane con los botones
        int seleccion = JOptionPane.showOptionDialog(null, "Seleccione a la entidad supertipo", "Selección",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        return (entidadesSeleccionadas.get(seleccion));

    }

    public List<Entity> obtenerListaSubtipos(Entity supertipo) {

        List<Entity> entidadesSeleccionadas = drawingPanel.getSelectedEntities();

        List<Entity> retorno = new ArrayList<>(entidadesSeleccionadas);
        retorno.remove((supertipo));

        return retorno;
    }

    public void swapExclusivity(Hierarchy hierarchy) {

        if (hierarchy.getExclusivity().equals(HierarchyType.DISJUNCT)) {
            hierarchy.setExclusivity(HierarchyType.OVERLAPPING);
        } else {
            hierarchy.setExclusivity(HierarchyType.DISJUNCT);
        }

        this.drawingPanel.repaint();

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                 Add Note                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void addNote() {
        // Muestra una ventana emergente para ingresar el contenido de la nota
        String contenido = JOptionPane.showInputDialog(this.drawingPanel, "Ingrese el contenido de la nueva nota");
        if (contenido != null) {
            // Si el usuario ingresó contenido, agrega una nueva nota con ese contenido
            drawingPanel.addComponent(new Note(contenido, drawingPanel.getMouseX(), drawingPanel.getMouseY()));
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                         Delete Selected Components                                             */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void deleteSelectedComponents() {

        List<Component> selectedComponents = this.drawingPanel.getSelectedComponents();

        if (!selectedComponents.isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(this.drawingPanel, "¿Seguro de que desea eliminar el objeto seleccionado?");
            if (confirmacion == JOptionPane.YES_OPTION) {

                List<Component> componentsForRemoval = new ArrayList<>();

                for (Component component : selectedComponents) {

                    if (!component.canBeDeleted()) {
                        return;
                    }

                    componentsForRemoval.addAll(component.getComponentsForRemoval());
                }

                for (Component component : componentsForRemoval) {
                    this.drawingPanel.removeComponent(component);
                }

            }

            this.drawingPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(null, "Debe seleccionar al menos un elemento.");
        }

        // Desactiva el modo de selección
        drawingPanel.limpiarEntidadesSeleccionadas();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Rename Component                                                 */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void renameComponent(Component component) {

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
            component.setText(newText);
            this.drawingPanel.repaint();
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                Add Attribute                                                   */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void addAttribute(AttributableComponent component, AttributeSymbol attributeSymbol) {

        // Creation of the components of the panel.
        JTextField fieldNombre = new JTextField(10);
        JCheckBox boxOptional = new JCheckBox("Optional");
        JCheckBox boxMultivalued = new JCheckBox("Multivalued");

        // Crea un array de los componentes
        Object[] message = {
                "Ingrese el nombre del atributo:", fieldNombre,
                "Seleccione las opciones:", boxOptional, boxMultivalued
        };

        // Muestra el JOptionPane
        int option = JOptionPane.showConfirmDialog(null, message, "Ingrese la información del atributo", JOptionPane.OK_CANCEL_OPTION);

        // Muestra una ventana emergente para ingresar el nombre del atributo y seleccionar las opciones
        if (option == JOptionPane.OK_OPTION) {

            String nombre = fieldNombre.getText();

            if (nombre != null) {

                AttributeArrow arrowBody = (boxOptional.isSelected()) ? AttributeArrow.OPTIONAL : AttributeArrow.NON_OPTIONAL;
                AttributeEnding arrowEnding = (boxMultivalued.isSelected()) ? AttributeEnding.MULTIVALUED : AttributeEnding.NON_MULTIVALUED;

                Attribute newAttribute = new Attribute(component, nombre, attributeSymbol, arrowBody, arrowEnding);

                component.addAttribute(newAttribute);

                drawingPanel.addComponent(newAttribute);
            }
        }

    }

    public void addComplexAttribute(AttributableComponent component) {

        AttributeSymbol attributeSymbol = selectAtributeType();

        if (attributeSymbol.equals(AttributeSymbol.MAIN)) {

            if (component.hasMainAttribute()) {
                JOptionPane.showMessageDialog(null, "The component can only have one main attribute.");
                return;
            }

            JTextField fieldNombre = new JTextField(10);

            int option = JOptionPane.showConfirmDialog(null, fieldNombre, "Ingrese el nombre del atributo:", JOptionPane.OK_CANCEL_OPTION);

            // Muestra una ventana emergente para ingresar el nombre del atributo y seleccionar las opciones
            if (option == JOptionPane.OK_OPTION) {

                String nombre = fieldNombre.getText();

                if (nombre != null) {

                    Attribute newAttribute = new MainAttribute(component, nombre);

                    component.addAttribute(newAttribute);

                    drawingPanel.addComponent(newAttribute);
                }
            }

        } else {

            addAttribute(component, attributeSymbol);

        }
    }

    private AttributeSymbol selectAtributeType() {

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
            return AttributeSymbol.COMMON;
        } else if (alternativeAttributeOption.isSelected()) {
            return AttributeSymbol.ALTERNATIVE;
        } else {
            return AttributeSymbol.MAIN;
        }
    }

    public void changeOptionality(Attribute attribute) {

        attribute.changeOptionality();
        drawingPanel.repaint();

    }

    public void changeMultivalued(Attribute attribute) {

        attribute.changeMultivalued();
        drawingPanel.repaint();

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                             Add Macro-Entity                                                   */
    /* -------------------------------------------------------------------------------------------------------------- */

    // There must be selected at least an entity and a relationship (unary relationship)
    public void addMacroEntity() {

        if (drawingPanel.getSelectedComponents().size() == 1 && drawingPanel.onlyThisClassIsSelected(Relationship.class)) {

            MacroEntity macroEntity = new MacroEntity((Relationship) drawingPanel.getSelectedComponents().getFirst());

            drawingPanel.addComponentLast(macroEntity);

            drawingPanel.limpiarEntidadesSeleccionadas();

        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una relación.");
        }
    }

    /*                                                                                                               */

    private void changeEntity(Entity oldEntity, Entity newEntity) {

        List<Component> components = this.drawingPanel.getListComponents();

        for (Component component : components) {
            component.changeReference(oldEntity, newEntity);
        }

        this.drawingPanel.replaceComponent(oldEntity, newEntity);

        this.drawingPanel.repaint();

    }

}
