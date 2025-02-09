package com.bdd.mer.derivation;

public class TextUtility {

    @SuppressWarnings("SameParameterValue")
    public static void deleteLast(String textToBeDeleted, StringBuilder stringBuilder) {
        int startIndex = stringBuilder.lastIndexOf(textToBeDeleted);
        if (startIndex != -1) {
            stringBuilder.delete(startIndex, startIndex + 2); // Eliminar la coma y el espacio
        }
    }
}
