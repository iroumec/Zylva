package com.zylva.common.core;

import com.zylva.common.userPreferences.LanguageManager;
import com.zylva.common.userPreferences.Multilingual;

import javax.swing.*;

public final class Item extends JMenuItem implements Multilingual {

    private final String key;

    public Item(String languageKey) {
        super(LanguageManager.getMessage(languageKey));
        LanguageManager.suscribeToLanguageResetList(this);
        this.key = languageKey;
    }

    @Override
    public void resetLanguage() {
        this.setText(LanguageManager.getMessage(key));
    }
}
