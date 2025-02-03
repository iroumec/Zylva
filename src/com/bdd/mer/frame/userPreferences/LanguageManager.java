package com.bdd.mer.frame.userPreferences;

import com.bdd.mer.frame.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class LanguageManager {

    private static Locale currentLocale;
    private static ResourceBundle messages;

    /**
     * This method initializes the preferred language.
     * <p></p>
     * By default, the preferred language is English.
     */
    public static void initialize() {
        String language = UserPreferences.loadStringPreference(Preference.LANGUAGE, "en");
        currentLocale = Locale.forLanguageTag(language);
        messages = ResourceBundle.getBundle("resources/messages", currentLocale);
    }

    /**
     * The language of the text appearing to the frame is changed to the selected language.
     * <p>
     * The preference is saved for future executions of the program.
     *
     * @param mainFrame The frame whose language is wanted to be changed.
     */
    public static void changeLanguage(MainFrame mainFrame) {
        // Map display names to acronyms, sorted by display names (due to the TreeMap()).
        Map<String, String> languages = new TreeMap<>();
        languages.put(LanguageManager.getMessage("language.english"), "en");
        languages.put(LanguageManager.getMessage("language.spanish"), "es");
        languages.put(LanguageManager.getMessage("language.french"), "fr");
        languages.put(LanguageManager.getMessage("language.italian"), "it");
        languages.put(LanguageManager.getMessage("language.portuguese"), "pt");

        // Create the frame.
        JFrame frame = new JFrame(LanguageManager.getMessage("language.selectOption"));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 120);
        frame.setLocationRelativeTo(null); // Center window

        // Panel for components.
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Reduce spacing around components.

        // ComboBox for language selection.
        JComboBox<String> languageComboBox = new JComboBox<>(languages.keySet().toArray(new String[0]));
        languageComboBox.setSelectedItem(LanguageManager.getMessage("language." + LanguageManager.getCurrentLanguage()));
        languageComboBox.setPreferredSize(new Dimension(200, 25)); // Set fixed size to reduce padding.

        panel.add(languageComboBox);

        // Confirmation button.
        JButton confirmButton = new JButton(LanguageManager.getMessage("language.confirmOption"));
        panel.add(confirmButton);

        // Add action to the button.
        confirmButton.addActionListener(_ -> {
            String selectedDisplayLanguage = (String) languageComboBox.getSelectedItem();
            if (selectedDisplayLanguage != null) {
                String selectedAcronym = languages.get(selectedDisplayLanguage);
                applyLanguage(selectedAcronym, mainFrame);
                frame.dispose();
                JOptionPane.showMessageDialog(frame, LanguageManager.getMessage("language.languageChanged"));
            }
        });

        // Add the panel to frame.
        frame.add(panel);
        frame.setVisible(true);
    }

    private static String getCurrentLanguage() {
        return currentLocale.getDisplayLanguage(Locale.ENGLISH).toLowerCase();
    }

    /**
     * This method applies a language to the interface.
     *
     * @param language The language the interface will switch to.
     * @param mainFrame The frame containing all the interface.
     */
    private static void applyLanguage(String language, MainFrame mainFrame) {

        currentLocale = Locale.forLanguageTag(language);
        messages = ResourceBundle.getBundle("resources/messages", currentLocale);

        UserPreferences.savePreference(Preference.LANGUAGE, language);

        mainFrame.resetLanguage();
    }

    /**
     * This method returns a message depending on the key parameter in the selected language.
     *
     * @param key The key to access the message.
     * @return The message according to the key.
     */
    public static String getMessage(String key) {
        if (messages == null) {
            initialize();
        }
        return messages.getString(key);
    }
}
