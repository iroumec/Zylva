package com.bdd.mer.derivation;

import com.bdd.mer.components.Component;
import com.bdd.mer.derivation.derivationObjects.DerivationObject;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.ElementDecorator;
import com.bdd.mer.derivation.elements.SingleElement;
import com.bdd.mer.derivation.exporters.DerivationExporter;
import com.bdd.mer.frame.DrawingPanel;

import java.util.*;

/**
 *
 * The format adopted must be:
 */
public class DerivationManager {

    private static final Map<String, Derivation> derivations = new HashMap<>();
    private static final List<Constraint> referentialIntegrityConstraints = new ArrayList<>();

    public static void derivate(DrawingPanel drawingPanel) {

        List<Component> components = drawingPanel.getListComponents().reversed();

        for (Component component : components) {

            if (component instanceof Derivable derivableComponent) {

                List<DerivationObject> derivationObjects = derivableComponent.getDerivationObjects();

                for (DerivationObject derivationObject : derivationObjects) {

                    derivationObject.generateDerivation();

                    for (Derivation derivation : derivationObject.getDerivations()) {
                        addDerivation(derivation);
                    }
                }
            }
        }

        fillReferences();

        DerivationExporter.exportToHTML(derivations.values(), referentialIntegrityConstraints);

        derivations.clear();

        referentialIntegrityConstraints.clear();
    }

    private static void addDerivation(Derivation newDerivation) {

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

    private static void addReferencialIntegrityConstraint(Constraint newConstraint) {

        if (referentialIntegrityConstraints.contains(newConstraint)) {

            Constraint constraint = referentialIntegrityConstraints.get(referentialIntegrityConstraints.indexOf(newConstraint));

            if (newConstraint.hasSameReferencesAs(constraint)) {
                // In case of facing an N:N unary relation.

                newConstraint.setAsDuplicated();

                referentialIntegrityConstraints.add(newConstraint);
            } else {

                newConstraint.transferConstraintsTo(constraint);
            }

        } else {
            referentialIntegrityConstraints.add(newConstraint);
        }

    }

    /**
     * The order in which the derivation are analyzed doesn't matter.
     */
    private static void fillReferences() {

        // To avoid ConcurrentModificationException.
        List<Derivation> derivationToRemove = new ArrayList<>();

        for (Derivation derivation : derivations.values()) {

            List<SingleElement> replacementsNeeded = derivation.getReplacementNeeded();

            for (SingleElement elementToReplace : replacementsNeeded) {

                Derivation replacementDerivation = derivations.get(elementToReplace.getName());

                if (replacementDerivation != null) {
                    Element replacement = elementToReplace.abstractElements(
                            derivations.get(elementToReplace.getName())
                    );

                    if (replacement != null) {

                        // If all the elements will be replaced...
                        if (replacementDerivation.getNumberOfElements() == replacement.getNumberOfElements()) {
                            // There is no need for the replacement derivation to still existing.
                            derivationToRemove.add(replacementDerivation);
                        }

                        if (elementToReplace.generatesConstraints()) {
                            extractConstraints(derivation.getName(), replacementDerivation.getName(), replacement);
                        }

                        derivation.replace(elementToReplace, replacement);
                    }
                }
            }
        }

        for (Derivation derivation : derivationToRemove) {
            derivations.remove(derivation.getName());
        }
    }

    private static void extractConstraints(String referencing, String referenced, Element replacement) {

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

        addReferencialIntegrityConstraint(constraint);
    }
}
