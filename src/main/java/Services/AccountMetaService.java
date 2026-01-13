package Services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Properties;

public class AccountMetaService {

    private static final String DIR_NAME = ".patientmonitor";
    private static final String FILE_NAME = "account_meta.properties";

    private static final String K_CREATED_AT = "createdAt";
    private static final String K_LAST_LOGIN = "lastLogin";
    private static final String K_AVATAR_PATH = "avatarPath";
    private static final String K_ROLE = "role";

    private final Path dirPath;
    private final Path filePath;

    public AccountMetaService() {
        String home = System.getProperty("user.home");
        this.dirPath = Path.of(home, DIR_NAME);
        this.filePath = dirPath.resolve(FILE_NAME);
    }

    public static class Meta {
        public LocalDateTime createdAt;
        public LocalDateTime lastLogin;
        public String avatarPath;
        public String role;
    }

    public Meta loadOrInit() {
        Meta m = new Meta();

        // ensure directory exists
        try { Files.createDirectories(dirPath); } catch (IOException ignored) {}

        Properties p = new Properties();
        if (Files.exists(filePath)) {
            try (InputStream in = new FileInputStream(filePath.toFile())) {
                p.load(in);
            } catch (IOException ignored) {}
        }

        LocalDateTime now = LocalDateTime.now();

        // createdAt: first run -> set now and persist
        String createdStr = p.getProperty(K_CREATED_AT, "");
        m.createdAt = parseDT(createdStr, null);
        if (m.createdAt == null) {
            m.createdAt = now;
            p.setProperty(K_CREATED_AT, m.createdAt.toString());
        }

        // lastLogin: read old, then update to now and persist
        String lastLoginStr = p.getProperty(K_LAST_LOGIN, "");
        m.lastLogin = parseDT(lastLoginStr, now.minusHours(2)); // fallback display
        p.setProperty(K_LAST_LOGIN, now.toString()); // update on load

        // avatar path
        m.avatarPath = p.getProperty(K_AVATAR_PATH, "").trim();
        if (m.avatarPath.isBlank()) m.avatarPath = null;

        m.role = p.getProperty(K_ROLE, "").trim();
        if (m.role.isBlank()) m.role = null;

        if (!p.containsKey(K_ROLE)) {
            p.setProperty(K_ROLE, "");
        }
        // persist updated lastLogin / maybe createdAt
        try (OutputStream out = new FileOutputStream(filePath.toFile())) {
            p.store(out, "PatientMonitorClient Account Meta");
        } catch (IOException ignored) {}

        return m;
    }

    public void saveAvatarPath(String pathOrNull) {
        try { Files.createDirectories(dirPath); } catch (IOException ignored) {}

        Properties p = new Properties();
        if (Files.exists(filePath)) {
            try (InputStream in = new FileInputStream(filePath.toFile())) {
                p.load(in);
            } catch (IOException ignored) {}
        }
        p.setProperty(K_AVATAR_PATH, pathOrNull == null ? "" : pathOrNull);

        try (OutputStream out = new FileOutputStream(filePath.toFile())) {
            p.store(out, "PatientMonitorClient Account Meta");
        } catch (IOException ignored) {}
    }

    public void saveRole(String roleOrNull) {
        try { Files.createDirectories(dirPath); } catch (IOException ignored) {}

        Properties p = new Properties();
        if (Files.exists(filePath)) {
            try (InputStream in = new FileInputStream(filePath.toFile())) {
                p.load(in);
            } catch (IOException ignored) {}
        }
        p.setProperty(K_ROLE, roleOrNull == null ? "" : roleOrNull);

        try (OutputStream out = new FileOutputStream(filePath.toFile())) {
            p.store(out, "PatientMonitorClient Account Meta");
        } catch (IOException ignored) {}
    }
    
    public Path getDirPath() {
        return dirPath;
    }

    private LocalDateTime parseDT(String s, LocalDateTime fallback) {
        if (s == null || s.isBlank()) return fallback;
        try { return LocalDateTime.parse(s.trim()); } catch (Exception e) { return fallback; }
    }
}