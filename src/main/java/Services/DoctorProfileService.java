package Services;

import Models.DoctorProfile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DoctorProfileService {

    private static final String DIR_NAME = ".patientmonitor";
    private static final String FILE_NAME = "doctor_profile.properties";

    private static final String K_NAME = "name";
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
            // 第一次运行：写一份默认文件，之后就是真实数据源
            save(def);
            return def;
        }

        Properties p = new Properties();
        try (InputStream in = new FileInputStream(filePath.toFile())) {
            p.load(in);
        } catch (IOException e) {
            // 读取失败就回退默认（但不崩）
            return DoctorProfile.defaults();
        }

        DoctorProfile d = new DoctorProfile();
        d.setName(p.getProperty(K_NAME, DoctorProfile.defaults().getName()));
        d.setIdNumber(p.getProperty(K_ID, DoctorProfile.defaults().getIdNumber()));
        d.setSpecialty(p.getProperty(K_SPECIALTY, DoctorProfile.defaults().getSpecialty()));
        d.setEmail(p.getProperty(K_EMAIL, DoctorProfile.defaults().getEmail()));

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
        p.setProperty(K_NAME, safe(d.getName()));
        p.setProperty(K_ID, safe(d.getIdNumber()));
        p.setProperty(K_AGE, String.valueOf(d.getAge()));
        p.setProperty(K_SPECIALTY, safe(d.getSpecialty()));
        p.setProperty(K_EMAIL, safe(d.getEmail()));

        try (OutputStream out = new FileOutputStream(filePath.toFile())) {
            p.store(out, "PatientMonitorClient Doctor Profile");
        } catch (IOException e) {
            // 如果写失败：这里可以加弹窗/日志，但先不崩
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}