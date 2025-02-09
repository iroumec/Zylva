package com.bdd.mer.derivation.elements;

import com.bdd.mer.derivation.AttributeDecorator;

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

    public static void cleanAllFormatsExcept(Element element, AttributeDecorator attributeDecorator) {

        for (AttributeDecorator decorator : AttributeDecorator.values()) {
            if (decorator != attributeDecorator) {
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

    public static String getHTMLStyles() {
        return """
                    <style>
                        .main {
                            text-decoration: underline;
                        }
                        /* Definir estilos para las líneas punteadas */
                        .foreign {
                            border-bottom: 1px dotted black; /* Línea punteada original */
                            margin: 5px 0; /* Espaciado alrededor */
                            border-bottom-width: medium;
                            position: relative; /* Necesario para el pseudoelemento ::after */
                        }
                        /* Añadir una segunda línea punteada si está dentro de otra .dotted-line */
                        .foreign .foreign::after {
                            content: ""; /* Necesario para crear el pseudoelemento */
                            display: block; /* Hace que ocupe una nueva línea */
                            border-bottom: 1px dotted black; /* Línea punteada adicional */
                            margin-top: 5px; /* Espacio entre la primera y la segunda línea */
                        }
                        .alternative {
                            display: inline-block;
                            position: relative;
                            padding-bottom: 0; /* Espacio entre el texto y las líneas */
                        }
                
                        .alternative::after {
                            content: '';
                            position: absolute;
                            bottom: 0;
                            left: 0;
                            width: 100%;
                            height: 2px;
                            background-color: black;
                        }
                
                        .alternative::before {
                            content: '';
                            position: absolute;
                            bottom: -4px; /* Ajusta el espacio entre las dos líneas */
                            left: 0;
                            width: 100%;
                            height: 2px;
                            background-color: black;
                        }
                        /* Definir estilos para las líneas punteadas */
                        .dotted-line {
                            border-top: 1px dotted black;
                            margin: 5px 0;
                        }
                        /* Optional attributes */
                        .optional {
                            font-weight: bold;
                        }
                        .optional::before {
                            content: "*";
                            font-weight: bold;
                        }
                        .italic {
                            font-style: italic;
                        }
                        .bold {
                            font-weight: bold;
                        }
                    </style>
                """;
    }
}