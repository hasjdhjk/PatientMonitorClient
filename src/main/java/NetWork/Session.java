package NetWork;

public final class Session {
    private static volatile String doctorEmail = "demo"; // fallback
    private static volatile String doctorGivenName = "";
    private static volatile String doctorFamilyName = "";

    private Session() {}

    public static void setDoctorEmail(String email) {
        if (email != null && !email.isBlank()) {
            doctorEmail = email.trim();
        }
    }

    public static void setDoctorName(String givenName, String familyName) {
        doctorGivenName = (givenName == null ? "" : givenName.trim());
        doctorFamilyName = (familyName == null ? "" : familyName.trim());
    }

    public static String getDoctorFullName() {
        String full = (doctorGivenName + " " + doctorFamilyName).trim();
        return full.isBlank() ? getDoctorEmail() : full;
    }

    public static String getDoctorEmail() {
        return doctorEmail;
    }
    public static void clear() {
        doctorEmail = "demo";
        doctorGivenName = "";
        doctorFamilyName = "";
    }
}