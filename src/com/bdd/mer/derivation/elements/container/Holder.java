package com.bdd.mer.derivation.elements.container;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import org.jetbrains.annotations.Nullable;

public interface Holder {

    boolean needsRename();
    boolean mayNeedReplacement();
    boolean generatesConstraints();
    @Nullable Element abstractReplacement(Derivation derivation);
}
