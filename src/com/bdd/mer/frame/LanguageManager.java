package com.bdd.mer.frame;

import java.util.*;
import java.util.prefs.*;

public class LanguageManager {
    private static final String LANGUAGE_KEY = "language";
    private static Locale currentLocale;
    private static ResourceBundle messages;

    // Method to initialize the language.
    public static void initialize() {
        Preferences preferences = Preferences.userNodeForPackage(LanguageManager.class);
        String language = preferences.get(LANGUAGE_KEY, "en"); // Por defecto es inglés
        currentLocale = new Locale(language);
        messages = ResourceBundle.getBundle("resources/messages", currentLocale);
    }

    // Cambia el idioma y guarda la preferencia
    public static void changeLanguage(String language) {
        currentLocale = new Locale(language);
        messages = ResourceBundle.getBundle("resources/messages", currentLocale);

        // Guardamos la preferencia del usuario
        Preferences prefs = Preferences.userNodeForPackage(LanguageManager.class);
        prefs.put(LANGUAGE_KEY, language);
    }

    // Obtener un mensaje en el idioma seleccionado
    public static String getMessage(String key) {
        // Asegúrate de que initialize() se ha llamado antes
        if (messages == null) {
            initialize();
        }
        return messages.getString(key);
    }
}
