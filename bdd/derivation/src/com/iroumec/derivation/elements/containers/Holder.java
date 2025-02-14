package com.iroumec.derivation.elements.containers;

import com.iroumec.derivation.Derivation;
import com.iroumec.derivation.elements.Element;
import org.jetbrains.annotations.Nullable;

public interface Holder {

    boolean mayNeedReplacement();
    boolean generatesConstraints();
    @Nullable Element abstractReplacement(Derivation derivation);
}
