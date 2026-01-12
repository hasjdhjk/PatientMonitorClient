package Utilities;

import java.util.prefs.Preferences;

public class SettingManager {

    private static final String NODE = "PatientMonitorClient";

    private static final String KEY_DARK_MODE  = "darkMode";
    private static final String KEY_LANGUAGE   = "language";
    private static final String KEY_AVATAR     = "avatarPath"; // ✅ 新增

    private final Preferences prefs = Preferences.userRoot().node(NODE);

    // ======================
    // Theme
    // ======================
    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkMode(boolean enabled) {
        prefs.putBoolean(KEY_DARK_MODE, enabled);
    }

    // ======================
    // Language
    // ======================
    public String getLanguage() {
        // default English
        return prefs.get(KEY_LANGUAGE, "en");
    }

    public void setLanguage(String langCode) {
        // e.g., "en", "zh"
        if (langCode == null || langCode.isBlank()) return;
        prefs.put(KEY_LANGUAGE, langCode);
    }

    // ======================
    // Avatar
    // ======================
    /**
     * @return absolute path to avatar image, or empty string if none
     */
    public String getAvatarPath() {
        return prefs.get(KEY_AVATAR, "");
    }

    /**
     * @param path absolute file path; empty / null means "remove avatar"
     */
    public void setAvatarPath(String path) {
        if (path == null || path.isBlank()) {
            prefs.remove(KEY_AVATAR);
        } else {
            prefs.put(KEY_AVATAR, path);
        }
    }

    // ======================
    // Reset
    // ======================
    public void resetToDefaults() {
        setDarkMode(false);
        setLanguage("en");
        prefs.remove(KEY_AVATAR); // ✅ reset 时移除头像
    }
}

