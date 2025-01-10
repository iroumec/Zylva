package com.bdd.mer.components.atributo.symbology;

public enum AttributeArrow {
    OPTIONAL("- - - "),
    NON_OPTIONAL("------");

    private final String arrowBody;

    AttributeArrow(String arrowBody) { this.arrowBody = arrowBody; }

    public String getArrowBody() { return this.arrowBody; }
}

// Maybe all this should be drawing...