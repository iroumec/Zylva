//package com.bdd.mer.actions;
//
//import com.bdd.mer.components.AttributableEERComponent;
//import com.bdd.mer.components.attribute.*;
//import com.bdd.mer.components.relationship.Association;
//import com.bdd.mer.components.entity.EntityWrapper;
//import com.bdd.mer.components.hierarchy.HierarchySymbol;
//import com.bdd.GUI.components.Component;
//import com.bdd.mer.components.hierarchy.Hierarchy;
//import com.bdd.GUI.components.line.GuardedLine;
//import com.bdd.GUI.components.line.Line;
//import com.bdd.GUI.components.line.guard.Discriminant;
//import com.bdd.GUI.components.line.lineMultiplicity.DoubleLine;
//import com.bdd.GUI.components.line.guard.cardinality.Cardinality;
//import com.bdd.mer.components.relationship.Relationship;
//import com.bdd.GUI.Diagram;
//import com.bdd.GUI.components.note.Note;
//import com.bdd.GUI.userPreferences.LanguageManager;
//import com.bdd.GUI.structures.Pair;
//
//import javax.swing.*;
//import javax.swing.event.AncestorEvent;
//import javax.swing.event.AncestorListener;
//import java.awt.*;
//import java.io.Serializable;
//import java.util.*;
//import java.util.List;
//
//public final class ActionManager implements Serializable {
//
//    /**
//     * <Code>Diagram</Code> bounded to the <Code>ActionManager</Code>.
//     */
//    private Diagram diagram;
//
//    /**
//     * Constructs an <Code>ActionManager</Code>.
//     *
//     * @param diagram <Code>Diagram</Code> bounded to the <Code>ActionManager</Code>.
//     */
//    public ActionManager(Diagram diagram) {
//        this.diagram = diagram;
//    }
//
//    /**
//     * Construct an {@code ActionManager}.
//     * <p>
//     * To things work correctly, it's necessary to set a {@code Diagram}.
//     * @see #setDrawingPanel(Diagram)
//     */
//    public ActionManager() {
//        this(null);
//    }
//
//    /* ---------------------------------------------------------------------------------------------------------- */
//    /*                                              Add Entity                                                    */
//    /* ---------------------------------------------------------------------------------------------------------- */
//
//    /**
//     * Adds a new <Code>Entity</Code> to the <Code>Diagram</Code>.
//     */
//    public void addEntity() {
//
//        // It shows an emergent window to ask the user for the entity's name.
//        String entityName = JOptionPane.showInputDialog(
//                this.diagram,
//                null,
//                LanguageManager.getMessage("actionManager.addEntity.dialog"), // Title.
//                JOptionPane.QUESTION_MESSAGE // Message Source.
//        );
//
//        if (entityName != null) { // If it's null, the action was canceled.
//
//            if (!entityName.isEmpty()) {
//
//                if (!this.diagram.existsComponent(entityName)) {
//
//                    EntityWrapper entityWrapper = new EntityWrapper(
//                            entityName,
//                            diagram.getMouseX(),
//                            diagram.getMouseY(),
//                            this.diagram
//                    );
//
//                    diagram.addComponent(entityWrapper);
//                } else {
//
//                    JOptionPane.showMessageDialog(this.diagram, LanguageManager.getMessage("warning.nameDuplicated"));
//                    addEntity();
//                }
//            } else {
//
//                JOptionPane.showMessageDialog(this.diagram, LanguageManager.getMessage("warning.emptyName"));
//            }
//        }
//    }
//
//    /* -------------------------------------------------------------------------------------------------------------- */
//    /*                                            Add Hierarchy                                                       */
//    /* -------------------------------------------------------------------------------------------------------------- */
//
//    /**
//     * Adds a new <Code>Hierarchy</Code> to the <Code>Diagram</Code>.
//     * <p></p>
//     * At least three strong or weak entities must be selected.
//     */
//    public void addHierarchy() {
//
//        if (diagram.onlyTheseClassesAreSelected(EntityWrapper.class) && diagram.getSelectedComponents().size() >= 3) {
//
//            EntityWrapper parent = selectParent();
//
//            main: if (parent != null && !parent.isAlreadyParent()) {
//
//                List<EntityWrapper> subtipos = getChildrenList(parent);
//
//                Pair<Hierarchy, List<Component>> newHierarchyData = getHierarchy(parent);
//
//                if (newHierarchyData == null) {
//                    return;
//                }
//
//                Hierarchy newHierarchy = newHierarchyData.first();
//                List<Component> componentsToAdd = newHierarchyData.second();
//
//                for (EntityWrapper subtipo : subtipos) {
//                    newHierarchy.addChild(subtipo);
//
//                    if (!subtipo.addHierarchy(newHierarchy)) {
//
//                        // Repairing action.
//                        parent.removeHierarchy(newHierarchy);
//                        for (EntityWrapper s : subtipos) {
//                            s.removeHierarchy(newHierarchy);
//
//                            String message = LanguageManager.getMessage("warning.theEntity") + " "
//                                    + '\"' + subtipo.getText() + '\"'
//                                    + " " + LanguageManager.getMessage("warning.alreadyParticipatesInHierarchy") + " "
//                                    + LanguageManager.getMessage("warning.multipleInheritanceOnlyAllowed");
//
//                            JOptionPane.showMessageDialog(this.diagram, message);
//
//                            // Exit.
//                            break main;
//                        }
//                    }
//                }
//
//                parent.addHierarchy(newHierarchy);
//
//                for (Component component : componentsToAdd) {
//                    diagram.addComponent(component);
//                }
//
//                diagram.addComponent(newHierarchy);
//
//                } else {
//                    JOptionPane.showMessageDialog(this.diagram, LanguageManager.getMessage("warning.alreadyParent"));
//                }
//        } else {
//            JOptionPane.showMessageDialog(this.diagram, LanguageManager.getMessage("warning.threeEntities"));
//        }
//
//        // The selection mode is deactivated.
//        diagram.cleanSelectedComponents();
//    }
//
//    /**
//     * Creates a {@code Hierarchy} according to the options selected by the user.
//     *
//     * @param parent Entity parent of the hierarchy.
//     * @return {@code Hierarchy} according to the options selected by the user.
//     */
//    public Pair<Hierarchy, List<Component>> getHierarchy(EntityWrapper parent) {
//
//        // The radio buttons are created.
//        JRadioButton exclusiveButton = new JRadioButton(LanguageManager.getMessage("hierarchy.exclusive"), true);
//        JRadioButton overlapButton = new JRadioButton(LanguageManager.getMessage("hierarchy.overlap"));
//        JRadioButton totalButton = new JRadioButton(LanguageManager.getMessage("hierarchy.total"), true);
//        JRadioButton partialButton = new JRadioButton(LanguageManager.getMessage("hierarchy.partial"));
//
//        // The radio buttons are grouped so that only one can be selected at the same time.
//        ButtonGroup groupExclusivaCompartida = new ButtonGroup();
//        groupExclusivaCompartida.add(exclusiveButton);
//        groupExclusivaCompartida.add(overlapButton);
//
//        ButtonGroup groupTotalExclusiva = new ButtonGroup();
//        groupTotalExclusiva.add(totalButton);
//        groupTotalExclusiva.add(partialButton);
//
//        // A panel to contain the radio buttons is created.
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Añade un BoxLayout al panel
//
//        // A panel for each group of radio buttons is created.
//        JPanel panelEC = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        panelEC.add(exclusiveButton);
//        panelEC.add(overlapButton);
//
//        JPanel panelTP = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        panelTP.add(totalButton);
//        panelTP.add(partialButton);
//
//        panel.add(panelEC);
//        panel.add(panelTP);
//
//        int option = JOptionPane.showOptionDialog(null, panel, LanguageManager.getMessage("input.option"),
//                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
//
//        // If the user clicked "Cancel" or closed the window.
//        if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.CANCEL_OPTION) {
//            return null; // The process is canceled.
//        }
//
//        HierarchySymbol symbol = (exclusiveButton.isSelected()) ? HierarchySymbol.DISJUNCT : HierarchySymbol.OVERLAPPING;
//
//        Hierarchy newHierarchy = new Hierarchy(symbol, parent, this.diagram);
//
//        Discriminant discriminant = null;
//
//        if (symbol.equals(HierarchySymbol.DISJUNCT)) {
//
//            String discriminantText = JOptionPane.showInputDialog(
//                    this.diagram,
//                    null,
//                    "Enter a discriminant",
//                    JOptionPane.QUESTION_MESSAGE // Message Source.
//            );
//
//            discriminant = new Discriminant(discriminantText, this.diagram);
//        }
//
//        Line parentLine;
//
//        if (totalButton.isSelected()) {
//
//            if (discriminant != null) {
//
//                parentLine = new GuardedLine.Builder(this.diagram, parent, newHierarchy, discriminant)
//                        .lineMultiplicity(new DoubleLine(3)).build();
//            } else {
//
//                parentLine = new Line.Builder(this.diagram, parent, newHierarchy)
//                        .lineMultiplicity(new DoubleLine(3)).build();
//            }
//
//        } else {
//
//            if (discriminant != null) {
//
//                parentLine = new GuardedLine.Builder(this.diagram, parent, newHierarchy, discriminant)
//                        .strokeWidth(2).build();
//            } else {
//
//                parentLine = new Line.Builder(this.diagram, parent, newHierarchy)
//                        .strokeWidth(2).build();
//            }
//
//            // This way, setting the stroke, it's noticeable who is the parent of the hierarchy.
//        }
//
//        newHierarchy.setParentLine(parentLine);
//
//        List<Component> componentsToAdd = new ArrayList<>();
//
//        componentsToAdd.add(parentLine);
//
//        if (discriminant != null) {
//            componentsToAdd.add(discriminant);
//        }
//
//        return new Pair<>(newHierarchy, componentsToAdd);
//    }
//
//    /**
//     * Allows the user to select, from the selected entities, an {@code Entity} to be the parent.
//     *
//     * @return {@code Hierarchy} selected to be the parent of the {@code Hierarchy}.
//     */
//    public EntityWrapper selectParent() {
//
//        List<EntityWrapper> entidadesSeleccionadas = diagram.getSelectedComponentsByClass(EntityWrapper.class);
//        Object[] opciones = new Object[entidadesSeleccionadas.size()];
//
//        for (int i = 0; i < entidadesSeleccionadas.size(); i++) {
//            opciones[i] = (entidadesSeleccionadas.get(i)).getText();
//        }
//
//        // Muestra el JOptionPane con los botones
//        int selection = JOptionPane.showOptionDialog(null, LanguageManager.getMessage("hierarchy.selectParent"), "Selección",
//                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);
//
//        return (entidadesSeleccionadas.get(selection));
//
//    }
//
//    /**
//     * Given the parent of the hierarchy, it returns a list containing its children.
//     *
//     * @param parent {@code Entity} chosen as the parent of the hierarchy.
//     * @return {@code List<Entity>} containing the children entities of the hierarchy.
//     */
//    public List<EntityWrapper> getChildrenList(EntityWrapper parent) {
//
//        List<EntityWrapper> entidadesSeleccionadas = diagram.getSelectedComponentsByClass(EntityWrapper.class);
//
//        List<EntityWrapper> retorno = new ArrayList<>(entidadesSeleccionadas);
//        retorno.remove((parent));
//
//        return retorno;
//    }
//
//    /* -------------------------------------------------------------------------------------------------------------- */
//    /*                                                 Add Note                                                       */
//    /* -------------------------------------------------------------------------------------------------------------- */
//
//    /**
//     * Adds a new <Code>Note</Code> to the <Code>Diagram</Code>.
//     * <p></p>
//     * At least three strong or weak entities must be selected.
//     */
//    public void addNote() {
//
//        String text = JOptionPane.showInputDialog(
//                this.diagram,
//                null,
//                LanguageManager.getMessage("input.text"),
//                JOptionPane.QUESTION_MESSAGE
//        );
//
//        if (text != null) {
//            diagram.addComponent(new Note(text, diagram.getMouseX(), diagram.getMouseY(), this.diagram));
//        }
//    }
//
//    /* -------------------------------------------------------------------------------------------------------------- */
//    /*                                         Delete Selected Components                                             */
//    /* -------------------------------------------------------------------------------------------------------------- */
//
//    /**
//     * Deletes all the selected components and their close-related components.
//     * <p></p>
//     * At least a component must be selected.
//     */
//    public void deleteSelectedComponents() {
//
//        List<Component> selectedComponents = this.diagram.getSelectedComponents();
//
//        if (!selectedComponents.isEmpty()) {
//
//            int confirmation = JOptionPane.showConfirmDialog(
//                    this.diagram,
//                    LanguageManager.getMessage("input.delete"),
//                    LanguageManager.getMessage("title.delete"),
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.QUESTION_MESSAGE
//            );
//
//            if (confirmation == JOptionPane.YES_OPTION) {
//
//                Set<Component> componentsForRemoval = new HashSet<>();
//
//                for (Component component : selectedComponents) {
//
//                    if (!component.canBeDeleted()) {
//                        return;
//                    }
//
//                    componentsForRemoval.addAll(component.getComponentsForRemoval());
//                    componentsForRemoval.add(component);
//                }
//
//                for (Component component : componentsForRemoval) {
//                    this.diagram.removeComponent(component);
//                }
//
//            }
//
//            this.diagram.repaint();
//        } else {
//            JOptionPane.showMessageDialog(this.diagram, LanguageManager.getMessage("warning.delete"));
//        }
//
//        // Desactiva el modo de selección
//        diagram.cleanSelectedComponents();
//    }
//
//    /* -------------------------------------------------------------------------------------------------------------- */
//    /*                                               Rename Component                                                 */
//    /* -------------------------------------------------------------------------------------------------------------- */
//
//    /**
//     * Renames a {@code Component}.
//     */
//    public void renameComponent(Component component) {
//
//        String newText;
//
//        do {
//
//            newText= JOptionPane.showInputDialog(
//                    this.diagram,
//                    null,
//                    LanguageManager.getMessage("input.newText"),
//                    JOptionPane.QUESTION_MESSAGE
//            );
//
//            // "newText" can be null when the user pressed "cancel"
//            if (newText != null && newText.isEmpty()) {
//                JOptionPane.showMessageDialog(this.diagram, LanguageManager.getMessage("warning.oneCharacter"));
//            }
//        } while (newText != null && newText.isEmpty());
//
//        // If "Cancel" was not pressed
//        if (newText != null) {
//            component.setText(newText);
//            this.diagram.repaint();
//        }
//    }
//
//    /* -------------------------------------------------------------------------------------------------------------- */
//    /*                                              Add Association                                                   */
//    /* -------------------------------------------------------------------------------------------------------------- */
//
//    /**
//     * Add an association to the drawing panel.
//     * <p></p>
//     * There must be selected a relationship.
//     */
//    // There must be selected at least an entity and a relationship (unary relationship)
//    public void addAssociation() {
//
//        if (diagram.getSelectedComponents().size() == 1 && diagram.onlyTheseClassesAreSelected(Relationship.class)) {
//
//            Relationship relationship = (Relationship) diagram.getSelectedComponents().getFirst();
//
//            if (relationship.allMaxCardinalitiesAreN()) {
//
//                Association association = new Association(relationship, this.diagram);
//
//                diagram.addComponent(association);
//                diagram.repaint();
//
//                diagram.cleanSelectedComponents();
//            } else {
//
//                JOptionPane.showMessageDialog(this.diagram, "An association can only be created for N:N or N:N:N relationships.");
//            }
//
//        } else {
//            JOptionPane.showMessageDialog(null, LanguageManager.getMessage("input.selectRelationship"));
//        }
//    }
//
//    /* -------------------------------------------------------------------------------------------------------------- */
//    /*                                                Get JPopupMenu                                                  */
//    /* -------------------------------------------------------------------------------------------------------------- */
//
//    /**
//     * Given a list of actions, it returns a JPopupMenu that allows the component to perform all
//     * the specified actions.
//     *
//     * @param component The component owner of the actions.
//     * @param actions An infinite list of actions we want to add to the panel.
//     * @return A <code>JPopupMenu</code> containing all the specified options.
//     */
//
//    public JPopupMenu getPopupMenu(Component component, Action ... actions) {
//
//        JPopupMenu popupMenu = new JPopupMenu();
//
//        for (Action action : actions) {
//
//            JMenuItem actionItem = new JMenuItem(action.getText());
//
//            switch (action) {
//                case DELETE -> actionItem.addActionListener(_ -> deleteSelectedComponents());
//                case RENAME, CHANGE_TEXT -> actionItem.addActionListener(_ -> renameComponent(component));
//                case ADD_ATTRIBUTE -> actionItem.addActionListener(_ -> addAttribute((AttributableEERComponent) component));
//                case ADD_ASSOCIATION -> actionItem.addActionListener(_ -> addAssociation());
//                case ADD_COMPLEX_ATTRIBUTE -> actionItem.addActionListener(_ -> addComplexAttribute((AttributableEERComponent) component));
//                case SWAP_MULTIVALUED -> actionItem.addActionListener(_ -> changeMultivalued((Attribute) component));
//                case SWAP_OPTIONALITY -> actionItem.addActionListener(_ -> changeOptionality((Attribute) component));
//                case SWAP_EXCLUSIVITY -> actionItem.addActionListener(_ -> swapExclusivity((Hierarchy) component));
//                case CHANGE_CARDINALITY -> actionItem.addActionListener(_ -> changeCardinality((Cardinality) component));
//                case ADD_REFLEXIVE_RELATIONSHIP -> actionItem.addActionListener(_ -> addRelationship());
//            }
//
//            popupMenu.add(actionItem);
//        }
//
//        return popupMenu;
//    }
//
//    public JPopupMenu getBackgroundPopupMenu() {
//
//        JPopupMenu backgroundPopupMenu = new JPopupMenu();
//
//        JMenuItem addEntity = new JMenuItem(LanguageManager.getMessage("option.addEntity"));
//        addEntity.addActionListener(_ -> this.addEntity());
//
//        JMenuItem addRelationship = new JMenuItem(LanguageManager.getMessage("option.addRelationship"));
//        addRelationship.addActionListener(_ -> this.addRelationship());
//
//        JMenuItem addDependency = new JMenuItem(LanguageManager.getMessage("option.addDependency"));
//        addDependency.addActionListener(_ -> this.addDependency());
//
//        JMenuItem addHierarchy = new JMenuItem(LanguageManager.getMessage("option.addHierarchy"));
//        addHierarchy.addActionListener(_ -> this.addHierarchy());
//
//        JMenuItem addNote = new JMenuItem(LanguageManager.getMessage("option.addNote"));
//        addNote.addActionListener(_ -> this.addNote());
//
//        JMenuItem addAssociation = new JMenuItem(LanguageManager.getMessage("option.addAssociation"));
//        addAssociation.addActionListener(_ -> this.addAssociation());
//
//        backgroundPopupMenu.add(addEntity);
//        backgroundPopupMenu.add(addRelationship);
//        backgroundPopupMenu.add(addDependency);
//        backgroundPopupMenu.add(addHierarchy);
//        backgroundPopupMenu.add(addNote);
//        backgroundPopupMenu.add(addAssociation);
//
//        return backgroundPopupMenu;
//    }
//
//    public void setDrawingPanel(Diagram diagram) {
//        this.diagram = diagram;
//    }
//
//    /**
//     * Sets focus on the JComponent.
//     *
//     * @param component {@code JComponent} to be focused.
//     */
//    private void setFocus(JComponent component) {
//
//        component.addAncestorListener(new AncestorListener() {
//            @Override
//            public void ancestorAdded(AncestorEvent event) {
//                component.requestFocusInWindow();
//            }
//
//            @Override
//            public void ancestorRemoved(AncestorEvent event) {}
//
//            @Override
//            public void ancestorMoved(AncestorEvent event) {}
//        });
//    }
//
//}
