package Utilities;

import java.util.HashMap;
import java.util.Map;

public final class LanguageManager {

    private static String language = "en";

    private static final Map<String, Map<String, String>> TEXT = new HashMap<>();

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

    private LanguageManager() {}

    public static void setLanguage(String lang) {
        if (lang == null) lang = "en";
        lang = lang.trim().toLowerCase();
        language = TEXT.containsKey(lang) ? lang : "en";
    }

    public static String getLanguage() {
        return language;
    }

    public static String t(String key) {
        Map<String, String> table = TEXT.getOrDefault(language, TEXT.get("en"));
        return table.getOrDefault(key, key);
    }
}
