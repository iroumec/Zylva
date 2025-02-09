package com.bdd.mer.derivation.elements.singleElements;

import com.bdd.mer.derivation.Constraint;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.SingleElement;
import com.bdd.mer.derivation.elements.singleElements.replacers.types.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Replace the derivation reference for the derivation according to the type.
 * <p></p>
 * If the type is a {@code Common}, there will be not RIR.
 */
public abstract class Replacer extends SingleElement {

    private final Type type;
    private List<Constraint> constraints;

    public Replacer(String name, Type type) {
        super(name);
        this.type = type;
        this.constraints = new ArrayList<>();
    }

    @Override
    public List<Replacer> getReplacementsNeeded() {

        List<Replacer> out = new ArrayList<>();

        out.add(this);

        return out;
    }

    public List<Element> getElementsToReplace(Derivation derivation) {

        return this.type.getElements(derivation);
    }
}
