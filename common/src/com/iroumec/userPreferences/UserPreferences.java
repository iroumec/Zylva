package com.iroumec.userPreferences;

import org.jetbrains.annotations.NotNull;

import java.util.prefs.Preferences;

public class UserPreferences {

    public static void savePreference(@NotNull Preference preference, String value) {
        Preferences preferences = Preferences.userNodeForPackage(UserPreferences.class);
        preferences.put(preference.toString(), value);
    }

    public static void savePreference(@NotNull Preference preference, boolean value) {
        Preferences preferences = Preferences.userNodeForPackage(UserPreferences.class);
        preferences.putBoolean(preference.toString(), value);
    }

    public static String loadStringPreference(@NotNull Preference preference, String defaultValue) {
        Preferences preferences = Preferences.userNodeForPackage(UserPreferences.class);
        return preferences.get(preference.toString(), defaultValue);
    }

    public static boolean loadBooleanPreference(@NotNull Preference preference, boolean defaultValue) {
        Preferences preferences = Preferences.userNodeForPackage(UserPreferences.class);
        return preferences.getBoolean(preference.toString(), defaultValue);
    }
}
