package com.bdd.mer.derivation.elements.containers.sources;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.ElementGroup;

public class Common implements Source {

    @Override
    public Element getAbstractionElement(Derivation derivation) {

        ElementGroup commonElements = derivation.getCommonElements();

        if (commonElements.isEmpty()) {
            return null;
        }

        return commonElements.getCopy();
    }
}
