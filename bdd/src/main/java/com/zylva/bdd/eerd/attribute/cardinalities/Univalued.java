package com.zylva.bdd.eerd.attribute.cardinalities;

import com.zylva.bdd.eerd.attribute.Attribute;
import com.zylva.bdd.eerd.attribute.DescriptiveAttributable;
import com.zylva.bdd.derivation.Derivation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Univalued implements Cardinality {

    private final static Univalued INSTANCE = new Univalued();

    private Univalued() {}

    public static Univalued getInstance() {
        return INSTANCE;
    }

    @Override
    public Cardinality getOpposite() {
        return Multivalued.getInstance();
    }

    @Override
    public boolean generatesDerivation() {
        return false;
    }

    @Override
    public List<Derivation> getDerivations(@NotNull DescriptiveAttributable owner, @NotNull Attribute attribute) {
        return new ArrayList<>();
    }
}
