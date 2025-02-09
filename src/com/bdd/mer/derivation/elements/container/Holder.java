package com.bdd.mer.derivation.elements.container;

import com.bdd.mer.derivation.Constraint;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Holder {

    boolean mayNeedReplacement();
    @Nullable Element abstractReplacement(Derivation derivation);
    List<Constraint> getGeneratedConstraints();
}
