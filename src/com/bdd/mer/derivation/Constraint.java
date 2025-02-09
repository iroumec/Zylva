package com.bdd.mer.derivation;

import com.bdd.mer.structures.Pair;

import java.util.List;
import java.util.Objects;

public class Constraint {

    private final String referencing;
    private final String referenced;
    private final List<Pair<String, String>> references;

    Constraint(String referencing, String referenced) {
        this.referencing = referencing;
        this.referenced = referenced;
        this.references = new java.util.ArrayList<>();
    }

    void addReference(String referencingAttribute, String referencedAttribute) {
        this.references.add(new Pair<>(referencingAttribute, referencedAttribute));
    }

    @Override
    public String toString() {

        StringBuilder referencingAttributes = new StringBuilder();
        StringBuilder referencedAttributes = new StringBuilder();

        boolean addComma = false;

        for (Pair<String, String> reference : this.references) {

            if (addComma) {
                referencingAttributes.append(", ");
                referencedAttributes.append(", ");
            } else {
                addComma = true;
            }

            referencingAttributes.append(reference.getFirst());
            referencedAttributes.append(reference.getSecond());
        }

        return this.referencing +
                "[" + referencingAttributes + "]" +
                " << " + this.referenced +
                "[" + referencedAttributes + "]";
    }

    void transferConstraintsTo(Constraint constraint) {
        for (Pair<String, String> reference : this.references) {
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
