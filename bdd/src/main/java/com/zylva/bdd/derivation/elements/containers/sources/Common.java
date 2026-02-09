package com.zylva.bdd.derivation.elements.containers.sources;

import com.zylva.bdd.derivation.Derivation;
import com.zylva.bdd.derivation.elements.Element;
import com.zylva.bdd.derivation.elements.ElementGroup;

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
