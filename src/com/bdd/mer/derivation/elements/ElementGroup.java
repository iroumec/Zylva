package com.bdd.mer.derivation.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ElementGroup extends Element {

    private final List<Element> elements;

    public ElementGroup() {
        this.elements = new ArrayList<>();
    }

    public boolean isEmpty() { return this.elements.isEmpty(); }

    @Override
    public void replace(SingleElement element, Element replacement) {
        for (Element e : elements) {
            if (e.equals(element)) {
                elements.set(elements.indexOf(e), replacement);
                // This break is necessary in case of having duplicates.
                // For example, in an N:N unary relationship.
                break;
            } else {
                e.replace(element, replacement);
            }
        }
    }

    public void addElement(Element element) {
        this.elements.add(element);
    }

    @Override
    public List<SingleElement> getReplacementsNeeded() {

        List<SingleElement> out = new ArrayList<>();

        for (Element element : elements) {
            out.addAll(element.getReplacementsNeeded());
        }

        return out;
    }

    @Override
    public Element getCopy() {

        ElementGroup copy = new ElementGroup();

        for (Element element : elements) {
            copy.addElement(element.getCopy());
        }

        this.copyDecoratorsTo(copy);

        return copy;
    }

    @Override
    public String toString() {

        StringBuilder out = new StringBuilder();

        out.append(super.toString()).append("[");

        boolean addComma = false;

        for (Element element : elements) {

            if (addComma) {
                out.append(",");
            } else {
                addComma = true;
            }

            out.append(element.toString());
        }

        out.append("]");

        return out.toString();
    }

    @Override
    public String formatToHTML() {

        StringBuilder out = new StringBuilder();

        boolean addComma = false;

        for (Element element : elements) {

            if (addComma) {
                out.append(", ");
            } else {
                addComma = true;
            }

            out.append(element.formatToHTML());
        }

        return super.applyDecorators(out.toString());
    }

    @Override
    public void clearAllDecorations() {

        super.emptyDecorations();

        for (Element element : elements) {
            element.clearAllDecorations();
        }
    }

    @Override
    public int getNumberOfElements() {

        int out = 0;

        for (Element element : elements) {
            out += element.getNumberOfElements();
        }

        return out;
    }

    @Override
    public List<SingleElement> getPartitions() {

        List<SingleElement> out = new ArrayList<>();

        for (Element element : elements) {
            out.addAll(element.getPartitions());
        }

        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ElementGroup that = (ElementGroup) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elements);
    }
}
