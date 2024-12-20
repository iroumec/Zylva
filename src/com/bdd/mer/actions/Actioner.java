package com.bdd.mer.actions;

import com.bdd.mer.components.atributo.Attribute;
import com.bdd.mer.components.atributo.AttributeArrow;
import com.bdd.mer.components.atributo.AttributeEnding;
import com.bdd.mer.components.atributo.AttributeSymbol;
import com.bdd.mer.components.relationship.Dupla;
import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.components.entity.WeakEntity;
import com.bdd.mer.components.hierarchy.Hierarchy;
import com.bdd.mer.components.relationship.Cardinality;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.components.relationship.StaticCardinality;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.components.note.Note;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Actioner {

    private DrawingPanel drawingPanel;

    public Actioner(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    /* ---------------------------------------------------------------------------------------------------------- */
    /*                                              Add Entity                                                    */
    /* ---------------------------------------------------------------------------------------------------------- */

    public void addEntity() {
        // Muestra una ventana emergente para ingresar el nombre de la entidad. La ventana está centrada en el marco principal
        String nombre = JOptionPane.showInputDialog(this.drawingPanel, "Ingrese el nombre de la nueva entidad");

        if (nombre != null) {
            if (!nombre.isEmpty()) {
                // Si el usuario ingresó un nombre, agrega una nueva entidad con ese nombre
                Entity newEntity = new Entity(nombre, drawingPanel.getMouseX(), drawingPanel.getMouseY());
                //newEntity.setPopupMenu(getGenericPop(newEntity));
                drawingPanel.addComponent(newEntity);
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
        if (!drawingPanel.getSelectedEntities().isEmpty() && drawingPanel.getSelectedEntities().size() <= 3 && drawingPanel.getSelectedEntities().size() > 1) {
            // Muestra una ventana emergente para ingresar el nombre de la relación

            String nombre = JOptionPane.showInputDialog(this.drawingPanel, "Ingrese el nombre de la nueva relación");

            if (nombre != null) {

                Relationship newRelationship = new Relationship(nombre, drawingPanel.getMouseX(),drawingPanel.getMouseY());

                List<Cardinality> cardinalities = new ArrayList<>();

                for (Entity entity : drawingPanel.getSelectedEntities()) {
                    Cardinality cardinality = ingresarCardinalidad(entity);

                    if (cardinality != null) {

                        cardinalities.add(cardinality);
                        newRelationship.addEntity(entity, cardinality);

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

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                           Add Dependency                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void addDependency() {

        boolean canceledRelationship = false;

        System.out.println(drawingPanel.getSelectedEntities());

        if (!drawingPanel.getSelectedEntities().isEmpty() && drawingPanel.getSelectedEntities().size() == 2) {

            String nombre = JOptionPane.showInputDialog(this.drawingPanel, "Ingrese el nombre de la nueva dependencia");

            if (nombre != null) {

                Relationship newRelationship = new Relationship(nombre, drawingPanel.getMouseX(),drawingPanel.getMouseY());

                Entity entitySelected = seleccionarEntidadDebil();

                if (entitySelected != null) {
                    WeakEntity weakVersion = entitySelected.getWeakVersion();

                    List<Dupla<Entity, Cardinality>> entidadesParticipantes = new ArrayList<>();
                    Cardinality cardinality = null;
                    StaticCardinality staticCardinality = null;

                    for (Entity entity : drawingPanel.getSelectedEntities()) {

                        if (entity.equals(entitySelected)) {

                            cardinality = ingresarCardinalidad(entity);

                            if (cardinality != null) {

                                newRelationship.addEntity(entity, cardinality);

                            } else {

                                canceledRelationship = true;
                                break;

                            }
                        } else {

                            // Una entidad débil solo puede estar relacionada con una entidad fuerte si
                            // esta última tiene cardinalidad 1:1
                            staticCardinality = new StaticCardinality(entity, "1", "1");
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

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                            Add Hierarchy                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void addHierarchy() {
        // Solo procede si se ha seleccionado al menos tres entidades
        if (drawingPanel.getComponentesSeleccionadas().size() >= 3 && drawingPanel.participaSoloEntidades()) {
            Entity supertipo = seleccionarSupertipo();
            List<Entity> subtipos = obtenerListaSubtipos(supertipo);
            if (rolesJerarquiaOcupados(supertipo, subtipos)) {
                ocuparRoles(supertipo, subtipos);
                Dupla<Boolean, Boolean> tipo = seleccionarTipoJerarquia();

                Hierarchy newHierarchy = new Hierarchy("", supertipo, subtipos, "u");

                drawingPanel.addComponent(newHierarchy);

                // Add the hierarchy to the entities
                for (Component entity : drawingPanel.getComponentesSeleccionadas()) {
                    ((Entity) entity).addHierarchy(newHierarchy);
                }

            } else {
                JOptionPane.showMessageDialog(this.drawingPanel, "Una o más entidades seleccionadas ya ocupan el rol seleccionado en otra jerarquía.");
            }
        } else {
            JOptionPane.showMessageDialog(this.drawingPanel, "Seleccione al menos tres entidades.");
        }

        // Desactiva el modo de selección
        drawingPanel.limpiarEntidadesSeleccionadas();
        drawingPanel.repaint();
    }

    public void addNote() {
        // Muestra una ventana emergente para ingresar el contenido de la nota
        String contenido = JOptionPane.showInputDialog(this.drawingPanel, "Ingrese el contenido de la nueva nota");
        if (contenido != null) {
            // Si el usuario ingresó contenido, agrega una nueva nota con ese contenido
            drawingPanel.addComponent(new Note(contenido, drawingPanel.getMouseX(), drawingPanel.getMouseY()));
        }
    }

    public void deleteSelectedComponents() {

        List<Component> selectedComponents = this.drawingPanel.getComponentesSeleccionadas();

        if (!selectedComponents.isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(this.drawingPanel, "¿Seguro de que desea eliminar el objeto seleccionado?");
            if (confirmacion == JOptionPane.YES_OPTION) {


                List<Component> componentsForRemoval = new ArrayList<>();

                for (Component component : selectedComponents) {
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

    public void addAttribute() {

    }

    public void addComplexAttribute(AttributableComponent component) {

        // Creation of the components of the panel.
        JTextField fieldNombre = new JTextField(10);
        JCheckBox boxOptional = new JCheckBox("Optional");
        JCheckBox boxMultivalued = new JCheckBox("Multivalued");

        AttributeSymbol attributeSymbol = selectAtributeType();

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

        drawingPanel.repaint();

    }

    public void changeOptionality(Attribute attribute) {

        attribute.changeOptionality();
        drawingPanel.repaint();
        System.out.println("Hello");

    }

    public void changeCardinality(Cardinality cardinality) {
        
    }


    private Cardinality ingresarCardinalidad(Entity entity) {
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

        int resultado = JOptionPane.showConfirmDialog(null, miPanel,
                "Por favor ingrese dos valores", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            String cardinalidadMinima = cardinalidadMinimaCampo.getText();
            String cardinalidadMaxima = cardinalidadMaximaCampo.getText();

            if (cardinalidadMinima.isEmpty() || cardinalidadMaxima.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ambos campos deben tener un valor");
                return ingresarCardinalidad(entity);
            } else {
                return new Cardinality(String.valueOf(cardinalidadMinima.charAt(0)), String.valueOf(cardinalidadMaxima.charAt(0)));
            }
        } else {
            return null;
        }
    }

    private Entity seleccionarEntidadDebil() {

        // Define las opciones para los botones
        Object[] opciones = {drawingPanel.getSelectedEntities().getFirst().getText(),
                drawingPanel.getSelectedEntities().getLast().getText()};

        // Muestra el JOptionPane con los botones
        int seleccion = JOptionPane.showOptionDialog(this.drawingPanel, "Seleccione la entidad débil de la relación", "Selección",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        // Determina qué botón se seleccionó
        if (seleccion == 0) {
            //((Entity) panelDibujo.getComponentesSeleccionadas().getFirst()).setEntidadDebil(Boolean.TRUE);
            return (drawingPanel.getSelectedEntities().getFirst());
        } else if (seleccion == 1) {
            //((Entity) panelDibujo.getComponentesSeleccionadas().getLast()).setEntidadDebil(Boolean.TRUE);
            return (drawingPanel.getSelectedEntities().getFirst());
        } else {
            JOptionPane.showMessageDialog(this.drawingPanel, "Seleccione una entidad débil");
            seleccionarEntidadDebil();
        }

        return null;
    }

    public Entity seleccionarSupertipo() {

        List<Component> entidadesSeleccionadas = drawingPanel.getComponentesSeleccionadas();
        Object[] opciones = new Object[entidadesSeleccionadas.size()];

        for (int i = 0; i < entidadesSeleccionadas.size(); i++) {
            opciones[i] = (((Entity) entidadesSeleccionadas.get(i)).getText());
        }

        // Muestra el JOptionPane con los botones
        int seleccion = JOptionPane.showOptionDialog(null, "Seleccione a la entidad supertipo", "Selección",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion < entidadesSeleccionadas.size()) {
            return ((Entity) entidadesSeleccionadas.get(seleccion));
        } else {
            // Arreglar
            return null;
        }
    }

    public List<Entity> obtenerListaSubtipos(Entity superTipo) {
        List<Component> entidadesSeleccionadas = drawingPanel.getComponentesSeleccionadas();
        List<Entity> retorno = new ArrayList<>();

        for (Component a : entidadesSeleccionadas) {
            // Si tienen una referencia distinta al superTipo seleccionado
            if (a != superTipo) {
                retorno.add((Entity) a);
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
    public boolean rolesJerarquiaOcupados(Entity supertipo, List<Entity> subtipos) {
        if (supertipo.isSuperTipo()) {
            return false;
        }

        for (Entity e : subtipos) {
            if (e.isSubTipo()) {
                return false;
            }
        }

        return true;
    }

    public void ocuparRoles(Entity supertipo, List<Entity> subtipos) {
        supertipo.setSuperTipo(Boolean.TRUE);

        for (Entity e : subtipos) {
            e.setSubTipo(Boolean.TRUE);
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

    private void changeEntity(Entity oldEntity, Entity newEntity) {

        List<Component> components = this.drawingPanel.getListComponents();

        for (Component component : components) {
            component.changeReference(oldEntity, newEntity);
        }

        this.drawingPanel.removeComponent(oldEntity);

        this.drawingPanel.addComponent(newEntity);

        this.drawingPanel.repaint();

    }

}
