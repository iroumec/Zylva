package com.bdd.mer.derivation.elements.container.sources;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.ElementGroup;

public class Identifier implements Source {

    @Override
    public Element getAbstractionElement(Derivation derivation) {

        ElementGroup identificationElements = derivation.getIdentificationElements();
        Element.clearAllDecorations(identificationElements);

        if (identificationElements.isEmpty()) {
            return null;
        }

        return identificationElements.getCopy();
    }
}
