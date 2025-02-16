package com.iroumec.bdd.derivation.elements.containers;

import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.Element;
import org.jetbrains.annotations.Nullable;

public interface Holder {

    boolean mayNeedReplacement();
    boolean generatesConstraints();
    @Nullable Element abstractReplacement(Derivation derivation);
}
