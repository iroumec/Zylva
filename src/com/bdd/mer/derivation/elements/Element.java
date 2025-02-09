package com.bdd.mer.derivation.elements;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {

    private final List<ElementDecorator> decorations;

    public Element() {
        this.decorations = new ArrayList<>();
    }

    public void addDecoration(ElementDecorator decorator) {
        this.decorations.add(decorator);
    }

    void removeDecoration(ElementDecorator decorator) {
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

    public abstract int getNumberOfElements();

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
