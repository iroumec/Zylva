package com.bdd.mer.derivation.formats;

import com.bdd.mer.derivation.AttributeDecorator;

public class HTMLFormater {

    public static String format(String text) {
        String out = text;

        // Aquí debes reemplazar las ocurrencias de cada atributo antes de aplicar los estilos
        if (text.contains(AttributeDecorator.MAIN_ATTRIBUTE)) {
            out = "<span class=\"main\">" + out + "</span>";
        }

        if (text.contains(AttributeDecorator.ALTERNATIVE_ATTRIBUTE)) {
            out = "<span class=\"alternative\">" + out + "</span>";
        }

        if (text.contains(AttributeDecorator.FOREIGN_ATTRIBUTE)) {
            out = "<span class=\"foreign\">" + out + "</span>";
        }

        if (text.contains(AttributeDecorator.OPTIONAL_ATTRIBUTE)) {
            out = "<span class=\"optional\">" + out + "</span>";
        }

        if (text.contains(AttributeDecorator.DUPLICATED_ATTRIBUTE)) {
            out = "<span class=\"duplicated\">" + out + "</span>";
        }

        return out;
    }
}
