package com.bdd.mer.derivation.exporters;

import com.bdd.mer.derivation.Constraint;
import com.bdd.mer.derivation.Derivation;

import java.util.Collection;

class HTMLFormater {

     static String formatToHTML(Collection<Derivation> derivations, Collection<Constraint> constraints) {

        StringBuilder htmlContent =
                new StringBuilder("""
                        <!DOCTYPE html>
                        <html lang="es">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Estructura con formato</title>
                        """);

        htmlContent.append(getHTMLStyles());

        htmlContent.append("""
                            </head>
                            <body>
                            <h1>Derivation.</h1>
                            <div class="dotted-line"></div>
                            <h2>Relationships:</h2>
                            """);

        for (Derivation derivation : derivations) {
            htmlContent
                    .append("<ul>\n")
                    .append("<li>").append(derivation.formatToHTML()).append("</li>\n")
                    .append("</ul>\n")
            ;
        }

        if (!constraints.isEmpty()) {

            htmlContent
                    .append("<div class=\"dotted-line\"></div>\n")
                    .append("<h2>Referential integrity constraints:</h2>\n")
            ;

            for (Constraint constraint : constraints) {
                htmlContent
                        .append("<ul>\n")
                        .append("<li>").append(constraint).append("</li>\n")
                        .append("</ul>\n")
                ;
            }
        }

        htmlContent
                .append("<div class=\"dotted-line\"></div>\n")
                .append("<p>\n")
                .append("<span class=\"bold\">Disclaimer!</span>\n")
                .append("There could be more valid derivations. This is just one of those.\n")
                .append("</p>")
        ;

        htmlContent
                .append("<div class=\"dotted-line\"></div>\n")
                .append("</body>\n")
                .append("</html>\n")
        ;

        return htmlContent.toString();
    }

    static String getHTMLStyles() {
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
