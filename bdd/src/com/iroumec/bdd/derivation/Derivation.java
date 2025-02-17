package com.iroumec.bdd.derivation;

import com.iroumec.bdd.derivation.elements.Element;
import com.iroumec.bdd.derivation.elements.ElementDecorator;
import com.iroumec.bdd.derivation.elements.ElementGroup;
import com.iroumec.bdd.derivation.elements.SingleElement;

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
        this.identificationElements.addDecoration(ElementDecorator.MAIN);
    }

    public void addIdentificationElement(Element element) {
        element.clearAllDecorations();
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

    public void removeElement(Element element) {

        // If the element to be removed is not found in the common elements...
        // Searches in the identification elements.
        if (!commonElements.removeElement(element)) {
            identificationElements.removeElement(element);
        }
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

    public boolean isEmpty() {
        return this.identificationElements.isEmpty() && this.commonElements.isEmpty();
    }

    public String formatToHTML() {

        String out = this.name + "(";

        out += this.identificationElements.formatToHTML();

        if (!this.identificationElements.isEmpty() && !this.commonElements.isEmpty()) {
            out += ", ";
        }

        out += this.commonElements.formatToHTML() + ")";

        return out;
    }

    public String getName() { return this.name; }

    public static Derivation unify(Derivation firstDerivation, Derivation secondDerivation) {

        if (!firstDerivation.name.equals(secondDerivation.name)) {
            throw new IllegalArgumentException("The derivation names must be the same.");
        }

        Derivation unification = new Derivation(firstDerivation.name);

        ElementGroup identificationElements = new ElementGroup();

        if (!firstDerivation.identificationElements.isEmpty()) {
            identificationElements.addElement(firstDerivation.identificationElements.getCopy());
        }

        if (!secondDerivation.identificationElements.isEmpty()) {
            identificationElements.addElement(secondDerivation.identificationElements.getCopy());
        }

        ElementGroup commonElements = new ElementGroup();

        if (!firstDerivation.commonElements.isEmpty()) {
            commonElements.addElement(firstDerivation.commonElements.getCopy());
        }

        if (!secondDerivation.commonElements.isEmpty()) {
            commonElements.addElement(secondDerivation.commonElements.getCopy());
        }

        List<SingleElement> elementsToRemove = new ArrayList<>();

        for (SingleElement commonElement : commonElements.getPartitions()) {
            for (SingleElement identificationElement : identificationElements.getPartitions()) {
                if (commonElement.equals(identificationElement)
                        && !commonElement.hasDecoration(ElementDecorator.DUPLICATED)) {
                    elementsToRemove.add(commonElement);
                }
            }
        }

        for (SingleElement element : elementsToRemove) {
            commonElements.removeElement(element);
        }

        if (!commonElements.isEmpty()) {
            unification.addCommonElement(commonElements);
        }

        if (!identificationElements.isEmpty()) {
            unification.addIdentificationElement(identificationElements);
        }

        return unification;
    }

    public int getNumberOfIdentificationElements() {
        return this.identificationElements.getNumberOfElements();
    }

    @SuppressWarnings("unused")
    public int getNumberOfCommonElements() {
        return this.commonElements.getNumberOfElements();
    }

    @SuppressWarnings("unused")
    public int getNumberOfElements() {
        return this.getNumberOfCommonElements() + this.getNumberOfIdentificationElements();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public String toString() {

        String out = this.name + "(";

        out += this.identificationElements.toString();

        if (!this.identificationElements.isEmpty() && !this.commonElements.isEmpty()) {
            out += ",";
        }

        out += this.commonElements.toString() + ")";

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Derivation that = (Derivation) o;
        return Objects.equals(name, that.name);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public int hashCode() { return Objects.hash(name); }
}
