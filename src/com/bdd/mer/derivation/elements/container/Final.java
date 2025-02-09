package com.bdd.mer.derivation.elements.container;

import com.bdd.mer.derivation.Constraint;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;

import java.util.ArrayList;
import java.util.List;

public class Final implements Holder {

    @Override
    public boolean mayNeedReplacement() {
        return false;
    }

    @Override
    public Element abstractReplacement(Derivation derivation) {
        return null;
    }

    @Override
    public List<Constraint> getGeneratedConstraints() {
        return new ArrayList<>();
    }
}
