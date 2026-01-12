package Models;

public class DoctorProfile {
    private String firstName;
    private String lastName;
    private String idNumber;
    private int age;
    private String specialty;
    private String email;

    public DoctorProfile() {}

    public DoctorProfile(String firstName, String lastName, String idNumber, int age, String specialty, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.idNumber = idNumber;
        this.age = age;
        this.specialty = specialty;
        this.email = email;
    }

    public static DoctorProfile defaults() {
        return new DoctorProfile(
                "Raymond",
                "Chen",
                "DOC123456",
                20,
                "Cardiac Surgeon",
                "doctor@mail.com"
        );
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() {
        return (firstName + " " + lastName).trim();
    }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
