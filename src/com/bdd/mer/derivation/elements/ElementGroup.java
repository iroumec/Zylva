package com.bdd.mer.derivation.elements;

import java.util.ArrayList;
import java.util.List;

public class ElementGroup extends Element {

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
}
