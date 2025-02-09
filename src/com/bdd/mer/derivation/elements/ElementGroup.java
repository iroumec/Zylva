package com.bdd.mer.derivation.elements;

import com.bdd.mer.derivation.elements.singleElements.Replacer;

import java.util.ArrayList;
import java.util.List;

public class ElementGroup extends Element {

    private final List<Element> elements;

    public ElementGroup() {
        this.elements = new ArrayList<>();
    }

    public void addElement(Element element) {
        this.elements.add(element);
    }

    @Override
    public List<Replacer> getReplacementsNeeded() {

        List<Replacer> out = new ArrayList<>();

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
