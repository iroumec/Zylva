package com.bdd.mer.derivation.elements.container;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.container.replacers.types.Source;

/**
 * Replace the derivation reference for the derivation according to the source.
 * <p></p>
 * If the source is a {@code Common}, there will be not RIR.
 */
public abstract class Replacer implements Holder {

    private final Source source;

    public Replacer(Source source) {
        this.source = source;
    }

    @Override
    public boolean mayNeedReplacement() {
        return true;
    }

    @Override
    public Element abstractReplacement(Derivation derivation) {

        return source.getAbstractionElement(derivation);
    }
}
