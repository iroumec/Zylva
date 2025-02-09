package com.bdd.mer.derivation.elements.container;

import com.bdd.mer.derivation.Constraint;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.container.replacers.types.Type;

import java.util.List;

/**
 * Replace the derivation reference for the derivation according to the type.
 * <p></p>
 * If the type is a {@code Common}, there will be not RIR.
 */
public abstract class Replacer implements Holder {

    private final Type type;

    public Replacer(Type type) {
        this.type = type;
    }

    @Override
    public boolean mayNeedReplacement() {
        return true;
    }

    @Override
    public Element abstractReplacement(Derivation derivation) {

        return type.getAbstractionElement(derivation);
    }

    public abstract List<Constraint> getGeneratedConstraints();
}
