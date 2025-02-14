package com.iroumec.derivation;

import java.io.Serializable;
import java.util.List;

public interface Derivable extends Serializable {

    String getIdentifier();
    List<Derivation> getDerivations();
}
