package com.bdd.mer.derivation;

public enum AttributeDecorator {

    SEPARATOR(";"),
    MAIN_ATTRIBUTE("&"),
    FOREIGN_ATTRIBUTE("@"),
    OPTIONAL_ATTRIBUTE("*"),
    ALTERNATIVE_ATTRIBUTE("+"),
    MULTIVALUED_ATTRIBUTE("#");

    private final String notation;

    AttributeDecorator(String notation) { this.notation = notation; }

    @Override
    public String toString() {
        return this.notation;
    }
}
