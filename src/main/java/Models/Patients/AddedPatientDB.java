package Models.Patients;

import java.util.ArrayList;
import java.util.List;

 // This class acts as a shared registry used by the UI and services
 // to access the list of patients currently loaded into the system.

public class AddedPatientDB {
    private static final List<Patient> patients = new ArrayList<>();

    // Adds a patient to the shared in-memory patient list.
    public static void addPatient(Patient p) {
        patients.add(p);
    }

    // Removes a patient from the shared patient list.
    public static void removePatient(Patient p) {
        patients.remove(p);
    }

    // Returns a copy of all patients currently stored in the database.
    public static List<Patient> getAll() {
        return new ArrayList<>(patients);
    }

    // Replaces the current patient list with a new list.
    public static void replaceAll(List<Patient> newList) {
        patients.clear();
        if (newList != null) patients.addAll(newList);
    }

    // Searches for patients whose names contain the given query string.
    public static List<Patient> search(String query) {
        if (query == null || query.isEmpty()) return getAll();
        String q = query.toLowerCase();

        List<Patient> result = new ArrayList<>();
        for (Patient p : patients) {
            if (p.getName().toLowerCase().contains(q)) result.add(p);
        }
        return result;
    }

    // Returns a sorted copy of the given patient list, prioritising sticky patients.
    public static List<Patient> getSorted(List<Patient> list) {
        List<Patient> sorted = new ArrayList<>(list);
        sorted.sort((a, b) -> {
            if (a.isSticky() && !b.isSticky()) return -1;
            if (!a.isSticky() && b.isSticky()) return 1;
            return a.getName().compareToIgnoreCase(b.getName());
        });
        return sorted;
    }
}