package com.iroumec.derivation.elements.containers.sources;

import com.iroumec.derivation.Derivation;
import com.iroumec.derivation.elements.Element;

public interface Source {
    Element getAbstractionElement(Derivation derivation);
}
