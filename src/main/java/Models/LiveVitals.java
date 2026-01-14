package Models;

import Services.PatientSimulatorService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class LiveVitals {

    // =====================
    // Global shared registry
    // =====================
    private static final ConcurrentHashMap<Integer, LiveVitals> SHARED = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, PatientSimulatorService> SIMS = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService EXEC = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "LiveVitals-SimLoop");
        t.setDaemon(true);
        return t;
    });

    private static volatile boolean loopStarted = false;

    /**
     * Get (or create) a globally shared LiveVitals instance for a patientId.
     * This also ensures there is exactly one simulator loop updating all shared vitals once per second.
     */
    public static LiveVitals getShared(int patientId, String baselineBloodPressure) {
        LiveVitals v = SHARED.computeIfAbsent(patientId, LiveVitals::new);

        // Align baseline BP if provided
        if (baselineBloodPressure != null && !baselineBloodPressure.isBlank()) {
            v.setBloodPressure(baselineBloodPressure);
        }

        // Ensure simulator exists for this patient
        SIMS.computeIfAbsent(patientId, id -> new PatientSimulatorService(v));

        // Start one global loop (once)
        if (!loopStarted) {
            synchronized (LiveVitals.class) {
                if (!loopStarted) {
                    loopStarted = true;
                    EXEC.scheduleAtFixedRate(() -> {
                        try {
                            for (PatientSimulatorService sim : SIMS.values()) {
                                sim.update(1);
                            }
                        } catch (Exception ignored) {
                        }
                    }, 0, 1, TimeUnit.SECONDS);
                }
            }
        }

        return v;
    }

    /**
     * Optional: remove a patient's shared vitals + simulator to avoid memory growth after discharge.
     * Safe to call anytime.
     */
    public static void removeShared(int patientId) {
        SHARED.remove(patientId);
        SIMS.remove(patientId);
    }

    /* =====================
       Instance fields
       ===================== */

    private final int patientId;

    private double heartRate = 75;
    private double respRate = 16;
    private double temperature = 36.9;
    private double spo2 = 98;
    private String bloodPressure = "124/76";

    public LiveVitals(int patientId) {
        this.patientId = patientId;
    }

    public int getPatientId() { return patientId; }

    public double getHeartRate() { return heartRate; }
    public void setHeartRate(double heartRate) { this.heartRate = heartRate; }

    public double getRespRate() { return respRate; }
    public void setRespRate(double respRate) { this.respRate = respRate; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getSpO2() { return spo2; }
    public void setSpO2(double spo2) { this.spo2 = spo2; }

    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    /** Convenience: any vital abnormal (warning-level thresholds). */
    public boolean hasAbnormalVitals() {
        return isHeartRateAbnormal() || isTemperatureAbnormal() || isBloodPressureAbnormal();
    }

    /** WARNING-level causes (your current “abnormal” ranges). */
    public List<String> getWarningCauses() {
        List<String> causes = new ArrayList<>();

        if (isHeartRateAbnormal()) {
            causes.add("Heart rate abnormal (HR=" + String.format("%.0f", heartRate) + ", normal 66–88)");
        }
        if (isTemperatureAbnormal()) {
            causes.add("Temperature abnormal (Temp=" + String.format("%.1f", temperature) + ", normal 36.5–37.4)");
        }
        if (isBloodPressureAbnormal()) {
            causes.add("Blood pressure abnormal (BP=" + bloodPressure + ", normal 109–139 / 66–86)");
        }

        return causes;
    }

    /** DANGER-level causes (more extreme thresholds). */
    public List<String> getDangerCauses() {
        List<String> causes = new ArrayList<>();

        if (heartRate < 50 || heartRate > 110) {
            causes.add("Heart rate DANGER (HR=" + String.format("%.0f", heartRate) + ", danger <50 or >110)");
        }
        if (temperature < 35.5 || temperature > 38.5) {
            causes.add("Temperature DANGER (Temp=" + String.format("%.1f", temperature) + ", danger <35.5 or >38.5)");
        }

        if (bloodPressure != null && !bloodPressure.isEmpty()) {
            try {
                String[] parts = bloodPressure.split("/");
                int sys = Integer.parseInt(parts[0].trim());
                int dia = Integer.parseInt(parts[1].trim());
                if (sys < 90 || sys > 160 || dia < 55 || dia > 110) {
                    causes.add("Blood pressure DANGER (BP=" + bloodPressure + ", danger sys <90/>160 or dia <55/>110)");
                }
            } catch (Exception ignored) {
            }
        }

        return causes;
    }

    /** Unified helper for UI: return causes according to severity. */
    public List<String> getAlertCauses(VitalsSeverity sev) {
        if (sev == VitalsSeverity.DANGER) return getDangerCauses();
        if (sev == VitalsSeverity.WARNING) return getWarningCauses();
        return new ArrayList<>();
    }

    public enum VitalsSeverity { NORMAL, WARNING, DANGER }

    public boolean isHeartRateAbnormal() {
        return heartRate < 65.56 || heartRate > 87.64;
    }

    public boolean isTemperatureAbnormal() {
        return temperature < 36.46 || temperature > 37.36;
    }

    public boolean isBloodPressureAbnormal() {
        if (bloodPressure == null || bloodPressure.isEmpty()) return false;
        try {
            String[] parts = bloodPressure.split("/");
            int sys = Integer.parseInt(parts[0].trim());
            int dia = Integer.parseInt(parts[1].trim());
            return sys < 108 || sys > 139 || dia < 66 || dia > 86;
        } catch (Exception e) {
            return false;
        }
    }

    public VitalsSeverity getVitalsSeverity() {
        boolean dangerHR = heartRate < 50 || heartRate > 110;
        boolean dangerTemp = temperature < 35.5 || temperature > 38.5;

        boolean dangerBP = false;
        if (bloodPressure != null && !bloodPressure.isEmpty()) {
            try {
                String[] parts = bloodPressure.split("/");
                int sys = Integer.parseInt(parts[0].trim());
                int dia = Integer.parseInt(parts[1].trim());
                dangerBP = (sys < 90 || sys > 160 || dia < 55 || dia > 110);
            } catch (Exception ignored) {}
        }

        if (dangerHR || dangerTemp || dangerBP) return VitalsSeverity.DANGER;

        return hasAbnormalVitals() ? VitalsSeverity.WARNING : VitalsSeverity.NORMAL;
    }
    public static void clearAllShared() {
        SHARED.clear();
        SIMS.clear();
    }

}
