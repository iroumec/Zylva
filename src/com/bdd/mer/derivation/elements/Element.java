package com.bdd.mer.derivation.elements;

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

    void removeDecoration(ElementDecorator decorator) {
        this.decorations.remove(decorator);
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

    public static void clearAllDecorationsExcepting(Element element, ElementDecorator... except) {

        Set<ElementDecorator> exceptionList = new HashSet<>(Set.of(except));
        Set<ElementDecorator> decorationsToRemove = new HashSet<>(element.decorations);

        decorationsToRemove.removeAll(exceptionList);

        for (ElementDecorator decorator : decorationsToRemove) {
            element.removeDecoration(decorator);
        }
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
