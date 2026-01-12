package Services;

import Models.LiveVitals;
import Models.VitalRecord;
import Models.VitalRecordIO;

import java.util.ArrayList;
import java.util.List;

public class MinuteAveragingService {

    private final LiveVitals vitals;
    private final List<Double> hr = new ArrayList<>();
    private final List<Double> rr = new ArrayList<>();
    private final List<Double> temp = new ArrayList<>();
    private final List<Double> spo2 = new ArrayList<>();

    public MinuteAveragingService(LiveVitals vitals) {
        this.vitals = vitals;
    }

    public void sample() {

        hr.add(vitals.getHeartRate());
        rr.add(vitals.getRespRate());
        temp.add(vitals.getTemperature());
        spo2.add(vitals.getSpO2());

        if (hr.size() >= 60) {

            VitalRecord record = new VitalRecord(
                    vitals.getPatientId(),
                    hr.stream().mapToDouble(d -> d).average().orElse(0),
                    rr.stream().mapToDouble(d -> d).average().orElse(0),
                    temp.stream().mapToDouble(d -> d).average().orElse(0),
                    spo2.stream().mapToDouble(d -> d).average().orElse(0)
            );

            VitalRecordIO.append(record);

            hr.clear();
            rr.clear();
            temp.clear();
            spo2.clear();
        }
    }
}
