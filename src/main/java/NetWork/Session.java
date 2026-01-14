package NetWork;

public final class Session {
    private static volatile String doctorEmail = "demo"; // fallback

    private Session() {}

    public static void setDoctorEmail(String email) {
        if (email != null && !email.isBlank()) {
            doctorEmail = email.trim();
        }
    }

    public static String getDoctorEmail() {
        return doctorEmail;
    }
    public static void clear() {
        doctorEmail = "demo";
    }
}