package com.bdd.mer.derivation.elements.container;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;

public class Final implements Holder {

    @Override
    public boolean mayNeedReplacement() {
        return false;
    }

    @Override
    public Element abstractReplacement(Derivation derivation) {
        return null;
    }
}
