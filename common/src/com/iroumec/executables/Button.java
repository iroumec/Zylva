package com.iroumec.executables;

import com.iroumec.userPreferences.LanguageManager;

import javax.swing.*;

public final class Button extends JButton {

    private final String key;
    private final boolean requieresLanguageReset;

    public Button() {
        this(false);
    }

    public Button(String languageKey) {
        this(languageKey, false);
    }

    public Button(boolean requiereLanguageReset) {
        this(null, requiereLanguageReset);
    }

    public Button(String languageKey, boolean requieresLanguageReset) {
        super(LanguageManager.getMessage(languageKey));
        this.key = languageKey;
        this.requieresLanguageReset = requieresLanguageReset;
    }

    public void resetLanguage() {

        if (key != null) {
            this.setText(LanguageManager.getMessage(key));
        }

    }

    public boolean requireLanguageReset() {
        return this.requieresLanguageReset;
    }
}
