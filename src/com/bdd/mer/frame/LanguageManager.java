package com.bdd.mer.frame;

import javax.swing.*;
import java.awt.*;
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
        currentLocale = Locale.forLanguageTag(language);
        messages = ResourceBundle.getBundle("resources/messages", currentLocale);
    }

    // Cambia el idioma y guarda la preferencia
    public static void changeLanguage(MainFrame mainFrame) {

        // Map display names to acronyms
        Map<String, String> languages = new HashMap<>();
        languages.put(LanguageManager.getMessage("language.english"), "en");
        languages.put(LanguageManager.getMessage("language.spanish"), "es");

        // Create GUI
        JFrame frame = new JFrame(LanguageManager.getMessage("language.selectOption"));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null); // Center window

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Create a combo box with display names
        JComboBox<String> languageComboBox = new JComboBox<>(languages.keySet().toArray(new String[0]));
        panel.add(new JLabel(/*LanguageManager.getMessage("language.selectPrompt")*/));
        languageComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(languageComboBox);

        // Confirmation button
        JButton confirmButton = new JButton(LanguageManager.getMessage("language.confirmOption"));
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(confirmButton);

        // Button action
        confirmButton.addActionListener(_ -> {
            String selectedDisplayLanguage = (String) languageComboBox.getSelectedItem();
            if (selectedDisplayLanguage != null) {
                String selectedAcronym = languages.get(selectedDisplayLanguage);
                applyLanguage(selectedAcronym, mainFrame);
                frame.dispose();
                JOptionPane.showMessageDialog(frame, LanguageManager.getMessage("language.languageChanged") + " " + selectedDisplayLanguage);
            } else {
                JOptionPane.showMessageDialog(frame, LanguageManager.getMessage("language.noLanguageSelected"));
            }
        });

        // Add panel to frame
        frame.add(panel);
        frame.setVisible(true);
    }

    private static void applyLanguage(String language, MainFrame mainFrame) {

        currentLocale = Locale.forLanguageTag(language);
        messages = ResourceBundle.getBundle("resources/messages", currentLocale);

        // Guardamos la preferencia del usuario
        Preferences preferences = Preferences.userNodeForPackage(LanguageManager.class);
        preferences.put(LANGUAGE_KEY, language);

        mainFrame.resetLanguage();
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
