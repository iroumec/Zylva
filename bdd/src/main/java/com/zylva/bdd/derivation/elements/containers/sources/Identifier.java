package com.zylva.bdd.derivation.elements.containers.sources;

import com.zylva.bdd.derivation.Derivation;
import com.zylva.bdd.derivation.elements.Element;
import com.zylva.bdd.derivation.elements.ElementGroup;

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
