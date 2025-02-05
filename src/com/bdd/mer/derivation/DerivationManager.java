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
            "([A-Za-z]+)\\[([A-Za-z]+)]\\(([^)]*)\\)(?:\\[([A-Za-z0-9, ();]+(?:\\([0-9, ]+\\))?;?)+])?"
    );
    private static final Pattern cardinalityPattern = Pattern.compile("([A-Za-z0-9]+)\\((\\w+), (\\w+)\\)");

    public static void derivate(DrawingPanel drawingPanel) {

        List<Component> components = drawingPanel.getListComponents().reversed();

        for (Component component : components) {

            if (component instanceof Derivable derivableComponent) {

                System.out.println("\n" + derivableComponent.parse());

                derivate(derivableComponent.parse());
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

            //System.out.println("Clase: " + className);
            //System.out.println("Nombre: " + name);
            //System.out.println("Atributos: " + attributes);

            createDerivation(name, attributes);

            // Si existe la parte adicional de cardinalidad
            String cardinalities = matcher.group(4);
            if (cardinalities != null) {
                manageRelationship(name, cardinalities);
            }
        }
    }

    private static void createDerivation(String name, String attributes) {

        if (derivations.containsKey(name)) {
            throw new IllegalStateException("Something wrong occurred.");
        }

        Derivation derivation = new Derivation(name);

        String[] attributesArray = attributes.split(",");
        for (String attribute : attributesArray) {
            if (!attribute.isEmpty()) {
                derivation.addAttribute(attribute.trim());
            }
        }

        derivations.put(name, derivation);
    }

    private static void manageRelationship(String relationshipName, String cardinalities) {
        Matcher cardinalityMatcher = cardinalityPattern.matcher(cardinalities);

        System.out.println("Cardinalidades:");

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
        }
    }

    private static void manageTernaryRelationship(String relationshipName,
                                                  List<String> names,
                                                  List<String> minCardinalities,
                                                  List<String> maxCardinalities) {

        // Derivation for ternary.

    }

    private static void manageBinaryRelationship(String relationshipName,
                                                 List<String> names,
                                                 List<String> minCardinalities,
                                                 List<String> maxCardinalities) {

        if (names.getFirst().equals(names.getLast())) {
            manageUnaryRelationship(relationshipName, names, minCardinalities, maxCardinalities);
            return;
        }

        if (maxCardinalities.getFirst().equals("1") || maxCardinalities.getLast().equals("1")) {

            if (maxCardinalities.getFirst().equals("1") && maxCardinalities.getLast().equals("1")) {
                derivate1_1Relationship();
            } else {
                derivate1_NRelationship(relationshipName, names, minCardinalities, maxCardinalities);
            }
        } else {
            derivateN_NRelationship();
        }
    }

    private static void manageUnaryRelationship(String relationshipName,
                                                List<String> names,
                                                List<String> minCardinalities,
                                                List<String> maxCardinalities) {
        System.out.println("Tes");
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
            constraint = oneSideDerivation.copyIdentificationAttributesAsOptional(nSideDerivation);
        } else {
            constraint = oneSideDerivation.copyIdentificationAttributes(nSideDerivation);
        }

        if (constraint != null) {
            referentialIntegrityConstraints.add(constraint);
        }

        // Regla de negocio para la cardinalidad mínima.

        derivations.remove(relationshipName);
    }

    private static void derivate1_1Relationship() {

    }

    private static void derivateN_NRelationship() {

    }

//    private static void createDerivation1(String parsedContent) throws Exception {
//
//        Matcher nameMatcher = namePattern.matcher(parsedContent);
//        Matcher attributesMatcher = attributesPattern.matcher(parsedContent);
//
//        String name = "";
//        List<String> attributesList = new ArrayList<>();
//
//        if (nameMatcher.find()) {
//            System.out.println("Nombre: " + nameMatcher.group(1));
//        }
//
//        if (attributesMatcher.find()) {
//            String attributes = attributesMatcher.group(1);
//            String[] attributesArray = attributes.split(",");
//            System.out.println("Atributos:");
//            for (String attribute : attributesArray) {
//                attributesList.add(attribute.trim());
//            }
//        }
//
//        if (derivations.containsKey(name)) {
//            throw new Exception();
//        }
//
//        Derivation derivation = new Derivation(name);
//
//        for (String attribute : attributesList) {
//            derivation.addAttribute(attribute);
//        }
//
//        derivations.put(name, derivation);
//    }

    private static void derivate5(String parsedContent) {

        // Expresión regular para capturar el nombre y los atributos
        //String regex = "\\[([a-zA-Z0-9]+)]\\(([^)]+)\\)";
        String regex = "([A-Za-z]+)\\[([A-Za-z]+)]\\(([^)]*)\\)";

        // Crear el patrón y el matcher
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(parsedContent);

        // Comprobar si la expresión regular hace match
        if (matcher.find()) {
            // Extraer el nombre
            String name = matcher.group(1);

            // Extraer los atributos y convertirlos en una lista
            String atributosStr = matcher.group(2);
            List<String> atributos = Arrays.asList(atributosStr.split("\\s*,\\s*"));

            // Mostrar resultados
            Derivation result = new Derivation(name);

            for (String attribute : atributos) {
                result.addAttribute(attribute);
            }

            derivations.put(name, result);

            System.out.println("Nombre: " + name);
            System.out.println("Atributos: " + atributos);
        } else {
            System.out.println("No se encontró el formato esperado.");
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
                            <style>
                                /* Definir estilos para las líneas punteadas */
                                .dotted-line {
                                    border-top: 1px dotted black;
                                    margin: 5px 0;
                                }
                                /* Estilo para subrayado */
                                .underline {
                                    text-decoration: underline;
                                }
                                /* Estilo para negritas */
                                .bold {
                                    font-weight: bold;
                                }
                                /* Estilo para texto con color */
                                .highlight {
                                    color: blue;
                                }
                            </style>
                        </head>
                        <body>
                        <h1>Derivation.</h1>
                        <div class="dotted-line"></div>
                        <h2>Relationships:</h2>
                        """
                );

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
            while (constraint.hasReferences()) {
                htmlContent
                        .append("<ul>\n")
                        .append("<li>").append(constraint.popReference()).append("</li>\n")
                        .append("</ul>\n")
                ;
            }
        }

        htmlContent
                .append("<div class=\"dotted-line\"></div>\n")
                .append("</body>\n")
                .append("</html>\n")
        ;

        // Guardar el contenido en un archivo HTML
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("estructura.html"));
            writer.write(htmlContent.toString());
            writer.close();

            System.out.println("Texto exportado a estructura.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    // EntityWrapper[Yes](a) -> Yes(a)
    // EntityWrapper[No](b) -> No(b)
    // Relationship[Was](Yes(1,1);No(0, N)) -> No(b, a) and Yes is empty. So, it stops existing.
    // Relationship[Was]()[]

}
