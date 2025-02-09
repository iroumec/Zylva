package com.bdd.mer.derivation.elements.container;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;

public interface Holder {

    boolean mayNeedReplacement();
    Element abstractReplacement(Derivation derivation);
}
