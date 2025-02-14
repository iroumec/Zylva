package com.iroumec.derivation;

import com.iroumec.derivation.elements.Element;
import com.iroumec.derivation.elements.ElementDecorator;
import com.iroumec.structures.Pair;

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

            referencingAttributes.append(reference.first().toString());
            referencedAttributes.append(reference.second().toString());
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

            referencingAttributes.append(reference.first().formatToHTML());
            referencedAttributes.append(reference.second().formatToHTML());
        }

        return this.referencing +
                "[" + referencingAttributes + "]" +
                " << " + this.referenced +
                "[" + referencedAttributes + "]";
    }

    void transferConstraintsTo(Constraint constraint) {
        for (Pair<Element, Element> reference : this.references) {
            constraint.addReference(reference.first(), reference.second());
        }
    }

    void setAsDuplicated() {
        for (Pair<Element, Element> reference : this.references) {
            reference.first().addDecoration(ElementDecorator.DUPLICATED);
        }
    }

    boolean hasSameReferencesAs(Constraint constraint) {
        for (Pair<Element, Element> reference : this.references) {
            if (!constraint.hasReference(reference)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasReference(Pair<Element, Element> reference) {

        return this.references.contains(reference);
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
