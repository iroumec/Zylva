package com.bdd.mer.components.atributo.symbology;

import java.io.Serializable;

public enum AttributeEnding implements Serializable {
    MULTIVALUED(">"),
    NON_MULTIVALUED("-");

    private final String arrowEnding;

    AttributeEnding(String arrowEnding) { this.arrowEnding = arrowEnding; }

    public String getArrowEnding() { return this.arrowEnding; }
}
