package com.bdd.mer.frame.drawingPanels;

import com.bdd.mer.actions.ActionManager;
import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.association.Association;
import com.bdd.mer.components.attribute.Attribute;
import com.bdd.mer.components.attribute.MainAttribute;
import com.bdd.mer.components.attribute.symbology.AttributeArrow;
import com.bdd.mer.components.attribute.symbology.AttributeEnding;
import com.bdd.mer.components.attribute.symbology.AttributeSymbol;
import com.bdd.mer.components.entity.EntityWrapper;
import com.bdd.mer.components.hierarchy.Hierarchy;
import com.bdd.mer.components.hierarchy.HierarchySymbol;
import com.bdd.mer.components.line.GuardedLine;
import com.bdd.mer.components.line.Line;
import com.bdd.mer.components.line.guard.Discriminant;
import com.bdd.mer.components.line.guard.cardinality.Cardinality;
import com.bdd.mer.components.line.guard.cardinality.StaticCardinality;
import com.bdd.mer.components.line.lineMultiplicity.DoubleLine;
import com.bdd.mer.components.line.lineShape.SquaredLine;
import com.bdd.mer.components.note.Note;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.components.relationship.relatable.Relatable;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.userPreferences.LanguageManager;
import com.bdd.mer.structures.Pair;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.util.*;
import java.util.List;

public class EERPanel extends DrawingPanel {

    /**
     * Constructs a {@code DrawingPanel}.
     *
     * @param actionManager {@code ActionManager} from what the {@code this} will take its actions.
     */
    public EERPanel(ActionManager actionManager) {
        super(actionManager);
    }

    /* ---------------------------------------------------------------------------------------------------------- */
    /*                                              Add Entity                                                    */
    /* ---------------------------------------------------------------------------------------------------------- */

    /**
     * Adds a new <Code>Entity</Code> to the <Code>this</Code>.
     */
    public void addEntity() {

        // It shows an emergent window to ask the user for the entity's name.
        String entityName = JOptionPane.showInputDialog(
                this,
                null,
                LanguageManager.getMessage("actionManager.addEntity.dialog"), // Title.
                JOptionPane.QUESTION_MESSAGE // Message Source.
        );

        if (entityName != null) { // If it's null, the action was canceled.

            if (!entityName.isEmpty()) {

                if (!this.existsComponent(entityName)) {

                    EntityWrapper entityWrapper = new EntityWrapper(
                            entityName,
                            this.getMouseX(),
                            this.getMouseY(),
                            this
                    );

                    this.addComponent(entityWrapper);
                } else {

                    JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.nameDuplicated"));
                    addEntity();
                }
            } else {

                JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.emptyName"));
            }
        }
    }

    /* ---------------------------------------------------------------------------------------------------------- */
    /*                                           Add Relationship                                                 */
    /* ---------------------------------------------------------------------------------------------------------- */

    /**
     * Adds a new <Code>Relationship</Code> to the <Code>this</Code>.
     * <p></p>
     * Between one and three entities (strong or weak) or associations must be selected.
     */
    public void addRelationship() {

        int selectedComponents = this.getSelectedComponents().size();

        if (this.onlyTheseClassesAreSelected(EntityWrapper.class, Association.class)
                && this.isNumberOfSelectedComponentsBetween(1, 3)) {

            String name = JOptionPane.showInputDialog(
                    this,
                    null,
                    LanguageManager.getMessage("input.name"),
                    JOptionPane.QUESTION_MESSAGE
            );

            if (name != null) {
                if (!name.isEmpty()) {

                    Relationship newRelationship = null;

                    List<Line> lines = new ArrayList<>();

                    List<Cardinality> cardinalities = new ArrayList<>();

                    if (selectedComponents >= 2 && selectedComponents <= 3) {

                        Point center = this.getCenterOfSelectedComponents();

                        newRelationship = new Relationship(name, center.x, center.y, this);

                        for (com.bdd.mer.components.Component component : this.getSelectedComponents()) {

                            // It's safe, due to I asked at the stat if only objects from the Entity and Association classes are selected.
                            Relatable castedComponent = (Relatable) component;

                            Cardinality cardinality;

                            if (selectedComponents == 2) {
                                cardinality = new Cardinality("1", "N", this);
                            } else {
                                cardinality = new Cardinality("0", "N", this);
                            }

                            cardinalities.add(cardinality);

                            GuardedLine guardedLine = new GuardedLine.Builder(
                                    this,
                                    (com.bdd.mer.components.Component) castedComponent,
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

                        newRelationship = new Relationship(
                                name,
                                this.getMouseX() + 90,
                                this.getMouseY() - 90,
                                this
                        );

                        Relatable castedComponent = (Relatable) this.getSelectedComponents().getFirst();

                        Cardinality firstCardinality = new Cardinality("1", "N", this);
                        Cardinality secondCardinality = new Cardinality("1", "N", this);

                        GuardedLine firstCardinalityLine = new GuardedLine.Builder(
                                this,
                                (com.bdd.mer.components.Component) castedComponent,
                                newRelationship,
                                firstCardinality).lineShape(new SquaredLine()).build();
                        lines.add(firstCardinalityLine);

                        GuardedLine secondCardinalityLine = new GuardedLine.Builder(
                                this,
                                newRelationship,
                                (com.bdd.mer.components.Component) castedComponent,
                                secondCardinality).lineShape(new SquaredLine()).build();
                        lines.add(secondCardinalityLine);

                        cardinalities.add(firstCardinality);
                        cardinalities.add(secondCardinality);

                        newRelationship.addParticipant(castedComponent, firstCardinalityLine);
                        newRelationship.addParticipant(castedComponent, secondCardinalityLine);
                    }

                    // It can never be null, due to it is asked the exact amount of components previously.
                    // But the IDE doesn't know it.
                    assert newRelationship != null;

                    for (Cardinality cardinality : cardinalities) {
                        this.addComponent(cardinality);
                    }

                    for (Line line : lines) {
                        this.addComponent(line);
                    }

                    this.addComponent(newRelationship);

                    this.cleanSelectedComponents();

                }
            } else {
                JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.emptyName"));
            }
        } else {
            JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.relationshipCreation"));
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

        setFocus(cardinalidadMinimaCampo);

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

            // Here only the area of the cardinality could be repainted, but, if the cardinality now has a considerable
            // greater number, it'll lead to visual noise until all the panel is repainted.
            this.repaint();
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
     * Adds a new <Code>Dependency</Code> to the <Code>this</Code>.
     * <p></p>
     * Two strong entities must be selected.
     */
    public void addDependency() {

        if (this.onlyTheseClassesAreSelected(EntityWrapper.class) && this.isNumberOfSelectedComponents(2)) {

            String name = JOptionPane.showInputDialog(
                    this,
                    null,
                    LanguageManager.getMessage("input.name"),
                    JOptionPane.QUESTION_MESSAGE
            );

            if (name != null) {

                Point center = this.getCenterOfSelectedComponents();

                Relationship newRelationship = new Relationship(name, center.x, center.y, this);

                EntityWrapper entitySelected = selectWeakEntity();

                if (entitySelected != null) {

                    entitySelected.setWeakVersion(newRelationship);

                    Cardinality cardinality = null, staticCardinality = null;
                    GuardedLine strongLine = null, weakLine = null;

                    for (EntityWrapper entity : this.getSelectedComponentsByClass(EntityWrapper.class)) {

                        if (entity.equals(entitySelected)) {

                            cardinality = new Cardinality("1", "N", this);

                            strongLine = new GuardedLine.Builder(
                                    this,
                                    entity,
                                    newRelationship,
                                    cardinality
                            ).lineMultiplicity(new DoubleLine(3)).build();

                            newRelationship.addParticipant(entity, strongLine);

                        } else {

                            // A weak entity can only be related to a strong entity if the latter has a 1:1 cardinality.
                            staticCardinality = new StaticCardinality("1", "1", this);

                            weakLine = new GuardedLine.Builder(
                                    this,
                                    entity,
                                    newRelationship,
                                    staticCardinality
                            ).build();

                            newRelationship.addParticipant(entity, weakLine);
                        }
                    }

                    // These checks are only added so the IDE don't tell me they can be null.

                    if (weakLine != null) {
                        this.addComponent(weakLine);
                    }

                    if (strongLine != null) {
                        this.addComponent(strongLine);
                    }

                    if (cardinality != null) {
                        this.addComponent(cardinality);
                    }

                    if (staticCardinality != null) {
                        this.addComponent(staticCardinality);
                    }

                    this.addComponent(newRelationship);

                    this.cleanSelectedComponents();
                }

            }
        } else {
            JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.dependencyCreation"));
        }
    }

    /**
     * From the list of selected entities, allows the user to select the weak entity.
     *
     * @return {@code Entity} to be the weak entity of the dependency.
     */
    private EntityWrapper selectWeakEntity() {

        Object[] opciones = {this.getSelectedComponentsByClass(EntityWrapper.class).getFirst().getText(),
                this.getSelectedComponentsByClass(EntityWrapper.class).getLast().getText()};

        // THe JOptionPane with buttons is shown.
        int selection = JOptionPane.showOptionDialog(
                this,
                LanguageManager.getMessage("input.weakEntity"),
                LanguageManager.getMessage("input.option"),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        return switch (selection) {
            case 0 -> (this.getSelectedComponentsByClass(EntityWrapper.class).getFirst());
            case 1 -> (this.getSelectedComponentsByClass(EntityWrapper.class).getLast());
            default -> {
                JOptionPane.showMessageDialog(this, LanguageManager.getMessage("input.weakEntity"));
                yield selectWeakEntity();
            }
        };
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                            Add Hierarchy                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Adds a new <Code>Hierarchy</Code> to the <Code>this</Code>.
     * <p></p>
     * At least three strong or weak entities must be selected.
     */
    public void addHierarchy() {

        if (this.onlyTheseClassesAreSelected(EntityWrapper.class) && this.getSelectedComponents().size() >= 3) {

            EntityWrapper parent = selectParent();

            main: if (parent != null && !parent.isAlreadyParent()) {

                List<EntityWrapper> subtipos = getChildrenList(parent);

                Pair<Hierarchy, List<com.bdd.mer.components.Component>> newHierarchyData = getHierarchy(parent);

                if (newHierarchyData == null) {
                    return;
                }

                Hierarchy newHierarchy = newHierarchyData.first();
                List<com.bdd.mer.components.Component> componentsToAdd = newHierarchyData.second();

                for (EntityWrapper subtipo : subtipos) {
                    newHierarchy.addChild(subtipo);

                    if (!subtipo.addHierarchy(newHierarchy)) {

                        // Repairing action.
                        parent.removeHierarchy(newHierarchy);
                        for (EntityWrapper s : subtipos) {
                            s.removeHierarchy(newHierarchy);

                            String message = LanguageManager.getMessage("warning.theEntity") + " "
                                    + '\"' + subtipo.getText() + '\"'
                                    + " " + LanguageManager.getMessage("warning.alreadyParticipatesInHierarchy") + " "
                                    + LanguageManager.getMessage("warning.multipleInheritanceOnlyAllowed");

                            JOptionPane.showMessageDialog(this, message);

                            // Exit.
                            break main;
                        }
                    }
                }

                parent.addHierarchy(newHierarchy);

                for (com.bdd.mer.components.Component component : componentsToAdd) {
                    this.addComponent(component);
                }

                this.addComponent(newHierarchy);

            } else {
                JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.alreadyParent"));
            }
        } else {
            JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.threeEntities"));
        }

        // The selection mode is deactivated.
        this.cleanSelectedComponents();
    }

    /**
     * Creates a {@code Hierarchy} according to the options selected by the user.
     *
     * @param parent Entity parent of the hierarchy.
     * @return {@code Hierarchy} according to the options selected by the user.
     */
    public Pair<Hierarchy, List<com.bdd.mer.components.Component>> getHierarchy(EntityWrapper parent) {

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

        Hierarchy newHierarchy = new Hierarchy(symbol, parent, this);

        Discriminant discriminant = null;

        if (symbol.equals(HierarchySymbol.DISJUNCT)) {

            String discriminantText = JOptionPane.showInputDialog(
                    this,
                    null,
                    "Enter a discriminant",
                    JOptionPane.QUESTION_MESSAGE // Message Source.
            );

            discriminant = new Discriminant(discriminantText, this);
        }

        Line parentLine;

        if (totalButton.isSelected()) {

            if (discriminant != null) {

                parentLine = new GuardedLine.Builder(this, parent, newHierarchy, discriminant)
                        .lineMultiplicity(new DoubleLine(3)).build();
            } else {

                parentLine = new Line.Builder(this, parent, newHierarchy)
                        .lineMultiplicity(new DoubleLine(3)).build();
            }

        } else {

            if (discriminant != null) {

                parentLine = new GuardedLine.Builder(this, parent, newHierarchy, discriminant)
                        .strokeWidth(2).build();
            } else {

                parentLine = new Line.Builder(this, parent, newHierarchy)
                        .strokeWidth(2).build();
            }

            // This way, setting the stroke, it's noticeable who is the parent of the hierarchy.
        }

        newHierarchy.setParentLine(parentLine);

        List<com.bdd.mer.components.Component> componentsToAdd = new ArrayList<>();

        componentsToAdd.add(parentLine);

        if (discriminant != null) {
            componentsToAdd.add(discriminant);
        }

        return new Pair<>(newHierarchy, componentsToAdd);
    }

    /**
     * Allows the user to select, from the selected entities, an {@code Entity} to be the parent.
     *
     * @return {@code Hierarchy} selected to be the parent of the {@code Hierarchy}.
     */
    public EntityWrapper selectParent() {

        List<EntityWrapper> entidadesSeleccionadas = this.getSelectedComponentsByClass(EntityWrapper.class);
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
    public List<EntityWrapper> getChildrenList(EntityWrapper parent) {

        List<EntityWrapper> entidadesSeleccionadas = this.getSelectedComponentsByClass(EntityWrapper.class);

        List<EntityWrapper> retorno = new ArrayList<>(entidadesSeleccionadas);
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

        this.repaint();

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                 Add Note                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Adds a new <Code>Note</Code> to the <Code>this</Code>.
     * <p></p>
     * At least three strong or weak entities must be selected.
     */
    public void addNote() {

        String text = JOptionPane.showInputDialog(
                this,
                null,
                LanguageManager.getMessage("input.text"),
                JOptionPane.QUESTION_MESSAGE
        );

        if (text != null) {
            this.addComponent(new Note(text, this.getMouseX(), this.getMouseY(), this));
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

        List<com.bdd.mer.components.Component> selectedComponents = this.getSelectedComponents();

        if (!selectedComponents.isEmpty()) {

            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    LanguageManager.getMessage("input.delete"),
                    LanguageManager.getMessage("title.delete"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirmation == JOptionPane.YES_OPTION) {

                Set<com.bdd.mer.components.Component> componentsForRemoval = new HashSet<>();

                for (com.bdd.mer.components.Component component : selectedComponents) {

                    if (!component.canBeDeleted()) {
                        return;
                    }

                    componentsForRemoval.addAll(component.getComponentsForRemoval());
                    componentsForRemoval.add(component);
                }

                for (com.bdd.mer.components.Component component : componentsForRemoval) {
                    this.removeComponent(component);
                }

            }

            this.repaint();
        } else {
            JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.delete"));
        }

        // Desactiva el modo de selección
        this.cleanSelectedComponents();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Rename Component                                                 */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Renames a {@code Component}.
     */
    public void renameComponent(com.bdd.mer.components.Component component) {

        String newText;

        do {

            newText= JOptionPane.showInputDialog(
                    this,
                    null,
                    LanguageManager.getMessage("input.newText"),
                    JOptionPane.QUESTION_MESSAGE
            );

            // "newText" can be null when the user pressed "cancel"
            if (newText != null && newText.isEmpty()) {
                JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.oneCharacter"));
            }
        } while (newText != null && newText.isEmpty());

        // If "Cancel" was not pressed
        if (newText != null) {
            component.setText(newText);
            this.repaint();
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
        JCheckBox boxOptional = new JCheckBox(LanguageManager.getMessage("attribute.optional"));
        JCheckBox boxMultivalued = new JCheckBox(LanguageManager.getMessage("attribute.multivalued"));

        // Array of the components of the panel
        Object[] options = {
                boxOptional, boxMultivalued
        };

        // Show the confirmation dialog
        JOptionPane pane = new JOptionPane(
                options,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );

        // Create the dialog directly
        JDialog dialog = pane.createDialog(this, LanguageManager.getMessage("input.attributeInformation"));

        dialog.setVisible(true);

        // Get the selected value after the dialog is closed
        Object selectedValue = pane.getValue();
        if (selectedValue != null && (int) selectedValue == JOptionPane.OK_OPTION) {

            String name = this.getNameForComponent();

            if (name == null) {
                return;
            }

            AttributeArrow arrowBody = (boxOptional.isSelected()) ? AttributeArrow.OPTIONAL : AttributeArrow.NON_OPTIONAL;
            AttributeEnding arrowEnding = (boxMultivalued.isSelected()) ? AttributeEnding.MULTIVALUED : AttributeEnding.NON_MULTIVALUED;

            Attribute newAttribute = new Attribute(component, name, attributeSymbol, arrowBody, arrowEnding, this);

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
                JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.mainAttribute"));
                return;
            }

            String name = this.getNameForComponent();

            if (name == null) {
                return;
            }

            Attribute newAttribute = new MainAttribute(component, name, this);

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
    private String getNameForComponent() {

        String name = JOptionPane.showInputDialog(
                this,
                null,
                LanguageManager.getMessage("input.name"),
                JOptionPane.QUESTION_MESSAGE
        );

        boolean nameIsEmpty = false;
        boolean nameIsDuplicated = false;

        if (name != null) {

            nameIsEmpty = name.trim().isEmpty();

            if (!nameIsEmpty) {

                nameIsDuplicated = this.existsComponent(name);
            }
        }

        while (name != null && (nameIsEmpty || nameIsDuplicated)) {

            if (nameIsEmpty) {

                JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.emptyName"));
            } else {

                JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.nameDuplicated"));
            }

            name = JOptionPane.showInputDialog(
                    this,
                    null,
                    LanguageManager.getMessage("input.name"),
                    JOptionPane.QUESTION_MESSAGE
            );

            if (name != null) {

                nameIsEmpty = name.trim().isEmpty();

                if (!nameIsEmpty) {

                    nameIsDuplicated = this.existsComponent(name);
                }
            }
        }

        return name;
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

        this.addComponent(attribute);

        // This is necessary due to the repaint will not be done until this method ends, because it's asynchronous.
        // Maybe it would be good to search other possible solutions because this is not so efficient...
        this.paintImmediately(this.getBounds());

        attribute.setDrawingPriority(4);

        this.sortComponents();

        this.repaint();
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
        this.repaint();

    }

    /**
     * This method allows the user to change the number of values an attribute can take.
     *
     * @param attribute The attribute whose number of possible values will be changed.
     */
    public void changeMultivalued(Attribute attribute) {

        attribute.changeMultivalued();
        this.repaint();

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

        if (this.getSelectedComponents().size() == 1 && this.onlyTheseClassesAreSelected(Relationship.class)) {

            Relationship relationship = (Relationship) this.getSelectedComponents().getFirst();

            if (relationship.allMaxCardinalitiesAreN()) {

                Association association = new Association(relationship, this);

                this.addComponent(association);
                this.repaint();

                this.cleanSelectedComponents();
            } else {

                JOptionPane.showMessageDialog(this, "An association can only be created for N:N or N:N:N relationships.");
            }

        } else {
            JOptionPane.showMessageDialog(null, LanguageManager.getMessage("input.selectRelationship"));
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                Get JPopupMenu                                                  */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
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

    /**
     * Sets focus on the JComponent.
     *
     * @param component {@code JComponent} to be focused.
     */
    private void setFocus(JComponent component) {

        component.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                component.requestFocusInWindow();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }
}
