package com.zylva.bdd.derivation.elements.containers;

import com.zylva.bdd.derivation.Derivation;
import com.zylva.bdd.derivation.elements.Element;
import org.jetbrains.annotations.Nullable;

public interface Holder {

    boolean mayNeedReplacement();
    boolean generatesConstraints();
    @Nullable Element abstractReplacement(Derivation derivation);
}
