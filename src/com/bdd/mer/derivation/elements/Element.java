package com.bdd.mer.derivation.elements;

import com.bdd.mer.derivation.AttributeDecorator;

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

    void removeDecoration(AttributeDecorator decorator) {
        this.decorations.remove(decorator);
    }

    /**
     * There will never be a replacement of a {@code ElementGroup} for an {@code Element}.
     *
     * @param element
     * @param replacement
     */
    public abstract void replace(SingleElement element, Element replacement);

    public abstract List<SingleElement> getReplacementsNeeded();

    public Element getCopy() {

        Element copy = this.getCopy();

        for (AttributeDecorator decorator : this.decorations) {
            copy.addDecoration(decorator);
        }

        return copy;
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public String toString() {

        StringBuilder out = new StringBuilder();

        for (AttributeDecorator decorator : this.decorations) {
            out.append(decorator.toString());
        }

        return out.toString();
    }
}
