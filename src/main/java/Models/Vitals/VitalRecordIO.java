package Models.Vitals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VitalRecordIO {

    private static final List<VitalRecord> records = new ArrayList<>();

    // Append ONE minute-averaged record
    public static void append(VitalRecord r) {
        records.add(r);
    }

    // Load all records (used by table)
    public static List<VitalRecord> loadAll() {
        return new ArrayList<>(records);
    }

    // Load records for a patient within the last N hours (used by reports)
    public static List<VitalRecord> loadLastHours(int patientId, int hours) {

        LocalDateTime cutoff = LocalDateTime.now().minusHours(hours);

        return records.stream()
                .filter(r -> r.getPatientId() == patientId)
                .filter(r -> r.getTimestamp().isAfter(cutoff))
                .toList();
    }

    // Clear records (optional: call on patient discharge)
    public static void clearForPatient(int patientId) {
        records.removeIf(r -> r.getPatientId() == patientId);
    }
}
