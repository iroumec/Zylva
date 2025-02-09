package com.bdd.mer.derivation.elements;

import java.io.Serializable;

public class ElementFormater implements Serializable {

    // Why string? They could be char.
    public static final String SEPARATOR = ";";
    public static final String MAIN_ATTRIBUTE = "&";
    public static final String FOREIGN_ATTRIBUTE = "@";
    public static final String OPTIONAL_ATTRIBUTE = "*";
    public static final String ALTERNATIVE_ATTRIBUTE = "+";
    public static final String MULTIVALUED_ATTRIBUTE = "#";

    static final String DUPLICATED_ATTRIBUTE = "!RENAMED";

    static String format(String text) {
        String out = text;

        // Aquí debes reemplazar las ocurrencias de cada atributo antes de aplicar los estilos
        if (text.contains(MAIN_ATTRIBUTE)) {
            out = "<span class=\"main\">" + out + "</span>";
        }

        if (text.contains(ALTERNATIVE_ATTRIBUTE)) {
            out = "<span class=\"alternative\">" + out + "</span>";
        }

        if (text.contains(FOREIGN_ATTRIBUTE)) {
            out = "<span class=\"foreign\">" + out + "</span>";
        }

        if (text.contains(OPTIONAL_ATTRIBUTE)) {
            out = "<span class=\"optional\">" + out + "</span>";
        }

        if (text.contains(MULTIVALUED_ATTRIBUTE)) {
            out = "<span class=\"multivalued\">" + out + "</span>";
        }

        if (text.contains(DUPLICATED_ATTRIBUTE)) {
            out = "<span class=\"duplicated\">" + out + "</span>";
        }

        return cleanAllFormats(out);
    }

    public static void cleanAllFormatsExcept(Element element, ElementDecorator elementDecorator) {

        for (ElementDecorator decorator : ElementDecorator.values()) {
            if (decorator != elementDecorator) {
                element.removeDecoration(decorator);
            }
        }
    }

    static String cleanAllFormats(String text) {
        return text
                .replace(MAIN_ATTRIBUTE, "")
                .replace(ALTERNATIVE_ATTRIBUTE, "")
                .replace(OPTIONAL_ATTRIBUTE, "")
                .replace(MULTIVALUED_ATTRIBUTE, "")
                .replace(FOREIGN_ATTRIBUTE, "");
    }
}