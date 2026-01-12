package Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ECGSimulatorService {

    private double t = 0;
    private final Random r = new Random();

    public List<Double> nextSamples(int n, double hr) {

        List<Double> data = new ArrayList<>();
        double beatPeriod = 60.0 / hr;

        for (int i = 0; i < n; i++) {
            t += 0.01;

            double phase = (t % beatPeriod) / beatPeriod;

            double ecg =
                    Math.exp(-Math.pow((phase - 0.1) * 40, 2)) * 1.5 + // QRS
                            Math.exp(-Math.pow((phase - 0.25) * 20, 2)) * 0.3 + // T
                            Math.sin(2 * Math.PI * t * 0.33) * 0.05 +          // baseline
                            r.nextGaussian() * 0.02;                          // noise

            data.add(ecg);
        }
        return data;
    }
}
