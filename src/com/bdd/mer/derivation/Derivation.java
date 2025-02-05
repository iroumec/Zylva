package com.bdd.mer.derivation;

import java.util.ArrayList;
import java.util.List;

class Derivation {

    private final String name;
    private final List<String> commonAttributes;
    private final List<String> identificationAttributes;

    Derivation(String name) {
        this.name = name;
        this.commonAttributes = new ArrayList<>();
        this.identificationAttributes = new ArrayList<>();
    }

    void addAttribute(String attribute) {
        if (attribute.startsWith(DerivationFormater.MAIN_ATTRIBUTE)) {
            addIdentificationAttribute(attribute);
        } else {
            addCommonAttribute(attribute);
        }
    }

    void removeAttribute(String attribute) {
        if (attribute.startsWith(DerivationFormater.MAIN_ATTRIBUTE)) {
            this.identificationAttributes.remove(attribute);
        } else {
            this.commonAttributes.remove(attribute);
        }
    }

    private void addIdentificationAttribute(String attribute) {
        this.identificationAttributes.add(attribute);
    }

    private void addCommonAttribute(String attribute) {
        this.commonAttributes.add(attribute);
    }

    ReferencialIntegrityConstraint copyIdentificationAttributes(Derivation derivation) {

        ReferencialIntegrityConstraint constraint = new ReferencialIntegrityConstraint(this.name, derivation.name);

        for (String attribute : this.identificationAttributes) {
            derivation.addIdentificationAttribute(attribute);
            constraint.addReference(attribute, attribute);
        }

        return constraint;
    }

    ReferencialIntegrityConstraint copyIdentificationAttributesAsAlternative(Derivation derivation) {

        ReferencialIntegrityConstraint constraint = new ReferencialIntegrityConstraint(this.name, derivation.name);

        for (String attribute : this.identificationAttributes) {

            String cleanAttribute = attribute.replace(DerivationFormater.MAIN_ATTRIBUTE, "");
            derivation.addCommonAttribute(DerivationFormater.ALTERNATIVE_ATTRIBUTE + cleanAttribute);
            constraint.addReference(cleanAttribute, cleanAttribute);
        }

        return constraint;
    }

    ReferencialIntegrityConstraint copyIdentificationAttributesAsAlternativeForeign(Derivation derivation) {

        ReferencialIntegrityConstraint constraint = new ReferencialIntegrityConstraint(this.name, derivation.name);

        for (String attribute : this.identificationAttributes) {

            String cleanAttribute = attribute.replace(DerivationFormater.MAIN_ATTRIBUTE, "");
            derivation.addCommonAttribute(DerivationFormater.ALTERNATIVE_ATTRIBUTE + DerivationFormater.FOREIGN_ATTRIBUTE + cleanAttribute);
            constraint.addReference(cleanAttribute, cleanAttribute);
        }

        return constraint;
    }

    ReferencialIntegrityConstraint copyIdentificationAttributesAsOptionalForeign(Derivation derivation) {

        ReferencialIntegrityConstraint constraint = new ReferencialIntegrityConstraint(this.name, derivation.name);

        for (String attribute : this.identificationAttributes) {
            derivation.addCommonAttribute(
                    DerivationFormater.FOREIGN_ATTRIBUTE
                    + DerivationFormater.OPTIONAL_ATTRIBUTE
                    + attribute
            );
            constraint.addReference(attribute, attribute);
        }

        return constraint;
    }

    ReferencialIntegrityConstraint copyIdentificationAttributesAsOptional(Derivation derivation) {

        ReferencialIntegrityConstraint constraint = new ReferencialIntegrityConstraint(this.name, derivation.name);

        for (String attribute : this.identificationAttributes) {
            String cleanAttribute = attribute.replace(DerivationFormater.MAIN_ATTRIBUTE, "");
            derivation.addCommonAttribute(DerivationFormater.OPTIONAL_ATTRIBUTE + cleanAttribute);
            constraint.addReference(attribute, attribute);
        }

        return constraint;
    }

    void moveAttributesTo(Derivation derivation) {

        for (String attribute : this.identificationAttributes) {
            derivation.addIdentificationAttribute(attribute);
        }

        for (String attribute : this.commonAttributes) {
            derivation.addCommonAttribute(attribute);
        }
    }

    boolean hasAttribute(String attribute) {
        return this.identificationAttributes.contains(attribute) || this.commonAttributes.contains(attribute);
    }

    boolean isEmpty() {
        return this.identificationAttributes.isEmpty() && this.commonAttributes.isEmpty();
    }

    String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(this.name).append("(");

        // Agregar los atributos de identificación
        for (String attribute : this.identificationAttributes) {
            out.append(DerivationFormater.format(attribute)).append(", ");
        }

        // Agregar los atributos comunes
        for (String attribute : this.commonAttributes) {
            out.append(DerivationFormater.format(attribute)).append(", ");
        }

        // Eliminar la última coma y espacio
        int startIndex = out.lastIndexOf(", ");
        if (startIndex != -1) {
            out.delete(startIndex, startIndex + 2); // Eliminar la coma y el espacio
        }

        out.append(")");

        // Eliminar cualquier espacio residual al final
        return out.toString().replaceAll("\\s+$", "");
    }
}
