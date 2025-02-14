package com.bdd.mer.derivation.elements.containers.sources;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.ElementGroup;

public final class Common implements Source {

    private final static Common INSTANCE = new Common();

    private Common() {}

    public static Common getInstance() { return INSTANCE; }

    @Override
    public Element getAbstractionElement(Derivation derivation) {

        ElementGroup commonElements = derivation.getCommonElements();
        commonElements.clearAllDecorations();

        if (commonElements.isEmpty()) {
            return null;
        }

        return commonElements.getCopy();
    }
}
