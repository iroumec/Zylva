package com.iroumec.eerd.attribute.cardinalities;

import com.iroumec.derivation.Derivation;
import com.iroumec.eerd.attribute.Attribute;
import com.iroumec.eerd.attribute.DescAttrEERComp;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public interface Cardinality {

    Cardinality getOpposite();

    boolean generatesDerivation();

    void draw(Graphics2D g2, int x, int y);

    List<Derivation> getDerivations(@NotNull DescAttrEERComp owner, @NotNull Attribute attribute);
}
