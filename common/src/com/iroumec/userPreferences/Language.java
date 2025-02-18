package com.iroumec.userPreferences;

public enum Language {

    FRENCH("language.french", "fr"),
    SPANISH("language.spanish", "es"),
    ENGLISH("language.english", "en"),
    ITALIAN("language.italian", "it"),
    PORTUGUESE("language.portuguese", "pt");

    private final String key, acronym;

    Language(String key, String acronym) {
        this.key = key;
        this.acronym = acronym;
    }

    /**
     *
     * @return The acronym of the language, according to the ISO 639-2.
     */
    public String getAcronym() { return this.acronym; }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public String toString() {
        return LanguageManager.getMessage(this.key);
    }
}
