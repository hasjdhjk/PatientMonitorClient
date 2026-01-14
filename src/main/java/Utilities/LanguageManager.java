package Utilities;

import java.util.HashMap;
import java.util.Map;

// Simple language switch and text lookup
public final class LanguageManager {

    // Current language (default English)
    private static String language = "en";

    // All translations: language (key to text)
    private static final Map<String, Map<String, String>> TEXT = new HashMap<>();

    // Initialize translation tables
    static {
        Map<String, String> en = new HashMap<>();
        en.put("settings.title", "Settings");
        en.put("settings.darkMode", "Dark mode");
        en.put("settings.language", "Language");
        en.put("settings.reset", "Reset to defaults");
        en.put("settings.logout", "Log out");

        Map<String, String> zh = new HashMap<>();
        zh.put("settings.title", "设置");
        zh.put("settings.darkMode", "深色模式");
        zh.put("settings.language", "语言");
        zh.put("settings.reset", "恢复默认");
        zh.put("settings.logout", "退出登录");

        TEXT.put("en", en);
        TEXT.put("zh", zh);
    }

    // Utility class (no instances)
    private LanguageManager() {}

    // Change current language
    public static void setLanguage(String lang) {
        if (lang == null) lang = "en";
        lang = lang.trim().toLowerCase();
        language = TEXT.containsKey(lang) ? lang : "en";
    }

    // Get current language
    public static String getLanguage() {
        return language;
    }

    // Translate key based on the current language
    public static String t(String key) {
        Map<String, String> table = TEXT.getOrDefault(language, TEXT.get("en"));
        return table.getOrDefault(key, key);
    }
}
