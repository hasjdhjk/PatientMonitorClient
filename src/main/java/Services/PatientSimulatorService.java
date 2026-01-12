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

        vitals.setHeartRate(70 + rand.nextGaussian() * 3);
        vitals.setSpO2(98 + rand.nextGaussian());
        vitals.setRespRate(16 + rand.nextGaussian() * 0.5);
        vitals.setTemperature(36.8 + rand.nextGaussian() * 0.1);
        vitals.setBloodPressure("120/80");
    }
}
