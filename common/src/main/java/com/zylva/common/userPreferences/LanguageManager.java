package com.zylva.common.userPreferences;

import com.zylva.common.core.Diagram;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LanguageManager {

    private final static List<Multilingual> languageResetList = new ArrayList<>();
    private static Locale currentLocale;
    private static ResourceBundle messages;

    /**
     * This method initializes the preferred language.
     * <p></p>
     * By default, the preferred language is English.
     */
    public static void initialize(Diagram diagram) {

        String language = UserPreferences.loadStringPreference(Preference.LANGUAGE, "en");
        currentLocale = Locale.forLanguageTag(language);

        List<ResourceBundle> resourceBundle = diagram.getResourceBundles(currentLocale);

        messages = new CombinedResourceBundle(resourceBundle.toArray(new ResourceBundle[0]));
    }

    public static void suscribeToLanguageResetList(Multilingual multilingual) {
        languageResetList.add(multilingual);
    }

    /**
     * The language of the text appearing to the frame is changed to the selected language.
     * <p>
     * The preference is saved for future executions of the program.
     */
    public static void changeLanguage(Diagram diagram, Language ... languages) {

        Arrays.sort(languages, Comparator.comparing(Language::toString));

        // Create the frame.
        JFrame frame = new JFrame(LanguageManager.getMessage("language.selectOption"));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 120);
        frame.setLocationRelativeTo(diagram);

        // Panel for components.
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Reduce spacing around components.

        // ComboBox for language selection.
        JComboBox<Language> languageComboBox = new JComboBox<>(languages);
        languageComboBox.setSelectedItem(LanguageManager.getMessage("language." + LanguageManager.getCurrentLanguage()));
        languageComboBox.setPreferredSize(new Dimension(200, 25)); // Set fixed size to reduce padding.
        panel.add(languageComboBox);

        // Confirmation button.
        JButton confirmButton = new JButton(LanguageManager.getMessage("language.confirmOption"));
        panel.add(confirmButton);

        // Add action to the button.
        confirmButton.addActionListener(_ -> {
            Language selectedLanguage = (Language) languageComboBox.getSelectedItem();
            if (selectedLanguage != null) {
                String selectedAcronym = selectedLanguage.getAcronym();
                applyLanguage(diagram, selectedAcronym);
                frame.dispose();
                JOptionPane.showMessageDialog(diagram,
                        LanguageManager.getMessage("language.languageChanged"),
                        LanguageManager.getMessage("configurationApplied"),
                        JOptionPane.INFORMATION_MESSAGE);

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
     */
    private static void applyLanguage(Diagram diagram, String language) {

        UserPreferences.savePreference(Preference.LANGUAGE, language);

        initialize(diagram);

        notifyComponents();
    }

    /**
     * Notifies all the multilingual object subscribed to the language reset list.
     */
    private static void notifyComponents() {
        for (Multilingual multilingual : languageResetList) {
            multilingual.resetLanguage();
        }
    }

    /**
     * This method returns a message depending on the key parameter in the selected language.
     *
     * @param key The key to access the message.
     * @return The message according to the key.
     */
    public static String getMessage(String key) {

        if (messages == null) {
            throw new IllegalStateException("LanguageManager not initialized.");
        }

        try {

            return messages.getString(key);
        } catch (Exception e) {

            return "language." + LanguageManager.getCurrentLanguage();
        }
    }
}
