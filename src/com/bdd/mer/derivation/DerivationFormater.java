package com.bdd.mer.derivation;

public class DerivationFormater {

    // Why string? They could be char.
    public static final String MAIN_ATTRIBUTE = "&";
    public static final String OPTIONAL_ATTRIBUTE = "*";
    public static final String ALTERNATIVE_ATTRIBUTE = "/";

    static String format(String text) {

        String out = text;

        if (text.startsWith(MAIN_ATTRIBUTE)) {
            out = "<span class=\"main\">" + text + "</span>";
        }

        if (text.startsWith(ALTERNATIVE_ATTRIBUTE)) {
            out = "<span class=\"alternative\">" + text + "</span>";
        }

        if (text.startsWith(OPTIONAL_ATTRIBUTE)) {
            out = "<span class=\"optional\">" + text + "</span>";
        }

        return out;
    }
}
