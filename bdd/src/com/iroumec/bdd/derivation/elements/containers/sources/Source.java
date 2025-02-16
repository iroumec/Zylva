package com.iroumec.bdd.derivation.elements.containers.sources;

import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.Element;

public interface Source {
    Element getAbstractionElement(Derivation derivation);
}
