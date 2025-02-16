package com.iroumec.bdd.derivation.elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Element {

    private final Set<ElementDecorator> decorations;

    public Element() {
        this.decorations = new HashSet<>();
    }

    public void addDecoration(ElementDecorator decorator) {
        this.decorations.add(decorator);
    }

    /**
     * There will never be a replacement of a {@code ElementGroup} for an {@code Element}.
     */
    public abstract void replace(SingleElement element, Element replacement);

    public abstract List<SingleElement> getReplacementsNeeded();

    public abstract Element getCopy();

    final void copyDecoratorsTo(Element element) {

        for (ElementDecorator decorator : this.decorations) {
            element.addDecoration(decorator);
        }
    }

    public abstract String formatToHTML();

    final String applyDecorators(String text) {

        String out = text;

        for (ElementDecorator decorator : this.decorations) {
            out = decorator.formatToHTML(out);
        }

        return out;

    }

    public abstract void clearAllDecorations();

    final void emptyDecorations() {
        this.decorations.clear();
    }

    public abstract int getNumberOfElements();

    public abstract List<SingleElement> getPartitions();

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public String toString() {

        StringBuilder out = new StringBuilder();

        for (ElementDecorator decorator : this.decorations) {
            out.append(decorator.toString());
        }

        return out.toString();
    }
}
