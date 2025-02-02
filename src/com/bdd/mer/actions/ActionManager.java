package com.bdd.mer.actions;

import com.bdd.mer.components.attribute.*;
import com.bdd.mer.components.attribute.symbology.AttributeArrow;
import com.bdd.mer.components.attribute.symbology.AttributeEnding;
import com.bdd.mer.components.attribute.symbology.AttributeSymbol;
import com.bdd.mer.components.association.Association;
import com.bdd.mer.components.hierarchy.HierarchySymbol;
import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.components.entity.WeakEntity;
import com.bdd.mer.components.hierarchy.Hierarchy;
import com.bdd.mer.components.line.GuardedLine;
import com.bdd.mer.components.line.Line;
import com.bdd.mer.components.line.lineMultiplicity.DoubleLine;
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
import java.util.*;
import java.util.List;

public final class ActionManager implements Serializable {

    /**
     * <Code>DrawingPanel</Code> bounded to the <Code>ActionManager</Code>.
     */
    private DrawingPanel drawingPanel;

    /**
     * Constructs an <Code>ActionManager</Code>.
     *
     * @param drawingPanel <Code>DrawingPanel</Code> bounded to the <Code>ActionManager</Code>.
     */
    public ActionManager(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    /**
     * Construct an {@code ActionManager}.
     * <p>
     * To things work correctly, it's necessary to set a {@code DrawingPanel}.
     * @see #setDrawingPanel(DrawingPanel)
     */
    public ActionManager() {
        this(null);
    }

    /* ---------------------------------------------------------------------------------------------------------- */
    /*                                              Add Entity                                                    */
    /* ---------------------------------------------------------------------------------------------------------- */

    /**
     * Adds a new <Code>Entity</Code> to the <Code>DrawingPanel</Code>.
     */
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

    /**
     * Adds a new <Code>Relationship</Code> to the <Code>DrawingPanel</Code>.
     * <p></p>
     * Between one and three entities (strong or weak) or associations must be selected.
     */
    public void addRelationship() {

        int selectedComponents = this.drawingPanel.getSelectedComponents().size();

        if (drawingPanel.onlyTheseClassesAreSelected(Entity.class, WeakEntity.class, Association.class)
                && drawingPanel.isNumberOfSelectedComponentsBetween(1, 3)) {

            String name = JOptionPane.showInputDialog(
                    this.drawingPanel,
                    null,
                    LanguageManager.getMessage("input.name"),
                    JOptionPane.QUESTION_MESSAGE
            );

            if (name != null) {
                if (!name.isEmpty()) {

                    Point center = this.drawingPanel.getCenterOfSelectedComponents();

                    Relationship newRelationship = new Relationship(name, center.x, center.y, this.drawingPanel);

                    List<Cardinality> cardinalities = new ArrayList<>();

                    List<Line> lines = new ArrayList<>();

                    if (selectedComponents >= 2 && selectedComponents <= 3) {

                        for (Component component : drawingPanel.getSelectedComponents()) {

                            // It's safe, due to I asked at the stat if only objects from the Entity and Association classes are selected.
                            Relatable castedComponent = (Relatable) component;

                            Cardinality cardinality = new Cardinality("1", "N", this.drawingPanel);

                            cardinalities.add(cardinality);

                            GuardedLine guardedLine = new GuardedLine.Builder(
                                    this.drawingPanel,
                                    (Component) castedComponent,
                                    newRelationship,
                                    cardinality).build();

                            // This must be improved later.
                            // If an association is related, the line cannot wait until then the association is drawn.
                            // It must be drawn first.
                            if (component instanceof Association) {
                                guardedLine.setDrawingPriority(0);
                            }

                            lines.add(guardedLine);

                            newRelationship.addParticipant(castedComponent, guardedLine);

                        }

                    } else if (selectedComponents == 1) {

                        Relatable castedComponent = (Relatable) this.drawingPanel.getSelectedComponents().getFirst();

                        Cardinality firstCardinality = new Cardinality("1", "N", this.drawingPanel);
                        Cardinality secondCardinality = new Cardinality("1", "N", this.drawingPanel);

                        Line firstCardinalityLine = new GuardedLine.Builder(
                                this.drawingPanel,
                                (Component) castedComponent,
                                newRelationship,
                                firstCardinality).lineShape(new SquaredLine()).build();
                        lines.add(firstCardinalityLine);

                        Line secondCardinalityLine = new GuardedLine.Builder(
                                this.drawingPanel,
                                newRelationship,
                                (Component) castedComponent,
                                secondCardinality).lineShape(new SquaredLine()).build();
                        lines.add(secondCardinalityLine);

                        cardinalities.add(firstCardinality);
                        cardinalities.add(secondCardinality);

                        newRelationship.addParticipant(castedComponent, firstCardinalityLine);
                        newRelationship.addParticipant(castedComponent, secondCardinalityLine);
                    }

                    for (Cardinality cardinality : cardinalities) {
                        drawingPanel.addComponent(cardinality);
                    }

                    for (Line line : lines) {
                        drawingPanel.addComponent(line);
                    }

                    drawingPanel.addComponent(newRelationship);

                    drawingPanel.cleanSelectedComponents();

                }
            } else {
                JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.emptyName"));
            }
        } else {
            JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.relationshipCreation"));
        }
    }

    /**
     * Given a cardinality, changes its values.
     *
     * @param cardinality <Code>Cardinality</Code> whose values will be changed.
     */
    public void changeCardinality(Cardinality cardinality) {

        JTextField cardinalidadMinimaCampo = new JTextField(3);
        JTextField cardinalidadMaximaCampo = new JTextField(3);

        JPanel miPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        miPanel.add(new JLabel(LanguageManager.getMessage("cardinality.minimum")));
        miPanel.add(cardinalidadMinimaCampo);
        miPanel.add(new JLabel(LanguageManager.getMessage("cardinality.maximum")));
        miPanel.add(cardinalidadMaximaCampo);

        int resultado = JOptionPane.showConfirmDialog(null, miPanel, LanguageManager.getMessage("input.twoValues"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            String minText = cardinalidadMinimaCampo.getText().trim();
            String maxText = cardinalidadMaximaCampo.getText().trim();

            Optional<Integer> minValue = parseInteger(minText);

            // Validates if the fields are not empty.
            if (minText.isEmpty() || maxText.isEmpty()) {
                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.emptyFields"), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validates if the minimum cardinality is a valid number.
            if (minValue.isEmpty() || minValue.get() < 0) {
                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.invalidMinimum"), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validates if the maximum cardinality is a valid number or a letter.
            if (!isIntegerOrLetter(maxText) || (isInteger(maxText) && Integer.parseInt(maxText) < 1)) {
                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.invalidMaximum"), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If the maximum cardinality is a number, it must be greater than the minimum cardinality.
            if (isInteger(maxText)) {
                int maxValue = Integer.parseInt(maxText);
                if (minValue.get() > maxValue) {
                    JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.invalidRange"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // If everything is valid, the cardinality is updated.
            cardinality.setText(Cardinality.giveFormat(minText, maxText));
            this.drawingPanel.repaint();
        }
    }

    /**
     * It parses a text to <Code>Integer</Code> if it's possible.
     *
     * @param text Text to be parsed.
     * @return {@code Optional<Integer>} containing the parsed text if it was possible.
     */
    private Optional<Integer> parseInteger(String text) {
        try {
            return Optional.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Validates if a text is an integer xor a letter.
     *
     * @param text Text to be checked.
     * @return <Code>TRUE</Code> if the text is an integer xor a letter. It returns <Code>FALSE</Code> in any other
     * case.
     */
    private boolean isIntegerOrLetter(String text) {
        return text.matches("\\d+") || text.matches("[a-zA-Z]");
    }

    /**
     * Validates if a text is strictly a number.
     *
     * @param text Text to be checked.
     * @return <Code>TRUE</Code> if the text is an integer. It returns <Code>FALSE</Code> in any other case.
     */
    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                           Add Dependency                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Adds a new <Code>Dependency</Code> to the <Code>DrawingPanel</Code>.
     * <p></p>
     * Two strong entities must be selected.
     */
    public void addDependency() {

        if (drawingPanel.onlyTheseClassesAreSelected(Entity.class) && drawingPanel.isNumberOfSelectedComponents(2)) {

            String name = JOptionPane.showInputDialog(
                    this.drawingPanel,
                    null,
                    LanguageManager.getMessage("input.name"),
                    JOptionPane.QUESTION_MESSAGE
            );

            if (name != null) {

                Point center = this.drawingPanel.getCenterOfSelectedComponents();

                Relationship newRelationship = new Relationship(name, center.x, center.y, this.drawingPanel);

                Entity entitySelected = selectWeakEntity();

                if (entitySelected != null) {

                    WeakEntity weakVersion = entitySelected.getWeakVersion(newRelationship);

                    Cardinality cardinality = null, staticCardinality = null;
                    Line strongLine = null, weakLine = null;

                    for (Entity entity : drawingPanel.getSelectedEntities()) {

                        if (entity.equals(entitySelected)) {

                            cardinality = new Cardinality("1", "N", this.drawingPanel);

                            strongLine = new GuardedLine.Builder(
                                    this.drawingPanel,
                                    entity,
                                    newRelationship,
                                    cardinality
                            ).lineMultiplicity(new DoubleLine(3)).build();

                            newRelationship.addParticipant(entity, strongLine);

                        } else {

                            // A weak entity can only be related to a strong entity if the latter has a 1:1 cardinality.
                            staticCardinality = new StaticCardinality("1", "1", this.drawingPanel);

                            weakLine = new GuardedLine.Builder(
                                    this.drawingPanel,
                                    entity,
                                    newRelationship,
                                    staticCardinality
                            ).build();

                            newRelationship.addParticipant(entity, weakLine);
                        }
                    }

                    // These checks are only added so the IDE don't tell me they can be null.

                    if (weakLine != null) {
                        drawingPanel.addComponent(weakLine);
                    }

                    if (strongLine != null) {
                        drawingPanel.addComponent(strongLine);
                    }

                    if (cardinality != null) {
                        drawingPanel.addComponent(cardinality);
                    }

                    if (staticCardinality != null) {
                        drawingPanel.addComponent(staticCardinality);
                    }

                    drawingPanel.addComponent(newRelationship);

                    drawingPanel.replaceComponent(entitySelected, weakVersion);

                    drawingPanel.cleanSelectedComponents();
                }

            }
        } else {
            JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.dependencyCreation"));
        }
    }

    /**
     * From the list of selected entities, allows the user to select the weak entity.
     *
     * @return {@code Entity} to be the weak entity of the dependency.
     */
    private Entity selectWeakEntity() {

        Object[] opciones = {drawingPanel.getSelectedEntities().getFirst().getText(),
                drawingPanel.getSelectedEntities().getLast().getText()};

        // THe JOptionPane with buttons is shown.
        int selection = JOptionPane.showOptionDialog(
                this.drawingPanel,
                LanguageManager.getMessage("input.weakEntity"),
                LanguageManager.getMessage("input.option"),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
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

    /**
     * Adds a new <Code>Hierarchy</Code> to the <Code>DrawingPanel</Code>.
     * <p></p>
     * At least three strong or weak entities must be selected.
     */
    public void addHierarchy() {

        if (drawingPanel.onlyTheseClassesAreSelected(Entity.class, WeakEntity.class) && drawingPanel.getSelectedComponents().size() >= 3) {

            Entity parent = selectParent();

            main: if (parent != null && !parent.isAlreadyParent()) {

                List<Entity> subtipos = getChildrenList(parent);

                Hierarchy newHierarchy = getHierarchy(parent);

                if (newHierarchy == null) {
                    return;
                }

                parent.addHierarchy(newHierarchy);

                for (Entity subtipo : subtipos) {
                    newHierarchy.addChild(subtipo);

                    if (!subtipo.addHierarchy(newHierarchy)) {

                        // Repairing action.
                        parent.removeHierarchy(newHierarchy);
                        for (Entity s : subtipos) {
                            s.removeHierarchy(newHierarchy);

                            String message = LanguageManager.getMessage("warning.theEntity") + " "
                                    + '\"' + subtipo.getText() + '\"'
                                    + " " + LanguageManager.getMessage("warning.alreadyParticipatesInHierarchy") + " "
                                    + LanguageManager.getMessage("warning.multipleInheritanceOnlyAllowed");

                            JOptionPane.showMessageDialog(this.drawingPanel, message);

                            // Exit.
                            break main;
                        }
                    }
                }

                drawingPanel.addComponent(newHierarchy);

                } else {
                    JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.alreadyParent"));
                }
        } else {
            JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.threeEntities"));
        }

        // The selection mode is deactivated.
        drawingPanel.cleanSelectedComponents();
    }

    /**
     * Creates a {@code Hierarchy} according to the options selected by the user.
     *
     * @param parent Entity parent of the hierarchy.
     * @return {@code Hierarchy} according to the options selected by the user.
     */
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

        int option = JOptionPane.showOptionDialog(null, panel, LanguageManager.getMessage("input.option"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        // If the user clicked "Cancel" or closed the window.
        if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.CANCEL_OPTION) {
            return null; // The process is canceled.
        }

        HierarchySymbol symbol = (exclusiveButton.isSelected()) ? HierarchySymbol.DISJUNCT : HierarchySymbol.OVERLAPPING;

        Hierarchy newHierarchy = new Hierarchy(symbol, parent, this.drawingPanel);

        Line parentLine;

        if (totalButton.isSelected()) {
            parentLine = new Line.Builder(this.drawingPanel, parent, newHierarchy)
                    .lineMultiplicity(new DoubleLine(3)).build();
        } else {
            parentLine = new Line.Builder(this.drawingPanel, parent, newHierarchy)
                    .stroke(new BasicStroke(2)).build();
            // This way, setting the stroke, it's noticeable who is the parent of the hierarchy.
        }

        newHierarchy.setParentLine(parentLine);
        this.drawingPanel.addComponent(parentLine);

        return newHierarchy;
    }

    /**
     * Allows the user to select, from the selected entities, an {@code Entity} to be the parent.
     *
     * @return {@code Hierarchy} selected to be the parent of the {@code Hierarchy}.
     */
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

    /**
     * Given the parent of the hierarchy, it returns a list containing its children.
     *
     * @param parent {@code Entity} chosen as the parent of the hierarchy.
     * @return {@code List<Entity>} containing the children entities of the hierarchy.
     */
    public List<Entity> getChildrenList(Entity parent) {

        List<Entity> entidadesSeleccionadas = drawingPanel.getSelectedEntities();

        List<Entity> retorno = new ArrayList<>(entidadesSeleccionadas);
        retorno.remove((parent));

        return retorno;
    }

    /**
     * Given a hierarchy, swaps its exclusivity.
     *
     * @param hierarchy {@code Hierarchy} whose exclusivity will be swapped.
     */
    public void swapExclusivity(Hierarchy hierarchy) {

        if (hierarchy.getSymbol().equals(HierarchySymbol.DISJUNCT)) {
            hierarchy.setSymbol(HierarchySymbol.OVERLAPPING);
        } else {
            hierarchy.setSymbol(HierarchySymbol.DISJUNCT);
        }

        this.drawingPanel.repaint();

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                 Add Note                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Adds a new <Code>Note</Code> to the <Code>DrawingPanel</Code>.
     * <p></p>
     * At least three strong or weak entities must be selected.
     */
    public void addNote() {

        String text = JOptionPane.showInputDialog(
                this.drawingPanel,
                null,
                LanguageManager.getMessage("input.text"),
                JOptionPane.QUESTION_MESSAGE
        );

        if (text != null) {
            drawingPanel.addComponent(new Note(text, drawingPanel.getMouseX(), drawingPanel.getMouseY(), this.drawingPanel));
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                         Delete Selected Components                                             */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Deletes all the selected components and their close-related components.
     * <p></p>
     * At least a component must be selected.
     */
    public void deleteSelectedComponents() {

        List<Component> selectedComponents = this.drawingPanel.getSelectedComponents();

        if (!selectedComponents.isEmpty()) {

            int confirmation = JOptionPane.showConfirmDialog(
                    this.drawingPanel,
                    LanguageManager.getMessage("input.delete"),
                    LanguageManager.getMessage("title.delete"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirmation == JOptionPane.YES_OPTION) {

                Set<Component> componentsForRemoval = new HashSet<>();

                for (Component component : selectedComponents) {

                    if (!component.canBeDeleted()) {
                        return;
                    }

                    componentsForRemoval.addAll(component.getComponentsForRemoval());
                    componentsForRemoval.add(component);
                }

                for (Component component : componentsForRemoval) {
                    this.drawingPanel.removeComponent(component);
                }

            }

            this.drawingPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.delete"));
        }

        // Desactiva el modo de selección
        drawingPanel.cleanSelectedComponents();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Rename Component                                                 */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Renames a {@code Component}.
     */
    public void renameComponent(Component component) {

        String newText;

        do {

            newText= JOptionPane.showInputDialog(
                    this.drawingPanel,
                    null,
                    LanguageManager.getMessage("input.newText"),
                    JOptionPane.QUESTION_MESSAGE
            );

            // "newText" can be null when the user pressed "cancel"
            if (newText != null && newText.isEmpty()) {
                JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.oneCharacter"));
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

    /**
     * Given an attributable component, this method adds it a common attribute.
     *
     * @param component The attributable component owner of the attribute.
     */
    public void addAttribute(AttributableComponent component) {
        addAttribute(component, AttributeSymbol.COMMON);
    }

    /**
     * Given an attributable component, this method adds it an attribute according to the specified symbol.
     *
     * @param component The attributable component owner of the attribute.
     * @param attributeSymbol The type of the attribute.
     */
    public void addAttribute(AttributableComponent component, AttributeSymbol attributeSymbol) {

        // Create the components of the panel
        JTextField fieldNombre = new JTextField(10);
        JCheckBox boxOptional = new JCheckBox(LanguageManager.getMessage("attribute.optional"));
        JCheckBox boxMultivalued = new JCheckBox(LanguageManager.getMessage("attribute.multivalued"));

        // Array of the components of the panel
        Object[] message = {
                LanguageManager.getMessage("input.name"), fieldNombre,
                LanguageManager.getMessage("input.selectOptions"), boxOptional, boxMultivalued
        };

        // Show the confirmation dialog
        JOptionPane pane = new JOptionPane(
                message,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );

        // Create the dialog directly
        JDialog dialog = pane.createDialog(this.drawingPanel, LanguageManager.getMessage("input.attributeInformation"));

        setFocus(fieldNombre);

        dialog.setVisible(true);

        // Get the selected value after the dialog is closed
        Object selectedValue = pane.getValue();
        if (selectedValue != null && (int) selectedValue == JOptionPane.OK_OPTION) {
            String name = fieldNombre.getText();

            // Check if the input is empty
            while (name != null && name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.emptyName"));
                name = this.getName();
            }

            if (name == null) {
                return;
            }

            AttributeArrow arrowBody = (boxOptional.isSelected()) ? AttributeArrow.OPTIONAL : AttributeArrow.NON_OPTIONAL;
            AttributeEnding arrowEnding = (boxMultivalued.isSelected()) ? AttributeEnding.MULTIVALUED : AttributeEnding.NON_MULTIVALUED;

            Attribute newAttribute = new Attribute(component, name, attributeSymbol, arrowBody, arrowEnding, this.drawingPanel);

            this.addAttribute(newAttribute);
        }
    }

    /**
     * Given an attributable component, this method allows selecting a type of attribute so it can be added later.
     *
     * @param component The attributable component owner of the attribute.
     */
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

            String name = this.getName();

            if (name == null) {
                return;
            }

            Attribute newAttribute = new MainAttribute(component, name, this.drawingPanel);

            this.addAttribute(newAttribute);

        } else {

            addAttribute(component, attributeSymbol);
        }
    }

    /**
     * It makes sure to return a non-empty name.
     *
     * @return {@code String} entered by the user.
     */
    private String getName() {

        String name = JOptionPane.showInputDialog(
                this.drawingPanel,
                null,
                LanguageManager.getMessage("input.name"),
                JOptionPane.QUESTION_MESSAGE
        );

        while (name != null && name.trim().isEmpty()) {

            JOptionPane.showMessageDialog(this.drawingPanel, LanguageManager.getMessage("warning.emptyName"));

            name = JOptionPane.showInputDialog(
                    this.drawingPanel,
                    null,
                    LanguageManager.getMessage("input.name"),
                    JOptionPane.QUESTION_MESSAGE
            );
        }

        return name;
    }

    /**
     * Sets focus on the JComponent.
     *
     * @param component {@code JComponent} to be focused.
     */
    private void setFocus(JComponent component) {

        component.addAncestorListener(new javax.swing.event.AncestorListener() {
            @Override
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                component.requestFocusInWindow();
            }

            @Override
            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {}

            @Override
            public void ancestorMoved(javax.swing.event.AncestorEvent event) {}
        });

    }

    /**
     * Adds an attribute in a way that it's correctly drawn.
     * <p>
     * This is important, so the things depending on the attribute (such as associations and other
     * attributes) are drawn correctly in the start and not in the second drawing where the shape is set.
     *
     * @param attribute Attribute to be added.
     */
    private void addAttribute(Attribute attribute) {

        attribute.setDrawingPriority(0);

        drawingPanel.addComponent(attribute);

        // This is necessary due to the repaint will not be done until this method ends, because it's asynchronous.
        // Maybe it would be good to search other possible solutions because this is not so efficient...
        this.drawingPanel.paintImmediately(drawingPanel.getBounds());

        attribute.setDrawingPriority(4);

        drawingPanel.sortComponents();
    }

    /**
     * This method allows the user to select an attribute type.
     *
     * @return The attribute symbol selected.
     */
    @SuppressWarnings("Duplicates")
    private AttributeSymbol selectAttributeType() {

        // The radio buttons are created.
        JRadioButton commonAttributeOption = new JRadioButton(LanguageManager.getMessage("attribute.common"), true);
        JRadioButton alternativeAttributeOption = new JRadioButton(LanguageManager.getMessage("attribute.alternative"));
        JRadioButton mainAttributeOption = new JRadioButton(LanguageManager.getMessage("attribute.main"));

        // The radio buttons are grouped so only one can be selected at the same time.
        ButtonGroup group = new ButtonGroup();
        group.add(mainAttributeOption);
        group.add(alternativeAttributeOption);
        group.add(commonAttributeOption);

        // A panel for containing the radio buttons is created.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // A panel for the group of radio buttons is created.
        JPanel panelAttribute = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAttribute.add(mainAttributeOption);
        panelAttribute.add(alternativeAttributeOption);
        panelAttribute.add(commonAttributeOption);

        // The pair of options are added to the panel.
        panel.add(panelAttribute);

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

    /**
     * This method allows the user to change an attribute optionality.
     *
     * @param attribute The attribute whose optionality will be changed.
     */
    public void changeOptionality(Attribute attribute) {

        attribute.changeOptionality();
        drawingPanel.repaint();

    }

    /**
     * This method allows the user to change the number of values an attribute can take.
     *
     * @param attribute The attribute whose number of possible values will be changed.
     */
    public void changeMultivalued(Attribute attribute) {

        attribute.changeMultivalued();
        drawingPanel.repaint();

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                              Add Association                                                   */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Add an association to the drawing panel.
     * <p></p>
     * There must be selected a relationship.
     */
    // There must be selected at least an entity and a relationship (unary relationship)
    public void addAssociation() {

        if (drawingPanel.getSelectedComponents().size() == 1 && drawingPanel.onlyTheseClassesAreSelected(Relationship.class)) {

            Relationship relationship = (Relationship) drawingPanel.getSelectedComponents().getFirst();

            Association association = new Association(relationship, this.drawingPanel);

            drawingPanel.addComponent(association);
            drawingPanel.repaint();

            drawingPanel.cleanSelectedComponents();

        } else {
            JOptionPane.showMessageDialog(null, LanguageManager.getMessage("input.selectRelationship"));
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                Get JPopupMenu                                                  */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Given a list of actions, it returns a JPopupMenu that allows the component to perform all
     * the specified actions.
     *
     * @param component The component owner of the actions.
     * @param actions An infinite list of actions we want to add to the panel.
     * @return A <code>JPopupMenu</code> containing all the specified options.
     */

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

    public JPopupMenu getBackgroundPopupMenu() {

        JPopupMenu backgroundPopupMenu = new JPopupMenu();

        JMenuItem addEntity = new JMenuItem(LanguageManager.getMessage("option.addEntity"));
        addEntity.addActionListener(_ -> this.addEntity());

        JMenuItem addRelationship = new JMenuItem(LanguageManager.getMessage("option.addRelationship"));
        addRelationship.addActionListener(_ -> this.addRelationship());

        JMenuItem addDependency = new JMenuItem(LanguageManager.getMessage("option.addDependency"));
        addDependency.addActionListener(_ -> this.addDependency());

        JMenuItem addHierarchy = new JMenuItem(LanguageManager.getMessage("option.addHierarchy"));
        addHierarchy.addActionListener(_ -> this.addHierarchy());

        JMenuItem addNote = new JMenuItem(LanguageManager.getMessage("option.addNote"));
        addNote.addActionListener(_ -> this.addNote());

        JMenuItem addAssociation = new JMenuItem(LanguageManager.getMessage("option.addAssociation"));
        addAssociation.addActionListener(_ -> this.addAssociation());

        backgroundPopupMenu.add(addEntity);
        backgroundPopupMenu.add(addRelationship);
        backgroundPopupMenu.add(addDependency);
        backgroundPopupMenu.add(addHierarchy);
        backgroundPopupMenu.add(addNote);
        backgroundPopupMenu.add(addAssociation);

        return backgroundPopupMenu;
    }

    public void setDrawingPanel(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

}
