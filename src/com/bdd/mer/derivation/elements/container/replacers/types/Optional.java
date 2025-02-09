package com.bdd.mer.derivation.elements.container.replacers.types;

import com.bdd.mer.derivation.AttributeDecorator;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;

public class Optional extends Common {

    @Override
    public Element getAbstractionElement(Derivation derivation) {

        Element out = super.getAbstractionElement(derivation);

        if (out != null) {
            out.addDecoration(AttributeDecorator.OPTIONAL_ATTRIBUTE);
        }

        return out;
    }
}
