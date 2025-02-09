package com.bdd.mer.derivation;

import org.jetbrains.annotations.NotNull;

public enum AttributeDecorator implements CharSequence {

    SEPARATOR(";"),
    MAIN_ATTRIBUTE("&"),
    FOREIGN_ATTRIBUTE("@"),
    OPTIONAL_ATTRIBUTE("*"),
    ALTERNATIVE_ATTRIBUTE("+"),
    MULTIVALUED_ATTRIBUTE("#"),
    DUPLICATED_ATTRIBUTE("!RENAMED");

    private final String notation;

    AttributeDecorator(String notation) { this.notation = notation; }

    public String formatToHTML() {
        return "";
    }

    @Override
    public int length() {
        return notation.length();
    }

    @Override
    public char charAt(int index) {
        return notation.charAt(index);
    }

    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return notation.subSequence(start, end);
    }

    @Override
    public @NotNull String toString() {
        return this.notation;
    }
}
