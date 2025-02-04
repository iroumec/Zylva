package com.bdd.mer.derivation;

import com.bdd.mer.components.Component;
import com.bdd.mer.frame.DrawingPanel;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * The format adopted must be:
 */
public class DerivationManager {

    private static final Pattern pattern = Pattern.compile(
            "([A-Za-z]+)\\[([A-Za-z]+)]\\(([^)]+)\\)(?:\\[([A-Za-z0-9, ()]+)])?"
    );
    private static final Pattern cardinalityPattern = Pattern.compile("([A-Za-z0-9]+)\\((\\w+), (\\w+)\\)");
    private static final Pattern classPattern = Pattern.compile("^([A-Za-z]+)");
    private static final Pattern namePattern = Pattern.compile("\\[([a-zA-Z0-9]+)]");
    private static final Pattern attributesPattern = Pattern.compile("\\(([^)]+)\\)");
    private static final Map<String, Derivation> derivations = new HashMap<>();

    public static void derivate(DrawingPanel drawingPanel) {

        List<Component> components = drawingPanel.getListComponents().reversed();

        for (Component component : components) {

            if (component instanceof Derivable derivableComponent) {

                derivate(derivableComponent.parse());
            }
        }

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

            System.out.println("Clase: " + className);
            System.out.println("Nombre: " + name);
            System.out.println("Atributos: " + attributes);

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
            derivation.addAttribute(attribute.trim());
        }
    }

    private static void manageRelationship(String relationshipName, String cardinalities) {
        Pattern cardinalityPattern = Pattern.compile("([A-Za-z0-9]+)\\((\\w+), (\\w+)\\)");
        Matcher cardinalityMatcher = cardinalityPattern.matcher(cardinalities);

        System.out.println("Cardinalidades:");

        while (cardinalityMatcher.find()) {
            String name2 = cardinalityMatcher.group(1);
            String minCardinality = cardinalityMatcher.group(2);
            String maxCardinality = cardinalityMatcher.group(3);

            System.out.println("Nombre: " + name2 + ", Cardinalidad mínima: " + minCardinality + ", Cardinalidad máxima: " + maxCardinality);
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

        System.out.println("America");

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

        if (oneSideMinCardinality.equals("0")) {
            oneSideDerivation.copyIdentificationAttributesAsOptional(nSideDerivation);
        } else {
            oneSideDerivation.copyIdentificationAttributes(nSideDerivation);
        }

        // Regla de negocio para la cardinalidad mínima.

        derivations.remove(relationshipName);
    }

    private static void derivate1_1Relationship() {

    }

    private static void derivateN_NRelationship() {

    }

    private static void createDerivation1(String parsedContent) throws Exception {

        Matcher nameMatcher = namePattern.matcher(parsedContent);
        Matcher attributesMatcher = attributesPattern.matcher(parsedContent);

        String name = "";
        List<String> attributesList = new ArrayList<>();

        if (nameMatcher.find()) {
            System.out.println("Nombre: " + nameMatcher.group(1));
        }

        if (attributesMatcher.find()) {
            String attributes = attributesMatcher.group(1);
            String[] attributesArray = attributes.split(",");
            System.out.println("Atributos:");
            for (String attribute : attributesArray) {
                attributesList.add(attribute.trim());
            }
        }

        if (derivations.containsKey(name)) {
            throw new Exception();
        }

        Derivation derivation = new Derivation(name);

        for (String attribute : attributesList) {
            derivation.addAttribute(attribute);
        }

        derivations.put(name, derivation);
    }

    private static void derivat4e(Derivable entity) {

        String parsedContent = entity.parse();

        // Expresión regular para capturar el nombre y los atributos
        String regex = "\\[([a-zA-Z0-9]+)]\\(([^)]+)\\)";

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

    private static void formatToHTML() {

    }
    // EntityWrapper[Yes](a) -> Yes(a)
    // EntityWrapper[No](b) -> No(b)
    // Relationship[Was](Yes(1,1);No(0, N)) -> No(b, a) and Yes is empty. So, it stops existing.
    // Relationship[Was]()[]

}
