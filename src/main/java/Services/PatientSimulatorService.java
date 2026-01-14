package Services;

import Models.Vitals.LiveVitals;
import java.util.Random;

public class PatientSimulatorService {

    private final LiveVitals vitals;
    private final Random rand = new Random();

    // Episode state
    private int dangerTicksRemaining = 0;
    private int episodeType = 0; // 0=HR, 1=Temp, 2=BP

    public PatientSimulatorService(LiveVitals vitals) {
        this.vitals = vitals;
    }

    public void update(double dt) {

        // Baselines (centered on your "normal" mean values)
        // Noise is tight so most patients remain NORMAL most of the time.
        double hrBase   = 76.60 + rand.nextGaussian() * 3.5;
        double tempBase = 36.91 + rand.nextGaussian() * 0.12;
        int sysBase     = (int) Math.round(123.83 + rand.nextGaussian() * 6.0);
        int diaBase     = (int) Math.round(76.39  + rand.nextGaussian() * 4.0);

        // SpO2 (%) and Respiratory rate (breaths/min) stable
        vitals.setSpO2(98 + rand.nextGaussian() * 0.6);
        vitals.setRespRate(16 + rand.nextGaussian() * 0.5);

        //danger episode
        // If no episode active, low chance to start one.
        if (dangerTicksRemaining <= 0) {
            // ~1% chance per second -> roughly one short red episode every ~100s per patient
            if (rand.nextDouble() < 0.005) {
                dangerTicksRemaining = 4 + rand.nextInt(7); // 4–10 seconds of DANGER
                episodeType = rand.nextInt(3);
            }
        } else {
            dangerTicksRemaining--;
        }

        //Apply either baseline or danger spike
        if (dangerTicksRemaining > 0) {
            if (episodeType == 0) {
                // HR danger spike
                vitals.setHeartRate(118 + rand.nextGaussian() * 4);
                vitals.setTemperature(tempBase);
            } else if (episodeType == 1) {
                // Temperature danger spike
                vitals.setHeartRate(90 + rand.nextGaussian() * 5);
                vitals.setTemperature(38.6 + rand.nextGaussian() * 0.15);
            } else {
                // BP danger spike
                vitals.setHeartRate(hrBase);
                vitals.setTemperature(tempBase);
                sysBase = (int) Math.round(165 + rand.nextGaussian() * 6);
                diaBase = (int) Math.round(105 + rand.nextGaussian() * 5);
            }
        } else {
            // Normal most of the time
            vitals.setHeartRate(hrBase);
            vitals.setTemperature(tempBase);
        }

        //Blood pressure set + clamp to physiological bounds
        sysBase = Math.max(80, Math.min(180, sysBase));
        diaBase = Math.max(50, Math.min(120, diaBase));
        vitals.setBloodPressure(sysBase + "/" + diaBase);
    }
}
