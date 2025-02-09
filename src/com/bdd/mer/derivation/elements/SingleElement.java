package com.bdd.mer.derivation.elements;

import com.bdd.mer.derivation.elements.singleElements.Replacer;

import java.util.ArrayList;
import java.util.List;

public abstract class SingleElement extends Element {

    protected final String name;

    public SingleElement(String name) {
        this.name = name;
    }

    @Override
    public String toString() {

        return super.toString() + this.name;
    }

    @Override
    public List<Replacer> getReplacementsNeeded() {
        return new ArrayList<>();
    }
}
