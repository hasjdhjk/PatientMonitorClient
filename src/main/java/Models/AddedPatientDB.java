package Models;

import java.util.ArrayList;
import java.util.List;

public class AddedPatientDB {
    private static final List<Patient> patients = new ArrayList<>();

    public static void addPatient(Patient p) {
        patients.add(p);
    }

    public static void removePatient(Patient p) {
        patients.remove(p);
    }

    public static List<Patient> getAll() {
        return new ArrayList<>(patients);
    }

    public static void replaceAll(List<Patient> newList) {
        patients.clear();
        if (newList != null) patients.addAll(newList);
    }

    public static List<Patient> search(String query) {
        if (query == null || query.isEmpty()) return getAll();
        String q = query.toLowerCase();

        List<Patient> result = new ArrayList<>();
        for (Patient p : patients) {
            if (p.getName().toLowerCase().contains(q)) result.add(p);
        }
        return result;
    }

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