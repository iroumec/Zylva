package com.bdd.mer.derivation.elements.containers;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import org.jetbrains.annotations.Nullable;

public interface Holder {

    boolean mayNeedReplacement();
    boolean generatesConstraints();
    @Nullable Element abstractReplacement(Derivation derivation);
}
