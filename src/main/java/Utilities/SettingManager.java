package Utilities;

import java.util.prefs.Preferences;

public class SettingManager {

    private static final String NODE = "PatientMonitorClient";
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_LANGUAGE = "language";

    private final Preferences prefs = Preferences.userRoot().node(NODE);

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkMode(boolean enabled) {
        prefs.putBoolean(KEY_DARK_MODE, enabled);
    }

    public String getLanguage() {
        // default English
        return prefs.get(KEY_LANGUAGE, "en");
    }

    public void setLanguage(String langCode) {
        // e.g., "en", "zh", "de"
        prefs.put(KEY_LANGUAGE, langCode);
    }

    public void resetToDefaults() {
        setDarkMode(false);
        setLanguage("en");
    }
}
