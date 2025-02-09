package com.bdd.mer.derivation;

import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.ElementFormater;
import com.bdd.mer.derivation.elements.ElementGroup;
import com.bdd.mer.derivation.elements.SingleElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Derivation {

    private final String name;
    private final ElementGroup commonElements;
    private final ElementGroup identificationElements;

    public Derivation(String name) {
        this.name = name;
        this.commonElements = new ElementGroup();
        this.identificationElements = new ElementGroup();
        this.identificationElements.addDecoration(AttributeDecorator.MAIN_ATTRIBUTE);
    }

    public void addIdentificationElement(Element element) {
        ElementFormater.cleanAllFormatsExcept(element, AttributeDecorator.FOREIGN_ATTRIBUTE);
        this.identificationElements.addElement(element);
    }

    public void addCommonElement(Element element) {
        this.commonElements.addElement(element);
    }

    public List<SingleElement> getReplacementNeeded() {

        List<SingleElement> out = new ArrayList<>(this.commonElements.getReplacementsNeeded());
        out.addAll(this.identificationElements.getReplacementsNeeded());

        return out;
    }

    public void replace(SingleElement element, Element replacement) {

        this.commonElements.replace(element, replacement);
        this.identificationElements.replace(element, replacement);
    }

    public ElementGroup getCommonElements() {
        return (ElementGroup) this.commonElements.getCopy();
    }

    public ElementGroup getIdentificationElements() {
        return (ElementGroup) this.identificationElements.getCopy();
    }

//    public void addAttribute(String attribute) {
//        if (attribute.startsWith(ElementFormater.MAIN_ATTRIBUTE)) {
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
//            String cleanIdentificationAttribute = ElementFormater.cleanAllFormats(identificationAttribute);
//            if (cleanIdentificationAttribute.equals(ElementFormater.cleanAllFormats(attribute))) {
//                attributesToRemove.add(identificationAttribute);
//            }
//        }
//
//        this.identificationAttributes.removeAll(attributesToRemove);
//        attributesToRemove.clear();
//
//        for (String commonAttribute : this.commonAttributes) {
//            String cleanCommonAttribute = ElementFormater.cleanAllFormats(commonAttribute);
//            if (cleanCommonAttribute.equals(ElementFormater.cleanAllFormats(attribute))) {
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
//            this.identificationAttributes.add(ElementFormater.DUPLICATED_ATTRIBUTE + attribute);
//        } else {
//            this.identificationAttributes.add(attribute);
//        }
//    }
//
//    private void addCommonAttribute(String attribute) {
//        if (this.identificationAttributes.contains(attribute) || this.commonAttributes.contains(attribute)) {
//            this.commonAttributes.add(ElementFormater.DUPLICATED_ATTRIBUTE + attribute);
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
//            String cleanAttribute = attribute.replace(ElementFormater.MAIN_ATTRIBUTE, "");
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
//            String cleanAttribute = attribute.replace(ElementFormater.MAIN_ATTRIBUTE, "");
//            derivation.addCommonAttribute(ElementFormater.ALTERNATIVE_ATTRIBUTE + ElementFormater.FOREIGN_ATTRIBUTE + cleanAttribute);
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
//            String cleanCommonAttribute = ElementFormater.cleanAllFormats(commonAttribute);
//            if (cleanCommonAttribute.equals(ElementFormater.cleanAllFormats(attribute))) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    boolean hasIdentificationAttribute(String attribute) {
//        for (String identificationAttribute : this.identificationAttributes) {
//            String cleanIdentificationAttribute = ElementFormater.cleanAllFormats(identificationAttribute);
//            if (cleanIdentificationAttribute.equals(ElementFormater.cleanAllFormats(attribute))) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
    boolean isEmpty() {
        return this.identificationElements.isEmpty() && this.commonElements.isEmpty();
    }


    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(this.name).append("(");

        out.append(this.identificationElements.toString());

        out.append(this.commonElements.toString());

        // Eliminar la última coma y espacio
        deleteLast(", ", out);

        out.append(")");

        out.append(", ");

        // Eliminar cualquier espacio residual al final
        return out.toString();
    }

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

    public static Derivation unify(Derivation firstDerivation, Derivation secondDerivation) {

        if (!firstDerivation.name.equals(secondDerivation.name)) {
            throw new IllegalArgumentException("The derivation names must be the same.");
        }

        Derivation unification = new Derivation(firstDerivation.name);

        unification.addIdentificationElement(firstDerivation.identificationElements.getCopy());
        unification.addIdentificationElement(secondDerivation.identificationElements.getCopy());

        unification.addCommonElement(firstDerivation.commonElements.getCopy());
        unification.addCommonElement(secondDerivation.commonElements.getCopy());

        return unification;
    }

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
