package com.bdd.mer.derivation;

import com.bdd.mer.components.Component;
import com.bdd.mer.frame.DrawingPanel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * The format adopted must be:
 */
public class DerivationManager {

    private static final Map<String, Derivation> derivations = new HashMap<>();
    private static final List<ReferencialIntegrityConstraint> referentialIntegrityConstraints = new ArrayList<>();
    private static final Pattern pattern = Pattern.compile(
            "([A-Za-záéíóúÁÉÍÓÚñÑ]+)\\[([A-Za-záéíóúÁÉÍÓÚñÑ]+)]\\(([^)]*)\\)(?:\\[([A-Za-z0-9, ();áéíóúÁÉÍÓÚñÑ]+(?:\\([0-9, ]+\\))?;?)+])?"
    );
    private static final Pattern cardinalityPattern = Pattern.compile("([A-Za-z0-9]+)\\((\\w+), (\\w+)\\)");

    public static void derivate(DrawingPanel drawingPanel) {

        List<Component> components = drawingPanel.getListComponents().reversed();

        for (Component component : components) {

            if (component instanceof Derivable derivableComponent) {

                String parsedContent = derivableComponent.parse();

                if (parsedContent != null) {
                    derivate(parsedContent);
                }
            }
        }

        cleanEmptyDerivations();

        formatToHTML();

        derivations.clear();
    }

    private static void derivate(String parsedContent) {

        Matcher matcher = pattern.matcher(parsedContent);

        if (matcher.find()) {

            // Captura de la clase, nombre y atributos
            String className = matcher.group(1);
            String name = matcher.group(2);
            String attributes = matcher.group(3);

            // For optimization.
            if (!className.equals("Attribute") || !attributes.trim().isEmpty()) {
                createDerivation(name, attributes);
            }

            // Si existe la parte adicional de cardinalidad...
            String cardinalities = matcher.group(4);
            if (cardinalities != null) {
                manageRelationship(name, cardinalities);
            }
        }
    }

    private static void createDerivation(String name, String attributes) {

        if (!derivations.containsKey(name)) {

            Derivation derivation = new Derivation(name);

            addAttributes(derivation, attributes);

            derivations.put(name, derivation);
        } else if (!attributes.trim().isEmpty()) {
            // We are facing a compound attribute.
            searchAndReplaceCompound(name, attributes);
        }
    }

    private static void addAttributes(Derivation derivation, String attributes) {
        String[] attributesArray = attributes.split(DerivationFormater.SEPARATOR);
        for (String attribute : attributesArray) {
            if (!attribute.isEmpty()) {

                String cleanAttribute = DerivationFormater.cleanAllFormats(attribute);

                if (attribute.contains(DerivationFormater.MULTIVALUED_ATTRIBUTE)) {
                    ReferencialIntegrityConstraint constraint = new ReferencialIntegrityConstraint(cleanAttribute, derivation.getName());
                    constraint.addReference(derivation.getName(), derivation.getName());
                    referentialIntegrityConstraints.add(constraint);
                    Derivation multivaluedAttribute = new Derivation(cleanAttribute);
                    multivaluedAttribute.addAttribute(
                            DerivationFormater.MAIN_ATTRIBUTE +
                                    DerivationFormater.FOREIGN_ATTRIBUTE +
                                    derivation.getName()
                    );
                    multivaluedAttribute.addAttribute((DerivationFormater.MAIN_ATTRIBUTE + cleanAttribute).trim());
                    derivations.put(cleanAttribute, multivaluedAttribute);
                } else {
                    derivations.put(cleanAttribute, new Derivation(cleanAttribute)); // This is useful for compound attributes.
                    derivation.addAttribute(attribute.trim());
                }
            }
        }
    }

    private static void addIdentificationAttributes(Derivation derivation, String attributes) {
        String[] attributesArray = attributes.split(DerivationFormater.SEPARATOR);
        for (String attribute : attributesArray) {
            if (!attribute.isEmpty()) {

                String cleanAttribute = DerivationFormater.cleanAllFormats(attribute);

                derivations.put(cleanAttribute, new Derivation(cleanAttribute)); // This is useful for compound attributes.
                derivation.addAttribute(DerivationFormater.MAIN_ATTRIBUTE + attribute.trim());
            }
        }
    }

    private static void searchAndReplaceCompound(String name, String attributes) {

        Map<String, Derivation> derivationCopy = new HashMap<>(derivations);

        for (Map.Entry<String, Derivation> entry : derivationCopy.entrySet()) {
            Derivation derivation = entry.getValue();

            if (derivation.hasCommonAttribute(name)) {
                derivation.removeAttribute(name);
                addAttributes(derivation, attributes);
            }

            if (derivation.hasIdentificationAttribute(name)) {
                derivation.removeAttribute(name);
                // ¿Por qué mejor no un method replace?
                addIdentificationAttributes(derivation, attributes);
            }
        }

        derivations.remove(name);
    }

    private static void manageRelationship(String relationshipName, String cardinalities) {
        Matcher cardinalityMatcher = cardinalityPattern.matcher(cardinalities);

        List<String> names = new ArrayList<>();
        List<String> minCardinalities = new ArrayList<>();
        List<String> maxCardinalities = new ArrayList<>();

        while (cardinalityMatcher.find()) {
            names.add(cardinalityMatcher.group(1));
            minCardinalities.add(cardinalityMatcher.group(2));
            maxCardinalities.add(cardinalityMatcher.group(3));
        }

        if (names.size() == 2) {
            manageBinaryRelationship(relationshipName, names, minCardinalities, maxCardinalities);
        } else if (names.size() == 3) {
            manageTernaryRelationship(relationshipName, names, maxCardinalities);
        }
    }

    private static void manageTernaryRelationship(String relationshipName,
                                                  List<String> names,
                                                  List<String> maxCardinalities) {

        Map<String, List<Integer>> cardinalityMap = new HashMap<>();

        int oneCount = 0;

        for (int i = 0; i < names.size(); i++) {

            String maxCardinality = maxCardinalities.get(i);

            if (maxCardinality.equals("1")) {
                oneCount++;
            }

            List<Integer> cardinality = cardinalityMap.getOrDefault(names.get(i), new ArrayList<>());
            cardinality.add(i);
            cardinalityMap.put(maxCardinality, cardinality);
        }

        List<ReferencialIntegrityConstraint> constraints = new ArrayList<>();
        Derivation relationshipDerivation = derivations.get(relationshipName);
        Derivation firstDerivation = derivations.get(names.getFirst());
        Derivation secondDerivation = derivations.get(names.get(1));
        Derivation thirdDerivation = derivations.get(names.getLast());
        List<Derivation> derivationList = Arrays.asList(firstDerivation, secondDerivation, thirdDerivation);

        switch (oneCount) {
            case 0: // N:N:N

                for (Derivation derivation : derivationList) {
                    constraints.add(
                            derivation.copyIdentificationAttributesAs(
                                    relationshipDerivation, DerivationFormater.FOREIGN_ATTRIBUTE));
                }

                break;
            case 1, 2, 3: // 1:N:N
                Derivation maxCardinalityEqualsToOneDerivation
                        = derivations.get(names.get(cardinalityMap.get("1").getFirst()));

                for (Derivation derivation : derivationList) {
                    if (!derivation.equals(maxCardinalityEqualsToOneDerivation)) {
                        constraints.add(derivation
                                .copyIdentificationAttributesAs(relationshipDerivation, DerivationFormater.FOREIGN_ATTRIBUTE));
                    } else {
                        constraints.add(derivation
                                .copyIdentificationAttributesAs(
                                        relationshipDerivation,
                                        DerivationFormater.FOREIGN_ATTRIBUTE,
                                        Derivation.AttributeType.COMMON
                                ));
                    }
                }
                break;
            // 1:1:N -- The difference here is that there are more possible combinations. But the logic is the same as case 1 if we only want one combination.
            //case 2: break;
            // 1:1:1 -- Again, more possible, combinations.
            //case 3: break;
            default: throw new RuntimeException("Invalid cardinality for ternary relationship.");
        }

        referentialIntegrityConstraints.addAll(constraints);
    }

    private static void manageBinaryRelationship(String relationshipName,
                                                 List<String> names,
                                                 List<String> minCardinalities,
                                                 List<String> maxCardinalities) {

        if (names.getFirst().equals(names.getLast())) {
            manageUnaryRelationship(relationshipName,names, minCardinalities, maxCardinalities);
            return;
        }

        if (maxCardinalities.getFirst().equals("1") || maxCardinalities.getLast().equals("1")) {

            if (maxCardinalities.getFirst().equals("1") && maxCardinalities.getLast().equals("1")) {
                derivate1_1Relationship(relationshipName, names, minCardinalities);
            } else {
                derivate1_NRelationship(relationshipName, names, minCardinalities, maxCardinalities);
            }
        } else {
            derivateN_NRelationship(relationshipName, names);
        }
    }

    private static void manageUnaryRelationship(String relationshipName,
                                                List<String> names,
                                                List<String> minCardinalities,
                                                List<String> maxCardinalities) {

        if (maxCardinalities.getFirst().equals("1") || maxCardinalities.getLast().equals("1")) {

            if (maxCardinalities.getFirst().equals("1") && maxCardinalities.getLast().equals("1")) {
                // I couldn't find any rules for this kind of derivation.
                derivate1_1Relationship(relationshipName, names, minCardinalities);
            } else {
                derivate1_NUnaryRelationship(relationshipName, names.getFirst(), minCardinalities, maxCardinalities);
            }
        } else {
            derivateN_NRelationship(relationshipName, names);
        }

    }

    private static void derivate1_NUnaryRelationship(String relationshipName, String name, List<String> minCardinalities, List<String> maxCardinalities) {

        String minCardinality = (maxCardinalities.getFirst().equals("N")) ? minCardinalities.getLast() : minCardinalities.getFirst();

        Derivation derivation = derivations.get(name);
        Derivation relationshipDerivation = derivations.get(relationshipName);
        relationshipDerivation.moveCommonAttributesTo(derivation);

        if (minCardinality.equals("0")) {
            referentialIntegrityConstraints.add(
                    derivation.copyIdentificationAttributesAs(
                            derivation,
                            DerivationFormater.OPTIONAL_ATTRIBUTE
                    ));
        } else { // It's equal to 1.
            referentialIntegrityConstraints.add(derivation.copyIdentificationAttributesAs(derivation));
        }
    }

    private static void derivate1_NRelationship(String relationshipName,
                                                List<String> names,
                                                List<String> minCardinalities,
                                                List<String> maxCardinalities) {
        String oneSideName = "", nSideName = "";
        String oneSideMinCardinality = "";

        for (int i = 0; i < names.size(); i++) {

            if (maxCardinalities.get(i).equals("1")) {
                oneSideName = names.get(i);
                oneSideMinCardinality = minCardinalities.get(i);
            } else {
                nSideName = names.get(i);
            }
        }

        Derivation nSideDerivation = derivations.get(nSideName);
        Derivation oneSideDerivation = derivations.get(oneSideName);
        Derivation relationshipDerivation = new Derivation(relationshipName);

        relationshipDerivation.moveAttributesTo(nSideDerivation);

        ReferencialIntegrityConstraint constraint;

        if (oneSideMinCardinality.equals("0")) {
            constraint = oneSideDerivation
                    .copyIdentificationAttributesAs(
                            nSideDerivation,
                            DerivationFormater.OPTIONAL_ATTRIBUTE
                    );
        } else {
            constraint = oneSideDerivation
                    .copyIdentificationAttributesAs(
                            nSideDerivation,
                            DerivationFormater.FOREIGN_ATTRIBUTE,
                            Derivation.AttributeType.COMMON
                    );
        }

        if (constraint != null) {
            referentialIntegrityConstraints.add(constraint);
        }

        derivations.remove(relationshipName);
    }

    private static void derivate1_1Relationship(String relationshipName,
                                                List<String> names,
                                                List<String> minCardinalities) {

        Derivation firstDerivation = derivations.get(names.getFirst());
        Derivation lastDerivation = derivations.get(names.getLast());
        Derivation relationshipDerivation = derivations.get(relationshipName);

        // Al this could be simplified.

        if (minCardinalities.getFirst().equals("0") || minCardinalities.getLast().equals("0")) {

            if (minCardinalities.getFirst().equals("0") && minCardinalities.getLast().equals("0")) { // Case (0,1):(0,1)

                // The order could be different.
                relationshipDerivation.moveAttributesTo(firstDerivation);
                referentialIntegrityConstraints.add(
                        lastDerivation.copyIdentificationAttributesAs(
                                firstDerivation,
                                DerivationFormater.FOREIGN_ATTRIBUTE + DerivationFormater.OPTIONAL_ATTRIBUTE
                        ));
            } else { // Case (0,1):(1,1)

                if (minCardinalities.getFirst().equals("0")) {

                    relationshipDerivation.moveAttributesTo(firstDerivation);
                    referentialIntegrityConstraints.add(lastDerivation.copyIdentificationAttributesAsAlternativeForeign(firstDerivation));
                } else {

                    relationshipDerivation.moveAttributesTo(lastDerivation);
                    referentialIntegrityConstraints.add(firstDerivation.copyIdentificationAttributesAsAlternativeForeign(lastDerivation));
                }
            }
        } else { // Case (1,1):(1,1)

            // In the case of (1,1):(1,1), the order could be different.

            relationshipDerivation.moveAttributesTo(firstDerivation);
            referentialIntegrityConstraints.add(lastDerivation.copyIdentificationAttributesAsAlternativeForeign(firstDerivation));
        }

        derivations.remove(relationshipName);
    }

    private static void derivateN_NRelationship(String relationshipName,
                                                List<String> names) {

        Derivation relationshipDerivation = derivations.get(relationshipName);

        Derivation firstDerivation = derivations.get(names.getFirst());
        Derivation lastDerivation = derivations.get(names.getLast());

        referentialIntegrityConstraints
                .add(firstDerivation.copyIdentificationAttributesAs(
                        relationshipDerivation,
                        DerivationFormater.FOREIGN_ATTRIBUTE
                )
        );

        referentialIntegrityConstraints
                .add(lastDerivation.copyIdentificationAttributesAs(
                        relationshipDerivation,
                        DerivationFormater.FOREIGN_ATTRIBUTE
                )
        );
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

        for (ReferencialIntegrityConstraint constraint : referentialIntegrityConstraints) {
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
