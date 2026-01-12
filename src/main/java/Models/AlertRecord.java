package Models;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AlertRecord {
    private final int patientId;
    private final String patientName;
    private final Patient.VitalsSeverity severity;
    private final List<String> causes;
    private final Instant time;

    public AlertRecord(int patientId, String patientName, Patient.VitalsSeverity severity,
                       List<String> causes, Instant time) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.severity = severity;
        this.causes = causes;
        this.time = time;
    }

    public int getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public Patient.VitalsSeverity getSeverity() { return severity; }
    public List<String> getCauses() { return causes; }
    public Instant getTime() { return time; }

    public String getFormattedTime() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(time);
    }

    @Override
    public String toString() {
        return getFormattedTime() + " | " + severity + " | " + patientName + " (" + patientId + ") | " + String.join("; ", causes);
    }
}
