package com.bdd.mer.derivation.elements.singleElements.replacers.types;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;

import java.util.List;

public interface Type {

    List<Element> getElements(Derivation derivation);
}
