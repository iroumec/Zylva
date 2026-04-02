package com.zylva.common.core;

import javax.swing.JMenuItem;

import com.zylva.common.userPreferences.Multilingual;
import com.zylva.common.userPreferences.LanguageManager;

public final class Item extends JMenuItem implements Multilingual {

    /**
     * Represents the key used to obtain the message displayed in the item.
     */
    private final String key;

    /**
     * Creates a new Item.
     * 
     * @param languageKey Key of the message displayed in the item.
     */
    public Item(final String languageKey) {
        super(LanguageManager.getMessage(languageKey));
        LanguageManager.subscribeToLanguageResetList(this);
        this.key = languageKey;
    }

    @Override
    public void resetLanguage() {
        this.setText(LanguageManager.getMessage(key));
    }
}
