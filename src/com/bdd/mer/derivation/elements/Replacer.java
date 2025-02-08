package com.bdd.mer.derivation.elements;

import com.bdd.mer.derivation.Constraint;
import com.bdd.mer.derivation.Derivation;

import java.util.List;

/**
 * Replace the derivation reference for the derivation according to the type.
 * <p></p>
 * If the type is a {@code Common}, there will be not RIR.
 */
public abstract class Replacer extends Element {

    private final Type type;

    public Replacer(String name, Type type) {
        super(name);
        this.type = type;
    }

    public abstract List<Constraint> replace(List<Derivation> derivationsToCheck);
}
