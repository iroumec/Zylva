package com.zylva.bdd.derivation.elements.containers.sources;

import com.zylva.bdd.derivation.Derivation;
import com.zylva.bdd.derivation.elements.Element;

public interface Source {
    Element getAbstractionElement(Derivation derivation);
}
