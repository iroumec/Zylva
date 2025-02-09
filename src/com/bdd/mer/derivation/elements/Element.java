package com.bdd.mer.derivation.elements;

import com.bdd.mer.derivation.AttributeDecorator;
import com.bdd.mer.derivation.elements.singleElements.Replacer;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {

    private final List<AttributeDecorator> decorations;

    public Element() {
        this.decorations = new ArrayList<>();
    }

    public void addDecoration(AttributeDecorator decorator) {
        this.decorations.add(decorator);
    }

    public abstract List<Replacer> getReplacementsNeeded();

    public Element getCopy() {

        Element copy = this.getCopy();

        for (AttributeDecorator decorator : this.decorations) {
            copy.addDecoration(decorator);
        }

        return copy;
    }

    @Override
    public String toString() {

        StringBuilder out = new StringBuilder();

        for (AttributeDecorator decorator : this.decorations) {
            out.append(decorator.toString());
        }

        return out.toString();
    }
}
