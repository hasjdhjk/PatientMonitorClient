package Models;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class Patient {

    private int id;
    private String givenName;
    private String familyName;
    private int heartRate;
    private double temperature;
    private String bloodPressure;
    private boolean sticky = false; // sticky on top
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public Patient() {}  // GSON requires a no-arg constructor

    public Patient(int id, String givenName, String familyName,
                   int heartRate, double temperature,
                   String bloodPressure) {

        this.id = id;
        this.givenName = givenName;
        this.familyName = familyName;
        this.heartRate = heartRate;
        this.temperature = temperature;
        this.bloodPressure = bloodPressure;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return givenName + " " + familyName; }
    public String getGivenName() { return givenName; }
    public String getFamilyName() { return familyName; }
    public int getHeartRate() { return heartRate; }
    public double getTemperature() { return temperature; }
    public String getBloodPressure() { return bloodPressure; }
    public boolean isSticky() { return sticky; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setGivenName(String givenName) { this.givenName = givenName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }
    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
        pcs.firePropertyChange("vitals", null, null);
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        pcs.firePropertyChange("vitals", null, null);
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
        pcs.firePropertyChange("vitals", null, null);
    }
    public void setSticky(boolean sticky) { this.sticky = sticky; }

    @Override
    public String toString() {
        return givenName + " " + familyName +
                " (HR: " + heartRate +
                ", Temp: " + temperature +
                ", BP: " + bloodPressure + ")";
    }

    public boolean isHeartRateAbnormal() {
        return heartRate < 60 || heartRate > 100;
    }

    public boolean isTemperatureAbnormal() {
        return temperature < 36.1 || temperature > 37.2;
    }

    public boolean isBloodPressureAbnormal() {
        if (bloodPressure == null || bloodPressure.isEmpty()) return false;
        try {
            String[] parts = bloodPressure.split("/");
            int systolic = Integer.parseInt(parts[0].trim());
            int diastolic = Integer.parseInt(parts[1].trim());
            return systolic < 90 || systolic > 140 || diastolic < 60 || diastolic > 90;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasAbnormalVitals() {
        return isHeartRateAbnormal() || isTemperatureAbnormal() || isBloodPressureAbnormal();
    }
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    public enum VitalsSeverity {
        NORMAL, WARNING, DANGER
    }

    public VitalsSeverity getVitalsSeverity() {
        // DANGER thresholds (more extreme)
        boolean dangerHR = heartRate < 40 || heartRate > 130;
        boolean dangerTemp = temperature < 35.0 || temperature > 39.0;
        boolean dangerBP = false;

        if (bloodPressure != null && !bloodPressure.isEmpty()) {
            try {
                String[] parts = bloodPressure.split("/");
                int sys = Integer.parseInt(parts[0].trim());
                int dia = Integer.parseInt(parts[1].trim());
                dangerBP = (sys < 80 || sys > 180 || dia < 50 || dia > 120);
            } catch (Exception ignored) {}
        }

        if (dangerHR || dangerTemp || dangerBP) return VitalsSeverity.DANGER;

        // WARNING thresholds (your current “abnormal” range)
        boolean warning = isHeartRateAbnormal() || isTemperatureAbnormal() || isBloodPressureAbnormal();
        return warning ? VitalsSeverity.WARNING : VitalsSeverity.NORMAL;
    }
    public List<String> getWarningCauses() {
        List<String> causes = new ArrayList<>();

        if (isHeartRateAbnormal()) {
            causes.add("Heart rate abnormal (HR=" + heartRate + ", normal 60–100)");
        }
        if (isTemperatureAbnormal()) {
            causes.add("Temperature abnormal (Temp=" + temperature + ", normal 36.1–37.2)");
        }
        if (isBloodPressureAbnormal()) {
            causes.add("Blood pressure abnormal (BP=" + bloodPressure + ", normal 90–140 / 60–90)");
        }

        return causes;
    }

    public List<String> getDangerCauses() {
        List<String> causes = new ArrayList<>();

        if (heartRate < 40 || heartRate > 130) {
            causes.add("Heart rate DANGER (HR=" + heartRate + ", danger <40 or >130)");
        }
        if (temperature < 35.0 || temperature > 39.0) {
            causes.add("Temperature DANGER (Temp=" + temperature + ", danger <35.0 or >39.0)");
        }

        if (bloodPressure != null && !bloodPressure.isEmpty()) {
            try {
                String[] parts = bloodPressure.split("/");
                int sys = Integer.parseInt(parts[0].trim());
                int dia = Integer.parseInt(parts[1].trim());
                if (sys < 80 || sys > 180 || dia < 50 || dia > 120) {
                    causes.add("Blood pressure DANGER (BP=" + bloodPressure + ", danger sys <80/>180 or dia <50/>120)");
                }
            } catch (Exception ignored) {}
        }

        return causes;
    }

    public List<String> getAlertCauses(VitalsSeverity sev) {
        if (sev == VitalsSeverity.DANGER) return getDangerCauses();
        if (sev == VitalsSeverity.WARNING) return getWarningCauses();
        return new ArrayList<>();
    }
}
