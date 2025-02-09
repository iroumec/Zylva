package com.bdd.mer.derivation.elements.container.replacers.types;

import com.bdd.mer.derivation.Constraint;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;

public interface Source {
    Element getAbstractionElement(Derivation derivation);
}
