package NetWork;

// Holds current logged-in doctor info (from database)
public final class Session {
    // Default demo user when not logged in
    private static volatile String doctorEmail = "demo";
    // Cached doctor profile fields
    private static volatile String doctorGivenName = "";
    private static volatile String doctorFamilyName = "";
    private static volatile String doctorRole = "";

    private Session() {}

    // Set doctor email after login
    public static void setDoctorEmail(String email) {
        if (email != null && !email.isBlank()) {
            doctorEmail = email.trim();
        }
    }

    // Set doctor's given and family name
    public static void setDoctorName(String givenName, String familyName) {
        doctorGivenName = (givenName == null ? "" : givenName.trim());
        doctorFamilyName = (familyName == null ? "" : familyName.trim());
    }

    // Prefer full name, fallback to email
    public static String getDoctorFullName() {
        String full = (doctorGivenName + " " + doctorFamilyName).trim();
        return full.isBlank() ? getDoctorEmail() : full;
    }

    // Set doctor role
    public static void setDoctorRole(String role) {
        doctorRole = (role == null ? "" : role.trim());
    }

    // Get current doctor role
    public static String getDoctorRole() {
        return doctorRole;
    }

    // Get current doctor email
    public static String getDoctorEmail() {
        return doctorEmail;
    }

    // Clear session on logout
    public static void clear() {
        doctorEmail = "demo";
        doctorGivenName = "";
        doctorFamilyName = "";
        doctorRole = "";
    }
}