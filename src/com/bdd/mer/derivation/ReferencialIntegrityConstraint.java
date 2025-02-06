package com.bdd.mer.derivation;

import com.bdd.mer.structures.Pair;

import java.util.List;

public class ReferencialIntegrityConstraint {

    private final String referencing;
    private final String referenced;
    private final List<Pair<String, String>> references;

    ReferencialIntegrityConstraint(String referencing, String referenced) {
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

        for (Pair<String, String> reference : this.references) {
            referencingAttributes.append(DerivationFormater.cleanAllFormats(reference.getFirst())).append(", ");
            referencedAttributes.append(DerivationFormater.cleanAllFormats(reference.getSecond())).append(", ");
        }

        deleteLast(", ", referencingAttributes);
        deleteLast(", ", referencedAttributes);

        return this.referencing +
                "[" + referencingAttributes + "]" +
                " << " + this.referenced +
                "[" + referencedAttributes + "]";
    }

    @SuppressWarnings("SameParameterValue")
    private void deleteLast(String textToBeDeleted, StringBuilder stringBuilder) {
        int startIndex = stringBuilder.lastIndexOf(textToBeDeleted);
        if (startIndex != -1) {
            stringBuilder.delete(startIndex, startIndex + 2);
        }
    }
}
