package com.bdd.mer.derivation;

import com.bdd.mer.derivation.elements.*;

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
        element.clearAllDecorations();
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

    public boolean isEmpty() {
        return this.identificationElements.isEmpty() && this.commonElements.isEmpty();
    }

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

    public String formatToHTML() {

        String out = this.name + "(";

        out += this.identificationElements.formatToHTML();

        if (!this.identificationElements.isEmpty() && !this.commonElements.isEmpty()) {
            out += ", ";
        }

        out += this.commonElements.formatToHTML() + ")";

        return out;
    }

    public String getName() {
        return this.name;
    }

    public static Derivation unify(Derivation firstDerivation, Derivation secondDerivation) {

        if (!firstDerivation.name.equals(secondDerivation.name)) {
            throw new IllegalArgumentException("The derivation names must be the same.");
        }

        Derivation unification = new Derivation(firstDerivation.name);

        if (!firstDerivation.identificationElements.isEmpty()) {
            unification.addIdentificationElement(firstDerivation.identificationElements.getCopy());
        }

        if (!secondDerivation.identificationElements.isEmpty()) {
            unification.addIdentificationElement(secondDerivation.identificationElements.getCopy());
        }

        if (!firstDerivation.commonElements.isEmpty()) {
            unification.addCommonElement(firstDerivation.commonElements.getCopy());
        }

        if (!secondDerivation.commonElements.isEmpty()) {
            unification.addCommonElement(secondDerivation.commonElements.getCopy());
        }

        return unification;
    }

    public int getNumberOfElements() {
        return this.commonElements.getNumberOfElements() + this.identificationElements.getNumberOfElements();
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
