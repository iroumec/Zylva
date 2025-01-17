package com.bdd.mer.frame;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.prefs.*;

public class LanguageManager {

    private static final String LANGUAGE_KEY = "language";
    private static Locale currentLocale;
    private static ResourceBundle messages;

    /**
     * This method initializes the preferred language.
     * <p></p>
     * By default, the preferred language is English.
     */
    public static void initialize() {
        Preferences preferences = Preferences.userNodeForPackage(LanguageManager.class);
        String language = preferences.get(LANGUAGE_KEY, "en");
        currentLocale = Locale.forLanguageTag(language);
        messages = ResourceBundle.getBundle("resources/messages", currentLocale);
    }

    /**
     * The language of the text appearing to the frame are changed to the selected language.
     * <p>
     * The preference is saved for future executions of the program.
     *
     * @param mainFrame The frame whose language is wanted to be changed.
     */
    public static void changeLanguage(MainFrame mainFrame) {

        // Map display names to acronyms.
        Map<String, String> languages = new HashMap<>();
        languages.put(LanguageManager.getMessage("language.english"), "en");
        languages.put(LanguageManager.getMessage("language.spanish"), "es");

        // Create GUI.
        JFrame frame = new JFrame(LanguageManager.getMessage("language.selectOption"));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null); // Center window

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // A combo box with display names is created.
        JComboBox<String> languageComboBox = new JComboBox<>(languages.keySet().toArray(new String[0]));
        panel.add(new JLabel());
        languageComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(languageComboBox);

        // Confirmation button.
        JButton confirmButton = new JButton(LanguageManager.getMessage("language.confirmOption"));
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(confirmButton);

        // Action button.
        confirmButton.addActionListener(_ -> {
            String selectedDisplayLanguage = (String) languageComboBox.getSelectedItem();
            if (selectedDisplayLanguage != null) {
                String selectedAcronym = languages.get(selectedDisplayLanguage);
                applyLanguage(selectedAcronym, mainFrame);
                frame.dispose();
                JOptionPane.showMessageDialog(frame, LanguageManager.getMessage("language.languageChanged"));
            } else {
                JOptionPane.showMessageDialog(frame, LanguageManager.getMessage("language.noLanguageSelected"));
            }
        });

        // Add panel to frame.
        frame.add(panel);
        frame.setVisible(true);
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

        // The user preference is saved.
        Preferences preferences = Preferences.userNodeForPackage(LanguageManager.class);
        preferences.put(LANGUAGE_KEY, language);

        mainFrame.resetLanguage();
    }

    /**
     * This method returns a message depending on the key parameter.
     *
     * @param key The key to access the message.
     * @return The message according to the key.
     */
    // Obtener un mensaje en el idioma seleccionado
    public static String getMessage(String key) {
        // Asegúrate de que initialize() se ha llamado antes
        if (messages == null) {
            initialize();
        }
        return messages.getString(key);
    }
}
