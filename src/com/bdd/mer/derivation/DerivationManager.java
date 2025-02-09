package com.bdd.mer.derivation;

import com.bdd.mer.components.Component;
import com.bdd.mer.derivation.derivationObjects.DerivationObject;
import com.bdd.mer.frame.DrawingPanel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

        cleanEmptyDerivations();

        formatToHTML();

        derivations.clear();
    }

    private static void addDerivation(Derivation newDerivation) {

        if (derivations.containsKey(newDerivation.getName())) {

            Derivation currentDerivation = derivations.get(newDerivation.getName());

            Derivation unification = Derivation.unify(currentDerivation, newDerivation);

            derivations.put(unification.getName(), unification);
        } else {
            derivations.put(newDerivation.getName(), newDerivation);
        }
    }

    private static void addReferencialIntegrityConstraint(Constraint newConstraint) {

        if (referentialIntegrityConstraints.contains(newConstraint)) {

            Constraint constraint = referentialIntegrityConstraints.get(referentialIntegrityConstraints.indexOf(newConstraint));

            newConstraint.transferConstraintsTo(constraint);
        } else {
            referentialIntegrityConstraints.add(newConstraint);
        }

    }

    private static void fillReferences() {

        for (Derivation derivation : derivations.values()) {

            List<String> replacementsNeeded = derivation.getReplacementNeeded();

            for (String replacementNeeded : replacementsNeeded) {

                derivation.replace(derivations.get(replacementNeeded));
            }
        }

        for (Derivation derivation : derivations.values()) {



        }

    }

    private static void cleanEmptyDerivations() {

        // This must be done due to ConcurrentModificationException.
        List<String> keysToRemove = new ArrayList<>();

        for (Map.Entry<String, Derivation> entry : derivations.entrySet()) {
            if (entry.getValue().isEmpty()) {
                keysToRemove.add(entry.getKey());
            }
        }

        for (String key : keysToRemove) {
            derivations.remove(key);
        }
    }

    private static void formatToHTML() {

        StringBuilder htmlContent =
                new StringBuilder("""
                        <!DOCTYPE html>
                        <html lang="es">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Estructura con formato</title>
                        """);

        htmlContent.append(DerivationFormater.getHTMLStyles());

        htmlContent.append("""
                            </head>
                            <body>
                            <h1>Derivation.</h1>
                            <div class="dotted-line"></div>
                            <h2>Relationships:</h2>
                            """);

        for (Derivation derivation : derivations.values()) {
            htmlContent
                    .append("<ul>\n")
                    .append("<li>").append(derivation.toString()).append("</li>\n")
                    .append("</ul>\n")
            ;
        }

        htmlContent
                .append("<div class=\"dotted-line\"></div>\n")
                .append("<h2>Referential integrity constraints:</h2>\n")
        ;

        for (Constraint constraint : referentialIntegrityConstraints) {
            htmlContent
                    .append("<ul>\n")
                    .append("<li>").append(constraint).append("</li>\n")
                    .append("</ul>\n")
            ;
        }

        htmlContent
                .append("<div class=\"dotted-line\"></div>\n")
                .append("<p>\n")
                .append("<span class=\"bold\">Disclaimer!</span>\n")
                .append("There could be more valid derivations. This is just one of those.\n")
                .append("</p>")
        ;

        htmlContent
                .append("<div class=\"dotted-line\"></div>\n")
                .append("</body>\n")
                .append("</html>\n")
        ;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("estructura.html"));
            writer.write(htmlContent.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
