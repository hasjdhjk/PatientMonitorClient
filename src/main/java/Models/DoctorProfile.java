package Models;

/**
 * Real clinician profile data shown on Account page.
 * Not UI preferences (dark mode/language).
 */
public class DoctorProfile {
    private String name;
    private String idNumber;
    private int age;
    private String specialty;
    private String email;

    public DoctorProfile() {}

    public DoctorProfile(String name, String idNumber, int age, String specialty, String email) {
        this.name = name;
        this.idNumber = idNumber;
        this.age = age;
        this.specialty = specialty;
        this.email = email;
    }

    public static DoctorProfile defaults() {
        return new DoctorProfile(
                "Raymond",
                "DOC123456",
                20,
                "Cardiac Surgeon",
                "doctor@mail.com"
        );
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}