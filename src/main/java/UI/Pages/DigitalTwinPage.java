package UI.Pages;

import Models.AddedPatientDB;
import Models.LiveVitals;
import Models.Patient;
import UI.Components.DigitalTwinPanel;
import UI.Components.Tiles.BaseTile;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DigitalTwinPage extends JPanel {

    private final MainWindow window;
    private final List<Patient> allPatients;

    private int currentIndex = 0;
    private Patient currentPatient;

    // Shared globally
    private LiveVitals liveVitals;

    // Only used to refresh UI (simulation runs globally)
    private Timer liveTimer;

    private final DigitalTwinPanel digitalTwinPanel;
    private final BaseTile twinTile;

    public DigitalTwinPage(MainWindow window) {
        this.window = window;
        this.allPatients = AddedPatientDB.getAll();

        if (!allPatients.isEmpty()) {
            currentPatient = allPatients.get(0);
        }

        if (currentPatient != null) {
            initLiveVitalsForCurrentPatient();
        }

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        digitalTwinPanel = new DigitalTwinPanel();

        twinTile = new BaseTile(1200, 750, 30, true);
        twinTile.setLayout(new BorderLayout());
        twinTile.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        twinTile.add(digitalTwinPanel, BorderLayout.CENTER);

        add(twinTile, BorderLayout.CENTER);

        if (currentPatient != null) {
            pushPatientToTwin();
            startLiveLoop();
        }
    }

    private void pushPatientToTwin() {
        if (currentPatient == null) return;

        digitalTwinPanel.setSelectedPatientId(currentPatient.getId());

        if (liveVitals == null) {
            initLiveVitalsForCurrentPatient();
        }

        String bp = liveVitals.getBloodPressure();
        int sys = 120;
        int dia = 80;
        try {
            String[] parts = bp.split("/");
            sys = Integer.parseInt(parts[0].trim());
            dia = Integer.parseInt(parts[1].trim());
        } catch (Exception ignored) {}

        int hr = (int) Math.round(liveVitals.getHeartRate());
        int rr = (int) Math.round(liveVitals.getRespRate());
        int sp = (int) Math.round(liveVitals.getSpO2());
        double temp = liveVitals.getTemperature();

        digitalTwinPanel.setVitals(hr, rr, sp, sys, dia, temp);
    }

    private void initLiveVitalsForCurrentPatient() {
        String baselineBp = currentPatient.getBloodPressure();
        liveVitals = LiveVitals.getShared(currentPatient.getId(), baselineBp);
    }

    private void startLiveLoop() {
        if (liveTimer != null) liveTimer.stop();

        liveTimer = new Timer(1000, e -> pushPatientToTwin());
        liveTimer.start();
    }

    public void setPatient(Patient patient) {
        this.currentPatient = patient;
        this.currentIndex = allPatients.indexOf(patient);
        initLiveVitalsForCurrentPatient();
        pushPatientToTwin();
        startLiveLoop();
    }

    public void nextPatient() {
        if (allPatients.isEmpty()) return;
        currentIndex = (currentIndex + 1) % allPatients.size();
        currentPatient = allPatients.get(currentIndex);
        initLiveVitalsForCurrentPatient();
        pushPatientToTwin();
        startLiveLoop();
    }

    public void previousPatient() {
        if (allPatients.isEmpty()) return;
        currentIndex = (currentIndex - 1 + allPatients.size()) % allPatients.size();
        currentPatient = allPatients.get(currentIndex);
        initLiveVitalsForCurrentPatient();
        pushPatientToTwin();
        startLiveLoop();
    }
}