package com.bdd.mer.derivation;

import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.structures.Pair;

import java.util.List;
import java.util.Objects;

public class Constraint {

    private final String referencing;
    private final String referenced;
    private final List<Pair<Element, Element>> references;

    Constraint(String referencing, String referenced) {
        this.referencing = referencing;
        this.referenced = referenced;
        this.references = new java.util.ArrayList<>();
    }

    void addReference(Element referencing, Element referenced) {
        this.references.add(new Pair<>(referencing, referenced));
    }

    @Override
    public String toString() {

        StringBuilder referencingAttributes = new StringBuilder();
        StringBuilder referencedAttributes = new StringBuilder();

        boolean addComma = false;

        for (Pair<Element, Element> reference : this.references) {

            if (addComma) {
                referencingAttributes.append(", ");
                referencedAttributes.append(", ");
            } else {
                addComma = true;
            }

            referencingAttributes.append(reference.getFirst().toString());
            referencedAttributes.append(reference.getSecond().toString());
        }

        return this.referencing +
                "[" + referencingAttributes + "]" +
                " << " + this.referenced +
                "[" + referencedAttributes + "]";
    }

    public String formatToHTML() {

        StringBuilder referencingAttributes = new StringBuilder();
        StringBuilder referencedAttributes = new StringBuilder();

        boolean addComma = false;

        for (Pair<Element, Element> reference : this.references) {

            if (addComma) {
                referencingAttributes.append(", ");
                referencedAttributes.append(", ");
            } else {
                addComma = true;
            }

            referencingAttributes.append(reference.getFirst().formatToHTML());
            referencedAttributes.append(reference.getSecond().formatToHTML());
        }

        return this.referencing +
                "[" + referencingAttributes + "]" +
                " << " + this.referenced +
                "[" + referencedAttributes + "]";
    }

    void transferConstraintsTo(Constraint constraint) {
        for (Pair<Element, Element> reference : this.references) {
            constraint.addReference(reference.getFirst(), reference.getSecond());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Constraint that = (Constraint) o;
        return Objects.equals(referencing, that.referencing) && Objects.equals(referenced, that.referenced);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referencing, referenced);
    }
}
