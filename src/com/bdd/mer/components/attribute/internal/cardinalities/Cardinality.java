package com.bdd.mer.components.attribute.internal.cardinalities;

import com.bdd.mer.components.attribute.external.Attribute;
import com.bdd.mer.components.attribute.external.DescAttrEERComp;
import com.bdd.mer.derivation.Derivation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public interface Cardinality {

    Cardinality getOpposite();

    boolean generatesDerivation();

    void draw(Graphics2D g2, int x, int y);

    List<Derivation> getDerivations(@NotNull DescAttrEERComp owner, @NotNull Attribute attribute);
}
