package NetWork;

// Central place for server URLs
public final class ServerConfig {
    private ServerConfig() {}

    // Fallback server address
    private static final String DEFAULT_BASE = "https://bioeng-bbb-app.impaas.uk";

    // Decide which server base URL to use
    public static String baseUrl() {
        // 1) JVM argument
        String jvm = System.getProperty("server.base");
        if (jvm != null && !jvm.isBlank()) return trimSlash(jvm);

        // 2) Environment variable
        String env = System.getenv("PATIENT_SERVER_BASE");
        if (env != null && !env.isBlank()) return trimSlash(env);

        // 3) Default
        return trimSlash(DEFAULT_BASE);
    }

    // Build full API URL
    public static String url(String endpoint) {
        if (endpoint == null || endpoint.isBlank()) return baseUrl();
        if (!endpoint.startsWith("/")) endpoint = "/" + endpoint;
        return baseUrl() + endpoint;
    }

    // Digital twin dashboard page
    public static String dashboardUrl() {
        return url("/digital_twin/dashboard.html");
    }

    // Remove trailing slashes
    private static String trimSlash(String s) {
        s = s.trim();
        while (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        return s;
    }
}