package com.zylva.bdd.derivation;

import java.io.Serializable;
import java.util.List;

public interface Derivable extends Serializable {

    /**
     * Not all derivable components need to return an identifier. Sometimes, the derivation of a component is just
     * an extension to the derivation of its parts and, so, it doesn't need to provide an identifier because it will
     * never be used.
     */
    default String getIdentifier() { return String.valueOf(this.hashCode()); }

    /**
     * @return A list with the derivations produced by the component.
     */
    List<Derivation> getDerivations();
}
