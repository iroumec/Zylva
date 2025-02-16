package com.iroumec.bdd.eerd.attribute.cardinalities;

import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.eerd.attribute.Attribute;
import com.iroumec.bdd.eerd.attribute.DescriptiveAttributable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

public interface Cardinality extends Serializable {

    Cardinality getOpposite();

    boolean generatesDerivation();

    void draw(Graphics2D g2, int x, int y);

    List<Derivation> getDerivations(@NotNull DescriptiveAttributable owner, @NotNull Attribute attribute);
}
