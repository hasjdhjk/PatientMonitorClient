package Services;

import Models.LiveVitals;
import java.util.Random;

public class PatientSimulatorService {

    private final LiveVitals vitals;
    private final Random rand = new Random();

    public PatientSimulatorService(LiveVitals vitals) {
        this.vitals = vitals;
    }

    public void update(double dt) {

        // Heart rate (bpm)
        vitals.setHeartRate(130 + rand.nextGaussian() * 3);

        // SpO2 (%)
        vitals.setSpO2(98 + rand.nextGaussian());

        // Respiratory rate (breaths/min)
        vitals.setRespRate(16 + rand.nextGaussian() * 0.5);

        // Temperature (°C)
        vitals.setTemperature(36.8 + rand.nextGaussian() * 0.1);

        // Blood pressure (mmHg) — simulate systolic/diastolic separately
        int systolic = (int) Math.round(120 + rand.nextGaussian() * 8);
        int diastolic = (int) Math.round(80 + rand.nextGaussian() * 5);

        // Clamp to physiological ranges to avoid nonsense values
        systolic = Math.max(80, Math.min(180, systolic));
        diastolic = Math.max(50, Math.min(120, diastolic));

        vitals.setBloodPressure(systolic + "/" + diastolic);
    }
}
