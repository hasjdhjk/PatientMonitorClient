package Models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable alert history record.
 * Stores the patient id/name, the severity (from LiveVitals), the human-readable causes, and a timestamp.
 */
public class AlertRecord {

    private final int patientId;
    private final String patientName;
    private final LiveVitals.VitalsSeverity severity;
    private final List<String> causes;
    private final Instant timestamp;

    public AlertRecord(int patientId,
                       String patientName,
                       LiveVitals.VitalsSeverity severity,
                       List<String> causes,
                       Instant timestamp) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.severity = (severity == null) ? LiveVitals.VitalsSeverity.NORMAL : severity;
        this.causes = (causes == null) ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(causes));
        this.timestamp = (timestamp == null) ? Instant.now() : timestamp;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public LiveVitals.VitalsSeverity getSeverity() {
        return severity;
    }

    public List<String> getCauses() {
        return causes;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "AlertRecord{" +
                "patientId=" + patientId +
                ", patientName='" + patientName + '\'' +
                ", severity=" + severity +
                ", causes=" + causes +
                ", timestamp=" + timestamp +
                '}';
    }
}
