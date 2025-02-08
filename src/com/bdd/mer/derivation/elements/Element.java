package com.bdd.mer.derivation.elements;

import com.bdd.mer.derivation.AttributeDecorator;

import java.util.ArrayList;
import java.util.List;

public class Element {

    protected String name;
    private final List<AttributeDecorator> decorations;

    public Element(String name) {
        this.name = name;
        this.decorations = new ArrayList<>();
    }

    public void addDecoration(AttributeDecorator decorator) {
        this.decorations.add(decorator);
    }
}
