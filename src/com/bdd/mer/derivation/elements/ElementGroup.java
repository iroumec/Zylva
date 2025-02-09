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

        return copy;
    }
}
