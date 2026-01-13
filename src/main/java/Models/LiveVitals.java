package Models;

import Services.PatientSimulatorService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private double temperature = 36.8;
    private double spo2 = 98;
    private String bloodPressure = "120/80";

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
}