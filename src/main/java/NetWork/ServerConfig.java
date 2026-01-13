package NetWork;

public final class ServerConfig {
    private ServerConfig() {}

    private static final String DEFAULT_BASE = "https://bioeng-bbb-app.impaas.uk";

    public static String baseUrl() {
        String jvm = System.getProperty("server.base");
        if (jvm != null && !jvm.isBlank()) return trimSlash(jvm);

        String env = System.getenv("PATIENT_SERVER_BASE");
        if (env != null && !env.isBlank()) return trimSlash(env);

        return trimSlash(DEFAULT_BASE);
    }

    public static String url(String endpoint) {
        if (endpoint == null || endpoint.isBlank()) return baseUrl();
        if (!endpoint.startsWith("/")) endpoint = "/" + endpoint;
        return baseUrl() + endpoint;
    }

    public static String dashboardUrl() {
        return url("/digital_twin/dashboard.html");
    }

    private static String trimSlash(String s) {
        s = s.trim();
        while (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        return s;
    }
}