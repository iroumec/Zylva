package com.bdd.mer.derivation.elements.container.replacers.types;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;

public interface Type {
    Element getAbstractionElement(Derivation derivation);
}
