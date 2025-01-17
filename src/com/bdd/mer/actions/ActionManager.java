package com.bdd.mer.actions;

import com.bdd.mer.components.atributo.*;
import com.bdd.mer.components.atributo.symbology.AttributeArrow;
import com.bdd.mer.components.atributo.symbology.AttributeEnding;
import com.bdd.mer.components.atributo.symbology.AttributeSymbol;
import com.bdd.mer.components.association.Association;
import com.bdd.mer.components.hierarchy.HierarchyExclusivity;
import com.bdd.mer.components.hierarchy.TotalHierarchy;
import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.components.entity.WeakEntity;
import com.bdd.mer.components.hierarchy.Hierarchy;
import com.bdd.mer.components.line.GuardedLine;
import com.bdd.mer.components.line.Line;
import com.bdd.mer.components.line.lineMultiplicity.DoubleLine;
import com.bdd.mer.components.line.lineMultiplicity.SingleLine;
import com.bdd.mer.components.line.lineShape.DirectLine;
import com.bdd.mer.components.line.lineShape.SquaredLine;
import com.bdd.mer.components.relationship.cardinality.Cardinality;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.components.relationship.cardinality.StaticCardinality;
import com.bdd.mer.components.relationship.relatable.Relatable;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.components.note.Note;
import com.bdd.mer.frame.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class ActionManager implements Serializable {

    private final DrawingPanel drawingPanel;

    public ActionManager(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    /* ---------------------------------------------------------------------------------------------------------- */
    /*                                              Add Entity                                                    */
    /* ---------------------------------------------------------------------------------------------------------- */

    public void addEntity() {

        // It shows an emergent window to ask the user for the entity's name.
        String entityName = JOptionPane.showInputDialog(
                this.drawingPanel,
                null,
                LanguageManager.getMessage("actionManager.addEntity.dialog"), // Title.
                JOptionPane.QUESTION_MESSAGE // Message Type.
        );

        if (entityName != null) { // If it's null, the action was canceled.

            if (!entityName.isEmpty()) {

                if (!this.drawingPanel.existsComponent(entityName)) {

                    Entity newEntity = new Entity(entityName, drawingPanel.getMouseX(), drawingPanel.getMouseY(), this.drawingPanel);
                    drawingPanel.addComponent(newEntity);
                } else {

                    JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.nameDuplicated"));
                    addEntity();
                }
            } else {

                JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.emptyName"));
            }
        }
    }

    /* ---------------------------------------------------------------------------------------------------------- */
    /*                                           Add Relationship                                                 */
    /* ---------------------------------------------------------------------------------------------------------- */

    public void addRelationship() {

        int selectedComponents = this.drawingPanel.getSelectedComponents().size();

        if (drawingPanel.onlyTheseClassesAreSelected(Entity.class, WeakEntity.class, Association.class)
                && drawingPanel.isNumberOfSelectedComponentsBetween(1, 3)) {

            String nombre = JOptionPane.showInputDialog(this.drawingPanel, LanguageManager.getMessage("input.name"));

            if (nombre != null) {
                if (!nombre.isEmpty()) {

                    Relationship newRelationship = new Relationship(nombre, drawingPanel.getMouseX(),drawingPanel.getMouseY(), this.drawingPanel);

                    List<Cardinality> cardinalities = new ArrayList<>();

                    List<Line> lines = new ArrayList<>();

                    if (selectedComponents >= 2 && selectedComponents <= 3) {

                        for (Component component : drawingPanel.getSelectedComponents()) {

                            // It's safe, due to I asked at the stat if only objects from the Entity and Association classes are selected.
                            Relatable castedComponent = (Relatable) component;

                            Cardinality cardinality = new Cardinality("0", "N", this.drawingPanel);

                            cardinalities.add(cardinality);

                            GuardedLine guardedLine = new GuardedLine(
                                    this.drawingPanel,
                                    (Component) castedComponent,
                                    newRelationship, new DirectLine(),
                                    new SingleLine(),
                                    cardinality
                            );

                            lines.add(guardedLine);

                            newRelationship.addParticipant(castedComponent, guardedLine);

                        }

                    } else if (selectedComponents == 1) {

                        Relatable castedComponent = (Relatable) this.drawingPanel.getSelectedComponents().getFirst();

                        Cardinality firstCardinality = new Cardinality("0", "N", this.drawingPanel);
                        Cardinality secondCardinality = new Cardinality("0", "N", this.drawingPanel);

                        Line firstCardinalityLine = new GuardedLine(this.drawingPanel, (Component) castedComponent, newRelationship, new SquaredLine(), new SingleLine(), firstCardinality);
                        lines.add(firstCardinalityLine);
                        Line secondCardinalityLine = new GuardedLine(this.drawingPanel, newRelationship, (Component) castedComponent, new SquaredLine(), new SingleLine(), secondCardinality);
                        lines.add(secondCardinalityLine);

                        cardinalities.add(firstCardinality);
                        cardinalities.add(secondCardinality);

                        newRelationship.addParticipant(castedComponent, firstCardinalityLine);
                        newRelationship.addParticipant(castedComponent, secondCardinalityLine);
                    }

                    for (Cardinality cardinality : cardinalities) {
                        drawingPanel.addComponentLast(cardinality);
                    }

                    for (Line line : lines) {
                        drawingPanel.addComponent(line);
                    }

                    drawingPanel.addComponentLast(newRelationship);

                    drawingPanel.cleanSelectedComponents();

                }
            } else {
                JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.emptyName"));
            }
        } else {
            JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.relationshipCreation"));
        }
    }

    private int getResultado(JPanel miPanel, JTextField cardinalidadMinimaCampo) {
        JOptionPane optionPane = new JOptionPane(miPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

        // Crea el cuadro de diálogo basado en el JOptionPane
        JDialog dialog = optionPane.createDialog(LanguageManager.getMessage("input.twoValues"));

        // Establece el foco en el campo de la cardinalidad mínima al abrir el cuadro de diálogo
        dialog.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                cardinalidadMinimaCampo.requestFocusInWindow();
            }
        });

        // Muestra el cuadro de diálogo
        dialog.setVisible(true);

        return (int) optionPane.getValue();
    }

    public void changeCardinality(Cardinality cardinality) {

        JTextField cardinalidadMinimaCampo = new JTextField(1);
        JTextField cardinalidadMaximaCampo = new JTextField(1);
        JButton okButton = new JButton("OK");
        okButton.setEnabled(false); // OK button is not enabled

        JPanel miPanel = new JPanel();
        miPanel.add(new JLabel(LanguageManager.getMessage("input.twoValues")));
        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
        miPanel.add(new JLabel(LanguageManager.getMessage("cardinality.minimum")));
        miPanel.add(cardinalidadMinimaCampo);
        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
        miPanel.add(new JLabel(LanguageManager.getMessage("cardinality.maximum")));
        miPanel.add(cardinalidadMaximaCampo);

        int resultado = getResultado(miPanel, cardinalidadMinimaCampo);

        if (resultado == JOptionPane.OK_OPTION) {
            String cardinalidadMinima = cardinalidadMinimaCampo.getText();
            String cardinalidadMaxima = cardinalidadMaximaCampo.getText();

            if (cardinalidadMinima.isEmpty() || cardinalidadMaxima.isEmpty()) {
                JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.emptyFields"));
                changeCardinality(cardinality);
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

        if (drawingPanel.onlyTheseClassesAreSelected(Entity.class) && drawingPanel.isNumberOfSelectedComponents(2)) {

            String nombre = JOptionPane.showInputDialog(this.drawingPanel, LanguageManager.getMessage("input.name"));

            if (nombre != null) {

                Relationship newRelationship = new Relationship(nombre, drawingPanel.getMouseX(),drawingPanel.getMouseY(), this.drawingPanel);

                Entity entitySelected = selectWeakEntity();

                if (entitySelected != null) {

                    WeakEntity weakVersion = entitySelected.getWeakVersion(newRelationship);

                    Cardinality cardinality = null, staticCardinality = null;
                    Line strongLine = null, weakLine = null;

                    for (Entity entity : drawingPanel.getSelectedEntities()) {

                        if (entity.equals(entitySelected)) {

                            cardinality = new Cardinality("0", "N", this.drawingPanel);

                            strongLine = new GuardedLine(this.drawingPanel, entity, newRelationship, new DirectLine(), new SingleLine(), cardinality);
                            newRelationship.addParticipant(entity, strongLine);

                        } else {

                            // A weak entity can only be related to a strong entity if the latter has a 1:1 cardinality.
                            staticCardinality = new StaticCardinality("1", "1", this.drawingPanel);

                            weakLine = new GuardedLine(this.drawingPanel, entity, newRelationship, new DirectLine(), new DoubleLine(3), staticCardinality);

                            newRelationship.addParticipant(entity, weakLine);
                        }
                    }

                    drawingPanel.addComponent(weakLine);
                    drawingPanel.addComponent(strongLine);

                    drawingPanel.addComponentLast(cardinality);
                    drawingPanel.addComponentLast(staticCardinality);

                    drawingPanel.addComponentLast(newRelationship);

                    drawingPanel.replaceComponent(entitySelected, weakVersion);

                    drawingPanel.cleanSelectedComponents();
                }

            }
        } else {
            JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.dependencyCreation"));
        }
    }

    private Entity selectWeakEntity() {

        Object[] opciones = {drawingPanel.getSelectedEntities().getFirst().getText(),
                drawingPanel.getSelectedEntities().getLast().getText()};

        // THe JOptionPane with buttons is shown.
        int selection = JOptionPane.showOptionDialog(
                this.drawingPanel,
                LanguageManager.getMessage("input.weakEntity"),
                "",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        return switch (selection) {
            case 0 -> (drawingPanel.getSelectedEntities().getFirst());
            case 1 -> (drawingPanel.getSelectedEntities().getLast());
            default -> {
                JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("input.weakEntity"));
                yield selectWeakEntity();
            }
        };
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                            Add Hierarchy                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void addHierarchy() {

        if (drawingPanel.onlyTheseClassesAreSelected(Entity.class, WeakEntity.class) && drawingPanel.getSelectedComponents().size() >= 3) {

            Entity parent = selectParent();

            main: if (parent != null && !parent.isAlreadyParent()) {

                List<Entity> subtipos = obtenerListaSubtipos(parent);

                Hierarchy newHierarchy = getHierarchy(parent);

                if (newHierarchy == null) {
                    return;
                }

                parent.addHierarchy(newHierarchy);

                for (Entity subtipo : subtipos) {
                    newHierarchy.addChild(subtipo);

                    if (!subtipo.addHierarchy(newHierarchy)) {

                        // Acción reparadora.
                        parent.removeHierarchy(newHierarchy);
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
            JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.threeEntities"));
        }

        // The selection mode is deactivated.
        drawingPanel.cleanSelectedComponents();
    }

    public Hierarchy getHierarchy(Entity parent) {

        // The radio buttons are created.
        JRadioButton exclusiveButton = new JRadioButton(LanguageManager.getMessage("hierarchy.exclusive"), true);
        JRadioButton overlapButton = new JRadioButton(LanguageManager.getMessage("hierarchy.overlap"));
        JRadioButton totalButton = new JRadioButton(LanguageManager.getMessage("hierarchy.total"), true);
        JRadioButton partialButton = new JRadioButton(LanguageManager.getMessage("hierarchy.partial"));

        // The radio buttons are grouped so that only one can be selected at the same time.
        ButtonGroup groupExclusivaCompartida = new ButtonGroup();
        groupExclusivaCompartida.add(exclusiveButton);
        groupExclusivaCompartida.add(overlapButton);

        ButtonGroup groupTotalExclusiva = new ButtonGroup();
        groupTotalExclusiva.add(totalButton);
        groupTotalExclusiva.add(partialButton);

        // A panel to contain the radio buttons is created.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Añade un BoxLayout al panel

        // A panel for each group of radio buttons is created.
        JPanel panelEC = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEC.add(exclusiveButton);
        panelEC.add(overlapButton);

        JPanel panelTP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTP.add(totalButton);
        panelTP.add(partialButton);

        panel.add(panelEC);
        panel.add(panelTP);

        int option = JOptionPane.showOptionDialog(null, panel, "Elige una opción",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        // If the user clicked Cancel or closed the window
        if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.CANCEL_OPTION) {
            return null; // Cancel the process
        }

        HierarchyExclusivity letter = (exclusiveButton.isSelected()) ? HierarchyExclusivity.DISJUNCT : HierarchyExclusivity.OVERLAPPING;

        Hierarchy newHierarchy;

        if (totalButton.isSelected()) {
            newHierarchy = new TotalHierarchy(letter, parent, this.drawingPanel);
        } else {
            newHierarchy = new Hierarchy(letter, parent, this.drawingPanel);
        }

        return newHierarchy;
    }

    public Entity selectParent() {

        List<Entity> entidadesSeleccionadas = drawingPanel.getSelectedEntities();
        Object[] opciones = new Object[entidadesSeleccionadas.size()];

        for (int i = 0; i < entidadesSeleccionadas.size(); i++) {
            opciones[i] = (entidadesSeleccionadas.get(i)).getText();
        }

        // Muestra el JOptionPane con los botones
        int selection = JOptionPane.showOptionDialog(null, LanguageManager.getMessage("hierarchy.selectParent"), "Selección",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        return (entidadesSeleccionadas.get(selection));

    }

    public List<Entity> obtenerListaSubtipos(Entity parent) {

        List<Entity> entidadesSeleccionadas = drawingPanel.getSelectedEntities();

        List<Entity> retorno = new ArrayList<>(entidadesSeleccionadas);
        retorno.remove((parent));

        return retorno;
    }

    public void swapExclusivity(Hierarchy hierarchy) {

        if (hierarchy.getExclusivity().equals(HierarchyExclusivity.DISJUNCT)) {
            hierarchy.setExclusivity(HierarchyExclusivity.OVERLAPPING);
        } else {
            hierarchy.setExclusivity(HierarchyExclusivity.DISJUNCT);
        }

        this.drawingPanel.repaint();

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                 Add Note                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void addNote() {

        String text = JOptionPane.showInputDialog(this.drawingPanel, LanguageManager.getMessage("input.text"));

        if (text != null) {
            drawingPanel.addComponent(new Note(text, drawingPanel.getMouseX(), drawingPanel.getMouseY(), this.drawingPanel));
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                         Delete Selected Components                                             */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void deleteSelectedComponents() {

        List<Component> selectedComponents = this.drawingPanel.getSelectedComponents();

        if (!selectedComponents.isEmpty()) {
            int confirmation = JOptionPane.showConfirmDialog(this.drawingPanel, "¿Seguro de que desea eliminar el objeto seleccionado?");
            if (confirmation == JOptionPane.YES_OPTION) {

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
        drawingPanel.cleanSelectedComponents();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Rename Component                                                 */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void renameComponent(Component component) {

        String newText;

        do {
            newText = JOptionPane.showInputDialog(this.drawingPanel, "Ingrese el nuevo texto: ");

            // "newText" can be null when the user pressed "cancel"
            if (newText != null && newText.isEmpty()) {
                JOptionPane.showMessageDialog(this.drawingPanel, "Ingrese al menos un carácter");
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

    public void addAttribute(AttributableComponent component) {
        addAttribute(component, AttributeSymbol.COMMON);
    }

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

                Attribute newAttribute = new Attribute(component, nombre, attributeSymbol, arrowBody, arrowEnding, this.drawingPanel);

                component.addAttribute(newAttribute);

                drawingPanel.addComponent(newAttribute);
            }
        }

    }

    public void addComplexAttribute(AttributableComponent component) {

        AttributeSymbol attributeSymbol = selectAttributeType();

        if (attributeSymbol == null) {
            return; // The option was canceled.
        }

        if (attributeSymbol.equals(AttributeSymbol.MAIN)) {

            if (component.hasMainAttribute()) {
                JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.mainAttribute"));
                return;
            }

            JTextField fieldNombre = new JTextField(10);

            int option = JOptionPane.showConfirmDialog(null, fieldNombre, LanguageManager.getMessage("input.name"), JOptionPane.OK_CANCEL_OPTION);

            // Muestra una ventana emergente para ingresar el nombre del atributo y seleccionar las opciones
            if (option == JOptionPane.OK_OPTION) {

                String nombre = fieldNombre.getText();

                if (nombre != null) {

                    Attribute newAttribute = new MainAttribute(component, nombre, this.drawingPanel);

                    component.addAttribute(newAttribute);

                    drawingPanel.addComponent(newAttribute);
                }
            }

        } else {

            addAttribute(component, attributeSymbol);

        }
    }

    private AttributeSymbol selectAttributeType() {

        // Crea los radio buttons
        JRadioButton commonAttributeOption = new JRadioButton(LanguageManager.getMessage("attribute.common"), true);
        JRadioButton alternativeAttributeOption = new JRadioButton(LanguageManager.getMessage("attribute.alternative"));
        JRadioButton mainAttributeOption = new JRadioButton(LanguageManager.getMessage("attribute.main"));

        // Agrupa los radio buttons para que solo se pueda seleccionar uno a la vez
        ButtonGroup group = new ButtonGroup();
        group.add(mainAttributeOption);
        group.add(alternativeAttributeOption);
        group.add(commonAttributeOption);

        // Crea un panel para contener los radio buttons
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Añade un BoxLayout al panel

        // Crea un panel para el grupo de radio buttons
        JPanel panelAttribute = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAttribute.add(mainAttributeOption);
        panelAttribute.add(alternativeAttributeOption);
        panelAttribute.add(commonAttributeOption);

        // Agrega los pares de opciones al panel
        panel.add(panelAttribute);

        // Muestra el panel en un JOptionPane
        int result = JOptionPane.showOptionDialog(null, panel, LanguageManager.getMessage("input.attributeType"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        // In case the user closes the dialog...
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

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
    public void addAssociation() {

        if (drawingPanel.getSelectedComponents().size() == 1 && drawingPanel.onlyTheseClassesAreSelected(Relationship.class)) {

            Relationship relationship = (Relationship) drawingPanel.getSelectedComponents().getFirst();

            Association association = new Association(relationship, this.drawingPanel);

            drawingPanel.addComponent(association);
            drawingPanel.repaint();

            drawingPanel.cleanSelectedComponents();

        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una relación.");
        }
    }

    public JPopupMenu getPopupMenu(Component component, Action ... actions) {

        JPopupMenu popupMenu = new JPopupMenu();

        for (Action action : actions) {

            JMenuItem actionItem = new JMenuItem(action.getText());

            switch (action) {
                case DELETE -> actionItem.addActionListener(_ -> deleteSelectedComponents());
                case RENAME, CHANGE_TEXT -> actionItem.addActionListener(_ -> renameComponent(component));
                case ADD_ATTRIBUTE -> actionItem.addActionListener(_ -> addAttribute((AttributableComponent) component));
                case ADD_ASSOCIATION -> actionItem.addActionListener(_ -> addAssociation());
                case ADD_COMPLEX_ATTRIBUTE -> actionItem.addActionListener(_ -> addComplexAttribute((AttributableComponent) component));
                case SWAP_MULTIVALUED -> actionItem.addActionListener(_ -> changeMultivalued((Attribute) component));
                case SWAP_OPTIONALITY -> actionItem.addActionListener(_ -> changeOptionality((Attribute) component));
                case SWAP_EXCLUSIVITY -> actionItem.addActionListener(_ -> swapExclusivity((Hierarchy) component));
                case CHANGE_CARDINALITY -> actionItem.addActionListener(_ -> changeCardinality((Cardinality) component));
                case ADD_REFLEXIVE_RELATIONSHIP -> actionItem.addActionListener(_ -> addRelationship());
            }

            popupMenu.add(actionItem);
        }

        return popupMenu;

    }

}
