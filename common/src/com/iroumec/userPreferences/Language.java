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

    @Override
    public String toString() {
        return LanguageManager.getMessage(this.key);
    }

    public String getKey() { return this.key; }

    public String getAcronym() { return this.acronym; }
}
