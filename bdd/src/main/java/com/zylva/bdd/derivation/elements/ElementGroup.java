package com.zylva.bdd.derivation.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ElementGroup extends Element {

    private final List<Element> elements;

    public ElementGroup() {
        this.elements = new ArrayList<>();
    }

    public void addElement(Element element) {
        this.elements.add(element);
    }

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

    /**
     * @param element Element to be removed.
     * @return {@code TRUE} if the element was removed.
     */
    @Override
    public boolean removeElement(Element element) {

        // The element is in the list.
        if (this.elements.remove(element)) {
            return true;
        }

        // The element must be searched.
        for (Element e : elements) {
            if (e.removeElement(element)) {

                if (e.isEmpty()) {
                    this.elements.remove(e);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isEmpty() { return this.elements.isEmpty(); }

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
    public boolean contains(SingleElement element) {

        for (Element e : elements) {
            if (e.contains(element)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {

        StringBuilder out = new StringBuilder();

        String decorator = super.toString();

        if (!decorator.isEmpty()) {
            out.append(decorator).append("[");
        }

        boolean addComma = false;

        for (Element element : elements) {

            if (addComma) {
                out.append(",");
            } else {
                addComma = true;
            }

            out.append(element.toString());
        }

        if (!decorator.isEmpty()) {
            out.append("]");
        }

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
