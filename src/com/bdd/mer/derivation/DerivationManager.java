package com.bdd.mer.derivation;

import com.bdd.mer.derivation.derivationObjects.DerivationObject;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.ElementDecorator;
import com.bdd.mer.derivation.elements.SingleElement;
import com.bdd.mer.derivation.exporters.DerivationExporter;
import com.bdd.GUI.Diagram;

import java.util.*;

public final class DerivationManager {

    public static void derivate(Diagram diagram) {

        Map<String, Derivation> derivations = new HashMap<>();
        List<Constraint> constraints = new ArrayList<>();

        List<Derivable> derivablesComponents = diagram.getListComponents().stream()
                .filter(component -> component instanceof Derivable)
                .map(component -> (Derivable) component)
                .toList();

        for (Derivable derivableComponent : derivablesComponents) {

            List<DerivationObject> derivationObjects = derivableComponent.getDerivations();

            for (DerivationObject derivationObject : derivationObjects) {

                derivationObject.generateDerivation();

                for (Derivation derivation : derivationObject.getDerivations()) {
                    addDerivation(derivation, derivations);
                }
            }

        }

        fillReferences(derivations, constraints);

        DerivationExporter.exportToHTML(derivations.values(), constraints);
    }

    private static void addDerivation(Derivation newDerivation, Map<String, Derivation> derivations) {

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

    private static void addReferencialIntegrityConstraint(Constraint newConstraint, List<Constraint> constraints) {

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
    private static void fillReferences(Map<String, Derivation> derivations, List<Constraint> constraints) {

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

    private static void fillReferences(Derivation derivation,
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

    private static void extractConstraints(String referencing, String referenced,
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
}
