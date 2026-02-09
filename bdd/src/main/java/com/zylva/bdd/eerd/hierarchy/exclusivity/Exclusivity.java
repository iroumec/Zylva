package com.zylva.bdd.eerd.hierarchy.exclusivity;

import com.zylva.bdd.derivation.Derivation;
import com.zylva.bdd.eerd.entity.EntityWrapper;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public interface Exclusivity extends Serializable {

    String getSymbol();

    default @Nullable Discriminant getDiscriminant() { return null; }

    default List<Derivation> getDerivations(EntityWrapper hierarchyParent) { return new ArrayList<>(); }
}
