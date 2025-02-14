package com.iroumec.derivation.elements.containers;

import com.iroumec.derivation.Derivation;
import com.iroumec.derivation.elements.Element;

public final class Final implements Holder {

    private final static Final INSTANCE = new Final();

    private Final() {}

    public static Final getInstance() { return INSTANCE; }

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
