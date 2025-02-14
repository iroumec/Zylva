package com.bdd.mer.derivation.elements.containers.sources;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.ElementGroup;

public final class Identifier implements Source {

    private final static Identifier INSTANCE = new Identifier();

    private Identifier() {}

    public static Identifier getInstance() { return INSTANCE; }

    @Override
    public Element getAbstractionElement(Derivation derivation) {

        ElementGroup identificationElements = derivation.getIdentificationElements();
        identificationElements.clearAllDecorations();

        if (identificationElements.isEmpty()) {
            return null;
        }

        return identificationElements.getCopy();
    }
}
