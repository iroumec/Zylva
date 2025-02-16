package com.iroumec.bdd.derivation.elements.containers.sources;

import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.Element;
import com.iroumec.bdd.derivation.elements.ElementGroup;

public final class Common implements Source {

    private final static Common INSTANCE = new Common();

    public Common() {}

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
