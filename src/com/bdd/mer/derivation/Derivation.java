package com.bdd.mer.derivation;

import com.bdd.mer.derivation.elements.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Derivation {

    enum AttributeType {
        IDENTIFICATION,
        COMMON
    }

    private final String name;
    private final List<Element> commonElements;
    private final List<Element> identificationElements;

    public Derivation(String name) {
        this.name = name;
        this.commonElements = new ArrayList<>();
        this.identificationElements = new ArrayList<>();
    }

    public void addIdentificationElement(Element element) {
        this.identificationElements.add(element);
    }

    public void addCommonElement(Element element) {
        this.commonElements.add(element);
    }

    public void addElement(Element element) {
        this.commonElements.add(element);
    }

//    public void addAttribute(String attribute) {
//        if (attribute.startsWith(DerivationFormater.MAIN_ATTRIBUTE)) {
//            addIdentificationAttribute(attribute);
//        } else {
//            addCommonAttribute(attribute);
//        }
//    }

//    void removeAttribute(String attribute) {
//
//        List<String> attributesToRemove = new ArrayList<>();
//
//        for (String identificationAttribute : this.identificationAttributes) {
//            String cleanIdentificationAttribute = DerivationFormater.cleanAllFormats(identificationAttribute);
//            if (cleanIdentificationAttribute.equals(DerivationFormater.cleanAllFormats(attribute))) {
//                attributesToRemove.add(identificationAttribute);
//            }
//        }
//
//        this.identificationAttributes.removeAll(attributesToRemove);
//        attributesToRemove.clear();
//
//        for (String commonAttribute : this.commonAttributes) {
//            String cleanCommonAttribute = DerivationFormater.cleanAllFormats(commonAttribute);
//            if (cleanCommonAttribute.equals(DerivationFormater.cleanAllFormats(attribute))) {
//                attributesToRemove.add(commonAttribute);
//            }
//        }
//
//        this.commonAttributes.removeAll(attributesToRemove);
//    }
//
//    private void addIdentificationAttribute(String attribute) {
//
//        // The legibility of this could be improved.
//        if (this.identificationAttributes.contains(attribute)) {
//            this.identificationAttributes.add(DerivationFormater.DUPLICATED_ATTRIBUTE + attribute);
//        } else {
//            this.identificationAttributes.add(attribute);
//        }
//    }
//
//    private void addCommonAttribute(String attribute) {
//        if (this.identificationAttributes.contains(attribute) || this.commonAttributes.contains(attribute)) {
//            this.commonAttributes.add(DerivationFormater.DUPLICATED_ATTRIBUTE + attribute);
//        } else {
//            this.commonAttributes.add(attribute);
//        }
//    }
//
//    Constraint copyIdentificationAttributesAs(Derivation derivation) {
//
//        return copyIdentificationAttributesAs(derivation, "");
//    }
//
//    Constraint copyIdentificationAttributesAs(Derivation derivation, String text) {
//
//        return copyIdentificationAttributesAs(derivation, text, AttributeType.IDENTIFICATION);
//    }
//
//    Constraint copyIdentificationAttributesAs(Derivation derivation, String text, AttributeType type) {
//
//        Constraint constraint = new Constraint(derivation.name, this.name);
//
//        for (String attribute : this.identificationAttributes) {
//
//            String cleanAttribute = attribute.replace(DerivationFormater.MAIN_ATTRIBUTE, "");
//
//            if (type == AttributeType.COMMON) {
//                derivation.addCommonAttribute(text + cleanAttribute);
//            } else {
//                derivation.addIdentificationAttribute(text + cleanAttribute);
//            }
//
//            constraint.addReference(cleanAttribute, cleanAttribute);
//        }
//
//        return constraint;
//    }
//
//    Constraint copyIdentificationAttributesAsAlternativeForeign(Derivation derivation) {
//
//        Constraint constraint = new Constraint(this.name, derivation.name);
//
//        for (String attribute : this.identificationAttributes) {
//
//            String cleanAttribute = attribute.replace(DerivationFormater.MAIN_ATTRIBUTE, "");
//            derivation.addCommonAttribute(DerivationFormater.ALTERNATIVE_ATTRIBUTE + DerivationFormater.FOREIGN_ATTRIBUTE + cleanAttribute);
//            constraint.addReference(cleanAttribute, cleanAttribute);
//        }
//
//        return constraint;
//    }
//
//    public void moveAttributesTo(Derivation derivation) {
//
//        for (String attribute : this.identificationAttributes) {
//            derivation.addIdentificationAttribute(attribute);
//        }
//
//        for (String attribute : this.commonAttributes) {
//            derivation.addCommonAttribute(attribute);
//        }
//    }
//
//    void moveCommonAttributesTo(Derivation derivation) {
//        for (String attribute : this.commonAttributes) {
//            derivation.addCommonAttribute(attribute);
//        }
//    }
//
//    @SuppressWarnings("unused")
//    boolean hasAttribute(String attribute) {
//        return this.identificationAttributes.contains(attribute) || this.commonAttributes.contains(attribute);
//    }
//
//    boolean hasCommonAttribute(String attribute) {
//        for (String commonAttribute : this.commonAttributes) {
//            String cleanCommonAttribute = DerivationFormater.cleanAllFormats(commonAttribute);
//            if (cleanCommonAttribute.equals(DerivationFormater.cleanAllFormats(attribute))) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    boolean hasIdentificationAttribute(String attribute) {
//        for (String identificationAttribute : this.identificationAttributes) {
//            String cleanIdentificationAttribute = DerivationFormater.cleanAllFormats(identificationAttribute);
//            if (cleanIdentificationAttribute.equals(DerivationFormater.cleanAllFormats(attribute))) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    boolean isEmpty() {
//        return this.identificationAttributes.isEmpty() && this.commonAttributes.isEmpty();
//    }
//
//
//    @Override
//    public String toString() {
//        StringBuilder out = new StringBuilder(this.name).append("(");
//
//        StringBuilder identificationAttributes = new StringBuilder();
//
//        // Agregar los atributos de identificación
//        for (Element attribute : this.identificationElements) {
//            // This "cleanAllFormats" could be moved to the adding to identification attributes.
//            identificationAttributes.append(attribute.replace(DerivationFormater.MAIN_ATTRIBUTE, "")).append(", ");
//        }
//
//        deleteLast(", ", identificationAttributes);
//
//        out.append(DerivationFormater
//                .format(DerivationFormater.MAIN_ATTRIBUTE + identificationAttributes))
//            .append(", ")
//        ;
//
//        // Agregar los atributos comunes
//        for (String attribute : this.commonAttributes) {
//            out.append(DerivationFormater.format(attribute)).append(", ");
//        }
//
//        // Eliminar la última coma y espacio
//        deleteLast(", ", out);
//
//        out.append(")");
//
//        // Eliminar cualquier espacio residual al final
//        return out.toString().replaceAll("\\s+$", "");
//    }

    @SuppressWarnings("SameParameterValue")
    private void deleteLast(String textToBeDeleted, StringBuilder stringBuilder) {
        int startIndex = stringBuilder.lastIndexOf(textToBeDeleted);
        if (startIndex != -1) {
            stringBuilder.delete(startIndex, startIndex + 2); // Eliminar la coma y el espacio
        }
    }

    public String getName() {
        return this.name;
    }

//    public Derivation unify(Derivation firstDerivation, Derivation secondDerivation) {
//
//        secondDerivation.moveAttributesTo(firstDerivation);
//
//        return firstDerivation;
//    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Derivation that = (Derivation) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
