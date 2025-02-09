package com.bdd.mer.derivation.elements.singleElements.replacers.types;

import com.bdd.mer.derivation.AttributeDecorator;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;

import java.util.ArrayList;
import java.util.List;

public class Common implements Type {

    private final boolean optional;

    public Common() {
        this(false);
    }

    public Common(boolean optional) {
        this.optional = optional;
    }

    @Override
    public List<Element> getElements(Derivation derivation) {

        List<Element> out = new ArrayList<>();

        for (Element element : derivation.getCommonElements()) {

            Element elementToBeAdded = element.getCopy();

            if (optional) {
                elementToBeAdded.addDecoration(AttributeDecorator.OPTIONAL_ATTRIBUTE);
            }
        }

        return out;
    }
}
