package com.bdd.mer.derivation.elements.singleElements;

import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.SingleElement;

import java.util.ArrayList;
import java.util.List;

public class Final extends SingleElement {

    public Final(String name) {
        super(name);
    }

    @Override
    public List<Replacer> getReplacementsNeeded() {
        return new ArrayList<>();
    }

    @Override
    public Element getCopy() {
        return new Final(this.name);
    }
}
