package com.bdd.mer.derivation.elements.containers.sources;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;

public interface Source {
    Element getAbstractionElement(Derivation derivation);
}
