package com.bdd.mer.derivation;

public class DerivationFormater {

    // Why string? They could be char.
    public static final String MAIN_ATTRIBUTE = "&";
    public static final String OPTIONAL_ATTRIBUTE = "*";
    public static final String ALTERNATIVE_ATTRIBUTE = "/";
    public static final String MULTIVALUED_ATTRIBUTE = "#";

    static String format(String text) {
        String out = text;

        // Aquí debes reemplazar las ocurrencias de cada atributo antes de aplicar los estilos
        if (text.startsWith(MAIN_ATTRIBUTE)) {
            out = "<span class=\"main\">" + text.substring(1) + "</span>";
        }

        if (text.startsWith(ALTERNATIVE_ATTRIBUTE)) {
            out = "<span class=\"alternative\">" + text.substring(1) + "</span>";
        }

        if (text.startsWith(OPTIONAL_ATTRIBUTE)) {
            out = "<span class=\"optional\">" + text.substring(1) + "</span>";
        }

        if (text.startsWith(MULTIVALUED_ATTRIBUTE)) {
            out = "<span class=\"multivalued\">" + text.substring(1) + "</span>";
        }

        // Eliminar los espacios y signos '$'
        return out.replaceAll("\\$|\\s", "");
    }

    static String cleanAllFormats(String text) {
        return text
                .replace(MAIN_ATTRIBUTE, "")
                .replace(ALTERNATIVE_ATTRIBUTE, "")
                .replace(OPTIONAL_ATTRIBUTE, "")
                .replace(MULTIVALUED_ATTRIBUTE, "")
                .replaceAll("\\$|\\s", ""); // Elimina todos los '$' y espacios
    }

    static String getHTMLStyles() {
        return "";
    }
}