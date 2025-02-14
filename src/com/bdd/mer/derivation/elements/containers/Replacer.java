package com.bdd.mer.derivation.elements.containers;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.ElementDecorator;
import com.bdd.mer.derivation.elements.containers.sources.Identifier;
import com.bdd.mer.derivation.elements.containers.sources.Source;

import java.util.HashSet;
import java.util.Set;

/**
 * Replace the derivation reference for the derivation according to the source.
 * <p></p>
 * If the source is a {@code CommonRol}, there will be not RIR.
 */
public class Replacer implements Holder {

    private final Source source;
    private final Set<ElementDecorator> modifiers;

    public Replacer(Source source) {
        this(source, (new HashSet<ElementDecorator>()).toArray(new ElementDecorator[0]));
    }

    public Replacer(ElementDecorator... modifiers) {
        this(new Identifier(), modifiers);
    }

    public Replacer(Source source, ElementDecorator... modifiers) {
        this.source = source;
        this.modifiers = new HashSet<>(Set.of(modifiers));
    }

    @Override
    public boolean mayNeedReplacement() {
        return true;
    }

    @Override
    public boolean generatesConstraints() {
        return this.modifiers.contains(ElementDecorator.FOREIGN);
    }

    @Override
    public Element abstractReplacement(Derivation derivation) {

        Element out = source.getAbstractionElement(derivation);

        if (out != null) {

            for (ElementDecorator decorator : modifiers) {
                out.addDecoration(decorator);
            }
        }

        return out;
    }
}
