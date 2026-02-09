package com.zylva.bdd.eerd.attribute.cardinalities;

import com.zylva.bdd.derivation.Derivation;
import com.zylva.bdd.eerd.attribute.Attribute;
import com.zylva.bdd.eerd.attribute.DescriptiveAttributable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

public interface Cardinality extends Serializable {

    Cardinality getOpposite();

    boolean generatesDerivation();

    default void draw(Graphics2D g2, int x, int y) {}

    List<Derivation> getDerivations(@NotNull DescriptiveAttributable owner, @NotNull Attribute attribute);
}
