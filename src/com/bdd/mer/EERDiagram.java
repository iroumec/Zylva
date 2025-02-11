package com.bdd.mer;

import com.bdd.mer.components.AttributableEERComponent;
import com.bdd.GUI.Component;
import com.bdd.mer.components.EERComponent;
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
import com.bdd.GUI.Diagram;
import com.bdd.GUI.userPreferences.LanguageManager;
import com.bdd.structures.Pair;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.util.*;
import java.util.List;

public class EERDiagram extends Diagram {

    @Override
    public void addComponent(@NotNull Component component) {
        if (!(component instanceof EERComponent)) {
            throw new IllegalArgumentException("The component must be a type of EERComponent.");
        }
        super.addComponent(component);
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

                Pair<Hierarchy, List<Component>> newHierarchyData = getHierarchy(parent);

                if (newHierarchyData == null) {
                    return;
                }

                Hierarchy newHierarchy = newHierarchyData.first();
                List<Component> componentsToAdd = newHierarchyData.second();

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

                for (Component component : componentsToAdd) {
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
    public Pair<Hierarchy, List<Component>> getHierarchy(EntityWrapper parent) {

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

        List<Component> componentsToAdd = new ArrayList<>();

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

        List<Component> selectedComponents = this.getSelectedComponents();

        if (!selectedComponents.isEmpty()) {

            int confirmation = JOptionPane.showConfirmDialog(
                    this,
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
    public void renameComponent(Component component) {

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
    public void addAttribute(AttributableEERComponent component) {
        addAttribute(component, AttributeSymbol.COMMON);
    }

    /**
     * Given an attributable component, this method adds it an attribute according to the specified symbol.
     *
     * @param component The attributable component owner of the attribute.
     * @param attributeSymbol The type of the attribute.
     */
    public void addAttribute(AttributableEERComponent component, AttributeSymbol attributeSymbol) {

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
    public void addComplexAttribute(AttributableEERComponent component) {

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
        addRelationship.addActionListener(_ -> Relationship.addRelationship(
                this,
                this.getSelectedComponents())
        );

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
}
