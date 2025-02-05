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

    boolean hasReferences() {
        return !this.references.isEmpty();
    }

    String popReference() {

        Pair<String, String> reference = this.references.removeFirst();

        return this.referenced +
                "[" + DerivationFormater.cleanAllFormats(reference.getFirst()) + "]" +
                " << " + this.referencing +
                "[" + DerivationFormater.cleanAllFormats(reference.getSecond()) + "]";
    }
}
