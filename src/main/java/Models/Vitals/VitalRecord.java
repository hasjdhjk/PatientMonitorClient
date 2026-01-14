package Models.Vitals;

import java.time.LocalDateTime;

public class VitalRecord {

    private final int patientId;
    private final LocalDateTime timestamp;

    private final double avgHeartRate;
    private final double avgRespRate;
    private final double avgTemperature;
    private final double avgSpO2;

    public VitalRecord(int patientId,
                       double hr,
                       double rr,
                       double temp,
                       double spo2) {
        this.patientId = patientId;
        this.avgHeartRate = hr;
        this.avgRespRate = rr;
        this.avgTemperature = temp;
        this.avgSpO2 = spo2;
        this.timestamp = LocalDateTime.now();
    }

    public int getPatientId() { return patientId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getAvgHeartRate() { return avgHeartRate; }
    public double getAvgRespRate() { return avgRespRate; }
    public double getAvgTemperature() { return avgTemperature; }
    public double getAvgSpO2() { return avgSpO2; }
}
