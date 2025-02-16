package com.iroumec.bdd.eerd.hierarchy.exclusivity;

import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.SingleElement;
import com.iroumec.bdd.eerd.entity.EntityWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Also known as "Exclusive".
 */
public final class Disjunct implements Exclusivity {

    private final Discriminant discriminant;
    private static final String SYMBOL = "d";
    private static final Disjunct INSTANCE = new Disjunct(null);

    public static Disjunct getInstance() { return INSTANCE; }

    private Disjunct(Discriminant discriminant) { this.discriminant = discriminant; }

    public Disjunct setDiscriminant(@NotNull Discriminant discriminant) {
        return new Disjunct(discriminant);
    }

    @Override
    public String getSymbol() { return SYMBOL; }

    @Override
    public @Nullable Discriminant getDiscriminant() {
        return this.discriminant;
    }

    @Override
    public List<Derivation> getDerivations(EntityWrapper hierarchyParent) {

        if (discriminant == null) {
            throw new IllegalStateException("Discriminant not set.");
        }

        Derivation parentDerivation = new Derivation(hierarchyParent.getIdentifier());
        parentDerivation.addCommonElement(new SingleElement(discriminant.getText()));

        return List.of(parentDerivation);
    }
}
