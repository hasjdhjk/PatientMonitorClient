package Services;

import Models.DoctorProfile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DoctorProfileService {

    private static final String DIR_NAME = ".patientmonitor";
    private static final String FILE_NAME = "doctor_profile.properties";

    private static final String K_FIRST = "firstName";
    private static final String K_LAST  = "lastName";
    private static final String K_ID = "idNumber";
    private static final String K_AGE = "age";
    private static final String K_SPECIALTY = "specialty";
    private static final String K_EMAIL = "email";

    private final Path filePath;

    public DoctorProfileService() {
        String home = System.getProperty("user.home");
        Path dir = Path.of(home, DIR_NAME);
        this.filePath = dir.resolve(FILE_NAME);
    }

    public DoctorProfile load() {
        if (!Files.exists(filePath)) {
            DoctorProfile def = DoctorProfile.defaults();
            save(def);
            return def;
        }

        Properties p = new Properties();
        try (InputStream in = new FileInputStream(filePath.toFile())) {
            p.load(in);
        } catch (IOException e) {
            return DoctorProfile.defaults();
        }

        DoctorProfile d = new DoctorProfile();
        DoctorProfile def = DoctorProfile.defaults();

        d.setFirstName(p.getProperty(K_FIRST, def.getFirstName()));
        d.setLastName(p.getProperty(K_LAST, def.getLastName()));
        d.setIdNumber(p.getProperty(K_ID, def.getIdNumber()));
        d.setOrgnization(p.getProperty(K_SPECIALTY, def.getOrgnization()));
        d.setEmail(p.getProperty(K_EMAIL, def.getEmail()));

        int age = DoctorProfile.defaults().getAge();
        try {
            age = Integer.parseInt(p.getProperty(K_AGE, String.valueOf(age)).trim());
        } catch (Exception ignored) {}
        d.setAge(age);

        return d;
    }

    public void save(DoctorProfile d) {
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException ignored) {}

        Properties p = new Properties();
        p.setProperty(K_FIRST, safe(d.getFirstName()));
        p.setProperty(K_LAST, safe(d.getLastName()));
        p.setProperty(K_ID, safe(d.getIdNumber()));
        p.setProperty(K_AGE, String.valueOf(d.getAge()));
        p.setProperty(K_SPECIALTY, safe(d.getOrgnization()));
        p.setProperty(K_EMAIL, safe(d.getEmail()));

        try (OutputStream out = new FileOutputStream(filePath.toFile())) {
            p.store(out, "PatientMonitorClient Doctor Profile");
        } catch (IOException e) {
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}