package com.bdd.mer.derivation.elements;

public class Common extends Type {

    private final boolean optional;

    public Common() {
        this(false);
    }

    public Common(boolean optional) {
        this.optional = optional;
    }
}
