package com.iroumec.executables;

import com.iroumec.userPreferences.LanguageManager;

import javax.swing.*;

public final class Item extends JMenuItem {

    private final String key;
    private final boolean requieresLanguageReset;

    public Item(String languageKey, boolean requiereLanguageReset) {
        super(LanguageManager.getMessage(languageKey));
        this.key = languageKey;
        this.requieresLanguageReset = requiereLanguageReset;
    }

    public void resetLanguage() {
        this.setText(LanguageManager.getMessage(key));
    }

    public boolean requireLanguageReset() {
        return this.requieresLanguageReset;
    }
}
