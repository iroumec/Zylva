package com.bdd.mer.frame;

import java.util.prefs.Preferences;

public class UserPreferences {

    private static final String ANTIALIASING_KEY = "antialiasing_enabled";

    // Save the preference
    public static void saveAntialiasingPreference(boolean enabled) {
        Preferences preferences = Preferences.userNodeForPackage(UserPreferences.class);
        preferences.putBoolean(ANTIALIASING_KEY, enabled);
    }

    // Load the preference
    public static boolean loadAntialiasingPreference() {
        Preferences preferences = Preferences.userNodeForPackage(UserPreferences.class);
        // Default to true if the preference is not set
        return preferences.getBoolean(ANTIALIASING_KEY, true);
    }
}
