package com.bdd.mer.derivation;

import com.bdd.mer.structures.Pair;

import java.util.List;
import java.util.Objects;

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

    void transferConstraintsTo(ReferencialIntegrityConstraint constraint) {
        for (Pair<String, String> reference : this.references) {
            constraint.addReference(reference.getFirst(), reference.getSecond());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReferencialIntegrityConstraint that = (ReferencialIntegrityConstraint) o;
        return Objects.equals(referencing, that.referencing) && Objects.equals(referenced, that.referenced);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referencing, referenced);
    }
}
