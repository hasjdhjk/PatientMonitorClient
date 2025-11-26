package Models;

import java.util.ArrayList;
import java.util.List;

// this records the added patients, for fast search and list operations
// it is different from the PatientServer database, this does not connect to server directly
public class AddedPatientDB {
    private static final List<Patient> patients = new ArrayList<>();

    public static void addPatient(Patient p) {
        patients.add(p);
    }

    public static void removePatient(Patient p) {
        patients.remove(p);
    }

    public static List<Patient> getAll() {
        return new ArrayList<>(patients); // return copy
    }

    public static List<Patient> search(String query) {
        if (query == null || query.isEmpty()) return getAll();

        String q = query.toLowerCase();

        List<Patient> result = new ArrayList<>();
        for (Patient p : patients) {
            if (p.getName().toLowerCase().contains(q)) {
                result.add(p);
            }
        }
        return result;
    }

    // later sync with backend servlet
}
