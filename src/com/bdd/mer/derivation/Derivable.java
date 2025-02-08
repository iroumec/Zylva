package com.bdd.mer.derivation;

import com.bdd.mer.derivation.derivationObjects.DerivationObject;

import java.io.Serializable;
import java.util.List;

public interface Derivable extends Serializable {

    String getIdentifier();
    List<DerivationObject> getDerivationObjects();
}
