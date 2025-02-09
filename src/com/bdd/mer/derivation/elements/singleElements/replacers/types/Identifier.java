package com.bdd.mer.derivation.elements.singleElements.replacers.types;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;

import java.util.ArrayList;
import java.util.List;

public class Identifier implements Type {

    @Override
    public List<Element> getElements(Derivation derivation) {

        List<Element> out = new ArrayList<>();


        for (Element element : derivation.getIdentificationElements()) {

            out.add(element.getCopy());
        }

        return out;
    }
}
