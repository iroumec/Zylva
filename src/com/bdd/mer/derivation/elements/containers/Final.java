package com.bdd.mer.derivation.elements.containers;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;

public class Final implements Holder {

    @Override
    public boolean needsRename() {
        return false;
    }

    @Override
    public boolean mayNeedReplacement() {
        return false;
    }

    @Override
    public boolean generatesConstraints() {
        return false;
    }

    @Override
    public Element abstractReplacement(Derivation derivation) {
        return null;
    }
}
