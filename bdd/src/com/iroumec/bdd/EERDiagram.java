package com.iroumec.bdd;

import com.iroumec.core.Component;
import com.iroumec.core.Diagram;
import com.iroumec.components.Line;
import com.iroumec.components.Note;
import com.iroumec.bdd.derivation.Constraint;
import com.iroumec.bdd.derivation.Derivable;
import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.Element;
import com.iroumec.bdd.derivation.elements.ElementDecorator;
import com.iroumec.bdd.derivation.elements.SingleElement;
import com.iroumec.bdd.derivation.exporters.DerivationExporter;
import com.iroumec.bdd.eerd.relationship.Association;
import com.iroumec.bdd.eerd.attribute.Attribute;
import com.iroumec.bdd.eerd.entity.EntityWrapper;
import com.iroumec.bdd.eerd.hierarchy.exclusivity.Discriminant;
import com.iroumec.bdd.eerd.hierarchy.Hierarchy;
import com.iroumec.bdd.eerd.relationship.Cardinality;
import com.iroumec.bdd.eerd.relationship.Relationship;
import com.iroumec.core.Item;
import com.iroumec.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.*;

public final class EERDiagram extends Diagram {

    @Override
    protected boolean addingIsValid(@NotNull Component component) {

        Set<Class<?>> allowedTypes = Set.of(
                Relationship.class, Cardinality.class, Hierarchy.class, Discriminant.class,
                EntityWrapper.class, Attribute.class, Association.class, Line.class, Note.class
        );

        if (allowedTypes.stream().noneMatch(type -> type.isInstance(component))) {
            throw new IllegalArgumentException(
                    "The component must be a type of \n" + allowedTypes
                    + "\nProvided: " + component.getClass().getName()
            );
        }

        return true;
    }

    @Override
    public JPopupMenu getBackgroundPopupMenu() {

        JPopupMenu backgroundPopupMenu = new JPopupMenu();

        JMenuItem addEntity = new JMenuItem(LanguageManager.getMessage("action.addEntity"));
        addEntity.addActionListener(_ -> {
            EntityWrapper.addEntity(this);
            cleanSelectedComponents();
        });

        JMenuItem addRelationship = new JMenuItem(LanguageManager.getMessage("action.addRelationship"));
        addRelationship.addActionListener(_ -> {
            Relationship.addRelationship(this, this.getSelectedComponents());
            cleanSelectedComponents();
        });

        JMenuItem addDependency = new JMenuItem(LanguageManager.getMessage("action.addDependency"));
        addDependency.addActionListener(_ -> {
            Relationship.addDependency(this, this.getSelectedComponents());
            cleanSelectedComponents();
        });

        JMenuItem addHierarchy = new JMenuItem(LanguageManager.getMessage("action.addHierarchy"));
        addHierarchy.addActionListener(_ -> {
            Hierarchy.addHierarchy(this, this.getSelectedComponents());
            cleanSelectedComponents();
        });

        JMenuItem addNote = new JMenuItem(LanguageManager.getMessage("action.addNote"));
        addNote.addActionListener(_ -> {
            Note.addNote(this);
            cleanSelectedComponents();
        });

        backgroundPopupMenu.add(addEntity);
        backgroundPopupMenu.add(addRelationship);
        backgroundPopupMenu.add(addDependency);
        backgroundPopupMenu.add(addHierarchy);
        backgroundPopupMenu.add(addNote);

        return backgroundPopupMenu;
    }

    @Override
    public List<JButton> getMainFrameKeys() {

        List<JButton> out = super.getMainFrameKeys();

        JButton addEntityKey = new JButton();
        addEntityKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "actionE");
        addEntityKey.getActionMap().put("actionE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EntityWrapper.addEntity(EERDiagram.this);
                EERDiagram.this.cleanSelectedComponents();
            }
        });
        out.add(addEntityKey);

        JButton addRelationshipKey = new JButton();
        addRelationshipKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "actionR");
        addRelationshipKey.getActionMap().put("actionR", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Relationship.addRelationship(EERDiagram.this, EERDiagram.this.getSelectedComponents());
                EERDiagram.this.cleanSelectedComponents();
            }
        });
        out.add(addRelationshipKey);

        JButton addDependencyKey= new JButton();
        addDependencyKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "actionD");
        addDependencyKey.getActionMap().put("actionD", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Relationship.addDependency(EERDiagram.this, EERDiagram.this.getSelectedComponents());
                EERDiagram.this.cleanSelectedComponents();
            }
        });
        out.add(addDependencyKey);

        JButton addHierarchyKey= new JButton();
        addHierarchyKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK), "actionH");
        addHierarchyKey.getActionMap().put("actionH", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hierarchy.addHierarchy(EERDiagram.this, EERDiagram.this.getSelectedComponents());
                EERDiagram.this.cleanSelectedComponents();
            }
        });
        out.add(addHierarchyKey);

        JButton addNoteKey= new JButton();
        addNoteKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "actionN");
        addNoteKey.getActionMap().put("actionN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Note.addNote(EERDiagram.this);
                EERDiagram.this.cleanSelectedComponents();
            }
        });
        out.add(addNoteKey);

        return out;
    }

    @Override
    public List<Item> getFileMenuItems() {
        List<Item> out = super.getFileMenuItems();

        Item derivate = new Item("fileMenu.derivate");
        derivate.addActionListener(_ -> this.derivate());
        out.add(derivate);

        return out;
    }

    @Override
    public List<ResourceBundle> getResourceBundles(Locale currentLocale) {

        List<ResourceBundle> out = super.getResourceBundles(currentLocale);

        out.add(ResourceBundle.getBundle("EERresources/messages", currentLocale));

        return out;
    }

    private void derivate() {
        Map<String, Derivation> derivations = new HashMap<>();

        this.getDiagramComponents().stream()
                .filter(Derivable.class::isInstance)
                .map(Derivable.class::cast)
                .flatMap(derivable -> derivable.getDerivations().stream())
                .forEach(derivation -> addDerivation(derivation, derivations));

        List<Constraint> constraints = new ArrayList<>();
        fillReferences(derivations, constraints);

        DerivationExporter.exportToHTML(derivations.values(), constraints);
    }


    private void addDerivation(Derivation newDerivation, Map<String, Derivation> derivations) {

        if (!newDerivation.isEmpty()) {

            if (derivations.containsKey(newDerivation.getName())) {

                Derivation currentDerivation = derivations.get(newDerivation.getName());

                Derivation unification = Derivation.unify(currentDerivation, newDerivation);

                derivations.put(unification.getName(), unification);
            } else {

                derivations.put(newDerivation.getName(), newDerivation);
            }
        }
    }

    private void addReferencialIntegrityConstraint(Constraint newConstraint, List<Constraint> constraints) {

        if (constraints.contains(newConstraint)) {

            Constraint constraint = constraints.get(constraints.indexOf(newConstraint));

            if (newConstraint.hasSameReferencesAs(constraint)) {

                // In case of facing an N:N unary relation.
                newConstraint.setAsDuplicated();

                constraints.add(newConstraint);
            } else {

                newConstraint.transferConstraintsTo(constraint);
            }

        } else {
            constraints.add(newConstraint);
        }

    }

    /**
     * The order in which the derivation are analyzed doesn't matter.
     */
    private void fillReferences(Map<String, Derivation> derivations, List<Constraint> constraints) {

        // To avoid ConcurrentModificationException.
        List<Derivation> derivationsToRemove = new ArrayList<>();
        List<Derivation> alreadyFilledDerivations = new ArrayList<>();

        for (Derivation derivation : derivations.values()) {

            if (!alreadyFilledDerivations.contains(derivation)) {
                fillReferences(derivation, derivations, constraints, derivationsToRemove, alreadyFilledDerivations);
            }
        }

        for (Derivation derivation : derivationsToRemove) {
            derivations.remove(derivation.getName());
        }
    }

    private void fillReferences(Derivation derivation,
                                       Map<String, Derivation> derivations,
                                       List<Constraint> constraints,
                                       List<Derivation> derivationsToRemove,
                                       List<Derivation> alreadyFilledDerivations) {

        List<SingleElement> replacementsNeeded = derivation.getReplacementNeeded();

        for (SingleElement elementToReplace : replacementsNeeded) {

            Derivation replacementDerivation = derivations.get(elementToReplace.getName());

            if (replacementDerivation != null) {

                // Useful in case of having a reference to a reference... and don't depend on the order.
                fillReferences(replacementDerivation, derivations, constraints,
                        derivationsToRemove, alreadyFilledDerivations);

                Element replacement = elementToReplace.abstractElements(
                        derivations.get(elementToReplace.getName())
                );

                if (replacement != null) {

                    // A derivation must be removed if we take all the elements from its common elements,
                    // and it doesn't have identifier elements.
                    if (replacementDerivation.getNumberOfCommonElements() == replacement.getNumberOfElements()
                            && replacementDerivation.getNumberOfIdentificationElements() == 0) {
                        // There is no need for the replacement derivation to still existing.
                        derivationsToRemove.add(replacementDerivation);
                    }

                    if (elementToReplace.generatesConstraints()) {
                        extractConstraints(derivation.getName(), replacementDerivation.getName(),
                                replacement, constraints);
                    }

                    derivation.replace(elementToReplace, replacement);
                }
            }
        }

        alreadyFilledDerivations.add(derivation);
    }

    private void extractConstraints(String referencing, String referenced,
                                           Element replacement, List<Constraint> constraints) {

        Constraint constraint = new Constraint(referencing, referenced);

        List<SingleElement> elements = replacement.getPartitions();

        for (SingleElement element : elements) {

            Element firstElement = element.getCopy();
            firstElement.clearAllDecorations();

            // It's important that they are two distinct objects.
            Element secondElement = firstElement.getCopy();

            // We are in the case of a unary relationship....
            if (referencing.equals(referenced)) {

                firstElement.addDecoration(ElementDecorator.DUPLICATED);
            }

            constraint.addReference(firstElement, secondElement);
        }

        addReferencialIntegrityConstraint(constraint, constraints);
    }

    @Override
    public String getControls() {

        return "\nCtrl + E: " + LanguageManager.getMessage("control.addEntity")
                + "\nCtrl + R: " + LanguageManager.getMessage("control.addRelationship")
                + "\nCtrl + D: " + LanguageManager.getMessage("control.addDependency")
                + "\nCtrl + H: " + LanguageManager.getMessage("control.addHierarchy")
                + "\nCtrl + N: " + LanguageManager.getMessage("control.addNote")
                + super.getControls();
    }
}
