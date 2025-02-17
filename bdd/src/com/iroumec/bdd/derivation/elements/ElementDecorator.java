package com.iroumec.bdd.derivation.elements;

import org.jetbrains.annotations.NotNull;

public enum ElementDecorator implements CharSequence {

    MAIN("&", "main"),
    FOREIGN("@", "foreign"),
    OPTIONAL("*", "optional"),
    ALTERNATIVE("+", "alternative"),
    MULTIVALUED("#", "multivalued"),
    DUPLICATED("!RENAMED", "duplicated");

    private final String notation;
    private final String styleName;

    ElementDecorator(String notation, String styleName) {
        this.notation = notation;
        this.styleName = styleName;
    }

    public String formatToHTML(String text) {
        return "<span class=\"" + styleName + "\">" + text + "</span>";
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

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
